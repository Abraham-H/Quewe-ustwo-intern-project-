package app.com.example.android.queuee2.dialog;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import app.com.example.android.queuee2.R;

/**
 * Created by Abraham on 10/2/2015.
 */
public class GettingCloseDialog extends BaseDialog {

    public GettingCloseDialog(Context context) {
        super(context);
        removeYesNoButtons();
        setViewContent();
        setListener(mOkButton,()->{});
        show();
    }

    private void setViewContent(){
        mImageView.setImageResource(R.drawable.getting_close_face);
        mHeader.setText("Getting Close!");
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
