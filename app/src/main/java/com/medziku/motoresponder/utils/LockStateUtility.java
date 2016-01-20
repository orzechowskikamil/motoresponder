package com.medziku.motoresponder.utils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import com.google.common.base.Predicate;

/**
 * This Utility listens for changes of phone being unlocked or not and report most recent value.
 */
public class LockStateUtility {

    private Context context;

    public LockStateUtility(Context context) {
        this.context = context;
    }



    public void listenToLockStateChanges(Predicate<Boolean> lockStateChangedCallback) {
        // TODO K. Orzechowski: implement Issue #60
    }

    public void stopListeningToLockStateChanges(){
        // TODO K. Orzechowski: implement Issue #60
    }


    /**
     * If true, phone is unlocked and turned screen on, if false - not
     */
    @TargetApi(Build.VERSION_CODES.KITKAT_WATCH)
    public boolean isPhoneUnlocked() {

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        boolean isPhoneLocked = keyguardManager.inKeyguardRestrictedInputMode();

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenAwake = (Build.VERSION.SDK_INT < 20
                ? powerManager.isScreenOn()
                : powerManager.isInteractive());

        boolean phoneIsUnlocked = isScreenAwake && !isPhoneLocked;

        return phoneIsUnlocked;
    }
}
