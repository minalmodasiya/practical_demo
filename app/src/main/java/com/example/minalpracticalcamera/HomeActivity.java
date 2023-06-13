package com.example.minalpracticalcamera;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity  {

    public Button btnopenCamera;
    public Button btnstartService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnopenCamera = (Button) findViewById(R.id.btnopenCamera);
        btnstartService = (Button)findViewById(R.id.btnstartService);

        btnopenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOpenCamera = new Intent(getApplicationContext(),CameraActivity.class);
                startActivity(intentOpenCamera);
            }
        });

        btnstartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager _locManager = null;
                _locManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

                boolean gps_enabled = _locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                if (gps_enabled)
                {
                    ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}, 1);
                    Intent _scanServiceIntent = new Intent(HomeActivity.this, ScanService.class);
                    startService(_scanServiceIntent);
                }

            }
        });

    }


}

