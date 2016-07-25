package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.AccelerometerNotAvailableException;
import com.medziku.motoresponder.utils.MotionUtility;
import com.medziku.motoresponder.utils.SensorsUtility;
import com.medziku.motoresponder.utils.WiFiUtility;

import java.util.concurrent.ExecutionException;


/**
 * This class represent if user ride or not.
 */
public class UserRide {

    private WiFiUtility wiFiUtility;
    private Settings settings;
    private CustomLog log;
    private SensorsUtility sensorsUtility;
    private MotionUtility motionUtility;
    private GPSRideRecognition gpsRideRecognition;


    public UserRide(Settings settings,
                    GPSRideRecognition gpsRideRecognition,
                    SensorsUtility sensorsUtility,
                    MotionUtility motionUtility,
                    WiFiUtility wifiUtility,
                    CustomLog log) {
        this.gpsRideRecognition = gpsRideRecognition;
        this.sensorsUtility = sensorsUtility;
        this.motionUtility = motionUtility;
        this.wiFiUtility = wifiUtility;
        this.settings = settings;
        this.log = log;
    }


    public boolean isUserRiding() throws GPSNotAvailableException {
        boolean USER_IS_RIDING = true;
        boolean USER_IS_NOT_RIDING = false;
        this.log.add("Trying to measure if user riding, by checking conditions in which for sure he is not riding.");

        // if rider rides, phone should be in pocket (ofc if somebody use phone during ride outside pocket, he should
        // disable this option).
        // in pocket is proxime (to leg or chest)... If there is no proximity, he is not riding.
        if (this.settings.isProximityCheckEnabled() && !this.isProxime()) {
            this.log.add("Device screen is not near something. User not riding.");
            return USER_IS_NOT_RIDING;
        }

        // if user ride, he shouldn't have wifi connected. wifi can be connected only in home, work or car or tram.
        if (this.settings.isWiFiCheckEnabled() && this.isWiFiConnected()) {
            this.log.add("Wifi connected - you are in home, not riding.");
            return USER_IS_NOT_RIDING;
        }

        // if phone doesn't report any movement we can also assume that user is not riding motorcycle
        try {
            if (this.settings.includeDeviceMotionCheck() && !this.motionSensorReportsMovement()) {
                this.log.add("Device's accelerometer doesn't report movement. User not riding.");
                return USER_IS_NOT_RIDING;
            }
        } catch (AccelerometerNotAvailableException e) {
            // do nothing - it's normal situation if accelerometer not available, continue.
        }

        if (!this.gpsRideRecognition.isUserRidingByGPS()) {
            return USER_IS_NOT_RIDING;
        }


        // all conditions when we are sure that user is not riding are not met - so user is riding.
        this.log.add("All conditions met - user is riding.");
        return USER_IS_RIDING;
    }

    public void cancelUserRideCheck() {
        this.gpsRideRecognition.cancelGPSCheck();
    }

    protected boolean isWiFiConnected() {
        return this.wiFiUtility.isWifiConnected();
    }

    protected boolean motionSensorReportsMovement() throws AccelerometerNotAvailableException {
        try {
            Boolean result = this.motionUtility.isDeviceInMotion(this.settings.getAccelerationRequiredToMotion()).get();

            this.log.add(this.motionUtility.getAndClearInternalLog());

            if (result == null) {
                // null means that something disturbed accelerometer during process, and it's equal to exception thrown situation.
                throw new AccelerometerNotAvailableException();
            }

            return result;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // TODO K. Orzechowski: using here also gyroscope and magneometer is not a bad idea
        // maybe other method will be required for it. Issue #58

        return false;
    }

    protected boolean isProxime() {
        return this.sensorsUtility.isProxime();
    }


}
