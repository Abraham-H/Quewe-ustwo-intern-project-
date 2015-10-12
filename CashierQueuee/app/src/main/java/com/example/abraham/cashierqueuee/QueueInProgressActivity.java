package com.example.abraham.cashierqueuee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.abraham.cashierqueuee.R;

import java.util.ArrayList;

import model.Queue;
import model.Response;

public class QueueInProgressActivity extends Activity {

    private Queue mQueue;
    private ImageButton mNextInQueueImageButton;
    private ImageButton mFinishQueueImageButton;
    private TextView mCashierNumberTextView;
    private TextView mNumOfPeopleInQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_queue_in_progress);
        setViews();
        setQueue();
        setChangeListener();
    }

    private void setViews() {
        mCashierNumberTextView = (TextView) findViewById(R.id.queue_in_progress_textview_cashier_number);
        mNumOfPeopleInQueue = (TextView) findViewById(R.id.activity_queue_in_progress_textview_number_in_queue);

        mNextInQueueImageButton = (ImageButton) findViewById(R.id.activity_queue_in_progress_image_button_next);
        mNextInQueueImageButton.setOnClickListener(this::nextInQueueImageButtonTapped);

        mFinishQueueImageButton = (ImageButton) findViewById(R.id.finish_queue_image_button);
        mFinishQueueImageButton.setOnClickListener(this::finishQueueImageButtonTapped);
    }

    private void setQueue() {
        mQueue = new Queue();
        mQueue.setQueueId(getIntent().getStringExtra("queueId"));

        if (getIntent().getStringExtra("queueId") == null){
            mQueue.setQueueId("queue1");
        }// TODO: 10/12/2015 Remove just for testing with no beacon. Also change launcher 

        Log.d("StartQueueActivity", mQueue.getQueueId());
        mCashierNumberTextView.setText("People in " + mQueue.getQueueId());
    }

    private void setChangeListener() {
        mQueue.setChangeListener(mQueue.getQueueId(), this::changeListener);
    }

    private void changeListener() {
        mQueue.getQueue(this::onGetQueue, this::onGetQueueError);
    }

    private void onGetQueue(Response response) {
        ArrayList<String> queueData = (ArrayList<String>) response.getData();
        String resultString = String.valueOf(queueData.size());
        if (resultString == null) {
            mNumOfPeopleInQueue.setText("0");
        } else {
            mNumOfPeopleInQueue.setText(resultString);
            mNextInQueueImageButton.setEnabled(true);
            mFinishQueueImageButton.setEnabled(true);
        }
    }

    private void onGetQueueError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        if (error.getStatus() == 404) { // Queue not found
            mNextInQueueImageButton.setEnabled(false);
            mFinishQueueImageButton.setEnabled(false);
            mNumOfPeopleInQueue.setText("No");
        }
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
        launchActivity(LoginActivity.class);
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

    private void launchActivity(Class toActivityClass) {
        Intent intent = new Intent(this, toActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
