package app.com.example.android.queuee2.utils;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import org.w3c.dom.Text;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.com.example.android.queuee2.MyApplication;
import app.com.example.android.queuee2.R;
import app.com.example.android.queuee2.model.Response;

/**
 * Created by Abraham on 9/25/2015.
 */
public class Utils {

    public static Response jsonToResponse(JsonElement data) {
        Gson gson = new Gson();
        return gson.fromJson(data, Response.class);
    }

    public static int getQueueImageResource(String queueId) {
        switch (queueId) {
            case "queue1":
                return R.drawable.logo_hm;
            case "queue2":
                return R.drawable.logo_lindex;
            case "queue3":
                return R.drawable.logo_monki;
        }
        return -1;
    }

    public static void storeQueueId(String queueId){
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("queueId", queueId);
        editor.commit();
    }

    public static String getQueueId() {
        return PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext())
                .getString("queueId", null);
    }

    // TODO: 10/16/15 remove this when appropriate
    public static void setupActionBar(Activity activity, String queueId, ActionBar ab, Runnable cancelBarRunnable) {
        if (ab != null) {
            View v = LayoutInflater.from(ab.getThemedContext())
                    .inflate(R.layout.action_bar_default_layout, null);

            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);

            ab.setCustomView(v, layout);
            ab.setDisplayShowCustomEnabled(true);

            ImageView logoImageView = (ImageView) activity.findViewById(R.id.action_bar_centered_image);
            ImageView cancelImageView = (ImageView) activity.findViewById(R.id.action_bar_default_layout_cancel_button);
            TextView titleTextView = (TextView) activity.findViewById(R.id.action_bar_title_text);

            if (queueId != null) {
                // TODO: 10/15/15 When new logo comes, redo this
                titleTextView.setVisibility(View.INVISIBLE);
                logoImageView.setImageResource(Utils.getQueueImageResource(queueId));
            }

            if (cancelBarRunnable != null) {
                cancelImageView.setVisibility(View.VISIBLE);
                cancelImageView.setOnClickListener((view) -> cancelBarRunnable.run());
            }

            Toolbar toolbar = (Toolbar) v.getParent();
            toolbar.setContentInsetsAbsolute(0, 0);
        }
    }

    public static void afterDelayRun(int time, Runnable callback) {
        final Handler handler = new Handler();
        handler.postDelayed(callback,time * 1000);
    }
}
