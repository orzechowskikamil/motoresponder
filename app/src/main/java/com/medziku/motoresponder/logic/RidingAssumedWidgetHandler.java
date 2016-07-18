package com.medziku.motoresponder.logic;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.IntentsUtility;
import com.medziku.motoresponder.widgets.RidingAssumedWidgetProvider;

public class RidingAssumedWidgetHandler {

    private final IntentsUtility intentsUtility;
    protected Settings settings;
    protected Context context;

    protected BroadcastReceiver receiver;
    private Predicate<Boolean> isRidingAssumedSettingChangeCallback;

    public RidingAssumedWidgetHandler(Context context, Settings settings, IntentsUtility intentsUtility) {
        this.settings = settings;
        this.context = context;
        this.intentsUtility = intentsUtility;
    }

    public void handleWidget() {
        this.listenToWidgetOnOffIntent();
        this.listenToIsRidingAssumedChangeInSettings();
        this.updateWidget();
    }

    public void stopHandlingWidget() {
        this.stopListeningToOnOffWidgetIntent();
        this.stopListeningToIsRidingAssumedChangeInSettings();
        this.updateWidget();
    }

    protected void handleWidgetTap() {
        if (this.settings.isResponderEnabled()) {
            this.toggleIsRidingAssumed();
        }
    }

    protected void toggleIsRidingAssumed() {
        boolean isRidingAssumed = this.settings.isRidingAssumed();
        boolean toggledIsRidingAssumed = !isRidingAssumed;

        this.settings.setRidingAssumed(toggledIsRidingAssumed);

    }

    private void listenToWidgetOnOffIntent() {
        if (this.receiver != null) {
            this.stopHandlingWidget();
        }

        this.receiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                RidingAssumedWidgetHandler.this.handleWidgetTap();
            }
        };

        this.context.registerReceiver(receiver, this.intentsUtility.createIntentFilter(RidingAssumedWidgetProvider.ACTION_WIDGET_TAP));
    }

    private void listenToIsRidingAssumedChangeInSettings() {
        this.isRidingAssumedSettingChangeCallback = new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                RidingAssumedWidgetHandler.this.updateWidget();
                return false;
            }
        };
        this.settings.listenToSettingChange(this.settings.IS_RIDING_ASSUMED_KEY, this.isRidingAssumedSettingChangeCallback);
        this.settings.listenToSettingChange(this.settings.RESPONDER_ENABLED_KEY, this.isRidingAssumedSettingChangeCallback);
    }

    private void stopListeningToIsRidingAssumedChangeInSettings() {
        this.settings.stopListeningToSetting(this.settings.IS_RIDING_ASSUMED_KEY, this.isRidingAssumedSettingChangeCallback);
        this.settings.stopListeningToSetting(this.settings.RESPONDER_ENABLED_KEY, this.isRidingAssumedSettingChangeCallback);
    }

    private void updateWidget() {
        Intent intent = this.intentsUtility.createIntent(this.context, RidingAssumedWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        this.context.sendBroadcast(intent);
    }

    private void stopListeningToOnOffWidgetIntent() {
        if (this.receiver == null) {
            return;
        }

        this.context.unregisterReceiver(this.receiver);
        this.receiver = null;

    }

}
