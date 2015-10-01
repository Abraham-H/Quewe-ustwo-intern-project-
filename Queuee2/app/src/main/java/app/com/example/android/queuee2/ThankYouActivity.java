package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import app.com.example.android.queuee2.model.FirebaseListener;
import app.com.example.android.queuee2.model.HerokuApiClient;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Utils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ThankYouActivity extends Activity {

    private static final String TAG = ThankYouActivity.class.getSimpleName();
    private Queue mQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().hide();
        setContentView(R.layout.activity_thank_you);
        setQueue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mQueue.connectChangeListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mQueue.disconnectChangeListener();
    }

    private void setQueue(){
        mQueue = new Queue();
        mQueue.setChangeListener(getIntent().getStringExtra("queueId"), this::changeListener);
    }

    private void changeListener() {
        mQueue.getUser(this::onGetUser, this::onGetUserError);
    }

    private void onGetUser(Response response) {
        Log.d(TAG, "onGetUser: Still in queue...");
    }

    private void onGetUserError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        switch (error.getStatus()) {
            case 404: // Not in the queue
                backToAddToQueueActivity();
                break;
            case 400: // Queue Not Found
                backToAddToQueueActivity();
                break;
        }
    }

    public void backToAddToQueueActivity() {
        mQueue.disconnectChangeListener();
        Intent intent = new Intent(this,AddToQueueActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){

    }
}
