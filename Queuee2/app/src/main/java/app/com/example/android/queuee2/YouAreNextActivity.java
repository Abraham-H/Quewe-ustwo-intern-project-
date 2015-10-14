package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import app.com.example.android.queuee2.dialog.LeaveQueueConfirmationDialog;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Notification;
import app.com.example.android.queuee2.utils.Utils;

public class YouAreNextActivity extends Activity {

    private static final String TAG = YouAreNextActivity.class.getSimpleName();
    private Queue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_are_next);
        setQueue();
        setViews();
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
        String queueId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("queueId", "Shared Preferences Error");
        mQueue = new Queue();
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
                backToAddToQueueActivity();
                break;
        }
    }

    private void setViews() {
        String queueId = getIntent().getStringExtra("queueId");
        Utils.setupActionBar(this, queueId,
                getActionBar(), this::launchLeaveQueueDialog);
    }

    private void launchLeaveQueueDialog(){
        new LeaveQueueConfirmationDialog(this, this::onYesLeaveQueue, this::onNoLeaveQueue);
    }

    private void onYesLeaveQueue(){
        removeFromQueue();
    }

    private void onNoLeaveQueue(){}


    private void removeFromQueue(){
        mQueue.removeUserFromQueue(this::onRemoveSuccess,this::onRemoveError);
    }

    private void onRemoveSuccess(Response response){
        backToAddToQueueActivity();
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
