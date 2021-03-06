package com.medziku.motoresponder.pseudotesting;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.medziku.motoresponder.pseudotesting.utilities.*;

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
    public static final boolean ARE_PSEUDOTESTS_ENABLED = false;


    private LockStateUtilityTest lockStateUtilityTest;
    private LocationUtilityTest locationUtilityTest;
    private MotionUtilityTest motionUtilityTest;
    private NotificationUtilityTest notificationUtilityTest;
    private SensorsUtilityTest sensorsUtilityTest;
    private CallsUtilityTest callsUtilityRunnner;
    private ContactsUtilityTest contactsUtilityTest;
    private SharedPreferencesUtilityTest sharedPreferencesUtilityTest;
    private SMSUtilityTest smsUtilityTest;
    private WiFiUtilityTest wifiUtilityTest;


    public UtilitiesRunner(Context context) {
        this.locationUtilityTest = new LocationUtilityTest(context);
        this.motionUtilityTest = new MotionUtilityTest(context);
        this.lockStateUtilityTest = new LockStateUtilityTest(context);
        this.notificationUtilityTest = new NotificationUtilityTest(context);
        this.sensorsUtilityTest = new SensorsUtilityTest(context);
        this.callsUtilityRunnner = new CallsUtilityTest(context);
        this.contactsUtilityTest = new ContactsUtilityTest(context);
        this.sharedPreferencesUtilityTest = new SharedPreferencesUtilityTest(context);
        this.smsUtilityTest = new SMSUtilityTest(context);
        this.wifiUtilityTest = new WiFiUtilityTest(context);
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
                switch (32) {
                    case 1:
                        // this is working 10.04.2016 on Android 5.1
                        UtilitiesRunner.this.locationUtilityTest.testOfGettingAccurateLocation();
                        break;

                    case 11:
                        // this is working 10.04.2016 on Android 5.1
                        UtilitiesRunner.this.locationUtilityTest.testOfBreakingAccurateLocationProcess();
                        break;

                    case 2:
                        // this is working 07.03.2016 on Android 5.1
                        UtilitiesRunner.this.motionUtilityTest.testOfIsDeviceInMotion();
                        break;

                    case 30:
                        // this is working 09.03.2016 on Android 5.1
                        UtilitiesRunner.this.lockStateUtilityTest.testOfListeningToLockStateChanges();
                        break;
                    case 31:
                        // this is working 09.03.2016 on Android 5.1
                        UtilitiesRunner.this.lockStateUtilityTest.testOfIsLocked();
                        break;
                    case 32:
                        UtilitiesRunner.this.lockStateUtilityTest.testOfDetectingPowerSave();
                        break;


                    case 40:
                        // this is working 31.03.2016 android 5.1 motog
                        UtilitiesRunner.this.notificationUtilityTest.testOfShowingAndHidingOngoingNotification();
                        break;

                    case 41:
                        // this is working 31.03.2016 android 5.1 motog
                        UtilitiesRunner.this.notificationUtilityTest.testOfShowingToast();
                        break;


                    case 42:
                        // this is working 31.03.2016 android 5.1 motog
                        UtilitiesRunner.this.notificationUtilityTest.testOfShowingNotOngoingNotification();
                        break;


                    case 43:
                        // this is working 31.03.2016 android 5.1 motog
                        UtilitiesRunner.this.notificationUtilityTest.testOfShowingAndHidingBigTextNotification();
                        break;


                    case 5:
                        // this is working 09.03.2016 on Android 5.1
                        UtilitiesRunner.this.sensorsUtilityTest.proximitySensorTest();
                        break;

                    case 60:
                        // this is working 20.03.2016 on Android 5.1
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
                        // this is working 20.03.2016 on Android 5.1
                        UtilitiesRunner.this.contactsUtilityTest.testContactBookContainsContact();
                        break;
                    case 71:
                        // this is working 12.03.2016 on Android 5.1 motog 1 gen - correctly throw exception on
                        // SIM card which doesn't allow reading current phone number.
                        UtilitiesRunner.this.contactsUtilityTest.testReadCurrentDevicePhoneNumber();
                        break;
                    case 72:
                        // this is working 17 05 2016 on android 5.1 motog
                        UtilitiesRunner.this.contactsUtilityTest.testReadAllContactBookGroupNames();
                        break;
                    case 73:
                        // this is working 17.05.2016 on android 5.1 motog
                        UtilitiesRunner.this.contactsUtilityTest.testGetContactID();
                        break;
                    case 74:
                        // this is working 17.05.2016 on android 5.1 motog
                        UtilitiesRunner.this.contactsUtilityTest.testGetContactName();
                        break;
                    case 75:
                        // this is working 17 05 2016 on android motog 5.1
                        UtilitiesRunner.this.contactsUtilityTest.testHasGroupNumber();
                        break;
                    case 76:
                        // this is working 17 05 2016 on motog
                        UtilitiesRunner.this.contactsUtilityTest.testReadGroupID();
                        break;
                    case 77:
                        // this is working 23 05 2016 on motog
                        UtilitiesRunner.this.contactsUtilityTest.testReadCurrentMobileCountryCode();
                        break;


                    case 80:
                        // This is working 29.03.2016 on android 5.1 motog
                        UtilitiesRunner.this.sharedPreferencesUtilityTest.testSettingResponseText();
                        break;
                    case 81:
                        // This is working 29.03.2016 on android 5.1 motog
                        UtilitiesRunner.this.sharedPreferencesUtilityTest.testReadingFromResources();
                        break;
                    case 82:
                        // this is working 09.04.2016 on android 5.1 motog
                        UtilitiesRunner.this.sharedPreferencesUtilityTest.testListeningToChanges();
                        break;
                    case 83:
                        // this is working 09.04.2016 on android 5.1 motog
                        UtilitiesRunner.this.sharedPreferencesUtilityTest.testListeningToChangesInUI();
                        break;

                    case 90:
                        // this is working on 20.03.2016 on Android 5.1 motog
                        UtilitiesRunner.this.smsUtilityTest.testGettingDateOfLastSMSSent();
                        break;
                    case 91:
                        // This is working 16.05.2016 on android 5.1 motog
                        UtilitiesRunner.this.smsUtilityTest.testListeningForSMS();
                        break;
                    case 92:
                        // This is working 10.04.2016 on android 5.1 motog
                        UtilitiesRunner.this.smsUtilityTest.testSendingSMS();
                        break;
                    case 93:
                        // this is working on 14.03.2016 on Android 5.1 motog
                        UtilitiesRunner.this.smsUtilityTest.testSendingSMSAndGettingItsDate();
                        break;
                    case 94:
                        // this is working on 20.03.2016 on Android 5.1 motog
                        UtilitiesRunner.this.smsUtilityTest.testWasOutgoingSMSSentAfterDate();
                        break;

                    case 100:
                        // working on 23.05.2016 on motog 5.1
                        UtilitiesRunner.this.wifiUtilityTest.wifiSensorTest();
                        break;
                }
            }
        }).execute();
    }
}
