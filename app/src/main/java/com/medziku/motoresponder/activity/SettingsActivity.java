package com.medziku.motoresponder.activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.ListAdapter;

import com.medziku.motoresponder.R;
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
    }

    protected void onResume() {
        super.onResume();

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
}
