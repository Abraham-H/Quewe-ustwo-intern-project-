package app.com.example.android.queuee2.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.media.Image;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import app.com.example.android.queuee2.R;
import app.com.example.android.queuee2.utils.Utils;

/**
 * Created by Abraham on 10/2/2015.
 */
public class BaseDialog extends Dialog {

    protected Context mCtx;
    protected ImageView mImageView;
    protected ImageView mLogo;
    protected Button mYesButton;
    protected Button mNoButton;
    protected Button mOkButton;
    protected TextView mText;

    public BaseDialog(Context context) {
        super(context);
        mCtx = context;
        setContentView(R.layout.base_dialog_layout);
        setWindowDimensions(0.6f, 0.5f);
        setDialogViews();
    }

    private void setWindowDimensions(float width, float height) {
        Display display = ((WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        LinearLayout layout = (LinearLayout) findViewById(R.id.base_dialog_layout);

        Point dimensions = new Point();
        display.getSize(dimensions);

        layout.setMinimumWidth((int) (dimensions.x * width));
        layout.setMinimumHeight((int) (dimensions.y * height));
    }

    private void setDialogViews(){
        mLogo = (ImageView) findViewById(R.id.base_dialog_logo);
        mLogo.setImageResource(Utils.getQueueImageResource(Utils.getQueueId()));

        mImageView = (ImageView) findViewById(R.id.base_dialog_image);

        mYesButton = (Button) findViewById(R.id.base_dialog_yes);
        mYesButton.setTextColor(mCtx.getResources().getColor(R.color.happy_blue));
        mNoButton = (Button) findViewById(R.id.base_dialog_no);
        mNoButton.setTextColor(mCtx.getResources().getColor(R.color.happy_blue));
        mOkButton = (Button) findViewById(R.id.base_dialog_ok);
        mOkButton.setTextColor(mCtx.getResources().getColor(R.color.happy_blue));
        mText = (TextView) findViewById(R.id.base_dialog_text);
    }

    protected void setListener(Button button, Runnable runnable) {
        button.setOnClickListener(v -> {
            runnable.run();
            dismiss();;
        });
    }
}
