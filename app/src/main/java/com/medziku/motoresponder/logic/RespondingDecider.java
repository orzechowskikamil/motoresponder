package com.medziku.motoresponder.logic;

/**
 * This class makes decision if we should respond to particular SMS or ll.
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


}
