package app.com.example.android.queuee2;

import android.app.Activity;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.Queue.QueueEventListener;
import app.com.example.android.queuee2.model.User;

public class NewQueueView extends Activity {

    private static String TAG = NewQueueView.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_queue_view);

        // Some quick examples of how to use the queue system

//        Queue queue = Queue.createQueue();
//        User user = new User(Queue.androidID);
//        queue.add(user);
//        queue.add(new User("whatever_user_id"));
//        queue.get(0);
//        queue.pop();

        //  Here's how to set up a listener

//        queue.setQueueEventInQueue(new QueueEventListener() {
//            @Override
//            public void run() {
//              // Code in here!!
//            }
//        });

//        There are four types of listeners:

//          setQueueEventNoQueue
//          setQueueEventInQueue
//          setQueueEventNext
//          setQueueEventNotInQueue

//        Have fun!
//        -b
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
