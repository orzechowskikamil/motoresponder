package com.medziku.motoresponder.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import com.google.common.base.Function;
import com.medziku.motoresponder.R;


/**
 * This class exposes methods for dealing with shared preferences API + resources.
 */
public class SharedPreferencesUtility {

    private Context context;
    private SharedPreferences sharedPreferences;
    private Resources resources;
    private SharedPreferences.OnSharedPreferenceChangeListener sharedListener;

    /**
     * This is the real constructor
     *
     * @param context Activity context
     */
    public SharedPreferencesUtility(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        this.resources = this.context.getResources();
    }


    public boolean getBooleanValue(String key, boolean defValue) {
        return this.sharedPreferences.getBoolean(key, defValue);
    }


    public String getStringValue(String key, String defValue) {
        return this.sharedPreferences.getString(key, defValue);
    }


    public int getIntValue(String key, int defValue) {
        // no standard preference UI controls are capable of saving int into preferences, so we need to make a conversion from/to string.
        String stringValue = this.sharedPreferences.getString(key, Integer.toString(defValue));
        int integerValue = Integer.parseInt(stringValue);
        return integerValue;
    }


    public void setBooleanValue(String key, boolean value) {
        boolean result = this.sharedPreferences.edit().putBoolean(key, value).commit();
        if (result == false) {
            throw new RuntimeException("Failure during saving boolean value with key '" + key + "'");
        }
    }

    public void setStringValue(String key, String value) {
        boolean result = this.sharedPreferences.edit().putString(key, value).commit();
        if (result == false) {
            throw new RuntimeException("Failure during saving string value with key '" + key + "'");
        }
    }


    public void setIntValue(String key, int value) {
        boolean result = this.sharedPreferences.edit().putString(key, Integer.toString(value)).commit();
        if (result == false) {
            throw new RuntimeException("Failure during saving int value with key '" + key + "'");
        }
    }

    public void listenToSharedPreferenceChanged(final Function<String, Boolean> listener) {
        if (this.sharedListener != null) {
            return;
        }
        this.sharedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                listener.apply(key);
            }
        };
        this.sharedPreferences.registerOnSharedPreferenceChangeListener(this.sharedListener);
    }

    public void stopListeningToSharedPreferenceChanged() {
        if (this.sharedListener == null) {
            return;
        }
        this.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this.sharedListener);
        this.sharedListener = null;
    }

    public int getIntegerFromRes(String name) {
        String resourceName = "@integer/" + name;
        int id = this.resources.getIdentifier(resourceName, "int", this.context.getPackageName());
        if (id == 0) {
            throw new RuntimeException("Integer '" + name + "' not found in resources.");
        }
        return this.getIntegerFromRes(id);
    }


    public boolean getBooleanFromRes(String name) {
        String resourceName = "@bool/" + name;
        int id = this.resources.getIdentifier(resourceName, "bool", this.context.getPackageName());
        if (id == 0) {
            throw new RuntimeException("Bool '" + name + "' not found in resources.");
        }
        return this.getBooleanFromRes(id);
    }


    public String getStringFromRes(String name) {
        String resourceName = "@string/" + name;
        int id = this.resources.getIdentifier(resourceName, "string", this.context.getPackageName());
        if (id == 0) {
            throw new RuntimeException("String '" + name + "' not found in resources.");
        }
        return this.getStringFromRes(id);
    }

    public int getIntegerFromRes(int id) {
        return this.resources.getInteger(id);
    }

    public boolean getBooleanFromRes(int id) {
        return this.resources.getBoolean(id);
    }

    public String getStringFromRes(int id) {
        return this.resources.getString(id);
    }


}
