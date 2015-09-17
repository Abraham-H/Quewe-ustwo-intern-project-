package app.com.example.android.queuee2;
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

public class NewQueueActivity extends Activity {

    private static String TAG = NewQueueActivity.class.getSimpleName();
    private ImageButton addToQueueImageButton;
    private TextView numInLineTextView;
    private TextView dequeueTextView;
    private TextView userAddedTextView;
    private Response.QueueData queueData;
    private HerokuApiClient.HerokuService herokuService;
    private static String androidId;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_queue);
        herokuService = HerokuApiClient.getHerokuService();
        setAndroidId();
        gson= new Gson();
        populateView();
        setQueue();
    }

    private void setQueue(){
        queueData = new Response.QueueData();
    }

    private void onHerokuAddUser(JsonElement herokuData){
        Response.Message response  = gson.fromJson(herokuData, Response.Message.class);
        userAddedTextView.setText(response.getMessage());
    }

    private void onHerokuRecieved(JsonElement herokuData) {
        Gson gson = new Gson();
        queueData = gson.fromJson(herokuData,Response.QueueData.class);
        numInLineTextView.setText(String.valueOf(queueData.getSize()));
    }

    private void onHerokuError(Throwable error){
        Log.d(TAG, "onHerokuError: " + error.getLocalizedMessage());
    }

    private void populateView() {
        addToQueueImageButton = (ImageButton)findViewById(R.id.add_to_queue_image_button);
        numInLineTextView = (TextView)findViewById(R.id.num_in_line_textView);
        dequeueTextView = (TextView)findViewById(R.id.deqeue_view);
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
                .subscribe(this::onHerokuAddUser);
    }

    private void launchInQueueView(){
        Intent intent = new Intent(this, InQueueActivity.class);
        startActivity(intent);
    }

    private void setAndroidId(){
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_queue_view, menu);
        return true;
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
