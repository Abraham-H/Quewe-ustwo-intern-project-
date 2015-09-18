package app.com.example.android.queuee2;
import app.com.example.android.queuee2.model.FirebaseListener;
import app.com.example.android.queuee2.model.HerokuApiClient;
import app.com.example.android.queuee2.model.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class AddToQueueActivity extends Activity {

    private static String TAG = AddToQueueActivity.class.getSimpleName();
    private TextView userAddedTextView;
    private Response.QueueData queueData;
    private HerokuApiClient.HerokuService herokuService;
    private static String androidId;
    private Gson gson;
    private FirebaseListener firebaseListener;
    private TextView queuePositionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_queue);
        setInstanceVariables();
        instantiateViews();
        setupFirebaseListener();
    }

    private void setInstanceVariables() {
        herokuService = HerokuApiClient.getHerokuService();
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        gson = new Gson();
    }

    private void instantiateViews() {

       ImageButton addToQueueImageButton = (ImageButton)findViewById(R.id.add_to_queue_image_button);
        queuePositionTextView = (TextView)findViewById(R.id.queuePositionTextView);
        userAddedTextView = (TextView)findViewById(R.id.userAddedTextView);

        addToQueueImageButton.setEnabled(true);
        addToQueueImageButton.setOnClickListener((v) -> {
            addUserToQueue();
            launchInQueueView();
        });
    }

    private void addUserToQueue(){
        herokuService.add("queue", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onAddUser, this::onHerokuError);
    }

    private void onAddUser(JsonElement herokuData){
        Response.Message response  = gson.fromJson(herokuData, Response.Message.class);
        userAddedTextView.setText(response.getMessage());
    }

    private void onHerokuError(Throwable error){
        Log.d(TAG, "onHerokuError: " + error.getLocalizedMessage());
    }
    public void updateViewsWithServerData(){
        herokuService.info("queue", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((herokuData) -> {
                    queueData = gson.fromJson(herokuData, Response.QueueData.class);
                    queuePositionTextView.setText(String.valueOf(queueData.getPosition()));
                }, this::onHerokuError);
    }

    private void launchInQueueView(){
        Intent intent = new Intent(this, InQueueActivity.class);
        startActivity(intent);
    }

    public void setupFirebaseListener(){
        firebaseListener = new FirebaseListener(this,this::updateViewsWithServerData);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }
}