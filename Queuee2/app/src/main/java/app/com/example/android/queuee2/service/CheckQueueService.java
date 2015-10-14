package app.com.example.android.queuee2.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import app.com.example.android.queuee2.InQueueActivity;
import app.com.example.android.queuee2.YouAreNextActivity;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Response;
import app.com.example.android.queuee2.utils.Notification;

public class CheckQueueService extends Service {

    private final IBinder mBinder = new LocalBinder();      // interface for clients that bind
    boolean mAllowRebind; // indicates whether onRebind should be used
    private static final String TAG = CheckQueueService.class.getSimpleName();
    private static Queue sQueue;

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
        mAllowRebind = true;
        sQueue = new Queue();
        Log.d(TAG, "onCreate() called with: " + "");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // The service is starting, due to a call to startService()
        String queueId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("queueId", "Shared Preferences Error");
        sQueue.setQueueId(queueId);
        if (intent == null) {
            Log.d(TAG, "intent is null <------------------------------------");
            setChangeListener(sQueue.getQueueId(), null);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        isBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // All clients have unbound with unbindService()
        isBound = false;
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        // A client is binding to the service with bindService(),
        // after onUnbind() has already been called
        isBound = true;
    }

    @Override
    public void onDestroy() {
        // The service is no longer used and is being destroyed
        disconnectChangeListener();
    }

    public void setChangeListener(String queueId, InQueueActivity.InQueueActivityListener boundChangeListener){
        mBoundChangeListener = boundChangeListener;
        sQueue.setChangeListener(queueId, this::changeListener);
    }


    private void changeListener(){
        sQueue.getUser(this::onGetUserInfo, this::onGetUserInfoError);
    }

    private void onGetUserInfo(Response response) {

        int position = ((Double) response.getData()).intValue() + 1;

        if (isBound) {
            mBoundChangeListener.run(position);
        } else if (position == 1){
            Notification.removeLastNotification(this);
            Notification.youAreNextNotification(this,
                    YouAreNextActivity.class, sQueue.getQueueId());
        } else if (position == 2) {
            Notification.removeLastNotification(this);
            Notification.almostThereNotification(this,
                    InQueueActivity.class, sQueue.getQueueId());
        }
    }

    private void onGetUserInfoError(Throwable throwable) {
        Notification.removeLastNotification(this);
        if (isBound){
            mBoundChangeListener.run(-1);
        } else {
           stopSelf();
        }
    }

    public void checkSnoozable(Runnable onSnoozable, Runnable onNotSnoozable){
        sQueue.getQueue(response -> {
            ArrayList<String> queueData = (ArrayList<String>) response.getData();
            if (queueData.contains(sQueue.getUserId())) {
                if (queueData.indexOf(sQueue.getUserId()) < queueData.size() - 1) {
                    onSnoozable.run();
                } else {
                    onNotSnoozable.run();
                }
            }
        }, (e) -> Log.d(TAG, e.getLocalizedMessage()));
    }


    public Queue getQueue(){
        return sQueue;
    }

    public void connectChangeListener(){
        sQueue.connectChangeListener();
    }

    public void disconnectChangeListener(){
        sQueue.connectChangeListener();
    }

}
