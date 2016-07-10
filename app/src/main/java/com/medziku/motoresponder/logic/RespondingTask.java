package com.medziku.motoresponder.logic;

import android.os.AsyncTask;
import android.os.PowerManager;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.NotificationUtility;
import com.medziku.motoresponder.utils.SMSUtility;


public class RespondingTask extends AsyncTask<RespondingSubject, Boolean, Boolean> {

    public static final String RECIPIENT_SUBSTITUTION_TAG = "%recipient%";
    protected CustomLog log;
    private ContactsUtility contactsUtility;
    private SMSUtility smsUtility;
    private NotificationUtility notificationUtility;
    private Settings settings;
    private Predicate<Boolean> resultCallback;
    private RespondingDecision respondingDecision;
    private ResponsePreparator responsePreparator;
    private LockStateUtility lockStateUtility;
    private boolean isFinished;
    private PowerManager.WakeLock wakeLock;

    public RespondingTask(RespondingDecision respondingDecision,
                          Settings settings,
                          NotificationUtility notificationUtility,
                          SMSUtility smsUtility,
                          ContactsUtility contactsUtility,
                          LockStateUtility lockStateUtility,
                          ResponsePreparator responsePreparator,
                          CustomLog log,
                          Predicate<Boolean> resultCallback) {
        this.respondingDecision = respondingDecision;
        this.resultCallback = resultCallback;
        this.settings = settings;
        this.notificationUtility = notificationUtility;
        this.smsUtility = smsUtility;
        this.responsePreparator = responsePreparator;
        this.lockStateUtility = lockStateUtility;
        this.contactsUtility = contactsUtility;
        this.log = log;
        this.isFinished = false;
    }


    protected Boolean doInBackground(RespondingSubject... params) {
        this.handleRespondingTask(params[0]);
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        this.finishTask();
    }

    protected void finishTask() {
        if (this.isFinished == true) {
            return;
        }
        this.hidePendingNotificationIfEnabled();

        this.showDebugNotificationIfEnabled();

        this.isFinished = true;
        this.stopPreventingPhoneFromSleep();
        this.resultCallback.apply(true);
    }

    /**
     * This is called when doInBackground() is finished
     */


    protected boolean isTerminated() {
        return this.isCancelled();
    }

    /**
     * This is called each time you call publishProgress()
     */
    protected void onProgressUpdate(Boolean... progress) {
    }

    /**
     * Cancells responding, cleanup (notifications, handlers, etc) and kills task
     */
    public void cancelResponding() {
        if (this.isFinished == true) {
            return;
        }
        this.respondingDecision.cancelDecision();

        this.log.add("Responding cancelled.");

        this.cancel(true);

        this.finishTask();
    }


    protected void handleRespondingTask(RespondingSubject subject) {
        if (this.isFinished == true) {
            return;
        }

        this.preventPhoneFromSleep();

        // wait some time before responding, to allow user manually respond
        try {
            Thread.sleep(this.settings.getWaitBeforeResponseSeconds() * 1000);
        } catch (InterruptedException e) {
            // normal situation - unlocked phone.
        }

        // K. Orzechowski: I am not sure, but I read that I should check for this.
        if (this.isTerminated()) {
            this.log.add("Not responded because phone unlocked in meantime.");
            this.finishTask();
            return;
        }


        // show notification to give user possibiity to cancel autorespond
        this.showPendingNotificationIfEnabled();


        boolean shouldRespond = this.respondingDecision.shouldRespond(subject);
        if (shouldRespond) {
            // this check can took long time so before responding we can check again for cancellation.
            if (this.isTerminated()) {
                this.log.add("Not responded because phone unlocked after determining responding decision.");
                this.finishTask();
                return;
            }

            this.log.add("Decision = RESPOND. Sending SMS.");
            this.respondWithSMS(subject);
        } else {
            this.log.add("Decision = NOT respond.");
        }


        if (this.settings.isShowingSummaryNotificationEnabled() && shouldRespond) {
            this.showSummaryNotification(subject.getPhoneNumber());
        }
    }

    private void preventPhoneFromSleep() {
        this.wakeLock = this.lockStateUtility.acquirePartialWakeLock();
    }

    private void stopPreventingPhoneFromSleep() {
        if (this.wakeLock != null) {
            this.lockStateUtility.releaseWakeLock(this.wakeLock);
        }
    }

    private void showPendingNotificationIfEnabled() {
        if (this.settings.isShowingPendingNotificationEnabled()) {
            this.notifyAboutPendingAutoRespond();
        }
    }

    private void hidePendingNotificationIfEnabled() {
        if (this.settings.isShowingPendingNotificationEnabled()) {
            this.notificationUtility.hideNotification();
        }

    }

    private void showDebugNotification() {
        String debugTitle = this.settings.getDebugNotificationTitleText();
        String debugBigText = this.log.getLogStr();
        String debugShortText = this.settings.getDebugNotificationShortText();

        this.notificationUtility.showBigTextNotification(debugTitle, debugShortText, debugBigText);
    }

    private void showSummaryNotification(String phoneNumber) {
        String contactDisplayName = this.contactsUtility.getContactDisplayName(phoneNumber);

        String recipient = (contactDisplayName != null) ? contactDisplayName : phoneNumber;

        String title = this.settings.getSummaryNotificationTitleText();
        String shortText = this.settings.getSummaryNotificationShortText().replace(RECIPIENT_SUBSTITUTION_TAG, recipient);
        String bigText = this.settings.getSummaryNotificationBigText().replace(RECIPIENT_SUBSTITUTION_TAG, recipient);

        this.notificationUtility.showBigTextNotification(title, shortText, bigText);
    }

    private void respondWithSMS(RespondingSubject subject) {
        String message = this.responsePreparator.prepareResponse(subject);
        try {
            int attemptsLeft = 3;
            this.sendSMSAndRetryOnFail(subject.getPhoneNumber(), message, attemptsLeft);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void sendSMSAndRetryOnFail(final String phoneNumber, final String message, final int attemptsLeft) {
        if (attemptsLeft <= 0) {
            return;
        }

        this.smsUtility.sendSMS(phoneNumber, message, new Predicate<String>() {
            public boolean apply(String error) {
                if (error != null) {
                    RespondingTask.this.log.add("Error during sending response SMS: '" + error + "'");
                    RespondingTask.this.sendSMSAndRetryOnFail(phoneNumber, message, attemptsLeft - 1);
                }
                return true;
            }
        });
    }


    private void notifyAboutPendingAutoRespond() {
        String title = this.settings.getOngoingNotificationTitleText();
        String bigText = this.settings.getOngoingNotificationBigText();
        this.notificationUtility.showOngoingNotification(title, bigText, "");
    }


    private void showDebugNotificationIfEnabled() {
        if (this.settings.isShowingDebugNotificationEnabled()) {
            this.showDebugNotification();
        }
    }

}

