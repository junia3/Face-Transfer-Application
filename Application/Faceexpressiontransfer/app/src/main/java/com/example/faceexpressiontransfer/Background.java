package com.example.faceexpressiontransfer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Background extends AppCompatActivity {
    byte[] backs = new byte[6];
    private byte[] emotion;
    private String ip_server;
    private int port_server;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background);

        Intent sending = getIntent();
        byte[] data = sending.getByteArrayExtra("data");
        ip_server = sending.getStringExtra("ip");
        port_server = sending.getIntExtra("port", 0);
        emotion = sending.getByteArrayExtra("emotion");

        ImageView face = (ImageView) findViewById(R.id.scene_main);
        Bitmap image = BitmapFactory.decodeByteArray(data,0,data.length);
        face.setImageBitmap(image);

        backs[0] = (byte)0;
        backs[1] = (byte)0;
        backs[2] = (byte)0;
        backs[3] = (byte)0;
        backs[4] = (byte)0;
        backs[5] = (byte)0;
        ImageView bg1 = (ImageView) findViewById(R.id.bg1);
        ImageView bg2 = (ImageView) findViewById(R.id.bg2);
        ImageView bg3 = (ImageView) findViewById(R.id.bg3);
        ImageView bg4 = (ImageView) findViewById(R.id.bg4);
        ImageView bg5 = (ImageView) findViewById(R.id.bg5);
        ImageView bg6 = (ImageView) findViewById(R.id.bg6);

        bg1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backs[0] = (byte)1;
                Intent updating = new Intent(getApplicationContext(), Update.class);
                updating.putExtra("ip", ip_server);
                updating.putExtra("port", port_server);
                updating.putExtra("data", backs);
                updating.putExtra("emotion", emotion);
                startActivity(updating);
                finish();
            }
        });
        bg2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backs[1] = (byte)1;
                Intent updating = new Intent(getApplicationContext(), Update.class);
                updating.putExtra("ip", ip_server);
                updating.putExtra("port", port_server);
                updating.putExtra("data", backs);
                updating.putExtra("emotion", emotion);
                startActivity(updating);
                finish();
            }
        });
        bg3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backs[2] = (byte)1;
                Intent updating = new Intent(getApplicationContext(), Update.class);
                updating.putExtra("ip", ip_server);
                updating.putExtra("port", port_server);
                updating.putExtra("data", backs);
                updating.putExtra("emotion", emotion);
                startActivity(updating);
                finish();
            }
        });
        bg4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backs[3] = (byte)1;
                Intent updating = new Intent(getApplicationContext(), Update.class);
                updating.putExtra("ip", ip_server);
                updating.putExtra("port", port_server);
                updating.putExtra("data", backs);
                updating.putExtra("emotion", emotion);
                startActivity(updating);
                finish();
            }
        });
        bg5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backs[4] = (byte)1;
                Intent updating = new Intent(getApplicationContext(), Update.class);
                updating.putExtra("ip", ip_server);
                updating.putExtra("port", port_server);
                updating.putExtra("data", backs);
                updating.putExtra("emotion", emotion);
                startActivity(updating);
                finish();
            }
        });
        bg6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backs[5] = (byte)1;
                Intent updating = new Intent(getApplicationContext(), Update.class);
                updating.putExtra("ip", ip_server);
                updating.putExtra("port", port_server);
                updating.putExtra("data", backs);
                updating.putExtra("emotion", emotion);
                startActivity(updating);
                finish();
            }
        });
    }
}
