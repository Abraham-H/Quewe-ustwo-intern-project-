package app.com.example.android.queuee2;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import app.com.example.android.queuee2.dialog.InQueueDialog;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Notification;
import app.com.example.android.queuee2.utils.PopUp;

public class InQueueActivity extends Activity {

    private static String TAG = InQueueActivity.class.getSimpleName();

    private Queue mQueue;
    private boolean activityVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_queue);
        setQueue();
        setViews();
        InQueueDialog inQueueDialog = new InQueueDialog(this);
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
        mQueue = new Queue();
        mQueue.setChangeListener(getIntent().getStringExtra("queueId"),this::changeListener);
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
            TextView queuePositionTextView = (TextView)findViewById(R.id.queuePositionTextView);
            queuePositionTextView.setText(String.valueOf(position-1));
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
        PopUp.startInQueuePopUp(this);
    }

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
