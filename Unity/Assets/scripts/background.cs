using System;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using UnityEngine;
using System.Collections;

public class background : MonoBehaviour
{
    static Socket listener;
    private CancellationTokenSource source;
    public ManualResetEvent allDone;
    public byte[] backs;
    public byte[] byte_data;
    public static readonly int PORT = 33333;
    public static readonly int WAITTIME = 1;
    private bool update = true;

    public Texture bg1, bg2, bg3, bg4, bg5, bg6;
    public MeshRenderer planeRenderer;

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

    background()
    {
        source = new CancellationTokenSource();
        allDone = new ManualResetEvent(false);
    }
    void Awake()
    {

    }
    // Start is called before the first frame update
    async void Start()
    {        
        backs = new byte[6];
        backs[0] = 0;
        backs[1] = 0;
        backs[2] = 0;
        backs[3] = 0;
        backs[4] = 0;
        backs[5] = 0;
        await Task.Run(() => ListenEvents(source.Token));
    }

    // Update is called once per frame
    void Update()
    {
        if (!update)
        {
            if(backs[0] == 1)
            {
                planeRenderer.material.SetTexture("_MainTex", bg1);
            }
            else if (backs[1] == 1)
            {
                planeRenderer.material.SetTexture("_MainTex", bg2);
            }
            else if(backs[2] == 1)
            {
                planeRenderer.material.SetTexture("_MainTex", bg3);
            }
            else if(backs[3] == 1)
            {
                planeRenderer.material.SetTexture("_MainTex", bg4);
            }
            else if(backs[4] == 1)
            {
                planeRenderer.material.SetTexture("_MainTex", bg5);
            }
            else if(backs[5] == 1)
            {
                planeRenderer.material.SetTexture("_MainTex", bg6);
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
        handler.BeginReceive(state.buffer, 0, StateObject.BufferSize, 0, new AsyncCallback(ReadCallback), state);
        backs[0] = state.buffer[0];
        backs[1] = state.buffer[1];
        backs[2] = state.buffer[2];
        backs[3] = state.buffer[3];
        backs[4] = state.buffer[4];
        backs[5] = state.buffer[5];
        update = false;
        handler.Close();
    }

    private void OnDestroy()
    {
        source.Cancel();
    }

    public class StateObject
    {
        public Socket workSocket = null;
        public const int BufferSize = 6;
        public byte[] buffer = new byte[BufferSize];
    }
}