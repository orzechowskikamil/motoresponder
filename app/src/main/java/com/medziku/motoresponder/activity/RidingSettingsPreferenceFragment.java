package com.medziku.motoresponder.activity;

import android.os.Bundle;
import android.preference.*;
import com.medziku.motoresponder.R;

public class RidingSettingsPreferenceFragment extends PreferenceFragment {

    @Override  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.addPreferencesFromResource(R.xml.riding_settings_preference_fragment);
    }
 
}
