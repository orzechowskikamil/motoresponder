package com.medziku.motoresponder.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.medziku.motoresponder.services.BackgroundService;

import java.util.logging.Logger;

/**
 * Created by medziku on 26.10.15.
 */
public class BootReceiver extends BroadcastReceiver {
    private static final Logger log = Logger.getLogger(BootReceiver.class.getName());


    @Override
    public void onReceive(Context context, Intent intent) {
        log.info("onReceive");
        Intent backgroundServiceStarter = new Intent(context, BackgroundService.class);
        context.startService(backgroundServiceStarter);//TODO start only if settings say so  Issue #4
    }
}
