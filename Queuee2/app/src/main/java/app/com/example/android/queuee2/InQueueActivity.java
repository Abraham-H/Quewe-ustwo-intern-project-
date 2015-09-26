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

import app.com.example.android.queuee2.model.FirebaseListener;
import app.com.example.android.queuee2.model.HerokuApiClient;
import app.com.example.android.queuee2.model.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InQueueActivity extends Activity {

    private static String TAG = InQueueActivity.class.getSimpleName();
    private HerokuApiClient.HerokuService herokuService;
    private static String androidId;
    private TextView popFromQueueTextView;
    private TextView queuePositionTextView;
    private ImageView waitingAnimationImageView;
    private AnimationDrawable waitingAnimationDrawable;
    private FirebaseListener firebaseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().hide();
        setContentView(R.layout.activity_in_queue);
        setInstanceVariables();
        instantiateViews();
        updateViewsWithServerData();
        createFirebaseListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        prepareAndRunAnimation();
        super.onWindowFocusChanged(hasFocus);
    }

    private void setInstanceVariables(){
        herokuService = HerokuApiClient.getHerokuService();
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    private void instantiateViews() {
        queuePositionTextView = (TextView)findViewById(R.id.queuePositionTextView);
        waitingAnimationImageView = (ImageView)findViewById(R.id.waitingAnimationImageView);
    }

    public void updateViewsWithServerData(){
        herokuService.info("queue1", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe((response) -> {
                    int position = ((Double) response.getData()).intValue() + 1;
                    if (position == 1) {
//                        Utils.postNotification(this, "You're Next!");
                        launchYouAreNextActivity();
                    } else {
                        firebaseListener.connectListener();
                        queuePositionTextView.setText(Integer.toString(position));
                    }
                }, throwable -> {
                    Response.Error error = Response.getError(throwable);
                    switch (error.getStatus()) {
                        case 404: // Not in the queue
                            toastError(error.getMessage());
                            break;
                        case 400: // Queue Not Found
                            toastError(error.getMessage());
                            break;
                    }
                });
    }

    public void createFirebaseListener(){
        firebaseListener = new FirebaseListener(this,this::updateViewsWithServerData);
    }

    private void prepareAndRunAnimation(){
        waitingAnimationImageView.setBackgroundResource(R.drawable.waiting_animation);
        waitingAnimationDrawable = (AnimationDrawable) waitingAnimationImageView.getBackground();
        waitingAnimationDrawable.start();
    }

    @Override
    public void onBackPressed() {
        cancelAction();
    }

    private void cancelAction(){
        firebaseListener.disconnectListener();
        removeCurrentUser();
    }

    private void removeCurrentUser(){
        herokuService.remove("queue1", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe((response) -> {
                    Toast.makeText(this, "Removed from queue", Toast.LENGTH_SHORT).show();
                    finish();
                }, throwable -> {
                    Response.Error error = Response.getError(throwable);
                    switch (error.getStatus()) {
                        case 404: // Not in the queue
                            toastError(error.getMessage());
                            break;
                        case 400: // Queue Not Found
                            toastError(error.getMessage());
                            break;
                    }
                });
    }

    private void launchYouAreNextActivity(){
        firebaseListener.disconnectListener();
        Intent intent = new Intent(this, YouAreNextActivity.class);
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
                cancelAction();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
