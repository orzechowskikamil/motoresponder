package com.medziku.motoresponder.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

/**
 * This Utility listens for changes of phone being unlocked or not and report most recent value.
 */
public class LockStateUtility {

    private Context context;
    private boolean phoneUnlocked;

    public LockStateUtility(Context context) {
        this.context = context;
        this.context.registerReceiver(new UserPresentBroadcastReceiver(),
                new IntentFilter("android.intent.action.USER_PRESENT"));

        this.context.registerReceiver(new UserPresentBroadcastReceiver(),
                new IntentFilter("android.intent.action.SCREEN_OFF"));

        // if somebody started app, phone must be unlocked at start
        // TODO K. Orzechowski: That can be not true if app will start with phone startup or as a service...
        this.phoneUnlocked = true;
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

            // Sent when the user is present after device wakes up (e.g when the keyguard is gone)
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                LockStateUtility.this.setPhoneUnlocked(true);
                Log.d("lock state utility", "phone unlocked");
            }
            // Device is shutting down. This is broadcast when the device is being shut down
            // (completely turned off, not sleeping)
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                LockStateUtility.this.setPhoneUnlocked(false);
                Log.d("lock state utility", "phone locked");
            }
        }

    }
}
