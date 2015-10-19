package utils;

import android.app.ActionBar;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toolbar;

import com.example.abraham.cashierqueuee.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;

import model.Response;

/**
 * Created by Abraham on 9/25/2015.
 */
public class Utils {

    public static Response jsonToResponse(JsonElement data) {
        Gson gson = new Gson();
        return gson.fromJson(data, Response.class);
    }

    public static void setupActionBar(Activity activity, String queueId, ActionBar ab, Runnable cancelBarRunnable) {
        if (ab != null) {
            View v = LayoutInflater.from(ab.getThemedContext())
                    .inflate(R.layout.action_bar_default_layout, null);

            ActionBar.LayoutParams layout = new ActionBar.LayoutParams(
                    ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);

            ab.setCustomView(v, layout);
            ab.setDisplayShowCustomEnabled(true);

            ImageView logoImageView = (ImageView) activity.findViewById(R.id.action_bar_centered_image);

            if (queueId != null) {
                logoImageView.setImageResource(R.drawable.logo_quewe);
            }

            Toolbar toolbar = (Toolbar) v.getParent();
            toolbar.setContentInsetsAbsolute(0, 0);
        }
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
}
