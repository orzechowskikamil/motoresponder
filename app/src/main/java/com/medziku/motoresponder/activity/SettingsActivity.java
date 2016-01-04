package com.medziku.motoresponder.activity;


import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.medziku.motoresponder.R;


/**
 * This activity is UI of this application.
 * Main functionality of application doesn't have UI, so only UI of the app is settings panel of application.
 */
public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.background_prefs);
        /*Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);*/
    }
}
