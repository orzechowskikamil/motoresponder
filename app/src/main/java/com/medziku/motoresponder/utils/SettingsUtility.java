package com.medziku.motoresponder.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.medziku.motoresponder.R;


/**
 * This class exposes settings of application and totally hide process of storing them.
 */
public class SettingsUtility {

    public static final String RESPONDER_SERVICE_ENABLED_KEY = "responder_on";
    public static final String RESPONSE_TEXT_KEY = "response_text";

    private final String APP_SHARED_PREFERENCES = "AppSharedPreferences";
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private final String defaultResoinseText;

    public SettingsUtility(Context context) {
        this.sharedPreferences = context.getSharedPreferences(this.APP_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        this.editor = this.sharedPreferences.edit();

        this.defaultResoinseText = context.getString(R.string.default_resoinse_text);
    }

    private boolean getValue(String name, boolean defaultValue) {//TODO key not name
        return this.sharedPreferences.getBoolean(name, defaultValue);
    }

    private void setValue(String name, boolean value) {//TODO key not name
        this.editor.putBoolean(name, value);
        this.editor.commit();
    }

    private String getValue(String name, String defaultValue) {//TODO key not name
        return this.sharedPreferences.getString(name, defaultValue);
    }

    private void setValue(String name, String value) {//TODO key not name
        this.editor.putString(name, value);
        this.editor.commit();
    }

    private int getValue(String name, int defaultValue) {//TODO TODO key not name
        return this.sharedPreferences.getInt(name, defaultValue);
    }

    private void setValue(String name, int value) {
        this.editor.putInt(name, value);
        this.editor.commit();
    }


    /**
     * If true, it means that autoesponding service is enabled. If not, it's disabled (whole app shouldn't work).
     * @return
     */
    public boolean isServiceEnabled() {
        return this.getValue(RESPONDER_SERVICE_ENABLED_KEY, true);
    }

    /**
     * For changing setting of service enabled or disabled.
     * TODO K. Orzechowski:  it should stop/start service or not?
     * @param value
     */
    public void setServiceEnabled(boolean value) {
        this.setValue(RESPONDER_SERVICE_ENABLED_KEY, value);
    }


    /**
     * Return stored text of auto response for SMS message.
     * @return
     */
    public String getAutoResponseTextForSMS() {
        return defaultResoinseText;
    }

    /**
     * Should we treat phone unlocked as not riding or not?
     * @return
     */
    public boolean isPhoneUnlockedInterpretedAsNotRiding() {
        return true;
    }


    /**
     * Should we display notification when motoresponder is measuring if user is riding or not?
     * @return
     */
    public boolean isShowingPendingNotificationEnabled() {
        return true;
    }

    /**
     * How long responder should wait since receiving message to starting responding process, to allow user
     * manually respond if he is not away.
     * @return
     */
    public int getDelayBeforeRespondingMs() {
        return   30000;
    }

    /**
     * Return response text stored in preferences.
     * @return
     */
    //TODO tests
    public String getResponseText() {//TODO probably there will be need to use phone number argument
        return this.sharedPreferences.getString(RESPONSE_TEXT_KEY, getDefaultResponseText());
    }

    /**
     * Return default response text.
     * @return
     */
    public String getDefaultResponseText() {
        return this.defaultResoinseText;
    }
}
