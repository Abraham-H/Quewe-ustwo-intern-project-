package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.ImageButton;

import app.com.example.android.queuee2.utils.Utils;

public class ThankYouActivity extends Activity {

    private static final String TAG = ThankYouActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thank_you);
        setViews();
    }

    private void setViews(){
        String queueId = PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("queueId", "Shared Preferences Error");
        Utils.setupActionBar( this, queueId, getActionBar(), null );
        ImageButton queueAgainButton = (ImageButton) findViewById(R.id.thank_you_activity_queue_again_button);
        queueAgainButton.setOnClickListener((v) -> {
            Intent intent = new Intent(this, AddToQueueActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
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
