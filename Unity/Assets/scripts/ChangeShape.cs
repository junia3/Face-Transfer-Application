using System;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using UnityEngine;
using System.Collections;

public class ChangeShape : MonoBehaviour
{
    static Socket listener;
    private CancellationTokenSource source;
    public ManualResetEvent allDone;
    public SkinnedMeshRenderer skinnedMeshRenderer;
    public int blendShapeCount;
    public Mesh skinnedMesh;
    public string[] shapes;
    public byte[] byte_data;
    public static readonly int PORT = 11111;
    public static readonly int WAITTIME = 1;
    private bool update = true;

    private String cappath = @"C:\Users\junia\Desktop\final_pj\Assets\capture.jpg";
    IEnumerator Capture()
    {
        yield return new WaitForEndOfFrame();
        Texture2D texture = new Texture2D(Screen.width, Screen.height, TextureFormat.RGB24, false);
        texture.ReadPixels(new Rect(0, 0, Screen.width, Screen.height), 0, 0, false);
        texture.Apply();

        byte_data = texture.EncodeToJPG();
        System.IO.File.WriteAllBytes(cappath, byte_data);
    }

    ChangeShape()
    {
        source = new CancellationTokenSource();
        allDone = new ManualResetEvent(false);
    }
    void Awake()
    {
        skinnedMeshRenderer = GetComponent<SkinnedMeshRenderer>();
        skinnedMesh = GetComponent<SkinnedMeshRenderer>().sharedMesh;
        blendShapeCount = skinnedMesh.blendShapeCount;

    }
    // Start is called before the first frame update
    async void Start()
    {
        await Task.Run(() => ListenEvents(source.Token));
    }

    // Update is called once per frame
    void Update()
    {
        if (shapes.Length != 0 && !update)
        {
            for (int i = 0; i < blendShapeCount; i++)
            {
                skinnedMeshRenderer.SetBlendShapeWeight(i, float.Parse(shapes[i]));
            }
            StartCoroutine("Capture");
            update = true;
            Debug.Log("Image captured!");
        }
    }

    private void ListenEvents(CancellationToken token)
    {


        IPHostEntry ipHostInfo = Dns.GetHostEntry(Dns.GetHostName());
        IPAddress ipAddress = ipHostInfo.AddressList.FirstOrDefault(ip => ip.AddressFamily == AddressFamily.InterNetwork);
        IPEndPoint localEndPoint = new IPEndPoint(ipAddress, PORT);


        listener = new Socket(ipAddress.AddressFamily, SocketType.Stream, ProtocolType.Tcp);


        try
        {
            listener.Bind(localEndPoint);
            listener.Listen(10);


            while (!token.IsCancellationRequested)
            {
                allDone.Reset();

                print("Waiting for a connection... host :" + ipAddress.MapToIPv4().ToString() + " port : " + PORT);
                listener.BeginAccept(new AsyncCallback(AcceptCallback), listener);
                while (!token.IsCancellationRequested)
                {
                    if (allDone.WaitOne(WAITTIME))
                    {
                        break;
                    }
                }

            }

        }
        catch (Exception e)
        {
            print(e.ToString());
        }
    }

    void AcceptCallback(IAsyncResult ar)
    {
        Socket listener = (Socket)ar.AsyncState;
        Socket handler = listener.EndAccept(ar);

        allDone.Set();

        StateObject state = new StateObject();
        state.workSocket = handler;
        handler.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0, new AsyncCallback(ReadCallback), state);
    }

    void ReadCallback(IAsyncResult ar)
    {
        StateObject state = (StateObject)ar.AsyncState;
        Socket handler = state.workSocket;
        int read = handler.EndReceive(ar);

        if (read > 0)
        {
            state.shapeCode.Append(Encoding.ASCII.GetString(state.buffer, 0, read));
            handler.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0, new AsyncCallback(ReadCallback), state);
        }
        else
        {
            if (state.shapeCode.Length > 1)
            {
                string content = state.shapeCode.ToString();
                print($"Read {content.Length} bytes from socket.\n Data : {content}");
                SetShape(content);
            }
            handler.Close();
        }
    }

    //Set color to the Material
    private void SetShape(string data)
    {
        
        shapes = data.Split(',');
        update = false;
    }

    private void OnDestroy()
    {
        source.Cancel();
    }

    public class StateObject
    {
        public Socket workSocket = null;
        public const int BufferSize = 1024;
        public byte[] buffer = new byte[BufferSize];
        public StringBuilder shapeCode = new StringBuilder();
    }
}