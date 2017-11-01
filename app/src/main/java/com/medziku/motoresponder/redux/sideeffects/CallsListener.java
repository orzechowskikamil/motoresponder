package com.medziku.motoresponder.redux.sideeffects;


import android.content.Context;
import android.os.Looper;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.medziku.motoresponder.redux.Actions;
import com.medziku.motoresponder.redux.sideeffects.base.ContextSideEffect;
import trikita.jedux.Action;


public class CallsListener extends ContextSideEffect {
    private TelephonyManager telephonyManager;
    private PhoneStateListener phoneStateListener;
    private Looper looperForListeningThread;

    @Override
    public void afterStart() {
        this.telephonyManager = (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);
        (new Thread() {
            @Override
            public void run() {
                CallsListener.this.registerPhoneStateListener();
            }
        }).start();
    }

    @Override
    protected void beforeStop() {
        this.telephonyManager.listen(this.phoneStateListener, PhoneStateListener.LISTEN_NONE);
        this.looperForListeningThread.quitSafely();
    }

    /**
     * This will run in separate thread
     */
    private void registerPhoneStateListener() {
        Looper.prepare();
        this.looperForListeningThread = Looper.myLooper();

        this.phoneStateListener = new PhoneStateListener() {
            private boolean isRinging = false;

            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (this.isRinging == true) {
                            this.isRinging = false;
                            CallsListener.this.onCallRinging(incomingNumber);
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
        };
        this.telephonyManager.listen(this.phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        this.looperForListeningThread.loop();
    }

    private void onCallRinging(String incomingNumber) {
        this.store.dispatch(new Action(Actions.Calls.INCOMING_CALL, incomingNumber));
    }

}
