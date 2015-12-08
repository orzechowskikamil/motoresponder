package com.medziku.motoresponder.logic;

import android.os.AsyncTask;
import android.util.Log;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.NotificationUtility;
import com.medziku.motoresponder.utils.SMSUtility;
import com.medziku.motoresponder.utils.SettingsUtility;


/**
 * Every task is responding to one call/sms, so every object of this class should be used only once.
 */
public class RespondingTask extends AsyncTask<String, Boolean, Boolean> {


    private SMSUtility smsUtility;
    private NotificationUtility notificationUtility;
    private SettingsUtility settingsUtility;
    private Predicate<Boolean> resultCallback;

    private RespondingDecision respondingDecision;

    private int waitBeforeRespondingMs = 30000;


    public RespondingTask(RespondingDecision respondingDecision, SettingsUtility settingsUtility, NotificationUtility notificationUtility, SMSUtility smsUtility, Predicate<Boolean> resultCallback) {

        this.respondingDecision = respondingDecision;
        this.resultCallback = resultCallback;
        this.settingsUtility = settingsUtility;
        this.notificationUtility = notificationUtility;
        this.smsUtility = smsUtility;
    }

    private void handleRespondingTask(String phoneNumber) {


        // wait 30 seconds before responding.
        try {
            Thread.sleep(this.waitBeforeRespondingMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        // show notification to give user possibiity to cancel autorespond
        if (this.settingsUtility.isShowingPendingNotificationEnabled()) {
            this.notifyAboutPendingAutoRespond();
        }


        if (this.respondingDecision.shouldRespond(phoneNumber)) {
            this.respondWithSMS(phoneNumber);


        }

        if (this.settingsUtility.isShowingPendingNotificationEnabled()) {
            this.unnotifyAboutPendingAutoRespond();
        }
    }

    private void respondWithSMS(String phoneNumber) {
        // TODO K. Orzechowski: this probably should be in separate logic class.
        String message = this.settingsUtility.getAutoResponseTextForSMS();

        try {
            this.smsUtility.sendSMS(phoneNumber, message, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.notificationUtility.showToast("Notification sended!");
    }

    private void notifyAboutPendingAutoRespond() {
        this.notificationUtility.showNotification("MotoResponder", "Moto responder is determining if should automatically respond", "");
    }

    private void unnotifyAboutPendingAutoRespond() {
        this.notificationUtility.hideNotification();
    }


    @Override
    protected Boolean doInBackground(String... params) {
        this.handleRespondingTask(params[0]);
        // TODO K. Orzechowski: refactor, we need void here, not boolean.
        return true;
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(Boolean... result) {
        this.resultCallback.apply(result[0]);

    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Boolean... progress) {
        Log.d("motoapp", "RespondingTask progress update called");
    }
}

