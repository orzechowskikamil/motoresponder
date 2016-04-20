package com.medziku.motoresponder.logic;

import android.location.Location;
import com.medziku.motoresponder.utils.AccelerometerNotAvailableException;
import com.medziku.motoresponder.utils.LocationUtility;
import com.medziku.motoresponder.utils.MotionUtility;
import com.medziku.motoresponder.utils.SensorsUtility;

import java.util.Date;
import java.util.concurrent.ExecutionException;


/**
 * This class represent if user ride or not.
 */
public class UserRide {

    private Settings settings;
    private DecisionLog log;
    private LocationUtility locationUtility;
    private SensorsUtility sensorsUtility;
    private MotionUtility motionUtility;

    /**

    /**
     * For real usage
     *
     * @param locationUtility
     * @param sensorsUtility
     * @param motionUtility
     */
    public UserRide(Settings settings, LocationUtility locationUtility, SensorsUtility sensorsUtility, MotionUtility motionUtility, DecisionLog log) {
        this.locationUtility = locationUtility;
        this.sensorsUtility = sensorsUtility;
        this.motionUtility = motionUtility;
        this.settings = settings;
        this.log = log;
    }


    public boolean isUserRiding() {
        // if rider rides, phone should be in pocket (ofc if somebody use phone during ride outside pocket, he should
        // disable this option).
        // in pocket is proxime (to leg or chest)... If there is no proximity, he is not riding.
        if (this.settings.isProximityCheckEnabled() && !this.isProxime()) {
            this.log.add("Device screen is not near something.");
            return false;
        }

        // TODO k.orzechowsk: If you know way of making promise, why not make promisable light check and
        // TODO k.orzechowsk: proximity check? It will save battery aswell... Issue #53

        // if phone doesn't report any movement we can also assume that user is not riding motorcycle
        try {
            if (this.settings.includeDeviceMotionCheck() && !this.motionSensorReportsMovement()) {
                this.log.add("Device's accelerometer doesn't report movement.");
                return false;
            }
        } catch (AccelerometerNotAvailableException e) {
            // do nothing - it's normal situation if accelerometer not available, continue.
        }


        // TODO k.orzechowsk add Bluetooth Beacon option to identify that you sit on bike IN FUTURE Issue #54
        // TODO k.orzechowsk add NFC tag in pocket option to identify that you sit on bike IN FUTURE Issue #55
        // TODO k.orzechowsk identify of stolen bikes via beacon in very very future when app will be popular. Issue #56

        // TODO k.orzechowsk add option to disable GPS, maybe someone don't want to use it, only gyro? Issue #10

        this.log.add("Starting GPS speed check.");

        Float quickCheckSpeedKmh = this.getQuickCheckCurrentSpeedKmh();

        if (this.isLocationTimeouted(quickCheckSpeedKmh)) {
            // if quick check is timeouted, it means that user is in building with quick access to GPS signal, so he is not riding.
            this.log.add("First check speed timeouted - user probably in building, not riding.");
            return false;
        }


        if (!this.isSpeedForSureRiding(quickCheckSpeedKmh)) {
            this.log.add("First check is speed less than sure riding speed, but not timeouted - checking again, using longer check.");

            // if quick check returned speed below sure riding speed, but no timeout, it means that user is outside but 
            // he is not moving with motorcycle speed. We need to verify if it is not staying at traffic lights,
            // so we need to perform long GPS check for 3-4 minutes and wait for minimumSureRidingSpeedKmh speed from GPS.

            Float longCheckSpeedKmh = this.getLongCheckCurrentSpeedKmh();

            if (this.isLocationTimeouted(longCheckSpeedKmh)) {
                // but if again timeouted, then no mercy...
                this.log.add("Second check of location timeouted - user is in building, or not moving fast enough to assume ride.");
                return false;
            }

            if (!this.isSpeedForSureRiding(longCheckSpeedKmh)) {
                // if he don't reach minimumSureRidingSpeedKmh in time of long check, then he for sure is not riding.
                this.log.add("After second check app isn't sure that user is riding - assuming that he is not riding.");
                return false;
            }
        }

        // all conditions when we are sure that user is not riding are not met - so user is riding.
        return true;
    }

    public void cancelUserRideCheck(){
       this.cancelGPSCheck(); 
    }
    
    private void cancelGPSCheck(){
        this.locationUtility.cancelGPSCheck();    
    }
    
    protected boolean isSpeedForSureRiding(float speedKmh) {
        return speedKmh >= this.settings.getSureRidingSpeedKmh();
    }

    protected boolean isLocationTimeouted(Float speedKmh) {
        return speedKmh == null;
    }

    protected boolean motionSensorReportsMovement() throws AccelerometerNotAvailableException {
        try {
            return this.motionUtility.isDeviceInMotion().get();
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

    /**
     * This method will make quick check to see if device's GPS will return timeout (home), 0-40 speed (maybe traffic jam
     * and maybe home - need to make long check) and +40 - for sure riding.
     *
     * @return
     */
    protected Float getQuickCheckCurrentSpeedKmh() {
        return this.getCurrentSpeedKmh(
                this.settings.getMaximumStayingStillSpeedKmh(),
                this.settings.getRequiredAccuracyMeters(),
                this.settings.getQuickSpeedCheckDurationSeconds() * 1000);
    }


    /**
     * This method will wait until sureRidingSpeed will be grabbed by location services for smth like 4 minutes.
     * It's only for clarification for cases where it's unsure if you are staying in traffic jam or not riding.
     *
     * @return
     */
    protected Float getLongCheckCurrentSpeedKmh() {
        return this.getCurrentSpeedKmh(
                this.settings.getSureRidingSpeedKmh(),
                this.settings.getRequiredAccuracyMeters(),
                this.settings.getLongSpeedCheckDurationSeconds() * 1000);
    }


    /**
     * @return Speed in km/h or -1 if location request timeouted.
     */
    protected Float getCurrentSpeedKmh(float minimumSpeedKmh, float maximumAccuracyMeters, long timeoutMs) {
        long startDateTimestamp = new Date().getTime();

        Location location = null;
        float minimumSpeedMs = this.kmhToMs(minimumSpeedKmh) - 1; // to avoid rounding error problems during comparations.
        try {
            location = this.locationUtility.getAccurateLocation(minimumSpeedMs, maximumAccuracyMeters, timeoutMs).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        long endDateTimestamp = new Date().getTime();
        int checkDurationSeconds = Math.round((endDateTimestamp - startDateTimestamp) / 1000);

        if (location == null) {
            this.log.add("GPS check took " + checkDurationSeconds + "s and it was timeouted.");
            return null;
        }

        float speedMs = location.getSpeed();
        float speedKmh = this.msToKmh(speedMs);

        this.log.add("GPS check took " + checkDurationSeconds + "s and determined speed was " + speedKmh + " km/h.");

        return speedKmh;
    }


    protected float msToKmh(float speedMs) {
        return (float) (speedMs * 3.6);
    }

    protected float kmhToMs(float speedKmh) {
        return (float) (speedKmh / 3.6);
    }
}
