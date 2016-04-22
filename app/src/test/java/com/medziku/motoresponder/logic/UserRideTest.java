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
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.concurrent.Future;

import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.anyFloat;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class UserRideTest {


    private UserRide userRide;
    private LocationUtility locationUtility;
    private SensorsUtility sensorsUtility;
    private MotionUtility motionUtility;
    private double msToKmh = 3.6;
    private int FAKE_FOR_SURE_MOVING_SPEED = 70;
    private int FAKE_TIMEOUT_SPEED = -1;
    private double FAKE_UNSURE_MOVING_SPEED = 10 / msToKmh;

    private Settings settings;

    @Before
    public void setUp() throws Exception {
        this.locationUtility = Mockito.mock(LocationUtility.class);
        this.sensorsUtility = Mockito.mock(SensorsUtility.class);
        this.motionUtility = Mockito.mock(MotionUtility.class);
        this.settings = Mockito.mock(Settings.class);
        DecisionLog log = new DecisionLog();
        this.userRide = new UserRide(this.settings, this.locationUtility, this.sensorsUtility, this.motionUtility, log);

        this.setIncludeProximityCheck(true);
        this.setSensorsUtilityIsProximeValue(true);

        this.setIncludeDeviceMotionCheck(true);
        this.setDeviceInMotionValue(true);

        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_FOR_SURE_MOVING_SPEED);
        when(this.settings.getSureRidingSpeedKmh()).thenReturn(Math.round(this.FAKE_FOR_SURE_MOVING_SPEED));
    }

    @Test
    public void testIfSureRidingSpeed1stHitProperlyHandled() {
        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_FOR_SURE_MOVING_SPEED);

        this.expectUserRideIsUserRidingToBe(true);
    }

    @Test
    public void testIfTimeouted1stHitIsProperlyHandled() {
        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_TIMEOUT_SPEED);

        this.expectUserRideIsUserRidingToBe(false);
    }

    @Test
    public void testIfNotTimeouted1stHitThenTimeoutedIsProperlyHandled() {
        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_UNSURE_MOVING_SPEED, this.FAKE_TIMEOUT_SPEED);
        this.expectUserRideIsUserRidingToBe(false);
    }

    @Test
    public void testIfNotTimeouted1stHitThenUnsureRidingSpeedProperlyHandled() {
        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_UNSURE_MOVING_SPEED, this.FAKE_UNSURE_MOVING_SPEED);
        this.expectUserRideIsUserRidingToBe(false);
    }

    @Test
    public void testIfNotTimeouted1stHitThenSureRidingSpeedProperlyHandled() {
        this.setLocationUtilityGetAccurateLocationSpeedResult(this.FAKE_UNSURE_MOVING_SPEED, this.FAKE_FOR_SURE_MOVING_SPEED);
        this.expectUserRideIsUserRidingToBe(true);
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
        // all checks are by default true so we need to just check for true value.
        this.expectUserRideIsUserRidingToBe(true);

    }

    @Test
    public void testOfCancellation() {
        this.userRide.cancelUserRideCheck();

        verify(this.locationUtility, times(1)).cancelGPSCheck();
    }

    // region helper methods


    private void setSensorsUtilityIsProximeValue(boolean value) {
        try {
            when(this.sensorsUtility.isProxime()).thenReturn(value);
        } catch (RuntimeException e) {
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
            when(this.motionUtility.isDeviceInMotion(anyDouble())).thenReturn(result);
        } catch (AccelerometerNotAvailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exception is thrown when screen is turned off
     */
    private void setDeviceInMotionToException() {
        try {
            when(this.motionUtility.isDeviceInMotion(anyDouble())).thenThrow(AccelerometerNotAvailableException.class);
        } catch (AccelerometerNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void setIncludeDeviceMotionCheck(boolean value) {
        when(this.settings.includeDeviceMotionCheck()).thenReturn(value);
    }

    private void setIncludeProximityCheck(boolean value) {
        when(this.settings.isProximityCheckEnabled()).thenReturn(value);
    }


    private void setLocationUtilityGetAccurateLocationSpeedResult(double value) {
        SettableFuture<Location> result = SettableFuture.create();

        Location location = Mockito.mock(Location.class);
        when(location.getSpeed()).thenReturn((float) value);

        Location valueOfFuture = (value > this.FAKE_TIMEOUT_SPEED) ? location : null;
        result.set(valueOfFuture);
        when(this.locationUtility.getAccurateLocation(anyFloat(), anyFloat(), anyLong())).thenReturn(result);
    }

    private void setLocationUtilityGetAccurateLocationSpeedResult(final double firstValue, final double secondValue) {
        final boolean[] secondCall = {false};

        when(this.locationUtility.getAccurateLocation(anyFloat(), anyFloat(), anyLong())).thenAnswer(new Answer<Future<Location>>() {
            @Override
            public Future answer(InvocationOnMock invocation) throws Throwable {
                float value = (float) ((secondCall[0]) ? secondValue : firstValue);

                final SettableFuture<Location> result = SettableFuture.create();
                Location location = Mockito.mock(Location.class);
                when(location.getSpeed()).thenReturn(value);

                Location valueOfFuture = (value > UserRideTest.this.FAKE_TIMEOUT_SPEED) ? location : null;
                result.set(valueOfFuture);
                if (secondCall[0] == false) {
                    secondCall[0] = true;
                }
                return result;
            }
        });
    }


    // endregion

}
