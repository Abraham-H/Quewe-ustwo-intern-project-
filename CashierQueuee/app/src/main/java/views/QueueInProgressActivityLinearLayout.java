package views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.example.abraham.cashierqueuee.R;
import java.util.ArrayList;
import model.Response;

/**
 * Created by Abraham on 10/21/2015.
 */
public class QueueInProgressActivityLinearLayout extends BaseCustomLinearLayout {

    private static final String TAG = QueueInProgressActivityLinearLayout.class.getSimpleName();

    public QueueInProgressActivityLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateQueueInProgressActivityLayoutViews();
    }

    public void update(ArrayList<String> queueData) {
        setTextViewsVisibility();
        mHeaderTextView.setText(String.valueOf(queueData.size()));

        if (queueData.size() == 1) {
            mSubheaderTextView.setText("Person in queue");

        }

        else mSubheaderTextView.setText("People in queue");
        enableCentreImageButton();
    }

    private void setTextViewsVisibility() {
        if (mHeaderTextView.getVisibility() == INVISIBLE) {
            mHeaderTextView.setVisibility(VISIBLE);
        }
    }

    private void updateQueueInProgressActivityLayoutViews() {
        setImageButtonResources();
    }

    public void onUserPopped(Response response) {
        enableCentreImageButton();
    }

    private void enableCentreImageButton() {
        mCenterImageButton.setEnabled(true);
    }

    public void disableCentreImageButton() {
        mCenterImageButton.setEnabled(false);
    }

    public void onUserPoppedError(Throwable throwable) {
        Response.Error error = Response.getError(throwable);
        String errorMsg = error.toString();
        Toast.makeText(getContext(), errorMsg,
                Toast.LENGTH_LONG).show();

        Log.d(TAG, "EstimoteBeacon error:" + throwable.getMessage());
    }

    public void disableFooterImageButton() {
        mFooterImageButton.setEnabled(false);
    }

    private void setImageButtonResources() {
        mCenterImageButton.setImageResource(R.drawable.next_button_selector);
        mFooterImageButton.setImageResource(R.drawable.stop_button_selector);
    }

    public void setNextQueueButtonOnClickListener(Runnable activityCallback) {
        mCenterImageButton.setOnClickListener(v -> {
            disableCentreImageButton();
            activityCallback.run();
        });
    }

    public void setCloseQueueButtonOnClickListener(Runnable activityCallback) {
        mFooterImageButton.setOnClickListener(v -> {
            activityCallback.run();
        });
    }
}
