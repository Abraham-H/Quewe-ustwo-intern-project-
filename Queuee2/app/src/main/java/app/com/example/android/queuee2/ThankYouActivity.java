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
        setContentView(R.layout.activity_thank_you);
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
