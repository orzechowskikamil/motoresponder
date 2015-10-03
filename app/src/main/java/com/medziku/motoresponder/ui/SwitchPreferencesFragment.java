package com.medziku.motoresponder.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.widget.Switch;

import com.medziku.motoresponder.R;

/**
 * Created by medziku on 28.09.15.
 */
public class SwitchPreferencesFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {

    public static final String BACKGROUND_SERVICE_ENABLED = "BACKGROUND_SERVICE_ENABLED";
    public static final String GENERAL_PREFERENCES_ENABLED = "GENERAL_PREFERENCES_ENABLED";
    public static final String BACKGROUND = "background";
    public static final String GENERAL = "general";

    private PropertyEnabler mPropertyEnabler;
    private String mKey;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);

        //addPreferencesFromResource(R.xml.background_prefs);

        String settings = getArguments().getString("prefs");
        if (BACKGROUND.equals(settings)) {
            addPreferencesFromResource(R.xml.background_prefs);
            mKey = BACKGROUND_SERVICE_ENABLED;
        } else if (GENERAL.equals(settings)) {
            addPreferencesFromResource(R.xml.general_prefs);
            mKey = GENERAL_PREFERENCES_ENABLED;
        }

        Activity activity = getActivity();
        ActionBar actionbar = activity.getActionBar();
        Switch actionBarSwitch = new Switch(activity);

        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(actionBarSwitch, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
                | Gravity.RIGHT));

        mPropertyEnabler = new PropertyEnabler(getActivity(), actionBarSwitch, BACKGROUND_SERVICE_ENABLED);
        updateSettings();
    }

    public void onResume() {
        super.onResume();
        mPropertyEnabler.resume();
        updateSettings();
    }

    public void onPause() {
        super.onPause();
        mPropertyEnabler.pause();
    }

    protected void updateSettings() {

        boolean available = mPropertyEnabler.isSwitchOn();

        int count = getPreferenceScreen().getPreferenceCount();
        for (int i = 0; i < count; ++i) {
            Preference pref = getPreferenceScreen().getPreference(i);
            pref.setEnabled(available);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(mKey))
            updateSettings();
    }
}
