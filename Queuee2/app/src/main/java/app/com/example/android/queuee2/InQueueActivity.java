package app.com.example.android.queuee2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
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
    private Response.QueueData queueData;
    private TextView popFromQueueTextView;
    private TextView queuePositionTextView;
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
        popFromQueueTextView = (TextView)findViewById(R.id.removedFromQueueTextView);
        queuePositionTextView = (TextView)findViewById(R.id.queuePositionTextView);

        Button popFromQueueButton = (Button) findViewById(R.id.popFromQueueButton);
        popFromQueueButton.setEnabled(true);
        popFromQueueButton.setOnClickListener((v) -> {
            popUserFromQueue();
        });
    }

    private void setInstanceVariables(){
        herokuService = HerokuApiClient.getHerokuService();
        gson = new Gson();
        queueData = new Response.QueueData();
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
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

    public void setupFirebaseListener(){
        firebaseListener = new FirebaseListener(this,this::updateViewsWithServerData);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        herokuService.remove("queue",androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((herokuData) -> {
                    Log.d(TAG, "onBackPressed : " +
                            gson.fromJson(herokuData, Response.Message.class).getMessage());
                });
    }

    private void popUserFromQueue(){
        herokuService.pop()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((herokuData) -> {
                    Response.Message response = gson.fromJson(herokuData, Response.Message.class);
                    popFromQueueTextView.setText(response.getMessage());
                }, this::onHerokuError);
    }

    private void onHerokuError(Throwable error){
        Log.d(TAG, "onHerokuError: " + error.getLocalizedMessage());
    }
}
