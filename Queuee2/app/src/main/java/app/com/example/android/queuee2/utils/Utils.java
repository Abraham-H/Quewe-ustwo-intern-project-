package app.com.example.android.queuee2.utils;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import app.com.example.android.queuee2.MyApplication;
import app.com.example.android.queuee2.R;
import app.com.example.android.queuee2.model.Response;

/**
 * Created by Abraham on 9/25/2015.
 */
public final class Utils {

    private Utils() {
    }

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

    public static void storeQueueId(String queueId) {
        SharedPreferences sharedPref =
                PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("queueId", queueId);
        editor.apply();
    }

    public static String getQueueId() {
        return PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext())
                .getString("queueId", null);
    }

    public static void afterDelayRun(int time, Runnable callback) {
        final Handler handler = new Handler();
        handler.postDelayed(callback, time * 1000);
    }

    public static String positionToString(int position) {
        String suffix;
        if (position % 10 == 1 && position % 11 != 0) {
            suffix = "st";
        } else if (position % 10 == 2 && position % 12 != 0) {
            suffix = "nd";
        } else if (position % 10 == 3 && position % 13 != 0) {
            suffix = "rd";
        } else {
            suffix = "th";
        }
        return String.valueOf(position) + suffix;
    }
}
