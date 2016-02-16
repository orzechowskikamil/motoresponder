package com.medziku.motoresponder.logic;

import java.util.Date;

/**
 * This class makes decision if we should respond to particular SMS or call.
 */
public class RespondingDecision {

    private final DeviceUnlocked deviceUnlocked;
    private AlreadyResponded alreadyResponded;
    private NumberRules numberRules;
    private UserRide userRide;


    public RespondingDecision(UserRide userRide, NumberRules numberRules, AlreadyResponded alreadyResponded,DeviceUnlocked deviceUnlocked) {
        this.userRide = userRide;
        this.numberRules = numberRules;
        this.alreadyResponded = alreadyResponded;
        this.deviceUnlocked = deviceUnlocked;
    }

    public boolean shouldRespond(String phoneNumber) {
        if (this.deviceUnlocked.shouldNotRespondBecauseDeviceUnlocked()) {
            return false;
        }

        Date dateOfReceiving = new Date();

        // do not answer numbers which user doesnt want to autorespond
        // this check is relatively cheap compared to measuring if user is riding
        // TODO K. Orzechowski: rename to smth like numberRulesAllowResponding? Issue #51
        if (!this.numberRules.shouldRespondToThisNumber(phoneNumber)) {
            return false;
        }
        
        // this this will check if user or application already responded to this number. In case if he is already responded
        // we don't respond automatically. App can have some delay between receiving message and starting responding process
        // to allow user respond manually, so this check checks if it happened
        if (this.alreadyResponded.isUserRespondedSince(dateOfReceiving, phoneNumber)) {
            return false;
        }
        
        // we shouldn't sent auto responses over and over, so sent only if last response was call / sms to given number,
        // not the auto response.
        if (this.alreadyResponded.isUserNotAnsweredSinceLastAutomaticalResponse(phoneNumber)){
            return false;
        }


        // TODO k.orzechowski: idea: check if you are not in public transportation by checking
        // for available wifi, or many bluetooth devices around you. Issue #52

        // TODO K. Orzechowski: sleep here for long time. Issue #51
        // TODO K. Orzechowski: allow user to respond himself and then check. Issue #51


        // this check is more expensive in terms of power and battery
        // so it's performed later.
        if (!this.userRide.isUserRiding()) {
            return false;
        }
        
        // and now because isUserRiding can took several seconds, we check again if user doesn't use this time for responding.
        // if he returned, we prevent auto responding.
        if (this.alreadyResponded.isUserRespondedSince(dateOfReceiving, phoneNumber)) {
            return false;
        }

        // all excluding conditions not met, we should respond.
        return true;
    }


}
