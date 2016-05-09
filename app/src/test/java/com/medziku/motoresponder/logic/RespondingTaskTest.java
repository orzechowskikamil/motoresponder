package com.medziku.motoresponder.logic;

import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.NotificationUtility;
import com.medziku.motoresponder.utils.SMSUtility;
import com.medziku.motoresponder.utils.SharedPreferencesUtility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.internal.verification.Calls;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;


public class RespondingTaskTest {

    private ExposedRespondingTask respondingTask;
    private RespondingDecision respondingDecision;
    private Settings settings;
    private NotificationUtility notificationUtility;
    private SMSUtility smsUtility;
    private Predicate<Boolean> returnCallback;
    private String FAKE_PHONE_NUMBER = "777777777";
    private ResponsePreparator responsePreparator;


    @Before
    public void setUp() {

        this.respondingDecision = Mockito.mock(RespondingDecision.class);
        this.settings = Mockito.mock(Settings.class);
        this.notificationUtility = Mockito.mock(NotificationUtility.class);
        this.smsUtility = Mockito.mock(SMSUtility.class);
        this.responsePreparator = Mockito.mock(ResponsePreparator.class);
        this.returnCallback = Mockito.mock(Predicate.class);
        DecisionLog log = Mockito.mock(DecisionLog.class);


        this.respondingTask = new ExposedRespondingTask(
                this.respondingDecision,
                this.settings,
                this.notificationUtility,
                this.smsUtility,
                this.responsePreparator,
                log,
                this.returnCallback);
    }


    @Test
    public void testOfResponse() throws Exception {
        when(this.respondingDecision.shouldRespond(anyString())).thenReturn(true);
        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.smsUtility).sendSMS(anyString(), anyString(), any(Predicate.class));

    }

    @Test
    public void testOfNotifications() {
        when(this.settings.isShowingPendingNotificationEnabled()).thenReturn(true);
        when(this.respondingDecision.shouldRespond(anyString())).thenReturn(false);
        when(this.settings.isShowingSummaryNotificationEnabled()).thenReturn(false);
        when(this.settings.isShowingDebugNotificationEnabled()).thenReturn(false);


        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));
        verify(this.notificationUtility, times(0)).showBigTextNotification(anyString(), anyString(), anyString());

        when(this.settings.isShowingSummaryNotificationEnabled()).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));
        verify(this.notificationUtility, times(0)).showBigTextNotification(anyString(), anyString(), anyString());

        when(this.respondingDecision.shouldRespond(anyString())).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationUtility, times(3)).showOngoingNotification(anyString(), anyString(), anyString());
        verify(this.notificationUtility, times(1)).showBigTextNotification(anyString(), anyString(), anyString());
        verify(this.notificationUtility, times(3)).hideNotification();
    }

    @Test
    public void testOfTermination() throws Exception {
        this.respondingTask.terminated = true;
        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.smsUtility, times(0)).sendSMS(anyString(), anyString(), any(Predicate.class));
    }

    @Test
    public void testOfRetryingSendingSms() {

        final Predicate<String>[] sendSmsCallback = new Predicate[]{null};

        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                sendSmsCallback[0] = (Predicate<String>) invocation.getArguments()[2];
                return null;
            }
        }).when(this.smsUtility).sendSMS(anyString(), anyString(), any(Predicate.class));

        this.respondingTask.sendSMSAndRetryOnFail(FAKE_PHONE_NUMBER, "test message", 2);
        sendSmsCallback[0].apply("some error");
        sendSmsCallback[0].apply(null);

        verify(this.smsUtility, times(2)).sendSMS(anyString(), anyString(), any(Predicate.class));

        this.respondingTask.sendSMSAndRetryOnFail(FAKE_PHONE_NUMBER, "test message", 2);
        sendSmsCallback[0].apply(null);
        // previously it was called two times and now should be called once so all = 3
        verify(this.smsUtility, times(3)).sendSMS(anyString(), anyString(), any(Predicate.class));
    }

}

class ExposedRespondingTask extends RespondingTask {

    public boolean terminated = false;

    public ExposedRespondingTask(RespondingDecision respondingDecision,
                                 Settings sharedPreferencesUtility,
                                 NotificationUtility notificationUtility,
                                 SMSUtility smsUtility,
                                 ResponsePreparator responsePreparator,
                                 DecisionLog log,
                                 Predicate<Boolean> resultCallback) {
        super(respondingDecision, sharedPreferencesUtility, notificationUtility, smsUtility, responsePreparator, log, resultCallback);
    }


    public void callLogic(RespondingSubject subject) {
        this.handleRespondingTask(subject);
        this.onPostExecute(true);
    }

    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
    }

    @Override
    protected boolean isTerminated() {

        return this.terminated;
    }
}
