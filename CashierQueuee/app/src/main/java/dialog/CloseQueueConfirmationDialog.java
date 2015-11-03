package dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.abraham.cashierqueuee.R;

/**
 * Created by Abraham on 10/2/2015.
 */

public class CloseQueueConfirmationDialog extends BaseDialog {

    public CloseQueueConfirmationDialog(Context context, Runnable yesClickListener, Runnable noClickListener) {
        super(context);
        setViewContent();
        setDialogListeners(yesClickListener, noClickListener);
        show();
    }

    private void setViewContent(){
        mImageView.setVisibility(View.GONE);
        mText.setText("Are you sure you want to deactivate queue?");
    }

    private void setDialogListeners(Runnable yes, Runnable no) {
        setListener(mYesButton, yes);
        setListener(mNoButton, no);
    }
}
