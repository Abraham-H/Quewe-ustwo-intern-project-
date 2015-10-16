package app.com.example.android.queuee2;

import app.com.example.android.queuee2.activity.StyledActionBarActivity;
import app.com.example.android.queuee2.model.BeaconListener;
import app.com.example.android.queuee2.model.Permissions;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Utils;
import app.com.example.android.queuee2.view.AddToQueueLinearLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class AddToQueueActivity extends StyledActionBarActivity{

    private static final String TAG = AddToQueueActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1234;

    private BeaconListener mBeaconListener;
    private Queue mQueue;
    private boolean mIsBluetoothDenied;

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
        mBeaconListener.disconnect();
        mQueue.disconnectChangeListener();
    }

    private void setInstanceVariables() {
        mBeaconListener = new BeaconListener(this);
        mQueue = new Queue();
        mIsBluetoothDenied = false;
    }

    private void setupView() {
        hideActionBarLogo();
        mView = (AddToQueueLinearLayout) findViewById(R.id.add_to_queue_linear_layout);
        mView.setAddToQueueButtonListener(this::addUserToQueue);
    }

    private void addUserToQueue(View v) {
        mQueue.addUserToQueue(this::onUserAdded, this::onUserAddedError);
    }

    private void onUserAdded(Response response) {
        Log.d(TAG, response.getMessage());
        mQueue.disconnectChangeListener();
        launchActivity(InQueueActivity.class);
    }

    private void onUserAddedError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        Log.d(TAG, String.valueOf(error.getStatus()) + " Server Error: " + error.getMessage());
    }

    private void connectBeaconListener(boolean isBluetoothDenied) {
        mBeaconListener.connect(this::onBeaconFound, this::onBeaconError, isBluetoothDenied);
    }

    private void onBeaconFound(String queueId) {
        Utils.storeQueueId(queueId);
        mQueue.setChangeListener(queueId, this::changeListener);
    }

    private void onBeaconError(Throwable throwable) {
        Log.d(TAG, "EstimoteBeacon error:" + throwable.getMessage());
    }

    private void changeListener(){
        mQueue.getQueue(this::onGetQueue, this::onGetQueueError);
    }

    private void onGetQueue(Response response) {
        ArrayList<String> data = (ArrayList<String>) response.getData();
        if (data.contains(mQueue.getUserId())) {
            if (data.indexOf(mQueue.getUserId()) == 0) {
                launchActivity(YouAreNextActivity.class);
            } else {
                launchActivity(InQueueActivity.class);
            }
        } else {
            mView.update(data);
        }
    }

    private void onGetQueueError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        if (error.getStatus() == 404) { // Queue not found
            mView.update(null);
        }
    }

    private void launchActivity(Class toActivityClass) {
        Intent intent = new Intent(this, toActivityClass);
        startActivity(intent);
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