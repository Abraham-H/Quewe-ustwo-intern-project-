package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageButton;

import app.com.example.android.queuee2.activity.StyledActionBarActivity;
import app.com.example.android.queuee2.utils.Utils;
import app.com.example.android.queuee2.view.ThankYouLinearLayout;

public class ThankYouActivity extends StyledActionBarActivity {

    private static final String TAG = ThankYouActivity.class.getSimpleName();
    private ThankYouLinearLayout mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        setView();
    }

    private void setView(){
        mView = (ThankYouLinearLayout) findViewById(R.id.thank_you_linear_layout);
        mView.setQueueAgainButtonListener(this::backToAddToQueueActivity);
    }

    public void backToAddToQueueActivity() {
        Intent intent = new Intent(this,AddToQueueActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){

    }
}
