package com.medziku.motoresponder;

import android.location.Location;

import com.medziku.motoresponder.services.BackgroundService;
import com.medziku.motoresponder.utils.LocationUtility;
import com.medziku.motoresponder.utils.LockStateUtility;

/**
 * Created by Kamil on 2015-09-08.
 */
public class Responder {

    // TODO refactor it to create RespondingDecision class where this class will become abstract decision about responding or not 
    // while extracting to other classes process of gathering location or sending sms logic

    // TODO k.orzechowskk create action log where every decision is stored and USER can debug settings and see FLOW of algorithm

    private LocationUtility locationUtility;
    //private SensorsUtility sensorsUtility;

    public boolean notifyAboutAutoRespond = true;
    public boolean showPendingNotification = true;
    /**
     * If true, it will assume not riding if phone proximity sensor read false value (no proximity - not in pocket).
     * If false, it will ignore proximity check.
     */
    public boolean includeProximityCheck = true;
    /**
     * If true, it will assume not riding if there is light on the sensor (phone not in pocket).
     * If false, it will ignore light readings.
     */
    public boolean includeLightCheck = true;
    /**
     * If true, it will interpret timeout during gathering location as being in home (often location timeout
     * is caused by being in building, riding through tunnel is rare).
     * If false, it will ignore timeout.
     */
    // TODO K. Orzechowski: for normal it should be true, for development - false
    public boolean interpretLocationTimeoutAsNotRiding = false;
    /**
     * If true, if phone is unlocked it will be assumed as not riding (no automatical answer).
     * If false, it will ignore unlocked/locked state.
     */
    // TODO K. Orzechowski: It should be true - but for development I set false
    public boolean assumePhoneUnlockedAsNotRiding = false;
    /**
     * If true, if accelerometer will report staying still, app will assume that staying = not riding.
     * If false, it will ignore accelerometer reading
     */
    public boolean includeAccelerometerCheck = true;
    public boolean doAnotherGPSCheckIfNotSure = true;


    public int maybeRidingSpeed = 15;
    public int sureRidingSpeed = 60;
    public long waitForAnotherGPSCheckTimeout = 20000;

    /**
     * Time to wait before anything will be done in terms of handling sms/call
     */
    // TODO K. Orzechowski: for development set to 100, for normal it should be 1000 at least
    public long waitAfterReceivingMsgOrCall = 1000;
    /**
     * Time for user to get phone out of pocket and respond
     */
    // TODO K. Orzechowski: for development set to 100, for real it should be 10 000 at least
    public long waitBeforeResponding = 100;

    /**
     * Responding current country or also abroad.
     */
    public int respondingCountrySettings = 0;
    /**
     * Responding to group, contact book, normal numbers or everyone.
     */
    public int respondingSettings = 2;

    public static final int RESPONDING_COUNTRY_SETTINGS_CURRENT_COUNTRY_ONLY = 0;
    public static final int RESPONDING_COUNTRY_SETTINGS_ANY_COUNTRY = 1;

    public static final int RESPONDING_SETTINGS_RESPOND_EVERYONE = 0;
    public static final int RESPONDING_SETTINGS_RESPOND_EVERY_NORMAL_NUMBER = 1;
    public static final int RESPONDING_SETTINGS_RESPOND_ONLY_CONTACT_BOOK = 2;
    public static final int RESPONDING_SETTINGS_RESPOND_ONLY_GROUP = 3;
    private LockStateUtility lockStateUtility;

    private BackgroundService bs;//TODO


    public Responder(BackgroundService bs, LocationUtility locationUtility, LockStateUtility lockStateUtility) {
        // probably we have to start every onsmsreceived in new thread
        // TODO k.orzechowski: refactor it to something like RespondTask constructed again for every response.
        this.bs = bs;
        this.locationUtility = locationUtility;
        this.lockStateUtility = lockStateUtility;
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
    
    private boolean isUserNotRiding(){
        // if rider rides, phone should be in pocket (ofc if somebody use phone during ride outside pocket, he should
        // disable this option).
        // in pocket is proxime (to leg or chest)... If there is no proximity, he is not riding.
        if (this.includeProximityCheck && !this.isProxime()) {
            return false;
        }  
        
               // TODO k.orzechowsk: If you know way of making promise, why not make promisable light check and 
        // TODO k.orzechowsk: proximity check? It will save battery aswell...
        
       // inside pocket should be dark. if it's light, he is probably not riding
        if (this.includeLightCheck && this.isLightOutside()) {
            return false;
        }
        
        
        // if phone doesn't report any movement we can also assume that user is not riding motorcycle
        // TODO k.orzechowsk this name is plural, refactor it to motionSensorsReportsMovement
        boolean deviceStayingStill = !this.motionSensorReportsMovement();
        if (this.includeAccelerometerCheck && deviceStayingStill) {
            return false;
        }

        // TODO k.orzechowsk add Bluetooth Beacon option to identify that you sit on bike IN FUTURE
        // TODO k.orzechowsk add NFC tag in pocket option to identify that you sit on bike IN FUTURE
        // TODO k.orzechowsk identify of stolen bikes via beacon in very very future when app will be popular.

        // TODO k.orzechowsk add option to disable GPS, maybe someone don't want to use it, only gyro?
        float speedKmh = this.getCurrentSpeedKmh();
        
      
        boolean locationTimeouted = speedKmh == -1;
        // TODO K. Orzechowski: this setting is for future, when I implement asking again for location after some time.
        // TODO K. Orzechowski: for now it's just dumb if
        if (this.interpretLocationTimeoutAsNotRiding && locationTimeouted) {
            // if timeout, it means that phone is probably in home with no access to GPS satelites.
            // so if no ride, no need to respond automatically
            return false;
        }

        // TODO K. Orzechowski: add second check of speed if user is between sure riding speed and no riding speed
        // for example: 15 km/h. It can be motorcycle or running. We make another check in few minutes - maybe
        // we hit bigger speed and it become sure.

        if (speedKmh <= this.sureRidingSpeed) {
            return false;
        }
        
        // all conditions when we are sure that user is not riding are not met - so user is riding. 
        return true;
    }


    /**
     * This is method containing all logic of responding in human readable way.
     * In other words: it's just an algorithm.
     *
     * @param phoneNumber Phone number of incoming call/sms
     */
    private void handleIncoming(final String phoneNumber) {
        // for now for simplification just wait one second forclaryfying sensor values

        // TODO K. Orzechowski: not sure if this is safe or lock main thread
        // TODO K.orzechowski: not sure if it is required at all.
        this.sleep(waitAfterReceivingMsgOrCall);

        // if phone is unlocked we do not need to autorespond at all.
        if (this.assumePhoneUnlockedAsNotRiding && this.phoneIsUnlocked()) {
            return;
        }

      

        // do not answer numbers which user doesnt want to autorespond
        if (!this.shouldRespondToThisNumber(phoneNumber)) {

            // TODO K. Orzechowski: I disabled this for now because it's not most important part of application
            // right now. Do it later and remove development bypass. (uncomment return)
            //return;
        }
        
         // show notification to give user possibiity to cancel autorespond
        if (this.showPendingNotification) {
            this.notifyAboutPendingAutoRespond();
        }
        
        // wait some time before responding - give user time to get phone from the pocket
        // or from the desk and respond manually.
        // unlocking phone should break any responding at all
        // TODO K. Orzechowski: not sure if I am able to sleep main thread, and not got ANR
        this.sleep(this.waitBeforeResponding);
        
        // now things will go automatically in one milisecond so it's not required to still show this
        if (this.showPendingNotification) {
            // TODO K. Orzechowski: hmmm. It can be a flaw - check all returns if some return
            // not cause to exit without unnotyfing
            this.unnotifyAboutPendingAutoRespond();
        }
        
        // if phone is unlocked now, we can return - user heard ring, get phone and will
        // respond manually.
        if (this.assumePhoneUnlockedAsNotRiding && this.phoneIsUnlocked()) {
            return;
        }
        
        if (this.isUserNotRiding()){
            return;
        }

       
        

       this.bs.showStupidNotify("MotoResponder", "GPS speed: " + speedKmh);


        String message = this.generateAutoRespondMessage(phoneNumber);
        this.sendSMS(phoneNumber, message);
        this.notifyAboutAutoRespond(phoneNumber);


    }
    
    /**
     * @return Speed in km/h or -1 if location request timeouted. 
     */
    private float getCurrentSpeedKmh(){
        Location location = null;
        try {
            location = this.locationUtility.getAccurateLocation().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
         // -1 is value of speed for timeouted request.
        float speedMs = (location == null) ?  -1 : location.getSpeed();
        float speedKmh = this.msToKmh(speedMs);
        return speedKmh;
    }
    
    private float msToKmh(float speedMs){
        return speedMs*3.6;
    }

    private void sleep(long timeoutMs) {
        try {
            Thread.sleep(timeoutMs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void cancelAllHandling() {
        // call this to break all autoresponding
        // TODO K. Orzechowski: Implement it.
    }

    private boolean phoneIsUnlocked() {
        return !this.lockStateUtility.isPhoneUnlocked();
    }

    private boolean motionSensorReportsMovement() {
        // TODO K. Orzechowski: using here also gyroscope and magneometer is not a bad idea
        // maybe other method will be required for it.
        // TODO K. Orzechowski: if accelerometer does not report movement, return false, otherwise true.
        return true;
    }

    private void notifyAboutPendingAutoRespond() {
        // TODO K. Orzechowski:show something, for example toast that autorespond is pending, with possibility to cancel it by user
    }

    private void unnotifyAboutPendingAutoRespond() {
        // TODO K. Orzechowski:  hide toast shown by notifyAoutPendingautorespond
    }

    private boolean isNormalNumber(String phoneNumber) {
        return true; // TODO K. Orzechowski:  return true if normal number - no sms premium or smth.
    }

    private boolean isNumberFromCurrentCountry(String phoneNumber) {
        // TODO K. Orzechowski: implement real logic
        return true;
    }

    private boolean isInContactBook(String phoneNumber) {
        return true;// TODO K. Orzechowski:  check if in contact book
    }

    private boolean isInGroup(String phoneNumber) {
        return true;
        // TODO K. Orzechowski:  probably we need one special group, or selector from exisiting groups allowing user to choose many groups.
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


        /* TODO k.orzechowski 
           break method below into three sections
           First is BLACKLISTING with options: none, blacklist (put in application), contact book group 
           Second is WHITELISTING with options: none, all contacts, whitelist (put in application), contact book group
           Third is NORMAL/SHORT numbers with options: everyone / normal numbers / short numbers (like sms premium)
        */

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

        // TODO K. Orzechowski: loop when you will got message from your own device!!!!

        // TODO K. Orzechowski: create smth like no more than 2 responses in one day
        // TODO K. Orzechowski: or no more than 2 responses from one unlock to another
        boolean respondingAllowed = respondingConstraintsMeet && countryRespondingConstraintsMeet;


        return respondingAllowed;
    }

    private String generateAutoRespondMessage(String phoneNumber) {

        return "Jadę właśnie motocyklem i nie mogę odebrać. Oddzwonię później.";
        // TODO K. Orzechowski: add possibility to personalize message IN LATER STAGE

        // TODO K. Orzechowski: separate messages for sms and call would be nice
    }

    private void sendSMS(String phoneNumber, String message) {
        // TODO K. Orzechowski: this is empty , implement me
    }

    // TODO K. Orzechowski: add tryNotifyAutoRespond to name or move this.notifyAboutAutoRespond to upper method.
    // idea is to have all things dependent on settings on one level to improve readability
    private void notifyAboutAutoRespond(String phoneNumber) {
        // this should show some toast like this: 'motoresponder responded XXX person for you. call him'
        // ofc if setting allow this
        if (this.notifyAboutAutoRespond == false) {
            return;
        }
        // TODO K. Orzechowski: Implement showing notification , best if with events.
    }

    private boolean isProxime() {
        // return true if phone reports proximity to smth.
        // TODO: 2015-09-16 recheck, probably invalid 
        return this.bs.isProxime();
    }

    private boolean isLightOutside() {
        // return true if light sensor reports light
        // TODO: 2015-09-16 probably invalid 
        return this.bs.isLightOutside();
    }


}
