package com.medziku.motoresponder.logic;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;


public class RespondingDecisionTest {

    private UserRide userRide;
    private NumberRules numberRules;
    private RespondingDecision respondingDecision;
    private AlreadyResponded alreadyResponded;
    private DeviceUnlocked deviceUnlocked;
    private String FAKE_PHONE_NUMBER = "777777777";


    @Before
    public void runBeforeTests() {
        this.alreadyResponded = Mockito.mock(AlreadyResponded.class);
        this.userRide = Mockito.mock(UserRide.class);
        this.numberRules = Mockito.mock(NumberRules.class);
        this.deviceUnlocked = Mockito.mock(DeviceUnlocked.class);
        this.respondingDecision = new RespondingDecision(this.userRide, this.numberRules, this.alreadyResponded, this.deviceUnlocked);


        this.setDeviceUnlockedIsNotRidingReturnValue(false);
        this.setNumberRulesAllowRespondingReturnValue(true);
        this.setAlreadyRespondedIsAutomaticalResponseLastReturnValue(false);
        this.setAlreadyRespondedIsUserRespondedSinceReturnValue(false);
        this.setUserRideIsUserRidingReturnValue(true);
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
    public void automaticalResponseIsLastMakeNegativeDecision() {
        this.setAlreadyRespondedIsAutomaticalResponseLastReturnValue(true);
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
    public void lateUnlockOfDeviceMakeNegativeDecision() {
        Mockito.when(this.userRide.isUserRiding()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                // simulate unlocking screen after determining location.
                Mockito.when(RespondingDecisionTest.this.deviceUnlocked.isNotRidingBecausePhoneUnlocked()).thenReturn(true);
                return true;
            }
        });

        this.expectRespondingDecisionShouldRespondToBe(false);
    }


    @Test
    public void lateResponseByUserMakeNegativeDecision() {
        Mockito.when(this.userRide.isUserRiding()).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                // simulate user answered after determining location.
                Mockito.when(
                        RespondingDecisionTest.this.alreadyResponded.isUserRespondedSince(Matchers.any(Date.class), Matchers.anyString())
                ).thenReturn(true);
                return true;
            }
        });

        this.expectRespondingDecisionShouldRespondToBe(false);
    }


    // region helper methods

    private void expectRespondingDecisionShouldRespondToBe(boolean expectedValue) {
        Assert.assertTrue(this.respondingDecision.shouldRespond(this.FAKE_PHONE_NUMBER) == expectedValue);
    }

    private void setNumberRulesAllowRespondingReturnValue(boolean returnValue) {
        Mockito.when(this.numberRules.numberRulesAllowResponding(this.FAKE_PHONE_NUMBER)).thenReturn(returnValue);
    }

    private void setDeviceUnlockedIsNotRidingReturnValue(boolean returnValue) {
        Mockito.when(this.deviceUnlocked.isNotRidingBecausePhoneUnlocked()).thenReturn(returnValue);
    }


    private void setUserRideIsUserRidingReturnValue(boolean returnValue) {
        Mockito.when(this.userRide.isUserRiding()).thenReturn(returnValue);
    }

    private void setAlreadyRespondedIsUserRespondedSinceReturnValue(boolean returnValue) {
        Mockito.when(this.alreadyResponded.isUserRespondedSince(Matchers.any(Date.class), Matchers.anyString())).thenReturn(returnValue);
    }

    private void setAlreadyRespondedIsAutomaticalResponseLastReturnValue(boolean returnValue) {
        Mockito.when(this.alreadyResponded.isAutomaticalResponseLast(this.FAKE_PHONE_NUMBER)).thenReturn(returnValue);
    }

    // endregion


}