package com.medziku.motoresponder.logic;

import com.google.common.base.Predicate;

import java.util.Date;

/**
 * This class makes decision if we should respond to particular SMS or call.
 */
public class RespondingDecision {


    private ResponderAnswered responderAnswered;
    private UserResponded userResponded;
    private NumberRules numberRules;
    private UserRide userRide;


    public RespondingDecision(UserRide userRide, NumberRules numberRules, UserResponded userResponded, ResponderAnswered responderAnswered) {
        this.userRide = userRide;
        this.numberRules = numberRules;
        this.userResponded = userResponded;
        this.responderAnswered = responderAnswered;
    }

    public boolean shouldRespond(String phoneNumber) {

        if (this.responderAnswered.shouldNotRespondBecauseDeviceUnlocked()) {
            return false;
        }

        Date dateOfReceiving = new Date();

        // send auto respose only on first message on phone number, do not spam with responses. User action will unlock responding.
        if (this.responderAnswered.responderAnsweredFromLastUserAction(phoneNumber) == true) {
            return false;
        }

        // limit daily responses
        if (this.responderAnswered.tooMuchAutomaticalAnswersIn24h(phoneNumber) == true) {
            return false;
        }


        // do not answer numbers which user doesnt want to autorespond
        // this check is relatively cheap compared to measuring if user is riding
        // TODO K. Orzechowski: rename to smth like numberRulesAllowResponding? Issue #51
        if (!this.numberRules.shouldRespondToThisNumber(phoneNumber)) {
            return false;
        }

        // TODO k.orzechowski: idea: check if you are not in public transportation by checking
        // for available wifi, or many bluetooth devices around you. Issue #52

        // TODO K. Orzechowski: sleep here for long time. Issue #51
        // TODO K. Orzechowski: allow user to respond himself and then check. Issue #51


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


}
