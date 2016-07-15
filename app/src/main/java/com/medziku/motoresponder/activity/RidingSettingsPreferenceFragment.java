package com.medziku.motoresponder.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

public class RidingSettingsPreferenceFragment extends RidingSettingsPreferenceFragmentDefinition {

    private Predicate<Boolean> responderEnabledSettingChangedCallback;
    private Predicate<Boolean> sensorCheckSettingChangedCallback;
    private Predicate<Boolean> isRidingAssumedSettingChangedCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.sensorCheckSettingChangedCallback = new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                RidingSettingsPreferenceFragment.this.manageEnabledState();
                return true;
            }
        };

        this.responderEnabledSettingChangedCallback = new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                RidingSettingsPreferenceFragment.this.manageEnabledState();
                return false;
            }
        };

        this.isRidingAssumedSettingChangedCallback = new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                RidingSettingsPreferenceFragment.this.updateIsRidingAssumed();
                return false;
            }
        };

        this.settings.listenToSettingChange(this.settings.SENSOR_CHECK_ENABLED_KEY, this.sensorCheckSettingChangedCallback);
        this.settings.listenToSettingChange(this.settings.RESPONDER_ENABLED_KEY, this.responderEnabledSettingChangedCallback);
        this.settings.listenToSettingChange(this.settings.IS_RIDING_ASSUMED_KEY, this.isRidingAssumedSettingChangedCallback);

        this.manageEnabledState();
    }

    @Override
    public void onDestroy() {
        this.settings.stopListeningToSetting(this.settings.RESPONDER_ENABLED_KEY, this.responderEnabledSettingChangedCallback);
        this.settings.stopListeningToSetting(this.settings.SENSOR_CHECK_ENABLED_KEY, this.sensorCheckSettingChangedCallback);
        this.settings.stopListeningToSetting(this.settings.IS_RIDING_ASSUMED_KEY, this.isRidingAssumedSettingChangedCallback);
        super.onDestroy();
    }

    protected void manageEnabledState() {
        boolean responderEnabled = this.settings.isResponderEnabled();

        this.getSensorCheckEnabledPreference().setEnabled(responderEnabled);
        this.getWaitBeforeResponsePreference().setEnabled(responderEnabled);
        this.getAssumePhoneUnlockedAsNotRidingPreference().setEnabled(responderEnabled);
    }

    private void updateIsRidingAssumed() {
        this.getIsRidingAssumedPreference().setChecked(this.settings.isRidingAssumed());
    }


}

abstract class RidingSettingsPreferenceFragmentDefinition extends PreferenceFragment {

    protected Settings settings;
    protected Context context;
    protected SharedPreferencesUtility sharedPreferencesUtility;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addPreferencesFromResource(R.xml.riding_settings_preference_fragment);

        this.context = this.getActivity().getApplicationContext();
        this.sharedPreferencesUtility = new SharedPreferencesUtility(this.context);
        this.settings = new Settings(this.sharedPreferencesUtility);
    }

    protected SwitchPreference getIsRidingAssumedPreference() {
        return (SwitchPreference) this.findPreferenceByID(R.string.is_riding_assumed_key);
    }

    protected SwitchPreference getSensorCheckEnabledPreference() {
        return (SwitchPreference) this.findPreferenceByID(R.string.sensor_check_enabled_key);
    }

    protected ListPreference getWaitBeforeResponsePreference() {
        return (ListPreference) this.findPreferenceByID(R.string.wait_before_response_key);
    }

    protected SwitchPreference getAssumePhoneUnlockedAsNotRidingPreference() {
        return (SwitchPreference) this.findPreferenceByID(R.string.assume_screen_unlocked_as_not_riding_key);
    }

    private Preference findPreferenceByID(int preferenceID) {
        return this.findPreference(sharedPreferencesUtility.getStringFromRes(preferenceID));
    }
}


