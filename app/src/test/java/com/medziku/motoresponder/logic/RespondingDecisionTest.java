package com.medziku.motoresponder.logic;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

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
    public void runBeforeTests() {
        this.fakeRespondingSubject = new CallRespondingSubject(this.FAKE_PHONE_NUMBER);
        this.alreadyResponded = Mockito.mock(AlreadyResponded.class);
        this.userRide = Mockito.mock(UserRide.class);
        this.numberRules = Mockito.mock(NumberRules.class);
        this.deviceUnlocked = Mockito.mock(DeviceUnlocked.class);
        this.settings = Mockito.mock(Settings.class);
        when(this.settings.getLimitOfGeolocationResponses()).thenReturn(2);
        when(this.settings.getLimitOfResponses()).thenReturn(1);
        DecisionLog log = Mockito.mock(DecisionLog.class);
        this.respondingDecision = new RespondingDecision(this.userRide, this.numberRules, this.alreadyResponded, this.deviceUnlocked, this.settings, log);


        this.setDeviceUnlockedIsNotRidingReturnValue(false);
        this.setNumberRulesAllowRespondingReturnValue(true);
        this.setAlreadyRespondedIsAutomaticalResponseLastReturnValue(false);
        this.setAlreadyRespondedIsUserRespondedSinceReturnValue(false);
        this.setUserRideIsUserRidingReturnValue(true);
        this.setAlreadyRespondedGetAmountOfAutomaticalResponsesSent(1);
    }


    @Test
    public void unlockedScreenMakesNegativeDecision() {
        this.setDeviceUnlockedIsNotRidingReturnValue(true);
        this.expectRespondingDecisionShouldRespondToBe(false);
    }


    @Test
    public void notFillingNumberRulesMakeNegativeDecision() {
        this.setNumberRulesAllowRespondingReturnValue(false);
        this.expectRespondingDecisionShouldRespondToBe(false);
    }

    @Test
    public void userRespondedSinceMessageReceivedMakeNegativeDecision() {
        this.setAlreadyRespondedIsUserRespondedSinceReturnValue(true);
        this.expectRespondingDecisionShouldRespondToBe(false);
    }


    @Test
    public void userNotRidingMakeNegativeDecision() {
        this.setUserRideIsUserRidingReturnValue(false);
        this.expectRespondingDecisionShouldRespondToBe(false);
    }

    @Test
    public void allFullfilledConditionsMakePositiveDecision() {
        this.expectRespondingDecisionShouldRespondToBe(true);

    }

    @Test
    public void tooMuchAutomaticalResponsesMakeNegativeDecision() {
        this.setAlreadyRespondedGetAmountOfAutomaticalResponsesSent(5);
        this.expectRespondingDecisionShouldRespondToBe(false);
    }

    @Test
    public void lateUnlockOfDeviceMakeNegativeDecision() {
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
    public void lateResponseByUserMakeNegativeDecision() {
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

    private void expectRespondingDecisionShouldRespondToBe(boolean expectedValue) {
        Assert.assertTrue(this.respondingDecision.shouldRespond(this.fakeRespondingSubject) == expectedValue);
    }

    private void setNumberRulesAllowRespondingReturnValue(boolean returnValue) {
        when(this.numberRules.numberRulesAllowResponding(this.FAKE_PHONE_NUMBER)).thenReturn(returnValue);
    }

    private void setDeviceUnlockedIsNotRidingReturnValue(boolean returnValue) {
        when(this.deviceUnlocked.isNotRidingBecausePhoneUnlocked()).thenReturn(returnValue);
    }

    private void setUserRideIsUserRidingReturnValue(boolean returnValue) {
        when(this.userRide.isUserRiding()).thenReturn(returnValue);
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
