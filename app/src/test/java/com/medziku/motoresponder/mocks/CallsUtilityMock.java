package com.medziku.motoresponder.mocks;

import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.CallsUtility;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public class CallsUtilityMock {

    public CallsUtility mock;
    private Predicate<String> unansweredCallCallback;
    private MockedCallsLogEntry[] mockedCallsLog;

    public CallsUtilityMock() {
        this.mock = mock(CallsUtility.class);
        this.setupMock();
    }

    public void setMockedCallsLog(MockedCallsLogEntry[] mockedCallsLog) {
        this.mockedCallsLog = mockedCallsLog;
    }

    public void simulateUnansweredCall(String phoneNumber) {
        if (this.unansweredCallCallback != null) {
            this.unansweredCallCallback.apply(phoneNumber);
        }
    }

    private void setupMock() {
        doAnswer(new Answer() {
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CallsUtilityMock.this.unansweredCallCallback = (Predicate<String>) invocation.getArguments()[0];
                return null;
            }
        }).when(this.mock).listenForUnansweredCalls(any(Predicate.class));

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                CallsUtilityMock.this.unansweredCallCallback = null;
                return null;
            }
        }).when(this.mock).stopListeningForCalls();

        doAnswer(new Answer() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                for (MockedCallsLogEntry entry : CallsUtilityMock.this.mockedCallsLog) {
                    Date date = (Date) invocation.getArguments()[0];
                    String phoneNumber = (String) invocation.getArguments()[1];

                    if (entry.date.getTime() > date.getTime() && entry.isOutgoing == true && entry.phoneNumber.equals(phoneNumber)) {
                        return true;
                    }
                }
                return false;
            }
        }).when(this.mock).wasOutgoingCallAfterDate(any(Date.class), anyString());
    }
}

class MockedCallsLogEntry {
    Date date;
    boolean isOutgoing;
    String phoneNumber;
}
