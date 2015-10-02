package app.com.example.android.queuee2;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.stetho.Stetho;

/**
 * Created by Abraham on 9/29/2015.
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
