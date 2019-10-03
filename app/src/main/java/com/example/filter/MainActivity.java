package com.example.filter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    LinearLayout image, video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        image = findViewById(R.id.filter_image);
        video = findViewById(R.id.filter_video);

        image.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ImageActivity.class);
            startActivity(intent);
        });

        video.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
            startActivity(intent);
        });
    }
}
