package com.medziku.motoresponder.logic;

import android.os.AsyncTask;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.NotificationUtility;
import com.medziku.motoresponder.utils.SMSUtility;


public class RespondingTask extends AsyncTask<RespondingSubject, Boolean, Boolean> {

    private DecisionLog log;
    private SMSUtility smsUtility;
    private NotificationUtility notificationUtility;
    private Settings settings;
    private Predicate<Boolean> resultCallback;
    private RespondingDecision respondingDecision;
    private RespondingSubject respondingSubject;
    private ResponsePreparator responsePreparator;

    // TODO K. Orzechowski: Change this to real configurable #67
    public boolean shouldShowNotification = true;
    public boolean shouldShowDebugNotification = true;

    public RespondingTask(RespondingDecision respondingDecision,
                          Settings settings,
                          NotificationUtility notificationUtility,
                          SMSUtility smsUtility,
                          ResponsePreparator responsePreparator,
                          DecisionLog log,
                          Predicate<Boolean> resultCallback) {
        this.respondingDecision = respondingDecision;
        this.resultCallback = resultCallback;
        this.settings = settings;
        this.notificationUtility = notificationUtility;
        this.smsUtility = smsUtility;
        this.responsePreparator = responsePreparator;
        this.log = log;
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
            this.log.add("Not responded because phone unlocked in meantime.");
            return;
        }


        // show notification to give user possibiity to cancel autorespond
       this.showPendingNotificationIfEnabled();


        boolean shouldRespond = this.respondingDecision.shouldRespond(this.respondingSubject.getPhoneNumber());
        if (shouldRespond) {
            // this check can took long time so before responding we can check again for cancellation.
            if (this.isTerminated()) {
                this.log.add("Not responded because phone unlocked after determining responding decision.");
                return;
            }

            this.log.add("Decision = RESPOND. Sending SMS.");
            this.respondWithSMS();
        } else {
            this.log.add("Decision = NOT respond.");
        }

 
        if (this.shouldShowNotification && shouldRespond) {
            this.showSummaryNotification(this.respondingSubject.getPhoneNumber());
        }

        if (this.shouldShowDebugNotification) {
            this.showDebugNotification();
        }
    }
    
    private void showPendingNotificationIfEnabled(){
     if (this.settings.isShowingPendingNotificationEnabled()) {
            this.notifyAboutPendingAutoRespond();
        }
    }
    
    private void hidePendingNotificationIfEnabled(){
           if (this.settings.isShowingPendingNotificationEnabled()) {
            this.unnotifyAboutPendingAutoRespond();
        }

    }

    private void showDebugNotification() {
        String debugText = this.log.getLogStr();
        this.notificationUtility.showBigTextNotification("MotoResponder debug", "Close other notifications to see full content", debugText);
    }

    private void showSummaryNotification(String phoneNumber) {
        // todo #Issue #69 move strings into resources
        String summary = "Answered " + phoneNumber;

        String bigText = "You received call/message from '" + phoneNumber + "' and because you ride this number received auto response.";

        String title = "MotoResponder";
        this.notificationUtility.showBigTextNotification(title, summary, bigText);
    }

    private void respondWithSMS() {
        String message = this.responsePreparator.prepareResponse(this.respondingSubject);
        try {
            this.smsUtility.sendSMS(this.respondingSubject.getPhoneNumber(), message, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void notifyAboutPendingAutoRespond() {
        // TODO K. Orzechowski: move strings into resources #69
        this.notificationUtility.showOngoingNotification("MotoResponder", "Moto responder is determining if it should automatically respond", "");
    }


    private void unnotifyAboutPendingAutoRespond() {
        this.notificationUtility.hideNotification();
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
        this.hidePendingNotificationIfEnabled();
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

