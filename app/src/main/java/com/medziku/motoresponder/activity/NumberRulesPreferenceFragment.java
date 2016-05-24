package com.medziku.motoresponder.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.*;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

import java.util.ArrayList;
import java.util.List;


public class NumberRulesPreferenceFragment extends NumberRulesPreferenceFragmentsDefinition {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setupList(this.getWhiteListGroupNamePreference(), this.settings.getDontUseWhitelistText());
        this.setupList(this.getBlackListGroupNamePreference(), this.settings.getDontUseBlacklistText());
        this.setupList(this.getGeolocationWhiteListGroupNamePreference(), this.settings.getDontUseGeolocationWhitelistText());

        this.settings.listenToResponderEnabledChange(new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                NumberRulesPreferenceFragment.this.manageDisabledState();
                return false;
            }
        });

        this.settings.listenToResponderEnabledChange(new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                NumberRulesPreferenceFragment.this.manageDisabledState();
                return false;
            }
        });

        this.manageDisabledState();
    }


    private void setupList(ListPreference listPreference, String dontUseText) {
        List groupNames = contactsUtility.readAllContactBookGroupNames();

        List<String> entriesList = new ArrayList<>(groupNames);
        List<String> entryValuesList = new ArrayList<>(groupNames);

        entriesList.add(0, dontUseText);
        entryValuesList.add(0, "");

        listPreference.setEntries(entriesList.toArray(new String[0]));
        listPreference.setEntryValues(entryValuesList.toArray(new String[0]));
    }


    private void manageDisabledState() {
        boolean responderEnabled = this.settings.isResponderEnabled();
        boolean geolocationEnabled = this.settings.isRespondingWithGeolocationEnabled();

        this.getRespondingRestrictedToContactListPrerefence().setEnabled(responderEnabled);
        this.getWhiteListGroupNamePreference().setEnabled(responderEnabled);
        this.getBlackListGroupNamePreference().setEnabled(responderEnabled);
        this.getGeolocationWhiteListGroupNamePreference().setEnabled(responderEnabled && geolocationEnabled);
        this.getDevicePhoneNumberPreference().setEnabled(responderEnabled);
    }

}

abstract class NumberRulesPreferenceFragmentsDefinition extends PreferenceFragment {

    protected Settings settings;
    protected ContactsUtility contactsUtility;
    protected Context context;
    protected SharedPreferencesUtility sharedPreferencesUtility;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addPreferencesFromResource(R.xml.number_rules_preference_fragment);

        this.context = this.getActivity().getApplicationContext();
        this.contactsUtility = new ContactsUtility(this.context);
        this.sharedPreferencesUtility = new SharedPreferencesUtility(this.context);
        this.settings = new Settings(this.sharedPreferencesUtility);

    }

    public ListPreference getWhiteListGroupNamePreference() {
        return (ListPreference) this.findPreferenceByID(R.string.whitelist_group_name_key);
    }

    public ListPreference getBlackListGroupNamePreference() {
        return (ListPreference) this.findPreferenceByID(R.string.blacklist_group_name_key);
    }


    public ListPreference getGeolocationWhiteListGroupNamePreference() {
        return (ListPreference) this.findPreferenceByID(R.string.geolocation_whitelist_group_name_key);
    }

    public SwitchPreference getRespondingRestrictedToContactListPrerefence() {
        return (SwitchPreference) this.findPreferenceByID(R.string.responding_restricted_to_contact_list_key);
    }


    public EditTextPreference getDevicePhoneNumberPreference() {
        return (EditTextPreference) this.findPreferenceByID(R.string.device_phone_number_key);
    }

    private Preference findPreferenceByID(int preferenceID) {
        return this.findPreference(sharedPreferencesUtility.getStringFromRes(preferenceID));
    }
}
