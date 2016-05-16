package com.medziku.motoresponder.activity;

import android.os.Bundle;
import android.preference.*;
import com.medziku.motoresponder.R;


public class NumberRulesPreferenceFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.number_rules_preference_fragment);
    }

}
