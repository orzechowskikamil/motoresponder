package com.medziku.motoresponder.utils;

import android.content.Context;
import android.telephony.*;
import com.google.common.base.Predicate;

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
}