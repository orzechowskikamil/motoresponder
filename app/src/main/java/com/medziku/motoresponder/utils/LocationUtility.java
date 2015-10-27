package com.medziku.motoresponder.utils;


import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.util.Log;
import com.google.common.util.concurrent.SettableFuture;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * This utility allow to listen for location once and get linear response instead of cyclic
 * notifications like LocationListener serves.
 * NOTE: It allow you only to listen once per one time (simpler to implement and for now no need to do multiply
 * listening).
 * TODO K. Orzechowski: maybe it should be not LocationUtility but object designed for handling one listening.
 */
public class LocationUtility {

    private Context context;
    private LocationManager locationManager;
    private int minimumTimeBetweenUpdates;
    private int minimumDistanceBetweenUpdates;
    // 30 seconds is enough...
    private int gettingLocationTimeout = 30 * 1000;

    public LocationUtility(Context context, int minimumTimeBetweenUpdates, int minimumDistanceBetweenUpdates) {
        this.context = context;
        this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        this.minimumDistanceBetweenUpdates = minimumDistanceBetweenUpdates;
        this.minimumTimeBetweenUpdates = minimumTimeBetweenUpdates;
    }


    /**
     * Initiate location utility with 100ms as time between location updates and 0m minimum distance
     * between location updates.
     */
    public LocationUtility(Context context) {
        this(context, 100, 0);
    }

    /**
     * Listens for location update
     *
     * @return Future which is fullfilled when location with appropriate accuracy is known, or null if timeout/error.
     */
    public Future<Location> getAccurateLocation() {
        // TODO K. Orzechowski: maybe it will be good to move minimumDistance and minimumTime settings
        // from constructor to this method.
 
        final SettableFuture<Location> result = SettableFuture.create();

        final LocationListener listener = new LocationListener() {
            
            private float goodAccuracy = 0.68;
            
            public void onLocationChanged(Location loc) {
                // TODO K. Orzechowski: magic number, fix it
                Log.d("loc", "Location changed " + loc.getSpeed());
                if (loc.getAccuracy() >= this.goodAccuracy) {
                    listener.setFutureAndUnregister(loc);
                }
            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("loc", "Status changed " + status);
                if (status == LocationProvider.OUT_OF_SERVICE) {
                     listener.setFutureAndUnregister(null);
                }
//                        case LocationProvider.AVAILABLE:
//
//                        case LocationProvider.OUT_OF_SERVICE:
//
//                        case LocationProvider.TEMPORARILY_UNAVAILABLE:

            }

            // TODO k.orzechowsk resolve future also on timeout for example 10 000 ms

            @Override
            public void onProviderEnabled(String provider) {
                // TODO K. Orzechowski: probably needs to do nothing, Marcin - correct me if I am wrong
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("loc", "Provider disabled " + provider);
                // TODO K. Orzechowski: probably needs to return timeout - Marcin correct me if I am wrong
                listener.setFutureAndUnregister(null);
            }
            
            public void setFutureAndUnregister(Location location){
                LocationUtility.this.locationManager.removeUpdates(this);
                result.set(location);
            }
        };


        // this is safety timeout - if no location after desired time, it cancells location listening
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                listener.setFutureAndUnregister(null);
                Log.d("loc", "location timeout");
            }
        }, gettingLocationTimeout);

        // TODO K. Orzechowski: add unregistering to all sets


        this.locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            this.minimumTimeBetweenUpdates,
            this.minimumDistanceBetweenUpdates,
            listener);
    
        return result;
    }
}
