package com.example.abraham.cashierqueuee;

import android.content.Context;
import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;

/**
 * Created by Abraham on 10/6/2015.
 */
public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = getApplicationContext();
        Stetho.initializeWithDefaults(this);
        Fresco.initialize(context);
    }

    public static Context getAppContext() {
        return MyApplication.context;
    }
}
