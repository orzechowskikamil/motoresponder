package com.medziku.motoresponder.utils.utilitiesrunner.runners;


import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.callbacks.SMSReceivedCallback;
import com.medziku.motoresponder.callbacks.SendSMSCallback;
import com.medziku.motoresponder.utils.SMSUtility;

import java.util.Date;

public class SMSUtilityRunner {

    private static final String TAG = "SMSUtilityRunner";
    private Context context;
    private SMSUtility smsUtility;
    private String TEST_PHONE_NUMBER = "791467855";
    private String TEST_PHONE_NUMBER_COUNTRY_ISO = "+48791467855";
    private String TEST_PHONE_NUMBER_SPACES = "+48 791 467 855";

    public SMSUtilityRunner(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.smsUtility = new SMSUtility(this.context);
    }

    public void testGettingDateOfLastSMSSent() {
        this.setUp();

        Log.d(TAG, "This test will return last date of sms sent to given user by device owner.");

        Date dateSent = this.smsUtility.getDateOfLastSMSSent(TEST_PHONE_NUMBER, false);

        Log.d(TAG, "Date of last sms sent by device owner to number " + TEST_PHONE_NUMBER + " is: " + dateSent.toString());


    }

    public void testListeningForSMS() {
        this.setUp();

        Log.d(TAG, "This test will print all received by utility SMS messages. Send from someone else's phone SMS to this " +
                "device and verify if it works.");

        this.smsUtility.listenForSMS(new SMSReceivedCallback() {
            @Override
            public void onSMSReceived(String phoneNumber, String message) {
                Log.d(TAG, "Phone number " + phoneNumber + " sends you a message: '" + message + "'");

            }
        });
    }

    public void testSendingSMS() {
        this.setUp();
        Log.d(TAG, "This test will sent SMS to some number. Verify if recipient received it.");


        String message = "Test message";

        try {
            Log.d(TAG, "Sending SMS to number " + TEST_PHONE_NUMBER + ", message is '" + message + "'!");
            this.smsUtility.sendSMS(TEST_PHONE_NUMBER, message, new SendSMSCallback() {
                @Override
                public void onSMSSent(String status) {
                    Log.d(TAG, "SentSMS callback is called");
                }

                @Override
                public void onSMSDelivered(String status) {
                    Log.d(TAG, "SMSDelivered callback is called");
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "Exception happened during testing of sending sms!");
        }
    }

    public void testWasOutgoingSMSSentAfterDate() {
        this.setUp();

        Log.d(TAG, "Test will check if outgoing sms sent by user was sent since yesterday to now.");

        Date dateYesterday = new Date(new Date().getTime() - 60 * 60 * 24 * 1000);
        Date date30SecondsAgo = new Date(new Date().getTime() - 30 * 1000);

        boolean resultYesterday = this.smsUtility.wasOutgoingSMSSentAfterDate(dateYesterday, TEST_PHONE_NUMBER, false);
        boolean result30SecondsAgo = this.smsUtility.wasOutgoingSMSSentAfterDate(date30SecondsAgo, TEST_PHONE_NUMBER, false);
        boolean resultYesterdayCountryIso = this.smsUtility.wasOutgoingSMSSentAfterDate(dateYesterday, TEST_PHONE_NUMBER_COUNTRY_ISO, false);
        boolean resultYesterdaySpaces= this.smsUtility.wasOutgoingSMSSentAfterDate(dateYesterday, TEST_PHONE_NUMBER_SPACES, false);

        Log.d(TAG, "Since yesterday sms to number " + TEST_PHONE_NUMBER + " was sent? = " + resultYesterday);
        Log.d(TAG, "Since 30 seconds ago sms to number " + TEST_PHONE_NUMBER + " was sent? = " + result30SecondsAgo);
        Log.d(TAG, "Since yesterday sms to number " + TEST_PHONE_NUMBER_COUNTRY_ISO + " was sent? = " + resultYesterdayCountryIso);
        Log.d(TAG, "Since yesterday sms to number " + TEST_PHONE_NUMBER_SPACES + " was sent? = " + resultYesterdaySpaces);


    }

    public void testSendingSMSAndGettingItsDate() {
        this.setUp();

        Log.d(TAG, "This test will verify if util can distingush sms sent by it's own from sms sent by user.");
        Log.d(TAG, "So, it will firstly sent sms to test number, and then try to query it from sms log.");
        Log.d(TAG, "So for this test, testSendingSMS and testGettingDateOfLastSMSSent should work firstly.");

        this.testSendingSMS();

        Date dateOfLastSMSSent = this.smsUtility.getDateOfLastSMSSent(TEST_PHONE_NUMBER, true);

        Log.d(TAG, "Date of last sms sent by our app is: " + dateOfLastSMSSent.toString() + ". Date should be close to NOW");

    }
}
