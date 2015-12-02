package com.medziku.motoresponder.logic;

public class ResponderAnswered {


    public boolean responderAnsweredFromLastUserAction(String phoneNumber) {
        // TODO if there was already an auto response to this number, return true.
        return false;
    }

    public boolean tooMuchAutomaticalAnswersIn24h(String phoneNumber) {
        return false;
    }
}
