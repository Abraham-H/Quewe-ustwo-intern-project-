package dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.abraham.cashierqueuee.R;

/**
 * Created by Abraham on 10/2/2015.
 */
public class CloseQueueConfirmationDialog extends Dialog {

    Runnable mYesClickListener;
    Runnable mNoClickListener;

    public CloseQueueConfirmationDialog(Context context, Runnable yesClickListener, Runnable noClickListener) {
        super(context);
        mYesClickListener = yesClickListener;
        mNoClickListener = noClickListener;
        setDialogComponents();
        show();
    }

    private void setDialogComponents() {
        setContentView(R.layout.close_queue_confirmation_dialog);
       // setTitle("Warning do you want to leave queue");
        TextView popUpText = (TextView) findViewById(R.id.popUpDialogTextView);
        popUpText.setText("Warning do you want to close the queue?");
        ImageView popUpImageView = (ImageView) findViewById(R.id.popUpDialogImage);
        popUpImageView.setImageResource(R.drawable.sad_face_icon);

        Button yesPopUpButton = (Button) findViewById(R.id.dialogButtonOK);
        yesPopUpButton.setOnClickListener(z -> {
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
