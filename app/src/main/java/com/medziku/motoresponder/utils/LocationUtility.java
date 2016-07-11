package com.medziku.motoresponder.utils;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Looper;
import com.google.common.util.concurrent.SettableFuture;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;

/**
 * This utility allow to listen for accurate location and get response as promise.
 */
public class LocationUtility {

    private LocationManager locationManager;
    private SettableFuture<Location> locationFuture;
    private GettingAccurateLocationProcess mostRecentLocationProcess;


    public LocationUtility(Context context) {
        this.locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }


    public String getInternalLog() {
        if (this.mostRecentLocationProcess == null) {
            return null;
        }


        String logMsg = this.mostRecentLocationProcess.logMsg;
        this.mostRecentLocationProcess.logMsg = "";

        return logMsg;
    }

    /**
     * Listens for location update
     *
     * @return Future which is fullfilled when location with appropriate accuracy is known, or null if timeout/error.
     */
    public Future<Location> getAccurateLocation(float minimumExpectedSpeed, float expectedAccuracy, long timeoutMs) {
        // whole content of this method was moved to separate class GettingAccurateLocationProcess,
        // which represent process of getting location, but I didn't want to break api so this method is almost empty.
        this.mostRecentLocationProcess = new GettingAccurateLocationProcess(this.locationManager, minimumExpectedSpeed, expectedAccuracy, timeoutMs);

        this.locationFuture = this.mostRecentLocationProcess.getLocation();
        return this.locationFuture;
    }

    public Future<Location> getLastRequestedLocation() {
        return this.locationFuture;
    }

    public void cancelGPSCheck() {
        if (this.mostRecentLocationProcess != null) {
            this.mostRecentLocationProcess.cancelGPSCheck();
        }
    }
}


/**
 * This class exposes all details of getting one accurate location.
 * Must be a separate class to be testable.
 */
class GettingAccurateLocationProcess implements LocationListener {


    private final SettableFuture<Location> result;
    public int minimumTimeBetweenUpdates = 5000;
    public int minimumDistanceBetweenUpdates = 0;
    /**
     * It's way to expose information about how this class performed, to any other class without knowing about it.
     */
    public String logMsg;
    private long timeoutMs;
    private float expectedSpeed;
    private float expectedAccuracy;
    private LocationManager locationManager;
    private Looper looperForListeningThread;

    /**
     * Use one class instance per one location.
     *
     * @param locationManager
     */
    public GettingAccurateLocationProcess(LocationManager locationManager, float expectedSpeedMs, float expectedAccuracyMeters, long timeoutMs) {
        this.locationManager = locationManager;
        this.expectedSpeed = expectedSpeedMs;
        this.expectedAccuracy = expectedAccuracyMeters;
        this.timeoutMs = timeoutMs;
        this.result = SettableFuture.create();
        this.logMsg = "GPS check: ";
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
        this.logMsg += " Starting listening to GPS. ";

        // this will prevent exiting from separate thread.
        this.loopLooper();
    }


    /**
     * This is also running in separate thread
     */
    public void onTimeout() {
        this.logMsg += " Timeout happened to GPS check, we were waiting too long, and event with expected speed and accuracy not happened. ";
        this.setEmptyResultAndStopListening();
    }

    /**
     * This is also running in separate thread
     */
    @Override
    public void onLocationChanged(Location location) {
        float accuracy = location.getAccuracy();
        float speed = location.getSpeed();
        this.logMsg += "speed: " + speed + "m/s, accuracy: " + accuracy + "; ";

        if (accuracy <= this.expectedAccuracy && speed >= this.expectedSpeed) {
            this.logMsg += "Last event was with expected accuracy and speed. ";
            this.setResultAndStopListening(location);
        } else {
            this.logMsg += "Not enough speed or accuracy. ";
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
                this.logMsg += " GPS is out of service.";
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
        }, this.timeoutMs);
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

    public void cancelGPSCheck() {
        this.setEmptyResultAndStopListening();
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
