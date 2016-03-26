package com.medziku.motoresponder.logic;

import android.location.Location;
import com.medziku.motoresponder.utils.LocationUtility;

import java.util.concurrent.ExecutionException;

public class ResponsePreparator {

    public static final String LOCATION_SUBSTITUTION_TAG = "%location%";
    private Settings settings;
    private LocationUtility locationUtility;

    public ResponsePreparator(Settings settings, LocationUtility locationUtility) {
        this.settings = settings;
        this.locationUtility = locationUtility;
    }


    public String prepareResponse(RespondingSubject subject) {
        return this.shouldRespondWithGeolocation(subject) ? this.getAutoResponseMessageWithGeolocation() : this.getAutoResponseMessage(subject);
    }

    private boolean shouldRespondWithGeolocation(RespondingSubject subject) {
        return this.isRespondingWithGeolocationEnabled() && this.isCurrentRespondingSubjectGeolocationRequest(subject);
    }

    private String getAutoResponseMessageWithGeolocation() {
        String messageTemplate = this.settings.getAutoResponseToSmsWithGeolocationTemplate();

        Location location = this.getCurrentLocation();


        String locationLink = "http://maps.google.com/?q=%latitude%,%longitude%";

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


    private boolean isCurrentRespondingSubjectGeolocationRequest(RespondingSubject subject) {
        if (subject instanceof SMSRespondingSubject) {
            String message = ((SMSRespondingSubject) subject).getMessage().toLowerCase();

            for (String pattern : this.settings.getGeolocationRequestPatterns()) {
                if (message.indexOf(pattern.toLowerCase()) != -1) {
                    return true;
                }
            }
        }
        return false;
    }


    private Location getCurrentLocation() {
        try {
            return this.locationUtility.getLastRequestedLocation().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getAutoResponseMessage(RespondingSubject subject) {
        if (subject instanceof SMSRespondingSubject) {
            return this.settings.getAutoResponseToSmsTemplate();
        } else if (subject instanceof CallRespondingSubject) {
            return this.settings.getAutoResponseToCallTemplate();
        }

        return null; // should never happen
    }

    private boolean isRespondingWithGeolocationEnabled() {
        return this.settings.isRespondingWithGeolocationEnabled();
    }

}
