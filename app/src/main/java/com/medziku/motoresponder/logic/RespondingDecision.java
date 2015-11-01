package com.medziku.motoresponder.logic;

import android.os.AsyncTask;
import android.util.Log;
import com.google.common.base.Predicate;

/**
 * This class makes decision if we should respond to particular SMS or ll.
 * You can use every object of this class only once (every object is one decision)
 */
public class RespondingDecision extends AsyncTask<String, Boolean, Boolean> {


    private final Predicate<Boolean> resultCallback;
    private NumberRules numberRules;
    private UserRide userRide;

    public RespondingDecision(UserRide userRide, NumberRules numberRules, Predicate<Boolean> resultCallback) {
        this.userRide = userRide;
        this.numberRules = numberRules;
        this.resultCallback = resultCallback;

    }

    private boolean shouldRespond(String phoneNumber) {
        // do not answer numbers which user doesnt want to autorespond
        // this check is relatively cheap compared to measuring if user is riding
        if (!this.numberRules.shouldRespondToThisNumber(phoneNumber)) {
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

