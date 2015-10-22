package views;

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

import com.example.abraham.cashierqueuee.R;
import com.facebook.common.util.UriUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by Abraham on 10/19/2015.
 */
public class BaseCustomLinearLayout extends LinearLayout {

    protected ImageView mHeaderImageView;
    protected TextView mHeaderTextView;
    protected TextView mSubheaderTextView;
    protected SimpleDraweeView mAnimationView;
    protected ImageButton mCenterImageButton;
    protected ImageButton mFooterImageButton;
    protected TextView mFooterTextView;

    public BaseCustomLinearLayout(Context context, AttributeSet attrs){
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
        mCenterImageButton = (ImageButton) findViewById(R.id.custom_linear_layout__start_center_image_button);
        mFooterImageButton = (ImageButton) findViewById(R.id.custom_linear_layout_next_image_button);
        mFooterTextView = (TextView) findViewById(R.id.custom_footer_text_view);
    }

    private void setAttributes(Context context, AttributeSet attrs){
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.BaseCustomLinearLayout,
                0, 0);
        try {
            mHeaderTextView.setText(a.getString(R.styleable.BaseCustomLinearLayout_header_text));
            mSubheaderTextView.setText(a.getString(R.styleable.BaseCustomLinearLayout_subheader_text));
            mFooterTextView.setText(a.getString(R.styleable.BaseCustomLinearLayout_footer_text));

        } finally {
            a.recycle();
        }
    }

    protected void runAnimationDrawable(int drawable) {
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
