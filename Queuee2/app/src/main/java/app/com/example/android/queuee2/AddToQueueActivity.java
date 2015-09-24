package app.com.example.android.queuee2;
import app.com.example.android.queuee2.model.FirebaseListener;
import app.com.example.android.queuee2.model.HerokuApiClient;
import app.com.example.android.queuee2.model.Response;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AddToQueueActivity extends Activity {

    private static String TAG = AddToQueueActivity.class.getSimpleName();
    private HerokuApiClient.HerokuService herokuService;
    private static String androidId;
    private Gson gson;
    private TextView numInQueueTextView;
    private ImageButton addToQueueImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_to_queue);
        setInstanceVariables();
        instantiateViews();
        updateViewsWithServerData();
        setupFirebaseListener();
    }

    private void setInstanceVariables() {
        herokuService = HerokuApiClient.getHerokuService();
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        gson = new Gson();
    }

    private void instantiateViews() {
        TextView welcomeTextView = (TextView)findViewById(R.id.welcome_text_view);
        welcomeTextView.setLineSpacing(0.0f,0.8f);
        numInQueueTextView = (TextView)findViewById(R.id.num_in_queue_textview);
        addToQueueImageButton = (ImageButton)findViewById(R.id.add_to_queue_image_button);
        addToQueueImageButton.setEnabled(false);
        addToQueueImageButton.setOnClickListener((v) -> {
            addUserToQueue();
            launchInQueueView();
        });
    }

    private void addUserToQueue(){
        herokuService.add("queue1", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((herokuData) -> {
                    Response response = gson.fromJson(herokuData, Response.class);
                    toastError(response);
                }, this::onHerokuError);
    }

    private void onHerokuError(Throwable error){
        Log.d(TAG, "onHerokuError: " + error.getLocalizedMessage());
        toastError(
                new Response("Heroku Error: " + error.getLocalizedMessage(),true));
    }

    public void setupFirebaseListener(){
        FirebaseListener firebaseListener = new FirebaseListener(this, this::updateViewsWithServerData);
    }

    public void updateViewsWithServerData(){
        herokuService.info("queue1")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((herokuData) -> {
                    Response response = gson.fromJson(herokuData, Response.class);
                    if(response.isError()){
                        if(response.getMessage().equals("Queue does not exist")){
                            numInQueueTextView.setText("Queue not open");
                        }
                    } else {
                        ArrayList<String> queue = (ArrayList<String>) response.getData();
                        String noun = queue.size() == 1 ? " person" : " people";
                        numInQueueTextView.setText(String.valueOf(queue.size()) + noun + " in the queue");
                        addToQueueImageButton.setEnabled(true);
                    }

                }, this::onHerokuError);
    }

    private void launchInQueueView(){
        Intent intent = new Intent(this, InQueueActivity.class);
        startActivity(intent);
    }

    private void toastError(Response response) {
        if (response.isError()) {
            Toast.makeText(AddToQueueActivity.this,
                    response.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
    }
}
