package model;

import com.example.abraham.cashierqueuee.MyApplication;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import utils.Utils;

/**
 * Created by bkach on 9/28/15.
 */
public class Queue {

    private HerokuApiClient.HerokuService mHerokuService;
    private FirebaseListener mFirebaseListener;
    private Runnable mOnFirebaseChange;
    private static String sAndroidId;
    private String mQueueId;

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

    public void startQueue(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        mHerokuService.add(mQueueId)//
                .compose(applySchedulers())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    public void closeQueue(Action1<Response> onSuccess, Action1<Throwable> onFailure) {
        mHerokuService.remove(mQueueId)// TODO: 10/9/2015 Check mHerokuService code remove with 1 param if needed!!!!
                .compose(applySchedulers())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
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

    public void popUserFromQueue(Action1<Response> onSuccess, Action1<Throwable> onFailure){
        mHerokuService.pop(mQueueId)
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

    public String getUserId() {
        return sAndroidId;
    }

    public String getQueueId() {
        return mQueueId;
    }

    public void setQueueId(String queueId) {
        mQueueId = queueId;
    }

}
