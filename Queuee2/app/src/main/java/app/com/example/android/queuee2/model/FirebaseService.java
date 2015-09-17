package app.com.example.android.queuee2.model;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by bkach on 9/10/15.
 */
//public class FirebaseService extends Service {
//
//
//}

//    private final String TAG = FirebaseService.class.getSimpleName();
//    private final IBinder mBinder = new FirebaseServiceBinder();
//
//    private Firebase mFirebase;
//    private ArrayList<User> mQueue = new ArrayList<>();
//    private Queue.QueueEventListener mQueueEventNoQueue;
//    private Queue.QueueEventListener mQueueEventNext;
//    private Queue.QueueEventListener mQueueEventInQueue;
//    private Queue.QueueEventListener mQueueEventNotInQueue;
//
//    public boolean dataLoaded = false;
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        Log.d(TAG, "FirebaseService Bound");
//        setupFirebase();
//        return mBinder;
//    }
//
//    public class FirebaseServiceBinder extends Binder {
//        FirebaseService getService() {
//            return FirebaseService.this;
//        }
//    }
//
//    private void setupFirebase(){
//        Firebase.setAndroidContext(this);
//        mFirebase = new Firebase("https://burning-torch-3063.firebaseio.com/queue");
//        setupFirebaseListeners((DataSnapshot dataSnapshot) -> {
//            if (!dataLoaded) {
//                dataLoaded = true;
//                Toast.makeText(this, "Data Loaded!", Toast.LENGTH_SHORT).show();
//            }
//            runListeners(dataSnapshot.getValue());
//        });
//    }
//
//    public void setupFirebaseListeners(DataSnapshotLambda listener) {
//        instantiateDefaultListeners();
//        mFirebase.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                listener.run(dataSnapshot);
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//                Log.e(TAG, firebaseError.toString());
//            }
//        });
//    }
//
//    public void instantiateDefaultListeners(){
//        Queue.QueueEventListener defaultQueueListener = (i) -> {
//            Log.v(TAG, "Empty Listener, Index: " + Integer.toString(i));
//        };
//        setQueueEventInQueue(defaultQueueListener);
//        setQueueEventNext(defaultQueueListener);
//        setQueueEventNoQueue(defaultQueueListener);
//        setQueueEventNotInQueue(defaultQueueListener);
//    }
//
//    private void runListeners(Object firebaseData){
//        if (firebaseData == null) {
//            mQueue.clear();
//            mQueueEventNoQueue.run(-1);
//        } else {
//            mQueue = parseFirebaseData((ArrayList) firebaseData);
//            switch (indexOfUserByID(Queue.androidID)) {
//                case 0:
//                    mQueueEventNext.run(0);
//                    break;
//                case -1:
//                    mQueueEventNotInQueue.run(-1);
//                    break;
//                default:
//                    mQueueEventInQueue.run(indexOfUserByID(Queue.androidID));
//                    break;
//            }
//        }
//    }
//
//    private static ArrayList<User> parseFirebaseData(ArrayList firebaseData){
//        ArrayList resultQueue = new ArrayList<>();
//        Iterator it = firebaseData.iterator();
//        while (it.hasNext()){
//            HashMap pair = (HashMap) it.next();
//            resultQueue.add(new User((String) pair.get("id")));
//        }
//        return resultQueue;
//    }
//
//    private int indexOfUserByID(String id){
//        for (int i = 0; i < mQueue.size(); i++){
//            User user = mQueue.get(i);
//            String userId = user.getId();
//            if (userId.equals(id)) {
//                return i;
//            }
//        }
//        return -1;
//    }
//
//    public int getIndex(){
//        return indexOfUserByID(Queue.androidID);
//    }
//
//    public void moveBack(User user, int numPos){
//        int curIndex = indexOfUserByID(user.getId());
//        if(mQueue.size() > curIndex + numPos) {
//            Collections.swap(mQueue, curIndex, curIndex + numPos);
//        } else {
//            Toast.makeText(this, "skip : Not skipped. Queue size is " + Integer.toString(mQueue.size())
//                    + " and user is index " + curIndex, Toast.LENGTH_SHORT).show();
//        }
//        mFirebase.setValue(mQueue);
//    }
//
//    public void add(User user){
//        if(indexOfUserByID(user.getId()) == -1) {
//            mQueue.add(user);
//            mFirebase.setValue(mQueue);
//        } else {
//            Toast.makeText(this, "add : Cannot add single user multiple times", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public void remove(User user){
//        int index = indexOfUserByID(user.getId());
//        if(index != -1) {
//            mQueue.remove(indexOfUserByID(user.getId()));
//            mFirebase.setValue(mQueue);
//        } else {
//            Toast.makeText(FirebaseService.this, "pop: Not in queue?", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    public int queueLength(){
//        return mQueue.size();
//    }
//
//
//    public void setQueueEventNoQueue(Queue.QueueEventListener queueEventNoQueue) {
//        this.mQueueEventNoQueue = queueEventNoQueue;
//    }
//
//    public void setQueueEventInQueue(Queue.QueueEventListener queueEventInQueue) {
//        this.mQueueEventInQueue = queueEventInQueue;
//    }
//
//    public void setQueueEventNext(Queue.QueueEventListener queueEventNext) {
//        this.mQueueEventNext = queueEventNext;
//    }
//
//    public void setQueueEventNotInQueue(Queue.QueueEventListener QueueEventNotInQueue) {
//        this.mQueueEventNotInQueue = QueueEventNotInQueue;
//    }
//
//    public interface DataSnapshotLambda {void run(DataSnapshot dataSnapshot);}
