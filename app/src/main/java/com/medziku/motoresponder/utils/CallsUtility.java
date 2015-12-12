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
        if (this.callCallback != null) {
            throw new IllegalStateException("Utility is already listening for calls");
        }

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

    public void stopListeningForCalls() throws Exception {
        // TODO K. Orzechowski: fill me
        throw new Exception("not implemented");
    }


    public boolean isOutgoingCallAfterDate(Date date, String phoneNumber) {
        // TODO K. Orzechowski: it may still contain a flaw, since phone number sometimes is returned as
        // TODO K. Orzechowski: XXXXXXXXX, sometimes as +48XXXXXXXXX, and sometimes as 0048XXXXXXXXX.
        // TODO K. Orzechowski: verify it later.
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

}
