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
    private String queueId;

    public Queue(Activity activity) {
        this.mActivity = activity;
        mHerokuService = HerokuApiClient.getHerokuService();
    }

    public void setQueueId(String queueId) {
        this.queueId = queueId;
    }

    public void setChangeListener(Action1<Response> onSuccess,
                                  Action1<Throwable> onFailure) {
        if (queueId != null) {
            this.mOnFirebaseChange = () ->
                    mHerokuService.info(queueId)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .map(Utils::jsonToResponse)
                            .subscribe(onSuccess, onFailure);
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

    public void addUserToQueue(String queueId, String androidId,
                                Action1<Response> onSuccess,
                                Action1<Throwable> onFailure) {
        mHerokuService.add(queueId, androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

    private void getQueue(String queueId, String androidId,
                          Action1<Response> onSuccess,
                          Action1<Throwable> onFailure) {
        mHerokuService.info(queueId, androidId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Utils::jsonToResponse)
                .subscribe(onSuccess, onFailure);
    }

}
