package com.medziku.motoresponder.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.*;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.CountryPrefix;
import com.medziku.motoresponder.logic.CustomLog;
import com.medziku.motoresponder.logic.NumberRules;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

import java.util.ArrayList;
import java.util.List;


public class NumberRulesPreferenceFragment extends NumberRulesPreferenceFragmentsDefinition {

    private Predicate<Boolean> responderEnabledKeyCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setVisibilityOfPhoneNumberField();

        this.hideRespondingRestrictedToCurrentCountryIfDeviceNotAbleToFilter();

        this.setupList(this.getWhiteListGroupNamePreference(), this.settings.getStringFromRes(this.settings.DONT_USE_WHITELIST_TEXT_RES_ID));
        this.setupList(this.getBlackListGroupNamePreference(), this.settings.getStringFromRes(this.settings.DONT_USE_BLACKLIST_TEXT_RES_ID));
        this.setupList(this.getGeolocationWhiteListGroupNamePreference(), this.settings.getStringFromRes(this.settings.DONT_USE_GEOLOCATION_WHITELIST_TEXT_RES_ID));


        this.responderEnabledKeyCallback = new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                NumberRulesPreferenceFragment.this.manageDisabledState();
                return false;
            }
        };

        this.settings.listenToSettingChange(this.settings.RESPONDER_ENABLED_KEY, this.responderEnabledKeyCallback);

        this.manageDisabledState();
    }

    @Override
    public void onDestroy() {
        this.settings.stopListeningToSetting(this.settings.RESPONDER_ENABLED_KEY, this.responderEnabledKeyCallback);
        super.onDestroy();
    }

    private void setVisibilityOfPhoneNumberField() {
        if (this.deviceIsAbleToReadCurrentPhoneNumber()) {
            this.getPreferenceScreen().removePreference(this.getDevicePhoneNumberPreference());
        }
    }

    private void setVisibilityOfDelayField() {
        if (this.settings.getMethodOfLimitingResponses() != this.settings.METHOD_OF_LIMITING_RESPONSES_TIME_BASED) {
            this.getPreferenceScreen().removePreference(this.getDelayBetweenResponsesPreference());
        }
    }

    private void hideRespondingRestrictedToCurrentCountryIfDeviceNotAbleToFilter() {
        if (!this.numberRules.isAbleToFilterForeignNumbers()) {
            this.getPreferenceScreen().removePreference(this.getRespondingRestrictedToCountryPreference());
        }
    }

    private boolean deviceIsAbleToReadCurrentPhoneNumber() {
        return this.contactsUtility.isAbleToReadCurrentDeviceNumber();
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

        SwitchPreference respondingRestrictedToCountryPreference = this.getRespondingRestrictedToCountryPreference();
        if (respondingRestrictedToCountryPreference != null) {
            respondingRestrictedToCountryPreference.setEnabled(responderEnabled);
        }
        this.getRespondingRestrictedToContactListPrerefence().setEnabled(responderEnabled);
        this.getWhiteListGroupNamePreference().setEnabled(responderEnabled);
        this.getBlackListGroupNamePreference().setEnabled(responderEnabled);
        this.getGeolocationWhiteListGroupNamePreference().setEnabled(responderEnabled && geolocationEnabled);

        EditTextPreference devicePhoneNumberPreference = this.getDevicePhoneNumberPreference();
        if (devicePhoneNumberPreference != null) {
            devicePhoneNumberPreference.setEnabled(responderEnabled);
        }
    }
}

abstract class NumberRulesPreferenceFragmentsDefinition extends PreferenceFragment {

    public NumberRules numberRules;
    protected Settings settings;
    protected ContactsUtility contactsUtility;
    protected Context context;
    protected SharedPreferencesUtility sharedPreferencesUtility;
    protected CountryPrefix countryPrefix;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addPreferencesFromResource(R.xml.number_rules_preference_fragment);

        this.context = this.getActivity().getApplicationContext();
        this.contactsUtility = new ContactsUtility(this.context);
        this.sharedPreferencesUtility = new SharedPreferencesUtility(this.context);
        this.settings = new Settings(this.sharedPreferencesUtility);
        this.countryPrefix = new CountryPrefix(this.contactsUtility);
        CustomLog log = new CustomLog(this.settings);

        this.numberRules = new NumberRules(this.contactsUtility, this.countryPrefix, this.settings, log);

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

    public SwitchPreference getRespondingRestrictedToCountryPreference() {
        return (SwitchPreference) this.findPreferenceByID(R.string.responding_restricted_to_current_country_key);
    }

    public EditTextPreference getDevicePhoneNumberPreference() {
        return (EditTextPreference) this.findPreferenceByID(R.string.device_phone_number_key);
    }

    protected ListPreference getDelayBetweenResponsesPreference() {
        return (ListPreference) this.findPreferenceByID(R.string.delay_between_responses_minutes_key);
    }

    private Preference findPreferenceByID(int preferenceID) {
        return this.findPreference(sharedPreferencesUtility.getStringFromRes(preferenceID));
    }
}
