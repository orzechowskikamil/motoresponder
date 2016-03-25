package com.medziku.motoresponder.pseudotesting.utilities;


import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.SettingsUtility;

public class SettingsUtilityTest {

    private static final String TAG = "SettingsUtilityTest";
    private Context context;
    private SettingsUtility settingsUtility;

    public SettingsUtilityTest(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.settingsUtility = new SettingsUtility(this.context);
    }

    public void testSettingResponseText() {
        this.setUp();

        Log.d(TAG, "This test will store setting in SettingsUtility. Beware - it can change your real settings on your device.");

        String exampleResponseText = "Example response text";
        this.settingsUtility.setAutoResponseText(exampleResponseText);

        String responseTextFromSettings = this.settingsUtility.getAutoResponseText();

        Log.d(TAG, "Response text stored in settings = '" + exampleResponseText + "', text read from settings = '" + responseTextFromSettings + "'");
    }

    public void testIfResponseTextInSettingsIsUpdatedWithUI() {
        this.setUp();

        Log.d(TAG, "Try to put new response text in application settings UI and watch here if it will be modified");
        (new Thread() {
            public void run() {
                do {
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String responseText = SettingsUtilityTest.this.settingsUtility.getAutoResponseText();
                    Log.d(TAG, "Currently response text stored in settings is: " + responseText);
                } while (true);
            }
        }).start();
    }
}
