package com.medziku.motoresponder.pseudotesting.integration;

import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.logic.*;
import com.medziku.motoresponder.utils.*;

import java.util.Date;

public class RespondingDecisionIntegrationTest {
    private static final String TAG = "RespondingDecisionTest";
    private Context context;
    private RespondingDecision respondingDecision;

    public RespondingDecisionIntegrationTest(Context context) {
        this.context = context;
    }

    public static void log(String msg) {
        Log.d(TAG, "[" + new Date().toString() + "]" + msg);
    }

    private void setUp() {
        DecisionLog log = new DecisionLog();

        LocationUtility locationUtility = new LocationUtility(this.context);
        SensorsUtility sensorsUtility = new SensorsUtility(this.context);
        LockStateUtility lockStateUtility = new LockStateUtility(this.context);
        MotionUtility motionUtility = new MotionUtility(this.context, lockStateUtility);
        ContactsUtility contactsUtility = new ContactsUtility(this.context);
        CallsUtility callsUtility = new CallsUtility(this.context);
        SMSUtility smsUtility = new SMSUtility(this.context);
        SharedPreferencesUtility sharedPreferencesUtility = new SharedPreferencesUtility(this.context);
        Settings settings = new Settings(sharedPreferencesUtility);
        WiFiUtility wifiUtility = new WiFiUtility(this.context);
        CountryPrefix countryPrefix = new CountryPrefix(contactsUtility);

        DeviceUnlocked deviceUnlocked = this.createLoggingDeviceUnlocked(lockStateUtility, settings);
        UserRide userRide = this.createLoggingUserRide(settings, locationUtility, sensorsUtility, motionUtility, wifiUtility, log);
        NumberRules numberRules = this.createLoggingUserRules(contactsUtility, countryPrefix, settings);
        AlreadyResponded alreadyResponded = this.createLoggingAlreadyResponded(callsUtility, smsUtility);

        this.respondingDecision =
                this.createLoggingRespondingDecision(deviceUnlocked, userRide, numberRules, alreadyResponded, settings, log);

        sensorsUtility.registerSensors();
    }


    public void testRespondingDecisionInIsolation() {
        this.setUp();
        // time to turn off the screen.
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log("Starting isolated responding decision test. This test will make a responding decision from current state of the phone");
        boolean result = this.respondingDecision.shouldRespond(new CallRespondingSubject("791467855"));
        log("Result of testRespondingDecisionInIsolation()=" + result);
    }

    public void testRespondingDecisionInIsolationContinously() {
        do {
            log("\n\n\n\n========Performing next responding decision in isolation test========");
            this.testRespondingDecisionInIsolation();
        } while (true);
    }

    // region extending classes to make them loggable

    private RespondingDecision createLoggingRespondingDecision(
            DeviceUnlocked deviceUnlocked, UserRide userRide, NumberRules numberRules,
            AlreadyResponded alreadyResponded, Settings settings, DecisionLog log
    ) {
        return new RespondingDecision(userRide, numberRules, alreadyResponded, deviceUnlocked, settings, log) {
            public boolean shouldRespond(RespondingSubject subject) {
                boolean result = super.shouldRespond(subject);
                log("shouldRespond()=" + result);
                return result;
            }

            @Override
            public void cancelDecision() {
                Log.d(TAG, "RespondingDecision cancelled!");
            }
        };

    }


    private AlreadyResponded createLoggingAlreadyResponded(final CallsUtility callsUtility, final SMSUtility smsUtility) {
        return new AlreadyResponded(callsUtility, smsUtility) {
            public boolean isUserRespondedSince(Date date, String phoneNumber) {
                boolean result = super.isUserRespondedSince(date, phoneNumber);
                log("isUserRespondedSince()=" + result);
                return result;
            }

            public boolean isAutomaticalResponseLast(String phoneNumber) {
                boolean result = super.isAutomaticalResponseLast(phoneNumber);
                log("isAutomaticalResponseLast()=" + result);
                if (result == true) {
                    log("CHECK IF AUTORESPONDER DON'T RESPOND CURRENT NUMBER - IF YES, WHOLE TEST WILL NOT WORK");
                }
                return result;
            }
        };
    }


    private NumberRules createLoggingUserRules(ContactsUtility contactsUtility, CountryPrefix countryPrefix, Settings settings) {
        return new NumberRules(contactsUtility, countryPrefix, settings) {
            public boolean numberRulesAllowResponding(String phoneNumber) {
                boolean result = super.numberRulesAllowResponding(phoneNumber);
                log("numberRulesAllowResponding()=" + result);
                return result;
            }
        };
    }

    private DeviceUnlocked createLoggingDeviceUnlocked(final LockStateUtility lockStateUtility, final Settings settings) {
        return new DeviceUnlocked(settings, lockStateUtility) {
            public boolean isNotRidingBecausePhoneUnlocked() {
                boolean result = super.isNotRidingBecausePhoneUnlocked();
                log("isNotRidingBecausePhoneUnlocked()=" + result);
                return result;
            }
        };
    }

    private UserRide createLoggingUserRide(final Settings settings, final LocationUtility locationUtility, final SensorsUtility sensorsUtility, final MotionUtility motionUtility, WiFiUtility wifiUtility, DecisionLog log) {
        return new UserRide(settings, locationUtility, sensorsUtility, motionUtility, wifiUtility, log) {
            public boolean isUserRiding() {
                boolean result = super.isUserRiding();
                log("isUserRiding()=" + result);
                return result;
            }


            protected boolean isProxime() {
                boolean result = super.isProxime();
                log("isProxime()=" + result);
                return result;
            }

            public boolean isSpeedForSureRiding(float speedKmh) {
                boolean result = super.isSpeedForSureRiding(speedKmh);
                log("isSpeedForSureRiding()=" + result);
                return result;
            }

            protected boolean isWiFiConnected() {
                boolean result = super.isWiFiConnected();
                log("isWifiConnected()=" + result);
                return result;
            }

            public Float getCurrentSpeedKmh(float minimumSpeedKmh, float maximumAccuracyMeters, long timeoutMs) {
                Float result = super.getCurrentSpeedKmh(minimumSpeedKmh, maximumAccuracyMeters, timeoutMs);
                log("getCurrentSpeedKmh()=" + result);
                return result;
            }

            public boolean motionSensorReportsMovement() {
                boolean result = super.motionSensorReportsMovement();
                log("motionSensorReportsMovement()=" + result);
                return result;
            }
        };
    }

    // endregion
}
