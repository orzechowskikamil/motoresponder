package com.medziku.motoresponder.logic;

public class GeolocationRequestRecognition {

    private Settings settings;

    public GeolocationRequestRecognition(Settings settings) {
        this.settings = settings;
    }

    public boolean isGeolocationRequest(String message) {
        String messageLowerCase = message.toLowerCase();

        for (String pattern : this.settings.getGeolocationRequestPatterns()) {
            if (messageLowerCase.indexOf(pattern.toLowerCase()) != -1) {
                return true;
            }
        }

        return false;
    }
} 
