package com.medziku.motoresponder.preferences;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.logic.CustomLog;
import com.medziku.motoresponder.logic.Settings;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

public class LogViewerPreference extends DialogPreference {


    private SharedPreferencesUtility sharedPreferencesUtility;
    private Settings settings;
    private CustomLog customLog;

    public LogViewerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.sharedPreferencesUtility = new SharedPreferencesUtility(context);
        this.settings = new Settings(this.sharedPreferencesUtility);
        this.customLog = new CustomLog(settings);
    }


    @Override
    protected View onCreateDialogView() {
        String logStr = customLog.getLogStr();
        String dialogMessage = null;

        if (logStr == null || logStr.length() == 0) {
            dialogMessage = this.sharedPreferencesUtility.getStringFromRes(R.string.custom_log_empty_message);
        } else {
            dialogMessage = logStr;
        }
        this.setDialogMessage(dialogMessage);
        return super.onCreateDialogView();
    }
}
