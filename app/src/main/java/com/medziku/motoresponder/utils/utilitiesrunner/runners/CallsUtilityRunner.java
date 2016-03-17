package com.medziku.motoresponder.utils.utilitiesrunner.runners;


import android.content.Context;
import android.util.Log;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.CallsUtility;

import java.util.Date;

public class CallsUtilityRunner {
    private static final String TAG = "CallsUtilityRunner";

    public static final String PHONE_NUMBER_NEVER_CALLED = "777777777";
    public static final String PHONE_NUMBER_CALLED_NO_LATER_THAN_ONE_DAY_AGO = "791467855";
    /**
     * It's great and cheap shooting range in Warsaw BTW. You can use here Glock pistols, TT, WALTHERs, or
     * if you have luck, AK-47.
     */
    private static final String PHONE_NUMBER_CALLED_MORE_THAN_ONE_DAY_AGO = "22 834 41 08";

    private Context context;
    private CallsUtility callsUtility;


    public CallsUtilityRunner(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.callsUtility = new CallsUtility(this.context);
    }

    public void testWasOutgoingCallAfterDate() {
        this.setUp();

        Date dateOneDayAgo = new Date(new Date().getTime() - 1000 * 60 * 60 * 24);
        Date date30SecondsAgo = new Date(new Date().getTime() - 30 * 1000);

        Log.d(TAG, "Choose phone number which you called no later than one day ago in setup of the test.");
        Log.d(TAG, "For today this number is... " + PHONE_NUMBER_CALLED_NO_LATER_THAN_ONE_DAY_AGO + "");
        Log.d(TAG, "And choose number which was called long long time ago, but not in one day ago.");
        Log.d(TAG, "For today this number is..." + PHONE_NUMBER_CALLED_MORE_THAN_ONE_DAY_AGO);
        Log.d(TAG, "And choose number which was never called.");
        Log.d(TAG, "For today this number is... " + PHONE_NUMBER_NEVER_CALLED);

        Log.d(TAG, "Results of test:");

        boolean resultNoLaterOneDayAgo = this.callsUtility.wasOutgoingCallAfterDate(dateOneDayAgo, PHONE_NUMBER_CALLED_NO_LATER_THAN_ONE_DAY_AGO);
        Log.d(TAG, "wasOutgoingCallAfterDate(oneDayAgo, " + PHONE_NUMBER_CALLED_NO_LATER_THAN_ONE_DAY_AGO + ")=" +
                resultNoLaterOneDayAgo + ", should be = true");

        boolean result30SecondsAgo = this.callsUtility.wasOutgoingCallAfterDate(date30SecondsAgo, PHONE_NUMBER_CALLED_NO_LATER_THAN_ONE_DAY_AGO);
        Log.d(TAG, "wasOutgoingCallAfterDate(30secondsAgo, " + PHONE_NUMBER_CALLED_NO_LATER_THAN_ONE_DAY_AGO + ")=" +
                result30SecondsAgo + ", should be = false (impossible to make so quick call)");

        boolean resultLaterDayAgo = this.callsUtility.wasOutgoingCallAfterDate(dateOneDayAgo, PHONE_NUMBER_CALLED_MORE_THAN_ONE_DAY_AGO);
        Log.d(TAG, "wasOutgoingCallAfterDate(oneDayAgo, " + PHONE_NUMBER_CALLED_MORE_THAN_ONE_DAY_AGO + ")=" +
                resultLaterDayAgo + ", should be = false");

        boolean resultNeverCalled = this.callsUtility.wasOutgoingCallAfterDate(dateOneDayAgo, PHONE_NUMBER_NEVER_CALLED);
        Log.d(TAG, "wasOutgoingCallAfterDate(oneDayAgo, " + PHONE_NUMBER_NEVER_CALLED + ")=" + resultNeverCalled + ", should be = false");

        Log.d(TAG, "Overall result: " + (resultNoLaterOneDayAgo && !resultLaterDayAgo && !resultNeverCalled));
    }


    /**
     * This test test if callback is firing only for unanswered calls.
     */
    public void testListenForUnansweredCalls() {
        this.setUp();
        Log.d(TAG, "Call your cell phone from someone's else phone and verify if callback is firing by examining the console.");


        this.callsUtility.listenForUnansweredCalls(new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                Log.d(TAG, "Someone called you with number " + input + " and you didn't answer call");
                return false;
            }
        });
    }

    public void testStopListeningForCalls() {
        this.setUp();
        Log.d(TAG, "Call your cell phone from someone's else phone and verify if callback is firing by examining the console.");
        Log.d(TAG, "IN THIS TEST CALLBACK SHOULDN'T BE FIRED - IF IT IS - IT MEANS METHOD BROKEN.");


        this.callsUtility.listenForUnansweredCalls(new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                Log.d(TAG, "Someone called you with number: " + input);
                return false;
            }
        });

        this.callsUtility.stopListeningForCalls();

    }
}
