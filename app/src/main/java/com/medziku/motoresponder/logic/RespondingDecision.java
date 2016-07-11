package com.medziku.motoresponder.logic;

import java.util.Date;

/**
 * This class makes decision if we should respond to particular SMS or call.
 */
public class RespondingDecision {

    private DeviceUnlocked deviceUnlocked;
    private AlreadyResponded alreadyResponded;
    private NumberRules numberRules;
    private UserRide userRide;
    private CustomLog log;
    private Settings settings;


    public RespondingDecision(UserRide userRide,
                              NumberRules numberRules,
                              AlreadyResponded alreadyResponded,
                              DeviceUnlocked deviceUnlocked,
                              Settings settings,
                              CustomLog log) {
        this.userRide = userRide;
        this.numberRules = numberRules;
        this.settings = settings;
        this.alreadyResponded = alreadyResponded;
        this.deviceUnlocked = deviceUnlocked;
        this.settings = settings;
        this.log = log;
    }

    public boolean shouldRespond(RespondingSubject subject) {
        this.log.add("App will now make a decision if it should autorespond or not.");
        
        if (this.deviceUnlocked.isNotRidingBecausePhoneUnlocked()) {
            this.log.add("User is not riding because phone is unlocked.");
            return false;
        }
        
        Date dateOfReceiving = new Date();

        // do not answer numbers which user doesnt want to autorespond
        // this check is relatively cheap compared to measuring if user is riding
        if (!this.numberRules.numberRulesAllowResponding(subject.getPhoneNumber())) {
            this.log.add("App will not answer because number rules do not allow responding.");
            return false;
        }

        // this this will check if user or application already responded to this number. In case if he is already responded
        // we don't respond automatically. App can have some delay between receiving message and starting responding process
        // to allow user respond manually, so this check checks if it happened
        if (this.settings.isAlreadyRespondedFilteringEnabled() && this.alreadyResponded.isUserRespondedSince(dateOfReceiving, subject.getPhoneNumber())) {
            this.log.add("User responded since receiving input, no need of autoresponse.");
            return false;
        }

        // we shouldn't sent auto responses over and over so sent only if amount of responses sent to given number
        // since last normal response not exceed the limit
        if (this.exceededAmountOfAllowedAutomaticalResponsesSinceUserResponded(subject)) {
            this.log.add("Cant sent more responses to this number.");
            return false;
        }


        boolean isUserRiding = false;
        
        if (this.settings.isRidingAssumed()) {
                isUserRiding = true;
                 this.log.add("User manually set that he is riding now, no need to smart check.");
        }else{
          if (this.settings.isSensorCheckEnabled()) {
            if (this.userRide.isUserRiding()) {
                isUserRiding = true;
                this.log.add("Sensor check (smart detection) is enabled and find out that user is riding.");
            }else{
                this.log.add("Sensor check (smart detection) is enabled and find out that user is NOT riding.");
            }
        }  
        }
    
        if (!isUserRiding){
            this.log.add("Final decision is that User is NOT riding.");
            return false;
        }else{
            this.log.add("Final decision is that User is riding.");
        }

        // and now because isUserRiding can took several seconds, we check again if user not unlocked phone during this time.
        if (this.deviceUnlocked.isNotRidingBecausePhoneUnlocked()) {
            this.log.add("Phone unlocked during determining if user is riding.");
            return false;
        }

        // and now finally we check if user doesn't respond in time of checking if device is riding.
        // (there is possibility that user responded quickly, turning on screen doesn't affect logic, and user
        // quickly responded and hide phone before ride/not ride state was known)
        if (this.settings.isAlreadyRespondedFilteringEnabled() && this.alreadyResponded.isUserRespondedSince(dateOfReceiving, subject.getPhoneNumber())) {
            this.log.add("User responded between receiving input and determining if user is riding.");
            return false;
        }

        // Finally check if application doesn't respond in another thread when we were determining if user is riding
        // it is possible if applicattion will receive two smses quickly.
        if (this.exceededAmountOfAllowedAutomaticalResponsesSinceUserResponded(subject)) {
            this.log.add("Application already sent response between receiving input and determining if user is riding.");
            return false;
        }

        // all excluding conditions not met, we should respond.
        return true;
    }

    private boolean exceededAmountOfAllowedAutomaticalResponsesSinceUserResponded(RespondingSubject subject) {
        int limitOfAutoResponsesForRespondingSubject = this.getLimitForAutoresponses(subject);
        int automaticalSMSSent = this.alreadyResponded.getAmountOfAutomaticalResponsesSinceUserResponded(subject.getPhoneNumber());

        boolean limitExceeded = automaticalSMSSent >= limitOfAutoResponsesForRespondingSubject;

        return limitExceeded;
    }

    private int getLimitForAutoresponses(RespondingSubject subject) {
        return (subject instanceof GeolocationRequestRespondingSubject) ?
                this.settings.getLimitOfGeolocationResponses() :
                this.settings.getLimitOfResponses();
    }


    public void cancelDecision() {
        this.userRide.cancelUserRideCheck();
    }

}
