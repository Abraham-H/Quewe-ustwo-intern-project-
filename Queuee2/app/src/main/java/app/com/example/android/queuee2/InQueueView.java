package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import app.com.example.android.queuee2.model.FirebaseService;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.User;

/**
 * Created by Abraham on 9/7/2015.
 */
public class InQueueView extends Activity {

    private final static String TAG = InQueueView.class.getSimpleName();

    private TextView positionInQueueTextView;
    private Button snoozeButton;
    private Queue queue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_queue_view);
        populateView();
    }

    @Override
    protected void onStart(){
        super.onStart();
    }

    private void populateView() {
        positionInQueueTextView = (TextView)findViewById(R.id.position_in_queue_text_view);
        snoozeButton = (Button)findViewById(R.id.snooze_button);
        snoozeButton.setEnabled(false);
        snoozeButton.setOnClickListener((v) -> queue.moveBack(2));
        loadQueue();
    }

    private void loadQueue(){
        queue = new Queue(this);
        queue.onLoadData(
                () -> {
                    snoozeButton.setEnabled(true);

                    // Initial set text
                    positionInQueueTextView.setText(Integer.toString(queue.getIndex()));
                    // If changed set text
                    queue.setQueueEventInQueue(index ->
                            positionInQueueTextView.setText(Integer.toString(queue.getIndex())));
                    queue.setQueueEventNext(index ->
                            positionInQueueTextView.setText("You're Next!")
                    );
                }
        );
        queue.bindService();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        queue.remove();
        queue.unbindService();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_queue_view, menu);
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

