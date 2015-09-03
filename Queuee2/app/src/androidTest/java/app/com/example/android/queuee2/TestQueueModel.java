package app.com.example.android.queuee2;

import android.test.AndroidTestCase;
import android.util.Log;

import app.com.example.android.queuee2.model.Queue;
import app.com.example.android.queuee2.model.User;

/**
 * Created by bkach on 9/2/15.
 */
public class TestQueueModel extends AndroidTestCase {

    public void testInQueueListener(){
        Queue queue = Queue.createQueue(mContext);

        try {
            queue.clear();
            Thread.sleep((long) 1000);
            queue.setQueueEventInQueue(new Queue.QueueEventListener() {
                @Override
                public void run(int index) {
                    if (index > -1) {
                        assertEquals(3, index);
                    }
                }
            });
            queue.add(new User("Three"));
            queue.add(new User("Two"));
            queue.add(new User("One"));
            queue.add(new User(Queue.androidID));
            Thread.sleep((long) 3000);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            queue.clear();
        }
    }

    public void testNoQueueListener(){
        Queue queue = Queue.createQueue(mContext);
        try {
            queue.clear();
            Thread.sleep((long) 1000);
            queue.setQueueEventNoQueue(new Queue.QueueEventListener() {
                @Override
                public void run(int index) {
                    assertEquals(-1, index);
                }
            });
            Thread.sleep((long) 3000);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            queue.clear();
        }
    }

    public void testNotInQueueListener(){
        Queue queue = Queue.createQueue(mContext);
        try {
            queue.clear();
            queue.add(new User("one"));
            queue.add(new User("two"));
            queue.add(new User("three"));
            Thread.sleep((long) 1000);
            queue.setQueueEventNotInQueue(new Queue.QueueEventListener() {
                @Override
                public void run(int index) {
                    assertEquals(-1, index);
                }
            });
            Thread.sleep((long) 3000);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            queue.clear();
        }
    }

}
