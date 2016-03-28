package com.medziku.motoresponder.logic;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.callbacks.SMSReceivedCallback;
import com.medziku.motoresponder.utils.*;

/**
 * It's like all responding logic entry point
 */
public class Responder {
    protected RespondingTasksQueue respondingTasksQueue;
    protected LockStateUtility lockStateUtility;
    protected NumberRules numberRules;
    protected UserRide userRide;
    protected AlreadyResponded alreadyResponded;
    protected Context context;
    protected NotificationUtility notificationUtility;
    protected SMSUtility smsUtility;
    protected CallsUtility callsUtility;
    protected Settings settings;
    protected RespondingDecision respondingDecision;
    protected DeviceUnlocked deviceUnlocked;
    protected LocationUtility locationUtility;
    protected ContactsUtility contactsUtility;
    protected MotionUtility motionUtility;
    protected SensorsUtility sensorsUtility;
    protected ResponsePreparator responsePreparator;
    protected SharedPreferencesUtility sharedPreferencesUtility;
    protected boolean isRespondingNow;

    public Responder(Context context) {
        this.context = context;

        this.createUtilities();

        this.settings = this.createSettings();

        this.alreadyResponded = this.createAlreadyResponded();
        this.deviceUnlocked = this.createDeviceUnlocked();
        this.userRide = this.createUserRide();
        this.numberRules = this.createNumberRules();
        this.respondingDecision = this.createRespondingDecision();
        this.responsePreparator = this.createResponsePreparator();
        this.respondingTasksQueue = this.createRespondingTasksQueue();
    }


    /**
     * Call this to start responding
     */
    public void startResponding() {
        if (this.isRespondingNow == true) {
            return;
        }

        if (this.settings.isResponderEnabled() == false) {
            return;
        }

        this.isRespondingNow = true;
        // TODO K.Orzechowski throw out this smsreceivedcallback and replace it with predicate  #49
        this.listenToProximityChanges();
        this.listenForSMS();
        this.listenForCalls();
        this.listenForLockStateChanges();
    }


    /**
     * Call this to stop responding at all.
     */
    public void stopResponding() {
        this.isRespondingNow = false;
        this.respondingTasksQueue.cancelAllHandling();

        this.stopListeningForProximityChanges();
        this.stopListeningForSMS();
        this.stopListeningForCalls();
        this.stopListeningForLockStateChanges();
    }


    /**
     * Called when user will receive sms
     */
    public void onSMSReceived(String phoneNumber, String message) {
        this.handleIncoming(new SMSRespondingSubject(phoneNumber, message));
    }

    /**
     * Called when user will not pick a ringing call
     */
    public void onUnAnsweredCallReceived(String phoneNumber) {
        this.handleIncoming(new CallRespondingSubject(phoneNumber));
    }

    /**
     * Called when phone will be unlocked by user (screenlock passed)
     */
    public void onPhoneUnlocked() {
        this.respondingTasksQueue.cancelAllHandling();
    }

    /**
     * This method handles incoming sms or call by creating instance of RespondingTask (which handles incoming)
     * and add it to list of currently pending responses. After successfull autoresponse, it's removed from
     * this list.
     */
    protected void handleIncoming(RespondingSubject subject) {
        this.respondingTasksQueue.createAndExecuteRespondingTask(subject);
    }


    protected void listenForSMS() {
        this.smsUtility.listenForSMS(new SMSReceivedCallback() {
            @Override
            public void onSMSReceived(String phoneNumber, String message) {
                Responder.this.onSMSReceived(phoneNumber, message);
            }
        });
    }

    protected void listenForLockStateChanges() {
        try {
            this.lockStateUtility.listenToLockStateChanges(new Predicate<Boolean>() {
                @Override
                public boolean apply(Boolean isLocked) {
                    if (isLocked == false) {
                        Responder.this.onPhoneUnlocked();
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void listenForCalls() {
        this.callsUtility.listenForUnansweredCalls(new Predicate<String>() {
            @Override
            public boolean apply(String phoneNumber) {
                Responder.this.onUnAnsweredCallReceived(phoneNumber);
                return true;
            }
        });
    }

    protected void stopListeningForLockStateChanges() {
        this.lockStateUtility.stopListeningToLockStateChanges();
    }

    protected void stopListeningForCalls() {
        this.callsUtility.stopListeningForCalls();
    }

    protected void stopListeningForSMS() {
        this.smsUtility.stopListeningForSMS();
    }

    protected void listenToProximityChanges() {
        this.sensorsUtility.registerSensors();
    }

    protected void stopListeningForProximityChanges() {
        this.sensorsUtility.unregisterSensors();

    }
    // region factory methods

    protected void createUtilities() {
        this.sharedPreferencesUtility = new SharedPreferencesUtility(this.context);
        this.lockStateUtility = new LockStateUtility(this.context);
        this.smsUtility = new SMSUtility(this.context);
        this.callsUtility = new CallsUtility(this.context);
        this.notificationUtility = new NotificationUtility(this.context);
        this.locationUtility = new LocationUtility(this.context);
        this.contactsUtility = new ContactsUtility(this.context);
        this.motionUtility = new MotionUtility(this.context);
        this.sensorsUtility = new SensorsUtility(this.context);
    }

    protected AlreadyResponded createAlreadyResponded() {
        return new AlreadyResponded(this.callsUtility, this.smsUtility);
    }


    protected DeviceUnlocked createDeviceUnlocked() {
        return new DeviceUnlocked(this.settings, this.lockStateUtility);
    }


    protected UserRide createUserRide() {
        return new UserRide(this.locationUtility, this.sensorsUtility, this.motionUtility);
    }


    protected NumberRules createNumberRules() {
        return new NumberRules(this.contactsUtility);
    }

    protected ResponsePreparator createResponsePreparator() {
        return new ResponsePreparator(this.settings, this.locationUtility);
    }

    protected Settings createSettings() {
        return new Settings(this.sharedPreferencesUtility);
    }


    protected RespondingTasksQueue createRespondingTasksQueue() {
        return new RespondingTasksQueue(
                this.notificationUtility,
                this.smsUtility,
                this.settings,
                this.respondingDecision,
                this.responsePreparator
        );
    }


    protected RespondingDecision createRespondingDecision() {
        return new RespondingDecision(this.userRide, this.numberRules, this.alreadyResponded, this.deviceUnlocked);
    }

    // endregion
}