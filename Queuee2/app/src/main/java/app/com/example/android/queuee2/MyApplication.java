package app.com.example.android.queuee2;

import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Abraham on 9/29/2015.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
