package com.medziku.motoresponder.logic;

import android.location.Location;
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
    public int maybeRidingSpeed = 15;
    public int sureRidingSpeed = 60;

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
            // TODO K. Orzechowski: it's for development uncomment it later
            //  return false;
        }

        // TODO k.orzechowsk: If you know way of making promise, why not make promisable light check and
        // TODO k.orzechowsk: proximity check? It will save battery aswell...

        // inside pocket should be dark. if it's light, he is probably not riding
        if (this.includeLightCheck && this.isLightOutside()) {
            // TODO K. Orzechowski: it's for development uncomment it later
            //   return false;
        }


        // if phone doesn't report any movement we can also assume that user is not riding motorcycle
        // TODO k.orzechowsk this name is plural, refactor it to motionSensorsReportsMovement
        boolean deviceStayingStill = !this.motionSensorReportsMovement();
        if (this.includeDeviceMotionCheck && deviceStayingStill) {
            return false;
        }

        // TODO k.orzechowsk add Bluetooth Beacon option to identify that you sit on bike IN FUTURE
        // TODO k.orzechowsk add NFC tag in pocket option to identify that you sit on bike IN FUTURE
        // TODO k.orzechowsk identify of stolen bikes via beacon in very very future when app will be popular.

        // TODO k.orzechowsk add option to disable GPS, maybe someone don't want to use it, only gyro?
        float speedKmh = this.getCurrentSpeedKmh();


        boolean locationTimeouted = speedKmh == -1;
        // TODO K. Orzechowski: this setting is for future, when I implement asking again for location after some time.
        // TODO K. Orzechowski: for now it's just dumb if
        if (this.interpretLocationTimeoutAsNotRiding && locationTimeouted) {
            // if timeout, it means that phone is probably in home with no access to GPS satelites.
            // so if no ride, no need to respond automatically
            return false;
        }

        // TODO K. Orzechowski: add second check of speed if user is between sure riding speed and no riding speed
        // for example: 15 km/h. It can be motorcycle or running. We make another check in few minutes - maybe
        // we hit bigger speed and it become sure.

        if (speedKmh <= this.sureRidingSpeed) {
            return false;
        }

        // all conditions when we are sure that user is not riding are not met - so user is riding.
        return true;
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

    private boolean isLightOutside() {
        return this.sensorsUtility.isLightOutside();
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