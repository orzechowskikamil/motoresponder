package com.medziku.motoresponder.logic;

import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;

public class TimeBasedAlreadyRespondedTest {
    private TimeBasedAlreadyResponded timeBasedAlreadyResponded;
    private Settings settings;
    private String NUMBER = "777888999";
    private HashMap<String, Long> timestamps = new HashMap<>();
    private int TIME_LIMIT_MINUTES = 1;

    @Before
    public void setUp() {
        this.settings = mock(Settings.class);
        this.timeBasedAlreadyResponded = new TimeBasedAlreadyResponded(this.settings);
        when(this.settings.getTimestampsOfLastResponses()).thenReturn(this.timestamps);
        when(this.settings.getDelayBetweenResponsesMinutes()).thenReturn(this.TIME_LIMIT_MINUTES);
    }

    @Test
    public void testOfNeverSendResponseBefore() {
        RespondingSubject sms = new SMSRespondingSubject(NUMBER, "aaa", new Date(), this.settings);

        boolean autoResponsesLimitExceeded = this.timeBasedAlreadyResponded.isAutoResponsesLimitExceeded(sms);
        assertFalse(autoResponsesLimitExceeded);
    }

    @Test
    public void testOfResponseSent20SecondsAgo() {
        Date date = new Date();
        long timestampOfLastResponse = (date.getTime() / 1000) - 20;

        this.timestamps.put(NUMBER, timestampOfLastResponse);

        RespondingSubject sms = new SMSRespondingSubject(NUMBER, "aaa", date, this.settings);
        boolean result = this.timeBasedAlreadyResponded.isAutoResponsesLimitExceeded(sms);
        assertTrue(result);
    }

    @Test
    public void testOfIsUserRespondedSince() {
        try {
            this.timeBasedAlreadyResponded.isUserRespondedSince(new SMSRespondingSubject(NUMBER, "aaa", new Date(), this.settings));
            fail();
        } catch (Exception e) {
            // success
        }
    }

    @Test
    public void testOfResponseSent20MinutesAgo() {
        Date date = new Date();
        long timestampOfLastResponse = (date.getTime() / 1000) - (20 * 60);

        this.timestamps.put(NUMBER, timestampOfLastResponse);

        RespondingSubject sms = new SMSRespondingSubject(NUMBER, "aaa", date, this.settings);
        boolean result = this.timeBasedAlreadyResponded.isAutoResponsesLimitExceeded(sms);
        assertFalse(result);
    }

    @Test
    public void testOfRememberAboutResponse() {
        RespondingSubject firstSMS = new SMSRespondingSubject(NUMBER, "aaa", new Date(), this.settings);

        long beforeOperationSeconds = new Date().getTime() / 1000;

        this.timeBasedAlreadyResponded.rememberAboutAutoResponse(firstSMS);
        verify(this.settings, times(1)).setTimestampsOfLastResponses(eq(timestamps));

        Long addedTimestamp = timestamps.get(NUMBER);
        long nowSeconds = new Date().getTime() / 1000;

        assertTrue(addedTimestamp <= nowSeconds && addedTimestamp >= beforeOperationSeconds);
    }
}
