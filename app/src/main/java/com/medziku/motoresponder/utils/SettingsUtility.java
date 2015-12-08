package com.medziku.motoresponder.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * This class exposes settings of application and totally hide process of storing them.
 */
public class SettingsUtility {

    private final String APP_SHARED_PREFERENCES = "AppSharedPreferences";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private String SERVICE_ENABLED = "service-enabled";

    public SettingsUtility(Context context) {
        this.sharedPreferences = context.getSharedPreferences(this.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
    }

    private boolean getValue(String name, boolean defaultValue) {
        return this.sharedPreferences.getBoolean(name, defaultValue);
    }

    private void setValue(String name, boolean value) {
        this.editor.putBoolean(name, value);
        this.editor.commit();
    }

    private String getValue(String name, String defaultValue) {
        return this.sharedPreferences.getString(name, defaultValue);
    }

    private void setValue(String name, String value) {
        this.editor.putString(name, value);
        this.editor.commit();
    }

    private int getValue(String name, int defaultValue) {
        return this.sharedPreferences.getInt(name, defaultValue);
    }

    private void setValue(String name, int value) {
        this.editor.putInt(name, value);
        this.editor.commit();
    }


    /**
     * If true, it means that autoesponding service is enabled. If not, it's disabled (whole app shouldn't work).
     * @return
     */
    public boolean isServiceEnabled() {
        return this.getValue(SERVICE_ENABLED, true);
    }

    /**
     * For changing setting of service enabled or disabled.
     * TODO K. Orzechowski:  it should stop/start service or not?
     * @param value
     */
    public void setServiceEnabled(boolean value) {
        this.setValue(SERVICE_ENABLED, value);
    }


    /**
     * Return stored text of auto response for SMS message.
     * @return
     */
    public String getAutoResponseTextForSMS() {
        return "(Automatyczna odpowiedz) Czesc, jezdze wlasnie motocyklem, odezwe sie jak skonczy mi sie paliwo.";
    }

    /**
     * Should we treat phone unlocked as not riding or not?
     * @return
     */
    public boolean isPhoneUnlockedInterpretedAsNotRiding() {
        return true;
    }


    /**
     * Should we display notification when motoresponder is measuring if user is riding or not?
     * @return
     */
    public boolean isShowingPendingNotificationEnabled() {
        return true;
    }
}
