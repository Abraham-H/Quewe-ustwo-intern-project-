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

    private int mState;
    private final int DEFAULT = 0;
    private final int ALMOST_THERE = 1;
    private final int YOUR_TURN = 2;
    private final int NEXT = 3;
    private final int SNOOZE = 4;

    private int mCurrentColor;


    public InQueueLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mState = DEFAULT;
        mCurrentColor = getBackgroundColorForState();
    }

    public void setSnoozeButtonListener(Runnable runnable) {
        setFooterButtonListener((v) -> {
            mState = SNOOZE;

            mFooterImageButton.setEnabled(false);
            replaceAnimationDrawable(getDrawableForState());
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
            if (mState != YOUR_TURN) {
                setYourTurnStyle();
            }
        } else if (position == 2) {
            if (mState != NEXT) {
                setNextStyle();
            }
        } else if (position == 5) {
            if (mState != ALMOST_THERE) {
                setAlmostThereStyle(position);
            }
        } else {
            if (mState != DEFAULT) {
                setDefaultStyle();
            }
            mHeaderTextView.setText(Utils.positionToString(position));
            String subheaderText = "About " + String.valueOf(position * 2) + " min left";
            mSubheaderTextView.setText(subheaderText);
        }
    }

    private void setNextStyle() {
        mState = NEXT;
        mHeaderTextView.setTextSize(42.0f);
        mHeaderTextView.setText("You're Next!");
    }

    private void setAlmostThereStyle(int position) {
        mState = ALMOST_THERE;
        transitionBackgroundColor();
        replaceAnimationDrawable(getDrawableForState());
        mHeaderTextView.setTextSize(42.0f);
        mHeaderTextView.setText(R.string.in_queue_almost_there_header);
        mSubheaderTextView.setText(Utils.positionToString(position));
        mSubheaderTextView.setTextColor(Color.WHITE);
        mHeaderTextView.setTextColor(Color.WHITE);
        mFooterTextView.setTextColor(Color.WHITE);
        mFooterImageButton.setImageResource(R.drawable.snooze_almost_there_button_selector);
    }

    private void setDefaultStyle() {
        mState = DEFAULT;
        transitionBackgroundColor();
        replaceAnimationDrawable(getDrawableForState());
        mHeaderTextView.setTextSize(70.0f);
        mHeaderTextView.setTextColor(getResources().getColor(R.color.happy_grey));
        mFooterTextView.setTextColor(getResources().getColor(R.color.happy_grey));
        mSubheaderTextView.setTextColor(getResources().getColor(R.color.happy_grey));
        mFooterImageButton.setImageResource(R.drawable.snooze_button_selector);
    }

    private void setYourTurnStyle() {
        mState = YOUR_TURN;
        transitionBackgroundColor();
        replaceAnimationDrawable(getDrawableForState());
        mHeaderTextView.setTextSize(42.0f);
        mHeaderTextView.setText("It's your turn!");
        mHeaderTextView.setTextColor(Color.WHITE);
        mSubheaderTextView.setText("Go to the cashier");
        mSubheaderTextView.setTextColor(Color.WHITE);
        mFooterTextView.setTextColor(Color.WHITE);
        mFooterTextView.setText("Show this screen to the cashier");
        mFooterImageButton.setVisibility(GONE);
    }

    private int getDrawableForState() {
        switch (mState){
            case SNOOZE:
                return R.drawable.snooze_face;
            case YOUR_TURN:
                return R.drawable.animation_you_are_next;
            case ALMOST_THERE:
                return R.drawable.animation_almost_there;
            case DEFAULT:
                return R.drawable.animation_waiting;
            default:
                return -1;
        }
    }

    private int getBackgroundColorForState() {
        switch (mState){
            case SNOOZE:
                return Color.WHITE;
            case YOUR_TURN:
                return getResources().getColor(R.color.happy_blue);
            case ALMOST_THERE:
                return getResources().getColor(R.color.happy_peach);
            case DEFAULT:
                return Color.WHITE;
            default:
                return -1;
        }
    }

    private void transitionBackgroundColor() {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), mCurrentColor, getBackgroundColorForState());
        colorAnimation.addUpdateListener(
                animator -> setBackgroundColor((Integer) animator.getAnimatedValue()));
        colorAnimation.start();
        mCurrentColor = getBackgroundColorForState();
    }
}
