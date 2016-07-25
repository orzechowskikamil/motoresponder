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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.*;


public class AmountBasedAlreadyRespondedTest {

    private SMSUtility smsUtility;
    private AmountBasedAlreadyResponded amountBasedAlreadyResponded;
    private CallsUtility callsUtility;
    private Date dateOfLastSMSSentByUser;
    private Date dateOfLastSMSSentByApp;
    private Date dateOfLastCallMadeByUser;
    private String FAKE_INCOMING_PHONE_NUMBER = "777777777";
    private Settings settings;
    private int LIMIT_OF_RESPONSES = 1;

    @Before
    public void setUp() throws Exception {
        this.callsUtility = Mockito.mock(CallsUtility.class);
        this.smsUtility = Mockito.mock(SMSUtility.class);
        this.settings = Mockito.mock(Settings.class);

        this.dateOfLastSMSSentByUser = null;
        this.dateOfLastSMSSentByApp = null;
        this.dateOfLastCallMadeByUser = null;

        this.stubCallsUtility();
        this.stubSMSUtility();

        when(this.settings.getLimitOfResponses()).thenReturn(this.LIMIT_OF_RESPONSES);

        this.amountBasedAlreadyResponded = new AmountBasedAlreadyResponded(this.callsUtility, this.smsUtility);
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
    public void testLimitExceed() {
        when(this.settings.getLimitOfResponses()).thenReturn(1);
        when(this.settings.getLimitOfGeolocationResponses()).thenReturn(3);
        when(this.smsUtility.howManyOutgoingSMSSentAfterDate(any(Date.class), anyString(), eq(true))).thenReturn(2);


        assertTrue(this.amountBasedAlreadyResponded.isAutoResponsesLimitExceeded(new CallRespondingSubject(FAKE_INCOMING_PHONE_NUMBER, new Date(), this.settings)));

        when(this.smsUtility.howManyOutgoingSMSSentAfterDate(any(Date.class), anyString(), eq(true))).thenReturn(0);

        assertFalse(this.amountBasedAlreadyResponded.isAutoResponsesLimitExceeded(new CallRespondingSubject(FAKE_INCOMING_PHONE_NUMBER, new Date(), this.settings)));

        when(this.smsUtility.howManyOutgoingSMSSentAfterDate(any(Date.class), anyString(), eq(true))).thenReturn(0);

        assertFalse(this.amountBasedAlreadyResponded.isAutoResponsesLimitExceeded(new GeolocationRequestRespondingSubject(FAKE_INCOMING_PHONE_NUMBER, "", new Date(), this.settings)));

        when(this.smsUtility.howManyOutgoingSMSSentAfterDate(any(Date.class), anyString(), eq(true))).thenReturn(10);

        assertTrue(this.amountBasedAlreadyResponded.isAutoResponsesLimitExceeded(new GeolocationRequestRespondingSubject(FAKE_INCOMING_PHONE_NUMBER, "", new Date(), this.settings)));
    }


    @Test
    public void testAmountOfAutomaticalResponsesIfUserNeverResponded() {
        boolean SENT_BY_USER = false;
        when(this.smsUtility.getDateOfLastSMSSent(FAKE_INCOMING_PHONE_NUMBER, SENT_BY_USER)).thenReturn(null);

        this.amountBasedAlreadyResponded.isAutoResponsesLimitExceeded(new CallRespondingSubject(FAKE_INCOMING_PHONE_NUMBER, new Date(), this.settings));
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

    private void testCaseLimitExceed(boolean limitExceed, Date lastCallMadeByUser, Date lastSMSSentByApp, Date lastSMSSentByUser) {
        this.dateOfLastCallMadeByUser = lastCallMadeByUser;
        this.dateOfLastSMSSentByApp = lastSMSSentByApp;
        this.dateOfLastSMSSentByUser = lastSMSSentByUser;
        this.expectIsLimitExceed(limitExceed);
    }


    private void expectIsLimitExceed(boolean exceeded) {
        Assert.assertEquals(exceeded, this.amountBasedAlreadyResponded.isAutoResponsesLimitExceeded(new CallRespondingSubject(FAKE_INCOMING_PHONE_NUMBER, new Date(), this.settings)));
    }

    private void expectIsRespondedIsUserRespondedSinceToBe(boolean expectedResult, Date dateToCheck) {
        Assert.assertEquals(expectedResult, this.amountBasedAlreadyResponded.isUserRespondedSince(new CallRespondingSubject(FAKE_INCOMING_PHONE_NUMBER, dateToCheck, this.settings)));
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
                        ? AmountBasedAlreadyRespondedTest.this.dateOfLastSMSSentByApp
                        : AmountBasedAlreadyRespondedTest.this.dateOfLastSMSSentByUser;

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
                        ? AmountBasedAlreadyRespondedTest.this.dateOfLastSMSSentByApp
                        : AmountBasedAlreadyRespondedTest.this.dateOfLastSMSSentByUser;
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
                if (AmountBasedAlreadyRespondedTest.this.dateOfLastCallMadeByUser == null) {
                    return false;
                }
                return dateToCheck.getTime() < AmountBasedAlreadyRespondedTest.this.dateOfLastCallMadeByUser.getTime();
            }
        });
    }

}
