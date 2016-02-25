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


    public boolean isServiceEnabled() {
        return this.getValue(SERVICE_ENABLED, true);
    }

    public void setServiceEnabled(boolean value) {
        this.setValue(SERVICE_ENABLED, value);
    }

    public String getAutoResponseTextForSMS() {
        return "(Automatyczna odpowiedz) Czesc, jezdze wlasnie motocyklem, odezwe sie jak skonczy mi sie paliwo.";
    }

}
