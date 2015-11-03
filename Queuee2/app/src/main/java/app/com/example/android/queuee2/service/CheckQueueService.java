package app.com.example.android.queuee2.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;

import app.com.example.android.queuee2.InQueueActivity;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.utils.Notification;
import rx.functions.Action1;

public class CheckQueueService extends Service {

    private final IBinder mBinder = new LocalBinder();      // interface for clients that bind
    private boolean mAllowRebind; // indicates whether onRebind should be used
    private static final String TAG = CheckQueueService.class.getSimpleName();
    private static Queue sQueue;
    private int mLastPosition;

    private boolean isBound;
    private Action1<Integer> mBoundChangeListener;

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
        String queueId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("queueId", null);
        sQueue.setQueueId(queueId);
        if (intent == null) {
            setChangeListener(sQueue.getQueueId(), null);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        isBound = true;
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isBound = false;
        return mAllowRebind;
    }

    @Override
    public void onRebind(Intent intent) {
        isBound = true;
    }

    @Override
    public void onDestroy() {
        disconnectChangeListener();
    }

    public void setChangeListener(String queueId, Action1<Integer> boundChangeListener) {
        mBoundChangeListener = boundChangeListener;
        sQueue.setChangeListener(queueId,
                () -> sQueue.getQueue(this::onGetQueue, this::onGetQueueError));
    }

    private void onGetQueue(ArrayList<String> queue) {

        int position = queue.indexOf(sQueue.getUserId()) + 1;

        if (isBound) {
            mBoundChangeListener.call(position);
        } else if (position != mLastPosition) {
            mLastPosition = position;
            if (position == 1) {
                Notification.removeLastNotification(this);
                Notification.itsYourTurnNotification(this, InQueueActivity.class);
            } else if (position == 2) {
                Notification.removeLastNotification(this);
                Notification.youAreNextNotification(this, InQueueActivity.class);
            } else if (position == 5) {
                Notification.removeLastNotification(this);
                Notification.almostThereNotification(this, InQueueActivity.class, position);
            } else if (position > 10 && getQueue().isHalfway(position)){
                Notification.removeLastNotification(this);
                Notification.halfwayThereNotification(this, InQueueActivity.class, position);
            } else if (position == 10) {
                Notification.removeLastNotification(this);
                Notification.gettingCloseNotification(this, InQueueActivity.class, position);
            }
        }
    }

    private void onGetQueueError(Throwable throwable) {
        Notification.removeLastNotification(this);
        if (isBound) {
            mBoundChangeListener.call(-1);
        } else {
            stopSelf();
        }
    }

    public boolean isLast() {
        return sQueue.isLast();
    }

    public Queue getQueue() {
        return sQueue;
    }

    public void connectChangeListener() {
        sQueue.connectChangeListener();
    }

    public void disconnectChangeListener() {
        sQueue.connectChangeListener();
    }

}
