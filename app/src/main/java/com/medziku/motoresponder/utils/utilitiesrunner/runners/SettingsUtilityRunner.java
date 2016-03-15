package com.medziku.motoresponder.utils.utilitiesrunner.runners;


import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.SettingsUtility;

public class SettingsUtilityRunner {

    private static final String TAG = "SettingsUtilityRunner";
    private Context context;
    private SettingsUtility settingsUtility;

    public SettingsUtilityRunner(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.settingsUtility = new SettingsUtility(this.context);
    }

    public void testSettingResponseText() {
        this.setUp();

        Log.d(TAG, "This test will store setting in SettingsUtility. Beware - it can change your real settings on your device.");

        String exampleResponseText = "Example response text";
        this.settingsUtility.setAutoResponseTextForSMS(exampleResponseText);

        String responseTextFromSettings = this.settingsUtility.getAutoResponseTextForSMS();

        Log.d(TAG, "Response text stored in settings = '" + exampleResponseText + "', text read from settings = '" + responseTextFromSettings + "'");
    }
}
