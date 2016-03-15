package com.medziku.motoresponder.utils;

import android.annotation.SuppressLint;
import android.location.Location;
import android.location.LocationManager;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.Mockito.mock;


/**
 * This test verifies logic of getting accurate location in simulated environment.
 * We assume that API calls are correct here - they will be tested by instrumented test.
 */
public class GettingAccurateLocationProcessTest {

    private ExposedGettingAccurateLocationProcess locationProcess;
    private LocationManager locationManager;

    @Before
    public void setUp() throws Exception {
        this.locationManager = mock(LocationManager.class);
        this.locationProcess = new ExposedGettingAccurateLocationProcess(this.locationManager);
    }


    @Test
    public void testGettingAccurateLocationProcessFirstHit() throws ExecutionException, InterruptedException {
        Future<Location> future = this.locationProcess.getLocation();

        double acceptableAccuracy = this.locationProcess.goodAccuracyForMoving / 2;

        final Location[] locations = {this.createLocation(acceptableAccuracy)};

        this.simulateReceivingLocations(locations);


        Location location = future.get();
        Assert.assertEquals(location.getAccuracy(), (float) acceptableAccuracy);
    }


    @Test
    public void testGettingAccurateLocationProcessInaccurateAtStart() throws ExecutionException, InterruptedException {
        Future<Location> future = this.locationProcess.getLocation();

        double unacceptableAccuracy = this.locationProcess.goodAccuracyForMoving * 2;
        double acceptableAccuracy = this.locationProcess.goodAccuracyForMoving / 2;
        float movingSpeed = (float) 50.0;

        final Location[] locations = {this.createLocation(unacceptableAccuracy, movingSpeed), this.createLocation(acceptableAccuracy, movingSpeed)};

        this.simulateReceivingLocations(locations);


        Location location = future.get();
        Assert.assertEquals(location.getAccuracy(), (float) acceptableAccuracy);
    }

    @Test
    public void testGettingAccurateLocationProcessAccurateAndThenInaccurate() throws ExecutionException, InterruptedException {
        Future<Location> future = this.locationProcess.getLocation();

        double unacceptableAccuracy = this.locationProcess.goodAccuracyForMoving * 2;
        double acceptableAccuracy = this.locationProcess.goodAccuracyForMoving / 2;

        float movingSpeed = (float) 50.0;
        final Location[] locations = {this.createLocation(acceptableAccuracy, movingSpeed), this.createLocation(unacceptableAccuracy, movingSpeed)};

        this.simulateReceivingLocations(locations);


        Location location = future.get();
        Assert.assertEquals(location.getAccuracy(), (float) acceptableAccuracy);
    }

    @Test
    public void testGettingAccurateStayingLocationProcessAccurateAndThenInaccurate() throws ExecutionException, InterruptedException {
        Future<Location> future = this.locationProcess.getLocation();
        // this test must be differentiated from moving tests, because for staying still (up to 1km/h) less accuracy
        // is acceptable. And because many of the cases will be staying still at home, it's important to reduce
        // gps time, by accepting first medium-accurate location if it is close to 0

        double unacceptableAccuracy = this.locationProcess.goodAccuracyForStayingStill * 2;
        double acceptableAccuracy = this.locationProcess.goodAccuracyForStayingStill / 2;

        float speed = (float) 0.1;
        final Location[] locations = {this.createLocation(acceptableAccuracy, speed), this.createLocation(unacceptableAccuracy, speed)};

        this.simulateReceivingLocations(locations);


        Location location = future.get();
        Assert.assertEquals(location.getAccuracy(), (float) acceptableAccuracy);
    }

    @Test
    public void testGettingAccurateLocationProcessTimeout() throws ExecutionException, InterruptedException {
        Future<Location> future = this.locationProcess.getLocation();

        Location location = future.get();
        Assert.assertEquals(location, null);
    }

    @Test
    public void testGettingAccurateLocationUnRegisteringAfterListening() throws ExecutionException, InterruptedException {
        Future<Location> future = this.locationProcess.getLocation();

        Location location = future.get();
        Assert.assertEquals(location, null);

        Mockito.verify(this.locationManager, Mockito.times(1)).removeUpdates(this.locationProcess);
    }


    private Location createLocation(double accuracy) {
        Location loc = new TestLocation(LocationManager.GPS_PROVIDER);
        loc.setAccuracy((float) accuracy);
        loc.setLatitude(10.0);
        loc.setLongitude(10.0);
        return loc;
    }

    private Location createLocation(double accuracy, float speed) {
        Location location = this.createLocation(accuracy);
        location.setSpeed(speed);
        return location;
    }


    private void simulateReceivingLocations(final Location[] locations) {
        (new Thread() {
            @Override
            public void run() {
                GettingAccurateLocationProcess process = GettingAccurateLocationProcessTest.this.locationProcess;

                for (Location location : locations) {
                    process.onLocationChanged(location);
                }
            }
        }).start();
    }
}

@SuppressLint("ParcelCreator")
class TestLocation extends Location {
    private float accuracy;
    private double latitude;
    private double longitude;
    private float speed;

    @Override
    public float getSpeed() {
        return this.speed;
    }

    @Override
    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public TestLocation(String provider) {
        super(provider);
    }

    @Override
    public double getLatitude() {
        return this.latitude;
    }

    @Override
    public float getAccuracy() {
        return this.accuracy;
    }

    @Override
    public double getLongitude() {
        return this.longitude;
    }

    @Override
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }


    @Override
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

/**
 * Class of process of getting location, which emptied methods which are not necessary
 * for unit test, and which are dependent on Android APIs
 */
class ExposedGettingAccurateLocationProcess extends GettingAccurateLocationProcess {

    public ExposedGettingAccurateLocationProcess(LocationManager locationManager) {
        super(locationManager);
    }

    @Override
    protected void log(String msg) {
    }

    @Override
    protected void loopLooper() {
    }

    @Override
    protected void quitLooper() {
    }

    @Override
    protected void runWholeProcessInSeparateThread() {
        this.startListeningToLocationUpdates();
    }

    @Override
    protected void prepareLooper() {
    }
}
