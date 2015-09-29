package app.com.example.android.queuee2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.w3c.dom.Attr;

/**
 * Created by Abraham on 9/28/2015.
 */
public class YourInLinePopUpView extends LinearLayout {

    public YourInLinePopUpView(Context context) {
        super(context);
        commonInit(context);
    }

    public YourInLinePopUpView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        commonInit(context);
    }

    public YourInLinePopUpView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.commonInit(context);
    }

    private void commonInit(Context context){
       // setOrientation(VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.your_in_line_pop_up_view, this, true);
       //setBackgroundResource(R.drawable.black_border);
    }
}
