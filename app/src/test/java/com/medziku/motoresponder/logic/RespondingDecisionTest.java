package com.medziku.motoresponder.logic;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;


public class RespondingDecisionTest {

    private UserRide userRide;
    private NumberRules numberRules;
    private RespondingDecision respondingDecision;
    private AlreadyResponded alreadyResponded;
    private DeviceUnlocked deviceUnlocked;
    private String FAKE_PHONE_NUMBER = "777777777";
    private RespondingSubject fakeRespondingSubject;
    private Settings settings;


    @Before
    // test is build in manner, that all values in setup make positive decision, and in each test I am testing
    // if changing one parameter correctly makes decision negative.
    public void runBeforeTests() {
        this.fakeRespondingSubject = new CallRespondingSubject(this.FAKE_PHONE_NUMBER);
        this.alreadyResponded = Mockito.mock(AlreadyResponded.class);
        this.userRide = Mockito.mock(UserRide.class);
        this.numberRules = Mockito.mock(NumberRules.class);
        this.deviceUnlocked = Mockito.mock(DeviceUnlocked.class);
        this.settings = Mockito.mock(Settings.class);
        when(this.settings.getLimitOfGeolocationResponses()).thenReturn(2);
        when(this.settings.getLimitOfResponses()).thenReturn(1);
        when(this.settings.isAlreadyRespondedFilteringEnabled()).thenReturn(true);
        CustomLog log = Mockito.mock(CustomLog.class);
        this.respondingDecision = new RespondingDecision(this.userRide, this.numberRules, this.alreadyResponded, this.deviceUnlocked, this.settings, log);


        this.setDeviceUnlockedIsNotRidingReturnValue(false);
        this.setNumberRulesAllowRespondingReturnValue(true);
        this.setAlreadyRespondedIsAutomaticalResponseLastReturnValue(false);
        this.setAlreadyRespondedIsUserRespondedSinceReturnValue(false);
        this.setUserRideIsUserRidingReturnValue(true);

        this.setSensorCheckEnabled(true);
        this.setIsRidingAssumed(false);
        this.setAlreadyRespondedGetAmountOfAutomaticalResponsesSent(0);
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
        this.setAlreadyRespondedIsUserRespondedSinceReturnValue(true);
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
        this.setAlreadyRespondedIsUserRespondedSinceReturnValue(false);

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
    public void tooMuchAutomaticalResponsesMakeNegativeDecision() throws GPSNotAvailableException {
        this.setAlreadyRespondedGetAmountOfAutomaticalResponsesSent(5);
        this.expectRespondingDecisionShouldRespondToBe(false);

        this.setAlreadyRespondedGetAmountOfAutomaticalResponsesSent(1);
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
                        RespondingDecisionTest.this.alreadyResponded.isUserRespondedSince(Matchers.any(Date.class), Matchers.anyString())
                ).thenReturn(true);
                return true;
            }
        });

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

    private void setUserRideIsUserRidingReturnValue(boolean returnValue)  {
        try {
            when(this.userRide.isUserRiding()).thenReturn(returnValue);
        } catch (GPSNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void setAlreadyRespondedIsUserRespondedSinceReturnValue(boolean returnValue) {
        when(this.alreadyResponded.isUserRespondedSince(Matchers.any(Date.class), Matchers.anyString())).thenReturn(returnValue);
    }

    private void setAlreadyRespondedIsAutomaticalResponseLastReturnValue(boolean returnValue) {
        when(this.alreadyResponded.isAutomaticalResponseLast(this.FAKE_PHONE_NUMBER)).thenReturn(returnValue);
    }

    private void setAlreadyRespondedGetAmountOfAutomaticalResponsesSent(int val) {
        when(this.alreadyResponded.getAmountOfAutomaticalResponsesSinceUserResponded(anyString())).thenReturn(val);
    }
    // endregion
}
