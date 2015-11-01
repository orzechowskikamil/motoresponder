package com.medziku.motoresponder.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.activity.SettingsActivity;

/**
 * Exposes simple methods for showing notifications.
 */
public class NotificationUtility {

    private final Context context;
    private final NotificationManager notificationManager;
    private final int defaultNotificationID = 0;

    public NotificationUtility(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    public void showNotification(String title, String content, String info, int notificationID) {
        Class<SettingsActivity> activityClass = SettingsActivity.class;

        Intent resultIntent = new Intent(this.context, activityClass);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.context);

        stackBuilder.addParentStack(activityClass);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder = new Notification.Builder(this.context.getApplicationContext());

        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo(info)
                .setContentIntent(resultPendingIntent);

        Notification notification = notificationBuilder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;

        notificationManager.notify(notificationID, notification);
    }

    public void showNotification(String title, String content, String info) {
        this.showNotification(title, content, info, this.defaultNotificationID);
    }

    public void hideNotification(int notificationID) {
        this.notificationManager.cancel(notificationID);
    }

    public void hideNotification() {
        this.hideNotification(this.defaultNotificationID);
    }

}
