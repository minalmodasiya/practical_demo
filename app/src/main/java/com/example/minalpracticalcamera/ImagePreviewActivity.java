package com.example.minalpracticalcamera;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class ImagePreviewActivity extends AppCompatActivity {


    ImageView img_preview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        String imgUri = getIntent().getStringExtra("KEY");
        img_preview = (ImageView) findViewById(R.id.img_preview);
        img_preview.setImageURI(Uri.parse(imgUri));
    }
}