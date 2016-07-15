package com.medziku.motoresponder.logic;

import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.IntentsUtility;
import com.medziku.motoresponder.widgets.OnOffWidgetProvider;

public class OnOffWidgetHandler {

    private final IntentsUtility intentsUtility;
    protected Settings settings;
    protected Context context;

    protected BroadcastReceiver receiver;
    private Predicate<Boolean> isRidingAssumedSettingChangeCallback;

    public OnOffWidgetHandler(Context context, Settings settings, IntentsUtility intentsUtility) {
        this.settings = settings;
        this.context = context;
        this.intentsUtility = intentsUtility;
    }

    // TODO K. Orzechowski: write unit tests later
    // TODO K. Orzechowski: beautify widget - it has gray background and still too small icon.
    // TODO K. Orzechowski: icon doesn't have preview in widget gallery - fix


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
                OnOffWidgetHandler.this.handleWidgetTap();
            }
        };

        this.context.registerReceiver(receiver, this.intentsUtility.createIntentFilter(OnOffWidgetProvider.ACTION_WIDGET_TAP));
    }

    private void listenToIsRidingAssumedChangeInSettings() {
        this.isRidingAssumedSettingChangeCallback = new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                OnOffWidgetHandler.this.updateWidget();
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
        Intent intent = this.intentsUtility.createIntent(this.context, OnOffWidgetProvider.class);
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
