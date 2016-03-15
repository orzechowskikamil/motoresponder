package com.medziku.motoresponder.utils.utilitiesrunner.runners;

import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.MotionUtility;

import java.util.concurrent.Future;

public class MotionUtilityRunner {

    private static final String TAG = "MotionUtilityRunner";

    private MotionUtility motionUtility;
    private Context context;

    public MotionUtilityRunner(Context context) {
        this.context = context;
    }


    private void setUp() {
        this.motionUtility = new MotionUtility(this.context);
    }


    public void testOfIsDeviceInMotion() {
        this.setUp();

        Future<Boolean> future = this.motionUtility.isDeviceInMotion();
        Boolean isInMotion = null;
        try {
            isInMotion = future.get();
        } catch (Exception e) {
            Log.e(TAG, "Exception during testing.");
        }

        Log.d(TAG, "Done! isDeviceInMotion()==" + isInMotion + "");
    }

}
