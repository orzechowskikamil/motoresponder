package com.medziku.motoresponder.mocks;

import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.SMSObject;
import com.medziku.motoresponder.utils.SMSUtility;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SMSUtilityMock {

    public SMSUtility mock;
    private Predicate<SMSObject> listenForSMSCallback;
    private MockedSMSEntry[] smsLog;

    public SMSUtilityMock() {
        this.mock = mock(SMSUtility.class);

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                SMSUtilityMock.this.listenForSMSCallback = (Predicate<SMSObject>) invocation.getArguments()[0];
                return null;
            }
        }).when(this.mock).listenForSMS(any(Predicate.class));

        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                SMSUtilityMock.this.listenForSMSCallback = null;
                return null;
            }
        }).when(this.mock).stopListeningForSMS();

        when(this.mock.getDateOfLastSMSSent(anyString(), anyBoolean())).thenAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) {
                String phoneNumber = (String) invocation.getArguments()[0];
                Boolean shouldBeSentByOurApp = (Boolean) invocation.getArguments()[1];

                MockedSMSEntry oldest = null;

                for (MockedSMSEntry entry : SMSUtilityMock.this.smsLog) {
                    if (entry.sentByOurApp
                            && entry.phoneNumber.equals(phoneNumber)
                            && entry.date.getTime() > ((oldest != null) ? oldest.date.getTime() : 0)) {
                        oldest = entry;
                    }
                }
                if (oldest != null) {
                    return oldest.date;
                }
                return null;
            }
        });
    }

    public void setMockedSMSLog(MockedSMSEntry[] smsLog) {
        this.smsLog = smsLog;
    }

    public void simulateIncomingSMS(SMSObject object) {
        this.listenForSMSCallback.apply(object);
    }
}

class MockedSMSEntry {
    Date date;
    boolean sentByOurApp;
    String phoneNumber;
}
