using System;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using UnityEngine;
using System.Collections;

public class item : MonoBehaviour
{
    static Socket listener;
    private CancellationTokenSource source;
    public ManualResetEvent allDone;
    public byte[] items;
    public byte[] byte_data;
    public static readonly int PORT = 55555;
    public static readonly int WAITTIME = 1;
    public GameObject hat;
    public GameObject mask;
    public GameObject sunglasses;

    private GameObject instance_hat;
    private GameObject instance_mask;
    private GameObject instance_sunglasses;

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

    item()
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
        items = new byte[3];
        items[0] = 0;
        items[1] = 0;
        items[2] = 0;
        await Task.Run(() => ListenEvents(source.Token));
    }

    // Update is called once per frame
    void Update()
    {
        if (!update)
        {
            if(items[0] == 1)
            {
                instance_hat = Instantiate(hat, transform.position, transform.rotation) as GameObject;
                instance_hat.transform.position = new Vector3(0f, 1f, 0f);
                instance_hat.transform.localScale = new Vector3(0.7f, 0.7f, 0.7f);
            }
            if(items[1] == 1)
            {
                instance_mask= Instantiate(mask, transform.position, transform.rotation) as GameObject;
                instance_mask.transform.position = new Vector3(0f, 0.3f, 0f);
                instance_mask.transform.localScale = new Vector3(6f, 5f, 5f);
            }
            if (items[2] == 1)
            {
                instance_sunglasses = Instantiate(sunglasses, transform.position, transform.rotation) as GameObject;
                instance_sunglasses.transform.position = new Vector3(0, 0.84f, 0.3f);
                instance_sunglasses.transform.localScale = new Vector3(30f, 30f, 30f);
            }
            StartCoroutine("Capture");
            update = true;
            Debug.Log("Image captured!");
            Destroy(instance_hat, 2f);
            Destroy(instance_mask, 2f);
            Destroy(instance_sunglasses, 2f);
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
        items[0] = state.buffer[0];
        items[1] = state.buffer[1];
        items[2] = state.buffer[2];
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
        public const int BufferSize = 3;
        public byte[] buffer = new byte[BufferSize];
    }
}