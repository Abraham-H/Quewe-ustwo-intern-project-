package app.com.example.android.queuee2.model;

import android.content.Context;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by bkach on 9/17/15.
 */
public class FirebaseListener {

    Firebase fRef;
    Runnable onChange;
    ValueEventListener eventListener;

    public FirebaseListener(Context ctx, Runnable onChange) {
        Firebase.setAndroidContext(ctx);
        fRef = new Firebase("https://burning-torch-3063.firebaseio.com/");
        setOnChange(onChange);
        setupListener();
    }

    private void setupListener() {
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onChange.run();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        fRef.addValueEventListener(eventListener);
    }

    public void disconnectListener() {
        fRef.removeEventListener(eventListener);
    }

    private void setOnChange(Runnable onChange) {
        this.onChange = onChange;
    }

}
