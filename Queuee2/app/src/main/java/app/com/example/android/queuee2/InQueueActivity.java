package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;

import app.com.example.android.queuee2.dialog.LeaveQueueConfirmationDialog;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.service.CheckQueueService;
import app.com.example.android.queuee2.utils.Utils;

public class InQueueActivity extends Activity {

    private static String TAG = InQueueActivity.class.getSimpleName();

    private ImageButton mSnoozeButton;
    private TextView mHeaderTextView;
    private TextView mSubheaderTextView;
    private TextView mFooterTextView;
    private SimpleDraweeView mDraweeView;

    private Intent mServiceIntent;
    private CheckQueueService mService;
    private InQueueActivityListener mChangeListener;
    boolean mBound;
    boolean mAlmostNextStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_queue);
        setViews();
        setListener();
        launchService();
    }


    @Override
    protected void onResume(){
        super.onResume();
        bindService();
    }

    @Override
    protected void onPause(){
        super.onPause();
        unbindService();
    }

    private void launchService(){
        mServiceIntent = new Intent(this, CheckQueueService.class);
        startService(mServiceIntent);
        bindService();
    }

    private void bindService() {
        mServiceIntent = new Intent(this, CheckQueueService.class);
        bindService(mServiceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindService() {
        unbindService(mConnection);
    }

    private void stopService() {
        stopService(mServiceIntent);
    }

    private void setListener(){
        mChangeListener = this::changeListener;
    }

    private void changeListener(int position){
        if (position == 1) {
            launchYouAreNextActivity();
        } else if (position == 2){
            if ( !mAlmostNextStyle ) {
                setAlmostNextStyle();
            }
        } else if (position == -1) {
            finish();
        }
        else {
            if ( mAlmostNextStyle ) {
                removeAlmostNextStyle();
            }

            String suffix;
            if (position % 10 == 1 && position % 11 != 0) {
                suffix = "st";
            } else if (position % 10 == 2 && position % 12 != 0) {
                suffix = "nd";
            } else if (position % 10 == 3 && position % 13 != 0) {
                suffix = "rd";
            } else {
                suffix = "th";
            }
            String headerText = String.valueOf(position) + suffix;
            String subheaderText = "About " + String.valueOf(position*2) + " min left";

            mHeaderTextView.setText(headerText);
            mSubheaderTextView.setText(subheaderText);
        }
        mService.checkSnoozable(
                () -> mSnoozeButton.setEnabled(true),
                () -> mSnoozeButton.setEnabled(false)
        );
    }

    private void removeAlmostNextStyle(){
        mAlmostNextStyle = false;
        View activity = findViewById(R.id.activityInQueueMainRelativeLayout);
        View root = activity.getRootView();
        root.setBackgroundColor(Color.WHITE);

        runAnimation(R.drawable.animation_waiting);

        mHeaderTextView.setTextSize(70.0f);
        mHeaderTextView.setTextColor(getResources().getColor(R.color.happy_grey));

        mSubheaderTextView.setTextColor(getResources().getColor(R.color.happy_grey));
        mFooterTextView.setTextColor(getResources().getColor(R.color.happy_grey));

        mSnoozeButton.setVisibility(View.INVISIBLE);
        mSnoozeButton = (ImageButton) findViewById(R.id.in_queue_activity_snooze_button);
        mSnoozeButton.setVisibility(View.VISIBLE);

        setSnoozeButtonListener();
    }

    private void setAlmostNextStyle() {
        mAlmostNextStyle = true;
        View activity = findViewById(R.id.activityInQueueMainRelativeLayout);
        View root = activity.getRootView();
        root.setBackgroundColor(getResources().getColor(R.color.happy_peach));

        runAnimation(R.drawable.animation_almost_there);

        mHeaderTextView.setTextSize(42.0f);

        mSubheaderTextView.setText("Soon it's your turn!");
        mHeaderTextView.setText("Almost There!");

        mSubheaderTextView.setTextColor(Color.WHITE);
        mFooterTextView.setTextColor(Color.WHITE);
        mHeaderTextView.setTextColor(Color.WHITE);

        mSnoozeButton.setVisibility(View.INVISIBLE);
        mSnoozeButton = (ImageButton) findViewById(R.id.in_queue_activity_almost_there_snooze_button);
        mSnoozeButton.setVisibility(View.VISIBLE);

        setSnoozeButtonListener();

    }

    private void setViews() {
        String queueId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("queueId", "Shared Preferences Error");
        Utils.setupActionBar(this, queueId, getActionBar(), this::launchLeaveQueueDialog);
        mSnoozeButton = (ImageButton) findViewById(R.id.in_queue_activity_snooze_button);
        mHeaderTextView = (TextView) findViewById(R.id.in_queue_activity_header_text_view);
        mSubheaderTextView = (TextView) findViewById(R.id.in_queue_activity_subheader_text_view);
        mFooterTextView = (TextView) findViewById(R.id.in_queue_activity_footer_text_view);
        mDraweeView = (SimpleDraweeView) findViewById(R.id.in_queue_animation_drawee_view);
        setSnoozeButtonListener();
        runAnimation(R.drawable.animation_waiting);
    }

    private void runAnimation(int drawable){
        // TODO: 10/14/15 custom drawee controller and custom URI
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(drawable))
                .build();

        DraweeController controller = Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build();

        mDraweeView.setController(controller);
    }

    private void setSnoozeButtonListener(){
        mSnoozeButton.setOnClickListener(v -> {
            mSnoozeButton.setEnabled(false);
            mService.getQueue().snooze(
                    (r) -> mSnoozeButton.setEnabled(true),
                    (e) -> toastError(e.getMessage()));
        });
    }

    private void launchLeaveQueueDialog(){
        new LeaveQueueConfirmationDialog(this, this::removeFromQueue, () -> {});
    }

    @Override
    public void onBackPressed() {
        launchLeaveQueueDialog();
    }

    private void removeFromQueue(){
        mService.disconnectChangeListener();
        mSubheaderTextView.setText("Leaving Queue...");

        mService.getQueue().removeUserFromQueue(this::onRemoveSuccess, this::onRemoveError);
    }

    private void onRemoveSuccess(Response response){
        stopService();
        finish();
    }

    private void onRemoveError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        switch (error.getStatus()) {
            case 404: // Not in the queue
                toastError(error.getMessage());
                break;
            case 400: // Queue Not Found
                toastError(error.getMessage());
                break;
        }
    }

    private void launchYouAreNextActivity(){
        stopService();

        Intent intent = new Intent(this, YouAreNextActivity.class);
        startActivity(intent);
    }

    private void toastError(String message) {
        Log.d(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Class for interacting with the main interface of the service.
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the object we can use to
            // interact with the service.  We are communicating with the
            // service using a Messenger, so here we get a client-side
            // representation of that from the raw IBinder object.
            CheckQueueService.LocalBinder binder = (CheckQueueService.LocalBinder) service;
            mService = binder.getService();
            String queueId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString("queueId", "Shared Preferences Error");
            mService.setChangeListener(queueId, mChangeListener);
            mBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            mBound = false;
        }
    };

    public interface InQueueActivityListener {
        void run(int position);
    }

}
