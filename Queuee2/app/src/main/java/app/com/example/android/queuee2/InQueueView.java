package app.com.example.android.queuee2;

import android.app.Activity;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.User;

/**
 * Created by Abraham on 9/7/2015.
 */
public class InQueueView extends Activity {

    private TextView positionInQueueTextView;
    private TextView timeEstimationTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_in_queue_view);
        populateView();

        super.onCreate(savedInstanceState);
    }

    private void populateView() {
        positionInQueueTextView = (TextView)findViewById(R.id.position_in_queue_text_view);
        timeEstimationTextView = (TextView)findViewById(R.id.time_estimation_text_view);
    }

    @Override
    protected void onStart(){
        super.onStart();
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

