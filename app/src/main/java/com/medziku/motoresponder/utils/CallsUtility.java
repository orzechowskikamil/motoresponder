package com.medziku.motoresponder.utils;

import android.content.Context;
import android.telephony.*;

import com.medziku.motoresponder.callbacks.CallCallback;
import com.medziku.motoresponder.callbacks.CellStateCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2015-09-08.
 */

@Deprecated
public class CallsUtility {

    private Context context;
    private List<CallCallback> callCallbacksList;

    public CallsUtility(Context context) {
        // TODO: 2015-09-08 add more callbacks!
        //medziku: no more callbacks!
        this.context = context;
        this.callCallbacksList = new ArrayList<CallCallback>();
    }

    public void listenForCalls(CallCallback callCallback) {
        TelephonyManager telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        MyCallListener phoneStateListener = new MyCallListener();
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        this.callCallbacksList.add(callCallback);

    }

    private class MyCallListener extends PhoneStateListener {//Responsible for incoming phone calls, phone state etc
        public String TAG = MyCallListener.class.getName();

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {//Call state
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    for (CallCallback callCallback : CallsUtility.this.callCallbacksList) {
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

    public List<CallCallback> getCallCallbacksList() {
        return callCallbacksList;
    }
}