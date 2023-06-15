package com.example.minalpracticalcamera;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatToggleButton;
import androidx.core.app.ActivityCompat;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class HomeActivity extends AppCompatActivity {

    public Button btnopenCamera;
    public AppCompatToggleButton btnstartService;

    public String longitude,latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnopenCamera = (Button) findViewById(R.id.btnopenCamera);
        btnstartService = (AppCompatToggleButton) findViewById(R.id.btnstartService);

        btnopenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentOpenCamera = new Intent(getApplicationContext(), CameraActivity.class);
                startActivity(intentOpenCamera);
            }
        });

        btnstartService.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    LocationManager _locManager = null;
                    _locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                    boolean gps_enabled = _locManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    if (gps_enabled) {
                        ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.INTERNET}, 1);
                        Intent _scanServiceIntent = new Intent(HomeActivity.this, LocationUpdateService.class);
                        startService(_scanServiceIntent);
                    }

                    exportToCSVFile();

                } else if (!isChecked) {
                    stopService(new Intent(HomeActivity.this, LocationUpdateService.class));
                }


            }
        });


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationUpdateEvent event) {
//handle updates here
        longitude = String.valueOf(event.getLocation().latitude);
        latitude = String.valueOf(event.getLocation().longitude);
    };
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void exportToCSVFile() {
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS+"/"+"LocationData.csv").getAbsolutePath());
            ContentValues values = new ContentValues();
            if(!file.exists()) {

                values.put(MediaStore.Files.FileColumns.DISPLAY_NAME, "LocationData");       //file name
                values.put(MediaStore.Files.FileColumns.MIME_TYPE, "text/csv");        //file extension, will automatically add to file
                values.put(MediaStore.Files.FileColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + "/");
            }
            Uri uri = getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);      //important!

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            String saveToFile = "Longitude= "+longitude+" , Latitude= "+latitude;
            outputStream.write(saveToFile.getBytes());

            outputStream.close();

        } catch (IOException e) {
        }


    }


}

