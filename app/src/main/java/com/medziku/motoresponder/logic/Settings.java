package com.medziku.motoresponder.logic;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

public class Settings {

    public static final String RESPONDER_ENABLED = "responder_enabled";
    public static final String AUTO_RESPONSE_TO_SMS_TEMPLATE = "auto_response_to_sms_template";
    public static final String AUTO_RESPONSE_TO_CALL_TEMPLATE = "auto_response_to_call_template";
    public static final String AUTO_RESPONSE_TO_SMS_WITH_GEOLOCATION_TEMPLATE = "auto_response_to_sms_with_geolocation_template";

    private SharedPreferencesUtility sharedPreferencesUtility;
    private Predicate<Boolean> responderEnabledCallback;


    public Settings(SharedPreferencesUtility sharedPreferencesUtility) {
        this.sharedPreferencesUtility = sharedPreferencesUtility;

        this.sharedPreferencesUtility.listenToSharedPreferenceChanged(new Function<String, Boolean>() {
            @Override
            public Boolean apply(String changedKey) {
                Settings.this.onSharedPreferenceChanged(changedKey);
                return false;
            }
        });
    }


    public boolean isResponderEnabled() {
        return this.sharedPreferencesUtility.getBooleanValue(RESPONDER_ENABLED);
    }

    public void setResponderEnabled(boolean isEnabled) {
        this.sharedPreferencesUtility.setBooleanValue(RESPONDER_ENABLED, isEnabled);
    }

    public String getAutoResponseToSmsTemplate() {
        return this.sharedPreferencesUtility.getStringValue(AUTO_RESPONSE_TO_SMS_TEMPLATE);
    }

    public void setAutoResponseToSmsTemplate(String template) {
        this.sharedPreferencesUtility.setStringValue(AUTO_RESPONSE_TO_SMS_TEMPLATE, template);
    }

    public String getAutoResponseToCallTemplate() {
        return this.sharedPreferencesUtility.getStringValue(AUTO_RESPONSE_TO_CALL_TEMPLATE);
    }

    public void setAutoResponseToCallTemplate(String template) {
        this.sharedPreferencesUtility.setStringValue(AUTO_RESPONSE_TO_CALL_TEMPLATE, template);
    }

    public String getAutoResponseToSmsWithGeolocationTemplate() {
        return this.sharedPreferencesUtility.getStringValue(AUTO_RESPONSE_TO_SMS_WITH_GEOLOCATION_TEMPLATE);
    }

    public void setAutoResponseToSmsWithGeolocationTemplate(String template) {
        this.sharedPreferencesUtility.setStringValue(AUTO_RESPONSE_TO_SMS_WITH_GEOLOCATION_TEMPLATE, template);
    }

    public boolean isPhoneUnlockedInterpretedAsNotRiding() {
        return this.sharedPreferencesUtility.getBooleanValue("phone_unlocked_interpreted_as_not_riding");
    }

    public boolean isShowingPendingNotificationEnabled() {
        return this.sharedPreferencesUtility.getBooleanValue("showing_pending_notification_enabled");
    }

    public int getDelayBeforeResponseMs() {
        return this.sharedPreferencesUtility.getIntValue("delay_before_response_ms");
    }

    public void listenToResponderEnabledChange(Predicate<Boolean> callback) {
        this.responderEnabledCallback = callback;
    }

    public String[] getGeolocationRequestPatterns() {
        String responsePattern1 = this.sharedPreferencesUtility.getStringFromRes("geolocation_request_pattern_1");
        String responsePattern2 = this.sharedPreferencesUtility.getStringFromRes("geolocation_request_pattern_2");
        String[] responsePatterns = {responsePattern1, responsePattern2};
        return responsePatterns;
    }

    public boolean isRespondingWithGeolocationEnabled() {
        return this.sharedPreferencesUtility.getBooleanFromRes("responding_with_geolocation_enabled");
    }

    private void onSharedPreferenceChanged(String changedKey) {
        switch (changedKey) {
            case RESPONDER_ENABLED:
                if (this.responderEnabledCallback != null) {
                    this.responderEnabledCallback.apply(true);
                }
                break;
        }
    }

}
