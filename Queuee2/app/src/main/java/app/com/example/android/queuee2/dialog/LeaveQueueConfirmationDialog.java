package app.com.example.android.queuee2.dialog;


import android.content.Context;
import android.view.View;

import app.com.example.android.queuee2.R;

/**
 * Created by Abraham on 10/2/2015.
 */
public class LeaveQueueConfirmationDialog extends BaseDialog {

    public LeaveQueueConfirmationDialog(Context context, Runnable yesClickListener, Runnable noClickListener) {
        super(context);
        setViewContent();
        setDialogListeners(yesClickListener, noClickListener);
        show();
    }

    private void setViewContent(){
        mImageView.setImageResource(R.drawable.sad_face_icon);
        mLogo.setVisibility(View.VISIBLE);
        mSubheader.setText("Are you sure you want to leave?");
    }

    private void setDialogListeners(Runnable yes, Runnable no) {
        mOkButton.setVisibility(View.GONE);
        setListener(mYesButton, yes);
        setListener(mNoButton, no);
    }
}
