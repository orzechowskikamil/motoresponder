package com.medziku.motoresponder.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.IntentsUtility;
import com.medziku.motoresponder.utils.NotificationUtility;

public class IAmRidingNotificationHandler {


    public static String DISABLE_I_AM_RIDING = "com.medziku.IAmRidingNotificationHandler.DISABLE_I_AM_RIDING";
    protected Settings settings;
    protected Context context;
    protected BroadcastReceiver receiver;
    private IntentsUtility intentsUtility;
    private NotificationUtility notificationUtility;
    private Predicate<Boolean> isRidingAssumedSettingChangedCallback;

    public IAmRidingNotificationHandler(Context context, Settings settings, NotificationUtility notificationUtility, IntentsUtility intentsUtility) {
        this.settings = settings;
        this.context = context;
        this.intentsUtility = intentsUtility;
        this.notificationUtility = notificationUtility;
    }


    public void handleNotification() {
        this.listenToNotificationOffIntent();
        this.listenToSettingsChange();
        this.displayNotificationIfNeeded();
    }

    public void stopHandlingNotification() {
        this.stopListeningToNotificationOffIntent();
        this.stopListeningToSettingChange();
        this.displayNotificationIfNeeded();
    }

    private void stopListeningToSettingChange() {
        this.settings.stopListeningToSetting(this.settings.IS_RIDING_ASSUMED_KEY, this.isRidingAssumedSettingChangedCallback);
    }

    private void listenToSettingsChange() {
        this.isRidingAssumedSettingChangedCallback = new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                IAmRidingNotificationHandler.this.displayNotificationIfNeeded();
                return false;
            }


        };
        this.settings.listenToSettingChange(this.settings.IS_RIDING_ASSUMED_KEY, this.isRidingAssumedSettingChangedCallback);
    }

    private void listenToNotificationOffIntent() {
        if (this.receiver != null) {
            this.stopListeningToNotificationOffIntent();
        }

        IntentFilter intentFilter =this.intentsUtility.createIntentFilter(IAmRidingNotificationHandler.DISABLE_I_AM_RIDING);

        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                IAmRidingNotificationHandler.this.stopIAmRidingMode();
            }
        };

        this.context.registerReceiver(receiver, intentFilter);
        this.receiver=receiver;
    }

    private void stopListeningToNotificationOffIntent() {
        if (this.receiver == null) {
            return;
        }

        this.context.unregisterReceiver(this.receiver);
        this.receiver = null;
    }

    private void displayNotificationIfNeeded() {
        if (this.shouldNotificationAboutIAmRidingBeDisplayed()) {
            this.showNotificationAboutIAmRiding();
        } else {
            this.notificationUtility.hideNotification(this.settings.ONGOING_NOTIFICATION_I_AM_RIDING_ID);
        }
    }

    private void showNotificationAboutIAmRiding() {
        String title = this.settings.getStringFromRes(this.settings.IS_RIDING_ASSUMED_NOTIFICATION_TITLE_TEXT_ID);
        String content = this.settings.getStringFromRes(this.settings.IS_RIDING_ASSUMED_NOTIFICATION_BIG_TEXT_ID);
        this.notificationUtility.showOngoingNotification(
                title,
                content,
                null,
                this.settings.ONGOING_NOTIFICATION_I_AM_RIDING_ID,
                IAmRidingNotificationHandler.DISABLE_I_AM_RIDING
        );
    }

    private boolean shouldNotificationAboutIAmRidingBeDisplayed() {
        return this.settings.isResponderEnabled() && this.settings.isRidingAssumed();
    }


    private void stopIAmRidingMode() {
        this.settings.setRidingAssumed(false);
        this.displayNotificationIfNeeded();
    }


}
