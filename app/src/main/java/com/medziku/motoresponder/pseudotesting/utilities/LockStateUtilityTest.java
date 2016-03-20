package com.medziku.motoresponder.pseudotesting.utilities;

import android.content.Context;
import android.util.Log;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.utils.LockStateUtility;

public class LockStateUtilityTest {

    private static final String TAG = "LockStateUtilityTest";
    private Context context;
    private LockStateUtility lockStateUtility;

    public LockStateUtilityTest(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.lockStateUtility = new LockStateUtility(this.context);
    }


    public void testOfIsLocked() {
        this.setUp();

        Log.d(TAG, "Starting testOfIsLocked");
        Log.d(TAG, "This test will ask for current status method isPhoneUnlocked()");

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        boolean isPhoneUnlocked = this.lockStateUtility.isPhoneUnlocked();
        Log.d(TAG, "Done! isPhoneUnlocked()==" + isPhoneUnlocked + "");
    }

    public void testOfListeningToLockStateChanges() {
        this.setUp();

        Log.d(TAG, "Starting listenToLockStateChanges");
        Log.d(TAG, "This test will show all changes of current lockState changes.");

        try {
            this.lockStateUtility.listenToLockStateChanges(new Predicate<Boolean>() {
                @Override
                public boolean apply(Boolean isLocked) {
                    Log.d(TAG, "IsLocked==" + isLocked);
                    return false;
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Failed! listenToLockStateChanges throwed exception");
        }

    }

    // TODO K. Orzechowski: add test of stopping listening to changes.

}
