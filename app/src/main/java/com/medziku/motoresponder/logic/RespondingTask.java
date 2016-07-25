package com.medziku.motoresponder.logic;

import android.os.AsyncTask;
import android.os.PowerManager;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.LockStateUtility;
import com.medziku.motoresponder.utils.SMSUtility;


public class RespondingTask extends AsyncTask<RespondingSubject, Boolean, Boolean> {
    // TODO move to notification utility

    protected CustomLog log;
    private ContactsUtility contactsUtility;
    private SMSUtility smsUtility;
    private NotificationFactory notificationFactory;
    private Settings settings;
    private Predicate<Boolean> resultCallback;
    private RespondingDecision respondingDecision;
    private ResponsePreparator responsePreparator;
    private LockStateUtility lockStateUtility;
    private boolean isFinished;
    private PowerManager.WakeLock wakeLock;
    private CurrentAlreadyResponded alreadyResponded;

    public RespondingTask(RespondingDecision respondingDecision,
                          Settings settings,
                          NotificationFactory notificationFactory,
                          SMSUtility smsUtility,
                          ContactsUtility contactsUtility,
                          LockStateUtility lockStateUtility,
                          ResponsePreparator responsePreparator,
                          CustomLog log,
                          CurrentAlreadyResponded alreadyResponded,
                          Predicate<Boolean> resultCallback) {
        this.respondingDecision = respondingDecision;
        this.alreadyResponded = alreadyResponded;
        this.resultCallback = resultCallback;
        this.settings = settings;
        this.notificationFactory = notificationFactory;
        this.smsUtility = smsUtility;
        this.responsePreparator = responsePreparator;
        this.lockStateUtility = lockStateUtility;
        this.contactsUtility = contactsUtility;
        this.log = log;
        this.isFinished = false;
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

    protected Boolean doInBackground(RespondingSubject... params) {
        this.handleRespondingTask(params[0]);
        this.finishTask();
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
        if (this.settings.isShowingPendingNotificationEnabled()) {
            this.notificationFactory.hidePendingNotification();
        }

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

    protected void handleRespondingTask(RespondingSubject subject) {
        if (this.isFinished == true) {
            return;
        }

        if (this.lockStateUtility.isPowerSaveModeEnabled()) {
            this.notificationFactory.showNotificationAboutPowerSaveMode();
            return;
        }

        this.preventPhoneFromSleep();

        this.waitBeforeResponse();

        // K. Orzechowski: I am not sure, but I read that I should check for this.
        if (this.isTerminated()) {
            this.log.add("Not responded because phone unlocked in meantime.");
            return;
        }

        // show notification to give user possibiity to cancel autorespond
        if (this.settings.isShowingPendingNotificationEnabled()) {
            this.notificationFactory.showPendingNotification();
        }


        this.log.add("Started measuring if application should respond or not.");

        boolean shouldRespond = false;
        try {
            shouldRespond = this.respondingDecision.shouldRespond(subject);
        } catch (GPSNotAvailableException e) {
            this.notificationFactory.showNotificationAboutTurnedOffGPS();
        }

        if (shouldRespond) {
            // this check can took long time so before responding we can check again for cancellation.
            if (this.isTerminated()) {
                this.log.add("Not responded because phone unlocked after determining responding decision.");
                return;
            }

            this.log.add("Decision = RESPOND. Sending SMS.");
            this.respondWithSMS(subject);

            if (this.settings.isShowingSummaryNotificationEnabled()) {
                this.log.add("Showing summary notification because application responded and notification is enabled.");
                String phoneNumber = subject.getPhoneNumber();
                String contactDisplayName = this.contactsUtility.getContactDisplayName(phoneNumber);
                String recipient = contactDisplayName != null ? contactDisplayName : phoneNumber;

                this.notificationFactory.showSummaryNotification(recipient);
            }
        } else {
            this.log.add("Decision = NOT respond.");
        }
    }

    protected void sendSMSAndRetryOnFail(final String phoneNumber, final String message, final int attemptsLeft) {
        if (attemptsLeft <= 0) {
            return;
        }

        RespondingTask.this.log.add("Trying to send autoresponse SMS...");
        this.smsUtility.sendSMS(phoneNumber, message, new Predicate<String>() {
            public boolean apply(String error) {
                if (error != null) {
                    RespondingTask.this.log.add("Error during sending response SMS: '" + error + "'");
                    RespondingTask.this.sendSMSAndRetryOnFail(phoneNumber, message, attemptsLeft - 1);
                }
                RespondingTask.this.log.add("Autoresponse SMS correctly sent.");
                return true;
            }
        });
    }

    private void waitBeforeResponse() {
        // wait some time before responding, to allow user manually respond
        try {
            int waitBeforeResponseSeconds = this.settings.getWaitBeforeResponseSeconds();

            this.log.add("Started waiting " + waitBeforeResponseSeconds + " sec to let user respond himself.");

            Thread.sleep(waitBeforeResponseSeconds * 1000);

            this.log.add("Waiting ended.");
        } catch (InterruptedException e) {
            this.log.add("User unlocked phone.");
        }
    }

    private void preventPhoneFromSleep() {
        this.log.add("Acquiring wake lock, preventing phone from sleep during measurement.");
        this.wakeLock = this.lockStateUtility.acquirePartialWakeLock();
    }

    private void stopPreventingPhoneFromSleep() {
        if (this.wakeLock != null) {
            this.log.add("Releasing wake lock, now phone can sleep again.");
            this.lockStateUtility.releaseWakeLock(this.wakeLock);
        }
    }


    private void respondWithSMS(RespondingSubject subject) {
        String message = this.responsePreparator.prepareResponse(subject);
        try {
            int attemptsLeft = 3;
            this.sendSMSAndRetryOnFail(subject.getPhoneNumber(), message, attemptsLeft);
            this.alreadyResponded.get().rememberAboutAutoResponse(subject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}

