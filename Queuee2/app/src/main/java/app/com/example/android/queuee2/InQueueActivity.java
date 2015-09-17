package app.com.example.android.queuee2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import app.com.example.android.queuee2.model.HerokuApiClient;
import app.com.example.android.queuee2.model.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InQueueActivity extends Activity {

    private static String TAG = InQueueActivity.class.getSimpleName();
    private HerokuApiClient.HerokuService herokuService;
    private static String androidId;
    private Response.QueueData queueData;
    private TextView removedFromQueueTextView;
    private TextView queuePositionTextView;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_queue);
        herokuService = HerokuApiClient.getHerokuService();
        populateView();
        gson= new Gson();
        setAndroidId();
        setQueue();
        loadQueuePositionInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_in_queue, menu);
        return true;
    }

    private void setQueue(){
        queueData = new Response.QueueData();
    }

    private void loadQueuePositionInfo(){
        herokuService.info("queue", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onHerokuReceived, this::onHerokuError);
    }
    private void removeUserFromQueue(){
        herokuService.dequeue()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((herokuData) -> {
                    Response.Message response = gson.fromJson(herokuData, Response.Message.class);
                    removedFromQueueTextView.setText(response.getMessage());
                },(e) -> Log.d(TAG, "dequeue " + e.getLocalizedMessage()));
    }

    private void onHerokuReceived(JsonElement herokuData) {
        Gson gson = new Gson();
        queueData = gson.fromJson(herokuData,Response.QueueData.class);
        queuePositionTextView.setText(String.valueOf(queueData.getSize()));
    }

    private void onHerokuError(Throwable error){
        Log.d(TAG, "onHerokuError: " + error.getLocalizedMessage());
    }

    private void setAndroidId(){
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    private void populateView() {
        removedFromQueueTextView = (TextView)findViewById(R.id.removedFromQueueTextView);
        queuePositionTextView = (TextView)findViewById(R.id.queuePositionTextView);

        Button removeMeFromQueueButton = (Button) findViewById(R.id.removeMeFromQueueButton);
        removeMeFromQueueButton.setEnabled(true);
        removeMeFromQueueButton.setOnClickListener((v) -> {
            removeUserFromQueue();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
