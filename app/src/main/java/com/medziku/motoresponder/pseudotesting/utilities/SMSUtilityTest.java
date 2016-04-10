package com.medziku.motoresponder.pseudotesting.utilities;


import android.content.Context;
import android.util.Log;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.SMSObject;
import com.medziku.motoresponder.utils.SMSUtility;

import java.util.Date;

public class SMSUtilityTest {

    private static final String TAG = "SMSUtilityTest";
    private Context context;
    private SMSUtility smsUtility;
    private String TEST_PHONE_NUMBER = "791467855";
    private String TEST_PHONE_NUMBER_COUNTRY_ISO = "+48791467855";
    private String TEST_PHONE_NUMBER_SPACES = "+48 791 467 855";
    private String TEST_PHONE_NUMBER_ZERO_ZERO = "+48 791 467 855";
    private String TEST_PHONE_NUMBER_CODE = "48 791 467 855";

    public SMSUtilityTest(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.smsUtility = new SMSUtility(this.context);
    }

    public void testGettingDateOfLastSMSSent() {
        this.setUp();

        Log.d(TAG, "This test will return last date of sms sent to given user by device owner. Dates should be the same.");
        this.testGettingDateOfLastSmsSentCase(TEST_PHONE_NUMBER);
        this.testGettingDateOfLastSmsSentCase(TEST_PHONE_NUMBER_COUNTRY_ISO);
        this.testGettingDateOfLastSmsSentCase(TEST_PHONE_NUMBER_SPACES);
        this.testGettingDateOfLastSmsSentCase(TEST_PHONE_NUMBER_ZERO_ZERO);
        this.testGettingDateOfLastSmsSentCase(TEST_PHONE_NUMBER_CODE);

    }

    private void testGettingDateOfLastSmsSentCase(String phoneNumber) {
        Date date = this.smsUtility.getDateOfLastSMSSent(phoneNumber, false);
        Log.d(TAG, "Date of last sms sent by device owner to number " + phoneNumber + " is: " + ((date == null) ? "null" : date.toString()));
    }

    public void testListeningForSMS() {
        this.setUp();

        Log.d(TAG, "This test will print all received by utility SMS messages. Send from someone else's phone SMS to this " +
                "device and verify if it works.");

        this.smsUtility.listenForSMS(new Predicate<SMSObject>() {
            @Override
            public boolean apply(SMSObject sms) {
                Log.d(TAG, "Phone number " + sms.phoneNumber + " sends you a message: '" + sms.message + "'");
                return true;
            }
        });
    }

    public void testSendingSMS() {
        this.setUp();
        Log.d(TAG, "This test will sent SMS to some number. Verify if recipient received it.");


        String message = "Test message which must be very long to correctly verify if phone is correctly sending" +
                "multipart messages. For make sure that it exceed 160 limit: some more chars. I love motorcycles. " +
                "Motorcycles are the best. Every day I am thinking about riding. Open throttle and escape away!";

        try {
            Log.d(TAG, "Sending SMS to number " + TEST_PHONE_NUMBER + ", message is '" + message + "'!");
            this.smsUtility.sendSMS(TEST_PHONE_NUMBER, message, new Predicate<String>() {
                @Override
                public boolean apply(String status) {
                    Log.d(TAG, "SentSMS callback is called with status: " + status);
                    return false;
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

        this.testcaseOutgoingSMSAfterDate(dateYesterday, TEST_PHONE_NUMBER);
        this.testcaseOutgoingSMSAfterDate(dateYesterday, TEST_PHONE_NUMBER_COUNTRY_ISO);
        this.testcaseOutgoingSMSAfterDate(dateYesterday, TEST_PHONE_NUMBER_SPACES);
        this.testcaseOutgoingSMSAfterDate(dateYesterday, TEST_PHONE_NUMBER_CODE);
        this.testcaseOutgoingSMSAfterDate(dateYesterday, TEST_PHONE_NUMBER_ZERO_ZERO);
        this.testcaseOutgoingSMSAfterDate(date30SecondsAgo, TEST_PHONE_NUMBER);

        Log.d(TAG, "Yesterday date should be true (all), 30 seconds ago should be false");
    }

    private boolean testcaseOutgoingSMSAfterDate(Date date, String mobileNumber) {
        boolean result = this.smsUtility.wasOutgoingSMSSentAfterDate(date, mobileNumber, false);

        Log.d(TAG, "Since " + date.toString() + " sms to number " + mobileNumber + " was sent? = " + result);
        return result;
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
