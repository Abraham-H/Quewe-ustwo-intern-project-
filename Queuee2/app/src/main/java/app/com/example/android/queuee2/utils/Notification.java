package app.com.example.android.queuee2.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import app.com.example.android.queuee2.R;

/**
 * Created by bkach on 9/28/15.
 */
public final class Notification {

    private static int ACTIVE_NOTIFICATION_ID = 0;

    private Notification() {
    }

    public static void youAreNextNotification(Context fromActivity, Class toActivityClass) {
        final int NOTIFICATION_ID = 123;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) fromActivity.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(fromActivity, toActivityClass);
        notifyIntent.putExtra("queueId", Utils.getQueueId());
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                fromActivity,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
        android.app.Notification notification = new android.app.Notification.Builder(fromActivity)
                .setSmallIcon(R.drawable.happy_face_icon)
                .setContentTitle("You're Next!")
                .setContentText("Tap here!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= android.app.Notification.DEFAULT_SOUND;
        notification.defaults |= android.app.Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);
        ACTIVE_NOTIFICATION_ID = NOTIFICATION_ID;
    }

    public static void almostThereNotification(Context fromActivity, Class toActivityClass) {
        final int NOTIFICATION_ID = 456;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) fromActivity.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent notifyIntent = new Intent(fromActivity, toActivityClass);
        notifyIntent.putExtra("queueId", Utils.getQueueId());
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(
                fromActivity,
                0,
                new Intent[]{notifyIntent},
                PendingIntent.FLAG_UPDATE_CURRENT);
        android.app.Notification notification = new android.app.Notification.Builder(fromActivity)
                .setSmallIcon(R.drawable.happy_face_icon)
                .setContentTitle("Almost There!")
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= android.app.Notification.DEFAULT_SOUND;
        notification.defaults |= android.app.Notification.DEFAULT_LIGHTS;
        notificationManager.notify(NOTIFICATION_ID, notification);
        ACTIVE_NOTIFICATION_ID = NOTIFICATION_ID;
    }

    public static void removeLastNotification(Context ctx) {
        if (Notification.ACTIVE_NOTIFICATION_ID != 0) {
            NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(Notification.ACTIVE_NOTIFICATION_ID);
        }
    }
}
