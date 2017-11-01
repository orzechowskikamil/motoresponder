package com.medziku.motoresponder.logic;

import android.content.Context;
import com.google.common.base.Predicate;
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


    private boolean currentlyListeningForSMS = false;
    private boolean currentlyListeningForCalls = false;

    private CustomLog log;
    private GeolocationRequestRecognition geolocationRequestRecognition;
    private WiFiUtility wiFiUtility;
    private CountryPrefix countryPrefix;

    public Responder(Context context) {
        this.context = context;


        this.createUtilities();

        this.settings = this.createSettings();

        this.log = new CustomLog(this.settings);

        this.alreadyResponded = this.createAlreadyResponded();
        this.deviceUnlocked = this.createDeviceUnlocked();
        this.userRide = this.createUserRide();
        this.numberRules = this.createNumberRules();
        this.respondingDecision = this.createRespondingDecision();
        this.responsePreparator = this.createResponsePreparator();
        this.respondingTasksQueue = this.createRespondingTasksQueue();
        this.geolocationRequestRecognition = this.createGeolocationRequestRecognition();
        this.countryPrefix = this.createCountryPrefix();
    }

    /**
     * Call this to afterStart responding
     */
    public void startResponding() {
        if (this.isRespondingNow == true) {
            return;
        }

        if (this.settings.isResponderEnabled() == false) {
            return;
        }

        this.log.add("Responder started responding. It's listening to unanswered calls, and SMSes.");

        this.isRespondingNow = true;
        this.listenToIncomingAccordingToSettings();
        this.settings.listenToChangeRespondToSmsOrCallSetting(new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean input) {
                Responder.this.listenToIncomingAccordingToSettings();
                return false;
            }
        });
        this.listenToProximityChanges();
        this.listenForLockStateChanges();
    }

    /**
     * Call this to stop responding at all.
     */
    public void stopResponding() {
        this.isRespondingNow = false;
        this.respondingTasksQueue.cancelAllHandling();

        this.log.add("Responder stopped responding (it was disabled).");

        this.stopListeningForProximityChanges();
        this.stopListeningForSMS();
        this.stopListeningForCalls();
        this.stopListeningForLockStateChanges();
    }

    /**
     * Called when user will receive sms
     */
    public void onSMSReceived(String phoneNumber, String message) {
        SMSRespondingSubject subject;

        this.log.add("Received sms from " + phoneNumber+" \r\n\r\n");

        if (this.geolocationRequestRecognition.isGeolocationRequest(message)) {
            subject = new GeolocationRequestRespondingSubject(phoneNumber, message);
            this.log.add("This SMS was recognized as location request (contains some patterns).");
        } else {
            subject = new SMSRespondingSubject(phoneNumber, message);
        }

        this.handleIncoming(subject);
    }

    /**
     * Called when user will not pick a ringing call
     */
    public void onUnAnsweredCallReceived(String phoneNumber) {
        this.log.add("Unanswered call from " + phoneNumber + " happened. \r\n\r\n");
        this.handleIncoming(new CallRespondingSubject(phoneNumber));
    }

    /**
     * Called when phone will be unlocked by user (screenlock passed)
     */
    public void onPhoneUnlocked() {
        if (this.settings.isAssumingScreenUnlockedAsNotRidingEnabled()) {
            this.log.add("Phone unlocked, cancelling all autoresponding processes.");
            this.respondingTasksQueue.cancelAllHandling();
        }
    }

    protected CountryPrefix createCountryPrefix() {
        return new CountryPrefix(this.contactsUtility);
    }

    protected void listenToIncomingAccordingToSettings() {
        if (this.settings.isRespondingForSMSEnabled() == true) {
            this.listenForSMS();
        } else {
            this.stopListeningForSMS();
        }
        if (this.settings.isRespondingForCallsEnabled() == true) {
            this.listenForCalls();
        } else {
            this.stopListeningForCalls();
        }
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
        if (this.currentlyListeningForSMS == true) {
            return;
        }

        this.smsUtility.listenForSMS(new Predicate<SMSObject>() {
            @Override
            public boolean apply(SMSObject input) {
                Responder.this.onSMSReceived(input.phoneNumber, input.message);
                return false;
            }
        });
        this.currentlyListeningForSMS = true;
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
        if (this.currentlyListeningForCalls == true) {
            return;
        }

        this.callsUtility.listenForUnansweredCalls(new Predicate<String>() {
            @Override
            public boolean apply(String phoneNumber) {
                Responder.this.onUnAnsweredCallReceived(phoneNumber);
                return true;
            }
        });
        this.currentlyListeningForCalls = true;
    }

    protected void stopListeningForLockStateChanges() {
        this.lockStateUtility.stopListeningToLockStateChanges();
    }

    protected void stopListeningForCalls() {
        if (this.currentlyListeningForCalls == false) {
            return;
        }
        this.callsUtility.stopListeningForCalls();
        this.currentlyListeningForCalls = false;
    }

    protected void stopListeningForSMS() {
        if (this.currentlyListeningForSMS == false) {
            return;
        }
        this.smsUtility.stopListeningForSMS();
        this.currentlyListeningForSMS = false;
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
        this.motionUtility = new MotionUtility(this.context, this.lockStateUtility);
        this.sensorsUtility = new SensorsUtility(this.context);
        this.wiFiUtility = new WiFiUtility(this.context);
    }

    protected AlreadyResponded createAlreadyResponded() {
        return new AlreadyResponded(this.callsUtility, this.smsUtility);
    }


    protected DeviceUnlocked createDeviceUnlocked() {
        return new DeviceUnlocked(this.settings, this.lockStateUtility);
    }


    protected UserRide createUserRide() {
        return new UserRide(this.settings, this.locationUtility, this.sensorsUtility, this.motionUtility, this.wiFiUtility, this.log);
    }


    protected NumberRules createNumberRules() {
        return new NumberRules(this.contactsUtility, this.countryPrefix, this.settings, this.log);
    }

    protected ResponsePreparator createResponsePreparator() {
        return new ResponsePreparator(this.settings, this.locationUtility, this.contactsUtility);
    }

    protected Settings createSettings() {
        return new Settings(this.sharedPreferencesUtility);
    }


    protected GeolocationRequestRecognition createGeolocationRequestRecognition() {
        return new GeolocationRequestRecognition(this.settings);
    }

    protected RespondingTasksQueue createRespondingTasksQueue() {
        return new RespondingTasksQueue(
                this.notificationUtility,
                this.smsUtility,
                this.contactsUtility,
                this.lockStateUtility,
                this.settings,
                this.respondingDecision,
                this.responsePreparator,
                this.log
        );
    }


    protected RespondingDecision createRespondingDecision() {
        return new RespondingDecision(this.userRide, this.numberRules, this.alreadyResponded, this.deviceUnlocked, this.settings, this.log);
    }

    // endregion
}
