package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.mocks.LocationUtilityMock;
import com.medziku.motoresponder.mocks.SettingsMock;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class GPSRideRecognitionTest {

    public float SPEED_ABOVE_RIDING_SPEED_MS;
    public float SPEED_BELOW_RIDING_SPEED_MS;
    public int DISTANCE_ENOUGH;
    public int DISTANCE_TOO_SMALL;
    public SettingsMock settingsMock;
    private LocationUtilityMock locationUtilityMock;
    private GPSRideRecognition gpsRideRecognition;

    @Before
    public void setUp() {
        // TODO copy it from integration tests branch
        this.locationUtilityMock = new LocationUtilityMock();
        this.settingsMock = new SettingsMock();

        float sureRidingSpeedMs = (float) (this.settingsMock.mock.getSureRidingSpeedKmh() / 3.6);
        this.SPEED_ABOVE_RIDING_SPEED_MS = sureRidingSpeedMs + 5;
        this.SPEED_BELOW_RIDING_SPEED_MS = sureRidingSpeedMs - 5;

        this.DISTANCE_ENOUGH = this.settingsMock.mock.getDistanceForTrafficJamDetectionMeters() + 100; // TODO fix it this name is incorrect; +
        this.DISTANCE_TOO_SMALL = this.settingsMock.mock.getDistanceForTrafficJamDetectionMeters() - 100;

        CustomLog log = new CustomLog(this.settingsMock.mock);
        this.gpsRideRecognition = new GPSRideRecognition(this.locationUtilityMock.mock, this.settingsMock.mock, log);
    }


    // Negative cases here:


    @Test
    public void testFirstCheckTimeouted() throws GPSNotAvailableException {
        this.locationUtilityMock.setMockLocations(new Object[]{
                null
        });

        this.expectIsUserRiding(false);
    }


    @Test
    public void testFirstCheckProviderDisabled() throws GPSNotAvailableException {
        this.locationUtilityMock.setMockLocations(new Object[]{
                new Exception("GPS not available")
        });

        this.expectThrowProviderDisabled();
    }


    @Test
    public void testDistanceCheckTimeouted() throws GPSNotAvailableException {
        this.locationUtilityMock.setMockLocations(new Object[]{
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS),
                null
        });

        this.expectIsUserRiding(false);
    }


    @Test
    public void testDistanceCheckNotEnoughSpeedAndNotEnoughDistance() throws GPSNotAvailableException {
        this.locationUtilityMock.setMockLocations(new Object[]{
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS),
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS, this.DISTANCE_TOO_SMALL),
        });

        this.expectIsUserRiding(false);
    }


    @Test
    public void testSecondCheckNotEnoughSpeed() throws GPSNotAvailableException {
        this.locationUtilityMock.setMockLocations(new Object[]{
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS),
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS, this.DISTANCE_ENOUGH),
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS)
        });

        this.expectIsUserRiding(false);
    }


    @Test
    public void testSecondCheckTimeouted() throws GPSNotAvailableException {
        this.locationUtilityMock.setMockLocations(new Object[]{
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS),
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS, this.DISTANCE_ENOUGH),
                null
        });

        this.expectIsUserRiding(false);
    }

    @Test
    public void testCancelGPSCheck() throws GPSNotAvailableException {
        this.gpsRideRecognition.cancelGPSCheck();
        verify(this.locationUtilityMock.mock, times(1)).cancelGPSCheck();
    }


    // Positive cases here:


    @Test
    public void testFirstCheckAboveSpeed() throws GPSNotAvailableException {
        this.locationUtilityMock.setMockLocations(new Object[]{
                this.locationUtilityMock.createMockLocation(this.SPEED_ABOVE_RIDING_SPEED_MS)
        });


        this.expectIsUserRiding(true);
    }


    @Test
    public void testDistanceCheckAboveSpeedDistanceEnough() throws GPSNotAvailableException {
        this.locationUtilityMock.setMockLocations(new Object[]{
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS),
                this.locationUtilityMock.createMockLocation(this.SPEED_ABOVE_RIDING_SPEED_MS, this.DISTANCE_ENOUGH),
        });

        this.expectIsUserRiding(true);
    }

    @Test
    public void testDistanceCheckAboveSpeedDistanceNotEnough() throws GPSNotAvailableException {
        // distance shouldnt matter if speed is okay.
        this.locationUtilityMock.setMockLocations(new Object[]{
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS),
                this.locationUtilityMock.createMockLocation(this.SPEED_ABOVE_RIDING_SPEED_MS, this.DISTANCE_TOO_SMALL),
        });

        this.expectIsUserRiding(true);
    }


    @Test
    public void testSecondCheckEnoughSpeed() throws GPSNotAvailableException {
        this.locationUtilityMock.setMockLocations(new Object[]{
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS),
                this.locationUtilityMock.createMockLocation(this.SPEED_BELOW_RIDING_SPEED_MS, this.DISTANCE_ENOUGH),
                this.locationUtilityMock.createMockLocation(this.SPEED_ABOVE_RIDING_SPEED_MS)
        });

        this.expectIsUserRiding(true);
    }


    private void expectIsUserRiding(boolean expectedValue) throws GPSNotAvailableException {
        assertTrue(this.gpsRideRecognition.isUserRidingByGPS() == expectedValue);
    }

    private void expectThrowProviderDisabled() {
        try {
            this.gpsRideRecognition.isUserRidingByGPS();
            fail();
        } catch (Exception e) {
            // success
        }
    }


}
