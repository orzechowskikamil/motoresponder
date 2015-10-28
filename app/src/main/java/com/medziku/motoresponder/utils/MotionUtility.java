package com.medziku.motoresponder.utils;

import android.content.Context;
import com.google.common.util.concurrent.SettableFuture;

import java.util.concurrent.Future;

/**
 * Util for getting info about device motion
 */
public class MotionUtility {

    public MotionUtility(Context context) {

    }

    public Future<Boolean> isDeviceInMotion() {
        SettableFuture<Boolean> result = SettableFuture.create();

        return result;
    }
}
