package app.com.example.android.queuee2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import app.com.example.android.queuee2.activity.StyledActionBarActivity;
import app.com.example.android.queuee2.model.BeaconListener;
import app.com.example.android.queuee2.model.Permissions;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Utils;
import app.com.example.android.queuee2.view.AddToQueueLinearLayout;

public class AddToQueueActivity extends StyledActionBarActivity {

    private static final String TAG = AddToQueueActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final boolean DEBUG = true;
    private static final String testQueue = "queue1";

    private BeaconListener mBeaconListener;
    private Queue mQueue;
    private boolean mIsBluetoothDenied;

    private boolean mIsLaunching;

    private AddToQueueLinearLayout mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_queue);
        setInstanceVariables();
        setupView();
        Permissions.askLocationPermission(this);
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
        if (!DEBUG)
            mBeaconListener.disconnect();
        mQueue.disconnectChangeListener();
        mIsLaunching = false;
    }

    private void setInstanceVariables() {
        if (!DEBUG)
            mBeaconListener = new BeaconListener(this);
        mQueue = new Queue();
        mIsBluetoothDenied = false;
    }

    private void setupView() {
        hideActionBarLogo();
        mView = (AddToQueueLinearLayout) findViewById(R.id.add_to_queue_linear_layout);
        mView.setAddToQueueButtonListener(() ->
                mQueue.addUserToQueue(this::onUserAdded, this::onUserAddedError));
    }

    private void onUserAdded(Response response) {
        Log.d(TAG, response.getMessage());
        mQueue.disconnectChangeListener();
        launchActivity();
    }

    private void onUserAddedError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        Log.d(TAG, String.valueOf(error.getStatus()) + " Server Error: " + error.getMessage());
    }

    private void connectBeaconListener(boolean isBluetoothDenied) {
        if (!DEBUG)
            mBeaconListener.connect(this::onBeaconFound, this::onBeaconError, isBluetoothDenied);
        else
            onBeaconFound(testQueue);
    }

    private void onBeaconFound(String queueId) {
        Utils.storeQueueId(queueId);
        mQueue.setChangeListener(queueId,
                () -> mQueue.getQueue(this::onGetQueue, this::onGetQueueError));
    }

    private void onBeaconError(Throwable throwable) {
        Log.d(TAG, "EstimoteBeacon error:" + throwable.getMessage());
    }

    private void onGetQueue(ArrayList<String> queueData) {
        if (queueData.contains(mQueue.getUserId())) {
            launchActivity();
        } else {
            mView.update(queueData);
        }
    }

    private void onGetQueueError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        if (error.getStatus() == 404) { // Queue not found
            mView.update(null);
        }
    }

    private void launchActivity() {
        if (!mIsLaunching) {
            mIsLaunching = true;
            Intent intent = InQueueActivity.createInQueueActivityIntent(this);
            startActivity(intent);
        }
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