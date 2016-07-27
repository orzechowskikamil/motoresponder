public class SettingsUtilityMock {

public boolean RESPONDER_ENABLED_DEFAULT;

public SettingsUtilityMock(){ this.mock = mock(SettingsUtility.class) ; this.setupMock();}

private void setupMock(){
    // pretty standard settings
    when(this.mock.isResponderEnabled()).thenReturn(RESPONDER_ENABLED_DEFAULT);
    when(this.mock.isSensorCheckEnabled()).thenReturn(SENSOR_CHECK_ENABLED_DEFAULT);
    when(this.isMock. isRidingAssumed()).thenReturn( IS_RIDING_ASSUMED_DEFAULT);
    when(this.isMock. getSureRidingSpeedKmh()).thenReturn( SURE_RIDING_SPEED_KMH_DEFAULT);
    when(this.isMock. getQuickSpeedCheckDurationSeconds()).thenReturn( _DEFAULT);
    when(this.isMock. getWaitBeforeResponseSeconds()).thenReturn( _DEFAULT);
    when(this.isMock. getRequiredAccuracyMeters()).thenReturn( _DEFAULT);
    when(this.isMock. isProximityCheckEnabled()).thenReturn( _DEFAULT);
    when(this.isMock. isAssumingScreenUnlockedAsNotRidingEnabled()).thenReturn( _DEFAULT);
    when(this.isMock. getMaximumStayingStillSpeedKmh()).thenReturn( _DEFAULT);
    when(this.isMock.getLongSpeedCheckDurationSeconds ()).thenReturn( _DEFAULT);
    when(this.isMock. isRespondingRestrictedToContactList()).thenReturn( _DEFAULT);
    when(this.isMock. getAutoResponseToSmsTemplate()).thenReturn( _DEFAULT);
    when(this.isMock. getAutoResponseToCallTemplate()).thenReturn( _DEFAULT);
    when(this.isMock.getAutoResponseToSmsWithGeolocationTemplate ()).thenReturn( _DEFAULT);
    when(this.isMock.isShowingSummaryNotificationEnabled ()).thenReturn( _DEFAULT);
    when(this.isMock.isShowingDebugNotificationEnabled ()).thenReturn( _DEFAULT);
    when(this.isMock.isShowingPendingNotificationEnabled ()).thenReturn( _DEFAULT);
    when(this.isMock.isAlreadyRespondedFilteringEnabled ()).thenReturn( _DEFAULT);
    when(this.isMock. ()).thenReturn( _DEFAULT);
    when(this.isMock. ()).thenReturn( _DEFAULT);
    when(this.isMock. ()).thenReturn( _DEFAULT);
    when(this.isMock. ()).thenReturn( _DEFAULT);
    when(this.isMock. ()).thenReturn( _DEFAULT);
}

}
