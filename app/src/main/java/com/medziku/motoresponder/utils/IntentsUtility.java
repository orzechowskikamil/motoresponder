package com.medziku.motoresponder.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Yeah, intents must be also wrapped inside utility, otherwise you arent' able to write unit tests...
 */
public class IntentsUtility {


    private final Context context;

    public IntentsUtility(Context context) {
        this.context = context;
    }

    public Intent createIntent(String action) {
        return new Intent(action);
    }

    public Intent createIntent(Context context, Class anClass) {
        return new Intent(context, anClass);
    }

    public IntentFilter createIntentFilter(String action) {
        return new IntentFilter(action);
    }

}