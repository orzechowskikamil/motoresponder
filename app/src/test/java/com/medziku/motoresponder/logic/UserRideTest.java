package com.medziku.motoresponder.logic;

import com.google.common.util.concurrent.SettableFuture;
import com.medziku.motoresponder.utils.AccelerometerNotAvailableException;
import com.medziku.motoresponder.utils.MotionUtility;
import com.medziku.motoresponder.utils.SensorsUtility;
import com.medziku.motoresponder.utils.WiFiUtility;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static junit.framework.Assert.fail;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Mockito.*;


public class UserRideTest {


    private UserRide userRide;
    private SensorsUtility sensorsUtility;
    private MotionUtility motionUtility;
    private Settings settings;
    private WiFiUtility wifiUtility;


    private double msToKmh = 3.6;
    private int FAKE_FOR_SURE_MOVING_SPEED = 70;
    private GPSRideRecognition gpsRideRecognition;

    @Before
    public void setUp() throws Exception {
        this.gpsRideRecognition = Mockito.mock(GPSRideRecognition.class);
        this.sensorsUtility = Mockito.mock(SensorsUtility.class);
        this.motionUtility = Mockito.mock(MotionUtility.class);
        this.wifiUtility = Mockito.mock(WiFiUtility.class);
        this.settings = Mockito.mock(Settings.class);
        CustomLog log = new CustomLog(this.settings);
        this.userRide = new UserRide(this.settings, this.gpsRideRecognition, this.sensorsUtility, this.motionUtility, this.wifiUtility, log);

        this.setIncludeProximityCheck(true);
        this.setSensorsUtilityIsProximeValue(true);


        this.setIncludeDeviceMotionCheck(true);
        this.setDeviceInMotionValue(true);

        when(this.gpsRideRecognition.isUserRidingByGPS()).thenReturn(true);


        when(this.settings.getSureRidingSpeedKmh()).thenReturn(Math.round(this.FAKE_FOR_SURE_MOVING_SPEED));
        when(this.settings.isWiFiCheckEnabled()).thenReturn(true);
        when(this.wifiUtility.isWifiConnected()).thenReturn(false);
    }


    @Test
    public void throwErrorOnUnsupportedGPS() {
        try {
            when(this.gpsRideRecognition.isUserRidingByGPS()).thenThrow(GPSNotAvailableException.class);
            this.userRide.isUserRiding();
            fail();
        } catch (GPSNotAvailableException e) {
            // success
        }
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
    public void testIfGpsNotRidingIsProperlyHandled() throws GPSNotAvailableException {
        when(this.gpsRideRecognition.isUserRidingByGPS()).thenReturn(false);
        this.expectUserRideIsUserRidingToBe(false);
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

        verify(this.gpsRideRecognition, times(1)).cancelGPSCheck();
    }

    @Test
    public void testOfWifi() {
        // not riding - when wifi connected.
        when(this.wifiUtility.isWifiConnected()).thenReturn(true);
        this.expectUserRideIsUserRidingToBe(false);

        // when check disabled, it doesn't matter.
        when(this.settings.isWiFiCheckEnabled()).thenReturn(false);
        this.expectUserRideIsUserRidingToBe(true);
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
        try {
            Assert.assertEquals(result, this.userRide.isUserRiding());
        } catch (GPSNotAvailableException e) {
            fail();
        }
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
// endregion

}
