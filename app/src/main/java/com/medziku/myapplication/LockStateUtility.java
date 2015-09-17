package com.medziku.myapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kamil on 2015-09-16.
 */

interface LockStateCallback {
    void onChangeState(boolean isLocked);
}


public class LockStateUtility {

    private Context context;
    private List<LockStateCallback> lockStateCallbacksList;
    private boolean phoneUnlocked;

    public LockStateUtility(Context context) {
        this.context = context;
        this.lockStateCallbacksList = new ArrayList<LockStateCallback>();


        this.context.registerReceiver(new UserPresentBroadcastReceiver(),
                new IntentFilter("android.intent.action.USER_PRESENT"));

        this.context.registerReceiver(new UserPresentBroadcastReceiver(),
                new IntentFilter("android.intent.action.SCREEN_OFF"));

        // if somebody started app, phone must be unlocked at start
        this.phoneUnlocked = true;
    }

    private void listenToUnlockEvent(LockStateCallback unlockCallback) {
        this.lockStateCallbacksList.add(unlockCallback);

    }

    public boolean isPhoneUnlocked() {
        return phoneUnlocked;
    }

    private void setPhoneUnlocked(boolean phoneUnlocked) {
        this.phoneUnlocked = phoneUnlocked;
    }

    private class UserPresentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {

        /*Sent when the user is present after
         * device wakes up (e.g when the keyguard is gone)
         * */
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                for (LockStateCallback unlockCallback : LockStateUtility.this.lockStateCallbacksList) {
                    unlockCallback.onChangeState(true);
                    LockStateUtility.this.setPhoneUnlocked(true);
                    Log.d("lock state utility", "phone unlocked");
                }

            }
        /*Device is shutting down. This is broadcast when the device
         * is being shut down (completely turned off, not sleeping)
         * */
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                for (LockStateCallback unlockCallback : LockStateUtility.this.lockStateCallbacksList) {
                    unlockCallback.onChangeState(false);
                    LockStateUtility.this.setPhoneUnlocked(false);
                    Log.d("lock state utility", "phone locked");
                }
            }
        }

    }
}
