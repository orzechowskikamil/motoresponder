package com.medziku.motoresponder.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.medziku.motoresponder.logic.Responder;


/**
 * Works in background and serves responses to unanswered calls and messages.
 */
public class BackgroundService extends Service {

    private Responder responder;
    private String TAG = "BackgroundService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        this.responder.stopResponding();
        super.onDestroy();
        //TODO powinna byc takze mozliwosc wyrejestrowania w on command started dla konkretnej komendy Issue #59
//        this.sensorsUtility.unregisterSensors();
        Log.d(TAG, "destroyed");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "created");

        this.responder = new Responder(this);
        this.responder.startResponding();
    }




    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }

}
