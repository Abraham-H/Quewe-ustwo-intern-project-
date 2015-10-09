package com.example.abraham.cashierqueuee;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abraham.cashierqueuee.R;

import model.Queue;
import model.Response;

public class QueueInProgressActivity extends Activity {

    private Queue mQueue;
    private ImageButton mNextInQueueImageButton;
    private ImageButton mFinishQueueImageButton;
    private TextView mCashierNumberTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_in_progress);
        setViews();
        setQueue();
    }

    private void setViews() {
        mCashierNumberTextView = (TextView) findViewById(R.id.queue_in_progress_textview_cashier_number);

        mNextInQueueImageButton = (ImageButton) findViewById(R.id.next_image_button);
        mNextInQueueImageButton.setOnClickListener(this::nextInQueueImageButtonTapped);

        mFinishQueueImageButton = (ImageButton) findViewById(R.id.finish_queue_image_button);
        mFinishQueueImageButton.setOnClickListener(this::finishQueueImageButtonTapped);
    }

    private void setQueue() {
        mQueue = new Queue();
        mQueue.setQueueId(getIntent().getStringExtra("queueId"));
        Log.d("StartQueueActivity", mQueue.getQueueId());
        mCashierNumberTextView.setText(mQueue.getQueueId());
    }

    private void finishQueueImageButtonTapped(View view) {
        closeQueue();
    }

    private void closeQueue() {
        mQueue.closeQueue(this::onQueueClosed, this::onQueueClosedError);
    }

    private void onQueueClosed(Response response) {
        Toast.makeText(getApplicationContext(), "Queue Closed!",
                Toast.LENGTH_LONG).show();
    }

    private void onQueueClosedError(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "Queue not closed...ERRRRROR!",
                Toast.LENGTH_LONG).show();
    }

    private void nextInQueueImageButtonTapped(View view) {
        popUserFromQueue();
    }

    private void popUserFromQueue() {
        mQueue.popUserFromQueue(this::onUserPopped, this::onUserPoppedError);
    }

    private void onUserPopped(Response response) {
        Toast.makeText(getApplicationContext(), "User popped!",
                Toast.LENGTH_LONG).show();
    }

    private void onUserPoppedError(Throwable throwable) {
        Toast.makeText(getApplicationContext(), "User NOT popped...ERRRRROR!",
                Toast.LENGTH_LONG).show();
    }
}
