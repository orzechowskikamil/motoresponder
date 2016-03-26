package com.medziku.motoresponder.pseudotesting.utilities;


import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

public class SharedPreferencesUtilityTest {

    private static final String TAG = "SharedPrefUtilityTest";
    private Context context;
    private SharedPreferencesUtility sharedPreferencesUtility;

    public SharedPreferencesUtilityTest(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.sharedPreferencesUtility = new SharedPreferencesUtility(this.context);
    }

    public void testSettingResponseText() {
        this.setUp();

        Log.d(TAG, "This test will store setting in SharedPreferencesUtility. Beware - it can change your real settings on your device.");

        String exampleResponseText = "Example response text";

        this.sharedPreferencesUtility.setStringValue("test_key", "test string");
        String responseTextFromSettings = this.sharedPreferencesUtility.getStringValue("test_key");

        Log.d(TAG, "Response text stored in settings = '" + exampleResponseText + "', text read from settings = '" + responseTextFromSettings + "'");
    }

    public void testReadingFromResources() {
        this.setUp();

        Log.d(TAG, "This test will read some string resource");

        String key = "responder_on_summary";
        String stringFromRes = this.sharedPreferencesUtility.getStringFromRes(key);

        Log.d(TAG, "getStringFromRes('" + key + "')='" + stringFromRes + "'");
    }

}
