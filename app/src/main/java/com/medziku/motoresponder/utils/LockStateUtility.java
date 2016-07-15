package com.medziku.motoresponder.utils;

import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;
import com.google.common.base.Predicate;

/**
 * This Utility listens for changes of phone being unlocked or not and report most recent value.
 */
public class LockStateUtility {

    private Context context;
    private boolean isCallbackRegistered = false;
    private BroadcastReceiver currentScreenLockReceiver;
    private BroadcastReceiver currentScreenUnlockReceiver;
    private boolean isCurrentlyLocked = false;
    private boolean isFirstEvent = true;
    private PowerManager powerManager;
    private KeyguardManager keyguardManager;

    public LockStateUtility(Context context) {
        this.context = context;
        this.powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        this.keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    }


    /**
     * Call this method for listening to lock state changes.
     * Method return state by calling callback predicate. Value in callback is true if screen is locked, and false when it is unlocked.
     * For simplicity, only one observer can listen in one time.
     */
    public void listenToLockStateChanges(final Predicate<Boolean> lockStateChangedCallback) throws IllegalArgumentException {
        if (this.isCallbackRegistered) {
            throw new IllegalArgumentException("Callback already registered");
        }
        this.isCallbackRegistered = true;

        this.currentScreenLockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                // phone locked
                if (LockStateUtility.this.isCurrentlyLocked == false || LockStateUtility.this.isFirstEvent) {
                    LockStateUtility.this.isCurrentlyLocked = true;
                    LockStateUtility.this.isFirstEvent = false;
                }
            }
        };

        this.currentScreenUnlockReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent intent) {
                // phone unlocked
                if (LockStateUtility.this.isCurrentlyLocked == true || LockStateUtility.this.isFirstEvent) {
                    LockStateUtility.this.isCurrentlyLocked = false;
                    LockStateUtility.this.isFirstEvent = false;
                    lockStateChangedCallback.apply(LockStateUtility.this.isCurrentlyLocked);
                }
            }
        };

        this.context.registerReceiver(this.currentScreenUnlockReceiver, new IntentFilter(Intent.ACTION_USER_PRESENT));
        this.context.registerReceiver(this.currentScreenLockReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    /**
     * Call this method for unsubscribing from listening to lock state changes.
     */
    public void stopListeningToLockStateChanges() {
        if (this.isCallbackRegistered == false) {
            return;
        }
        this.isCallbackRegistered = false;
        this.context.unregisterReceiver(this.currentScreenUnlockReceiver);
        this.context.unregisterReceiver(this.currentScreenLockReceiver);
    }


    /**
     * If true, phone is unlocked and turned screen on, if false - not
     * This method doesn't perform constant listening.
     */

    public boolean isPhoneUnlocked() {
        boolean isPhoneLocked = this.keyguardManager.inKeyguardRestrictedInputMode();

        boolean phoneIsUnlocked = this.isScreenAwake() && !isPhoneLocked;

        return phoneIsUnlocked;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public boolean isScreenAwake() {
        boolean isScreenAwake = (Build.VERSION.SDK_INT < 20
                ? this.powerManager.isScreenOn()
                : this.powerManager.isInteractive());

        return isScreenAwake;
    }
    
    public boolean isPowerSaveModeEnabled(){
        // normally documented api is only available on Lollipop and newer.
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && powerManager.isPowerSaveMode()){
            return true;
        }
        
        return false;
    }

    public PowerManager.WakeLock acquirePartialWakeLock() {
        return this.acquireWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Motoresponder partial wakelock");
    }

    public PowerManager.WakeLock acquireScreenAwakeWakeLock() {
        return this.acquireWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Motoresponder screen bright wakelock");
    }

    private PowerManager.WakeLock acquireWakeLock(int type, String tag) {
        PowerManager.WakeLock wakeLock = this.powerManager.newWakeLock(type, tag);

        wakeLock.acquire();

        return wakeLock;
    }

    public void releaseWakeLock(PowerManager.WakeLock mWakeLock) {
        if (mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }
}
