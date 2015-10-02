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
    public LeaveQueueConfirmationDialog(Context context) {
        super(context);
        setDialogComponents();
        show();
    }

    private void setDialogComponents() {
        setContentView(R.layout.leave_queue_confirmation_pop_up_dialog);
       // setTitle("Warning do you want to leave queue");

        TextView popUpText = (TextView) findViewById(R.id.popUpDialogTextView);
        popUpText.setText("Warning do you want to leave queue?");
        ImageView popUpImageView = (ImageView) findViewById(R.id.popUpDialogImage);
        popUpImageView.setImageResource(R.drawable.sad_face_icon);

        Button yesPopUpButton = (Button) findViewById(R.id.dialogButtonOK);
        yesPopUpButton.setOnClickListener(z -> {
                    dismiss();
                    // TODO: 10/2/2015 Actually remove user from queue
                    Toast.makeText(getContext(), "LeaveQueueConfirmationDialog yes tapped...now remove user!",
                            Toast.LENGTH_LONG).show();
                }
        );

        Button noPopUpButton = (Button) findViewById(R.id.dialogButtonNo);
        noPopUpButton.setOnClickListener(z -> {
                    dismiss();
                }
        );
    }
}
