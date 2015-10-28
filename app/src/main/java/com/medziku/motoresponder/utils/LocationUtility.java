package com.medziku.motoresponder.utils;


import android.content.Context;
import android.location.*;
import android.os.Bundle;
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
    private double goodAccuracy = 0.68;

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
                // TODO K. Orzechowski: magic number, fix it
                Log.d("loc", "Location changed " + loc.getSpeed());
                if (loc.getAccuracy() >= LocationUtility.this.goodAccuracy) {
                    LocationUtility.this.locationManager.removeUpdates(this);
                    result.set(loc);
                }
            }


            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.d("loc", "Status changed " + status);
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
            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.d("loc", "Provider disabled " + provider);
                // TODO K. Orzechowski: FIND way to return it by one method.
                LocationUtility.this.locationManager.removeUpdates(this);
                result.set(null);
            }
        };


        // this is safety timeout - if no location after desired time, it cancells location listening
        int gettingLocationTimeout = 30 * 1000;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Log.d("loc", "location timeout");
                LocationUtility.this.locationManager.removeUpdates(listener);
                result.set(null);
            }
        }, gettingLocationTimeout);

        int minimumTimeBetweenUpdates = 500;
        int minimumDistanceBetweenUpdates = 0;
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                minimumTimeBetweenUpdates,
                minimumDistanceBetweenUpdates,
                listener);

        return result;
    }
}
