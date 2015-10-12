package app.com.example.android.queuee2;

import app.com.example.android.queuee2.dialog.ProgressDialog;
import app.com.example.android.queuee2.model.BeaconListener;
import app.com.example.android.queuee2.model.Permissions;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddToQueueActivity extends Activity {

    private static final String TAG = AddToQueueActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private BeaconListener mBeaconListener;
    private Queue mQueue;
    private boolean mIsBluetoothDenied;

    private TextView mNumInQueueTextView;
    private TextView mBottomTextView;
    private ImageButton mAddToQueueImageButton;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_queue);
        setInstanceVariables();
        setViews();
        Permissions.askLocationPermission(this);
    }
    @Override
    protected void onStart() {
        super.onStart();
        dismissLoadingDialog();
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
        mBeaconListener = new BeaconListener(this);
        mQueue = new Queue();
        mIsBluetoothDenied = false;
    }

    private void setViews() {
        Utils.setupActionBar( this, null, getActionBar(), null );
        mNumInQueueTextView = (TextView) findViewById(R.id.num_in_queue_textview);
        mAddToQueueImageButton = (ImageButton) findViewById(R.id.add_to_queue_image_button);
        mBottomTextView = (TextView) findViewById(R.id.bottom_text_view);
        mAddToQueueImageButton.setEnabled(false);
        mAddToQueueImageButton.setOnClickListener(this::addToQueueButtonTapped);
    }


    private void addToQueueButtonTapped(View v) {
        mAddToQueueImageButton.setEnabled(false);
        mBottomTextView.setText("Adding to Queue...");
        addUserToQueue();
    }

    private void addUserToQueue() {
        mQueue.addUserToQueue(this::onUserAdded, this::onUserAddedError);
    }

    private void onUserAdded(Response response) {
        Log.d(TAG, response.getMessage());
        mQueue.disconnectChangeListener();
        launchActivity(InQueueActivity.class);
    }

    private void onUserAddedError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        switch (error.getStatus()) {
            case 409: // Already in queue
                toastError(error.getMessage());
                break;
            case 404: // Queue Not Found
                toastError(error.getMessage());
                break;
        }
    }

    private void connectBeaconListener(boolean isBluetoothDenied) {
        mBeaconListener.connect(this::onBeaconFound, this::onBeaconError, isBluetoothDenied);
    }

    private void onBeaconFound(String queueId) {
        mQueue.setChangeListener(queueId, this::changeListener);
    }

    private void onBeaconError(Throwable throwable) {
        Log.d(TAG, "EstimoteBeacon error:" + throwable.getMessage());
    }

    private void disconnectBeaconListener() {
        mBeaconListener.disconnect();
    }

    private void changeListener(){
        mQueue.getQueue(this::onGetQueue, this::onGetQueueError);
    }

    private void onGetQueue(Response response) {
        ArrayList<String> queueData = (ArrayList<String>) response.getData();
        // TODO: Queue contains user? (in queue class)
        if (queueData.contains(mQueue.getUserId())) {
            Toast.makeText(this, "Already in Queue", Toast.LENGTH_SHORT).show();
            if (queueData.indexOf(mQueue.getUserId()) == 0) {
                launchActivity(YouAreNextActivity.class);
            } else {
                launchActivity(InQueueActivity.class);
            }
        } else {
            String resultString = String.valueOf(queueData.size()) +
                    (queueData.size() == 1 ? " person" : " people")
                    + " in " + mQueue.getQueueId();
            mNumInQueueTextView.setText(resultString);
            mAddToQueueImageButton.setEnabled(true);
            mBottomTextView.setText("");
        }
        setBodyImage(mQueue.getQueueId());
    }

    private void setBodyImage(String queueId) {
        if(queueId.equals("queue1") || queueId.equals("queue2") || queueId.equals("queue3")) {
            ImageView actionBarImageView = (ImageView) findViewById(R.id.action_bar_centered_image);
            actionBarImageView.setImageResource(R.drawable.logo_hm);
        }
    }

    private void onGetQueueError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        if (error.getStatus() == 404) { // Queue not found
            mAddToQueueImageButton.setEnabled(false);
            mNumInQueueTextView.setText(mQueue.getQueueId() + " Closed");
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

    private void dismissLoadingDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
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

    @Override // Android M Integration
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog ->
                            Log.d(TAG, "onRequestPermissionResult() called with: " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults + "]"));
                    builder.show();
                }
                return;
            }
        }
    }
}