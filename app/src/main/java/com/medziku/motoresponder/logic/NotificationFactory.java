package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.NotificationUtility;

class NotificationFactory {
    public static String RECIPIENT_SUBSTITUTION_TAG = "%recipient%";
    private NotificationUtility notificationUtility;
    private Settings settings;

    private int PENDING_NOTIFICATION_ID = 1;
    private int TURNED_OFF_GPS_NOTIFICATION_ID = 2;
    private int POWER_SAVER_NOTIFICATION_ID = 3;

    public NotificationFactory(NotificationUtility notificationUtility, Settings settings) {
        this.notificationUtility = notificationUtility;
        this.settings = settings;
    }

    public void showNotificationAboutTurnedOffGPS() {
        String title = this.settings.getStringFromRes(this.settings.TURNED_OFF_GPS_NOTIFICATION_TITLE_RES_ID);
        String content = this.settings.getStringFromRes(this.settings.TURNED_OFF_GPS_NOTIFICATION_TEXT_RES_ID);

        this.notificationUtility.showNormalNotification(title, content, content, TURNED_OFF_GPS_NOTIFICATION_ID);
    }

    public void showSummaryNotification(String recipient) {
        String title = this.settings.getStringFromRes(this.settings.SUMMARY_NOTIFICATION_TITLE_TEXT_RES_ID);
        String shortText = this.settings.getStringFromRes(this.settings.SUMMARY_NOTIFICATION_SHORT_TEXT_RES_ID).replace(RECIPIENT_SUBSTITUTION_TAG, recipient);
        String bigText = this.settings.getStringFromRes(this.settings.SUMMARY_NOTIFICATION_BIG_TEXT_RES_ID).replace(RECIPIENT_SUBSTITUTION_TAG, recipient);

        this.notificationUtility.showBigTextNotification(title, shortText, bigText);
    }

    public void hideSummaryNotification() {
        this.notificationUtility.hideNotification();
    }


    public void showPendingNotification() {
        String title = this.settings.getStringFromRes(this.settings.ONGOING_NOTIFICATION_TITLE_TEXT_RES_ID);
        String bigText = this.settings.getStringFromRes(this.settings.ONGOING_NOTIFICATION_BIG_TEXT_RES_ID);
        this.notificationUtility.showOngoingNotification(title, bigText, "", this.PENDING_NOTIFICATION_ID);
    }

    public void hidePendingNotification() {
        this.notificationUtility.hideNotification(this.PENDING_NOTIFICATION_ID);
    }


    public void showNotificationAboutPowerSaveMode() {
        String title = this.settings.getStringFromRes(this.settings.POWER_SAVE_NOTIFICATION_TITLE_TEXT_RES_ID);
        String shortText = this.settings.getStringFromRes(this.settings.POWER_SAVE_NOTIFICATION_SHORT_TEXT_RES_ID);
        String bigText = this.settings.getStringFromRes(this.settings.POWER_SAVE_NOTIFICATION_BIG_TEXT);

        this.notificationUtility.showBigTextNotification(title, shortText, bigText, this.POWER_SAVER_NOTIFICATION_ID);
    }

}
