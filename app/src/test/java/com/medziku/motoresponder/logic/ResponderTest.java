package com.medziku.motoresponder.logic;

import android.content.Context;
import android.test.mock.MockContext;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.callbacks.SMSReceivedCallback;
import com.medziku.motoresponder.utils.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class ResponderTest {

    private String FAKE_PHONE_NUMBER = "777777777";
    private ExposedResponder responder;
    private MockContext context;


    @Before
    public void setUp() throws Exception {
        this.context = new MockContext();
        this.responder = new ExposedResponder(this.context);
    }

    @Test
    public void testReactionOnCall() {
        this.responder.startResponding();
        this.responder.currentCallCallback.apply(this.FAKE_PHONE_NUMBER);


        verify(this.responder.respondingTasksQueueMock, times(1)).createAndExecuteRespondingTask(anyString());
    }

    @Test
    public void testReactionOnSMS() {
        this.responder.startResponding();
        this.responder.currentSMSCallback.onSMSReceived(this.FAKE_PHONE_NUMBER, "mock message");


        verify(this.responder.respondingTasksQueueMock, times(1)).createAndExecuteRespondingTask(anyString());
    }


    @Test
    public void testIfItStartOnlyOnce() {
        this.responder.startResponding();
        this.responder.startResponding();
        this.responder.currentSMSCallback.onSMSReceived(this.FAKE_PHONE_NUMBER, "mock message");


        verify(this.responder.respondingTasksQueueMock, times(1)).createAndExecuteRespondingTask(anyString());
    }

    @Test
    public void testOfStoppingProcess() {
        this.responder.startResponding();
        this.responder.stopResponding();
        verify(this.responder.callsUtility, times(1)).stopListeningForCalls();
        verify(this.responder.smsUtility, times(1)).stopListeningForSMS();
    }


    @Test
    public void testOfCancelOnUnlock() {
        this.responder.startResponding();
        this.responder.currentLockStateCallback.apply(false);

        verify(this.responder.respondingTasksQueueMock, times(1)).cancelAllHandling();
        verify(this.responder.respondingTasksQueueMock, times(0)).createAndExecuteRespondingTask(anyString());
    }


}


class ExposedResponder extends Responder {
    public Predicate<Boolean> currentLockStateCallback;
    public Predicate<String> currentCallCallback;
    public SMSReceivedCallback currentSMSCallback;
    public RespondingTasksQueue respondingTasksQueueMock;
    public CallsUtility mockCallsUtility;
    public SMSUtility mockSMSUtility;


    public ExposedResponder(Context context) {
        super(context);
    }

    @Override
    protected void createUtilities() {
        try {
            this.lockStateUtility = this.createMockLockStateUtility();
            this.callsUtility = this.createMockCallsUtility();
            this.smsUtility = this.createMockSMSUtility();
            this.settingsUtility = this.createMockSettingsUtility();
            this.sensorsUtility = this.createSensorsUtility();

            this.mockSMSUtility = this.smsUtility;
            this.mockCallsUtility = this.callsUtility;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SensorsUtility createSensorsUtility() {
        return mock(SensorsUtility.class);
    }

    private SettingsUtility createMockSettingsUtility() {
        SettingsUtility mock = mock(SettingsUtility.class);

        when(mock.isServiceEnabled()).thenReturn(true);
        return mock;
    }

    @Override
    protected RespondingTasksQueue createRespondingTasksQueue() {
        RespondingTasksQueue mock = mock(RespondingTasksQueue.class);
        this.respondingTasksQueueMock = mock;
        return mock;
    }

    private LockStateUtility createMockLockStateUtility() throws Exception {
        LockStateUtility mock = mock(LockStateUtility.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Predicate callback = (Predicate)invocation.getArguments()[0];
                ExposedResponder.this.currentLockStateCallback = callback;
                return null;

            }
        }).when(mock).listenToLockStateChanges(any(Predicate.class));
        return mock;
    }

    private CallsUtility createMockCallsUtility() {
        CallsUtility mock = mock(CallsUtility.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Predicate callback =  (Predicate)invocation.getArguments()[0];
                ExposedResponder.this.currentCallCallback = callback;
                return null;
            }
        }).when(mock).listenForUnansweredCalls(any(Predicate.class));
        return mock;
    }

    private SMSUtility createMockSMSUtility() {
        SMSUtility mock = mock(SMSUtility.class);


        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                SMSReceivedCallback callback = (SMSReceivedCallback)invocation.getArguments()[0];
                ExposedResponder.this.currentSMSCallback = callback;
                return null;
            }
        }).when(mock).listenForSMS(any(SMSReceivedCallback.class));
        return mock;
    }
}

