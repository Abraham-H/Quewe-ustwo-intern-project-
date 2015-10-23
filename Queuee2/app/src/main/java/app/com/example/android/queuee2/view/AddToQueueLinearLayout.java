package app.com.example.android.queuee2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import app.com.example.android.queuee2.R;
import app.com.example.android.queuee2.utils.Utils;

/**
 * Created by bkach on 10/16/15.
 */
public class AddToQueueLinearLayout extends BaseLinearLayout {

    public AddToQueueLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private void startQueueOpenButtonTransition() {
        buttonTransition(R.drawable.animation_button);
    }

    private void startQueueClosedButtonTransition() {
        buttonTransition(R.drawable.animation_button_closed);
    }

    private void buttonTransition(int resourceId) {
        replaceAnimationDrawable(resourceId);
        Utils.afterDelayRun(5000, () -> {
            mCenterImageButton.setVisibility(View.VISIBLE);
            mAnimationView.setVisibility(View.INVISIBLE);
        });
    }

    private String numInQueueString(int size) {
        if (size > 0) {
            return String.valueOf(size) + (size == 1 ? " person" : " people") + " in queue";
        } else {
            return getResources().getString(R.string.add_to_queue_subheader_empty);
        }
    }

    public void update(ArrayList<String> data) {
        setHeaderImageView();

        if (data != null) {
            if (!mCenterImageButton.isEnabled()) {
                mCenterImageButton.setEnabled(true);
                mAnimationView.setVisibility(View.VISIBLE);
                mCenterImageButton.setVisibility(View.INVISIBLE);
                startQueueOpenButtonTransition();
            }
            mSubheaderTextView.setText(numInQueueString(data.size()));
            mFooterTextView.setText(R.string.add_to_queue_subheader_instructions);
        } else {
            if (mAnimationView.getVisibility() == View.VISIBLE || mCenterImageButton.isEnabled()) {
                startQueueClosedButtonTransition();
                mAnimationView.setVisibility(View.VISIBLE);
                mCenterImageButton.setVisibility(View.INVISIBLE);
            } else {
                mCenterImageButton.setVisibility(View.VISIBLE);
                mAnimationView.setVisibility(View.INVISIBLE);
            }
            mCenterImageButton.setEnabled(false);
            mSubheaderTextView.setText(R.string.add_to_queue_subheader_closed);
            mFooterTextView.setText(R.string.add_to_queue_footer_closed);
        }
    }


    public void setAddToQueueButtonListener(OnClickListener onClickListener) {
        mCenterImageButton.setOnClickListener(onClickListener);
    }
}
