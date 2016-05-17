package com.medziku.motoresponder.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.*;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

public class RidingSettingsPreferenceFragment extends RidingSettingsPreferenceFragmentDefinition {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.settings.listenToSensorCheckEnabledChange(new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                RidingSettingsPreferenceFragment.this.manageEnabledState();
                return true;
            }
        });

        this.settings.listenToResponderEnabledChange(new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                RidingSettingsPreferenceFragment.this.manageEnabledState();
                return false;
            }
        });

        this.manageEnabledState();
    }

    protected void manageEnabledState() {
        boolean responderEnabled = this.settings.isResponderEnabled();

        this.getIsRidingAssumedPreference().setEnabled(!this.settings.isSensorCheckEnabled() && responderEnabled);
        this.getSensorCheckEnabledPreference().setEnabled(responderEnabled);
        this.getWaitBeforeResponsePreference().setEnabled(responderEnabled);
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

    private Preference findPreferenceByID(int preferenceID) {
        return this.findPreference(sharedPreferencesUtility.getStringFromRes(preferenceID));
    }

    protected SwitchPreference getIsRidingAssumedPreference() {
        return (SwitchPreference) this.findPreferenceByID(R.string.is_riding_assumed_key);
    }

    protected SwitchPreference getSensorCheckEnabledPreference() {
        return (SwitchPreference) this.findPreferenceByID(R.string.sensor_check_enabled_key);
    }

    protected EditTextPreference getWaitBeforeResponsePreference() {
        return (EditTextPreference) this.findPreferenceByID(R.string.wait_before_response_key);
    }

}


