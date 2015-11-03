package com.medziku.motoresponder.utils;

import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;
import android.telephony.*;
import com.google.common.base.Predicate;

import java.util.Date;

public class CallsUtility {

    private Context context;
    private Predicate<String> callCallback;

    public CallsUtility(Context context) {
        this.context = context;
    }

    public void listenForCalls(Predicate<String> callCallback) {
        TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        CallsUtility.this.callCallback.apply(incomingNumber);
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        break;
                    default:
                        break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        this.callCallback = callCallback;
    }

    private boolean isOutgoingCallAfterDate(Date date, String phoneNumber) {
        String[] whichColumns = {CallLog.Calls.NUMBER};

        String selections = CallLog.Calls.DATE + " > ? AND " + CallLog.Calls.NUMBER + " = ? AND "
                + CallLog.Calls.TYPE + " = ?";

        String[] selectionArgs = {String.valueOf(date.getTime()), phoneNumber, String.valueOf(CallLog.Calls.OUTGOING_TYPE)};

        String sortOrder = CallLog.Calls.DATE + " DESC";

        Cursor cursor = this.context.getContentResolver()
                .query(CallLog.Calls.CONTENT_URI, whichColumns, selections, selectionArgs, sortOrder);

        boolean result = cursor.getCount() > 0;

        cursor.close();

        return result;
    }


//    private String getCallDetails() {
//        Context context = this.context; // TODO k.orzechowsk refactor this and remove this reference.
//
//        StringBuffer stringBuffer = new StringBuffer();
//        Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI,
//                null, null, null, CallLog.Calls.DATE + " DESC");
//
//        int number = cursor.getColumnIndex(CallLog.Calls.NUMBER);
//        int type = cursor.getColumnIndex(CallLog.Calls.TYPE);
//        int date = cursor.getColumnIndex(CallLog.Calls.DATE);
//        int duration = cursor.getColumnIndex(CallLog.Calls.DURATION);
//        while (cursor.moveToNext()) {
//            String phNumber = cursor.getString(number);
//            String callType = cursor.getString(type);
//            String callDate = cursor.getString(date);
//            Date callDayTime = new Date(Long.valueOf(callDate));
//            String callDuration = cursor.getString(duration);
//            String dir = null;
//            int dircode = Integer.parseInt(callType);
//            switch (dircode) {
//                case CallLog.Calls.OUTGOING_TYPE:
//                    dir = "OUTGOING";
//                    break;
//                case CallLog.Calls.INCOMING_TYPE:
//                    dir = "INCOMING";
//                    break;
//
//                case CallLog.Calls.MISSED_TYPE:
//                    dir = "MISSED";
//                    break;
//            }
//            stringBuffer.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
//                    + dir + " \nCall Date:--- " + callDayTime
//                    + " \nCall duration in sec :--- " + callDuration);
//            stringBuffer.append("\n----------------------------------");
//        }
//        cursor.close();
//        return stringBuffer.toString();
//    }

}
