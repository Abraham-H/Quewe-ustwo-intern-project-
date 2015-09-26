package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import app.com.example.android.queuee2.model.HerokuApiClient;
import app.com.example.android.queuee2.model.Response;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class YouAreNextActivity extends Activity {

    private static String androidId;
    private HerokuApiClient.HerokuService mHerokuService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_are_next);
        setInstanceVariables();
        instantiateViews();
        checkIfInQueue();
    }

    private void setInstanceVariables(){
        mHerokuService = HerokuApiClient.getHerokuService();
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    private void instantiateViews() {
        Button finishedShoppingButton = (Button)findViewById(R.id.finished_shopping_button);
        finishedShoppingButton.setEnabled(true);
        finishedShoppingButton.setOnClickListener((v) -> {
            launchThankYouActivity();
        });
    }

    private void checkIfInQueue() {
        mHerokuService.info("queue1", androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe((response) -> {
                    checkIfInQueue();
                }, throwable -> {
                    Response.Error error = Response.getError(throwable);
                    switch (error.getStatus()) {
                        case 404: // Not in the queue
                            backToAddToQueueActivity();
                            break;
                        case 400: // Queue Not Found
                            backToAddToQueueActivity();
                            break;
                    }
                });
    }

    public void backToAddToQueueActivity() {
        Intent a = new Intent(this,AddToQueueActivity.class);
        a.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(a);
    }

    @Override
    public void onBackPressed(){

    }

    private void launchThankYouActivity(){
        Intent intent = new Intent(this, ThankYouActivity.class);
        startActivity(intent);

    }
}
