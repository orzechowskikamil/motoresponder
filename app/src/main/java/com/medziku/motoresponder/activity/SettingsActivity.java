package com.medziku.motoresponder.activity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.Switch;

import com.medziku.motoresponder.R;
import com.medziku.motoresponder.services.BackgroundService;
import com.medziku.motoresponder.ui.MyPrefsHeaderAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by medziku on 27.09.15.
 */
public class SettingsActivity extends PreferenceActivity {

    private List<Header> mHeaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Add a button to the header list.
        /*if (hasHeaders()) {
            CompoundButton button = new Switch(this);
            button.setText("Włącz usługę w tle");
            //TODO set default value

            button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Intent serviceIntent = new Intent(getApplicationContext(), BackgroundService.class);
                        startService(serviceIntent);
                    } else {
                        Intent serviceIntent = new Intent(getApplicationContext(), BackgroundService.class);
                        stopService(serviceIntent);
                    }
                }
            });

            setListFooter(button);
        }*/
    }

    protected void onResume() {
        super.onResume();

        setTitle("Settings");

        if (getListAdapter() instanceof MyPrefsHeaderAdapter)
            ((MyPrefsHeaderAdapter) getListAdapter()).resume();
    }

    protected void onPause() {
        super.onPause();
        if (getListAdapter() instanceof MyPrefsHeaderAdapter)
            ((MyPrefsHeaderAdapter) getListAdapter()).pause();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.preference_headers, target);

        mHeaders = target;
    }

    public void setListAdapter(ListAdapter adapter) {
        int i, count;

        if (mHeaders == null) {
            mHeaders = new ArrayList<Header>();
            // When the saved state provides the list of headers,
            // onBuildHeaders is not called
            // so we build it from the adapter given, then use our own adapter

            count = adapter.getCount();
            for (i = 0; i < count; ++i)
                mHeaders.add((Header) adapter.getItem(i));
        }

        super.setListAdapter(new MyPrefsHeaderAdapter(this, mHeaders));
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }

    public static class Prefs1Fragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            //TODO ponizsze moze byc potrzebne
            /*PreferenceManager.setDefaultValues(getActivity(),
                    R.xml.advanced_preferences, false);*/

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.general_preferences);
        }
    }

    public static class Prefs2Fragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Can retrieve arguments from preference XML.
            Log.i("args", "Arguments: " + getArguments());

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragmented_preferences_inner);
        }
    }
}
