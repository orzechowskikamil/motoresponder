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
    private Settings settings;

    public RespondingDecisionIntegrationTest(Context context) {
        this.context = context;
    }

    public static void log(String msg) {
        Log.d(TAG, "[" + new Date().toString() + "]" + msg);
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
        boolean result = false;
        try {
            result = this.respondingDecision.shouldRespond(new CallRespondingSubject("791467855",new Date(), this.settings));
        } catch (Exception e) {
            log("GPS Error during testRespondingDecisionInIsolation()");
        }
        log("Result of testRespondingDecisionInIsolation()=" + result);
    }

    public void testRespondingDecisionInIsolationContinously() {
        do {
            log("\n\n\n\n========Performing next responding decision in isolation test========");
            this.testRespondingDecisionInIsolation();
        } while (true);
    }

    private void setUp() {
        SharedPreferencesUtility sharedPreferencesUtility = new SharedPreferencesUtility(this.context);
        this.settings = new Settings(sharedPreferencesUtility);

        CustomLog log = new CustomLog(settings);

        LocationUtility locationUtility = new LocationUtility(this.context);
        final SensorsUtility sensorsUtility = new SensorsUtility(this.context);
        final LockStateUtility lockStateUtility = new LockStateUtility(this.context);
        final MotionUtility motionUtility = new MotionUtility(this.context, lockStateUtility);
        ContactsUtility contactsUtility = new ContactsUtility(this.context);
        final CallsUtility callsUtility = new CallsUtility(this.context);
        final SMSUtility smsUtility = new SMSUtility(this.context);
        WiFiUtility wifiUtility = new WiFiUtility(this.context);
        CountryPrefix countryPrefix = new CountryPrefix(contactsUtility);
        final GPSRideRecognition gpsRideRecognition = new GPSRideRecognition(locationUtility, settings, log);

        DeviceUnlocked deviceUnlocked = new DeviceUnlocked(settings, lockStateUtility);
        UserRide userRide = new UserRide(settings, gpsRideRecognition, sensorsUtility, motionUtility, wifiUtility, log);
        NumberRules numberRules = new NumberRules(contactsUtility, countryPrefix, settings, log);
        CurrentAlreadyResponded alreadyResponded = new CurrentAlreadyResponded(settings, callsUtility, smsUtility);
        this.respondingDecision = new RespondingDecision(userRide, numberRules, alreadyResponded, deviceUnlocked, settings, log);
        sensorsUtility.registerSensors();
    }

    // region extending classes to make them loggable
}