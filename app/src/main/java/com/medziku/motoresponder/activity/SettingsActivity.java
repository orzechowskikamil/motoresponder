package com.medziku.motoresponder.activity;


import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.pseudotesting.IntegrationRunner;
import com.medziku.motoresponder.pseudotesting.UtilitiesRunner;
import com.medziku.motoresponder.services.BackgroundService;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

import java.util.List;


/**
 * This activity is UI of this application.
 * Main functionality of application doesn't have UI, so only UI of the app is settings panel of application.
 */
public class SettingsActivity extends PreferenceActivity {

    private SharedPreferencesUtility sharedPreferencesUtility;
    private Settings settings;
    private Predicate<Boolean> responderEnabledSettingChangedCallback;

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

        this.onGatheringFocus();

        this.tryShowWizardIfRequired();
        this.tryShowDisclaimerIfRequired();
    }

    @Override
    protected void onPause() {
        this.onLoosingFocus();
        super.onPause();
    }


    // TODO K. Orzechowski: divide it to app launcher and activity.


    @Override
    protected void onResume() {
        this.onGatheringFocus();
        super.onResume();
    }

    protected void onStop() {
        this.onLoosingFocus();
        super.onStop();
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        // making this check only introduce errors when adding fragment. ignore it.
        return true;
    }

    protected void acceptTermsAndConditions() {
        this.settings.setTermsAndCondition(true);
        this.settings.setResponderEnabled(true);
    }

    protected void rejectTermsAndConditions() {
        this.settings.setTermsAndCondition(false);
        this.settings.setResponderEnabled(false);
        this.killApplication();
    }

    private void onGatheringFocus() {
        this.runBackgroundProcessOrPseudotests();
    }

    private void onLoosingFocus() {
        this.stopListeningToChangeResponderEnabledSetting();
    }

    private void stopListeningToChangeResponderEnabledSetting() {
        if (this.settings != null) {
            this.settings.stopListeningToSetting(this.settings.RESPONDER_ENABLED_KEY, this.responderEnabledSettingChangedCallback);
        }
    }


    private void startWizard() {
        this.startActivity(new Intent(this, WizardActivity.class));
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
        this.listenToChangeResponderEnabledSetting();
    }

    private void listenToChangeResponderEnabledSetting() {
        if (this.responderEnabledSettingChangedCallback != null) {
            this.stopListeningToChangeResponderEnabledSetting();
        }

        this.responderEnabledSettingChangedCallback = new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                SettingsActivity.this.toggleBackgroundServiceAccordingToSettings();
                return false;
            }
        };

        this.settings.listenToSettingChange(this.settings.RESPONDER_ENABLED_KEY, this.responderEnabledSettingChangedCallback);
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

    private void killApplication() {
        System.exit(0);
    }

    private void showPopupWithDisclaimer() {
        Builder dialogBuilder = new Builder(this);

        String disclaimerTitleText = this.sharedPreferencesUtility.getStringFromRes(R.string.disclaimer_title);
        String disclaimerMessageText = this.sharedPreferencesUtility.getStringFromRes(R.string.disclaimer_message);
        String acceptBtnText = this.sharedPreferencesUtility.getStringFromRes(R.string.disclaimer_accept);
        String rejectBtnText = this.sharedPreferencesUtility.getStringFromRes(R.string.disclaimer_reject);

        dialogBuilder.setTitle(disclaimerTitleText);
        dialogBuilder.setMessage(disclaimerMessageText);
        dialogBuilder.setPositiveButton(acceptBtnText, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SettingsActivity.this.acceptTermsAndConditions();
                SettingsActivity.this.tryShowWizardIfRequired();
            }
        });

        dialogBuilder.setNegativeButton(rejectBtnText, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SettingsActivity.this.rejectTermsAndConditions();
            }
        });
        dialogBuilder.create().show();
    }

    private boolean shouldWizardBeShown() {
        return this.settings.isTermsAndConditionAccepted() && !this.settings.isWizardCompleted();
    }

    private void tryShowWizardIfRequired() {
        if (this.shouldWizardBeShown()) {
            this.startWizard();
        }
    }


    private void tryShowDisclaimerIfRequired() {
        if (this.shouldDisclaimerBeShown()) {
            this.showPopupWithDisclaimer();
        }
    }

    private boolean shouldDisclaimerBeShown() {
        return (!this.settings.isTermsAndConditionAccepted());
    }


}