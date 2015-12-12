package com.medziku.motoresponder.logic;

import android.location.Location;
import android.util.Log;
import com.medziku.motoresponder.utils.LocationUtility;
import com.medziku.motoresponder.utils.MotionUtility;
import com.medziku.motoresponder.utils.SensorsUtility;


/**
 * This class represent if user ride or not.
 */
public class UserRide {

    private LocationUtility locationUtility;
    private SensorsUtility sensorsUtility;
    private MotionUtility motionUtility;


    /**
     * If true, it will interpret timeout during gathering location as being in home (often location timeout
     * is caused by being in building, riding through tunnel is rare).
     * If false, it will ignore timeout.
     */
    // TODO K. Orzechowski: for normal it should be true, for development - false
    public boolean interpretLocationTimeoutAsNotRiding = false;
    /**
     * If true, it will assume not riding if phone proximity sensor read false value (no proximity - not in pocket).
     * If false, it will ignore proximity check.
     */
    public boolean includeProximityCheck = true;
    /**
     * If true, it will assume not riding if there is light on the sensor (phone not in pocket).
     * If false, it will ignore light readings.
     */
    public boolean includeLightCheck = true;

    /**
     * If true, if accelerometer will report staying still, app will assume that staying = not riding.
     * If false, it will ignore accelerometer reading
     */
    public boolean includeDeviceMotionCheck = true;
    // TODO K. Orzechowski: use it later
    public boolean doAnotherGPSCheckIfNotSure = true;

    // TODO K. Orzechowski: use it later
    public float maybeRidingSpeed = 15;
    public int maybeRidingTimeoutMs = 30000;

    /**
     * This is speed in kilometers which for sure is speed achieveable only by riding on motorcycle, and
     * for example, not walking or running.
     */
    public double sureRidingSpeed = 40;

    public UserRide(LocationUtility locationUtility, SensorsUtility sensorsUtility, MotionUtility motionUtility) {
        this.locationUtility = locationUtility;
        this.sensorsUtility = sensorsUtility;
        this.motionUtility = motionUtility;
    }


    public boolean isUserRiding() {
        // if rider rides, phone should be in pocket (ofc if somebody use phone during ride outside pocket, he should
        // disable this option).
        // in pocket is proxime (to leg or chest)... If there is no proximity, he is not riding.
        if (this.includeProximityCheck && !this.isProxime()) {
            // TODO K. Orzechowski: commented for development, uncomment later
            //  return false;
            //return false;
        }

        // TODO k.orzechowsk: If you know way of making promise, why not make promisable light check and
        // TODO k.orzechowsk: proximity check? It will save battery aswell...

        // if phone doesn't report any movement we can also assume that user is not riding motorcycle
        if (this.includeDeviceMotionCheck && !this.motionSensorReportsMovement()) {
            // TODO K. Orzechowski: commented for development, uncomment later
            //  return false;
        }


        // TODO k.orzechowsk add Bluetooth Beacon option to identify that you sit on bike IN FUTURE
        // TODO k.orzechowsk add NFC tag in pocket option to identify that you sit on bike IN FUTURE
        // TODO k.orzechowsk identify of stolen bikes via beacon in very very future when app will be popular.

        // TODO k.orzechowsk add option to disable GPS, maybe someone don't want to use it, only gyro?
        float speedKmh = this.getCurrentSpeedKmh();


        // TODO K. Orzechowski: this setting is for future, when I implement asking again for location after some time.
        // TODO K. Orzechowski: for now it's just dumb if
        if (this.interpretLocationTimeoutAsNotRiding && this.isLocationTimeouted(speedKmh)) {
            // if timeout, it means that phone is probably in home with no access to GPS satelites.
            // so if no ride, no need to respond automatically
            return false;
        }

        if (this.isSpeedForSureNotRiding(speedKmh)) {
            return false;
        }

        // second check of speed if user is between sure riding speed and no riding speed
        // for example: 15 km/h. It can be motorcycle or running. We make another check in few minutes - maybe
        // we hit bigger speed and it become sure.

        if (this.isSpeedMaybeRiding(speedKmh)) {
            try {
                Thread.sleep(this.maybeRidingTimeoutMs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            float secondCheckSpeedKmh = this.getCurrentSpeedKmh();

            if (this.isSpeedForSureRiding(secondCheckSpeedKmh) == false) {
                return false;
            }
        }

        // all conditions when we are sure that user is not riding are not met - so user is riding.
        return true;
    }

    private boolean isSpeedForSureNotRiding(float speedKmh) {
        return speedKmh < this.maybeRidingSpeed;
    }

    private boolean isSpeedForSureRiding(float speedKmh) {
        return speedKmh >= this.sureRidingSpeed;
    }

    private boolean isLocationTimeouted(float speedKmh) {
        return speedKmh == -1;
    }

    // todo k.orzechowsk ridiculous name, fix it
    private boolean isSpeedMaybeRiding(float speedKmh) {
        return this.isSpeedForSureNotRiding(speedKmh) == false && this.isSpeedForSureRiding(speedKmh) == false;
    }


    private boolean motionSensorReportsMovement() {
        try {
            return this.motionUtility.isDeviceInMotion().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // TODO K. Orzechowski: using here also gyroscope and magneometer is not a bad idea
        // maybe other method will be required for it.

        // default - false
        return false;
    }

    private boolean isProxime() {
        return this.sensorsUtility.isProxime();
    }


    /**
     * @return Speed in km/h or -1 if location request timeouted.
     */
    private float getCurrentSpeedKmh() {
        Location location = null;
        try {
            location = this.locationUtility.getAccurateLocation().get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // -1 is value of speed for timeouted request.
        float speedMs = (location == null) ? -1 : location.getSpeed();
        return this.msToKmh(speedMs);
    }


    private float msToKmh(float speedMs) {
        return (float) (speedMs * 3.6);
    }
}
