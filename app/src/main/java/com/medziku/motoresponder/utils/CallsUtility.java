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
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private boolean isCurrentlyListening;

    public CallsUtility(Context context) {
        this.context = context;
        this.telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * You can listen for calls only once.
     * To register another callCallback you need to call method stopListeningForCalls() before.
     *
     * @param callCallback
     */
    public void listenForCalls(Predicate<String> callCallback) {
        if (this.callCallback != null) {
            throw new IllegalStateException("Utility is already listening for calls");
        }

        if (this.isCurrentlyListening == true) {
            return;
        }

        this.isCurrentlyListening = true;

        this.phoneStateListener = new PhoneStateListener() {
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
        this.telephonyManager.listen(this.phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        this.callCallback = callCallback;
    }

    public void stopListeningForCalls() {
        if (this.isCurrentlyListening == false) {
            return;
        }
        this.telephonyManager.listen(this.phoneStateListener, PhoneStateListener.LISTEN_NONE);
    }


    public boolean wasOutgoingCallAfterDate(Date date, String phoneNumber) {
        // TODO K. Orzechowski: it may still contain a flaw, since phone number sometimes is returned as
        // TODO K. Orzechowski: XXXXXXXXX, sometimes as +48XXXXXXXXX, and sometimes as 0048XXXXXXXXX.
        // TODO K. Orzechowski: verify it later. Issue #33
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
