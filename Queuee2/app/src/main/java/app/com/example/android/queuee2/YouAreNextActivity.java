package app.com.example.android.queuee2;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import app.com.example.android.queuee2.dialog.LeaveQueueConfirmationDialog;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Notification;

public class YouAreNextActivity extends Activity {

    private static final String TAG = YouAreNextActivity.class.getSimpleName();
    private Queue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_are_next);
        setViews();
        setQueue();
        Notification.removeLastNotification(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQueue.connectChangeListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQueue.disconnectChangeListener();
    }

    private void setQueue(){
        mQueue = new Queue();
        // TODO: 10/5/15 get rid of this
        String queueId = getIntent().getStringExtra("queueId") == null ? "queue2" : getIntent().getStringExtra("queueId");
        mQueue.setChangeListener(queueId, this::changeListener);
    }

    private void changeListener() {
        mQueue.getUser(this::onGetUser, this::onGetUserError);
    }

    private void onGetUser(Response response) {
        Log.d(TAG, "onGetUser: Still in queue...");
    }

    private void onGetUserError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        switch (error.getStatus()) {
            case 404: // Not in the queue
                toThankYouActivity();
                break;
            case 400: // Queue Not Found
                toThankYouActivity();
                break;
        }
    }

    private void setViews() {
    }

    public void backToAddToQueueActivity() {
        mQueue.disconnectChangeListener();
        Intent intent = new Intent(this,AddToQueueActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void toThankYouActivity() {
        mQueue.disconnectChangeListener();
        Intent intent = new Intent(this,ThankYouActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){

    }

    private void toastError(String message) {
        Log.d(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
