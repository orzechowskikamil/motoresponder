package com.medziku.motoresponder.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.medziku.motoresponder.R;
import com.medziku.motoresponder.services.BackgroundService;
import com.medziku.motoresponder.utils.SettingsUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This activity is UI of this application.
 * Main functionality of application doesn't have UI, so only UI of the app is settings panel of application.
 */
public class SettingsActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final Logger log = LoggerFactory.getLogger(SettingsActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.background_prefs);
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        log.info("SharedPrefsKeyChanged: {}", key);

        if (key.equals(SettingsUtility.RESPONDER_SERVICE_ENABLED)) {
            Intent intent = new Intent(this, BackgroundService.class);
            boolean serviceEnabled = sharedPreferences.getBoolean(key, false);
            if (serviceEnabled) {
                startService(intent);
            } else {
                stopService(intent);
            }
        }
    }
}
