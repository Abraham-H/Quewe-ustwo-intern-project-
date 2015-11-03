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
        mHeader.setText("Halfway!");
        mSubheader.setText("We'll notify you when it's your turn");
        getWindow().setBackgroundDrawableResource(R.color.happy_peach);
        mHeader.setTextColor(Color.WHITE);
        mSubheader.setTextColor(Color.WHITE);
        mOkButton.setTextColor(Color.WHITE);
    }

    private void removeYesNoButtons(){
        mYesButton.setVisibility(View.GONE);
        mNoButton.setVisibility(View.GONE);
    }
}
