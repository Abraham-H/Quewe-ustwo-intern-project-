package app.com.example.android.queuee2.model;

/**
 * Created by bkach on 9/2/15.
 */
public class QueueInfo {

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    private int position;
    private int size;

}

//public static String androidID;
//    public boolean loadedAndBound = false;
//
//    private Context ctx;
//    private static String TAG = Queue.class.getSimpleName();
//    private Runnable loadedAndBoundListener;
//    private Handler mHandler;
//
//    FirebaseService mService;
//
//    public Queue(Context ctx) {
//        this.ctx = ctx;
//        androidID = android.provider.Settings.Secure.getString(this.ctx.getContentResolver(),
//                android.provider.Settings.Secure.ANDROID_ID);
//        mHandler = new Handler();
//    }
//
//    public void bindService(){
//        Intent intent = new Intent(this.ctx, FirebaseService.class);
//        intent.setAction(Integer.toString(android.os.Process.myPid()));
//        this.ctx.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
//    }
//
//    public void unbindService(){
//        this.ctx.unbindService(mConnection);
//    }
//
//    private void checkDataLoaded(){
//        new Thread( () -> {
//            while (!loadedAndBound) {
//                try {
//                    if(mService.dataLoaded){
//                        loadedAndBound = true;
//                        mHandler.post(() -> {
//                            loadedAndBoundListener.run();
//                        });
//                    }
//                    Thread.sleep(100);
//                } catch (Exception e) {
//                    Log.e(TAG, "CheckDataLoaded Error: " + e.toString(),e);
//                }
//            }
//        } ).start();
//    }
//
//    private ServiceConnection mConnection = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName className,
//                                       IBinder service) {
//            FirebaseService.FirebaseServiceBinder binder = (FirebaseService.FirebaseServiceBinder) service;
//            mService = binder.getService();
//            checkDataLoaded();
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//        }
//    };
//
//    public int getIndex(){
//        return mService.getIndex();
//    }
//
//    public void add(){
//        mService.add(new User(androidID));
//    }
//
//    public void remove(){
//        mService.remove(new User(androidID));
//    }
//
//    public int length(){
//        return mService.queueLength();
//    }
//
//    public void moveBack(int pos){
//        mService.moveBack(new User(androidID), pos);
//    }
//
//
//    public void setQueueEventNoQueue(QueueEventListener queueEventNoQueue) {
//        mService.setQueueEventNoQueue(queueEventNoQueue);
//    }
//
//    public void setQueueEventInQueue(QueueEventListener queueEventInQueue) {
//        mService.setQueueEventInQueue(queueEventInQueue);
//    }
//
//    public void setQueueEventNext(QueueEventListener queueEventNext) {
//        mService.setQueueEventNext(queueEventNext);
//    }
//
//    public void setQueueEventNotInQueue(QueueEventListener mQueueEventNotInQueue) {
//        mService.setQueueEventNotInQueue(mQueueEventNotInQueue);
//    }
//
//    public void onLoadData(Runnable loadedAndBoundListener) {
//        this.loadedAndBoundListener = loadedAndBoundListener;
//    }
//
//    public interface QueueEventListener {void run(int index);}


//    public static Queue createQueue(Context ctx){
//        QueueEventListener defaultQueueListener = (i) -> {
//            Log.v(TAG, "Empty Listener, Index: " + Integer.toString(i));
//        };
//        ConnectivityListener defaultConnectivityListner = (status) -> {
//            Log.v(TAG, "Empty Listener, Connected: " + Boolean.toString(status));
//        };
//        return new Queue(defaultQueueListener,
//                         defaultQueueListener,
//                         defaultQueueListener,
//                         defaultQueueListener,
//                         defaultConnectivityListner,
//                         ctx);
//    }

//    public void launchConnectivityDaemon() {
//        checkingConnectivity = true;
//        lastConnectivityStatus = true;
//        mHandler = new Handler();
//        new Thread(connectivityDaemon()).start();
//    }
//
//    public Runnable connectivityDaemon() {
//        return () -> {
//            while (checkingConnectivity) {
//                try {
//                    Thread.sleep(1000);
//                    mHandler.post(() -> {
//                        boolean status = isOnline();
//                        if (status != lastConnectivityStatus){
//                            mQueueConnectivityEvent.run(status);
//                            lastConnectivityStatus = status;
//                        }
//                    });
//                } catch (Exception e) {
//                    Log.e(TAG, "launchConnectivityDaemon Error: " + e.toString(),e);
//                }
//            }
//        };
//    }
//
//    public boolean isOnline() {
//        ConnectivityManager cm =
//                (ConnectivityManager) this.ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo netInfo = cm.getActiveNetworkInfo();
//        return netInfo != null && netInfo.isConnectedOrConnecting();
//    }
//
//    public void setupFirebase() {
//        Firebase.setAndroidContext(this.ctx);
//        mFirebase = new Firebase("https://burning-torch-3063.firebaseio.com/queue");
//        setupFirebaseListeners((DataSnapshot dataSnapshot) -> {
//            ArrayList<HashMap<String, String>> al = new ArrayList<HashMap<String, String>>();
//            al.add((HashMap<String, String>) dataSnapshot.getValue());
//            runListeners(al);
//        });
//    }
//
//    public void setupFirebaseListeners(DataSnapshotLambda listener) {
//        mFirebase.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot dataSnapshot, String s) {listener.run(dataSnapshot);}
//            @Override
//            public void onChildChanged(DataSnapshot dataSnapshot, String s) {listener.run(dataSnapshot);}
//            @Override
//            public void onChildRemoved(DataSnapshot dataSnapshot) {listener.run(dataSnapshot);}
//            @Override
//            public void onChildMoved(DataSnapshot dataSnapshot, String s) {listener.run(dataSnapshot);}
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {Log.e(TAG, firebaseError.toString());}
//        });
//    }
//
//    private void runListeners(Object firebaseData){
//        if (firebaseData == null) {
//            mQueue.clear();
//            mQueueEventNoQueue.run(-1);
//        } else {
//            //noinspection unchecked
//            mQueue = parseFirebaseData((ArrayList<HashMap<String, String>>) firebaseData);
//            switch (indexOfUserByID(androidID)) {
//                case 0:
//                    mQueueEventNext.run(0);
//                    break;
//                case -1:
//                    mQueueEventNotInQueue.run(-1);
//                    break;
//                default:
//                    mQueueEventInQueue.run(indexOfUserByID(androidID));
//                    break;
//            }
//        }
//    }
//
//    private static ArrayList<User> parseFirebaseData(ArrayList<HashMap<String,String>> dataSnapshot){
//        ArrayList<User> resultQueue = new ArrayList<>();
//        for (int i = dataSnapshot.size(); i > 0; i--) {
//            resultQueue.add(new User(dataSnapshot.get(i-1).get("id")));
//        }
//        return resultQueue;
//    }
//
//    public int getIndex(){
//        return indexOfUserByID(androidID);
//    }
//
//    public int indexOfUserByID(String id){
//        int returnValue = -1;
//
//        for (int i = 0; i < mQueue.size(); i++){
//            User user = mQueue.get(i);
//            String userId = user.getId();
//            if (userId.equals(id)) {
//                returnValue = i;
//            }
//        }
//        return returnValue;
//    }

//    public void add(User user){
//        mQueue.add(user);
//        Log.d(TAG, "add " + user.getId());
//        mFirebase.setValue(mQueue);
//    }
//
//    public void add(){
//        this.add(new User(this.androidID));
//    }
//
//    public User get(int i){
//        if(!mQueue.isEmpty()){
//            return mQueue.get(i);
//        }
//        return null;
//    }
//
//    public User pop(){
//        if(!mQueue.isEmpty()) {
//            User removedUser = mQueue.remove(0);
//            mFirebase.setValue(mQueue);
//            return removedUser;
//        }
//        return null;
//    }

//    public void clear(){
//        mFirebase.setValue(null);
//    }
