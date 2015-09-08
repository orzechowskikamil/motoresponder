package com.medziku.myapplication;

/**
 * Created by Kamil on 2015-09-08.
 */
public class Responder {
    
    public int respondingSettings = 2;
    public int respondingCountrySettings = 0;
    public boolean notifyAboutAutoRespond = true;
    public boolean showPendingNotification = true;
    
    public static final int RESPOND_COUNTRY_ONLY_CURRENT = 0;
    public static final int RESPOND_EVERY_COUNTRY = 1;
    
    public static final int RESPOND_EVERYONE = 0;
    public static final int RESPOND_EVERY_NORMAL_NUMBER = 1;
    public static final int RESPOND_ONLY_CONTACT_BOOK = 2;
    public static final int RESPOND_ONLY_GROUP = 3;
    

    public Responder() {
        // probably we have to start every onsmsreceived in new thread
    }
    
    public void onSMSReceived(String phoneNumber) {
        // call this when new SMS is detected
        this.handleIncoming(phoneNumber);
    }

    public void onUnAnsweredCallReceived(String phoneNumber) {
        // call this when new call is detected
        this.handleIncoming(phoneNumber);
    }
    
    public void onPhoneUnlocked(){
        // call this when phone is unlocked by user
        this.cancelAllHandling();
    }

    private void handleIncoming(String phoneNumber) {
        // if phone is unlocked we do not need to autorespond at all.
        if (this.phoneIsUnlocked()) {
            return;
        }
        
        // do not answer numbers which user doesnt want to autorespond
        if (this.shouldRespondToThisNumber(phoneNumber)==false){
            return;
        }

        // show notification to give user possibiity to cancel autorespond
        if (this.showPendingNotification){
            this.notifyAboutPendingAutoRespond();
        }
    
        // wait some time before responding - give user time to get phone from the pocket
        // or from the desk and respond manually.
        // unlocking phone should break any responding at all
        this.wait(10000);
        
        // now things will go automatically in one milisecond so it's not required to still show this
        if (this.showPendingNotification){
            this.unnotifyAboutPendingAutoRespond();
        }
        
        // if phone is unlocked now, we can return - user heard ring, get phone and will
        // respond manually.
        if (this.phoneIsUnlocked()) {
            return;
        }
    
        // if phone doesn't report any movement we can also assume that user is not riding motorcycle
        if (this.phoneReportsAccelerometerMovement() == false) {
            return;
        }
        
        Location location = this.getLocation();
        if (location.timeout){
            // if timeout, it means that phone is probably in home with no access to GPS satelites. 
            // so if no ride, no need to respond automatically
            // TODO add option to disable this.
            return;
        }
        
        if (location.speed < 15){
            // speed is small. too small. not sure if he rides.
            // try to recheck in few minutes.
            this.wait(20000);
            location = this.getLocation();
        }
        
        if (location.speed<60){
            // user is not riding. no need to autorespond
            return;
        }
        
        string message = this.generateAutorespondMessage(phoneNumber);
        this.sendSMS(phoneNumber, message);
        this.notifyAboutAutoRespond(phoneNumber);
    }
    
    private void cancelAllHandling() {
        // call this to break all autoresponding
    }
    
       
    private void wait(int amountMs) {
        // wait for given milliseconds
    }


    private boolean phoneIsUnlocked(){
        // return false if phone is unlocked, true if it has screen lock.
        return false;
    }
    
    private boolean phoneReportsAccelerometerMovement(){
        // if accelerometer does not report movement, return false, otherwise true.
        return false;
    }
    
    private void notifyAboutPendingAutoRespond(){
        // show something, for example toast that autorespond is pending, with possibility to cancel it by user
    }
    
    private void unnotifyAboutPendingAutoRespond(){
        // hide toast shown by notifyAoutPendingautorespond
    }
    
    private Location getLocation(){
        // return location or timeout
    }
    
    private boolean isNormalNumber(String phoneNumber){
        return false; // return true if normal number - no sms premium or smth.
    }
    
    private boolean isNumberFromCurrentCountry(String phoneNumber){
        return false;
    }
    
    private boolean isInContactBook(String phoneNumber){
        return false; // check if in contact book
    }
    
    private boolean isInGroup(String phoneNumber){
        return false; // probably we need one special group, or selector from exisiting groups allowing user to choose many groups.
    }
    
    
    private boolean shouldRespondToThisNumber(String phoneNumber){
        boolean respondingSettingAllow = false;
        boolean respondingCountrySettingAllow = false;
        
        switch(this.respondingCountrySettings){
            case this.RESPOND_EVERY_COUNTRY: respondingCountrySettingAllow = true;break;
            case this.RESPOND_ONLY_CURRENT_COUNTRY: respondingCountrySettingAllow = this.isNumberFromCurrentCountry(phoneNumber);break;
        }
        
        switch (this.respondingSettings){
            case this.RESPOND_EVERYONE: respondingCountrySettingAllow= true; break;
            case this.RESPOND_EVERY_NORMAL_NUMBER: respondingCountrySettingAllow= this.isNormalNumber(phoneNumber); break;
            case this.RESPOND_ONLY_CONTACT_BOOK: respondingCountrySettingAllow= this.isInContactBook(phoneNumber); break;
            case this.RESPOND_ONLY_GROUP: respondingCountrySettingAllow= this.isInGroup(phoneNumber);break;
        }
        
        boolean respondingAllowed = respondingSettingAllow && respondingCountrySettingAllow;
        return respondingAllowed;
    }
    
    private String generateAutorespondMessage(String phoneNumber){
        return "";
    }
    
    private void notifyAboutAutoRespond(String phoneNumber){
        // this should show some toast like this: 'motoresponder responded XXX person for you. call him'
        // ofc if setting allow this
        if (this.notifyAboutAutoRespond===false){
            return;
        }
        // do logic.
    }

}
