package com.medziku.motoresponder.logic;

/**
 * This class represent decision if we should respond to particular SMS or ll.
 */
public class RespondingDecider {


    private NumberRules numberRules;
    private UserRide userRide;

    public RespondingDecider(UserRide userRide, NumberRules numberRules) {
        this.userRide = userRide;
        this.numberRules = numberRules;

    }

    public boolean shouldRespond(String phoneNumber) {
        // do not answer numbers which user doesnt want to autorespond
        if (!this.numberRules.shouldRespondToThisNumber(phoneNumber)) {
            return false;
        }


        if (!this.userRide.isUserRiding()) {
            return false;
        }

        return true;
    }


}
