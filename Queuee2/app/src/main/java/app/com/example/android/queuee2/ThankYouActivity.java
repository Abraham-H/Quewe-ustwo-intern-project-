package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
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
        Utils.setupActionBar( this, getIntent().getStringExtra("queueId"), getActionBar(), null );
        ImageButton backToStartButton = (ImageButton) findViewById(R.id.back_to_start_button);
        backToStartButton.setOnClickListener((v) -> {
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
