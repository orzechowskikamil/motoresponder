package com.medziku.motoresponder.pseudotesting.utilities;


import android.content.Context;
import android.os.Looper;
import android.util.Log;
import com.google.common.base.Function;
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

        String storedText = "test string";
        this.sharedPreferencesUtility.setStringValue("test_key", storedText);

        String responseTextFromSettings = this.sharedPreferencesUtility.getStringValue("test_key", "error");

        Log.d(TAG, "Response text stored in settings = '" + storedText + "', text read from settings = '" + responseTextFromSettings + "'");
    }

    public void testReadingFromResources() {
        this.setUp();

        Log.d(TAG, "This test will read some string resource");

        String key = "responder_enabled_summary";
        String stringFromRes = this.sharedPreferencesUtility.getStringFromRes(key);

        Log.d(TAG, "getStringFromRes('" + key + "')='" + stringFromRes + "'");
    }

    public void testListeningToChanges() {
        this.setUp();


        Log.d(TAG, "This test will listen to setting change");
        Log.d(TAG, "Look if setting change will be logged");

        this.sharedPreferencesUtility.listenToSharedPreferenceChanged(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String key) {
                Log.d(TAG, "Listener: Shared preference changed, key = '" + key + "");
                return null;
            }
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "Changing test_of_listener to 'is working'");
        this.sharedPreferencesUtility.setStringValue("test_of_listener", "is working");
        Log.d(TAG, "Changing test_of_listener to 'is working again'");
        this.sharedPreferencesUtility.setStringValue("test_of_listener", "is working again");

    }

    public void testListeningToChangesInUI() {
        this.setUp();

        Log.d(TAG, "This test will listen to setting change if preference UI is changed");
        Log.d(TAG, "Look if setting change will be logged");

        this.sharedPreferencesUtility.listenToSharedPreferenceChanged(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String key) {
                Log.d(TAG, "Listener: Shared preference changed (from UI), key = '" + key + "");
                return null;
            }
        });

    }
}
