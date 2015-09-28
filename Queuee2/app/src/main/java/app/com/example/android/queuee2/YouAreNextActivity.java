package app.com.example.android.queuee2;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import app.com.example.android.queuee2.model.FirebaseListener;
import app.com.example.android.queuee2.model.HerokuApiClient;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Notification;
import app.com.example.android.queuee2.utils.Utils;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class YouAreNextActivity extends Activity {

    private static String androidId;
    private static final String TAG = YouAreNextActivity.class.getSimpleName();
    private HerokuApiClient.HerokuService mHerokuService;
    private FirebaseListener firebaseListener;
    private String queueId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getActionBar().hide();
        setContentView(R.layout.activity_you_are_next);
        setInstanceVariables();
        instantiateViews();
        connectFirebaseListener();
        checkIfInQueue();
        removeNotification();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectFirebaseListener();
    }

    private void connectFirebaseListener(){
        firebaseListener.connectListener();
    }

    private void disconnectFirebaseListener() {
        firebaseListener.disconnectListener();
    }

    private void removeNotification(){
        if (Notification.ACTIVE_NOTIFICATION_ID != 0) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Notification.ACTIVE_NOTIFICATION_ID);
        }
    }

    private void setInstanceVariables(){
        mHerokuService = HerokuApiClient.getHerokuService();
        androidId = android.provider.Settings.Secure.getString(this.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        firebaseListener = new FirebaseListener(this, this::checkIfInQueue);
        queueId = getIntent().getStringExtra("queueId");
    }

    private void instantiateViews() {
        Button finishedShoppingButton = (Button)findViewById(R.id.finished_shopping_button);
        finishedShoppingButton.setEnabled(true);
        finishedShoppingButton.setOnClickListener(this::launchThankYouActivity);
    }

    private void checkIfInQueue() {
        mHerokuService.info(queueId, androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe((response) -> {
                    Log.d(TAG,"still in queue..");
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
        Intent intent = new Intent(this,AddToQueueActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){

    }

    private void launchThankYouActivity(View v){
        Intent intent = new Intent(this, ThankYouActivity.class);
        intent.putExtra("queueId", queueId);
        startActivity(intent);

    }
}
