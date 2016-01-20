package com.medziku.motoresponder.utils;


import android.content.Context;
import android.location.*;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.google.common.util.concurrent.SettableFuture;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

/**
 * This utility allow to listen for accurate location and get response as promise.
 */
public class LocationUtility {

    private LocationManager locationManager;
    /**
     * Location must be more precise than 20 meters
     */
    private double goodAccuracyMeters = 20;
    public int gettingLocationTimeout = 15 * 1000;
    public int minimumTimeBetweenUpdates = 500;
    public int minimumDistanceBetweenUpdates = 0;

    public LocationUtility(Context context) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    /**
     * Listens for location update
     *
     * @return Future which is fullfilled when location with appropriate accuracy is known, or null if timeout/error.
     */
    public Future<Location> getAccurateLocation() {
        final SettableFuture<Location> result = SettableFuture.create();

        final LocationListener listener = new LocationListener() {

            public void onLocationChanged(Location loc) {
                Log.d("motoapp", "locationChanged event, current speed is: " + loc.getSpeed() + ", in kmh it is " + loc.getSpeed() * 3.6 + ", accuracy is " + loc.getAccuracy());
                if (loc.getAccuracy() <= LocationUtility.this.goodAccuracyMeters) {
                    Log.d("motoapp", "LOCATION MEASURED");
                    LocationUtility.this.locationManager.removeUpdates(this);
                    result.set(loc);
                }
            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("motoapp", "statusChanged changed " + status);
                switch (status) {
                    case LocationProvider.AVAILABLE:
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        break;
                    case LocationProvider.OUT_OF_SERVICE:
                        LocationUtility.this.locationManager.removeUpdates(this);
                        result.set(null);
                        break;
                }
            }


            @Override
            public void onProviderEnabled(String provider) {
                Log.d("motoapp", "Location Provider enabled");
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("motoapp", "providerDisabled event " + provider);
                // TODO K. Orzechowski: FIND way to return it by one method.#Issue not needed
                LocationUtility.this.locationManager.removeUpdates(this);
                result.set(null);
            }
        };


        // this is safety timeout - if no location after desired time, it cancells location listening

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("motoapp", "Location timeout");
                LocationUtility.this.locationManager.removeUpdates(listener);
                result.set(null);
            }
        }, this.gettingLocationTimeout);


        Looper.prepare();
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                this.minimumTimeBetweenUpdates,
                this.minimumDistanceBetweenUpdates,
                listener);
        Looper.loop();
        Log.d("motoapp", "Location registered");

        return result;
    }
}
