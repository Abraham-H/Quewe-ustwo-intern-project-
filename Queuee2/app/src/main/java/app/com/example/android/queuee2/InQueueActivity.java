package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_queue);
        setInstanceVariables();
        instantiateViews();
        updateViewsWithServerData();
        setupFirebaseListener();
    }

    private void instantiateViews() {
        queuePositionTextView = (TextView)findViewById(R.id.queuePositionTextView);
        waitingAnimationImageView = (ImageView)findViewById(R.id.waitingAnimationImageView);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        prepareAnimation();
        super.onWindowFocusChanged(hasFocus);
    }

    private void setInstanceVariables(){
        herokuService = HerokuApiClient.getHerokuService();
        gson = new Gson();
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    public void updateViewsWithServerData(){
        herokuService.info("queue1", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((herokuData) -> {
                    Response.Message userInfo = gson.fromJson(herokuData, Response.Message.class);
                    queuePositionTextView.setText(userInfo.getMessage());
                }, this::onHerokuError);
    }

    public void setupFirebaseListener(){
        firebaseListener = new FirebaseListener(this,this::updateViewsWithServerData);
    }

    private void prepareAnimation(){
        waitingAnimationImageView.setBackgroundResource(R.drawable.waiting_animation);
        waitingAnimationDrawable = (AnimationDrawable) waitingAnimationImageView.getBackground();
        waitingAnimationDrawable.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void cancelAction(){
        removeCurrentUser();
        Context context = getApplicationContext();
        CharSequence text = "Cancel has been tapped! You have been removed";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void removeCurrentUser(){
        herokuService.remove("queue1", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((herokuData) -> {
                    Log.d(TAG, "onBackPressed : " +
                            gson.fromJson(herokuData, Response.Message.class).getMessage());
                }, this::onHerokuError);
    }
    
    private void onHerokuError(Throwable error){
        Log.d(TAG, "onHerokuError: " + error.getLocalizedMessage());
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
