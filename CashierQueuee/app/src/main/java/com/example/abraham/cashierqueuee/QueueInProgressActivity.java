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
import views.QueueInProgressActivityLinearLayout;
import views.StartQueueActivityLinearLayout;

public class QueueInProgressActivity extends Activity {

    private Queue mQueue;
    private QueueInProgressActivityLinearLayout mView;

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
                getActionBar());

        mView = (QueueInProgressActivityLinearLayout) findViewById(R.id.queue_in_progress_linear_layout);
        mView.setNextQueueButtonOnClickListener(this::nextInQueueImageButtonTapped);
        mView.setCloseQueueButtonOnClickListener(this::finishQueueImageButtonTapped);
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
        mView.update(queueData);
    }

    private void onGetQueueError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        if (error.getStatus() == 404) { // Queue not found
        }
    }

    private void finishQueueImageButtonTapped() {
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

    private void nextInQueueImageButtonTapped() {
        popUserFromQueue();
    }

    private void popUserFromQueue() {
        mQueue.popUserFromQueue(mView::onUserPopped, mView::onUserPoppedError);
    }

    private void launchActivity(Class toActivityClass) {
        Intent intent = new Intent(this, toActivityClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
