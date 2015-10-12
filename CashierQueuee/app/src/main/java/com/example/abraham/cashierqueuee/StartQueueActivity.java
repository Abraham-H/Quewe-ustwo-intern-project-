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

public class StartQueueActivity extends Activity {

    private Queue mQueue;

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

        mQueue = new Queue();
        mQueue.setQueueId(getIntent().getStringExtra("queueId"));

        if (getIntent().getStringExtra("queueId") == null){
            mQueue.setQueueId("queue1");
        }// TODO: 10/12/2015 Remove just for testing with no beacon. Also change launcher

        Log.d("StartQueueActivity", mQueue.getQueueId());
        mCashierNumberTextView.setText("People in " + mQueue.getQueueId());

        Log.d("StartQueueActivity", mQueue.getQueueId());
        mCashierNumberTextView.setText(mQueue.getQueueId());
    }

    private void startQueueButtonTapped(View v){
        launchActivity(QueueInProgressActivity.class);
    }

    private void launchActivity(Class toActivityClass) {
        Intent intent = new Intent(this, toActivityClass);
        intent.putExtra("queueId", mQueue.getQueueId());
        startActivity(intent);
    }


}
