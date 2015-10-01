package app.com.example.android.queuee2;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
        this.getActionBar().hide();
        setViews();
        setQueue();
        removeNotification();
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

    private void removeNotification(){
        if (Notification.ACTIVE_NOTIFICATION_ID != 0) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Notification.ACTIVE_NOTIFICATION_ID);
        }
    }

    private void setQueue(){
        mQueue = new Queue();
        mQueue.setQueueId(getIntent().getStringExtra("queueId"));
        mQueue.setChangeListener(this::changeListener);
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
                backToAddToQueueActivity();
                break;
            case 400: // Queue Not Found
                backToAddToQueueActivity();
                break;
        }
    }

    private void setViews() {
        Button finishedShoppingButton = (Button)findViewById(R.id.finished_shopping_button);
        finishedShoppingButton.setEnabled(true);
        finishedShoppingButton.setOnClickListener(this::launchThankYouActivity);
    }

    public void backToAddToQueueActivity() {
        mQueue.disconnectChangeListener();
        Intent intent = new Intent(this,AddToQueueActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){

    }

    private void launchThankYouActivity(View v){
        mQueue.disconnectChangeListener();
        Intent intent = new Intent(this, ThankYouActivity.class);
        intent.putExtra("queueId", mQueue.getQueueId());
        startActivity(intent);

    }
}
