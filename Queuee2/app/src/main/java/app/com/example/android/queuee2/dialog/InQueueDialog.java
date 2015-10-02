package app.com.example.android.queuee2.dialog;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.ImageView;

import app.com.example.android.queuee2.R;

/**
 * Created by Abraham on 10/2/2015.
 */
public class InQueueDialog extends Dialog {
    public InQueueDialog(Context context) {
        super(context);
        setDialogComponents();
        show();
    }

    private void setDialogComponents() {
        setContentView(R.layout.in_queue_pop_up_dialog);
        setTitle("You are lined up");
        ImageView popUpImageView = (ImageView) findViewById(R.id.popUpDialogImage);
        popUpImageView.setImageResource(R.drawable.happy_face_icon);

        Button popUpButton = (Button) findViewById(R.id.dialogButtonOK);
        popUpButton.setOnClickListener(z -> {
                    dismiss();
                }
        );
    }
}
