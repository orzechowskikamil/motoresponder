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
    private boolean alreadyListening;

    public LocationUtility(Context context, int minimumTimeBetweenUpdates, int minimumDistanceBetweenUpdates) {
        this.context = context;
        this.locationManager = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);
        this.minimumDistanceBetweenUpdates = minimumDistanceBetweenUpdates;
        this.minimumTimeBetweenUpdates = minimumTimeBetweenUpdates;
        this.alreadyListening = false;
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
     * @param callback Callback which be called when location is known (null value on timeout)
     * @throws Exception When second callback tried to be registered when another is listening
     */
    public void listenForLocationOnce(final LocationChangedCallback callback) throws Exception {
        if (this.alreadyListening == true) {
            throw new Exception("Only one listener can be registered at once");
        }

        this.alreadyListening = true;
        // TODO K. Orzechowski: maybe it will be good to move minimumDistance and minimumTime settings
        // from constructor to this method.

        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                this.minimumTimeBetweenUpdates,
                this.minimumDistanceBetweenUpdates,
                new LocationListener() {
                    public void onLocationChanged(Location loc) {
                        // TODO K. Orzechowski: magic number, fix it
                        if (loc.getAccuracy() >= 0.68) {
                            callCallbackAndUnregister(loc);
                        }
                    }

                    private void callCallbackAndUnregister(Location loc) {
                        LocationUtility.this.alreadyListening = false;
                        LocationUtility.this.locationManager.removeUpdates(this);
                        callback.onLocationChange(loc);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        if (status == LocationProvider.OUT_OF_SERVICE) {
                            this.callCallbackAndUnregister(null);
                        }
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        // TODO K. Orzechowski: probably needs to do nothing, Marcin - correct me if I am wrong
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        // TODO K. Orzechowski: probably needs to return timeout - Marcin correct me if I am wrong
                        this.callCallbackAndUnregister(null);
                    }
                });
    }
}