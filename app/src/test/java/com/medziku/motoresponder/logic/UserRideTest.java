package com.medziku.motoresponder.logic;

import android.location.Location;
import com.google.common.util.concurrent.SettableFuture;
import com.medziku.motoresponder.utils.AccelerometerNotAvailableException;
import com.medziku.motoresponder.utils.LocationUtility;
import com.medziku.motoresponder.utils.MotionUtility;
import com.medziku.motoresponder.utils.SensorsUtility;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;


public class UserRideTest {


    private UserRide userRide;
    private LocationUtility locationUtility;
    private SensorsUtility sensorsUtility;
    private MotionUtility motionUtility;
    private double msToKmh = 3.6;
    private double FAKE_FOR_SURE_MOVING_SPEED = 70 / msToKmh;
    private int FAKE_TIMEOUT_SPEED = -1;
    private double FAKE_FOR_SURE_NOT_MOVING_SPEED = 1 / msToKmh;
    private double FAKE_UNSURE_MOVING_SPEED = 20 / msToKmh;
    public static final int DELAY_BETWEEN_UNSURE_AND_SURE_SPEED_LOCATION = 2000;

    @Before
    public void setUp() throws Exception {
        this.locationUtility = Mockito.mock(LocationUtility.class);
        this.sensorsUtility = Mockito.mock(SensorsUtility.class);
        this.motionUtility = Mockito.mock(MotionUtility.class);
        this.userRide = new UserRide(this.locationUtility, this.sensorsUtility, this.motionUtility);

        this.setIncludeProximityCheck(true);
        this.setSensorsUtilityIsProximeValue(true);

        this.setIncludeDeviceMotionCheck(true);
        this.setDeviceInMotionValue(true);

        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_FOR_SURE_MOVING_SPEED);
    }

    @Test
    public void testIfLocationTimeoutIsProperlyHandled() {
        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_TIMEOUT_SPEED);
        this.expectUserRideIsUserRidingToBe(false);

        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_FOR_SURE_MOVING_SPEED);
        this.expectUserRideIsUserRidingToBe(true);
    }

    @Test
    public void testIfLocationSureSpeedsAreProperlyHandled() {
        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_FOR_SURE_NOT_MOVING_SPEED);
        this.expectUserRideIsUserRidingToBe(false);

        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_FOR_SURE_MOVING_SPEED);
        this.expectUserRideIsUserRidingToBe(true);
    }

    @Test
    public void testIfLocationNotSureSpeedIsProperlyHandled() {
        this.testUnsureSpeedCase(true, this.FAKE_UNSURE_MOVING_SPEED, this.FAKE_FOR_SURE_MOVING_SPEED);
        this.testUnsureSpeedCase(false, this.FAKE_UNSURE_MOVING_SPEED, this.FAKE_UNSURE_MOVING_SPEED);
        this.testUnsureSpeedCase(false, this.FAKE_UNSURE_MOVING_SPEED, this.FAKE_FOR_SURE_NOT_MOVING_SPEED);
    }

    @Test
    public void testIfIncludeDeviceMotionCheckIsProperlyHandled() {
        this.setIncludeDeviceMotionCheck(true);

        this.setDeviceInMotionValue(true);
        this.expectUserRideIsUserRidingToBe(true);

        this.setDeviceInMotionValue(false);
        this.expectUserRideIsUserRidingToBe(false);

        this.setIncludeDeviceMotionCheck(false);

        this.setDeviceInMotionValue(true);
        this.expectUserRideIsUserRidingToBe(true);

        this.setDeviceInMotionValue(false);
        this.expectUserRideIsUserRidingToBe(true);


        this.setIncludeDeviceMotionCheck(true);
        this.setDeviceInMotionToException();
        this.expectUserRideIsUserRidingToBe(true);
    }

    @Test
    public void testIfProximityIsProperlyHandled() {
        this.setIncludeProximityCheck(true);

        this.setSensorsUtilityIsProximeValue(true);
        this.expectUserRideIsUserRidingToBe(true);

        this.setSensorsUtilityIsProximeValue(false);
        this.expectUserRideIsUserRidingToBe(false);

        this.setIncludeProximityCheck(false);

        this.setSensorsUtilityIsProximeValue(true);
        this.expectUserRideIsUserRidingToBe(true);

        this.setSensorsUtilityIsProximeValue(false);
        this.expectUserRideIsUserRidingToBe(true);

    }

    @Test
    public void testIsUserRiding() throws Exception {
        this.expectUserRideIsUserRidingToBe(true);

    }

    // region helper methods


    private void setSensorsUtilityIsProximeValue(boolean value) {
        try {
            Mockito.when(this.sensorsUtility.isProxime()).thenReturn(value);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    private void expectUserRideIsUserRidingToBe(boolean result) {
        Assert.assertEquals(result, this.userRide.isUserRiding());
    }

    private void setDeviceInMotionValue(boolean value) {
        SettableFuture<Boolean> result = SettableFuture.create();
        result.set(value);
        try {
            Mockito.when(this.motionUtility.isDeviceInMotion()).thenReturn(result);
        } catch (AccelerometerNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exception is thrown when screen is turned off
     */
    private void setDeviceInMotionToException() {
        try {
            Mockito.when(this.motionUtility.isDeviceInMotion()).thenThrow(AccelerometerNotAvailableException.class);
        } catch (AccelerometerNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void setIncludeDeviceMotionCheck(boolean value) {
        this.userRide.includeDeviceMotionCheck = value;
    }

    private void setIncludeProximityCheck(boolean value) {
        this.userRide.includeProximityCheck = value;
    }


    private void setLocationUtilityGetAccurateLocationSpeedResult(double value) {
        SettableFuture<Location> result = SettableFuture.create();

        Location location = Mockito.mock(Location.class);
        Mockito.when(location.getSpeed()).thenReturn((float) value);

        Location valueOfFuture = (value > -1.0) ? location : null;
        result.set(valueOfFuture);
        Mockito.when(this.locationUtility.getAccurateLocation()).thenReturn(result);
    }

    private void testUnsureSpeedCase(boolean expectedResult, double firstCheckSpeed, final double secondCheckSpeed) {
        this.userRide.maybeRidingTimeoutMs = UserRideTest.DELAY_BETWEEN_UNSURE_AND_SURE_SPEED_LOCATION * 2;
        this.setLocationUtilityGetAccurateLocationSpeedResult(firstCheckSpeed);

        (new Thread() {
            @Override
            public void run() {
                try {
                    this.sleep(UserRideTest.DELAY_BETWEEN_UNSURE_AND_SURE_SPEED_LOCATION);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                UserRideTest.this.setLocationUtilityGetAccurateLocationSpeedResult(secondCheckSpeed);
            }
        }).start();

        this.expectUserRideIsUserRidingToBe(expectedResult);
    }

    // endregion

}