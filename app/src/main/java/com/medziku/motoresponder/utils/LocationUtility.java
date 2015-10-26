package com.medziku.motoresponder.utils;


import android.content.Context;
import android.location.*;
import android.os.Bundle;

import com.medziku.motoresponder.callbacks.LocationChangedCallback;

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

    public LocationUtility(Context context, int minimumTimeBetweenUpdates, int minimumDistanceBetweenUpdates) {
        this.context = context;
        this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        this.minimumDistanceBetweenUpdates = minimumDistanceBetweenUpdates;
        this.minimumTimeBetweenUpdates = minimumTimeBetweenUpdates;
    }


    /**
     * Initiate location utility with 5000ms as time between location updates and 10m minimum distance
     * between location updates.
     */
    public LocationUtility(Context context) {
        this(context, 5000, 10);
    }

    /**
     * Listens for location (only one listener per one time)
     *
     * @throws Exception When second callback tried to be registered when another is listening
     */
    public Future<Location> listenForLocationOnce() throws Exception {
        // TODO K. Orzechowski: maybe it will be good to move minimumDistance and minimumTime settings
        // from constructor to this method.
        final Location[] tempLocation = new Location[1];
        
        Future<> result = new FutureTask<Location>(new Callable<Location>(){
                public Location call(){
                    return tempLocation[0];
                }
            });

        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                this.minimumTimeBetweenUpdates,
                this.minimumDistanceBetweenUpdates,
                new LocationListener() {
                    public void onLocationChanged(Location loc) {
                        // TODO K. Orzechowski: magic number, fix it
                        if (loc.getAccuracy() >= 0.68) {
                            tempLocation[0] = loc;
                            result.run();
                        }
                    }


                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        if (status == LocationProvider.OUT_OF_SERVICE) {
                            result.run();
                        }
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // TODO K. Orzechowski: probably needs to do nothing, Marcin - correct me if I am wrong
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        // TODO K. Orzechowski: probably needs to return timeout - Marcin correct me if I am wrong
                        result.run();
                    }
                });
        return result;
    }
}
