package com.example.dailyselfi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class PhotoDetailActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        ImageView imageViewDetail = findViewById(R.id.imageViewDetail);
        TextView textViewDetail = findViewById(R.id.textViewDetail);

        // Nhận dữ liệu từ Intent
        Intent intent = getIntent();
        PhotoModel photo = (PhotoModel) intent.getSerializableExtra("photo");

        if (photo != null) {
            // Hiển thị ảnh và thông tin
            Glide.with(this).load(photo.getPath()).into(imageViewDetail);
            textViewDetail.setText(photo.getDateTime());
        }
    }
}
