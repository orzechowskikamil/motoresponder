package com.medziku.motoresponder.utils;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.SmallTest;
import com.google.common.base.Predicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
@SmallTest
public class SMSUtilityTest {

    String CURRENT_NUMBER = "791467855";
    private ExposedSMSUtility smsUtility;

    @Before
    public void setUp() {
        this.smsUtility = new ExposedSMSUtility(InstrumentationRegistry.getContext());
        if (this.CURRENT_NUMBER == null) {
            throw new RuntimeException("Current number must be the same as on device to fullfill all tests");
        }
    }

    @Test
    public void testReadingFromSMSLog() {
    }

    @Test
    public void testOfSendingAndListeningForSMS() throws InterruptedException {
        final SMSObject[] receivedSMS = {null};
        final boolean[] smsSentCallbackFired = {false};
        boolean smsReceivedCallbackFired = false;
        int MAX_DURATION_MS = 15000;
        int timeSpentMs = 0;

        this.smsUtility.listenForSMS(new Predicate<SMSObject>() {
            @Override
            public boolean apply(SMSObject sms) {
                receivedSMS[0] = sms;
                return false;
            }
        });

        String MESSAGE = "DDD";
        this.smsUtility.sendSMS(CURRENT_NUMBER, MESSAGE, new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                smsSentCallbackFired[0] = true;
                return false;
            }

        });

        do {
            Thread.sleep(500);
            timeSpentMs += 500;

        } while (!(smsSentCallbackFired[0] && smsReceivedCallbackFired) && timeSpentMs < MAX_DURATION_MS);

        assertTrue(smsSentCallbackFired[0]);
        assertTrue(smsReceivedCallbackFired);
        assertTrue(receivedSMS[0].message.equals(MESSAGE));
        assertTrue(receivedSMS[0].phoneNumber.equals(CURRENT_NUMBER));
    }


//    @Test
//    public void testGetDateOfLastSmsSent() {
//        // it's hard to find appropriate data on device, so this test will use mocked data
//
//        this.smsUtility.contentProvider = new VirtualDatabase(new String[] () {
//            "id", "creator"
//        },new String[][] () {
//            new String[] () {
//                "aa", "bb"
//            },
//            new String[] () {
//                "aa", "bb"
//            }
//        });
//
//        Date date = this.smsUtility.getDateOfLastSmsSent();
//
//        assertTrue(date.getTime() > new Date().getTime());
//    }


}

class ExposedSMSUtility extends SMSUtility {


    public ExposedSMSUtility(Context context) {
        super(context);
    }
//    public VirtualDatabase contentProvider;

//    public Cursor query(whichColumns, selections, selectionArgs, sortOrder) {
//        if (db == null) {
//            super(whichColumns, selections, selectionArgs, sortOrder);
//        } else {
//            contentProvider.query(whichColumns, selections, selectionArgs, sortOrder);
//        }
//    }
}
