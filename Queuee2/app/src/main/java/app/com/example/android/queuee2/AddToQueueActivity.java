package app.com.example.android.queuee2;
import app.com.example.android.queuee2.model.BeaconListener;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class AddToQueueActivity extends Activity {

    private static final String TAG = AddToQueueActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1234;

    private BeaconListener mBeaconListener;
    private Queue mQueue;
    private boolean mIsBluetoothDenied;
    private static String sAndroidId;

    private TextView mNumInQueueTextView;
    private ImageButton mAddToQueueImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().hide();
        setContentView(R.layout.activity_add_to_queue);
        setInstanceVariables();
        setViews();
    }

    @Override
    protected void onResume(){
        super.onResume();
        connectBeaconListener(mIsBluetoothDenied);
        mQueue.connectChangeListener();
    }

    @Override
    protected void onPause(){
        super.onPause();
        disconnectBeaconListener();
        mQueue.disconnectChangeListener();
    }

    @Override
    public void onBackPressed() {
        String queueId = mQueue.getQueueId();
        if (queueId != null) {
            mAddToQueueImageButton.setEnabled(false);
            mNumInQueueTextView.setText("No Queue Found");
            connectBeaconListener(mIsBluetoothDenied);
            // TODO: Note sure this is such a good idea
            mQueue.setQueueId(null);
        } else{
            finish();
        }
    }

    private void setInstanceVariables() {
        mBeaconListener = new BeaconListener(this);
        mQueue = new Queue(this);
        mIsBluetoothDenied = false;
        sAndroidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    private void setViews() {
        TextView welcomeTextView = (TextView)findViewById(R.id.welcome_text_view);
        welcomeTextView.setLineSpacing(0.0f,0.8f);

        mNumInQueueTextView = (TextView)findViewById(R.id.num_in_queue_textview);
        mAddToQueueImageButton = (ImageButton)findViewById(R.id.add_to_queue_image_button);
        mAddToQueueImageButton.setEnabled(false);
        mAddToQueueImageButton.setOnClickListener(v -> addUserToQueue());
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
        mQueue.setQueueId(queueId);
        mQueue.disconnectChangeListener();
        mQueue.setChangeListener(this::changeListener);
        mAddToQueueImageButton.setEnabled(true);
    }

    private void onBeaconError(Throwable throwable) {
        Log.d(TAG, "EstimoteBeacon error:" + throwable.getMessage());
    }


    private void disconnectBeaconListener() {
        mBeaconListener.disconnect();
    }

    private void changeListener(){
        mQueue.getQueue(this::onGetQueue,this::onGetQueueError);
    }

    private void onGetQueue(Response response) {
        ArrayList<String> queue = (ArrayList<String>) response.getData();
        if (queue.contains(sAndroidId)) {
            Toast.makeText(this, "Already in Queue", Toast.LENGTH_SHORT).show();
            if (queue.indexOf(sAndroidId) == 0) {
                launchActivity(YouAreNextActivity.class);
            } else {
                launchActivity(InQueueActivity.class);
            }
        } else {
            String noun = queue.size() == 1 ? " person" : " people";
            mNumInQueueTextView.setText(String.valueOf(queue.size()) + noun + " in " + mQueue.getQueueId());
            mAddToQueueImageButton.setEnabled(true);
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

    @Override // Bluetooth Dialogue callback
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth turning on, please wait...", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                mIsBluetoothDenied = true;
                Toast.makeText(this, "Bluetooth Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}