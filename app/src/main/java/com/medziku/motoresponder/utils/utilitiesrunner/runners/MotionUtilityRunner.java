package com.medziku.motoresponder.utils.utilitiesrunner.runners;

import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.AccelerometerNotAvailableException;
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

        Future<Boolean> future = null;
        try {
            future = this.motionUtility.isDeviceInMotion();
        } catch (AccelerometerNotAvailableException e) {
            Log.d(TAG, "Device screen is turned off, no possibility to grab events.");
        }
        Boolean isInMotion = null;
        try {
            isInMotion = future.get();
        } catch (Exception e) {
            Log.e(TAG, "Exception during testing.");
        }

        Log.d(TAG, "Done! isDeviceInMotion()==" + isInMotion + "");
    }

}
