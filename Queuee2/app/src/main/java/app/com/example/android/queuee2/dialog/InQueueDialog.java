package app.com.example.android.queuee2.dialog;

import android.content.Context;
import android.view.View;

import app.com.example.android.queuee2.R;

/**
 * Created by Abraham on 10/2/2015.
 */
public class InQueueDialog extends BaseDialog {
    public InQueueDialog(Context context) {
        super(context);
        removeYesNoButtons();
        setViewContent();
        setListener(mOkButton,()->{});
        show();
    }

    private void setViewContent(){
        mImageView.setImageResource(R.drawable.happy_face_icon);
        mHeader.setText("Queued!");
        mSubheader.setText("We'll notify you when it's your turn");
    }

    private void removeYesNoButtons(){
        mYesButton.setVisibility(View.GONE);
        mNoButton.setVisibility(View.GONE);
    }
}
