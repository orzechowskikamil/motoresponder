package com.medziku.motoresponder.activity;


import android.content.Intent;
import android.os.Bundle;
import android.preference.*;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.pseudotesting.IntegrationRunner;
import com.medziku.motoresponder.pseudotesting.UtilitiesRunner;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.services.BackgroundService;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

import java.util.List;


/**
 * This activity is UI of this application.
 * Main functionality of application doesn't have UI, so only UI of the app is settings panel of application.
 */
public class SettingsActivity extends PreferenceActivity {
    public static final String SETTINGS_PREFERENCE_FRAGMENT_NAME = "com.medziku.motoresponder.activity.SettingsPreferenceFragment";
    public static final String RIDING_SETTINGS_PREFERENCE_FRAGMENT_NAME = "com.medziku.motoresponder.activity.RidingSettingsPreferenceFragment";
    private SharedPreferencesUtility sharedPreferencesUtility;
    private Settings settings;

    @Override
    public void onBuildHeaders(List<Header> target) {
        this.loadHeadersFromResource(R.xml.preference_headers, target);
    }

    /**
     * Please note that this method will be run everytime user will flip an device, or enter settings fragment -
     * because activity will be killed and created again.
     * <p/>
     * So, if some continous operation is needed, it's required to store activity state in Bundle savedInstanceState.
     * <p/>
     * Every time when activity get's recreated (when device is flipped or fragment entered) you get here fresh object
     * so you can't persist in this object anything, because every time you will get fresh object.
     * <p/>
     * Only savedInstanceState is reliable way to store data.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.runBackgroundProcessOrPseudotests();
    }


    private void runBackgroundProcessOrPseudotests() {
        if (this.arePseudoTestsEnabled()) {
            this.runPseudoTesting();
        } else if (this.areIntegrationPseudoTestsEnabled()) {
            this.runPseudoIntegrationTesting();
        } else {
            this.runBackgroundService();
        }
    }

    private void runBackgroundService() {
        this.sharedPreferencesUtility = new SharedPreferencesUtility(this);
        this.settings = new Settings(this.sharedPreferencesUtility);

        this.toggleBackgroundServiceAccordingToSettings();
        this.toggleBackgroundServiceOnSettingChange();
    }

    private void toggleBackgroundServiceOnSettingChange() {
        this.settings.listenToResponderEnabledChange(new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                SettingsActivity.this.toggleBackgroundServiceAccordingToSettings();
                return false;
            }
        });
    }

    protected void onStop() {
        if (this.settings != null) {
            this.settings.stopListening();
        }
        super.onStop();
    }

    /**
     * Get current value from settings
     */
    private void toggleBackgroundServiceAccordingToSettings() {
        this.toggleBackgroundService(this.settings.isResponderEnabled());
    }

    /**
     * Enables or disables background service, by sending intent
     *
     * @param serviceEnabled True for enabled service, false for disabled.
     */
    private void toggleBackgroundService(boolean serviceEnabled) {
        Intent intent = new Intent(this, BackgroundService.class);

        // No worries - you can call startService even if service is already started - and it will be not
        // started again. So it can be called even multiple times without checking if it is already in background.

        if (serviceEnabled) {
            this.startService(intent);
        } else if (!serviceEnabled) {
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
        new IntegrationRunner(this.getApplicationContext()).run();
    }

    private boolean arePseudoTestsEnabled() {
        return UtilitiesRunner.ARE_PSEUDOTESTS_ENABLED == true;
    }

    private boolean areIntegrationPseudoTestsEnabled() {
        return IntegrationRunner.ARE_INTEGRATION_TESTS_ENABLED == true;
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (fragmentName.equals(SETTINGS_PREFERENCE_FRAGMENT_NAME)) {
            return true;
        }
        if (fragmentName.equals(RIDING_SETTINGS_PREFERENCE_FRAGMENT_NAME)) {
            return true;
        }
        return false;
    }

}
