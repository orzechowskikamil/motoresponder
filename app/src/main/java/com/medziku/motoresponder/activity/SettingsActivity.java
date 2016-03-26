package com.medziku.motoresponder.activity;


import android.content.Intent;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.pseudotesting.IntegrationRunner;
import com.medziku.motoresponder.pseudotesting.UtilitiesRunner;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.services.BackgroundService;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;


/**
 * This activity is UI of this application.
 * Main functionality of application doesn't have UI, so only UI of the app is settings panel of application.
 */
public class SettingsActivity extends PreferenceActivity {


    private SharedPreferencesUtility sharedPreferencesUtility;
    private Settings settings;
    EditTextPreference autoResponseMessageControl;
    private SwitchPreference responderEnabledControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.handleControls();


        this.sharedPreferencesUtility = new SharedPreferencesUtility(this);
        this.settings = new Settings(this.sharedPreferencesUtility);

        this.runBackgroundProcessOrPseudotests();
    }

    private void handleControls() {
        this.addPreferencesFromResource(R.xml.background_prefs);

        this.autoResponseMessageControl = (EditTextPreference) this.findPreference(this.getString(R.string.auto_response_to_sms_template_control_id));
        this.responderEnabledControl = (SwitchPreference) this.findPreference(this.getString(R.string.responder_enabled_control_id));

        this.autoResponseMessageControl.setDefaultValue(this.settings.isResponderEnabled());
        this.autoResponseMessageControl.setDefaultValue(this.settings.getAutoResponseToSmsTemplate());

        this.autoResponseMessageControl.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsActivity.this.settings.setAutoResponseToSmsTemplate((String) newValue);
                return false;
            }
        });

        this.responderEnabledControl.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                SettingsActivity.this.settings.setResponderEnabled((Boolean) newValue);
                return false;
            }
        });
    }

    private void runBackgroundProcessOrPseudotests() {
        if (this.arePseudoTestsEnabled()) {
            this.runPseudoTesting();
        } else if (this.areIntegrationPseudoTestsEnabled()) {
            this.runPseudoIntegrationTesting();
        } else {
            this.toggleBackgroundServiceAccordingToSettings();
            this.toggleBackgroundServiceOnSettingChange();
        }
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
        new IntegrationRunner(this.getApplicationContext()).run();
    }

    private boolean arePseudoTestsEnabled() {
        return UtilitiesRunner.ARE_PSEUDOTESTS_ENABLED == true;
    }

    private boolean areIntegrationPseudoTestsEnabled() {
        return IntegrationRunner.ARE_INTEGRATION_TESTS_ENABLED == true;
    }
}
