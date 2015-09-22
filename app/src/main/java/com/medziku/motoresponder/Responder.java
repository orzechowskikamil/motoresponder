package com.medziku.motoresponder;

import android.location.Location;

/**
 * Created by Kamil on 2015-09-08.
 */
public class Responder {

    // TODO refactor it to create RespondingDecision class where this class will become abstract decision about responding or not 
    // while extracting to other classes process of gathering location or sending sms logic

    // todo create action log where every decision is stored and user can debug settings

    private LocationUtility locationUtility;
    private SensorsUtility sensorsUtility;

    public boolean notifyAboutAutoRespond = true;
    public boolean showPendingNotification = true;
    public boolean includeProximityCheck = true;
    public boolean includeLightCheck = true;
    public boolean interpretLocationTimeoutAsNotRiding = true;
    public boolean assumePhoneUnlockedAsNotRiding = true;
    public boolean interpretStayingStillAccelerometerAsNotRiding = true;
    public boolean doAnotherGPSCheckIfNotSure = true;


    public int maybeRidingSpeed = 15;
    public int sureRidingSpeed = 60;
    public long waitForAnotherGPSCheckTimeout = 20000;
    public long waitAfterReceivingMsgOrCall = 1000;
    public long waitBeforeResponding = 10000;
    public int respondingCountrySettings = 0;
    public int respondingSettings = 2;

    public static final int RESPONDING_COUNTRY_SETTINGS_CURRENT_COUNTRY_ONLY = 0;
    public static final int RESPONDING_COUNTRY_SETTINGS_ANY_COUNTRY = 1;

    public static final int RESPONDING_SETTINGS_RESPOND_EVERYONE = 0;
    public static final int RESPONDING_SETTINGS_RESPOND_EVERY_NORMAL_NUMBER = 1;
    public static final int RESPONDING_SETTINGS_RESPOND_ONLY_CONTACT_BOOK = 2;
    public static final int RESPONDING_SETTINGS_RESPOND_ONLY_GROUP = 3;
    private LockStateUtility lockStateUtility;


    public Responder(LocationUtility locationUtility, SensorsUtility proximityUtility, LockStateUtility lockStateUtility) {
        // probably we have to start every onsmsreceived in new thread
        this.locationUtility = locationUtility;
        this.sensorsUtility = proximityUtility;
        this.lockStateUtility = lockStateUtility;

        this.sensorsUtility.registerSensorUpdates();
    }

    public void onSMSReceived(String phoneNumber) {
        // call this when new SMS is detected
        this.handleIncoming(phoneNumber);
    }

    public void onUnAnsweredCallReceived(String phoneNumber) {
        // call this when new call is detected
        this.handleIncoming(phoneNumber);
    }

    public void onPhoneUnlocked() {
        // call this when phone is unlocked by user
        this.cancelAllHandling();
    }

    private void handleIncoming(final String phoneNumber) {
        // for now for simplification just wait one second forclaryfying sensor values
        try {
            Thread.sleep(this.waitAfterReceivingMsgOrCall);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // if phone is unlocked we do not need to autorespond at all.
        if (this.assumePhoneUnlockedAsNotRiding && this.phoneIsUnlocked()) {
            return;
        }

        if (this.includeProximityCheck && this.isProxime() == false) {
            // proxime test failed, so phone can't be in pocket. if not in pocket he probably does not ride
            return;
        }

        if (this.includeLightCheck && this.isLightOutside()) {
            // light outside. in pocket shouldn't be any light.
            return;
        }

        // do not answer numbers which user doesnt want to autorespond
        if (!this.shouldRespondToThisNumber(phoneNumber)) {
            return;
        }

        // show notification to give user possibiity to cancel autorespond
        if (this.showPendingNotification) {
            this.notifyAboutPendingAutoRespond();
        }

        // wait some time before responding - give user time to get phone from the pocket
        // or from the desk and respond manually.
        // unlocking phone should break any responding at all
        try {
            Thread.sleep(this.waitBeforeResponding);
        } catch (InterruptedException e) {
        }

        // now things will go automatically in one milisecond so it's not required to still show this
        if (this.showPendingNotification) {
            this.unnotifyAboutPendingAutoRespond();
        }

        // if phone is unlocked now, we can return - user heard ring, get phone and will
        // respond manually.
        if (this.assumePhoneUnlockedAsNotRiding && this.phoneIsUnlocked()) {
            return;
        }

        // if phone doesn't report any movement we can also assume that user is not riding motorcycle
        if (this.interpretStayingStillAccelerometerAsNotRiding && this.phoneReportsStayingStill()) {
            return;
        }

        this.getLocationAndProceed(phoneNumber);
    }


    private void handleIncomingSecondStep(String phoneNumber, Location location) {

        boolean locationTimeouted = false;
        float firstMeasurementSpeed = location.getSpeed();
        float secondMeasurementSpeed = 0;
        float biggerMeasurementSpeed = 0;

        if (this.interpretLocationTimeoutAsNotRiding && locationTimeouted) {
            // if timeout, it means that phone is probably in home with no access to GPS satelites.
            // so if no ride, no need to respond automatically
            return;
        }


//        if (this.doAnotherGPSCheckIfNotSure
//                && (firstMeasurementSpeed >= this.maybeRidingSpeed)
//                && (firstMeasurementSpeed < this.sureRidingSpeed)) {
//            // speed is small. too small. not sure if he rides or not.
//            // try to recheck in few minutes.
//            this.wait(this.waitForAnotherGPSCheckTimeout);
//            location = this.getLocationAndProceed();
//            // reinit speed and location
//            secondMeasurementSpeed = 0;
//        }

        biggerMeasurementSpeed = (secondMeasurementSpeed > firstMeasurementSpeed)
                ? secondMeasurementSpeed
                : firstMeasurementSpeed;


        if (biggerMeasurementSpeed < this.sureRidingSpeed) {
            // user is not riding. no need to autorespond
            return;
        }

        String message = this.generateAutoRespondMessage(phoneNumber);
        this.sendSMS(phoneNumber, message);
        this.notifyAboutAutoRespond(phoneNumber);
    }


    private void cancelAllHandling() {
        // call this to break all autoresponding
    }


    private void wait(int amountMs) {
        // wait for given milliseconds
    }


    private boolean phoneIsUnlocked() {
        // return false if phone is unlocked, true if it has screen lock.
        // TODO: 2015-09-18 FOR NOW IT ALWAYS REPORT LOCKED, CHANGE IT! 
        return !this.lockStateUtility.isPhoneUnlocked();
    }

    private boolean phoneReportsStayingStill() {
        // if accelerometer does not report movement, return false, otherwise true.
        return false;
    }

    private void notifyAboutPendingAutoRespond() {
        // show something, for example toast that autorespond is pending, with possibility to cancel it by user
    }

    private void unnotifyAboutPendingAutoRespond() {
        // hide toast shown by notifyAoutPendingautorespond
    }

    private void getLocationAndProceed(final String phoneNumber) {
        this.locationUtility.listenForLocationOnce(new LocationChangedCallback() {

            // TODO: 2015-09-16 promises welcome?
            @Override
            public void onLocationChange(Location location) {
                Responder.this.handleIncomingSecondStep(phoneNumber, location);
            }

        });
    }

    private boolean isNormalNumber(String phoneNumber) {
        return true; // return true if normal number - no sms premium or smth.
    }

    private boolean isNumberFromCurrentCountry(String phoneNumber) {
        return true;
    }

    private boolean isInContactBook(String phoneNumber) {
        return true; // check if in contact book
    }

    private boolean isInGroup(String phoneNumber) {
        return true; // probably we need one special group, or selector from exisiting groups allowing user to choose many groups.
    }


    private boolean shouldRespondToThisNumber(String phoneNumber) {
        boolean respondingConstraintsMeet = false;
        boolean countryRespondingConstraintsMeet = false;

        switch (this.respondingCountrySettings) {
            case Responder.RESPONDING_COUNTRY_SETTINGS_ANY_COUNTRY:
                countryRespondingConstraintsMeet = true;
                break;
            case Responder.RESPONDING_COUNTRY_SETTINGS_CURRENT_COUNTRY_ONLY:
                countryRespondingConstraintsMeet = this.isNumberFromCurrentCountry(phoneNumber);
                break;
        }

        switch (this.respondingSettings) {
            case Responder.RESPONDING_SETTINGS_RESPOND_EVERYONE:
                respondingConstraintsMeet = true;
                break;
            case Responder.RESPONDING_SETTINGS_RESPOND_EVERY_NORMAL_NUMBER:
                respondingConstraintsMeet = this.isNormalNumber(phoneNumber);
                break;
            case Responder.RESPONDING_SETTINGS_RESPOND_ONLY_CONTACT_BOOK:
                respondingConstraintsMeet = this.isInContactBook(phoneNumber);
                break;
            case Responder.RESPONDING_SETTINGS_RESPOND_ONLY_GROUP:
                respondingConstraintsMeet = this.isInGroup(phoneNumber);
                break;
        }

        boolean respondingAllowed = respondingConstraintsMeet && countryRespondingConstraintsMeet;


        return respondingAllowed;
    }

    private String generateAutoRespondMessage(String phoneNumber) {
        return "Jadę właśnie motocyklem i nie mogę odebrać. Oddzwonię później.";

        // TODO: 2015-09-08 separate messages for sms and call would be nice
    }

    private void sendSMS(String phoneNumber, String message) {
    }

    private void notifyAboutAutoRespond(String phoneNumber) {
        // this should show some toast like this: 'motoresponder responded XXX person for you. call him'
        // ofc if setting allow this
        if (this.notifyAboutAutoRespond == false) {
            return;
        }
        // do logic.
    }

    private boolean isProxime() {
        // return true if phone reports proximity to smth.
        // TODO: 2015-09-16 recheck, probably invalid 
        return this.sensorsUtility.isProxime();
    }

    private boolean isLightOutside() {
        // return true if light sensor reports light
        // TODO: 2015-09-16 probably invalid 
        return this.sensorsUtility.isLightOutside();
    }

}
