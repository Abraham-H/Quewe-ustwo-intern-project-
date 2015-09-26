package app.com.example.android.queuee2;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

import app.com.example.android.queuee2.model.Response;

/**
 * Created by Abraham on 9/25/2015.
 */
public class Utils {

    public static Response jsonToResponse(JsonElement data) {
        Gson gson = new Gson();
        return gson.fromJson(data, Response.class);
    }

    public static void postNotification(Activity activity, String msg ) {

        final int NOTIFICATION_ID =123;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) activity.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(activity, activity.getClass());
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                activity,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
                Notification notification = new Notification.Builder(activity)
                .setSmallIcon(R.drawable.cancel_button)
                .setContentTitle("Note")
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        notification.defaults |= Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

}
