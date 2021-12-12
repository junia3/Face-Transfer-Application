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

import org.w3c.dom.Text;

public class GameUnity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_unity_scene);
        ImageView face = (ImageView)findViewById(R.id.capture);
        Button button = (Button)findViewById(R.id.return2main);
        TextView result = (TextView)findViewById(R.id.result);
        TextView result2 = (TextView)findViewById(R.id.result2);
        Intent intent = getIntent();
        byte[] data = intent.getByteArrayExtra("data");
        byte[] emotion = intent.getByteArrayExtra("emotion");
        String emotion_game = intent.getStringExtra("emotion_game");

        if(emotion != null) {
            String emotion_text = new String(emotion);
            TextView emo_txt = (TextView) findViewById(R.id.emotion_result);
            if (emotion_text != null) emo_txt.setText(emotion_text);
            if(emotion_text.equals(emotion_game)){
                result.setText("You win!!");
                result2.setText("You successfully make "+emotion_game+" face!!");
            }
            else{
                result.setText("You lose..");
                result2.setText("You should make "+emotion_game+" face..");
            }
        }

        Bitmap image = BitmapFactory.decodeByteArray(data,0,data.length);
        face.setImageBitmap(image);

        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast.makeText(getApplicationContext(), "Go to the main activity", Toast.LENGTH_SHORT).show();
                Intent main = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(main);
                finish();
            }
        });
    }
}
