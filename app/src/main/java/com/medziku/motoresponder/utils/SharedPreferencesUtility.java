package com.medziku.motoresponder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import com.google.common.base.Function;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.pseudotesting.IntegrationRunner;


/**
 * This class exposes methods for dealing with shared preferences API + resources.
 */
public class SharedPreferencesUtility {

    private static final String APP_SHARED_PREFERENCES = "AppSharedPreferences";
    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Resources resources;

    /**
     * This is the real constructor
     *
     * @param context Activity context
     */
    public SharedPreferencesUtility(Context context) {
        this.sharedPreferences = context.getSharedPreferences(APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();
        this.context = context;
        this.resources = this.context.getResources();
    }


    public boolean getBooleanValue(String key) {
        return this.sharedPreferences.getBoolean(key, this.getBooleanFromRes(this.getDefaultValueName(key)));
    }


    public String getStringValue(String key) {
        return this.sharedPreferences.getString(key, this.getStringFromRes(this.getDefaultValueName(key)));
    }


    public int getIntValue(String key) {
        return this.sharedPreferences.getInt(key, this.getIntFromRes(this.getDefaultValueName(key)));
    }


    public void setBooleanValue(String key, boolean value) {
        this.editor.putBoolean(key, value);
        this.editor.commit();
    }

    public void setStringValue(String key, String value) {
        this.editor.putString(key, value);
        this.editor.commit();
    }


    public void setIntValue(String key, int value) {
        this.editor.putInt(key, value);
        this.editor.commit();
    }

    // TODO K. Orzechowski: #104 test this with pseudotest.
    public void listenToSharedPreferenceChanged(final Function<String, Boolean> listener) {
        this.sharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                listener.apply(key);
            }
        });
    }

    public int getIntFromRes(String name) {
        return this.resources.getInteger(this.resources.getIdentifier(name, "int", this.context.getPackageName()));
    }

    public boolean getBooleanFromRes(String name) {
        return this.resources.getBoolean(this.resources.getIdentifier(name, "bool", this.context.getPackageName()));
    }

    public String getStringFromRes(String name) {
        return this.resources.getString(this.resources.getIdentifier(name, "string", this.context.getPackageName()));
    }

    private String getDefaultValueName(String name) {
        return name + "_default_value";
    }

}
