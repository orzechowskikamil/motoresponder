package com.medziku.motoresponder.logic;

import android.os.AsyncTask;
import android.util.Log;
import com.google.common.base.Predicate;

import java.util.Date;

/**
 * This class makes decision if we should respond to particular SMS or call.
 * You can use every object of this class only once (every object is one decision)
 */
public class RespondingDecision extends AsyncTask<String, Boolean, Boolean> {


    private Predicate<Boolean> resultCallback;
    private UserResponded userResponded;
    private NumberRules numberRules;
    private UserRide userRide;
    private int waitBeforeRespondingMs = 30000;
    

    public RespondingDecision(UserRide userRide, NumberRules numberRules, UserResponded userResponded, Predicate<Boolean> resultCallback) {
        this.userRide = userRide;
        this.numberRules = numberRules;
        this.userResponded = userResponded;
        this.resultCallback = resultCallback;
    }

    private boolean shouldRespond(String phoneNumber) {
        Date dateOfReceiving = new Date();
        
        // send auto respose only on first message on phone number, do not spam with responses. User action will unlock responding.
        if (this.responderAnswered.responderAnsweredFromLastUserAction(phoneNumber)==true){
            return;
        }
        
        // limit daily responses
        if (this.responderAnswered.tooMuchAutomaticalAnswersIn24h(phoneNumber)==true){
            return;
        }

        // wait 30 seconds before responding.
        try {
            Thread.sleep(this.waitBeforeRespondingMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // do not answer numbers which user doesnt want to autorespond
        // this check is relatively cheap compared to measuring if user is riding
        // TODO K. Orzechowski: rename to smth like numberRulesAllowResponding?
        if (!this.numberRules.shouldRespondToThisNumber(phoneNumber)) {
            return false;
        }

        // TODO k.orzechowski: idea: check if you are not in public transportation by checking
        // for available wifi, or many bluetooth devices around you.

        // TODO K. Orzechowski: sleep here for long time.
        // TODO K. Orzechowski: allow user to respond himself and then check.


        if (this.userResponded.isUserRespondedSince(dateOfReceiving, phoneNumber)) {
            return false;
        }


        // this check is more expensive in terms of power and battery
        // so it's performed later.
        if (!this.userRide.isUserRiding()) {
            return false;
        }

        // all excluding conditions not met, we should respond.
        return true;
    }


    @Override
    protected Boolean doInBackground(String... params) {
        return this.shouldRespond(params[0]);
    }

    // This is called when doInBackground() is finished
    protected void onPostExecute(Boolean... result) {
        this.resultCallback.apply(result[0]);

    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Boolean... progress) {
        Log.d("motoapp", "RespondingDecision progress update called");
    }
}

