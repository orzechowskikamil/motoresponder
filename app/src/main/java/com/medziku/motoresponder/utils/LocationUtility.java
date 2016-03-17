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


    public LocationUtility(Context context) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    /**
     * Listens for location update
     *
     * @return Future which is fullfilled when location with appropriate accuracy is known, or null if timeout/error.
     */
    public Future<Location> getAccurateLocation() {
        // whole content of this method was moved to separate class GettingAccurateLocationProcess,
        // which represent process of getting location, but I didn't want to break api so this method is almost empty.
        return new GettingAccurateLocationProcess(this.locationManager).getLocation();
    }

}


/**
 * This class exposes all details of getting one accurate location.
 * Must be a separate class to be testable.
 */
class GettingAccurateLocationProcess implements LocationListener {
    private static final String TAG = "Location";
    private final SettableFuture<Location> result;
    private LocationManager locationManager;
    private Looper looperForListeningThread;

    /**
     * Location must be more precise than 20 meters, if reported speed is not 0.0
     */
    // TODO K. Orzechowski: it's increased temporary because i get only one event  #issue #80
    public double goodAccuracyForMoving = 60;
    /**
     * Location must be more precise than 60 meters if reported speed is 0.0 , also increased
     * // TODO K. Orzechowski: because of issue #80
     */
    public double goodAccuracyForStayingStill = 80;

    public int gettingLocationTimeout = 30 * 1000;
    public int minimumTimeBetweenUpdates = 0;
    public int minimumDistanceBetweenUpdates = 0;
    /**
     * Speed smaller than this will be assumed as staying still
     */
    public double stayingStillSpeed = 1.0;

    /**
     * Use one class instance per one location.
     *
     * @param locationManager
     */
    public GettingAccurateLocationProcess(LocationManager locationManager) {
        this.locationManager = locationManager;
        this.result = SettableFuture.create();
    }


    /**
     * Use this method to get a location.
     * If you call it 2nd time, it will return first result.  So you can only get one result from this class.
     *
     * @return
     */
    public SettableFuture<Location> getLocation() {
        // this is executed in current thread.
        if (this.result.isDone()) {
            return this.result;
        }

        // and this is executed in separate thread, because, otherwise, looper.loop would block returning
        // the unresolved future, so whole application is stopped.
        // with two threads, unresolved future is returned, and then another thread is able to resolve it in
        // any time, and then application continue.

        // keep in mind that it means that all of the listener methods like onLocationChanged etc are also
        // executed in this separate thread.
        this.runWholeProcessInSeparateThread();

        // this also is executed on current thread, so method getLocation return immediately.
        return this.result;
    }


    /**
     * This is running in separate thread
     */
    protected void startListeningToLocationUpdates() {
        // prepare looper for events in separate thread.
        this.prepareLooper();

        // this is safety timeout - if no location after desired time, it cancells location listening
        this.setSafetyTimeout();
        
        this.locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                this.minimumTimeBetweenUpdates,
                this.minimumDistanceBetweenUpdates,
                this
        );

        // this will prevent exiting from separate thread.
        this.loopLooper();
    }


    /**
     * This is also running in separate thread
     */
    public void onTimeout() {
        this.setEmptyResultAndStopListening();
    }

    /**
     * This is also running in separate thread
     */
    @Override
    public void onLocationChanged(Location location) {
        float accuracy = location.getAccuracy();
        float speed = location.getSpeed();

        Log.d(TAG, "Location event, accuracy is " + accuracy + ", speed is " + speed + "");

        boolean accurateMovingEvent = accuracy <= this.goodAccuracyForMoving;
        boolean accurateStayingStillEvent = accuracy <= this.goodAccuracyForStayingStill && speed < this.stayingStillSpeed;

        if (accurateMovingEvent || accurateStayingStillEvent) {
            this.setResultAndStopListening(location);
        }
    }

    /**
     * This is also running in separate thread
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
        
        switch (status) {
            case LocationProvider.AVAILABLE:
                break;
            case LocationProvider.TEMPORARILY_UNAVAILABLE:
                break;
            case LocationProvider.OUT_OF_SERVICE:
                this.setEmptyResultAndStopListening();
                break;
        }
    }

    /**
     * This is also running in separate thread
     */
    @Override
    public void onProviderEnabled(String provider) {
    }

    /**
     * This is also running in separate thread
     */
    @Override
    public void onProviderDisabled(String provider) {
        this.setEmptyResultAndStopListening();
    }


    private void setEmptyResultAndStopListening() {
        this.setResultAndStopListening(null);
    }

    private void setResultAndStopListening(Location location) {
        this.locationManager.removeUpdates(this);
        result.set(location);
        // gently quit separate thread, because it is no longer needed
        this.quitLooper();
    }


    private void setSafetyTimeout() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // this timer's run method will execute always after given delay, no matter if future is resolved
                // or not, so we need to check it previously.
                if (!GettingAccurateLocationProcess.this.result.isDone()) {
                    GettingAccurateLocationProcess.this.onTimeout();
                }
            }
        }, this.gettingLocationTimeout);
    }


    // region wrapped static & unmockable methods


    protected void prepareLooper() {
        Looper.prepare();
        this.looperForListeningThread = Looper.myLooper();
    }

    protected void loopLooper() {
        this.looperForListeningThread.loop();
    }

    protected void quitLooper() {
        this.looperForListeningThread.quitSafely();
    }


    protected void runWholeProcessInSeparateThread() {
        (new Thread() {
            @Override
            public void run() {
                GettingAccurateLocationProcess.this.startListeningToLocationUpdates();
            }
        }).start();
    }

    // endregion
}
