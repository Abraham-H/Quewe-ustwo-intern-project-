package app.com.example.android.queuee2.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by bkach on 10/20/15.
 */
public class ThankYouLinearLayout extends BaseLinearLayout {

    public ThankYouLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setQueueAgainButtonListener(Runnable runnable) {
        setFooterButtonListener((v) -> runnable.run());
    }
}
