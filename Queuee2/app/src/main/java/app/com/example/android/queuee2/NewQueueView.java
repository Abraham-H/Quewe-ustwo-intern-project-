package app.com.example.android.queuee2;
import app.com.example.android.queuee2.model.Queue;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

public class NewQueueView extends Activity {

    private static String TAG = NewQueueView.class.getSimpleName();
    private ImageButton addToQueueImageButton;
    private TextView numInLineTextView;
    private Queue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_queue_view);

        populateView();
        createQueue();
    }

    private void createQueue(){
        queue = new Queue(this);
        queue.onLoadData(() -> {
            addToQueueImageButton.setEnabled(true);
            numInLineTextView.setText(
                    Integer.toString(queue.length()) + " people in line.");
        });
        queue.bindService();
    }

    private void populateView() {
        addToQueueImageButton = (ImageButton)findViewById(R.id.add_to_queue_image_button);
        numInLineTextView = (TextView)findViewById(R.id.num_in_line_textView);
        addToQueueImageButton.setEnabled(false);
        addToQueueImageButton.setOnClickListener((v) -> {
            queue.add();
            addToQueueImageButton.setImageResource(R.drawable.button_transformed_state);
            launchInQueueView();
            addToQueueImageButton.setImageResource(R.drawable.button_normal_state);
        });
    }

    private void launchInQueueView(){
        Intent intent = new Intent(this, InQueueView.class);
        startActivity(intent);
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
