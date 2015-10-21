package app.com.example.android.queuee2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;

import app.com.example.android.queuee2.activity.StyledActionBarActivity;
import app.com.example.android.queuee2.dialog.InQueueDialog;
import app.com.example.android.queuee2.dialog.LeaveQueueConfirmationDialog;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.service.CheckQueueService;
import app.com.example.android.queuee2.view.InQueueLinearLayout;
import rx.functions.Action1;

public class InQueueActivity extends StyledActionBarActivity {

    private static final String LAUNCH_DIALOG = "launchDialog";
    private static final String TAG = InQueueActivity.class.getSimpleName();
    private InQueueLinearLayout mView;
    private Intent mServiceIntent;
    private CheckQueueService mService;
    private Action1<Integer> mChangeListener;

    @NonNull
    static Intent createInQueueActivityIntent(Context context) {
        Intent intent = new Intent(context, InQueueActivity.class);
        intent.putExtra(LAUNCH_DIALOG, true);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_queue);
        launchPopup();
        setupView();
        setListener();
        launchService();
    }


    @Override
    protected void onResume() {
        super.onResume();
        bindService();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    private void launchPopup() {
        if (getIntent().getBooleanExtra(LAUNCH_DIALOG, false)) {
            new InQueueDialog(this);
        }
    }

    private void setupView() {
        mView = (InQueueLinearLayout) findViewById(R.id.in_queue_linear_layout);
        mView.setSnoozeButtonListener(() ->
                mService.getQueue().snooze(
                        (r) -> Log.d(TAG, "Snooze Success!"),
                        (e) -> Log.d(TAG, "Snooze Error!")));
        setCancelListener(this::cancelConfirmation);
    }

    private void launchService() {
        mServiceIntent = new Intent(this, CheckQueueService.class);
        startService(mServiceIntent);
        bindService();
    }

    private void bindService() {
        mServiceIntent = new Intent(this, CheckQueueService.class);
        bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void setListener() {
        mChangeListener = (position) ->
        {
            if (position == 0) {
                launchThankYouActivity();
            } else if (position == -1) {
                finish();
            } else {
                mView.update(position);
                if (position == 1) {
                    removeCancelButton();
                } else {
                    if (mService.isLast()) {
                        mView.lastInQueue();
                    } else {
                        mView.notLastInQueue();
                    }
                }
            }
        };
    }

    @Override
    public void onBackPressed() {
        cancelConfirmation();
    }

    private void cancelConfirmation() {
        new LeaveQueueConfirmationDialog(this, this::removeFromQueue, () -> {});
    }

    private void removeFromQueue() {
        mView.leavingQueue();
        mService.disconnectChangeListener();
        mService.getQueue().removeUserFromQueue(this::onRemoveSuccess, this::onRemoveError);
    }

    private void onRemoveSuccess(Response response) {
        stopService(mServiceIntent);
        finish();
    }

    private void onRemoveError(Throwable throwable) {
        mService.connectChangeListener();
        Response.Error error = Response.getError(throwable);
        Log.d(TAG, "Error: " + error.getMessage());
    }

    private void launchThankYouActivity() {
        stopService(mServiceIntent);
        Intent intent = new Intent(this, ThankYouActivity.class);
        startActivity(intent);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            CheckQueueService.LocalBinder binder = (CheckQueueService.LocalBinder) service;
            mService = binder.getService();
            String queueId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString("queueId", null);
            mService.setChangeListener(queueId, mChangeListener);
        }
        public void onServiceDisconnected(ComponentName className) {}
    };
}
