package app.com.example.android.queuee2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import app.com.example.android.queuee2.model.HerokuApiClient;
import app.com.example.android.queuee2.model.QueueInfo;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class InQueueActivity extends Activity {

    private static String TAG = InQueueActivity.class.getSimpleName();
    private HerokuApiClient.HerokuService herokuService;
    private static String androidId;
    private QueueInfo queueInfoInfo;
    private Button removeMeFromQueueButton ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_queue);
        herokuService = HerokuApiClient.getHerokuService();
        setAndroidId();
        setQueue();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_in_queue, menu);
        return true;
    }

    private void setQueue(){
        queueInfoInfo = new QueueInfo();
    }

    private void getQueuePositionInfo(){
        herokuService.info("queue", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onHerokuRecieved, this::onHerokuError);
    }

    private void onHerokuRecieved(JsonElement herokuData) {
        Gson gson = new Gson();
        queueInfoInfo = gson.fromJson(herokuData,QueueInfo.class);
       // numInLineTextView.setText(String.valueOf(queue.getSize()));
    }

    private void onHerokuError(Throwable error){
        Log.d(TAG, "onHerokuError: " + error.getLocalizedMessage());
    }

    private void setAndroidId(){
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    private void populateView() {
        removeMeFromQueueButton = (Button)findViewById(R.id.add_to_queue_image_button);

        removeMeFromQueueButton.setEnabled(true);
        removeMeFromQueueButton.setOnClickListener((v) -> {

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
