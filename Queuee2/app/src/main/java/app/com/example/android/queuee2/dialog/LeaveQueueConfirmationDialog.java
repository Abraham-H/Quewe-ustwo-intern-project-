package app.com.example.android.queuee2.dialog;

import android.app.Dialog;
import android.content.Context;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.ImageView;

import app.com.example.android.queuee2.R;
import app.com.example.android.queuee2.utils.Utils;

/**
 * Created by Abraham on 10/2/2015.
 */
public class LeaveQueueConfirmationDialog extends Dialog {

    public LeaveQueueConfirmationDialog(Context context, Runnable yesClickListener, Runnable noClickListener) {
        super(context);
        setDialogComponents(yesClickListener,noClickListener);
        show();
    }

    private void setDialogComponents(Runnable yes, Runnable no) {
        setContentView(R.layout.leave_queue_confirmation_pop_up_dialog);

        ImageView leaveQueueImageView = (ImageView) findViewById(R.id.leave_queue_confirmation_dialog_image);

        String queueId = PreferenceManager.getDefaultSharedPreferences(getContext())
                .getString("queueId", "Shared Preferences Error");
        leaveQueueImageView.setImageResource(Utils.getQueueImageResource(queueId));


        Button yesPopUpButton = (Button) findViewById(R.id.leave_queue_confirmation_dialog_yes);
        yesPopUpButton.setOnClickListener(z -> {
                    yes.run();
                    dismiss();
                }
        );

        Button noPopUpButton = (Button) findViewById(R.id.leave_queue_confirmation_dialog_no);
        noPopUpButton.setOnClickListener(z -> {
                    no.run();
                    dismiss();
                }
        );
    }
}
