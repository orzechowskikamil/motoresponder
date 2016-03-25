package com.medziku.motoresponder.logic;

import com.google.common.base.Predicate;
import com.medziku.motoresponder.callbacks.SendSMSCallback;
import com.medziku.motoresponder.utils.NotificationUtility;
import com.medziku.motoresponder.utils.SMSUtility;
import com.medziku.motoresponder.utils.SettingsUtility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;


public class RespondingTaskTest {

    private ExposedRespondingTask respondingTask;
    private RespondingDecision respondingDecision;
    private SettingsUtility settingsUtility;
    private NotificationUtility notificationUtility;
    private SMSUtility smsUtility;
    private Predicate<Boolean> returnCallback;
    private String FAKE_PHONE_NUMBER = "777777777";
    private ResponsePreparator responsePreparator;

    @Before
    public void setUp() {

        this.respondingDecision = Mockito.mock(RespondingDecision.class);
        this.settingsUtility = Mockito.mock(SettingsUtility.class);
        this.notificationUtility = Mockito.mock(NotificationUtility.class);
        this.smsUtility = Mockito.mock(SMSUtility.class);
        this.responsePreparator = Mockito.mock(ResponsePreparator.class);
        this.returnCallback = Mockito.mock(Predicate.class);


        this.respondingTask = new ExposedRespondingTask(
                this.respondingDecision,
                this.settingsUtility,
                this.notificationUtility,
                this.smsUtility,
                this.responsePreparator,
                this.returnCallback);
    }


    @Test
    public void testOfResponse() throws Exception {
        when(this.respondingDecision.shouldRespond(anyString())).thenReturn(true);
        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.smsUtility).sendSMS(anyString(), anyString(), any(SendSMSCallback.class));

    }

    @Test
    public void testOfNotifications() {
        when(this.settingsUtility.isShowingPendingNotificationEnabled()).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationUtility).showOngoingNotification(anyString(), anyString(), anyString());
        verify(this.notificationUtility).hideOngoingNotification();
    }

    @Test
    public void testOfTermination() throws Exception {
        this.respondingTask.terminated = true;
        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.smsUtility, times(0)).sendSMS(anyString(), anyString(), any(SendSMSCallback.class));
    }

}

class ExposedRespondingTask extends RespondingTask {

    public boolean terminated = false;

    public ExposedRespondingTask(RespondingDecision respondingDecision,
                                 SettingsUtility settingsUtility,
                                 NotificationUtility notificationUtility,
                                 SMSUtility smsUtility,
                                 ResponsePreparator responsePreparator,
                                 Predicate<Boolean> resultCallback) {
        super(respondingDecision, settingsUtility, notificationUtility, smsUtility, responsePreparator, resultCallback);
    }


    public void callLogic(RespondingSubject subject) {
        this.handleRespondingTask(subject);
    }

    @Override
    protected boolean isTerminated() {

        return this.terminated;
    }
}