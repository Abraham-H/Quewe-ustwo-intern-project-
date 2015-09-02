package app.com.example.android.queuee2;

import android.test.AndroidTestCase;

import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.User;

/**
 * Created by bkach on 9/2/15.
 */
public class TestQueueModel extends AndroidTestCase {

    public void testAddToQueue() {
        Queue queue = Queue.createQueue(mContext);

        try {
            for (int i = 0; i < 100; i++) {
                queue.add(new User(Queue.androidID));
            }

            queue.add(new User("first"));

            Thread.sleep((long) 3000);

            assertEquals("first", queue.get(0).getId());
        } catch(Exception e){

        }
    }
}
