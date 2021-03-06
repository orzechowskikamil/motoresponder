package com.medziku.motoresponder.utils;

import android.content.Context;
import android.database.Cursor;
import android.os.Looper;
import android.provider.CallLog;
import android.telephony.*;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.logic.PhoneNumbersComparator;

import java.util.Date;

public class CallsUtility {

    private Context context;
    private Predicate<String> callCallback;
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private boolean isCurrentlyListening;
    private Looper looperForListeningThread;

    /**
     * This constructor is dedicated for real usage
     *
     * @param context
     */
    public CallsUtility(Context context) {
        this.context = context;
        this.telephonyManager = getTelephonyManager();
    }

    protected TelephonyManager getTelephonyManager() {
        return (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * You can listen for calls only once.
     * To register another callCallback you need to call method stopListeningForCalls() before.
     *
     * @param callCallback
     */
    public void listenForUnansweredCalls(Predicate<String> callCallback) {
        if (this.callCallback != null) {
            return;
        }

        this.callCallback = callCallback;


        (new Thread() {
            @Override
            public void run() {
                CallsUtility.this.registerPhoneStateListener();
            }
        }).start();
    }

    /**
     * This will run in separate thread
     */
    private void registerPhoneStateListener() {
        this.prepareLooper();

        this.phoneStateListener = new PhoneStateListener() {
            private boolean isRinging = false;

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (this.isRinging == true) {
                            this.isRinging = false;
                            this.callCallback(incomingNumber);
                        }
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        this.isRinging = true;
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        this.isRinging = false;
                        break;
                    default:
                        break;
                }
            }

            private void callCallback(String incomingNumber) {
                CallsUtility.this.callCallback.apply(incomingNumber);
            }
        };
        this.telephonyManager.listen(this.phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        this.loopLooper();
    }

    public void stopListeningForCalls() {
        if (this.callCallback == null) {
            return;
        }
        this.callCallback = null;

        this.telephonyManager.listen(this.phoneStateListener, PhoneStateListener.LISTEN_NONE);
        this.quitLooper();
    }


    public boolean wasOutgoingCallAfterDate(Date date, String phoneNumber) {
        String[] projection = {CallLog.Calls.NUMBER};
        String selections = CallLog.Calls.DATE + ">? AND " + CallLog.Calls.TYPE + "=?";
        String[] selectionArgs = {String.valueOf(date.getTime()), String.valueOf(CallLog.Calls.OUTGOING_TYPE)};
        String sortOrder = CallLog.Calls.DATE + " DESC";

        Cursor cursor = this.context.getContentResolver()
                .query(CallLog.Calls.CONTENT_URI, projection, selections, selectionArgs, sortOrder);

        boolean result = false;

        if (cursor.moveToFirst()) {
            do {
                String phoneNumberQuery = cursor.getString(cursor.getColumnIndex(CallLog.Calls.NUMBER));
                if (this.numbersAreEqual(phoneNumber, phoneNumberQuery)) {
                    result = true;
                    break;
                }
            } while (cursor.moveToNext());
        }

        cursor.close();

        return result;
    }

    private boolean numbersAreEqual(String phoneNumber, String phoneNumberQuery) {
        return PhoneNumbersComparator.areNumbersEqual(phoneNumber, phoneNumberQuery);

    }


    protected void prepareLooper() {
        Looper.prepare();
        this.looperForListeningThread = Looper.myLooper();
    }

    protected void loopLooper() {
        this.looperForListeningThread.loop();
    }

    protected void quitLooper() {
        this.looperForListeningThread.quitSafely();
    }

}
