package com.medziku.motoresponder.logic;

import android.os.AsyncTask;
import android.util.Log;
import com.google.common.base.Predicate;


/**
 * Every task is responding to one call/sms, so every object of this class should be used only once.
 */
public class RespondingTask extends AsyncTask<String, Boolean, Boolean> {


    private Predicate<Boolean> resultCallback;

    private RespondingDecision respondingDecision;

    private int waitBeforeRespondingMs = 30000;


    public RespondingTask(RespondingDecision respondingDecision, Predicate<Boolean> resultCallback) {

        this.respondingDecision = respondingDecision;
        this.resultCallback = resultCallback;
    }

    private void respond(String phoneNumber) {


        // wait 30 seconds before responding.
        try {
            Thread.sleep(this.waitBeforeRespondingMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (this.respondingDecision.shouldRespond(phoneNumber)) {
            // TODO K. Orzechowski: move responding logic here.
        }
    }


    @Override
    protected Boolean doInBackground(String... params) {
        this.respond(params[0]);
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

