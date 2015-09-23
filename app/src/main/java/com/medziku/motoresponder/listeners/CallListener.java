package com.medziku.motoresponder.listeners;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.medziku.motoresponder.callbacks.CallCallback;
import com.medziku.motoresponder.utils.CallsUtility;

/**
 * Created by medziku on 22.09.15.
 */
public class CallListener extends PhoneStateListener {//Responsible for incoming phone calls, phone state etc

    private CallsUtility callsUtility;
    public CallListener(CallsUtility callsUtility) {
        this.callsUtility = callsUtility;
    }

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {//Call state
        super.onCallStateChanged(state, incomingNumber);
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                for (CallCallback callCallback : callsUtility.getCallCallbacksList()) {
                    callCallback.onCall(incomingNumber);
                }
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                break;
            default:
                break;
        }
    }
}
