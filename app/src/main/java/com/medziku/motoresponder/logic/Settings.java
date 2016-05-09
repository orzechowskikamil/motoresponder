package com.medziku.motoresponder.logic;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;
import com.medziku.motoresponder.R;

public class Settings extends SettingsBase {


    private Predicate<Boolean> responderEnabledCallback;
    private Predicate<Boolean> onChangeRespondingToSMSOrCallsCallback;

    public Settings(SharedPreferencesUtility sharedPreferencesUtility) {
        super(sharedPreferencesUtility);
    }


    public boolean isResponderEnabled() {
        return this.getBooleanValue(R.string.responder_enabled_key);
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

    public String getAutoResponseToSmsTemplate() {
        return this.getStringValue(R.string.auto_response_to_sms_template_key);
    }

    public String getAutoResponseToCallTemplate() {
        return this.getStringValue(R.string.auto_response_to_call_template_key);
    }


    public String getAutoResponseToSmsWithGeolocationTemplate() {
        return this.getStringValue(R.string.auto_response_to_sms_with_geolocation_template_key);
    }
    
   public boolean isShowingSummaryNotificationEnabled(){
       return this.getBooleanValue(R.string.showing_summary_notification_enabled_key);
   }

    public boolean  isShowingDebugNotificationEnabled(){
          return this.getBooleanValue(R.string.showing_debug_notification_enabled_key);
    }


    public boolean isShowingPendingNotificationEnabled() {
        return this.getBooleanValue(R.string.showing_pending_notification_enabled_key);
    }


    public void listenToResponderEnabledChange(Predicate<Boolean> callback) {
        this.responderEnabledCallback = callback;
    }

    public String[] getGeolocationRequestPatterns() {
        String responsePattern1 = this.getStringValue(R.string.geolocation_request_pattern_1_key);
        String responsePattern2 = this.getStringValue(R.string.geolocation_request_pattern_2_key);
        String[] responsePatterns = {responsePattern1, responsePattern2};
        return responsePatterns;
    }
    
    public boolean includeDeviceMotionCheck(){
        return this.getBooleanValue(R.string.include_accelerometer_check_key);
    }
    
    public double getAccelerationRequiredToMotion(){
        return Double.parseDouble(this.getStringValue(R.string.acceleration_required_for_motion_key));
    }
    


    /**
     * If true, responding with geolocation will be possible.
     *
     * @return
     */
    public boolean isRespondingWithGeolocationEnabled() {
        return this.getBooleanValue(R.string.geolocation_request_enabled_key);
    }

    protected void onSharedPreferenceChanged(String changedKey) {
        final String RESPONDER_ENABLED_KEY = this.getStringFromRes(R.string.responder_enabled_key);

        if (changedKey.equals(RESPONDER_ENABLED_KEY)) {
            if (this.responderEnabledCallback != null) {
                this.responderEnabledCallback.apply(true);
            }
        }
    }
    
    public boolean isRespondingForSMSEnabled(){
        return this.getBooleanValue(R.string.auto_response_to_sms_enabled_key);
    }
    
    public boolean isRespondingForCallsEnabled(){
        return this.getBooleanValue(R.string.auto_response_to_call_enabled_key);
    }
    
    public void listenToChangeRespondToSmsOrCallSetting(Predicate<Boolean> callback){
        this.onChangeRespondingToSMSOrCallsCallback = callback;
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

    public String getOngoingNotificationTitleText(){
        return this.getStringFromRes(R.string.ongoing_notification_title_text);
    }

    public String getOngoingNotificationBigText(){
        return this.getStringFromRes(R.string.ongoing_notification_big_text);
    }

    /**
     * if true, app will always respond with geolocation no matter if someone ask for it.
     * Otherwise only if certain pattern found in message.
     */
    public boolean isRespondingWithGeolocationAlwaysEnabled() {
        // TODO K. Orzechowski: change constant settings to real configurable values #67
        return false;
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

    protected String getStringValue(int resID) {
        return this.getStringValue(this.getStringFromRes(resID));
    }

    protected String getStringFromRes(int resID) {
        return this.sharedPreferencesUtility.getStringFromRes(resID);
    }

    protected String getStringValue(String name) {
        return this.sharedPreferencesUtility.getStringValue(name, this.getDefaultStringValue(name));
    }

    protected boolean getBooleanValue(int resID) {
        return this.getBooleanValue(this.getStringFromRes(resID));
    }

    protected boolean getBooleanValue(String name) {
        return this.sharedPreferencesUtility.getBooleanValue(name, this.getDefaultBooleanValue(name));
    }

    protected int getIntValue(int resID) {
        return this.getIntValue(this.getStringFromRes(resID));
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
