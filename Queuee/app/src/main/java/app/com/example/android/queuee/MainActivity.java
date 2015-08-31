package app.com.example.android.queuee;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private static String androidID = "";
    private ArrayList<String> queue;
    private Firebase mRef;
    private TextView mTextViewShowNext;
    private Button mQueueStartButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup Firebase
        setupFirebase();

        // Get unique ID for Google Accounts
        androidID = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        // Setup Start Button
        mQueueStartButton = (Button) findViewById(R.id.queueStartButton);
        mQueueStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create queue and populate
                if(queue == null) {
                    queue = new ArrayList<String>();
                }
                queue.add(androidID);
                mRef.setValue(queue);
                mQueueStartButton.setEnabled(false);
            }
        });

        // Setup Next Button
        Button mQueueNextButton = (Button) findViewById(R.id.queueNextButton);
        mQueueNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove Queue Entry
                if(queue != null) {
                    queue.remove(0);
                    mRef.setValue(queue);
                }
            }
        });

    }

    @Override
    public void onStart() {

        super.onStart();
    }

    public void setupFirebase(){

        Firebase.setAndroidContext(this);
        mRef = new Firebase("https://burning-torch-3063.firebaseio.com/queue");
        mRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean isNext = false;

                if(dataSnapshot.getValue() == null){
                    queue = null;
                    isNext = true;
                }
                else {
                    queue = (ArrayList<String>) dataSnapshot.getValue();

                    int index = queue.indexOf(androidID);
                    Log.d(TAG,Integer.toString(index));
                    if (index == 0) {
                        isNext = true;
                        mQueueStartButton.setEnabled(true);
                    } else if (index == -1) {
                        mQueueStartButton.setEnabled(true);
                    } else {
                        mQueueStartButton.setEnabled(false);2
                    }
                }

                TextView mTextViewShowNext = (TextView) findViewById(R.id.textViewShowNext);
                if(isNext){
                    mTextViewShowNext.setText("next");
                }
                else{
                    mTextViewShowNext.setText("wait");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.e(TAG, firebaseError.toString());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
