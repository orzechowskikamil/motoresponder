package com.medziku.motoresponder.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.medziku.motoresponder.utils.utilitiesrunner.UtilitiesRunner;
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

        if (UtilitiesRunner.ARE_PSEUDOTESTS_ENABLED == true) {
            this.runPseudoTesting();
        } else {
            this.startBackgroundService();
        }
    }

    private void startBackgroundService() {
        Intent backgroundServiceStarter = new Intent(this, BackgroundService.class);
        this.startService(backgroundServiceStarter);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        log.info("SharedPrefsKeyChanged: {}", key);

        if (key.equals(SettingsUtility.RESPONDER_SERVICE_ENABLED_KEY)) {
            Intent intent = new Intent(this, BackgroundService.class);
            boolean serviceEnabled = sharedPreferences.getBoolean(key, false);
            if (serviceEnabled) {
                startService(intent);
            } else {
                stopService(intent);
            }
        }
    }

    /**
     * Special mode in which app instead of doing it's work, run this method to start utilities on real device.
     * Because there is no possibility of good testing utilities by unit tests or instrumented tests.
     */
    private void runPseudoTesting() {
        new UtilitiesRunner(this.getApplicationContext()).run();
    }
}
