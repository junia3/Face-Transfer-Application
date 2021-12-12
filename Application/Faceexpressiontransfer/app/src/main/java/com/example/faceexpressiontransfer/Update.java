package com.example.faceexpressiontransfer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.ByteBuffer;

import okio.Utf8;

public class Update extends AppCompatActivity {
    private String ip;
    private int port;
    private Handler mHandler;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    public byte[] data;
    private byte[] capture;
    private byte[] emotion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sending);

        Intent sending = getIntent();
        data = sending.getByteArrayExtra("data");
        ip = sending.getStringExtra("ip");
        port = sending.getIntExtra("port", 0);
        emotion = sending.getByteArrayExtra("emotion");
        connect();
    }
    void connect(){
        mHandler = new Handler();
        Thread checkupdate = new Thread(){
            public void run() {
                try {
                    socket = new Socket(ip, port);
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Server is not open");
                }

                try {
                    dos = new DataOutputStream(socket.getOutputStream());
                    dis = new DataInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.w("버퍼", "버퍼생성 잘못됨");
                }

                try{
                    byte[] d_length = ByteBuffer.allocate(4).putInt(data.length).array();
                    dos.write(d_length);
                    dos.flush();

                    dos.write(data);
                    dos.flush();

                    int length = dis.readInt();
                    capture = new byte[length];
                    dis.readFully(capture, 0, length);


                    Intent unity = new Intent(getApplicationContext(), UnityScene.class);
                    unity.putExtra("data", capture);
                    unity.putExtra("ip", ip);
                    unity.putExtra("port",port);
                    unity.putExtra("emotion", emotion);

                    startActivity(unity);
                    finish();
                }
                catch (Exception e){
                    Log.w("error", "error occur");
                }
            }
        };
        checkupdate.start();
        try{
            checkupdate.join();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}
