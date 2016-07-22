package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.R;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class Settings extends SettingsBase {

    public int TURNED_OFF_GPS_NOTIFICATION_TEXT_RES_ID;
    public int TURNED_OFF_GPS_NOTIFICATION_TITLE_RES_ID;
    public int SUMMARY_NOTIFICATION_TITLE_TEXT_RES_ID;
    public int SUMMARY_NOTIFICATION_SHORT_TEXT_RES_ID;
    public int SUMMARY_NOTIFICATION_BIG_TEXT_RES_ID;
    public int ONGOING_NOTIFICATION_TITLE_TEXT_RES_ID;
    public int ONGOING_NOTIFICATION_BIG_TEXT_RES_ID;
    public int DONT_USE_WHITELIST_TEXT_RES_ID;
    public int DONT_USE_BLACKLIST_TEXT_RES_ID;
    public int DONT_USE_GEOLOCATION_WHITELIST_TEXT_RES_ID;

    public int POWER_SAVE_NOTIFICATION_TITLE_TEXT_RES_ID;
    public int POWER_SAVE_NOTIFICATION_SHORT_TEXT_RES_ID;
    public int POWER_SAVE_NOTIFICATION_BIG_TEXT;

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

    public Settings(SharedPreferencesUtility sharedPreferencesUtility) {
        super(sharedPreferencesUtility);

        this.TURNED_OFF_GPS_NOTIFICATION_TITLE_RES_ID = R.string.turned_off_gps_notification_title;
        this.TURNED_OFF_GPS_NOTIFICATION_TEXT_RES_ID = R.string.turned_off_gps_notification_text;
        this.SUMMARY_NOTIFICATION_TITLE_TEXT_RES_ID = R.string.summary_notification_title_text;
        this.SUMMARY_NOTIFICATION_SHORT_TEXT_RES_ID = R.string.summary_notification_short_text;
        this.SUMMARY_NOTIFICATION_BIG_TEXT_RES_ID = R.string.summary_notification_big_text;
        this.ONGOING_NOTIFICATION_TITLE_TEXT_RES_ID = R.string.ongoing_notification_title_text;
        this.ONGOING_NOTIFICATION_BIG_TEXT_RES_ID = R.string.ongoing_notification_big_text;
        this.DONT_USE_WHITELIST_TEXT_RES_ID = R.string.dont_use_whitelist_text;
        this.DONT_USE_BLACKLIST_TEXT_RES_ID = R.string.dont_use_blacklist_text;
        this.DONT_USE_GEOLOCATION_WHITELIST_TEXT_RES_ID = R.string.dont_use_geolocation_whitelist_text;
        this.POWER_SAVE_NOTIFICATION_BIG_TEXT = R.string.power_save_notification_big_text;
        this.POWER_SAVE_NOTIFICATION_SHORT_TEXT_RES_ID = R.string.power_save_notification_short_text;
        this.POWER_SAVE_NOTIFICATION_TITLE_TEXT_RES_ID = R.string.power_save_notification_title_text;
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

    public void setSensorCheckEnabled(boolean value) {
        this.setBooleanValue(R.string.sensor_check_enabled_key, value);
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


    public boolean isShowingPendingNotificationEnabled() {
        return this.getBooleanValue(R.string.showing_pending_notification_enabled_key);
    }

    public boolean isAlreadyRespondedFilteringEnabled() {
        return this.getBooleanValue(R.string.already_responded_filtering_enabled_key);
    }


    public List<String> getGeolocationRequestPatterns() {
        List<String> value = this.getStringArrayValue(R.string.geolocation_request_patterns_key);

        // in this particular case, if value is somehow null, return an empty list. but it never should.
        if (value == null) {
            value = new ArrayList<>();
        }

        return value;
    }

    public void setGeolocationRequestPatterns(List<String> patterns) {
        this.setStringArrayValue(R.string.geolocation_request_patterns_key, patterns);
    }

    public int getLimitOfGeolocationResponses() {
        return 3;
    }

    public boolean isWizardCompleted() {
        return this.getBooleanValue(R.string.wizard_completed_key);
    }

    private void setWizardCompleted(boolean value) {
        this.setBooleanValue(R.string.wizard_completed_key, value);
    }

    public void setWizardAsCompleted() {
        this.setWizardCompleted(true);
    }

    /**
     * Warning: Calling this will also reset current step of wizard to 0.
     */
    public void setWizardAsNotCompleted() {
        this.setWizardCompleted(false);
        this.setCurrentStepOfWizard(0);
    }

    public int getCurrentStepOfWizard() {
        return this.getIntValue(R.string.current_step_of_wizard_key);
    }

    public void setCurrentStepOfWizard(int value) {
        this.setIntValue(R.string.current_step_of_wizard_key, value);
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

    public void setRespondingWithGeolocationEnabled(boolean value) {
        this.setBooleanValue(R.string.geolocation_request_enabled_key, value);
    }

    public int getTrafficJamDetectionDurationSeconds() {
        return this.getIntValue(R.string.traffic_jam_detection_duration_seconds_key);
    }

    public boolean isRespondingForSMSEnabled() {
        return this.getBooleanValue(R.string.auto_response_to_sms_enabled_key);
    }

    public boolean isRespondingForCallsEnabled() {
        return this.getBooleanValue(R.string.auto_response_to_call_enabled_key);
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

    public int getDistanceForTrafficJamDetectionMeters() {
        return this.getIntValue(R.string.distance_for_traffic_jam_detection_meters_key);
    }


    public int getTrafficJamDelaySeconds() {
        return this.getIntValue(R.string.traffic_jam_detection_delay_seconds_key);
            }
    }
