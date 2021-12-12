package com.example.faceexpressiontransfer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ConnectionActivity extends AppCompatActivity {
    private EditText IP, PORT;
    private String ip_server=null;
    private int port_server=0;
    private Socket socket;
    private DataOutputStream writeSocket;
    private DataInputStream readSocket;
    private Handler mHandler = new Handler();
    public byte[] data;
    String emotion_game=null;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection);

        IP = (EditText) findViewById(R.id.ip);
        IP.setText(wifiIPAddress().substring(0, 10));
        PORT = (EditText) findViewById(R.id.port);
        PORT.setText("4321");

        ImageView imageView = (ImageView) findViewById(R.id.face_image);
        Button send_btn = (Button) findViewById(R.id.click);

        Intent intent = getIntent();
        data = intent.getByteArrayExtra("image");
        System.out.println(data.length);
        emotion_game = intent.getStringExtra("emotion_game");
        Bitmap image = BitmapFactory.decodeByteArray(data,0, data.length);
        imageView.setImageBitmap(image);

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Connect()).start();
            }
        });

    }

    class Connect extends Thread{
        public void run(){
            Log.d("Connect", "Run Connect");
            try{
                ip_server = IP.getText().toString();
                port_server = Integer.parseInt(PORT.getText().toString());
            } catch(Exception e){
                final String recvInput = "정확히 입력하세요";
                mHandler.post(new Runnable(){
                    @Override
                    public void run(){
                        setToast(recvInput);
                    }
                });
            }

            try{
                socket = new Socket(ip_server, port_server);
                writeSocket = new DataOutputStream(socket.getOutputStream());
                readSocket = new DataInputStream(socket.getInputStream());
                mHandler.post(new Runnable(){
                   @Override
                   public void run(){
                       setToast("연결에 성공하셨습니다.");
                   }
                });
            } catch(Exception e) {
                final String recvInput = "연결에 실패하였습니다.";
                Log.d("Connect", e.getMessage());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        setToast(recvInput);
                    }
                });
            }
            try{
                byte[] size = getByte(data.length);
                writeSocket.write(size);
                writeSocket.flush();

                writeSocket.write(data);
                writeSocket.flush();

                int length = readSocket.readInt();
                byte[] emotion = new byte[length];
                readSocket.readFully(emotion, 0, length);

                String emotion_text = new String(emotion);
                if(emotion_text.equals("No face detected")){
                    Intent main = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(main);
                    finish();
                    mHandler.post(new Runnable(){
                        @Override
                        public void run(){
                            setToast("얼굴이 인식되지 않았습니다.");
                        }
                    });
                }
                else {
                    length = readSocket.readInt();
                    byte[] capture = new byte[length];
                    readSocket.readFully(capture, 0, length);
                    socket.close();


                    if (emotion_game == null) {
                        Intent unity = new Intent(getApplicationContext(), UnityScene.class);
                        unity.putExtra("ip", ip_server);
                        unity.putExtra("port", port_server);
                        unity.putExtra("data", capture);
                        unity.putExtra("emotion", emotion);
                        startActivity(unity);
                        finish();

                    } else {
                        Intent gameUnity = new Intent(getApplicationContext(), GameUnity.class);
                        gameUnity.putExtra("data", capture);
                        gameUnity.putExtra("emotion", emotion);
                        gameUnity.putExtra("emotion_game", emotion_game);
                        startActivity(gameUnity);
                        finish();
                    }
                }
            }
            catch (Exception e){
                Log.w("error", "error occur");
            }

        }
    }

    void setToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
    private byte[] getByte(int num) {
        byte[] buf = new byte[4];
        buf[0] = (byte)( (num >>> 24) & 0xFF );
        buf[1] = (byte)( (num >>> 16) & 0xFF );
        buf[2] = (byte)( (num >>>  8) & 0xFF );
        buf[3] = (byte)( (num >>>  0) & 0xFF );

        return buf;
    }
    public String wifiIPAddress(){
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

        if(ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)){
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try{
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex){
            Log.e("WIFIIP", "Unable to get host address.");
            ipAddressString = null;
        }

        return ipAddressString;
    }
}
