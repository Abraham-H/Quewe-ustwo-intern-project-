package app.com.example.android.queuee2.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import app.com.example.android.queuee2.R;
import app.com.example.android.queuee2.utils.Utils;

/**
 * Created by bkach on 10/16/15.
 */
public abstract class StyledActionBarActivity extends Activity {

    private static final String TAG = StyledActionBarActivity.class.getSimpleName();
    private ImageView mActionBarQueweLogo;
    private ImageView mActionBarLogoImageView;
    private ImageView mCancelImageView;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void setupActionBar() {
        ActionBar actionBar = getActionBar();
        View v = LayoutInflater.from(actionBar.getThemedContext())
                .inflate(R.layout.action_bar_default_layout, null);
        ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(v, layout);
        actionBar.setDisplayShowCustomEnabled(true);
        mActionBarLogoImageView = (ImageView) findViewById(R.id.action_bar_centered_image);
        mActionBarQueweLogo = (ImageView) findViewById(R.id.action_bar_quewe_logo);
        setActionBarLogo();

        Toolbar toolbar = (Toolbar) v.getParent();
        toolbar.setContentInsetsAbsolute(0, 0);
    }

    protected void setCancelListener(Runnable runnable) {
        mCancelImageView = (ImageView) findViewById(R.id.action_bar_default_layout_cancel_button);
        mCancelImageView.setVisibility(View.VISIBLE);
        mCancelImageView.setOnClickListener((v) -> runnable.run());
    }

    protected void removeCancelButton() {
        mCancelImageView.setVisibility(View.GONE);
    }

    protected void hideActionBarLogo() {
        mActionBarLogoImageView.setVisibility(View.INVISIBLE);
        mActionBarQueweLogo.setVisibility(View.VISIBLE);
    }

    protected void updateActionBarLogo() {
        setActionBarLogo();
        mActionBarLogoImageView.setVisibility(View.INVISIBLE);
        mActionBarQueweLogo.setVisibility(View.VISIBLE);
    }

    private void setActionBarLogo() {
        if (Utils.getQueueImageResource() != 0){
            mActionBarLogoImageView.setImageResource(Utils.getQueueImageResource());
        }
        mActionBarQueweLogo.setVisibility(View.INVISIBLE);
        mActionBarLogoImageView.setVisibility(View.VISIBLE);

    }


    @Override // Android M Integration
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog ->
                            Log.d(TAG, "onRequestPermissionResult() called with: " + "requestCode = [" + requestCode + "], permissions = [" + permissions + "], grantResults = [" + grantResults + "]"));
                    builder.show();
                }
            }
        }
    }
}
