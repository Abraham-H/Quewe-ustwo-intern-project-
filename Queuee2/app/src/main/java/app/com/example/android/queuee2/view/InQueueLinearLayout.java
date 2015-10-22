package app.com.example.android.queuee2.view;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import app.com.example.android.queuee2.R;
import app.com.example.android.queuee2.utils.Utils;

/**
 * Created by bkach on 10/18/15.
 */
public class InQueueLinearLayout extends BaseLinearLayout {

    private boolean mAlmostThere;
    private boolean mYourTurn;
    private boolean mNext;

    public InQueueLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mAlmostThere = false;
        mYourTurn = false;
        mNext = false;
    }

    public void setSnoozeButtonListener(Runnable runnable) {
        setFooterButtonListener((v) -> {
            mFooterImageButton.setEnabled(false);
            mFooterTextView.setText("Snoozing...");
            runnable.run();
        });
    }

    public void notLastInQueue() {
        mFooterImageButton.setEnabled(true);
        mFooterImageButton.setVisibility(VISIBLE);
        mFooterTextView.setText("Move two people in front of you");
    }

    public void lastInQueue() {
        mFooterImageButton.setVisibility(INVISIBLE);
        mFooterTextView.setText("At the back of the queue");
    }

    public void leavingQueue() {
        mSubheaderTextView.setText("Leaving queue...");
    }


    public void update(int position) {
        if (position == 1) {
            if (!mYourTurn) {
                setYourTurnStyle();
            }
        } else if (position == 2) {
            if (!mNext) {
                setNextStyle();
            }
        } else if (position == 5) {
            if (!mAlmostThere) {
                setAlmostThereStyle(position);
            }
        } else {
            if (mAlmostThere) {
                setDefaultStyle();
            }
            mHeaderTextView.setText(Utils.positionToString(position));
            String subheaderText = "About " + String.valueOf(position * 2) + " min left";
            mSubheaderTextView.setText(subheaderText);
        }
    }

    private void setNextStyle() {
       mNext = true;
        mHeaderTextView.setTextSize(42.0f);
        mHeaderTextView.setText("You're Next!");
    }

    private void setAlmostThereStyle(int position) {
        mAlmostThere = true;
        transitionBackgroundColor(Color.WHITE, getResources().getColor(R.color.happy_peach));
        replaceAnimationDrawable(R.drawable.animation_almost_there);
        mHeaderTextView.setTextSize(42.0f);
        mHeaderTextView.setText(R.string.in_queue_almost_there_header);
        mSubheaderTextView.setText(Utils.positionToString(position));
        mSubheaderTextView.setTextColor(Color.WHITE);
        mHeaderTextView.setTextColor(Color.WHITE);
        mFooterTextView.setTextColor(Color.WHITE);
        mFooterImageButton.setImageResource(R.drawable.snooze_almost_there_button_selector);
    }

    private void setDefaultStyle() {
        mAlmostThere = false;
        transitionBackgroundColor(getResources().getColor(R.color.happy_peach), Color.WHITE);
        replaceAnimationDrawable(R.drawable.animation_waiting);
        mHeaderTextView.setTextSize(70.0f);
        mHeaderTextView.setTextColor(getResources().getColor(R.color.happy_grey));
        mFooterTextView.setTextColor(getResources().getColor(R.color.happy_grey));
        mSubheaderTextView.setTextColor(getResources().getColor(R.color.happy_grey));
        mFooterImageButton.setImageResource(R.drawable.snooze_button_selector);
    }

    private void setYourTurnStyle() {
        mYourTurn = true;
        transitionBackgroundColor(Color.WHITE, getResources().getColor(R.color.happy_blue));
        replaceAnimationDrawable(R.drawable.animation_you_are_next);
        mHeaderTextView.setTextSize(42.0f);
        mHeaderTextView.setText("It's your turn!");
        mHeaderTextView.setTextColor(Color.WHITE);
        mSubheaderTextView.setText("Go to the cashier");
        mSubheaderTextView.setTextColor(Color.WHITE);
        mFooterTextView.setTextColor(Color.WHITE);
        mFooterTextView.setText("Show this screen to the cashier");
        mFooterImageButton.setVisibility(GONE);
    }

    private void transitionBackgroundColor(int colorFrom, int colorTo) {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(
                animator -> setBackgroundColor((Integer) animator.getAnimatedValue()));
        colorAnimation.start();
    }
}
