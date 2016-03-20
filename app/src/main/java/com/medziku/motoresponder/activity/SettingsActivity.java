package com.medziku.motoresponder.activity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.medziku.motoresponder.logic.integrationtesting.IntegrationTester;
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
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addPreferencesFromResource(R.xml.background_prefs);
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.sharedPreferences.registerOnSharedPreferenceChangeListener(this);

        if (this.arePseudoTestsEnabled()) {
            this.runPseudoTesting();
        } else if (this.areIntegrationPseudoTestsEnabled()) {
            this.runPseudoIntegrationTesting();
        } else {
            this.toggleBackgroundServiceAccordingToSettings();
        }
    }

    private boolean areIntegrationPseudoTestsEnabled() {
        return IntegrationTester.ARE_INTEGRATION_TESTS_ENABLED == true;
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        log.info("SharedPrefsKeyChanged: {}", key);

        if (key.equals(SettingsUtility.RESPONDER_SERVICE_ENABLED_KEY)) {
            this.toggleBackgroundServiceAccordingToSettings();
        }
    }


    /**
     * Get current value from settings
     */
    private void toggleBackgroundServiceAccordingToSettings() {
        // TODO K. Orzechowski: Use here settings utility pls. #Issue not needed
        boolean serviceEnabled = this.sharedPreferences.getBoolean(SettingsUtility.RESPONDER_SERVICE_ENABLED_KEY, false);
        this.toggleBackgroundService(serviceEnabled);
    }


    /**
     * Enables or disables background service, by sending intent
     *
     * @param serviceEnabled True for enabled service, false for disabled.
     */
    private void toggleBackgroundService(boolean serviceEnabled) {
        Intent intent = new Intent(this, BackgroundService.class);
        if (serviceEnabled) {
            this.startService(intent);
        } else {
            this.stopService(intent);
        }
    }

    /**
     * Special mode in which app instead of doing it's work, run this method to start utilities on real device.
     * Because there is no possibility of good testing utilities by unit tests or instrumented tests.
     */
    private void runPseudoTesting() {
        new UtilitiesRunner(this.getApplicationContext()).run();
    }

    /**
     * Special mode in which app instead of doing it's work, run this method to check some part of the application
     * if it run correctly or not. Because it's look like it's not good and easy way to reliable test application
     * integration by instrumented tests
     */
    private void runPseudoIntegrationTesting() {
        new IntegrationTester(this.getApplicationContext()).run();
    }

    private boolean arePseudoTestsEnabled() {
        return UtilitiesRunner.ARE_PSEUDOTESTS_ENABLED == true;
    }
}
