package app.com.example.android.queuee;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static String androidID = "";


    private WifiManager wifi;
    private static final int REQUEST_ENABLE_BT = 1234;
    private ArrayList<String> queue;
    private Firebase mRef;
    private Button mQueueStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupFirebase();
        androidID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        // Setup Start Button
        mQueueStartButton = (Button) findViewById(R.id.queueStartButton);
        mQueueStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create queue and populate
                if (queue == null) {
                    queue = new ArrayList<String>();
                }
                // If not already in queue
                if (queue.indexOf(androidID) == -1) {
                    queue.add(androidID);
                    mRef.setValue(queue);
                }
            }
        });

        // Setup Next Button
        Button mQueueNextButton = (Button) findViewById(R.id.queueNextButton);
        mQueueNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove Queue Entry
                if (queue != null) {
                    queue.remove(0);
                    mRef.setValue(queue);
                }
            }
        });
    }

    @Override
    public void onStart() {
        checkBluetoothWifi();
        super.onStart();
    }

    public void checkBluetoothWifi(){
        wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        android.net.NetworkInfo datac = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);


        if ((wifi != null || datac != null) || !mBluetoothAdapter.isEnabled()) {
            enableBluetoothWifi();
        }
            else
            {
                //Bluetooth/wifi is enabled
            }
    }

    public void enableBluetoothWifi(){

        AlertDialog.Builder myDialog = MyDialog.create(this, getLayoutInflater(), "Title", "Content");
        myDialog.setPositiveButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                MainActivity.this.finish();
            }
        })
                .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                            wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
                            wifi.setWifiEnabled(true);

                        dialog.cancel();
                    }
                }).show();
    }

    public void setupFirebase() {

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://burning-torch-3063.firebaseio.com/queue");
        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                TextView mTextViewShowNext = (TextView) findViewById(R.id.textViewShowNext);
                TextView mTextViewShowWaitTime = (TextView) findViewById(R.id.textViewShowWaitTime);

                int avgWaitInMins = 2;
                queue = (ArrayList<String>) dataSnapshot.getValue();

                if (dataSnapshot.getValue() == null) {
                    queue = null;
                    mTextViewShowNext.setText("No Queue");
                    mQueueStartButton.setEnabled(true);
                } else {
                    int index = queue.indexOf(androidID);

                    if (index == 0) {           // Next
                        mTextViewShowNext.setText("Your Turn!");
                        mTextViewShowWaitTime.setText("Your wait time is up!");
                        sendBasicNotification();
                        mQueueStartButton.setEnabled(false);
                    } else if (index == -1) {   // Not in Queue
                        mTextViewShowNext.setText("Not in queue");
                        mQueueStartButton.setEnabled(true);
                    } else {                    // In Queue
                        int qWaitTimeInMins = queue.indexOf(androidID) * avgWaitInMins;
                        mTextViewShowNext.setText("Your position in q is: " + Integer.toString(index));
                        mTextViewShowWaitTime.setText("Your wait time: " + Integer.toString(qWaitTimeInMins) + " Mins");
                        mQueueStartButton.setEnabled(false);
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, firebaseError.toString());
            }
        });
    }

    private void sendBasicNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setAutoCancel(true);
        mBuilder.setContentTitle("You are next in queue!");
        mBuilder.setContentText("Please go to till 1. Thanks for shopping with us today");
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);

        Intent resultIntent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
