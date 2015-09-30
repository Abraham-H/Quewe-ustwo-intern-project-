package app.com.example.android.queuee2.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

import app.com.example.android.queuee2.R;

/**
 * Created by Abraham on 9/30/2015.
 */
public class PopUp {

    public static void startInQueuePopUp(Context ctx) {

        Dialog inQueuePopUpDialog = new Dialog(ctx);

        inQueuePopUpDialog.setContentView(R.layout.in_queue_pop_up_dialog);//Set content view
        inQueuePopUpDialog.setTitle("Your lined up");

        //TextView popUpText = (TextView) inQueuePopUpDialog.findViewById(R.id.popUpDialogTextView);
        //popUpText.setText("You have been entered in the line");
        ImageView popUpImageView = (ImageView) inQueuePopUpDialog.findViewById(R.id.popUpDialogImage);
        popUpImageView.setImageResource(R.drawable.button_hold);

        Button popUpButton = (Button) inQueuePopUpDialog.findViewById(R.id.dialogButtonOK);
        popUpButton.setOnClickListener(z -> {
                    inQueuePopUpDialog.dismiss();
                }
        );
        inQueuePopUpDialog.show();
    }

    public void leaveQueueConfirmatioPopUp(){

//        inQueuePopUpDialog.setContentView(R.layout.leave_queue_confirmation_pop_up_dialog);//Set content view
//        inQueuePopUpDialog.setTitle("Are you sure you want to leave");
//
//        //TextView popUpText = (TextView) inQueuePopUpDialog.findViewById(R.id.popUpDialogTextView);
//        //popUpText.setText("You have been entered in the line");
//        ImageView popUpImageView = (ImageView) inQueuePopUpDialog.findViewById(R.id.popUpDialogImage);
//        popUpImageView.setImageResource(R.drawable.button_hold);
//
//        Button popUpButton = (Button) inQueuePopUpDialog.findViewById(R.id.dialogButtonOK);
//        popUpButton.setOnClickListener(z -> {
//                    inQueuePopUpDialog.dismiss();
//                }
//        );
//
//
//        inQueuePopUpDialog.show();
//
    }
}
