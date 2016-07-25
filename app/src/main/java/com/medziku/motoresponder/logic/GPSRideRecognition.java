package com.medziku.motoresponder.logic;

import android.location.Location;
import com.medziku.motoresponder.utils.LocationUtility;

import java.util.Date;

/**
 * This component can recognize if user is riding on motorcycle by using GPS.
 * Case where user stay at home, or stay - run - stay because of traffic jam is also covered.
 */
public class GPSRideRecognition {

    private final CustomLog log;
    private final LocationUtility locationUtility;
    private final Settings settings;

    public GPSRideRecognition(LocationUtility locationUtility, Settings settings, CustomLog log) {
        this.log = log;
        this.locationUtility = locationUtility;
        this.settings = settings;
    }

    public boolean isUserRidingByGPS() throws GPSNotAvailableException {
        this.log.add("Starting GPS speed check.");

        boolean USER_IS_RIDING = true;
        boolean USER_IS_NOT_RIDING = false;

        Location quickCheckLocation = this.getQuickCheckLocation();

        if (this.isLocationTimeouted(quickCheckLocation)) {
            // if quick check is timeouted, it means that user is in building with quick access to GPS signal, so he is not riding.
            this.log.add("First check of speed timeouted - user probably is in building.");
            return USER_IS_NOT_RIDING;
        }

        Float quickCheckSpeedKmh = this.getSpeedKmhFromLocation(quickCheckLocation);

        if (this.isSureRidingSpeed(quickCheckSpeedKmh)) {
            this.log.add("First check of speed - user exceeded riding speed");
            return USER_IS_RIDING;
        }


        // if quick check returned speed below sure riding speed, but no timeout, it means that user is outside but
        // he is not moving with motorcycle speed. We need to verify if it is not staying at traffic lights.
        // So we make another quick check after some delay, to see if user moved or he stay still in the same place.
        // if user moved it might be traffic jam.
        this.log.add("First check of speed - user not exceeded riding speed. Need to check if he is not in traffic jam.");


        // wait a little, let user wait out red light and move a little
        this.makeTrafficJamDetectionSleep();


        Location trafficJamDetectionQuickCheckLocation = this.getTrafficJamDetectionLocation();

        if (this.isLocationTimeouted(trafficJamDetectionQuickCheckLocation)) {
            this.log.add("Traffic jam detection check timeouted");
            return USER_IS_NOT_RIDING;
        }

        if (this.isSureRidingSpeed(this.getSpeedKmhFromLocation(trafficJamDetectionQuickCheckLocation))) {
            this.log.add("Riding speed exceed during traffic jam detection");
            return USER_IS_RIDING;
        }

        float distance = trafficJamDetectionQuickCheckLocation.distanceTo(quickCheckLocation);
        if (distance < this.settings.getDistanceForTrafficJamDetectionMeters()) { // TODO this can be calculated based on setting minimum riding speed and sleep delay instead of hardcode
            this.log.add("Riding speed not exceeded , and user move " + distance + "m since last check. It's below distance for traffic jam detection. User is not moving and speed is not much - he probably just stay still!");
            return USER_IS_NOT_RIDING;
        }

        // user didnt reach riding speed, but we confirmed that user moved a little, so we can make longer check to finally get a speed
        // so we need to perform long GPS check for 3-4 minutes and wait for minimumSureRidingSpeedKmh speed from GPS.
        this.log.add("Riding speed not exceeded but user moved " + distance + "m since last check. So he is riding from time to time and then stay. It might be traffic jam. It's worth to listen for some minutes to see if he will exceed riding speed.");

        Location longCheckLocation = this.getLongCheckCurrentSpeedKmh();

        if (this.isLocationTimeouted(longCheckLocation)) {
            // but if again timeouted, then no mercy...
            this.log.add("Long check of location timeouted - user is probably in building.");
            return USER_IS_NOT_RIDING;
        }

        if (!this.isSureRidingSpeed(this.getSpeedKmhFromLocation(longCheckLocation))) {
            // if he don't reach minimumSureRidingSpeedKmh in time of long check, then he for sure is not riding.
            this.log.add("During long check of speed user didn't exceed riding speed. He definitely is not riding.");
            return USER_IS_NOT_RIDING;
        }

        return USER_IS_RIDING;
    }

    public void cancelGPSCheck() {
        this.locationUtility.cancelGPSCheck();
    }

    private Float getSpeedKmhFromLocation(Location location) {
        float speedMs = location.getSpeed();
        float speedKmh = this.msToKmh(speedMs);

        return speedKmh;
    }

    private float msToKmh(float speedMs) {
        return (float) (speedMs * 3.6);
    }

    private float kmhToMs(float speedKmh) {
        return (float) (speedKmh / 3.6);
    }

    private Location getQuickCheckLocation() throws GPSNotAvailableException {
        return this.getCurrentLocation(
                (float) this.settings.getMaximumStayingStillSpeedKmh(),
                this.settings.getRequiredAccuracyMeters(),
                this.settings.getQuickSpeedCheckDurationSeconds() * 1000);
    }

    private Location getTrafficJamDetectionLocation() throws GPSNotAvailableException {
        return this.getCurrentLocation(
                null,
                this.settings.getRequiredAccuracyMeters(),
                this.settings.getTrafficJamDetectionDurationSeconds());
    }

    /**
     * This method will wait until sureRidingSpeed will be grabbed by location services for smth like 4 minutes.
     * It's only for clarification for cases where it's unsure if you are staying in traffic jam or not riding.
     *
     * @return
     */// TODO rename getLongCheckLocation
    private Location getLongCheckCurrentSpeedKmh() throws GPSNotAvailableException {
        return this.getCurrentLocation(
                (float) this.settings.getSureRidingSpeedKmh(),
                this.settings.getRequiredAccuracyMeters(),
                this.settings.getLongSpeedCheckDurationSeconds() * 1000);

    }

    /**
     * @return Speed in km/h or -1 if location request timeouted.
     */
    private Location getCurrentLocation(Float minimumSpeedKmh, float maximumAccuracyMeters, long timeoutMs) throws GPSNotAvailableException {
        long startDateTimestamp = new Date().getTime();

        Location location = null;
        Float minimumSpeedMs = minimumSpeedKmh == null ? null : this.kmhToMs(minimumSpeedKmh) - 1; // to avoid rounding error problems during comparations.
        try {
            location = this.locationUtility.getAccurateLocation(minimumSpeedMs, maximumAccuracyMeters, timeoutMs).get();
        } catch (Exception e) {
            throw new GPSNotAvailableException();
        }

        long endDateTimestamp = new Date().getTime();
        int checkDurationSeconds = Math.round((endDateTimestamp - startDateTimestamp) / 1000);

        this.log.add(this.locationUtility.getAndClearInternalLog());

        if (this.isLocationTimeouted(location)) {
            this.log.add("GPS check took " + checkDurationSeconds + "s and it was timeouted.");
            return null;
        }

        this.log.add("GPS check took " + checkDurationSeconds + "s and determined speed was " + this.msToKmh(location.getSpeed()) + " km/h.");

        return location;
    }

    private boolean isSureRidingSpeed(float speedKmh) {
        return speedKmh >= this.settings.getSureRidingSpeedKmh();
    }

    private boolean isLocationTimeouted(Location location) {
        return location == null;
    }

    private void makeTrafficJamDetectionSleep() {
        int sleepDelay = this.settings.getTrafficJamDelaySeconds();
        try {
            Thread.sleep(sleepDelay * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        this.log.add("Slept for " + sleepDelay + "s, waiting for movement in traffic jam.");
    }
}

