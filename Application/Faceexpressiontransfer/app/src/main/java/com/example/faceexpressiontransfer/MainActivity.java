package com.example.faceexpressiontransfer;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    ImageView imageView;
    Button camera;
    Button gallery;
    Button next;
    ImageView egg;
    String mCurrentPhotoPath;
    static int Image_Capture_Code = 1;
    static int Open_Gallery = 2;
    private Bitmap sendBitmap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.myimage);
        camera = (Button)findViewById(R.id.camera);
        gallery = (Button)findViewById(R.id.gallery);
        next = (Button)findViewById(R.id.next);
        egg = (ImageView)findViewById(R.id.yonsei);
        camera.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = null;
                try{
                    file = File.createTempFile("temp", ".jpg",
                            getExternalFilesDir(Environment.DIRECTORY_PICTURES));
                    mCurrentPhotoPath = file.getAbsolutePath();
                } catch(IOException e){
                    e.printStackTrace();
                }
                Uri uri = FileProvider.getUriForFile(MainActivity.this, "com.example.faceexpressiontransfer.fileprovider", file);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, Image_Capture_Code);

            }
        });
        gallery.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent, Open_Gallery);
            }
        });

        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(sendBitmap == null){
                    Toast.makeText(getApplicationContext(), "Choose the photo", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent send = new Intent(MainActivity.this, ConnectionActivity.class);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    sendBitmap.compress(Bitmap.CompressFormat.JPEG, 40, stream);
                    byte[] byteArray = stream.toByteArray();
                    send.putExtra("image", byteArray);
                    startActivity(send);
                    finish();
                }
            }
        });

        egg.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), Game.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        if(requestCode == Image_Capture_Code){
            if(resultCode == RESULT_OK){
                File bitmapFile = new File(mCurrentPhotoPath);
                Bitmap bitmap = null;
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),
                            Uri.fromFile(bitmapFile));
                    if(bitmap != null) {
                        ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
                        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_UNDEFINED);
                        switch (orientation) {
                            case ExifInterface.ORIENTATION_ROTATE_90:
                                sendBitmap = rotateImage(bitmap, 90);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_180:
                                sendBitmap = rotateImage(bitmap, 180);
                                break;
                            case ExifInterface.ORIENTATION_ROTATE_270:
                                sendBitmap = rotateImage(bitmap, 270);
                                break;
                            case ExifInterface.ORIENTATION_NORMAL:
                            default:
                                sendBitmap = bitmap;
                        }
                    }
                    sendBitmap = Bitmap.createScaledBitmap(sendBitmap, 400, 500, true);
                    imageView.setImageBitmap(sendBitmap);

                } catch(IOException e){
                    e.printStackTrace();
                }
            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }

        }
        else if(requestCode == Open_Gallery){
            if(resultCode == RESULT_OK){
                Uri uri = intent.getData();
                Glide.with(getApplicationContext()).asBitmap().load(uri).into(new CustomTarget<Bitmap>(){
                    @Override
                    public void onResourceReady(Bitmap bitmap, Transition<? super Bitmap> transition){
                        sendBitmap = bitmap;
                        int width = sendBitmap.getWidth();
                        int height = sendBitmap.getHeight();
                        int dest_w = width/2;
                        int dest_h = height/2;
                        if(width > 400 || height > 500){
                            sendBitmap = Bitmap.createScaledBitmap(sendBitmap, dest_w, dest_h, true);
                        }

                        imageView.setImageBitmap(sendBitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
            }
            else if(resultCode == RESULT_CANCELED){
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public Bitmap rotateImage(Bitmap src, float degree){
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        return Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);
    }
}