package com.medziku.motoresponder.logic;

import android.location.Location;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.LocationUtility;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ResponsePreparator {

    public static final String LOCATION_SUBSTITUTION_TAG = "%location%";
    public static final String LATITUDE_SUBSTITUTION_TAG = "%latitude%";
    public static final String LONGITUDE_SUBSTITUTION_TAG = "%longitude%";
    public static final String LOCATION_LINK = "\"http://maps.google.com/maps?q=" + LATITUDE_SUBSTITUTION_TAG + "," + LONGITUDE_SUBSTITUTION_TAG + "\"";

    private ContactsUtility contactsUtility;
    private Settings settings;
    private LocationUtility locationUtility;


    public ResponsePreparator(Settings settings, LocationUtility locationUtility, ContactsUtility contactsUtility) {
        this.settings = settings;
        this.contactsUtility = contactsUtility;
        this.locationUtility = locationUtility;
    }


    public String prepareResponse(RespondingSubject subject) {
        if (subject instanceof SMSRespondingSubject) {

            if (this.shouldRespondWithGeolocation(subject)) {
                return this.getAutoResponseMessageWithGeolocation();
            }

            return this.settings.getAutoResponseToSmsTemplate();
        } else if (subject instanceof CallRespondingSubject) {
            return this.settings.getAutoResponseToCallTemplate();
        }


        // should never happen
        return null;
    }


    private boolean shouldRespondWithGeolocation(RespondingSubject subject) {
        if (!this.settings.isRespondingWithGeolocationEnabled()) {
            return false;
        }

        boolean isGeolocationRequest = subject instanceof GeolocationRequestRespondingSubject
                || this.settings.isRespondingWithGeolocationAlwaysEnabled();

        if (!isGeolocationRequest) {
            return false;
        }

        String geolocationWhitelistGroupName = this.settings.getGeolocationWhitelistGroupName();
        if (geolocationWhitelistGroupName != null) {
            try {
                if (!this.contactsUtility.hasGroupNumberByGroupName(geolocationWhitelistGroupName, subject.getPhoneNumber())) {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }


        return true;

    }

    private String getAutoResponseMessageWithGeolocation() {
        String messageTemplate = this.settings.getAutoResponseToSmsWithGeolocationTemplate();

        Location location = this.getCurrentLocation();


        String locationLink = LOCATION_LINK;

        if (location != null) {
            locationLink = locationLink
                    .replace("%latitude%", Double.toString(location.getLatitude()))
                    .replace("%longitude%", Double.toString(location.getLongitude()));
        }

        if (messageTemplate.indexOf(LOCATION_SUBSTITUTION_TAG) == -1) {
            throw new RuntimeException("No location tag in message template");
        }

        String message = messageTemplate.replace(LOCATION_SUBSTITUTION_TAG, locationLink);


        return message;
    }


    private Location getCurrentLocation() {
        try {
            Future<Location> lastRequestedLocation = this.locationUtility.getLastRequestedLocation();
            if (lastRequestedLocation != null) {
                return lastRequestedLocation.get();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


}
