package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import app.com.example.android.queuee2.dialog.LeaveQueueConfirmationDialog;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.service.CheckQueueService;

public class InQueueActivity extends Activity {

    private static String TAG = InQueueActivity.class.getSimpleName();

    private ImageButton mSnoozeButton;
    private RelativeLayout mCancelRelativeLayout;
    private Intent mServiceIntent;
    private CheckQueueService mService;
    private InQueueActivityListener mChangeListener;
    boolean mBound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_queue);
        setViews();
        setListener();
        launchService();
    }

    @Override
    protected void onResume(){
        super.onResume();
        bindService();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unbindService();
    }

    private void launchService(){
        mServiceIntent = new Intent(this, CheckQueueService.class);
        startService(mServiceIntent);
        bindService();
    }

    private void bindService() {
        mServiceIntent = new Intent(this, CheckQueueService.class);
        bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        unbindService(mConnection);
    }

    private void stopService() {
        stopService(mServiceIntent);
    }

    private void setListener(){
        mChangeListener = this::changeListener;
    }

    private void changeListener(int position){
        if (position == 1) {
            launchYouAreNextActivity();
        }
        else {
            TextView queuePositionTextView = (TextView)findViewById(R.id.queue_position_text_view);
            TextView timeEstimationTextView = (TextView)findViewById(R.id.time_estimation_text_view);
            queuePositionTextView.setText(String.valueOf(position-1));
            timeEstimationTextView.setText("Around\n" + String.valueOf((position-1) * 2) + " minutes");
        }
        mService.checkSnoozable(
                () -> mSnoozeButton.setEnabled(true),
                () -> mSnoozeButton.setEnabled(false)
        );
    }


    private void setViews() {
        mSnoozeButton = (ImageButton) findViewById(R.id.snooze_button);
        mSnoozeButton.setOnClickListener(v -> {
            mService.getQueue().snooze(
                    (r) -> mSnoozeButton.setEnabled(true),
                    (e) -> toastError(e.getMessage())
            );
        });

        mCancelRelativeLayout = (RelativeLayout) findViewById(R.id.cancelHeaderRelativeLayout);
        mCancelRelativeLayout.setOnClickListener(z -> launchLeaveQueueDialog());
    }

    private void launchLeaveQueueDialog(){
        new LeaveQueueConfirmationDialog(this, this::onYesLeaveQueue, this::onNoLeaveQueue);
    }

    private void onYesLeaveQueue(){
        removeFromQueue();
    }

    private void onNoLeaveQueue(){}

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        prepareAndRunAnimation();
        super.onWindowFocusChanged(hasFocus);
    }

    private void prepareAndRunAnimation(){
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(R.drawable.waiting_animation))
                .build();
        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();
        SimpleDraweeView sdv = (SimpleDraweeView) findViewById(R.id.waiting_animation);
        sdv.setController(controller);
    }

    @Override
    public void onBackPressed() {
        launchLeaveQueueDialog();
    }

    private void removeFromQueue(){
        mService.disconnectChangeListener();
        TextView timeEstimationTextView = (TextView)findViewById(R.id.time_estimation_text_view);
        timeEstimationTextView.setText("Leaving\nQueue...");
        mCancelRelativeLayout.setEnabled(false);

        mService.getQueue().removeUserFromQueue(this::onRemoveSuccess, this::onRemoveError);
    }

    private void onRemoveSuccess(Response response){
        stopService();
        finish();
    }

    private void onRemoveError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        switch (error.getStatus()) {
            case 404: // Not in the queue
                toastError(error.getMessage());
                break;
            case 400: // Queue Not Found
                toastError(error.getMessage());
                break;
        }
    }

    private void launchYouAreNextActivity(){
        stopService();

        Intent intent = new Intent(this, YouAreNextActivity.class);
        intent.putExtra("queueId", mService.getQueue().getQueueId());
        startActivity(intent);
    }

    private void toastError(String message) {
        Log.d(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            CheckQueueService.LocalBinder binder = (CheckQueueService.LocalBinder) service;
            mService = binder.getService();
            mService.setChangeListener(getIntent().getStringExtra("queueId"), mChangeListener);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mBound = false;
        }
    };

    public interface InQueueActivityListener {
        void run(int position);
    }

}
