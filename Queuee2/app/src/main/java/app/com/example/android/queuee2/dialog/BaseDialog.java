package app.com.example.android.queuee2.dialog;


import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import app.com.example.android.queuee2.R;
import app.com.example.android.queuee2.utils.Utils;

/**
 * Created by Abraham on 10/2/2015.
 */
public class BaseDialog extends Dialog {

    private final PointF CUSTOM_DIMENS = new PointF(0.6f,0.5f);
    protected Context mCtx;
    protected ImageView mImageView;
    protected ImageView mLogo;
    protected Button mYesButton;
    protected Button mNoButton;
    protected Button mOkButton;
    protected TextView mSubheader;
    protected TextView mHeader;

    BaseDialog(Context context) {
        super(context);
        mCtx = context;
        setContentView(R.layout.base_dialog_layout);
        setWindowDimensions();
        setDialogViews();
    }

    private void setWindowDimensions() {
        Display display = ((WindowManager) mCtx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        LinearLayout layout = (LinearLayout) findViewById(R.id.base_dialog_layout);

        Point dimensions = new Point();
        display.getSize(dimensions);

        layout.setMinimumWidth((int) (dimensions.x * CUSTOM_DIMENS.x));
        layout.setMinimumHeight((int) (dimensions.y * CUSTOM_DIMENS.y));
    }

    private void setDialogViews(){
        mYesButton = (Button) findViewById(R.id.base_dialog_yes);
        mNoButton = (Button) findViewById(R.id.base_dialog_no);
        mOkButton = (Button) findViewById(R.id.base_dialog_ok);
        mImageView = (ImageView) findViewById(R.id.base_dialog_image);
        mSubheader = (TextView) findViewById(R.id.base_dialog_subheader);
        mHeader = (TextView) findViewById(R.id.base_dialog_header);
        mLogo = (ImageView) findViewById(R.id.base_dialog_logo);

        getWindow().setBackgroundDrawableResource(android.R.color.white);
        mLogo.setImageResource(Utils.getQueueImageResource());

        mYesButton.setTextColor(mCtx.getResources().getColor(R.color.happy_blue));
        mNoButton.setTextColor(mCtx.getResources().getColor(R.color.happy_blue));
        mOkButton.setTextColor(mCtx.getResources().getColor(R.color.happy_blue));
    }

    protected void setListener(Button button, Runnable runnable) {
        button.setOnClickListener(v -> {
            runnable.run();
            dismiss();
        });
    }
}
