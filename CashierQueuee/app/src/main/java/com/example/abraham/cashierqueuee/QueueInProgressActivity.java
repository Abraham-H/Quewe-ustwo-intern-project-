package com.example.abraham.cashierqueuee;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import dialog.CloseQueueConfirmationDialog;
import model.Queue;
import model.Response;
import utils.Utils;

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
        setQueue();
        setViews();
        changeListener();
        setChangeListener();
    }
    private void setQueue() {
        mQueue = new Queue();
        mQueue.setQueueId(getIntent().getStringExtra("queueId"));
    }

    private void setViews() {
        Utils.setupActionBar(this, getIntent().getStringExtra("queueId"),
                getActionBar(), this::launchCloseQueueConfirmationDialog);

        mCashierNumberTextView = (TextView) findViewById(R.id.queue_in_progress_textview_cashier_number);
        mNumOfPeopleInQueue = (TextView) findViewById(R.id.activity_queue_in_progress_textview_number_in_queue);

        mNextInQueueImageButton = (ImageButton) findViewById(R.id.activity_queue_in_progress_image_button_next);
        mNextInQueueImageButton.setOnClickListener(this::nextInQueueImageButtonTapped);

        mFinishQueueImageButton = (ImageButton) findViewById(R.id.finish_queue_image_button);
        mFinishQueueImageButton.setOnClickListener(this::finishQueueImageButtonTapped);

        mCashierNumberTextView.setText("People in " + mQueue.getQueueId());

    }

    private void launchCloseQueueConfirmationDialog() {
        new CloseQueueConfirmationDialog(this, this::onYesCloseQueue, this::onNoCloseQueue);
    }

    private void onYesCloseQueue() {
        closeQueue();
    }

    private void onNoCloseQueue() {
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
        if (resultString == "0") {
            mNumOfPeopleInQueue.setText("No");
            disableNextImageButton();
        } else {
            mNumOfPeopleInQueue.setText(resultString);
            enableActivityImageButtons();
        }
    }

    private void onGetQueueError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        if (error.getStatus() == 404) { // Queue not found
            mNextInQueueImageButton.setEnabled(false);
            mFinishQueueImageButton.setEnabled(true);
            mNumOfPeopleInQueue.setText("No");
        }
    }

    private void enableActivityImageButtons() {
        mNextInQueueImageButton.setEnabled(true);
        mFinishQueueImageButton.setEnabled(true);
    }

    private void disableNextImageButton() {
        mNextInQueueImageButton.setEnabled(false);
    }

    private void finishQueueImageButtonTapped(View view) {
        launchCloseQueueConfirmationDialog();
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
        Response.Error error = Response.getError(throwable);
        switch (error.getStatus()) {
            case 404: // Queue Not Found
                Toast.makeText(getApplicationContext(), "Queue did not exist!",
                        Toast.LENGTH_LONG).show();
                launchActivity(LoginActivity.class);
                break;
        }
    }

    private void nextInQueueImageButtonTapped(View view) {
        popUserFromQueue();
    }

    private void popUserFromQueue() {
        mQueue.popUserFromQueue(this::onUserPopped, this::onUserPoppedError);
    }

    private void onUserPopped(Response response) {
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
