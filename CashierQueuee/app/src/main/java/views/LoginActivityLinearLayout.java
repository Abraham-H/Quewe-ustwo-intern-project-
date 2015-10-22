package views;

import android.content.Context;
import android.util.AttributeSet;

import com.example.abraham.cashierqueuee.R;

/**
 * Created by Abraham on 10/19/2015.
 */
public class LoginActivityLinearLayout extends BaseCustomLinearLayout {
    public LoginActivityLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        startQueueWelcomeAnimation();
    }

    private void startQueueWelcomeAnimation(){
        runAnimationDrawable(R.drawable.animation_loading);
    }

}
