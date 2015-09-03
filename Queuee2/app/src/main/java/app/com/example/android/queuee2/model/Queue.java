package app.com.example.android.queuee2.model;

import android.content.Context;
import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by bkach on 9/2/15.
 */
public class Queue {

    public static String androidID;

    private Context ctx;
    private static String TAG = Queue.class.getSimpleName();
    private ArrayList<User> queue = new ArrayList<>();
    private QueueEventListener queueEventNoQueue;
    private QueueEventListener queueEventNext;
    private QueueEventListener queueEventInQueue;
    private QueueEventListener queueEventNotInQueue;
    private Firebase firebaseRef;


    public Queue(QueueEventListener queueEventNoQueue,
                 QueueEventListener queueEventNext,
                 QueueEventListener queueEventInQueue,
                 QueueEventListener queueEventNotInQueue,
                 Context ctx) {
        setQueueEventNoQueue(queueEventNoQueue);
        setQueueEventNext(queueEventNext);
        setQueueEventInQueue(queueEventInQueue);
        setQueueEventNotInQueue(queueEventNotInQueue);
        this.ctx = ctx;
        androidID = android.provider.Settings.Secure.getString(this.ctx.getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        setupFirebase();
    }

    public static Queue createQueue(Context ctx){
        QueueEventListener defaultListener = (new QueueEventListener() {
            @Override
            public void run(int index) {
                Log.v(TAG, "Empty Listener, Index: " + Integer.toString(index));
            }
        });
        return new Queue(defaultListener,
                         defaultListener,
                         defaultListener,
                         defaultListener,
                         ctx);
    }

    public void setupFirebase() {
        Firebase.setAndroidContext(this.ctx);
        firebaseRef = new Firebase("https://burning-torch-3063.firebaseio.com/queue");
        firebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                runListeners(dataSnapshot.getValue());
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, firebaseError.toString());
            }
        });
    }

    private void runListeners(Object firebaseData){
        if (firebaseData == null) {
            queue.clear();
            queueEventNoQueue.run(-1);
        } else {
            queue = parseFirebaseData((ArrayList<HashMap<String, String>>) firebaseData);
            switch (indexOfUserByID(androidID)) {
                case 0:
                    queueEventNext.run(0);
                    break;
                case -1:
                    queueEventNotInQueue.run(-1);
                    break;
                default:
                    queueEventInQueue.run(indexOfUserByID(androidID));
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

    public int indexOfUserByID(String id){
        for (int i = 0; i < queue.size(); i++){
            if(queue.get(i) != null) {
                if (queue.get(i).getId().equals(id)) {
                    return i;
                }
            }
        }
        return -1;
    }

    public void add(User user){
        queue.add(user);
        firebaseRef.setValue(queue);
    }

    public User get(int i){
        if(!queue.isEmpty()){
            return queue.get(i);
        }
        return null;
    }

    public User pop(){
        if(!queue.isEmpty()) {
            User removedUser = queue.remove(0);
            firebaseRef.setValue(queue);
            return removedUser;
        }
        return null;
    }

    public void clear(){
        firebaseRef.setValue(null);
    }

    public void setQueueEventNoQueue(QueueEventListener queueEventNoQueue) {
        this.queueEventNoQueue = queueEventNoQueue;
    }

    public void setQueueEventInQueue(QueueEventListener queueEventInQueue) {
        this.queueEventInQueue = queueEventInQueue;
    }

    public void setQueueEventNext(QueueEventListener queueEventNext) {
        this.queueEventNext = queueEventNext;
    }

    public void setQueueEventNotInQueue(QueueEventListener queueEventNotInQueue) {
        this.queueEventNotInQueue = queueEventNotInQueue;
    }

    public interface QueueEventListener {
        void run(int index);
    }

}
