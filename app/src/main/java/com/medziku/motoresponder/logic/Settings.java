package com.medziku.motoresponder.logic;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

public class Settings extends SettingsBase {

    public static final String RESPONDER_ENABLED = "responder_enabled";
    public static final String AUTO_RESPONSE_TO_SMS_TEMPLATE = "auto_response_to_sms_template";
    public static final String AUTO_RESPONSE_TO_CALL_TEMPLATE = "auto_response_to_call_template";
    public static final String AUTO_RESPONSE_TO_SMS_WITH_GEOLOCATION_TEMPLATE = "auto_response_to_sms_with_geolocation_template";

    private Predicate<Boolean> responderEnabledCallback;

    public Settings(SharedPreferencesUtility sharedPreferencesUtility) {
        super(sharedPreferencesUtility);
    }


    public boolean isResponderEnabled() {
        return this.getBooleanValue(RESPONDER_ENABLED);
    }


    public String getAutoResponseToSmsTemplate() {
        return this.getStringValue(AUTO_RESPONSE_TO_SMS_TEMPLATE);
    }


    public String getAutoResponseToCallTemplate() {
        return this.getStringValue(AUTO_RESPONSE_TO_CALL_TEMPLATE);
    }


    public String getAutoResponseToSmsWithGeolocationTemplate() {
        return this.getStringValue(AUTO_RESPONSE_TO_SMS_WITH_GEOLOCATION_TEMPLATE);
    }


    public boolean isPhoneUnlockedInterpretedAsNotRiding() {
        return this.getBooleanValue("phone_unlocked_interpreted_as_not_riding");
    }

    public boolean isShowingPendingNotificationEnabled() {
        return this.getBooleanValue("showing_pending_notification_enabled");
    }

    public int getDelayBeforeResponseMs() {
        return this.getIntValue("delay_before_response_ms");
    }

    public void listenToResponderEnabledChange(Predicate<Boolean> callback) {
        this.responderEnabledCallback = callback;
    }

    public String[] getGeolocationRequestPatterns() {
        String responsePattern1 = this.getStringValue("geolocation_request_pattern_1");
        String responsePattern2 = this.getStringValue("geolocation_request_pattern_2");
        String[] responsePatterns = {responsePattern1, responsePattern2};
        return responsePatterns;
    }

    public boolean isRespondingWithGeolocationEnabled() {
        return this.getBooleanValue("responding_with_geolocation_enabled");
    }

    protected void onSharedPreferenceChanged(String changedKey) {
        switch (changedKey) {
            case RESPONDER_ENABLED:
                if (this.responderEnabledCallback != null) {
                    this.responderEnabledCallback.apply(true);
                }
                break;
        }
    }


}

class SettingsBase {

    private SharedPreferencesUtility sharedPreferencesUtility;


    public SettingsBase(SharedPreferencesUtility sharedPreferencesUtility) {

        this.sharedPreferencesUtility = sharedPreferencesUtility;

        this.sharedPreferencesUtility.listenToSharedPreferenceChanged(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String changedKey) {
                SettingsBase.this.onSharedPreferenceChanged(changedKey);
                return false;
            }
        });
    }

    protected void onSharedPreferenceChanged(String changedKey) {
    }

    protected String getStringValue(String name) {
        return this.sharedPreferencesUtility.getStringValue(name, this.getDefaultStringValue(name));
    }

    protected boolean getBooleanValue(String name) {
        return this.sharedPreferencesUtility.getBooleanValue(name, this.getDefaultBooleanValue(name));
    }

    protected int getIntValue(String name) {
        return this.sharedPreferencesUtility.getIntValue(name, this.getDefaultIntValue(name));
    }


    protected String getDefaultStringValue(String name) {
        return this.sharedPreferencesUtility.getStringFromRes(this.getDefaultValueName(name));
    }

    protected int getDefaultIntValue(String name) {
        return this.sharedPreferencesUtility.getIntegerFromRes(this.getDefaultValueName(name));
    }

    protected boolean getDefaultBooleanValue(String name) {
        return this.sharedPreferencesUtility.getBooleanFromRes(this.getDefaultValueName(name));
    }

    protected String getDefaultValueName(String name) {
        return name + "_default_value";
    }

    public void stopListening() {
        this.sharedPreferencesUtility.stopListeningToSharedPreferenceChanged();
    }

}
