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

    private boolean phoneUnlocked;

    public LockStateUtility(Context context) {
        context.registerReceiver(new UserPresentBroadcastReceiver(),
                new IntentFilter("android.intent.action.USER_PRESENT"));

        context.registerReceiver(new UserPresentBroadcastReceiver(),
                new IntentFilter("android.intent.action.SCREEN_OFF"));

        // if somebody started app, phone must be unlocked at start
        // TODO K. Orzechowski: That can be not true if app will start with phone startup or as a service...
        this.phoneUnlocked = true;
    }

    public boolean isPhoneUnlocked() {
        return phoneUnlocked;
    } //

    private void setPhoneUnlocked(boolean phoneUnlocked) {
        this.phoneUnlocked = phoneUnlocked;
    }

    private class UserPresentBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {

            // This is why floating notification is required - because
            // app should respond always when screen is locked. Initially I thought that
            // there is no point of notification because when app works screen is always off but it is not true
            // because when screen is on but phone is not unlocked also auto respond will be sent.

            // Sent when the user is present after device wakes up (e.g when the keyguard is gone)
            if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                LockStateUtility.this.setPhoneUnlocked(true);
                Log.d("motoapp", "LockStateUtility: Phone unlocked");
            }
            // Device screen is off
            else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                LockStateUtility.this.setPhoneUnlocked(false);
                Log.d("motoapp", "LockStateUtility: Phone locked");
            }
        }

    }
}
