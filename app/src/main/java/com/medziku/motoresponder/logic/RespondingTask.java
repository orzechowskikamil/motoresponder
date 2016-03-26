package com.medziku.motoresponder.logic;

import android.os.AsyncTask;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.NotificationUtility;
import com.medziku.motoresponder.utils.SMSUtility;


public class RespondingTask extends AsyncTask<RespondingSubject, Boolean, Boolean> {

    private SMSUtility smsUtility;
    private NotificationUtility notificationUtility;
    private Settings settings;
    private Predicate<Boolean> resultCallback;
    private RespondingDecision respondingDecision;
    private RespondingSubject respondingSubject;
    private ResponsePreparator responsePreparator;


    public RespondingTask(RespondingDecision respondingDecision,
                          Settings settings,
                          NotificationUtility notificationUtility,
                          SMSUtility smsUtility,
                          ResponsePreparator responsePreparator,
                          Predicate<Boolean> resultCallback) {
        this.respondingDecision = respondingDecision;
        this.resultCallback = resultCallback;
        this.settings = settings;
        this.notificationUtility = notificationUtility;
        this.smsUtility = smsUtility;
        this.responsePreparator = responsePreparator;
    }

    /**
     * Cancells responding, cleanup (notifications, handlers, etc) and kills task
     */
    public void cancelResponding() {
        this.cancel(true);
        // remove notification if it was already shown.
        this.unnotifyAboutPendingAutoRespond();
    }

    protected void handleRespondingTask(RespondingSubject subject) {
        this.respondingSubject = subject;

        // wait some time before responding, to allow user manually
        try {
            Thread.sleep(this.settings.getDelayBeforeResponseMs());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // K. Orzechowski: I am not sure, but I read that I should check for this.
        if (this.isTerminated()) {
            return;
        }


        // show notification to give user possibiity to cancel autorespond
        if (this.settings.isShowingPendingNotificationEnabled()) {
            this.notifyAboutPendingAutoRespond();
        }


        if (this.respondingDecision.shouldRespond(this.respondingSubject.getPhoneNumber())) {
            // this check can took long time so before responding we can check again for cancellation.
            if (this.isTerminated()) {
                return;
            }
            this.respondWithSMS();
        }

        if (this.settings.isShowingPendingNotificationEnabled()) {
            this.unnotifyAboutPendingAutoRespond();
        }
    }

    private void respondWithSMS() {
        String message = this.responsePreparator.prepareResponse(this.respondingSubject);
        try {
            this.smsUtility.sendSMS(this.respondingSubject.getPhoneNumber(), message, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO K. Orzechowski: move strings into resources #69
        this.notificationUtility.showToast("Notification sended!");
    }


    private void notifyAboutPendingAutoRespond() {
        // TODO K. Orzechowski: move strings into resources #69
        this.notificationUtility.showOngoingNotification("MotoResponder", "Moto responder is determining if it should automatically respond", "");
    }

    private void unnotifyAboutPendingAutoRespond() {
        this.notificationUtility.hideOngoingNotification();
    }


    protected Boolean doInBackground(RespondingSubject... params) {
        try {
            // This must be wrapped into try.. catch... otherwise errors in 'doItBackground' method of async task will break application.
            this.handleRespondingTask(params[0]);
        } catch (Exception e) {
            // best place for catching errors from respondingTask
            e.printStackTrace();
        }
        return true;
    }

    /**
     * This is called when doInBackground() is finished
     */
    protected void onPostExecute(Boolean... result) {
        this.resultCallback.apply(result[0]);

    }


    protected boolean isTerminated() {
        return this.isCancelled();
    }

    /**
     * This is called each time you call publishProgress()
     */
    protected void onProgressUpdate(Boolean... progress) {
    }


}

