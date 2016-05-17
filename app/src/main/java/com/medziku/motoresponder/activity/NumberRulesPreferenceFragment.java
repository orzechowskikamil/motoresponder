package com.medziku.motoresponder.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.*;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

import java.util.ArrayList;
import java.util.List;


public class NumberRulesPreferenceFragment extends PreferenceFragment {

    private Settings settings;
    private SharedPreferencesUtility sharedPreferencesUtility;
    private ContactsUtility contactsUtility;
    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.number_rules_preference_fragment);

        this.context = this.getActivity().getApplicationContext();
        this.contactsUtility = new ContactsUtility(this.context);
        this.sharedPreferencesUtility = new SharedPreferencesUtility(this.context);
        this.settings = new Settings(this.sharedPreferencesUtility);


        this.loadWhitelistElements();
        this.loadBlacklistElements();
        this.loadGeolocationWhitelistElements();
    }

    public void loadWhitelistElements() {
        this.setupList(this.getListPreferenceByID(R.string.whitelist_group_name_key), this.settings.getDontUseWhitelistText());
    }

    public void loadBlacklistElements() {
        this.setupList(this.getListPreferenceByID(R.string.blacklist_group_name_key), this.settings.getDontUseBlacklistText());
    }

    public void loadGeolocationWhitelistElements() {
        this.setupList(
                this.getListPreferenceByID(R.string.geolocation_whitelist_group_name_key),
                this.settings.getDontUseGeolocationWhitelistText()
        );
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

    private ListPreference getListPreferenceByID(int listPreferenceID) {
        String whiteListPreferenceKey = sharedPreferencesUtility.getStringFromRes(listPreferenceID);
        return (ListPreference) findPreference(whiteListPreferenceKey);
    }

}
