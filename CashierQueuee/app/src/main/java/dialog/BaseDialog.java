package dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.abraham.cashierqueuee.R;

import utils.Utils;

/**
 * Created by Abraham on 10/23/2015.
 */
public class BaseDialog extends Dialog {

    private final PointF CUSTOM_DIMENS = new PointF(0.4f,0.33f);
    protected Context mCtx;
    protected ImageView mImageView;
    protected ImageView mLogo;
    protected Button mYesButton;
    protected Button mNoButton;
    protected TextView mText;

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
        mLogo = (ImageView) findViewById(R.id.base_dialog_logo);
        mLogo.setImageResource(Utils.getQueueImageResource(Utils.getQueueId()));

        mImageView = (ImageView) findViewById(R.id.base_dialog_image);

        mYesButton = (Button) findViewById(R.id.base_dialog_yes);
        mYesButton.setTextColor(mCtx.getResources().getColor(R.color.happy_blue));
        mNoButton = (Button) findViewById(R.id.base_dialog_no);
        mNoButton.setTextColor(mCtx.getResources().getColor(R.color.happy_blue));
        mNoButton.setTextColor(mCtx.getResources().getColor(R.color.happy_blue));

        mText = (TextView) findViewById(R.id.base_dialog_text);
    }

    protected void setListener(Button button, Runnable runnable) {
        button.setOnClickListener(v -> {
            runnable.run();
            dismiss();
        });
    }
}
