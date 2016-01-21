package com.medziku.motoresponder;

import android.content.Context;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.callbacks.SMSReceivedCallback;
import com.medziku.motoresponder.logic.*;
import com.medziku.motoresponder.utils.*;

import java.util.ArrayList;
import java.util.List;

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


    private List<RespondingTask> pendingRespondingTasks;
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
    private boolean isRespondingNow;

    public Responder(Context context) {
        this.context = context;

        this.lockStateUtility = new LockStateUtility(context);
        this.pendingRespondingTasks = new ArrayList<>();

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

    /**
     * Call this to start responding
     */
    public void startResponding() throws Exception {
        if (this.isRespondingNow == true) {
            throw new Exception("Not possible to start responding again if already responding");
        }
        this.isRespondingNow = true;
        // TODO K.Orzechowski throw out this smsreceivedcallback and replace it with predicate  #49
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

        this.lockStateUtility.listenToLockStateChanges(new Predicate<Boolean>() {
            @Override
            public boolean apply(Boolean isLocked) {
                if (isLocked == false) {
                    Responder.this.onPhoneUnlocked();
                }
                return true;
            }
        });


        // TODO K. Orzechowski: remove it later because its only for development #57
        // this.onSMSReceived("791467855");
    }

    /**
     * Call this to stop responding at all.
     */
    public void stopResponding() throws Exception {
        this.isRespondingNow = false;
        this.cancelAllHandling();

        this.smsUtility.stopListeningForSMS();
        this.callsUtility.stopListeningForCalls();
        this.lockStateUtility.stopListeningToLockStateChanges();
    }

    /**
     * Called when user will receive sms
     */
    public void onSMSReceived(String phoneNumber) {
        this.handleIncoming(phoneNumber);
    }

    /**
     * Called when user will not pick a ringing call
     */
    public void onUnAnsweredCallReceived(String phoneNumber) {
        this.handleIncoming(phoneNumber);
    }

    /**
     * Called when phone will be unlocked by user (screenlock passed)
     */
    public void onPhoneUnlocked() {
        this.cancelAllHandling();
    }

    /**
     * This method handles incoming sms or call by creating instance of RespondingTask (which handles incoming)
     * and add it to list of currently pending responses. After successfull autoresponse, it's removed from
     * this list.
     */
    private void handleIncoming(final String phoneNumber) {

        final RespondingTask[] task = new RespondingTask[1];

        task[0] = new RespondingTask(
                this.respondingDecision, this.settingsUtility, this.notificationUtility, this.smsUtility,
                new Predicate<Boolean>() {
                    @Override
                    public boolean apply(Boolean input) {
                        Responder.this.pendingRespondingTasks.remove(task[0]);
                        return true;
                    }
                });

        this.pendingRespondingTasks.add(task[0]);
        task[0].execute(phoneNumber);
    }


    /**
     * This method cancels all currently pending responding tasks, and clean after themselves.
     */
    private void cancelAllHandling() {
        for (RespondingTask task : this.pendingRespondingTasks) {
            task.cancelResponding();
        }
    }

}
