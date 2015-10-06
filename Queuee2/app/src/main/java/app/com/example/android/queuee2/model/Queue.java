package app.com.example.android.queuee2.model;

import android.util.Log;

import java.util.ArrayList;

import app.com.example.android.queuee2.MyApplication;
import app.com.example.android.queuee2.utils.Utils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by bkach on 9/28/15.
 */
public class Queue {

    private static final String TAG = Queue.class.getSimpleName();

    private HerokuApiClient.HerokuService mHerokuService;
    private FirebaseListener mFirebaseListener;
    private Runnable mOnFirebaseChange;
    private static String sAndroidId;
    private String mQueueId;
    private boolean mSnoozable;

    public Queue() {
        mHerokuService = HerokuApiClient.getHerokuService();
        sAndroidId = android.provider.Settings.Secure.getString(
                MyApplication.getAppContext().getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    public void setChangeListener(String queueId, Runnable callback) {
        // TODO: Set queueId here!
        this.disconnectChangeListener();
        this.mQueueId = queueId;
        this.mOnFirebaseChange = callback::run;
        mFirebaseListener = new FirebaseListener(mOnFirebaseChange);
        mFirebaseListener.connectListener();
    }

    public void connectChangeListener(){
        if (mFirebaseListener != null) {
            mFirebaseListener.connectListener();
        }
    }

    public void disconnectChangeListener() {
        if (mFirebaseListener != null) {
            mFirebaseListener.disconnectListener();
        }
    }

    public void addUserToQueue(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        mHerokuService.add(mQueueId, sAndroidId)
                .compose(applySchedulers())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    public void removeUserFromQueue(Action1<Response> onSuccess, Action1<Throwable> onFailure){
        mHerokuService.remove(mQueueId, sAndroidId)
                .compose(applySchedulers())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    public void getQueue(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        mHerokuService.info(mQueueId)
                .compose(applySchedulers())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    public void getUser(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        mHerokuService.info(mQueueId, sAndroidId)
                .compose(applySchedulers())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    public void snooze(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        mHerokuService.snooze(mQueueId, sAndroidId)
                .compose(applySchedulers())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    private <T> Observable.Transformer<T, T> applySchedulers() {
        return observable ->
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
    }

    public boolean snoozeable() {
        mHerokuService.info(mQueueId)
                .map(Utils::jsonToResponse)
                .subscribe(
                        (response) -> {
                            ArrayList<String> queueData = (ArrayList<String>) response.getData();
                            if (queueData.contains(getUserId())) {
                                if (queueData.indexOf(getUserId()) < queueData.size() - 1) {
                                    mSnoozable = true;
                                } else {
                                    mSnoozable = false;
                                }
                            }
                        }
                        ,
                        (e) -> Log.d(TAG, e.getLocalizedMessage()));
        return mSnoozable;
    }

    public String getUserId() {
        return sAndroidId;
    }

    public String getQueueId() {
        return mQueueId;
    }

}
