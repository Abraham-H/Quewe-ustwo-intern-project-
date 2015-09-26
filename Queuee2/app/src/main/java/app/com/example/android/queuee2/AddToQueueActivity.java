package app.com.example.android.queuee2;
import app.com.example.android.queuee2.model.BeaconListener;
import app.com.example.android.queuee2.model.FirebaseListener;
import app.com.example.android.queuee2.model.HerokuApiClient;
import app.com.example.android.queuee2.model.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class AddToQueueActivity extends Activity {

    private static String TAG = AddToQueueActivity.class.getSimpleName();
    private HerokuApiClient.HerokuService herokuService;
    private FirebaseListener firebaseListener;
    private BeaconListener mBeaconListener;
    private static String androidId;
    private TextView numInQueueTextView;
    private ImageButton addToQueueImageButton;
    private static final int REQUEST_ENABLE_BT = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().hide();
        setContentView(R.layout.activity_add_to_queue);
        setInstanceVariables();
        instantiateViews();
    }

    @Override
    protected void onStart(){
        super.onStart();
        connectBeaconListener();
        connectFirebaseListener();
        updateViewsWithServerData();
    }

    @Override
    protected void onPause(){
        super.onPause();
        disconnectBeaconListener();
        disconnectFirebaseListener();
    }

    private void setInstanceVariables() {
        herokuService = HerokuApiClient.getHerokuService();
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        firebaseListener = new FirebaseListener(this, this::updateViewsWithServerData);
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

    private void addUserToQueue() {
        herokuService.add("queue1", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe((response) -> {
                    Log.d(TAG, response.getMessage());
                    launchInQueueActivity();
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
//                    connectBeaconListener();
                });
    }

    private void connectBeaconListener() {
        mBeaconListener = new BeaconListener(this);
        Observable<String> beaconObservable = mBeaconListener.getObservable();
        beaconObservable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(uuid -> {
                    Log.d(TAG, "setupBeaconListener :" + uuid);
                    Toast.makeText(this, "PING!", Toast.LENGTH_SHORT).show();
                    addUserToQueue();
                }, error -> {
                    Log.d(TAG, "EstimoteBeacon error:" + error.getMessage());
                });
    }

    private void disconnectBeaconListener() {
        mBeaconListener.disconnect();
    }

    private void connectFirebaseListener(){
        firebaseListener.connectListener();
    }

    private void disconnectFirebaseListener() {
        firebaseListener.disconnectListener();
    }

    private void updateViewsWithServerData() {
        herokuService.info("queue1")
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe(response -> {
                    ArrayList<String> queue = (ArrayList<String>) response.getData();
                    if (queue.contains(androidId)) {
                        Toast.makeText(this, "Already in Queue", Toast.LENGTH_SHORT).show();
                        launchInQueueActivity();
                    } else {
                        String noun = queue.size() == 1 ? " person" : " people";
                        numInQueueTextView.setText(String.valueOf(queue.size()) + noun + " in the queue" +
                                "\nScanning for beacons...");
                        addToQueueImageButton.setEnabled(true);
                    }
                }, throwable -> {
                    Response.Error error = Response.getError(throwable);
                    if (error.getStatus() == 404) { // Queue not found
                        numInQueueTextView.setText("Queue Closed");
                    }
                });
    }

    private void launchInQueueActivity(){
        disconnectBeaconListener();
        disconnectFirebaseListener();
        Intent intent = new Intent(this, InQueueActivity.class);
        startActivity(intent);
    }

    private void toastError(String message) {
        Log.d(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override // Bluetooth Dialogue callback
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth turning on, please wait...", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Bluetooth Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
