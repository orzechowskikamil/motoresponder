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
public class SoundsPreferencesFragment extends PreferenceFragment implements
        SharedPreferences.OnSharedPreferenceChangeListener {//TODO change name

    private SoundEnabler mSoundEnabler;

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        PreferenceManager.getDefaultSharedPreferences(getActivity())
                .registerOnSharedPreferenceChangeListener(this);

        addPreferencesFromResource(R.xml.sounds_prefs);//TODO change name

        Activity activity = getActivity();
        ActionBar actionbar = activity.getActionBar();
        Switch actionBarSwitch = new Switch(activity);

        actionbar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                ActionBar.DISPLAY_SHOW_CUSTOM);
        actionbar.setCustomView(actionBarSwitch, new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL
                | Gravity.RIGHT));

        actionbar.setTitle("Sounds options");

        mSoundEnabler = new SoundEnabler(getActivity(), actionBarSwitch);
        updateSettings();
    }

    public void onResume() {
        super.onResume();
        mSoundEnabler.resume();
        updateSettings();
    }

    public void onPause() {
        super.onPause();
        mSoundEnabler.pause();
    }

    protected void updateSettings() {
        boolean available = mSoundEnabler.isSwitchOn();

        int count = getPreferenceScreen().getPreferenceCount();
        for (int i = 0; i < count; ++i) {
            Preference pref = getPreferenceScreen().getPreference(i);
            pref.setEnabled(available);
        }
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals("SOUND_ENABLED"))
            updateSettings();
    }
}
