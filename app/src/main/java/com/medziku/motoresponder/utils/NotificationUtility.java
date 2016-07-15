package com.medziku.motoresponder.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;
import com.medziku.motoresponder.R;

/**
 * Exposes simple methods for showing notifications.
 */
public class NotificationUtility {

    private final Context context;
    private final NotificationManager notificationManager;
    private final int defaultNotificationID = 0;
    private int nonPermamentNotificationID;

    public NotificationUtility(Context context) {
        this.context = context;
        this.notificationManager = (NotificationManager) this.context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.nonPermamentNotificationID = 0;
    }


    /**
     * This will show notification which can't be hidden by user
     */
    public void showOngoingNotification(String title, String content, String info, int notificationID, String intentName) {
        this.showNotification(title, content, info, notificationID, true, null, intentName);
    }

    /**
     * This will show notification which can't be hidden by user
     */
    public void showOngoingNotification(String title, String content, String info, int notificationID) {
        this.showOngoingNotification(title, content, info, notificationID, null);
    }

    /**
     * This will show notification which can't be hidden by user
     */
    public void showOngoingNotification(String title, String content, String info) {
        this.showOngoingNotification(title, content, info, this.defaultNotificationID);
    }

    /**
     * This will show notification with title as title and bigContent as long long text which is not limited to one line.
     */
    public void showBigTextNotification(String title, String summary, String bigContent, int notificationID) {
        this.showNotification(title, summary, "", notificationID, false, new Notification.BigTextStyle()
                .bigText(bigContent));
    }

    /**
     * This will show notification with title as title and bigContent as long long text which is not limited to one line.
     */
    public void showBigTextNotification(String title, String summary, String bigContent) {
        this.showBigTextNotification(title, summary, bigContent, this.createNextNotificationID());
    }

    /**
     * This will show normal notification with title as title, content on left side on bottom line, and info on right
     * side of bottom line.
     */
    public void showNormalNotification(String title, String content, String info, int notificationID) {
        this.showNotification(title, content, info, notificationID, false, null);
    }

    /**
     * This will show normal notification with title as title, content on left side on bottom line, and info on right
     * side of bottom line.
     */
    public void showNormalNotification(String title, String content, String info) {
        this.showNormalNotification(title, content, info, this.createNextNotificationID());
    }

    /**
     * This will show toast. Thread must call Looper.prepare() and Looper.loop().
     *
     * @param message
     */
    public void showToast(String message) {
        Toast.makeText(this.context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * This will hide notification, no matter of type.
     */
    public void hideNotification(int notificationID) {
        this.notificationManager.cancel(notificationID);
    }

    /**
     * This will hide notification, no matter of type.
     */
    public void hideNotification() {
        this.hideNotification(this.defaultNotificationID);
    }

    private int createNextNotificationID() {
        this.nonPermamentNotificationID++;
        return this.nonPermamentNotificationID;
    }

    private void showNotification(String title, String content, String info, int notificationID, boolean isOngoing, Notification.Style style) {
        this.showNotification(title, content, info, notificationID, isOngoing, style, null);
    }

    private void showNotification(String title, String content, String info, int notificationID, boolean isOngoing, Notification.Style style, String intentName) {

        Notification.Builder notificationBuilder = new Notification.Builder(this.context.getApplicationContext());

        notificationBuilder.setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(this.getLauncherIcon())
                .setContentTitle(title)
                .setContentText(content)
                .setContentInfo(info);

        if (intentName != null) {
            notificationBuilder.setContentIntent(PendingIntent.getBroadcast(
                    this.context,
                    notificationID,
                    new Intent(intentName),
                    PendingIntent.FLAG_UPDATE_CURRENT
            ));
        }

        if (style != null) {
            notificationBuilder.setStyle(style);
        }


        Notification notification = notificationBuilder.build();
        if (isOngoing) {
            notification.flags = Notification.FLAG_ONGOING_EVENT;
        }

        notificationManager.notify(notificationID, notification);
    }

    private Bitmap getLauncherIcon() {
        int iconID = R.mipmap.ic_launcher;
        return this.getIcon(iconID);
    }

    private Bitmap getIcon(int iconID) {
        return BitmapFactory.decodeResource(this.context.getResources(), iconID);
    }
}
