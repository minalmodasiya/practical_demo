package com.example.minalpracticalcamera;

import android.app.Application;
import android.content.Context;

//import androidx.multidex.MultiDex;



public class DemoApplication extends Application {
    private static DemoApplication app;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        app = this;
      //  MultiDex.install(this);

    }

    public static Context getContext() {
        return app.getApplicationContext();
    }
    public synchronized static DemoApplication getInstance() {
        return app;
    }
    @Override
    public void onCreate() {
        super.onCreate();

    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }
}

