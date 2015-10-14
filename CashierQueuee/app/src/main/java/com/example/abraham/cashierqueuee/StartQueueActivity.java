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

import model.Queue;
import model.Response;

public class StartQueueActivity extends Activity {

    private Queue mQueue;

    private static String TAG = StartQueueActivity.class.getSimpleName();

    private TextView mCashierNumberTextView;
    private ImageButton mStartQueueImageButton;
    private ImageButton mFinishQueueImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_queue);
        setViews();
        setQueue();
    }

    private void setViews(){
        mCashierNumberTextView = (TextView) findViewById(R.id.cashier_number);
        mStartQueueImageButton = (ImageButton) findViewById(R.id.start_queue_image_button);
        mStartQueueImageButton.setOnClickListener(this::startQueueButtonTapped);
    }

    private void setQueue(){
        mQueue = new Queue();
        mQueue.setQueueId(getIntent().getStringExtra("queueId"));

        Log.d("StartQueueActivity", mQueue.getQueueId());
        mCashierNumberTextView.setText(mQueue.getQueueId());
    }

    private void startQueueButtonTapped(View v){
        mQueue.startQueue(this::onQueueStartedSuccess, this::onQueueStartedFailure);
        launchActivity(QueueInProgressActivity.class);
    }

    private void onQueueStartedSuccess(Response response) {
        launchActivity(QueueInProgressActivity.class);
    }

    private void onQueueStartedFailure(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        if (error.getStatus() == 409){
            toastError(error.getMessage());
        }
    }

    private void launchActivity(Class toActivityClass) {
        Intent intent = new Intent(this, toActivityClass);
        intent.putExtra("queueId", mQueue.getQueueId());
        startActivity(intent);
    }

    private void toastError(String message) {
        Log.d(TAG, "Error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
