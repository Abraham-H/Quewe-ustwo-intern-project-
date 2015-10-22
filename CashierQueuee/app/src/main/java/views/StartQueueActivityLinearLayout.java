package views;

import android.content.Context;
import android.util.AttributeSet;

import com.example.abraham.cashierqueuee.R;

import java.util.ArrayList;

import utils.Utils;

/**
 * Created by Abraham on 10/21/2015.
 */
public class StartQueueActivityLinearLayout extends BaseCustomLinearLayout {

    String mQueueId = Utils.getQueueId();

    public StartQueueActivityLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        updateStartQueueActivityLayoutViews();
    }

   private void updateStartQueueActivityLayoutViews(){
       updateTextViews();
       setImageButtonResource();
       setHeaderImageView();
   }

   private void updateTextViews(){
        mSubheaderTextView.setText(mQueueId);
    }

    private void setImageButtonResource(){
        mCenterImageButton.setImageResource(R.drawable.start_button_selector);
    }

    private void setHeaderImageView(){
        mHeaderImageView.setImageResource(Utils.getQueueImageResource(Utils.getQueueId()));
    }

    public void setStartQueueButtonOnClickListener(OnClickListener onClickListener){
        mCenterImageButton.setOnClickListener(onClickListener);
    }
}
