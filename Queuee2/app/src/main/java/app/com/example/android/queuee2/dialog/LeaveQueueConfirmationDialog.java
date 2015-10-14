package app.com.example.android.queuee2.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.com.example.android.queuee2.R;

/**
 * Created by Abraham on 10/2/2015.
 */
public class LeaveQueueConfirmationDialog extends Dialog {

    Runnable mYesClickListener;
    Runnable mNoClickListener;

    public LeaveQueueConfirmationDialog(Context context, Runnable yesClickListener, Runnable noClickListener) {
        super(context);
        mYesClickListener = yesClickListener;
        mNoClickListener = noClickListener;
        setDialogComponents();
        show();
    }

    private void setDialogComponents() {
        setContentView(R.layout.leave_queue_confirmation_pop_up_dialog);
        TextView popUpText = (TextView) findViewById(R.id.popUpDialogTextView);
        popUpText.setText(R.string.leave_queue_confirmation_dialog);
        ImageView popUpImageView = (ImageView) findViewById(R.id.popUpDialogImage);
        popUpImageView.setImageResource(R.drawable.sad_face_icon);

        Button yesPopUpButton = (Button) findViewById(R.id.dialogButtonOK);
        yesPopUpButton.setOnClickListener(z -> {
                    // TODO: 10/2/2015 Actually remove user from queue
                    mYesClickListener.run();
                    dismiss();
                }
        );

        Button noPopUpButton = (Button) findViewById(R.id.dialogButtonNo);
        noPopUpButton.setOnClickListener(z -> {
                    mNoClickListener.run();
                    dismiss();
                }
        );
    }
}
