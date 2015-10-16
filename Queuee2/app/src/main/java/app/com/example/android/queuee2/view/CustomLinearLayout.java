package app.com.example.android.queuee2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import app.com.example.android.queuee2.R;

/**
 * Created by bkach on 10/15/15.
 */
public class CustomLinearLayout extends LinearLayout {

    protected ImageView mHeaderImageView;
    protected TextView mHeaderTextView;
    protected TextView mSubheaderTextView;
    protected SimpleDraweeView mAnimationView;
    protected ImageButton mCenterImageButton;
    protected ImageButton mSnoozeImageButton;
    protected TextView mFooterTextView;

    public CustomLinearLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.custom_linear_layout, (ViewGroup) this.getRootView(), true);
        setViews();
        setAttributes(context, attrs);
    }

    private void setViews(){
        mHeaderImageView = (ImageView) findViewById(R.id.custom_header_image_view);
        mHeaderTextView= (TextView) findViewById(R.id.custom_header_text_view);
        mSubheaderTextView = (TextView) findViewById(R.id.custom_subheader_text_view);
        mAnimationView = (SimpleDraweeView) findViewById(R.id.custom_animation_drawee_view);
        mCenterImageButton = (ImageButton) findViewById(R.id.custom_center_image_button);
        mSnoozeImageButton = (ImageButton) findViewById(R.id.custom_snooze_image_button);
        mFooterTextView = (TextView) findViewById(R.id.custom_footer_text_view);
    }

    private void setAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.CustomLinearLayout,
                0, 0);
        try {
            mHeaderTextView.setText(a.getString(R.styleable.CustomLinearLayout_header_text));
            mSubheaderTextView.setText(a.getString(R.styleable.CustomLinearLayout_subheader_text));
            mFooterTextView.setText(a.getString(R.styleable.CustomLinearLayout_footer_text));
            if (a.getBoolean(R.styleable.CustomLinearLayout_snooze_hidden, true)) {
                mSnoozeImageButton.setVisibility(GONE);
            }
        } finally {
            a.recycle();
        }
    }

    protected void replaceAnimationDrawable(int drawable) {
        Uri uri = new Uri.Builder()
                .scheme(UriUtil.LOCAL_RESOURCE_SCHEME)
                .path(String.valueOf(drawable))
                .build();

        mAnimationView.setController(Fresco.newDraweeControllerBuilder()
                .setUri(uri)
                .setAutoPlayAnimations(true)
                .build());
    }
}
