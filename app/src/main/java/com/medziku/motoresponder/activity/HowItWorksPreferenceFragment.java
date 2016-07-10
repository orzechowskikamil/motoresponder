package com.medziku.motoresponder.activity;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.medziku.motoresponder.R;


public class HowItWorksPreferenceFragment extends HowItWorksPreferenceFragmentDefinition {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}

abstract class HowItWorksPreferenceFragmentDefinition extends PreferenceFragment {

    private Context context;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.addPreferencesFromResource(R.xml.how_it_works_preference_fragment);

        this.context = this.getActivity().getApplicationContext();
    }
}
