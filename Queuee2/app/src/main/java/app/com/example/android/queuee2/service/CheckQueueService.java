package app.com.example.android.queuee2.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.com.example.android.queuee2.InQueueActivity;
import app.com.example.android.queuee2.R;
import app.com.example.android.queuee2.YouAreNextActivity;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Notification;

public class CheckQueueService extends Service {

    int mStartMode;       // indicates how to behave if the service is killed
    private final IBinder mBinder = new LocalBinder();      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    private static final String TAG = CheckQueueService.class.getSimpleName();
    private Queue mQueue;

    private ScheduledExecutorService mScheduledExecutorService;
    private boolean isBound;
    private InQueueActivity.InQueueActivityListener mBoundChangeListener;

    public CheckQueueService() {
    }

    public class LocalBinder extends Binder {
        public CheckQueueService getService() {
            return CheckQueueService.this;
        }
    }

    @Override
    public void onCreate() {
        // The service is being created
        Log.d(TAG, "onCreate() called with: " + " " + this.toString());
        mAllowRebind = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        Log.d(TAG, "onStartCommand() called with: " + "intent = [" + intent + "], flags = [" + flags + "], startId = [" + startId + "]");
        if (intent == null) {
            setChangeListener("queue2", null);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        Log.d(TAG, "onBind() called with: " + "intent = [" + intent + "]");
        isBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        Log.d(TAG, "onUnbind() called with: " + "intent = [" + intent + "]");
        isBound = false;
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        Log.d(TAG, "onRebind() called with: " + "intent = [" + intent + "]");
        isBound = true;
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        disconnectChangeListener();
        Log.d(TAG, "onDestroy() called");
    }

    public void setChangeListener(String queueId, InQueueActivity.InQueueActivityListener boundChangeListener){
        mQueue = new Queue();
        mBoundChangeListener = boundChangeListener;
        mQueue.setChangeListener(queueId, this::changeListener);
    }


    private void changeListener(){
        mQueue.getUser(this::onGetUserInfo, this::onGetUserInfoError);
    }

    private void onGetUserInfo(Response response) {

        int position = ((Double) response.getData()).intValue() + 1;
        Log.d(TAG, "onGetUserInfo() called with: " + "response = [" + response + "]");

        if (isBound) {
            mBoundChangeListener.run(position);
        } else if (position == 1){
            Notification.removeLastNotification(this);
            Notification.youAreNextNotification(this,
                    YouAreNextActivity.class, mQueue.getQueueId());
        } else if (position == 2) {
            Notification.removeLastNotification(this);
            Notification.almostThereNotification(this,
                    InQueueActivity.class, mQueue.getQueueId());

        }
    }

    private void onGetUserInfoError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        switch (error.getStatus()) {
            case 404: // Not in the queue
//                throw new RuntimeException("NOT IN THE QUEUE");
//                break;
            case 400: // Queue Not Found
//                throw new RuntimeException("QUEUE NOT FOUND");
//                break;
        }
    }

    public void checkSnoozable(Runnable onSnoozable, Runnable onNotSnoozable){
        mQueue.getQueue(response -> {
            ArrayList<String> queueData = (ArrayList<String>) response.getData();
            if (queueData.contains(mQueue.getUserId())) {
                if (queueData.indexOf(mQueue.getUserId()) < queueData.size() - 1) {
                    onSnoozable.run();
                } else {
                    onNotSnoozable.run();
                }
            }
        }, (e) -> Log.d(TAG, e.getLocalizedMessage()));
    }


    public Queue getQueue(){
        return mQueue;
    }

    public void connectChangeListener(){
        mQueue.connectChangeListener();
    }

    public void disconnectChangeListener(){
        mQueue.connectChangeListener();
    }

}
