package com.example.minalpracticalcamera;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageInfo;
import androidx.camera.core.ImageProxy;

import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CameraActivity extends AppCompatActivity{
    ImageView imageCaptureButton;
    String mDateFormat = null;
     ImageCapture imageCapture;
    FrameLayout frameRoot;
    View viewOverlayCenter;

    PreviewView viewFinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && RequestPermissionsActivity.startPermissionActivity(this)) {
            return;
        }
        setContentView(R.layout.activity_camera);

        frameRoot=(FrameLayout)findViewById(R.id.frameRoot);
        viewOverlayCenter = (View)findViewById(R.id.viewOverlay_center);
        viewFinder = (PreviewView)findViewById(R.id.viewFinder);
        imageCaptureButton = (ImageView)findViewById(R.id.image_capture_button);

        startCamera();
        imageCaptureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                capturePhoto();
            }
        });
    }
    private void capturePhoto(){
        if (imageCapture == null) {
            return;
        }

        


        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "Card-" + mDateFormat.format(String.valueOf(System.currentTimeMillis())));
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");


        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

        imageCapture.takePicture(ContextCompat.getMainExecutor(this), new ImageCapture.OnImageCapturedCallback() {
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                super.onCaptureSuccess(imageProxy);

                ImageInfo imageInfo = imageProxy.getImageInfo();

                Bitmap image = rotateBitmapIfNeeded(imageProxyToBitmap(imageProxy),imageInfo);
                saveBitmap(cropBitmapToCard(image,frameRoot,viewOverlayCenter),uri);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setClass(CameraActivity.this,  ImagePreviewActivity.class);
                intent.putExtra("KEY", uri.toString());
                startActivity(intent);

                imageProxy.close();
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                super.onError(exception);
                Log.e("MainActivity","Error on takePicture",exception);
            }
        });

    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    startCameraX(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
    }

    void startCameraX(@NonNull ProcessCameraProvider cameraProvider) {
        cameraProvider.unbindAll();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        // preview use case
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(viewFinder.getSurfaceProvider());


        // image capture use case
        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(this.getWindowManager().getDefaultDisplay().getRotation())
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build();

        // bind to lifecycle
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
    }

    void saveBitmap(Bitmap target, Uri uri){
        try {
            OutputStream output = getContentResolver().openOutputStream(uri);
            target.compress(Bitmap.CompressFormat.JPEG, 100, output);
        }
        catch (Exception e) {
            Log.d("onBtnSavePng", e.toString()); // java.io.IOException: Operation not permitted
        }
    }

    Bitmap cropBitmapToCard(Bitmap source, View frame, View cardPlaceHolder){
        float scaleX = source.getWidth() / (float)frame.getWidth();
        float scaleY = source.getHeight() / (float) frame.getHeight();

        int x =(int)((cardPlaceHolder.getLeft()) * scaleX);
        int y = (int)((cardPlaceHolder.getTop()) * scaleY);

        Log.v("MainActivity-Crop","leftPos: " + cardPlaceHolder.getLeft() + " width: " + cardPlaceHolder.getWidth());

        int width = (int)(cardPlaceHolder.getWidth() * scaleX);
        int height = (int)(cardPlaceHolder.getHeight() * scaleY);

        return Bitmap.createBitmap(source,x,y,width,height);
    }

    private Bitmap imageProxyToBitmap(ImageProxy image)
    {
        ImageProxy.PlaneProxy planeProxy = image.getPlanes()[0];
        ByteBuffer buffer = planeProxy.getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    private Bitmap rotateBitmapIfNeeded(Bitmap source, ImageInfo info){
        int angle = info.getRotationDegrees();
        Matrix mat = new Matrix();
        mat.postRotate(angle);
        return Bitmap.createBitmap(source,0,0,source.getWidth(),source.getHeight(),mat,true);
    }

}