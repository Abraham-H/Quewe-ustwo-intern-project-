package app.com.example.android.queuee2;
import app.com.example.android.queuee2.model.HerokuApiClient;
import app.com.example.android.queuee2.model.Queue;
import retrofit.client.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import android.app.Activity;
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
    private Queue queue;
    private HerokuApiClient.HerokuService herokuService;
    private static String androidId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_queue_view);
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);

        populateView();
        getQueue();
    }

    private void getQueue(){
        queue = new Queue();

        herokuService = HerokuApiClient.getHerokuService();

        herokuService.dequeue()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((herokuData) -> {
                    Gson gson2 = new Gson();
                    Response response = gson2.fromJson(herokuData, Response.class);
                    dequeueTextView.setText(response.getMessage());
                },(e) -> Log.d(TAG, "dequeue " + e.getLocalizedMessage()));

        herokuService.info("queue",androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onHerokuRecieved, this::onHerokuError);
    }

    private void onHerokuRecieved(JsonElement herokuData){
        Gson gson = new Gson();
        queue = gson.fromJson(herokuData,Queue.class);
        numInLineTextView.setText(String.valueOf(queue.getSize()));
    }

    private void onHerokuError(Throwable error){
        Log.d(TAG, "onHerokuError: " + error.getLocalizedMessage());
    }

    private void populateView() {
        addToQueueImageButton = (ImageButton)findViewById(R.id.add_to_queue_image_button);
        numInLineTextView = (TextView)findViewById(R.id.num_in_line_textView);
        dequeueTextView = (TextView)findViewById(R.id.deqeue_view);
        addToQueueImageButton.setEnabled(false);
        addToQueueImageButton.setOnClickListener((v) -> {});
    }

    private static class Response{
        public Response(String message){setMessage(message);}
        private String message;
        public String getMessage() {return message;}
        public void setMessage(String message) {this.message = message;}
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
