package app.com.example.android.queuee2;

import app.com.example.android.queuee2.animation.CustomAnimationController;
import app.com.example.android.queuee2.dialog.ProgressDialog;
import app.com.example.android.queuee2.model.BeaconListener;
import app.com.example.android.queuee2.model.Permissions;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.controller.BaseControllerListener;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.ImageInfo;

import java.util.ArrayList;

public class AddToQueueActivity extends Activity {

    private static final String TAG = AddToQueueActivity.class.getSimpleName();
    private static final int REQUEST_ENABLE_BT = 1234;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private BeaconListener mBeaconListener;
    private Queue mQueue;
    private boolean mIsBluetoothDenied;

    private TextView mSubtitleTextView;
    private TextView mHeaderTextView;
    private TextView mFooterTextView;
    private ImageView mAddToQueueLogoImageView;
    private ImageButton mAddToQueueImageButton;
    private SimpleDraweeView mDraweeView;

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
        mSubtitleTextView = (TextView) findViewById(R.id.add_to_queue_subtitle_text_view);
        mAddToQueueImageButton = (ImageButton) findViewById(R.id.add_to_queue_image_button);
        mAddToQueueLogoImageView = (ImageView) findViewById(R.id.add_to_queue_activity_logo_image_view);
        mFooterTextView = (TextView) findViewById(R.id.add_to_queue_footer_text_view);
        mHeaderTextView = (TextView) findViewById(R.id.add_to_queue_header_text_view);
        mAddToQueueImageButton.setEnabled(false);
        mAddToQueueImageButton.setOnClickListener(this::addToQueueButtonTapped);
        runLoadingAnimation();
    }

    private void runLoadingAnimation(){
        // TODO: 10/14/15 custom drawee controller and custom URI
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(R.drawable.animation_loading))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setControllerListener(new CustomAnimationController())
                .build();
        mDraweeView = (SimpleDraweeView) findViewById(R.id.add_to_queue_activity_loading_animation);
        mDraweeView.setController(controller);
    }


    private void addToQueueButtonTapped(View v) {
        mAddToQueueImageButton.setEnabled(false);
        mFooterTextView.setText("Adding to Queue...");
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
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("queueId", queueId);
        editor.commit();

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
            if (queueData.indexOf(mQueue.getUserId()) == 0) {
                launchActivity(YouAreNextActivity.class);
            } else {
                launchActivity(InQueueActivity.class);
            }
        } else {
            updateViews(queueData);
        }
    }

    private void updateViews(ArrayList<String> queueData){
        if (!mAddToQueueLogoImageView.isEnabled()) {
            mAddToQueueImageButton.setEnabled(true);
        }
        String resultString = String.valueOf(queueData.size()) +
                (queueData.size() == 1 ? " person" : " people") + " in queue";
        mSubtitleTextView.setText(resultString);
        mFooterTextView.setText("Press to enter queue");
        mAddToQueueLogoImageView.setImageResource(Utils.getQueueImageResource(mQueue.getQueueId()));
        resetQueueIcon();
    }

    private void resetQueueIcon(){
        mHeaderTextView.setVisibility(View.INVISIBLE);
        mAddToQueueLogoImageView.setImageResource(Utils.getQueueImageResource(mQueue.getQueueId()));
    }

    private void onGetQueueError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        if (error.getStatus() == 404) { // Queue not found
            mAddToQueueImageButton.setEnabled(false);
            resetQueueIcon();
            mSubtitleTextView.setText("queue closed");
            mFooterTextView.setText("");
        }
    }

    private void launchActivity(Class toActivityClass) {
        Intent intent = new Intent(this, toActivityClass);
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