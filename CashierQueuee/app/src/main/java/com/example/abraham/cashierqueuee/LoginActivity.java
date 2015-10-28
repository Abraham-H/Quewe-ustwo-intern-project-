package com.example.abraham.cashierqueuee;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import model.BeaconListener;
import model.Queue;
import model.Response;
import utils.Utils;

public class LoginActivity extends Activity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final Boolean DEBUG = true;
    private static final int REQUEST_ENABLE_BT = 1234;
    private BeaconListener mBeaconListener;
    private Queue mQueue;
    private boolean mIsBluetoothDenied;
    private static final String testQueue = "queue1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setInstanceVariables();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        connectBeaconListener(mIsBluetoothDenied);
        mQueue.connectChangeListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disconnectBeaconListener();
        mQueue.disconnectChangeListener();
    }

    private void setInstanceVariables() {

        if (!DEBUG) {
            mBeaconListener = new BeaconListener(this);
        }

        mQueue = new Queue();
        mIsBluetoothDenied = false;
    }

    private void connectBeaconListener(boolean isBluetoothDenied) {
        if (DEBUG) {
            onBeaconFound(testQueue);
        } else {
            mBeaconListener.connect(this::onBeaconFound, this::onBeaconError, isBluetoothDenied);
        }
    }

    private void onBeaconFound(String queueId) {
        Utils.storeQueueId(queueId);
        mQueue.setQueueId(queueId);
        mQueue.getQueue(this::onQueueExists, this::onQueueExistError);
    }

    private void onQueueExists(Response response) {
        launchActivity(QueueInProgressActivity.class);
    }

    private void onQueueExistError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        switch (error.getStatus()) {
            case 404: // Queue Not Found
                toastError(error.getMessage());
                launchActivity(StartQueueActivity.class);
                break;
        }
    }

    private void onBeaconError(Throwable throwable) {
        Log.d(TAG, "EstimoteBeacon error:" + throwable.getMessage());
    }

    private void disconnectBeaconListener() {
        if (!DEBUG) {
            mBeaconListener.disconnect();
        }
    }

    private void launchActivity(Class toActivityClass) {
            Intent intent = new Intent(this, toActivityClass);
            intent.putExtra("queueId", mQueue.getQueueId());
            startActivity(intent);
    }

    private void toastError(String message) {
        Log.d(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override // Bluetooth Dialogue callback
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth turning on, please wait...", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                mIsBluetoothDenied = true;
                Toast.makeText(this, "Bluetooth Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}