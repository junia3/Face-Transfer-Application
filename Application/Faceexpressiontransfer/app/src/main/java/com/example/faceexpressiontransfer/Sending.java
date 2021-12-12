package com.example.faceexpressiontransfer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.icu.util.Output;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Sending extends AppCompatActivity {
    private String ip = "192.168.0.4";
    private int port = 4321;
    private Handler mHandler;
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;
    public byte[] data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sending);

        Intent sending = getIntent();
        data = sending.getByteArrayExtra("image");
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
                    dos.writeUTF(Integer.toString(data.length));
                    dos.flush();

                    dos.write(data);
                    dos.flush();

                    int length = dis.readInt();
                    byte[] capture = new byte[length];
                    dis.readFully(capture, 0, length);
                    socket.close();

                    Intent unity = new Intent(getApplicationContext(), UnityScene.class);
                    unity.putExtra("data", capture);
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
