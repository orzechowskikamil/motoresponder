package com.medziku.motoresponder.logic;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.R;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Settings extends SettingsBase {


    public String RESPONDER_ENABLED_KEY;
    public String AUTO_RESPONSE_TO_CALL_ENABLED_KEY;
    public String AUTO_RESPONSE_TO_SMS_ENABLED_KEY;
    public String SENSOR_CHECK_ENABLED_KEY;
    public String GEOLOCATION_REQUEST_ENABLED_KEY;
    public String IS_RIDING_ASSUMED_KEY;
    public int IS_RIDING_ASSUMED_NOTIFICATION_TITLE_TEXT_ID;
    public int IS_RIDING_ASSUMED_NOTIFICATION_BIG_TEXT_ID;
    public int ONGOING_NOTIFICATION_I_AM_RIDING_ID = 1;
    public int ONGOING_NOTIFICATION_PENDING_AUTORESPOND_ID = 2;


    public static int POWER_SAVER_NOTIFICATION_ID = 3;

    public Settings(SharedPreferencesUtility sharedPreferencesUtility) {
        super(sharedPreferencesUtility);

        this.RESPONDER_ENABLED_KEY = this.getStringFromRes(R.string.responder_enabled_key);
        this.AUTO_RESPONSE_TO_CALL_ENABLED_KEY = this.getStringFromRes(R.string.auto_response_to_call_enabled_key);
        this.AUTO_RESPONSE_TO_SMS_ENABLED_KEY = this.getStringFromRes(R.string.auto_response_to_sms_enabled_key);
        this.SENSOR_CHECK_ENABLED_KEY = this.getStringFromRes(R.string.sensor_check_enabled_key);
        this.GEOLOCATION_REQUEST_ENABLED_KEY = this.getStringFromRes(R.string.geolocation_request_enabled_key);
        this.IS_RIDING_ASSUMED_KEY = this.getStringFromRes(R.string.is_riding_assumed_key);

        this.IS_RIDING_ASSUMED_NOTIFICATION_TITLE_TEXT_ID = R.string.ongoing_notification_is_riding_assumed_title_text;
        this.IS_RIDING_ASSUMED_NOTIFICATION_BIG_TEXT_ID = R.string.ongoing_notification_is_riding_assumed_big_text;
    }


    public boolean isResponderEnabled() {
        return this.getBooleanValue(R.string.responder_enabled_key);
    }

    public void setResponderEnabled(boolean responderEnabled) {
        this.setBooleanValue(R.string.responder_enabled_key, responderEnabled);
    }

    /**
     * if true, app will perform sensor checks to measure if you are riding.
     * if false, app will check isRidingAssumed for decide if you are riding or not
     */
    public boolean isSensorCheckEnabled() {
        return this.getBooleanValue(R.string.sensor_check_enabled_key);
    }

    /**
     * If true, app with disabled sensorChecks will assume that you are riding
     */
    public boolean isRidingAssumed() {
        return this.getBooleanValue(R.string.is_riding_assumed_key);
    }

    public void setRidingAssumed(boolean value) {
        this.setBooleanValue(R.string.is_riding_assumed_key, value);
    }

    public int getSureRidingSpeedKmh() {
        return this.getIntValue(R.string.sure_riding_speed_key);
    }

    public int getQuickSpeedCheckDurationSeconds() {
        return this.getIntValue(R.string.quick_speed_check_duration_key);
    }

    public int getWaitBeforeResponseSeconds() {
        return this.getIntValue(R.string.wait_before_response_key);
    }

    public int getRequiredAccuracyMeters() {
        return this.getIntValue(R.string.required_accuracy_key);
    }

    public boolean isProximityCheckEnabled() {
        return this.getBooleanValue(R.string.include_proximity_check_key);
    }

    public boolean isAssumingScreenUnlockedAsNotRidingEnabled() {
        return this.getBooleanValue(R.string.assume_screen_unlocked_as_not_riding_key);
    }

    public int getMaximumStayingStillSpeedKmh() {
        return this.getIntValue(R.string.staying_still_speed_key);
    }

    public int getLongSpeedCheckDurationSeconds() {
        return this.getIntValue(R.string.long_speed_check_duration_key);
    }

    public boolean isRespondingRestrictedToContactList() {
        return this.getBooleanValue(R.string.responding_restricted_to_contact_list_key);
    }

    public String getAutoResponseToSmsTemplate() {
        return this.getStringValue(R.string.auto_response_to_sms_template_key);
    }

    public String getAutoResponseToCallTemplate() {
        return this.getStringValue(R.string.auto_response_to_call_template_key);
    }

    public String getAutoResponseToSmsWithGeolocationTemplate() {
        return this.getStringValue(R.string.auto_response_to_sms_with_geolocation_template_key);
    }

    public boolean isShowingSummaryNotificationEnabled() {
        return this.getBooleanValue(R.string.showing_summary_notification_enabled_key);
    }

    public boolean isShowingDebugNotificationEnabled() {
        return this.getBooleanValue(R.string.showing_debug_notification_enabled_key);
    }

    public boolean isShowingPendingNotificationEnabled() {
        return this.getBooleanValue(R.string.showing_pending_notification_enabled_key);
    }

    public boolean isAlreadyRespondedFilteringEnabled() {
        return this.getBooleanValue(R.string.already_responded_filtering_enabled_key);
    }


    public String[] getGeolocationRequestPatterns() {
        String responsePattern1 = this.getStringValue(R.string.geolocation_request_pattern_1_key);
        String responsePattern2 = this.getStringValue(R.string.geolocation_request_pattern_2_key);
        String[] responsePatterns = {responsePattern1, responsePattern2};
        return responsePatterns;
    }

    public int getLimitOfGeolocationResponses() {
        return 3;
    }

    public boolean includeDeviceMotionCheck() {
        return this.getBooleanValue(R.string.include_accelerometer_check_key);
    }

    public int getLimitOfResponses() {
        return 1;
    }

    public double getAccelerationRequiredToMotion() {
        return Double.parseDouble(this.getStringValue(R.string.acceleration_required_for_motion_key));
    }

    public String getGeolocationWhitelistGroupName() {
        String result = this.getStringValue(R.string.geolocation_whitelist_group_name_key);

        if (result == null || result.trim().equals("")) {
            return null;
        }
        return result;
    }

    public String getWhiteListGroupName() {
        String result = this.getStringValue(R.string.whitelist_group_name_key);

        if (result == null || result.trim().equals("")) {
            return null;
        }
        return result;
    }

    public boolean isWiFiCheckEnabled() {
        return this.getBooleanValue(R.string.wifi_check_enabled_key);
    }

    /**
     * If true, responding with geolocation will be possible.
     *
     * @return
     */
    public boolean isRespondingWithGeolocationEnabled() {
        return this.getBooleanValue(R.string.geolocation_request_enabled_key);
    }

    public boolean isRespondingForSMSEnabled() {
        return this.getBooleanValue(R.string.auto_response_to_sms_enabled_key);
    }

    public boolean isRespondingForCallsEnabled() {
        return this.getBooleanValue(R.string.auto_response_to_call_enabled_key);
    }

    public String getDebugNotificationTitleText() {
        return this.getStringFromRes(R.string.debug_notification_title_text);
    }

    public String getDebugNotificationShortText() {
        return this.getStringFromRes(R.string.debug_notification_short_text);
    }

    public String getSummaryNotificationTitleText() {
        return this.getStringFromRes(R.string.summary_notification_title_text);
    }

    public String getSummaryNotificationShortText() {
        return this.getStringFromRes(R.string.summary_notification_short_text);
    }

    public String getSummaryNotificationBigText() {
        return this.getStringFromRes(R.string.summary_notification_big_text);
    }
    
        public String getPowerSaveNotificationTitleText() {
        return this.getStringFromRes(R.string.power_save_notification_title_text);
    }

    public String getPowerSaveNotificationShortText() {
        return this.getStringFromRes(R.string.power_save_notification_short_text);
    }

    public String getPowerSaveNotificationBigText() {
        return this.getStringFromRes(R.string.power_save_notification_big_text);
    }
    
    
    

    public String getOngoingNotificationTitleText() {
        return this.getStringFromRes(R.string.ongoing_notification_title_text);
    }

    public String getOngoingNotificationBigText() {
        return this.getStringFromRes(R.string.ongoing_notification_big_text);
    }

    public String getDontUseWhitelistText() {
        return this.getStringFromRes(R.string.dont_use_whitelist_text);
    }

    public String getDontUseBlacklistText() {
        return this.getStringFromRes(R.string.dont_use_blacklist_text);
    }

    public String getDontUseGeolocationWhitelistText() {
        return this.getStringFromRes(R.string.dont_use_geolocation_whitelist_text);
    }

    /**
     * if true, app will always respond with geolocation no matter if someone ask for it.
     * Otherwise only if certain pattern found in message.
     */
    public boolean isRespondingWithGeolocationAlwaysEnabled() {
        // TODO K. Orzechowski: change constant settings to real configurable values #67
        return false;
    }

    public String getBlackListGroupName() {
        String result = this.getStringValue(R.string.blacklist_group_name_key);

        if (result == null || result.trim().equals("")) {
            return null;

        }
        return result;
    }

    public String getStoredDevicePhoneNumber() {
        return this.getStringValue(R.string.device_phone_number_key);
    }

    public boolean isRespondingRestrictedToCurrentCountry() {
        return this.getBooleanValue(R.string.responding_restricted_to_current_country_key);
    }

    public boolean isTermsAndConditionAccepted() {
        return this.getBooleanValue(R.string.terms_and_conditions_accepted_key);
    }

    public void setTermsAndCondition(boolean accepted) {
        this.setBooleanValue(R.string.terms_and_conditions_accepted_key, accepted);
    }

    protected void onSharedPreferenceChanged(String changedKey) {
        try {
            for (Predicate<Boolean> callback : this.getCallbacksListForSetting(changedKey)) {
                if (callback != null) {
                    callback.apply(true);
                }
            }
        } catch (Exception e) {
        }
    }

}


