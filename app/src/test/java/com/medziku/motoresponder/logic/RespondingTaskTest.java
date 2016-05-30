package com.medziku.motoresponder.logic;

import android.os.PowerManager;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.NotificationUtility;
import com.medziku.motoresponder.utils.SMSUtility;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
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
    private ContactsUtility contactsUtility;
    private LockStateUtility lockStateUtility;


    @Before
    public void setUp() {

        this.respondingDecision = Mockito.mock(RespondingDecision.class);
        this.settings = Mockito.mock(Settings.class);
        this.notificationUtility = Mockito.mock(NotificationUtility.class);
        this.smsUtility = Mockito.mock(SMSUtility.class);
        this.responsePreparator = Mockito.mock(ResponsePreparator.class);
        this.returnCallback = Mockito.mock(Predicate.class);
        this.contactsUtility = Mockito.mock(ContactsUtility.class);
        this.lockStateUtility = Mockito.mock(LockStateUtility.class);
        DecisionLog log = Mockito.mock(DecisionLog.class);


        this.respondingTask = new ExposedRespondingTask(
                this.respondingDecision,
                this.settings,
                this.notificationUtility,
                this.smsUtility,
                this.contactsUtility,
                this.lockStateUtility,
                this.responsePreparator,
                log,
                this.returnCallback);

        when(this.settings.getDebugNotificationTitleText()).thenReturn("debug");
        when(this.settings.getDebugNotificationShortText()).thenReturn("debug");

        when(this.settings.getSummaryNotificationTitleText()).thenReturn("summary");
        when(this.settings.getSummaryNotificationShortText()).thenReturn("summary %recipient%.");
        when(this.settings.getSummaryNotificationBigText()).thenReturn("summary %recipient%.");
        when(this.settings.getOngoingNotificationTitleText()).thenReturn("ongoing");
        when(this.settings.getOngoingNotificationBigText()).thenReturn("ongoing");

        when(this.settings.isShowingPendingNotificationEnabled()).thenReturn(true);
        when(this.respondingDecision.shouldRespond(any(RespondingSubject.class))).thenReturn(false);

        when(this.settings.isShowingDebugNotificationEnabled()).thenReturn(false);

    }


    @Test
    public void testOfResponse() throws Exception {
        when(this.respondingDecision.shouldRespond(any(RespondingSubject.class))).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.smsUtility).sendSMS(anyString(), anyString(), any(Predicate.class));

    }

    @Test
    public void testOfDisabledSummaryNotificationsOnNotRespondingCase() {
        when(this.settings.isShowingSummaryNotificationEnabled()).thenReturn(false);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationUtility, times(0)).showBigTextNotification(anyString(), anyString(), anyString());
    }

    @Test
    public void testOfDisabledSummaryNotificationsOnRespondingCase() {
        when(this.respondingDecision.shouldRespond(any(RespondingSubject.class))).thenReturn(true);

        when(this.settings.isShowingSummaryNotificationEnabled()).thenReturn(false);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationUtility, times(0)).showBigTextNotification(anyString(), anyString(), anyString());
    }

    @Test
    public void testOfEnabledSummaryNotificationOnRespondingCase() {
        when(this.settings.isShowingSummaryNotificationEnabled()).thenReturn(true);
        when(this.respondingDecision.shouldRespond(any(RespondingSubject.class))).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        ArgumentCaptor<String> summaryNotificationSmallText = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> summaryNotificationBigText = ArgumentCaptor.forClass(String.class);

        verify(this.notificationUtility, times(1)).showOngoingNotification(anyString(), anyString(), anyString());
        verify(this.notificationUtility, times(1)).showBigTextNotification(
                anyString(),
                summaryNotificationSmallText.capture(),
                summaryNotificationBigText.capture()
        );
        verify(this.notificationUtility, times(1)).hideNotification();

        assertTrue(summaryNotificationSmallText.getValue().indexOf(FAKE_PHONE_NUMBER) >= 0);
        assertTrue(summaryNotificationBigText.getValue().indexOf(FAKE_PHONE_NUMBER) >= 0);
    }


    @Test
    public void testOfDisplayingContactName() {
        String TEST_CONTACT_NAME = "test contact";

        when(this.settings.isShowingSummaryNotificationEnabled()).thenReturn(true);
        when(this.contactsUtility.getContactDisplayName(anyString())).thenReturn(TEST_CONTACT_NAME);
        when(this.respondingDecision.shouldRespond(any(RespondingSubject.class))).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        ArgumentCaptor<String> summaryNotificationSmallText = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> summaryNotificationBigText = ArgumentCaptor.forClass(String.class);

        verify(this.notificationUtility, times(1)).showBigTextNotification(
                anyString(),
                summaryNotificationSmallText.capture(),
                summaryNotificationBigText.capture()
        );

        String smallText = summaryNotificationSmallText.getValue();
        String bigText = summaryNotificationBigText.getValue();

        assertTrue(smallText.indexOf(TEST_CONTACT_NAME) >= 0);
        assertFalse(smallText.indexOf(FAKE_PHONE_NUMBER) >= 0);

        assertTrue(bigText.indexOf(TEST_CONTACT_NAME) >= 0);
        assertFalse(bigText.indexOf(FAKE_PHONE_NUMBER) >= 0);

    }


    @Test
    public void testOfEnabledSummaryNotificationsOnNotRespondingCase() {
        when(this.settings.isShowingSummaryNotificationEnabled()).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationUtility, times(0)).showBigTextNotification(anyString(), anyString(), anyString());
    }


    @Test
    public void testOfEnabledDebugNotificationOnNotRespondingCase() {
        when(this.settings.isShowingDebugNotificationEnabled()).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationUtility, times(1)).showBigTextNotification(anyString(), anyString(), anyString());
    }

    @Test
    public void testOfEnabledDebugNotificationRespondingCase() {
        when(this.respondingDecision.shouldRespond(any(RespondingSubject.class))).thenReturn(true);
        when(this.settings.isShowingDebugNotificationEnabled()).thenReturn(true);

        this.respondingTask.callLogic(new CallRespondingSubject(this.FAKE_PHONE_NUMBER));

        verify(this.notificationUtility, times(1)).showBigTextNotification(anyString(), anyString(), anyString());
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
        verify(this.lockStateUtility, atLeast(1)).releaseWakeLock(any(PowerManager.WakeLock.class));
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
                                 ContactsUtility contactsUtility,
                                 LockStateUtility lockStateUtility,
                                 ResponsePreparator responsePreparator,
                                 DecisionLog log,
                                 Predicate<Boolean> resultCallback) {
        super(respondingDecision, sharedPreferencesUtility, notificationUtility, smsUtility, contactsUtility, lockStateUtility, responsePreparator, log, resultCallback);
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
