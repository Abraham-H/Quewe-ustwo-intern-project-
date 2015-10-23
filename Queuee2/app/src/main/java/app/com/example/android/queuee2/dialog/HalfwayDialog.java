package app.com.example.android.queuee2.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import app.com.example.android.queuee2.R;

/**
 * Created by Abraham on 10/2/2015.
 */
public class HalfwayDialog extends BaseDialog {

    public HalfwayDialog(Context context) {
        super(context);
        removeYesNoButtons();
        setViewContent();
        setListener(mOkButton,()->{});
        show();
    }

    private void setViewContent(){
        mImageView.setImageResource(R.drawable.half_way_face);
        mText.setText("Halfway there!");
        getWindow().setBackgroundDrawableResource(R.color.happy_peach);
        mText.setTextColor(Color.WHITE);
        mOkButton.setTextColor(Color.WHITE);
    }

    private void removeYesNoButtons(){
        mYesButton.setVisibility(View.GONE);
        mNoButton.setVisibility(View.GONE);
    }
}
