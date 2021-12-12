package com.example.faceexpressiontransfer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;

public class SendImage extends AppCompatActivity {
    private Bitmap bitmap;
    final String TAG = getClass().getSimpleName();
    Button send_btn;
    Button cancel_btn;
    ImageView send_img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendimage);

        send_img = (ImageView)findViewById(R.id.send_image);
        send_btn = (Button)findViewById(R.id.Sendbtn);
        cancel_btn = (Button)findViewById(R.id.Cancel);

        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inSampleSize = 4;
        Intent intent = getIntent();
        byte[] array = intent.getByteArrayExtra("image");
        Bitmap image = BitmapFactory.decodeByteArray(array,0,array.length);
        send_img.setImageBitmap(image);

        cancel_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SendImage.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        send_btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent sending = new Intent(SendImage.this, Sending.class);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                sending.putExtra("image", byteArray);
                startActivity(sending);
                finish();
            }
        });
    }
}