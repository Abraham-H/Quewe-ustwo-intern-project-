package app.com.example.android.queuee2.model;

import android.app.Activity;

import app.com.example.android.queuee2.utils.Utils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by bkach on 9/28/15.
 */
public class Queue {

    private Activity mActivity;
    private HerokuApiClient.HerokuService mHerokuService;
    private FirebaseListener mFirebaseListener;
    private Runnable mOnFirebaseChange;
    private static String sAndroidId;
    private String mQueueId;

    public Queue(Activity activity) {
        this.mActivity = activity;
        mHerokuService = HerokuApiClient.getHerokuService();
        sAndroidId = android.provider.Settings.Secure.getString(activity.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
    }

    public void setChangeListener(Runnable callback) {
        if (mQueueId != null) {
            this.mOnFirebaseChange = callback::run;
            mFirebaseListener = new FirebaseListener(mActivity, mOnFirebaseChange);
            mFirebaseListener.connectListener();
        }
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
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    public void removeUserFromQueue(Action1<Response> onSuccess, Action1<Throwable> onFailure){
        mHerokuService.remove(mQueueId, sAndroidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    public void getQueue(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        mHerokuService.info(mQueueId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    public void getUser(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        mHerokuService.info(mQueueId, sAndroidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    public void setQueueId(String queueId) {
        this.mQueueId = queueId;
    }

    public String getQueueId() {
        return mQueueId;
    }

}
