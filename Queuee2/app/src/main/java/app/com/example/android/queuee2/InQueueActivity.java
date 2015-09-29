package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Notification;

public class InQueueActivity extends Activity {

    private static String TAG = InQueueActivity.class.getSimpleName();

    private TextView queuePositionTextView;
    private ImageView waitingAnimationImageView;

    private Queue mQueue;
    private boolean activityVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().hide();
        setContentView(R.layout.activity_in_queue);
        setQueue();
        setViews();
    }

    @Override
    protected void onResume(){
        super.onResume();
        mQueue.connectChangeListener();
        activityVisible = true;
    }

    @Override
    protected void onPause(){
        super.onPause();
        mQueue.disconnectChangeListener();
        activityVisible = false;
    }

    private void setQueue(){
        mQueue = new Queue(this);
        mQueue.setQueueId(getIntent().getStringExtra("queueId"));
        mQueue.setChangeListener(this::changeListener);
    }

    private void changeListener(){
        mQueue.getUser(this::onGetUserInfo, this::onGetUserInfoError);
    }

    private void onGetUserInfo(Response response) {
        int position = ((Double) response.getData()).intValue() + 1;
        if (position == 1) {
            if(activityVisible)
            {
                launchYouAreNextActivity();
            } else {
                Notification.youAreNextNotification(this,
                        YouAreNextActivity.class, mQueue.getQueueId(), "You're Next!");
            }
        } else {
            String noun = position-1 == 1 ? " person" : " people";
            queuePositionTextView.setText(String.valueOf(position-1) + " " + noun + " ahead of you");
        }
    }

    private void onGetUserInfoError(Throwable throwable) {
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

    private void setViews() {
        queuePositionTextView = (TextView)findViewById(R.id.queuePositionTextView);
        waitingAnimationImageView = (ImageView)findViewById(R.id.waitingAnimationImageView);
    }

    private void prepareAndRunAnimation(){
        waitingAnimationImageView.setBackgroundResource(R.drawable.waiting_animation);
        AnimationDrawable waitingAnimationDrawable = (AnimationDrawable) waitingAnimationImageView.getBackground();
        waitingAnimationDrawable.start();
    }

    @Override
    public void onBackPressed() {
        removeFromQueue();
    }

    private void removeFromQueue(){
        mQueue.disconnectChangeListener();
        mQueue.removeUserFromQueue(this::onRemoveSuccess, this::onRemoveError);
    }

    private void onRemoveSuccess(Response response){
        Toast.makeText(this, "Removed from queue", Toast.LENGTH_SHORT).show();
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
        mQueue.disconnectChangeListener();
        Intent intent = new Intent(this, YouAreNextActivity.class);
        intent.putExtra("queueId", mQueue.getQueueId());
        startActivity(intent);
    }

    private void toastError(String message) {
        Log.d(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        prepareAndRunAnimation();
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_in_queue, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_cancel:
                removeFromQueue();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
