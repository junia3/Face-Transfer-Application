package com.example.faceexpressiontransfer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.example.faceexpressiontransfer.databinding.SplashLoadBinding;

public class Load_app extends AppCompatActivity {
    private SplashLoadBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SplashLoadBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            @Override
            public void run(){
                Intent intent = new Intent(Load_app.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1500);

    }
}
