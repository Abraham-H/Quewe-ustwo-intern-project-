package app.com.example.android.queuee2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;

import app.com.example.android.queuee2.model.HerokuApiClient;

public class YouAreNextActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_you_are_next);
        setInstanceVariables();
        instantiateViews();
    }

    private void setInstanceVariables(){

    }

    private void instantiateViews() {

        Button finishedShoppingButton = (Button)findViewById(R.id.finished_shopping_button);
        finishedShoppingButton.setEnabled(true);
        finishedShoppingButton.setOnClickListener((v) -> {
            launchThankYouActivity();
        });
    }

    private void launchThankYouActivity(){
        Intent intent = new Intent(this, ThankYouActivity.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_you_are_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
