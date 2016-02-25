package com.medziku.motoresponder.logic;

import android.content.Context;

public class ResponderAnswered {

    public ResponderAnswered(Context context) {
    }


    public boolean responderAnsweredFromLastUserAction(String phoneNumber) {
        // TODO if there was already an auto response to this number, return true.
        return false;
    }

    public boolean tooMuchAutomaticalAnswersIn24h(String phoneNumber) {
        return false;
    }
}
