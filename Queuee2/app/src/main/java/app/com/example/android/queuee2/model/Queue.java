package app.com.example.android.queuee2.model;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bkach on 9/2/15.
 */
public class Queue {

    public static String androidID;

    private Context ctx;
    private static String TAG = Queue.class.getSimpleName();
    private ArrayList<User> mQueue = new ArrayList<>();
    private QueueEventListener mQueueEventNoQueue;
    private QueueEventListener mQueueEventNext;
    private QueueEventListener mQueueEventInQueue;
    private QueueEventListener mQueueEventNotInQueue;
    private ConnectivityListener mQueueConnectivityEvent;
    private Firebase mFirebase;
    private Handler mHandler;
    private boolean checkingConnectivity;
    private boolean lastConnectivityStatus;

    public Queue(QueueEventListener queueEventNoQueue, QueueEventListener queueEventNext,
                 QueueEventListener queueEventInQueue, QueueEventListener queueEventNotInQueue,
                 ConnectivityListener queueConnectivityEvent, Context ctx) {
        setQueueEventNoQueue(queueEventNoQueue);
        setQueueEventNext(queueEventNext);
        setQueueEventInQueue(queueEventInQueue);
        setmQueueEventNotInQueue(queueEventNotInQueue);
        setQueueConnectivityEvent(queueConnectivityEvent);
        this.ctx = ctx;
        androidID = android.provider.Settings.Secure.getString(this.ctx.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        launchConnectivityDaemon();
        setupFirebase();
    }

    public static Queue createQueue(Context ctx){
        QueueEventListener defaultQueueListener = (i) -> {
            Log.v(TAG, "Empty Listener, Index: " + Integer.toString(i));
        };
        ConnectivityListener defaultConnectivityListner = (status) -> {
            Log.v(TAG, "Empty Listener, Connected: " + Boolean.toString(status));
        };
        return new Queue(defaultQueueListener,
                         defaultQueueListener,
                         defaultQueueListener,
                         defaultQueueListener,
                         defaultConnectivityListner,
                         ctx);
    }

    public void launchConnectivityDaemon() {
        checkingConnectivity = true;
        lastConnectivityStatus = true;
        mHandler = new Handler();
        new Thread(connectivityDaemon()).start();
    }

    public Runnable connectivityDaemon() {
        return () -> {
            while (checkingConnectivity) {
                try {
                    Thread.sleep(1000);
                    mHandler.post(() -> {
                        boolean status = isOnline();
                        if (status != lastConnectivityStatus){
                            mQueueConnectivityEvent.run(status);
                            lastConnectivityStatus = status;
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "launchConnectivityDaemon Error: " + e.toString(),e);
                }
            }
        };
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) this.ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void setupFirebase() {
        Firebase.setAndroidContext(this.ctx);
        mFirebase = new Firebase("https://burning-torch-3063.firebaseio.com/queue");
        setupFirebaseListeners((DataSnapshot dataSnapshot) -> {
            ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
            al.add((HashMap<String, String>) dataSnapshot.getValue());
            runListeners(al);
        });
    }

    public void setupFirebaseListeners(DataSnapshotLambda listener) {
        mFirebase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {listener.run(dataSnapshot);}
            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {listener.run(dataSnapshot);}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {listener.run(dataSnapshot);}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {listener.run(dataSnapshot);}
            @Override
            public void onCancelled(FirebaseError firebaseError) {Log.e(TAG, firebaseError.toString());}
        });
    }

    private void runListeners(Object firebaseData){
        if (firebaseData == null) {
            mQueue.clear();
            mQueueEventNoQueue.run(-1);
        } else {
            //noinspection unchecked
            mQueue = parseFirebaseData((ArrayList<HashMap<String, String>>) firebaseData);
            switch (indexOfUserByID(androidID)) {
                case 0:
                    mQueueEventNext.run(0);
                    break;
                case -1:
                    mQueueEventNotInQueue.run(-1);
                    break;
                default:
                    mQueueEventInQueue.run(indexOfUserByID(androidID));
                    break;
            }
        }
    }

    private static ArrayList<User> parseFirebaseData(ArrayList<HashMap<String,String>> dataSnapshot){
        ArrayList<User> resultQueue = new ArrayList<>();
        for (int i = dataSnapshot.size()-1; i >= 0; i--) {
            resultQueue.add(new User(dataSnapshot.get(i).get("id")));
        }
        return resultQueue;
    }

    public int getIndex(){
        return indexOfUserByID(androidID);
    }

    public int indexOfUserByID(String id){
        for (int i = 0; i < mQueue.size(); i++){
            if(mQueue.get(i) != null) {
                if (mQueue.get(i).getId().equals(id)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void add(User user){
        mQueue.add(user);
        Log.d(TAG, "add " + user.getId());
        mFirebase.setValue(mQueue);
    }

    public void add(){
        this.add(new User(this.androidID));
    }

    public User get(int i){
        if(!mQueue.isEmpty()){
            return mQueue.get(i);
        }
        return null;
    }

    public User pop(){
        if(!mQueue.isEmpty()) {
            User removedUser = mQueue.remove(0);
            mFirebase.setValue(mQueue);
            return removedUser;
        }
        return null;
    }

    public void clear(){
        mFirebase.setValue(null);
    }

    public void setQueueEventNoQueue(QueueEventListener queueEventNoQueue) {
        this.mQueueEventNoQueue = queueEventNoQueue;
    }

    public void setQueueEventInQueue(QueueEventListener queueEventInQueue) {
        this.mQueueEventInQueue = queueEventInQueue;
    }

    public void setQueueEventNext(QueueEventListener queueEventNext) {
        this.mQueueEventNext = queueEventNext;
    }

    public void setmQueueEventNotInQueue(QueueEventListener mQueueEventNotInQueue) {
        this.mQueueEventNotInQueue = mQueueEventNotInQueue;
    }

    public void setQueueConnectivityEvent(ConnectivityListener queueConnectivityEvent) {
        this.mQueueConnectivityEvent = queueConnectivityEvent;
    }

    public interface QueueEventListener {void run(int index);}
    public interface DataSnapshotLambda {void run(DataSnapshot dataSnapshot);}
    public interface ConnectivityListener {void run(boolean isConnected);}

}
