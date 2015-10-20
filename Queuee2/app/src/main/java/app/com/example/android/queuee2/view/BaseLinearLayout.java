package app.com.example.android.queuee2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

import app.com.example.android.queuee2.R;
import app.com.example.android.queuee2.utils.Utils;

/**
 * Created by bkach on 10/15/15.
 */
public class BaseLinearLayout extends LinearLayout {

    private static final String TAG = BaseLinearLayout.class.getSimpleName();

    protected ImageView mHeaderImageView;
    protected TextView mHeaderTextView;
    protected TextView mSubheaderTextView;
    protected SimpleDraweeView mAnimationView;
    protected ImageButton mCenterImageButton;
    protected ImageButton mFooterImageButton;
    protected TextView mFooterTextView;
    protected FrameLayout mFrameLayout;

    public BaseLinearLayout(Context context, AttributeSet attrs){
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.base_linear_layout, (ViewGroup) this.getRootView(), true);
        setViews();
        setAttributes(context, attrs);
    }

    private void setViews(){
        mHeaderImageView = (ImageView) findViewById(R.id.custom_header_image_view);
        mHeaderTextView= (TextView) findViewById(R.id.custom_header_text_view);
        mSubheaderTextView = (TextView) findViewById(R.id.custom_subheader_text_view);
        mAnimationView = (SimpleDraweeView) findViewById(R.id.custom_animation_drawee_view);
        mCenterImageButton = (ImageButton) findViewById(R.id.custom_center_image_button);
        mFooterImageButton = (ImageButton) findViewById(R.id.custom_footer_image_button);
        mFooterTextView = (TextView) findViewById(R.id.custom_footer_text_view);
        mFrameLayout = (FrameLayout) findViewById(R.id.custom_frame_layout);
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
            mFooterImageButton.setImageDrawable(a.getDrawable(R.styleable.CustomLinearLayout_footer_image_drawable));
            if (a.getBoolean(R.styleable.CustomLinearLayout_footer_image_hidden, true)) {
                mFooterImageButton.setVisibility(GONE);
            }
            int animationResource = a.getResourceId(R.styleable.CustomLinearLayout_initial_animation,0);
            if (animationResource != 0){
                replaceAnimationDrawable(animationResource);
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

    protected void setHeaderImageView(){
        mHeaderImageView.setImageResource(Utils.getQueueImageResource(Utils.getQueueId()));
        if (mHeaderTextView.getVisibility() == VISIBLE) {
            mHeaderTextView.setVisibility(INVISIBLE);
        }
    }

    protected void setFooterButtonListener(OnClickListener onClickListener){
        mFooterImageButton.setOnClickListener(onClickListener);
    }

}
