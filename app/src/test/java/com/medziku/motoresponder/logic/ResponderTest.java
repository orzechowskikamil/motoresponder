package com.medziku.motoresponder.logic;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;


public class ResponderTest {

    private static final String GEOLOCATION_REQUEST_PATTERN = "Where are you";
    private String FAKE_PHONE_NUMBER = "777777777";
    private ExposedResponder responder;
    private Context context;

    @Before
    public void setUp() throws Exception {
        this.context = mock(Context.class);
        this.responder = new ExposedResponder(this.context);
        when(this.responder.mockSettings.getGeolocationRequestPatterns()).thenReturn(new String[]{GEOLOCATION_REQUEST_PATTERN});
        when(this.responder.mockSettings.isResponderEnabled()).thenReturn(true);
        when(this.responder.mockSettings.isRespondingForSMSEnabled()).thenReturn(true);
        when(this.responder.mockSettings.isRespondingForCallsEnabled()).thenReturn(true);

    }

    @Test
    public void testReactionOnCall() {
        this.responder.startResponding();
        this.responder.currentCallCallback.apply(this.FAKE_PHONE_NUMBER);

        verify(this.responder.respondingTasksQueueMock, times(1)).createAndExecuteRespondingTask(any(CallRespondingSubject.class));
    }

    @Test
    public void testReactionOnSMS() {
        this.responder.startResponding();
        this.responder.currentSMSCallback.apply(new SMSObject(this.FAKE_PHONE_NUMBER, "mock message"));

        verify(this.responder.respondingTasksQueueMock, times(1)).createAndExecuteRespondingTask(any(SMSRespondingSubject.class));
    }

    @Test
    public void testReactionOnGeolocationRequest() {
        this.responder.startResponding();
        this.responder.currentSMSCallback.apply(new SMSObject(this.FAKE_PHONE_NUMBER, "Hey Bert, " + GEOLOCATION_REQUEST_PATTERN + " ? hahha"));


        verify(this.responder.respondingTasksQueueMock, times(1)).createAndExecuteRespondingTask(any(GeolocationRequestRespondingSubject.class));
    }


    @Test
    public void testReactionOnSMSAndCallDisabled() {
        when(this.responder.mockSettings.isRespondingForSMSEnabled()).thenReturn(false);
        when(this.responder.mockSettings.isRespondingForCallsEnabled()).thenReturn(false);

        verify(this.responder.respondingTasksQueueMock, times(0)).createAndExecuteRespondingTask(any(SMSRespondingSubject.class));
        verify(this.responder.respondingTasksQueueMock, times(0)).createAndExecuteRespondingTask(any(CallRespondingSubject.class));
    }

    @Test
    public void testReactionOnSMSAndCallCallback() {
        this.responder.startResponding();
        this.responder.changedSMSCallSettingCallback.apply(true);

        assertTrue(this.responder.listenToIncomingAccordingToSettingsCalled);
    }


    @Test
    public void testIfItStartOnlyOnce() {
        this.responder.startResponding();
        this.responder.startResponding();
        this.responder.currentSMSCallback.apply(new SMSObject(this.FAKE_PHONE_NUMBER, "mock message"));


        verify(this.responder.respondingTasksQueueMock, times(1)).createAndExecuteRespondingTask(any(RespondingSubject.class));
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
        when(this.responder.mockSettings.isAssumingScreenUnlockedAsNotRidingEnabled()).thenReturn(true);
        this.responder.startResponding();
        this.responder.currentLockStateCallback.apply(false);

        verify(this.responder.respondingTasksQueueMock, times(1)).cancelAllHandling();
        verify(this.responder.respondingTasksQueueMock, times(0)).createAndExecuteRespondingTask(any(RespondingSubject.class));
    }


}


class ExposedResponder extends Responder {
    public Predicate<Boolean> currentLockStateCallback;
    public Predicate<String> currentCallCallback;
    public Predicate<SMSObject> currentSMSCallback;
    public RespondingTasksQueue respondingTasksQueueMock;
    public CallsUtility mockCallsUtility;
    public SMSUtility mockSMSUtility;
    public Settings mockSettings;
    public Predicate<Boolean> changedSMSCallSettingCallback;
    public boolean listenToIncomingAccordingToSettingsCalled = false;

    public ExposedResponder(Context context) {
        super(context);
    }

    @Override
    public void listenToIncomingAccordingToSettings() {
        super.listenToIncomingAccordingToSettings();
        this.listenToIncomingAccordingToSettingsCalled = true;
    }

    @Override
    protected void createUtilities() {
        try {
            this.lockStateUtility = this.createMockLockStateUtility();
            this.callsUtility = this.createMockCallsUtility();
            this.smsUtility = this.createMockSMSUtility();
            this.settings = this.createMockSettingsUtility();
            this.sensorsUtility = this.createSensorsUtility();

            this.mockSMSUtility = this.smsUtility;
            this.notificationUtility = mock(NotificationUtility.class);
            this.intentsUtility = this.createIntentsUtility();
            this.mockCallsUtility = this.callsUtility;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected RespondingTasksQueue createRespondingTasksQueue() {
        RespondingTasksQueue mock = mock(RespondingTasksQueue.class);
        this.respondingTasksQueueMock = mock;
        return mock;
    }

    @Override
    protected Settings createSettings() {
        this.mockSettings = mock(Settings.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return null;
            }
        }).when(this.mockSettings).listenToSettingChange(anyString(), any(Predicate.class));

        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) throws Throwable {
                Predicate callback = (Predicate<Boolean>) invocation.getArguments()[1];
                ExposedResponder.this.changedSMSCallSettingCallback = callback;
                return null;
            }
        }).when(this.mockSettings).listenToSettingChange(anyString(), any(Predicate.class));
        return this.mockSettings;
    }

    private IntentsUtility createIntentsUtility() {
        IntentsUtility mock = mock(IntentsUtility.class);
        when(mock.createIntent(any(Context.class), any(Class.class))).thenAnswer(new Answer<Intent>() {
            public Intent answer(InvocationOnMock invocation) throws Throwable {
                return mock(Intent.class);
            }
        });
        when(mock.createIntentFilter(anyString())).thenReturn(mock(IntentFilter.class));
        return mock;
    }

    private SensorsUtility createSensorsUtility() {
        return mock(SensorsUtility.class);
    }

    private Settings createMockSettingsUtility() {
        Settings mock = mock(Settings.class);

        when(mock.isResponderEnabled()).thenReturn(true);
        return mock;
    }

    private LockStateUtility createMockLockStateUtility() throws Exception {
        LockStateUtility mock = mock(LockStateUtility.class);

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Predicate callback = (Predicate) invocation.getArguments()[0];
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
                Predicate callback = (Predicate) invocation.getArguments()[0];
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
                Predicate<SMSObject> callback = (Predicate<SMSObject>) invocation.getArguments()[0];
                ExposedResponder.this.currentSMSCallback = callback;
                return null;
            }
        }).when(mock).listenForSMS(any(Predicate.class));
        return mock;
    }


}

