package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.CallsUtility;
import com.medziku.motoresponder.utils.SMSUtility;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class AlreadyRespondedTest {

    private SMSUtility smsUtility;
    private AlreadyResponded alreadyResponded;
    private CallsUtility callsUtility;
    private Date dateOfLastSMSSentByUser;
    private Date dateOfLastSMSSentByApp;
    private Date dateOfLastCallMadeByUser;
    private String FAKE_INCOMING_PHONE_NUMBER = "777777777";

    @Before
    public void setUp() throws Exception {
        this.callsUtility = Mockito.mock(CallsUtility.class);
        this.smsUtility = Mockito.mock(SMSUtility.class);

        this.dateOfLastSMSSentByUser = null;
        this.dateOfLastSMSSentByApp = null;
        this.dateOfLastCallMadeByUser = null;

        this.stubCallsUtility();
        this.stubSMSUtility();

        this.alreadyResponded = new AlreadyResponded(this.callsUtility, this.smsUtility);
    }

    @Test
    public void testIsUserRespondedSince() throws Exception {
        // first case when sms and date and automatical sms are sent
        Date oldest = new Date(7000);
        Date mid = new Date(10000);
        Date newest = new Date(13000);

        Date beforeAll = new Date(6000);
        Date afterAll = new Date(15000);
        Date betweenMidAndNewest = new Date(12000);


        this.testCaseIsUserRespondedSince(true, beforeAll, oldest, mid, newest);
        this.testCaseIsUserRespondedSince(false, afterAll, oldest, mid, newest);
        this.testCaseIsUserRespondedSince(true, beforeAll, newest, mid, oldest);
        this.testCaseIsUserRespondedSince(false, afterAll, oldest, mid, newest);
        this.testCaseIsUserRespondedSince(false, beforeAll, null, mid, null);
        this.testCaseIsUserRespondedSince(true, beforeAll, null, mid, oldest);
        this.testCaseIsUserRespondedSince(false, afterAll, null, oldest, mid);
        this.testCaseIsUserRespondedSince(false, betweenMidAndNewest, oldest, newest, null);
        this.testCaseIsUserRespondedSince(true, betweenMidAndNewest, newest, oldest, null);
        this.testCaseIsUserRespondedSince(false, beforeAll, null, null, null);
    }


    @Test
    public void testWasAutomaticalResponseLast() {
        Date oldest = new Date(5000);
        Date mid = new Date(7500);
        Date newest = new Date(10000);


        this.testCaseWasAutomaticalResponseLast(true, mid, newest, oldest);
        this.testCaseWasAutomaticalResponseLast(false, newest, oldest, mid);
        this.testCaseWasAutomaticalResponseLast(false, oldest, mid, newest);
        this.testCaseWasAutomaticalResponseLast(false, newest, mid, oldest);
        this.testCaseWasAutomaticalResponseLast(false, oldest, null, newest);
        this.testCaseWasAutomaticalResponseLast(true, null, oldest, null);
        this.testCaseWasAutomaticalResponseLast(false, null, oldest, newest);
        this.testCaseWasAutomaticalResponseLast(true, oldest, newest, null);
        this.testCaseWasAutomaticalResponseLast(true, null, newest, oldest);

    }


    @Test
    public void testAmountOfAutomaticalResponsesIfUserNeverResponded() {
        boolean SENT_BY_USER = false;
        when(this.smsUtility.getDateOfLastSMSSent(FAKE_INCOMING_PHONE_NUMBER, SENT_BY_USER)).thenReturn(null);

        this.alreadyResponded.getAmountOfAutomaticalResponsesSinceUserResponded(FAKE_INCOMING_PHONE_NUMBER);
        ArgumentCaptor<Date> dateOfLastUserResponse = ArgumentCaptor.forClass(Date.class);
        verify(this.smsUtility, times(1)).howManyOutgoingSMSSentAfterDate(dateOfLastUserResponse.capture(), anyString(), eq(true));

        assertTrue(dateOfLastUserResponse.getValue().getSeconds() == 0);
    }

    // region test helpers


    private void testCaseIsUserRespondedSince(boolean expectedResult, Date dateToCheck, Date dateOfLastCall, Date dateOfLastAutomaticalSms, Date dateOfLastSms) {
        this.dateOfLastCallMadeByUser = dateOfLastCall;
        this.dateOfLastSMSSentByApp = dateOfLastAutomaticalSms;
        this.dateOfLastSMSSentByUser = dateOfLastSms;
        this.expectIsRespondedIsUserRespondedSinceToBe(expectedResult, dateToCheck);
    }

    private void testCaseWasAutomaticalResponseLast(boolean expectedResult, Date lastCallMadeByUser, Date lastSMSSentByApp, Date lastSMSSentByUser) {
        this.dateOfLastCallMadeByUser = lastCallMadeByUser;
        this.dateOfLastSMSSentByApp = lastSMSSentByApp;
        this.dateOfLastSMSSentByUser = lastSMSSentByUser;
        this.expectIsAutomaticalResponseLastToBe(expectedResult);
    }

    private void expectIsAutomaticalResponseLastToBe(boolean expectedResult) {
        Assert.assertEquals(expectedResult, this.alreadyResponded.isAutomaticalResponseLast(this.FAKE_INCOMING_PHONE_NUMBER));
    }

    private void expectIsRespondedIsUserRespondedSinceToBe(boolean expectedResult, Date dateToCheck) {
        Assert.assertEquals(expectedResult, this.alreadyResponded.isUserRespondedSince(dateToCheck, FAKE_INCOMING_PHONE_NUMBER));
    }


    private void stubSMSUtility() {
        when(
                this.smsUtility.wasOutgoingSMSSentAfterDate(Matchers.any(Date.class), Matchers.anyString(), Matchers.anyBoolean())
        ).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Date dateToCheck = (Date) invocation.getArguments()[0];
                boolean byOurApp = (Boolean) invocation.getArguments()[2];

                Date dateOfLastMessage;

                dateOfLastMessage = (byOurApp)
                        ? AlreadyRespondedTest.this.dateOfLastSMSSentByApp
                        : AlreadyRespondedTest.this.dateOfLastSMSSentByUser;

                if (dateOfLastMessage == null) {
                    return false;
                }
                return dateToCheck.getTime() < dateOfLastMessage.getTime();
            }

        });

        when(
                this.smsUtility.getDateOfLastSMSSent(Matchers.anyString(), Matchers.anyBoolean())
        ).thenAnswer(new Answer<Date>() {
            @Override
            public Date answer(InvocationOnMock invocation) throws Throwable {
                boolean byOurApp = (Boolean) invocation.getArguments()[1];

                return byOurApp
                        ? AlreadyRespondedTest.this.dateOfLastSMSSentByApp
                        : AlreadyRespondedTest.this.dateOfLastSMSSentByUser;
            }
        });
    }

    private void stubCallsUtility() {
        when(
                this.callsUtility.wasOutgoingCallAfterDate(Matchers.any(Date.class), Matchers.anyString())
        ).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Date dateToCheck = (Date) invocation.getArguments()[0];
                if (AlreadyRespondedTest.this.dateOfLastCallMadeByUser == null) {
                    return false;
                }
                return dateToCheck.getTime() < AlreadyRespondedTest.this.dateOfLastCallMadeByUser.getTime();
            }
        });
    }


// endregion

}