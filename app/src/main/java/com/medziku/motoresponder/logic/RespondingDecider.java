package com.medziku.motoresponder.logic;

import android.os.AsyncTask;
import android.util.Log;
import com.google.common.base.Predicate;

import java.net.URL;

/**
 * This class makes decision if we should respond to particular SMS or ll.
 * You can use every object of this class only once (every object is one decision)
 */
// TODO K. Orzechowski: rename to RespondingDecision since you can use it only once
public class RespondingDecider extends AsyncTask<String, Boolean, Boolean> {


    private final Predicate<Boolean> predicate;
    private NumberRules numberRules;
    private UserRide userRide;

    public RespondingDecider(UserRide userRide, NumberRules numberRules, Predicate<Boolean> predicate) {
        this.userRide = userRide;
        this.numberRules = numberRules;
        this.predicate = predicate;

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
        this.predicate.apply(result[0]);

    }

    // This is called each time you call publishProgress()
    protected void onProgressUpdate(Boolean... progress) {
        Log.d("motoapp", "RespondingDecider progress update called");
    }
}

