package com.medziku.motoresponder.widgets;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

public class OnOffWidgetProvider extends AppWidgetProvider {

    // TODO Rename to WIDGET TOOGLE or WIDGET TAP
    public static String ACTION_WIDGET_TAP = "com.medziku.motoresponder.widgets.OnOffWidgetProvider.ACTION_WIDGET_TAP";

    public void onReceive(Context context, Intent intent) {
        this.updateWidgetToActualValue(context);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        this.updateWidgetToActualValue(context);
    }

    private void updateWidgetToActualValue(Context context) {
        Settings settings = this.getSettings(context);

        if (!settings.isResponderEnabled()) {
            this.updateWidgetToDisabled(context);
        } else {
            if (settings.isRidingAssumed()) {
                this.updateWidgetToOn(context);
            } else {
                this.updateWidgetToOff(context);
            }
        }
    }

    private Settings getSettings(Context context) {
        return new Settings(new SharedPreferencesUtility(context));
    }

    private void updateWidgetToOff(Context context) {
        this.updateWidget(context, R.layout.riding_assumed_widget_off);
    }

    private void updateWidgetToDisabled(Context context) {
        // todo change
        this.updateWidget(context, R.layout.riding_assumed_widget_disabled);
    }

    private void updateWidgetToOn(Context context) {
        this.updateWidget(context, R.layout.riding_assumed_widget_on);
    }


    private void updateWidget(Context context, int layoutID) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                layoutID);

        Intent intent = new Intent(OnOffWidgetProvider.ACTION_WIDGET_TAP);
        PendingIntent broadcast = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.actionButton, broadcast);

        ComponentName myWidget = new ComponentName(context, OnOffWidgetProvider.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        manager.updateAppWidget(myWidget, remoteViews);
    }

}

