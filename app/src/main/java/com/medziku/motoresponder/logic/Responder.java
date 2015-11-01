package com.medziku.motoresponder.logic;

import android.content.Context;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.callbacks.SMSReceivedCallback;
import com.medziku.motoresponder.utils.*;

/**
 * It's like all responding logic entry point
 */
public class Responder {


    // TODO k.orzechowskk create action log where every decision is stored and USER can debug settings
    // TODO k.orzechowskk and see FLOW of algorithm
    // TODO K. Orzechowski: it can be done with heavy use of toasts!

    public boolean notifyAboutAutoRespond = true;
    public boolean showPendingNotification = true;


    /**
     * If true, if phone is unlocked it will be assumed as not riding (no automatical answer).
     * If false, it will ignore unlocked/locked state.
     */
    // TODO K. Orzechowski: It should be true - but for development I set false
    public boolean assumePhoneUnlockedAsNotRiding = false;


    public long waitForAnotherGPSCheckTimeout = 20000;

    /**
     * Time for user to get phone out of pocket and respond
     */
    // TODO K. Orzechowski: for development set to 100, for real it should be 10 000 at least
    public long waitBeforeResponding = 100;


    private LockStateUtility lockStateUtility;
    private NumberRules numberRules;
    private UserRide userRide;

    private Context context;
    private NotificationUtility notificationUtility;

    public Responder(Context context) {

        // probably we have to start every onsmsreceived in new thread
        this.context = context;

        LocationUtility locationUtility = new LocationUtility(context);
        this.lockStateUtility = new LockStateUtility(context);
        MotionUtility motionUtility = new MotionUtility(context);
        SensorsUtility sensorsUtility = new SensorsUtility(context);
        this.notificationUtility = new NotificationUtility(context);


        this.userRide = new UserRide(locationUtility, sensorsUtility, motionUtility);
        this.numberRules = new NumberRules();
    }

    public void startResponding() {
        SMSUtility smsUtility = new SMSUtility(this.context);
        CallsUtility callsUtility = new CallsUtility(this.context);


        smsUtility.listenForSMS(new SMSReceivedCallback() {
            @Override
            public void onSMSReceived(String phoneNumber, String message) {
                Responder.this.onSMSReceived(phoneNumber);
            }
        });

        callsUtility.listenForCalls(new Predicate<String>() {
            @Override
            public boolean apply(String phoneNumber) {
                Responder.this.onUnAnsweredCallReceived(phoneNumber);
                return true;
            }
        });


        // TODO K. Orzechowski: remove it later because its only for development
        this.onSMSReceived("79146755");
    }

    public void stopResponding() {
        // TODO K. Orzechowski: stop it really.
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
        // TODO K. Orzechowski: bind it
        // call this when phone is unlocked by user
        this.cancelAllHandling();
    }


    /**
     * This is method containing all logic of responding in human readable way.
     * In other words: it's just an algorithm.
     *
     * @param phoneNumber Phone number of incoming call/sms
     */
    private void handleIncoming(final String phoneNumber) {
        // if phone is unlocked we do not need to autorespond at all.
        if (this.assumePhoneUnlockedAsNotRiding && this.phoneIsUnlocked()) {
            return;
        }

        // show notification to give user possibiity to cancel autorespond
        if (this.showPendingNotification) {
            this.notifyAboutPendingAutoRespond();
        }


        new RespondingDecision(this.userRide, this.numberRules, new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                // TODO K. Orzechowski: uncomment this after getting info out from responding decider

                // if phone is unlocked now, we can return - user heard ring, get phone and will
                // respond manually.
                if (Responder.this.assumePhoneUnlockedAsNotRiding && Responder.this.phoneIsUnlocked()) {
                    return false;
                }

                // wait some time before responding - give user time to get phone from the pocket
                // or from the desk and respond manually.
                // unlocking phone should break any responding at all
                // TODO K. Orzechowski: not sure if I am able to sleep main thread, and not got ANR
//                Responder.this.sleep(Responder.this.waitBeforeResponding);

                // now things will go automatically in one milisecond so it's not required to still show this
                if (Responder.this.showPendingNotification) {
                    // TODO K. Orzechowski: hmmm. It can be a flaw - check all returns if some return
                    // not cause to exit without unnotyfing
                    Responder.this.unnotifyAboutPendingAutoRespond();
                }


//        this.bs.showStupidNotify("MotoResponder", "GPS speed: " + speedKmh);


                String message = Responder.this.generateAutoRespondMessage(phoneNumber);
                Responder.this.sendSMS(phoneNumber, message);
                Responder.this.notifyAboutAutoRespond(phoneNumber);
                return true;
            }
        }).execute(phoneNumber);


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


    private void notifyAboutPendingAutoRespond() {
        // TODO K. Orzechowski:show something, for example toast that autorespond is pending, with possibility to cancel it by user
    }

    private void unnotifyAboutPendingAutoRespond() {
        // TODO K. Orzechowski:  hide toast shown by notifyAoutPendingautorespond
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
        if (!this.notifyAboutAutoRespond) {
            return;
        }
        // TODO K. Orzechowski: Implement showing notification , best if with events.
    }


    public void showStupidNotify(String title, String content) {
        this.hideNotification();
        this.showNotification(title, content, "test info");
    }

    private void hideNotification() {
        this.notificationUtility.hideNotification();
    }


    private void showNotification(String title, String content, String info) {
        this.notificationUtility.showNotification(title, content, info);
    }

}
