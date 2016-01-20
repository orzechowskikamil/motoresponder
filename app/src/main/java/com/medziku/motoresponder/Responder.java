package com.medziku.motoresponder;

import android.content.Context;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.callbacks.SMSReceivedCallback;
import com.medziku.motoresponder.logic.*;
import com.medziku.motoresponder.utils.*;

/**
 * It's like all responding logic entry point
 */
public class Responder {

    // TODO Cleanup Issue #63
    public boolean notifyAboutAutoRespond = true;
    public boolean showPendingNotification = true;


    public long waitForAnotherGPSCheckTimeout = 20000;

    /**
     * Time for user to get phone out of pocket and respond
     */
    // TODO K. Orzechowski: for development set to 100, for real it should be 10 000 at least. Issue #57
    public long waitBeforeResponding = 100;


    private LockStateUtility lockStateUtility;
    private NumberRules numberRules;
    private UserRide userRide;
    private UserResponded userResponded;
    private ResponderAnswered responderAnswered;

    private Context context;
    private NotificationUtility notificationUtility;
    private SMSUtility smsUtility;
    private CallsUtility callsUtility;
    private SettingsUtility settingsUtility;
    private RespondingDecision respondingDecision;


    public Responder(Context context) {
        this.context = context;

        this.lockStateUtility = new LockStateUtility(context);

        this.smsUtility = new SMSUtility(this.context);
        this.callsUtility = new CallsUtility(this.context);
        this.settingsUtility = new SettingsUtility(this.context);
        this.responderAnswered = new ResponderAnswered(this.settingsUtility, this.lockStateUtility);
        this.userResponded = new UserResponded(this.callsUtility, this.smsUtility);

        LocationUtility locationUtility = new LocationUtility(context);

        MotionUtility motionUtility = new MotionUtility(context);
        SensorsUtility sensorsUtility = new SensorsUtility(context);
        this.notificationUtility = new NotificationUtility(context);

        ContactsUtility contactsUtility = new ContactsUtility(context);


        this.userRide = new UserRide(locationUtility, sensorsUtility, motionUtility);
        this.numberRules = new NumberRules(contactsUtility);

        this.respondingDecision = new RespondingDecision(this.userRide, this.numberRules, this.userResponded, this.responderAnswered);
    }

    public void startResponding() {
        this.smsUtility.listenForSMS(new SMSReceivedCallback() {
            @Override
            public void onSMSReceived(String phoneNumber, String message) {
                Responder.this.onSMSReceived(phoneNumber);
            }
        });

        this.callsUtility.listenForCalls(new Predicate<String>() {
            @Override
            public boolean apply(String phoneNumber) {
                Responder.this.onUnAnsweredCallReceived(phoneNumber);
                return true;
            }
        });


        // TODO K. Orzechowski: remove it later because its only for development #57
        // this.onSMSReceived("791467855");
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
        // call this when phone is unlocked by user Issue #60
        this.cancelAllHandling();
    }


    /**
     * This is method containing all logic of responding in human readable way.
     * In other words: it's just an algorithm.
     *
     * @param phoneNumber Phone number of incoming call/sms
     */
    private void handleIncoming(final String phoneNumber) {
        new RespondingTask(
                this.respondingDecision, this.settingsUtility, this.notificationUtility, this.smsUtility,
                new Predicate<Boolean>() {
                    @Override
                    public boolean apply(Boolean input) {
                        return true;
                    }
                }).execute(phoneNumber);


    }


    private void cancelAllHandling() {
         // TODO already implemented in other branch
    }


}
