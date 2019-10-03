package com.example.filter.Image;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.filter.R;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.IOException;

public class FullImageActivity extends AppCompatActivity {

    PhotoView photoView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_image);

        photoView = findViewById(R.id.full_image);

        String path = getIntent().getStringExtra("path");
        try {
            Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.parse(path));
            photoView.setImageBitmap(mBitmap);
        } catch (IOException e) {
            Toast.makeText(FullImageActivity.this, "Có lỗi xảy ra trong quá trình zoom hình, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

}
