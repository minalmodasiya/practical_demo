package com.example.minalpracticalcamera;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity  {

    public Button btnopenCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnopenCamera = (Button) findViewById(R.id.btnopenCamera);
        btnopenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOpenCamera = new Intent(getApplicationContext(),CameraActivity.class);
                startActivity(intentOpenCamera);
            }
        });

    }

//    @Override
//    public void onClick(View view) {
//        switch (view.getId()) {
//            case R.id.btnopenCamera:
//                Intent intentOpenCamera = new Intent(getApplicationContext(),CameraActivity.class);
//                startActivity(intentOpenCamera);
//                break;
//
//
//        }
//    }

}

