package com.medziku.motoresponder.logic;

import android.os.PowerManager;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.mocks.SettingsMock;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.SMSUtility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;


public class RespondingTaskTest {


    private ExposedRespondingTask respondingTask;
    private RespondingDecision respondingDecision;
    private SettingsMock settings;
    private SMSUtility smsUtility;
    private Predicate<Boolean> returnCallback;
    private String FAKE_PHONE_NUMBER = "777777777";
    private ResponsePreparator responsePreparator;
    private ContactsUtility contactsUtility;
    private LockStateUtility lockStateUtility;
    private NotificationFactory notificationFactory;


    @Before
    public void setUp() {
        this.respondingDecision = Mockito.mock(RespondingDecision.class);
        this.settings = new SettingsMock();
        this.notificationFactory = Mockito.mock(NotificationFactory.class);
        this.smsUtility = Mockito.mock(SMSUtility.class);
        this.responsePreparator = Mockito.mock(ResponsePreparator.class);
        this.returnCallback = Mockito.mock(Predicate.class);
        this.contactsUtility = Mockito.mock(ContactsUtility.class);
        this.lockStateUtility = Mockito.mock(LockStateUtility.class);
        CustomLog log = Mockito.mock(CustomLog.class);


        when(this.settings.mock.isShowingPendingNotificationEnabled()).thenReturn(true);
        this.setRespondingDecisionShouldRespond(false);

        when(this.lockStateUtility.acquirePartialWakeLock()).thenReturn(Mockito.mock(PowerManager.WakeLock.class));

        this.respondingTask = new ExposedRespondingTask(
                this.respondingDecision,
                this.settings.mock,
                this.notificationFactory,
                this.smsUtility,
                this.contactsUtility,
                this.lockStateUtility,
                this.responsePreparator,
                log,
                this.returnCallback);
    }

    @Test
    public void testOfResponse() throws Exception {
        this.setRespondingDecisionShouldRespond(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.smsUtility).sendSMS(anyString(), anyString(), any(Predicate.class));

    }

    @Test
    public void testOfDisabledSummaryNotificationsOnNotRespondingCase() {
        this.setRespondingDecisionShouldRespond(false);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationFactory, times(0)).showSummaryNotification(anyString());
    }

    @Test
    public void testOfDisabledSummaryNotificationsOnRespondingCase() {
        this.setRespondingDecisionShouldRespond(true);

        when(this.settings.mock.isShowingSummaryNotificationEnabled()).thenReturn(false);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationFactory, times(0)).showSummaryNotification(anyString());
    }

    @Test
    public void testOfEnabledPendingNotificationOnRespondingCase() {
        when(this.settings.mock.isShowingSummaryNotificationEnabled()).thenReturn(true);
        this.setRespondingDecisionShouldRespond(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationFactory, times(1)).showPendingNotification();
        verify(this.notificationFactory, times(1)).hidePendingNotification();
    }


    @Test
    public void testOfEnabledSummaryNotificationOnRespondingCase() {
        when(this.settings.mock.isShowingSummaryNotificationEnabled()).thenReturn(true);
        this.setRespondingDecisionShouldRespond(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationFactory, times(1)).showSummaryNotification(anyString());
        verify(this.notificationFactory, times(1)).hidePendingNotification();
    }


    @Test
    public void testGPSNotAvailableNotification() throws GPSNotAvailableException {
        when(this.respondingDecision.shouldRespond(any(RespondingSubject.class))).thenThrow(GPSNotAvailableException.class);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationFactory, times(1)).showNotificationAboutTurnedOffGPS();

    }

    @Test
    public void testOfEnabledSummaryNotificationsOnNotRespondingCase() {
        when(this.settings.mock.isShowingSummaryNotificationEnabled()).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationFactory, times(0)).showSummaryNotification(anyString());
    }

    @Test
    public void testOfTermination() throws Exception {
        this.respondingTask.terminated = true;
        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.smsUtility, times(0)).sendSMS(anyString(), anyString(), any(Predicate.class));
    }

    @Test
    public void testOfWakelock() {
        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));
        verify(this.lockStateUtility, times(1)).acquirePartialWakeLock();
        verify(this.lockStateUtility, times(1)).releaseWakeLock(any(PowerManager.WakeLock.class));
    }

    @Test
    public void testOfWakelockOnTerminatedTask() {
        this.respondingTask.terminated = true;
        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));
        verify(this.lockStateUtility, times(1)).acquirePartialWakeLock();
        verify(this.lockStateUtility, times(1)).releaseWakeLock(any(PowerManager.WakeLock.class));
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

    @Test
    public void testPowerSaveMode() throws GPSNotAvailableException {
        when(this.lockStateUtility.isPowerSaveModeEnabled()).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        // in power save mode it shouldn't try to respond.
        verify(this.respondingDecision, times(0)).shouldRespond(any(RespondingSubject.class));
        verify(this.smsUtility, times(0)).sendSMS(anyString(), anyString(), any(Predicate.class));
        verify(this.notificationFactory, times(1)).showNotificationAboutPowerSaveMode();
    }

    private void setRespondingDecisionShouldRespond(boolean value) {
        try {
            when(this.respondingDecision.shouldRespond(any(RespondingSubject.class))).thenReturn(value);
        } catch (GPSNotAvailableException e) {
            e.printStackTrace();
        }
    }
}


class ExposedRespondingTask extends RespondingTask {

    public boolean terminated = false;

    public ExposedRespondingTask(RespondingDecision respondingDecision,
                                 Settings sharedPreferencesUtility,
                                 NotificationFactory notificationFactory,
                                 SMSUtility smsUtility,
                                 ContactsUtility contactsUtility,
                                 LockStateUtility lockStateUtility,
                                 ResponsePreparator responsePreparator,
                                 CustomLog log,
                                 Predicate<Boolean> resultCallback) {
        super(respondingDecision, sharedPreferencesUtility, notificationFactory, smsUtility, contactsUtility, lockStateUtility, responsePreparator, log, resultCallback);
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
