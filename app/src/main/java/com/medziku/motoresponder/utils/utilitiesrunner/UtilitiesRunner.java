package com.medziku.motoresponder.utils.utilitiesrunner;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.medziku.motoresponder.utils.utilitiesrunner.runners.*;

/**
 * This class can be used to test real behavior of Utilities on some device or emulator, because it looks like
 * unit tests and instrumented tests fail to do this task, environments of instrumented tests and real app
 * differ a lot.
 * <p/>
 * Active tests requires user interactions.
 * For more complicated logic testing, which is not oriented about proper input/output from api, but from internal logic
 * of Utility, unit tests are created (those unit tests have assumption that input/output from API is correct, so they
 * summed up are covering 100% of class).
 * <p/>
 * TO RUN this "suite", switch ARE_PSEUDOTESTS_ENABLED to true, and run APP on device or emulator (NOT UNIT TESTS).
 * <p/>
 * It is for integration testing so it must run on real environment to exclude any fuckups.
 * <p/>
 * RUN THOSE TESTS manually if you feel that Utilities (part of app which works against Android API) are not going to
 * work under real device
 * We have unit tests for them but they can't cover everything.
 * <p/>
 * // TODO Kamil Orzechowski: think about converting it to instrumented test or flavor of application (gradle) #Issue #72
 */
public class UtilitiesRunner {

    public static final String TAG = "MainUtilityRunner";

    /**
     * When this is true, debug utilities (tests of utilities) will be run instead of motoresponder application.
     */
    public static final boolean ARE_PSEUDOTESTS_ENABLED = true;


    private LockStateUtilityRunner lockStateUtilityRunner;
    private LocationUtilityRunner locationUtilityRunner;
    private MotionUtilityRunner motionUtilityRunner;
    private NotificationUtilityRunner notificationUtilityRunner;
    private SensorsUtilityRunner sensorsUtilityRunner;
    private CallsUtilityRunner callsUtilityRunnner;
    private ContactsUtilityRunner contactsUtilityRunner;
    private SettingsUtilityRunner settingsUtilityRunner;
    private SMSUtilityRunner smsUtilityRunner;


    public UtilitiesRunner(Context context) {
        this.locationUtilityRunner = new LocationUtilityRunner(context);
        this.motionUtilityRunner = new MotionUtilityRunner(context);
        this.lockStateUtilityRunner = new LockStateUtilityRunner(context);
        this.notificationUtilityRunner = new NotificationUtilityRunner(context);
        this.sensorsUtilityRunner = new SensorsUtilityRunner(context);
        this.callsUtilityRunnner = new CallsUtilityRunner(context);
        this.contactsUtilityRunner = new ContactsUtilityRunner(context);
        this.settingsUtilityRunner = new SettingsUtilityRunner(context);
        this.smsUtilityRunner = new SMSUtilityRunner(context);
    }


    public void run() {
        (new AsyncTask<Boolean, Boolean, Boolean>() {
            @Override
            protected Boolean doInBackground(Boolean... params) {
                // unfortunately I can't show those errors in any different way that throwing exception
                // and catching it here. Hope it will be enough to test.
                Log.d(TAG, "Utilities runner... starting...");
                this.runTest();
                Log.d(TAG, "Utilities runner... finished...");

                return null;
            }

            private void runTest() {
                // choose the test which you are interested in
                // after performing, leave short note with date here about result of test
                switch (61) {
                    case 1:
                        // this is working 11.03.2016 on Android 5.1
                        // beware! I test it only at home at staying still
                        // newer received more than one event - maybe because of being at home
                        // not outside
                        UtilitiesRunner.this.locationUtilityRunner.testOfGettingAccurateLocation();
                        break;

                    case 2:
                        // this is working 07.03.2016 on Android 5.1
                        UtilitiesRunner.this.motionUtilityRunner.testOfIsDeviceInMotion();
                        break;

                    case 30:
                        // this is working 09.03.2016 on Android 5.1
                        UtilitiesRunner.this.lockStateUtilityRunner.testOfListeningToLockStateChanges();
                        break;
                    case 31:
                        // this is working 09.03.2016 on Android 5.1
                        UtilitiesRunner.this.lockStateUtilityRunner.testOfIsLocked();
                        break;

                    case 40:
                        // this is working 09.03.2016 on Android 5.1
                        UtilitiesRunner.this.notificationUtilityRunner.testOfShowingAndHidingOngoingNotification();
                        break;
                    case 41:
                        // this is working 09.03.2016 on Android 5.1
                        UtilitiesRunner.this.notificationUtilityRunner.testOfShowingToast();
                        break;

                    case 5:
                        // this is working 09.03.2016 on Android 5.1
                        UtilitiesRunner.this.sensorsUtilityRunner.proximitySensorTest();
                        break;

                    case 60:
                        // this is working 15.03.2016 if number is strictly following putted number on Android 5.1
                        // normalization not work.
                        UtilitiesRunner.this.callsUtilityRunnner.testWasOutgoingCallAfterDate();
                        break;
                    case 61:
                        // this is working 19.03.2016 - only unanswered calls trigger callback
                        UtilitiesRunner.this.callsUtilityRunnner.testListenForUnansweredCalls();
                        break;
                    case 62:
                        // this is working 15.03.2016 on Android 5.1
                        UtilitiesRunner.this.callsUtilityRunnner.testStopListeningForCalls();
                        break;

                    case 70:
                        // this is working 12.03.2016 on Android 5.1
                        UtilitiesRunner.this.contactsUtilityRunner.testContactBookContainsContact();
                        break;
                    case 71:
                        // this is working 12.03.2016 on Android 5.1 motog 1 gen - correctly throw exception on
                        // SIM card which doesn't allow reading current phone number.
                        UtilitiesRunner.this.contactsUtilityRunner.testReadCurrentDevicePhoneNumber();
                        break;

                    case 8:
                        // this is working on 14.03.2016 on Android 5.1 motog
                        UtilitiesRunner.this.settingsUtilityRunner.testSettingResponseText();
                        break;

                    case 90:
                        // this is working on 14.03.2016 on Android 5.1 motog
                        // normalization not work.
                        UtilitiesRunner.this.smsUtilityRunner.testGettingDateOfLastSMSSent();
                        break;
                    case 91:
                        // this is working on 14.03.2016 on Android 5.1 motog
                        UtilitiesRunner.this.smsUtilityRunner.testListeningForSMS();
                        break;
                    case 92:
                        // this is working on 14.03.2016 on Android 5.1 motog
                        UtilitiesRunner.this.smsUtilityRunner.testSendingSMS();
                        break;
                    case 93:
                        // this is working on 14.03.2016 on Android 5.1 motog
                        // normalization not work.
                        UtilitiesRunner.this.smsUtilityRunner.testSendingSMSAndGettingItsDate();
                        break;
                    case 94:
                        // this is working on 14.03.2016 on Android 5.1 motog.
                        UtilitiesRunner.this.smsUtilityRunner.testWasOutgoingSMSSentAfterDate();
                        break;
                }
            }
        }).execute();
    }
}
