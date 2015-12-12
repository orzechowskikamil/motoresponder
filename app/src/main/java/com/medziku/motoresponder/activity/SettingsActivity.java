package com.medziku.motoresponder.activity;


import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.ListAdapter;

import com.medziku.motoresponder.R;
import com.medziku.motoresponder.services.BackgroundService;
import com.medziku.motoresponder.ui.PrefsHeaderAdapter;

import java.util.ArrayList;
import java.util.List;


/**
 * This activity is UI of this application.
 * Main functionality of application doesn't have UI, so only UI of the app is settings panel of application.
 */
public class SettingsActivity extends PreferenceActivity {

    private List<Header> mHeaders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
    }

    protected void onResume() {
        super.onResume();

        if (getListAdapter() instanceof PrefsHeaderAdapter)
            ((PrefsHeaderAdapter) getListAdapter()).resume();
    }

    protected void onPause() {
        super.onPause();
        if (getListAdapter() instanceof PrefsHeaderAdapter)
            ((PrefsHeaderAdapter) getListAdapter()).pause();
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
            count = adapter.getCount();
            for (i = 0; i < count; ++i)
                mHeaders.add((Header) adapter.getItem(i));
        }

        super.setListAdapter(new PrefsHeaderAdapter(this, mHeaders));
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return true;
    }
}
