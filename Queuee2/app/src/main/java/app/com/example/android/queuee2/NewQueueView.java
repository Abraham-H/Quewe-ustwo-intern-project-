package app.com.example.android.queuee2;
import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.User;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;


public class NewQueueView extends Activity {

    private static String TAG = NewQueueView.class.getSimpleName();
    private ImageButton addToQueueImageButton;
    private Queue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_queue_view);

        queue = Queue.createQueue(this);
        queue.indexOfUserByID(Queue.androidID);
        populateView();
    }

    private void populateView() {
        addToQueueImageButton = (ImageButton)findViewById(R.id.add_to_queue_image_button);
        addToQueueImageButton.setOnClickListener((v) -> {
            changeSrcOfImageButton();
            queue.add();
        });
    }

    private void changeSrcOfImageButton() {
        addToQueueImageButton.setImageResource(R.drawable.button_transformed_state);
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
