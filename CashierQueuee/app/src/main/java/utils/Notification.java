package utils;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.abraham.cashierqueuee.R;

/**
 * Created by bkach on 9/28/15.
 */
public class Notification {

    public static int ACTIVE_NOTIFICATION_ID = 0;

    public static void youAreNextNotification(Activity fromActivity, Class toActivityClass, String queueId,String msg) {
        final int NOTIFICATION_ID = 123;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) fromActivity.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(fromActivity, toActivityClass);
        notifyIntent.putExtra("queueId", queueId);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                fromActivity,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
        android.app.Notification notification = new android.app.Notification.Builder(fromActivity)
                .setSmallIcon(R.drawable.cancel_button)
                .setContentTitle("Note")
                .setContentText(msg)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= android.app.Notification.DEFAULT_SOUND;
        notification.defaults |= android.app.Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);
        ACTIVE_NOTIFICATION_ID = NOTIFICATION_ID;
    }
}
