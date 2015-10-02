package app.com.example.android.queuee2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import app.com.example.android.queuee2.dialog.LeaveQueueConfirmationDialog;

/**
 * Created by Abraham on 10/2/2015.
 */
public class CancelHeader extends LinearLayout {

    public CancelHeader(Context context) {
        super(context);
    }

    public CancelHeader(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        commonInit(context);
    }

    public CancelHeader(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.commonInit(context);
    }

    private void commonInit(Context context){
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.cancel_header, this, true);
    }
}
