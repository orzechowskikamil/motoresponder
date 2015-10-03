package com.medziku.motoresponder.ui;

import android.content.Context;
import android.preference.PreferenceActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.medziku.motoresponder.R;

import java.util.List;

/**
 * Created by medziku on 28.09.15.
 */
public class MyPrefsHeaderAdapter extends ArrayAdapter<PreferenceActivity.Header> {

    static final int HEADER_TYPE_CATEGORY = 0;
    static final int HEADER_TYPE_NORMAL = 1;
    static final int HEADER_TYPE_SWITCH = 2;

    private LayoutInflater mInflater;
    private PropertyEnabler mPropertyEnabler;
    private PropertyEnabler mGeneralEnabler;

    public MyPrefsHeaderAdapter(Context context, List<PreferenceActivity.Header> objects) {
        super(context, 0, objects);

        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mPropertyEnabler = new PropertyEnabler(context, new Switch(context), "GENERAL_PREFERENCES_ENABLED");
        mGeneralEnabler = new PropertyEnabler(context, new Switch(context), BackgroundPreferencesFragment.BACKGROUND_SERVICE_ENABLED);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        PreferenceActivity.Header header = getItem(position);
        int headerType = getHeaderType(header);
        View view = null;

        switch (headerType) {
            case HEADER_TYPE_CATEGORY:
                view = mInflater.inflate(android.R.layout.preference_category, parent, false);
                ((TextView) view.findViewById(android.R.id.title)).setText(header.getTitle(getContext()
                        .getResources()));
                break;

            case HEADER_TYPE_SWITCH:
                view = mInflater.inflate(R.layout.preference_header_switch_item, parent, false);

                ((ImageView) view.findViewById(android.R.id.icon)).setImageResource(header.iconRes);
                ((TextView) view.findViewById(android.R.id.title)).setText(header.getTitle(getContext()
                        .getResources()));
                ((TextView) view.findViewById(android.R.id.summary)).setText(header
                        .getSummary(getContext().getResources()));

                if (header.id == R.id.sounds_settings) {
                    mPropertyEnabler.setSwitch((Switch) view.findViewById(R.id.switchWidget));
                }
                if (header.id == R.id.general_settings) {
                    mGeneralEnabler.setSwitch((Switch) view.findViewById(R.id.switchWidget));
                }

                break;

            case HEADER_TYPE_NORMAL:
                view = mInflater.inflate(R.layout.preference_header_item, parent, false);
                ((ImageView) view.findViewById(android.R.id.icon)).setImageResource(header.iconRes);
                ((TextView) view.findViewById(android.R.id.title)).setText(header.getTitle(getContext()
                        .getResources()));
                ((TextView) view.findViewById(android.R.id.summary)).setText(header
                        .getSummary(getContext().getResources()));
                break;
        }

        return view;
    }

    public static int getHeaderType(PreferenceActivity.Header header) {
        if ((header.fragment == null) && (header.intent == null)) {
            return HEADER_TYPE_CATEGORY;
        } else if (header.id == R.id.sounds_settings || header.id == R.id.general_settings) {
            return HEADER_TYPE_SWITCH;
        } else {
            return HEADER_TYPE_NORMAL;
        }
    }

    public void resume() {
        mPropertyEnabler.resume();
        mGeneralEnabler.resume();
    }

    public void pause() {
        mPropertyEnabler.pause();
        mGeneralEnabler.pause();
    }
}
