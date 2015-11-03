package app.com.example.android.queuee2.model;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import app.com.example.android.queuee2.MyApplication;

/**
 * Created by bkach on 9/17/15.
 */
public class FirebaseListener {

    private final static String TAG = FirebaseListener.class.getSimpleName();
    private Firebase fRef;
    private Runnable onChange;
    private ValueEventListener eventListener;

    public FirebaseListener(Runnable onChange) {
        Firebase.setAndroidContext(MyApplication.getAppContext());
        fRef = new Firebase("https://burning-torch-3063.firebaseio.com/");
        setOnChange(onChange);
    }

    public void connectListener() {
        if (eventListener == null) {
            eventListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (eventListener != null)
                        onChange.run();
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {
                    Log.d(TAG, "onCancelled() called with: " + "firebaseError = [" + firebaseError + "]");
                }
            };
            fRef.addValueEventListener(eventListener);
        }
    }

    public void disconnectListener() {
        if (eventListener != null) {
            fRef.removeEventListener(eventListener);
            eventListener = null;
        }
    }

    private void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

}
