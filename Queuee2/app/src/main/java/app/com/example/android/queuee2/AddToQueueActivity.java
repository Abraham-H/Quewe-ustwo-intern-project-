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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
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
        });
    }

    private void addUserToQueue(){
        herokuService.add("queue1", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((herokuData) -> {
                    Response response = gson.fromJson(herokuData, Response.class);
                    Log.d(TAG, "addUserToQueue response:" + response.getMessage());
                    launchInQueueView();
                }, (throwable) -> {
                    Response.Error error = Response.getError(throwable);
                    switch (error.getStatus()) {
                        case 409: // Already in queue
                            toastError(error.getMessage());
                            break;
                        case 404: // Queue Not Found
                            toastError(error.getMessage());
                            break;
                    }
                });
    }

    public void setupFirebaseListener(){
        FirebaseListener firebaseListener = new FirebaseListener(this, this::updateViewsWithServerData);
    }

    public void updateViewsWithServerData(){
        herokuService.info("queue1")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(herokuData -> {
                    Response response = gson.fromJson(herokuData, Response.class);
                    ArrayList<String> queue = (ArrayList<String>) response.getData();
                    String noun = queue.size() == 1 ? " person" : " people";
                    numInQueueTextView.setText(String.valueOf(queue.size()) + noun + " in the queue");
                    addToQueueImageButton.setEnabled(true);
                }, throwable -> {
                    Response.Error error = Response.getError(throwable);
                    if (error.getStatus() == 404) {
                        toastError(error.getMessage());
                    }
                });
    }

    private void launchInQueueView(){
        Intent intent = new Intent(this, InQueueActivity.class);
        startActivity(intent);
    }

    private void toastError(String message) {
        Log.d(TAG, "Error :" + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }
}
