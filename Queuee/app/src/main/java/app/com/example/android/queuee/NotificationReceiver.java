package app.com.example.android.queuee;

/**
 * Created by Abraham on 9/1/2015.
 */
import android.app.Activity;
import android.os.Bundle;

public class NotificationReceiver extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notification_received);
    }
}