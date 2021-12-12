package com.example.faceexpressiontransfer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UnityScene extends AppCompatActivity{
    byte[] items = new byte[3];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unity_scene);
        ImageView face = (ImageView)findViewById(R.id.face);
        Button button = (Button)findViewById(R.id.button);
        Button update = (Button)findViewById(R.id.update);
        Button change_back = (Button)findViewById(R.id.change_back);
        Intent intent = getIntent();
        byte[] data = intent.getByteArrayExtra("data");
        String ip_server = intent.getStringExtra("ip");
        int port_server = intent.getIntExtra("port", 0);
        byte[] emotion = intent.getByteArrayExtra("emotion");
        if(emotion != null) {
            String emotion_text = new String(emotion);
            TextView emo_txt = (TextView) findViewById(R.id.emotion_text);
            if (emotion_text != null) emo_txt.setText(emotion_text);
        }

        Bitmap image = BitmapFactory.decodeByteArray(data,0,data.length);
        face.setImageBitmap(image);

        items[0] = (byte)0;
        items[1] = (byte)0;
        items[2] = (byte)0;
        CheckBox hat = (CheckBox)findViewById(R.id.hat);
        CheckBox mask = (CheckBox)findViewById(R.id.mask);
        CheckBox sunglass = (CheckBox)findViewById(R.id.sunglass);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "Go to the main activity", Toast.LENGTH_SHORT).show();
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });

        update.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                items[0] = hat.isChecked() ? (byte)1 : (byte)0;
                items[1] = mask.isChecked() ? (byte)1 : (byte)0;
                items[2] = sunglass.isChecked() ? (byte)1 : (byte)0;
                Intent updating = new Intent(getApplicationContext(), Update.class);
                updating.putExtra("ip", ip_server);
                updating.putExtra("port", port_server);
                updating.putExtra("data", items);
                updating.putExtra("emotion", emotion);
                startActivity(updating);
                finish();
            }
        });

        change_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Change background image", Toast.LENGTH_SHORT).show();
                Intent change = new Intent(getApplicationContext(), Background.class);
                change.putExtra("ip", ip_server);
                change.putExtra("port", port_server);
                change.putExtra("data", data);
                change.putExtra("emotion", emotion);
                startActivity(change);
                finish();
            }
        });
    }
}
