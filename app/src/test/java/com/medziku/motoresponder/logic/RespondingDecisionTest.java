package com.medziku.motoresponder.logic;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


public class RespondingDecisionTest {

    private UserRide userRide;
    private NumberRules numberRules;
    private RespondingDecision respondingDecision;
    private CurrentAlreadyResponded currentAlreadyResponded;
    private DeviceUnlocked deviceUnlocked;
    private String FAKE_PHONE_NUMBER = "777777777";
    private RespondingSubject fakeRespondingSubject;
    private Settings settings;
    private TimeBasedAlreadyResponded timeBasedAlreadyResponded;
    private NoLimitingAlreadyResponded noLimitingAlreadyResponded;
    private AmountBasedAlreadyResponded amountBasedAlreadyResponded;


    @Before
    // test is build in manner, that all values in setup make positive decision, and in each test I am testing
    // if changing one parameter correctly makes decision negative.
    public void runBeforeTests() {
        this.settings = Mockito.mock(Settings.class);
        this.fakeRespondingSubject = new CallRespondingSubject(this.FAKE_PHONE_NUMBER, new Date(), this.settings);

        this.currentAlreadyResponded = Mockito.mock(CurrentAlreadyResponded.class);

        this.timeBasedAlreadyResponded = Mockito.mock(TimeBasedAlreadyResponded.class);
        this.amountBasedAlreadyResponded = Mockito.mock(AmountBasedAlreadyResponded.class);
        this.noLimitingAlreadyResponded = Mockito.mock(NoLimitingAlreadyResponded.class);

        when(this.currentAlreadyResponded.get()).thenReturn(this.amountBasedAlreadyResponded);


        this.userRide = Mockito.mock(UserRide.class);
        this.numberRules = Mockito.mock(NumberRules.class);
        this.deviceUnlocked = Mockito.mock(DeviceUnlocked.class);
        when(this.settings.getLimitOfGeolocationResponses()).thenReturn(2);
        when(this.settings.getLimitOfResponses()).thenReturn(1);
        when(this.settings.isAlreadyRespondedFilteringEnabled()).thenReturn(true);
        CustomLog log = Mockito.mock(CustomLog.class);
        this.respondingDecision = new RespondingDecision(this.userRide, this.numberRules, this.currentAlreadyResponded, this.deviceUnlocked, this.settings, log);


        this.setDeviceUnlockedIsNotRidingReturnValue(false);
        this.setNumberRulesAllowRespondingReturnValue(true);
        when(this.amountBasedAlreadyResponded.isUserRespondedSince(any(RespondingSubject.class))).thenReturn(false);
        this.setUserRideIsUserRidingReturnValue(true);

        this.setSensorCheckEnabled(true);
        this.setIsRidingAssumed(false);
    }


    @Test
    public void unlockedScreenMakesNegativeDecision() throws GPSNotAvailableException {
        this.setDeviceUnlockedIsNotRidingReturnValue(true);
        this.expectRespondingDecisionShouldRespondToBe(false);
    }

    @Test
    public void gpsNotAvailableExceptionIsNotCatched() throws GPSNotAvailableException {
        when(this.userRide.isUserRiding()).thenThrow(GPSNotAvailableException.class);

        try {
            this.expectRespondingDecisionShouldRespondToBe(false);
            fail();
        } catch (GPSNotAvailableException e) {
            // success
        }
    }


    @Test
    public void notFillingNumberRulesMakeNegativeDecision() throws GPSNotAvailableException {
        this.setNumberRulesAllowRespondingReturnValue(false);
        this.expectRespondingDecisionShouldRespondToBe(false);
    }

    @Test
    public void userRespondedSinceMessageReceivedMakeNegativeDecision() throws GPSNotAvailableException {
        when(this.amountBasedAlreadyResponded.isUserRespondedSince(any(RespondingSubject.class))).thenReturn(true);


        this.expectRespondingDecisionShouldRespondToBe(false);
    }


    @Test
    public void userNotRidingMakeNegativeDecision() throws GPSNotAvailableException {
        this.setUserRideIsUserRidingReturnValue(false);
        this.expectRespondingDecisionShouldRespondToBe(false);
    }

    @Test
    public void allFullfilledConditionsMakePositiveDecision() throws GPSNotAvailableException {
        this.expectRespondingDecisionShouldRespondToBe(true);
    }

    @Test
    public void alreadyRespondedDoesntMatterWhenDisabled() throws GPSNotAvailableException {
        when(this.settings.isAlreadyRespondedFilteringEnabled()).thenReturn(false);
        when(this.amountBasedAlreadyResponded.isUserRespondedSince(any(RespondingSubject.class))).thenReturn(false);

        this.expectRespondingDecisionShouldRespondToBe(true);
    }

    @Test
    public void userManuallySetRidingMakesTrueDecision() throws GPSNotAvailableException {
        this.setUserRideIsUserRidingReturnValue(false);

        this.setSensorCheckEnabled(false);
        this.setIsRidingAssumed(true);

        this.expectRespondingDecisionShouldRespondToBe(true);
    }

    @Test
    public void userManuallySetNotRidingMakesFalseDecision() throws GPSNotAvailableException {
        this.setUserRideIsUserRidingReturnValue(true);

        this.setSensorCheckEnabled(false);
        this.setIsRidingAssumed(false);

        this.expectRespondingDecisionShouldRespondToBe(false);
    }

    @Test
    public void userManuallySetRidingAndSensorReportsNotRidingMakesTrueDecision() throws GPSNotAvailableException {
        this.setUserRideIsUserRidingReturnValue(false);

        this.setSensorCheckEnabled(true);
        this.setIsRidingAssumed(true);

        this.expectRespondingDecisionShouldRespondToBe(true);
    }

    @Test
    public void userEnabledSensorCheckAndManualSettingOffMakesTrueDecision() throws GPSNotAvailableException {
        this.setUserRideIsUserRidingReturnValue(true);

        this.setSensorCheckEnabled(true);
        this.setIsRidingAssumed(false);

        this.expectRespondingDecisionShouldRespondToBe(true);
    }


    @Test
    public void tooMuchAutomaticalResponsesMakeNegativeDecision2() throws GPSNotAvailableException {
        when(this.amountBasedAlreadyResponded.isAutoResponsesLimitExceeded(any(RespondingSubject.class))).thenReturn(true);
        this.expectRespondingDecisionShouldRespondToBe(false);
    }


    @Test
    public void lateUnlockOfDeviceMakeNegativeDecision() throws GPSNotAvailableException {
        when(this.userRide.isUserRiding()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                // simulate unlocking screen after determining location.
                when(RespondingDecisionTest.this.deviceUnlocked.isNotRidingBecausePhoneUnlocked()).thenReturn(true);
                return true;
            }
        });

        this.expectRespondingDecisionShouldRespondToBe(false);
    }


    @Test
    public void lateResponseByUserMakeNegativeDecision() throws GPSNotAvailableException {
        when(this.userRide.isUserRiding()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                // simulate user answered after determining location.
                when(
                        RespondingDecisionTest.this.amountBasedAlreadyResponded.isUserRespondedSince(any(RespondingSubject.class))
                ).thenReturn(true);
                return true;
            }
        });

        this.expectRespondingDecisionShouldRespondToBe(false);
    }

    @Test
    public void respondingDecisionWorksWithTimeBasedAlreadyResponded() throws GPSNotAvailableException {
        when(this.currentAlreadyResponded.get()).thenReturn(this.timeBasedAlreadyResponded);

        this.expectRespondingDecisionShouldRespondToBe(true);
    }

    @Test
    public void timeBasedAlreadyRespondedPreventResponse() throws GPSNotAvailableException {
        when(this.currentAlreadyResponded.get()).thenReturn(this.timeBasedAlreadyResponded);

        when(this.timeBasedAlreadyResponded.isAutoResponsesLimitExceeded(any(RespondingSubject.class))).thenReturn(true);
        when(this.timeBasedAlreadyResponded.isUserRespondedSince(any(RespondingSubject.class))).thenThrow(UnsupportedOperationException.class);

        this.expectRespondingDecisionShouldRespondToBe(false);
    }


    // region helper methods
    private void setIsRidingAssumed(boolean value) {
        when(this.settings.isRidingAssumed()).thenReturn(value);
    }

    private void setSensorCheckEnabled(boolean val) {
        when(this.settings.isSensorCheckEnabled()).thenReturn(val);
    }

    private void expectRespondingDecisionShouldRespondToBe(boolean expectedValue) throws GPSNotAvailableException {
        Assert.assertTrue(this.respondingDecision.shouldRespond(this.fakeRespondingSubject) == expectedValue);
    }

    private void setNumberRulesAllowRespondingReturnValue(boolean returnValue) {
        when(this.numberRules.numberRulesAllowResponding(this.FAKE_PHONE_NUMBER)).thenReturn(returnValue);
    }

    private void setDeviceUnlockedIsNotRidingReturnValue(boolean returnValue) {
        when(this.deviceUnlocked.isNotRidingBecausePhoneUnlocked()).thenReturn(returnValue);
    }

    private void setUserRideIsUserRidingReturnValue(boolean returnValue) {
        try {
            when(this.userRide.isUserRiding()).thenReturn(returnValue);
        } catch (GPSNotAvailableException e) {
            e.printStackTrace();
        }
    }
    // endregion
}
