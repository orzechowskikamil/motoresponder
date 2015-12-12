package com.medziku.motoresponder.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.medziku.motoresponder.Responder;


/**
 * Works in background and serves responses to unanswered calls and messages.
 */
public class BackgroundService extends Service {


    private Responder responder;


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
        //TODO powinna byc takze mozliwosc wyrejestrowania w on command started dla konkretnej komendy
//        this.sensorsUtility.unregisterSensors();
        Log.d("BackgroundService", "destroed");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("BackgroundService", "created");

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
