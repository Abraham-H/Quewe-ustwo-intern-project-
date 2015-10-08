package app.com.example.android.queuee2.model;

import android.util.Log;
import java.util.ArrayList;

import com.google.gson.JsonElement;
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
        addSubscribers(mHerokuService.add(mQueueId, sAndroidId), onSuccess, onFailure);
    }

    public void removeUserFromQueue(Action1<Response> onSuccess, Action1<Throwable> onFailure){
        addSubscribers(mHerokuService.remove(mQueueId, sAndroidId), onSuccess, onFailure);
    }

    public void getQueue(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        addSubscribers(mHerokuService.info(mQueueId), onSuccess, onFailure);
    }

    public void getUser(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        addSubscribers(mHerokuService.info(mQueueId, sAndroidId), onSuccess, onFailure);
    }

    public void snooze(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        addSubscribers(mHerokuService.snooze(mQueueId, sAndroidId), onSuccess, onFailure);
    }

    public void addSubscribers(Observable<JsonElement> observable, Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    public boolean snoozeable() {
        mHerokuService.info(mQueueId)
                .map(Utils::jsonToResponse)
                .subscribe(
                        (response) -> {
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
