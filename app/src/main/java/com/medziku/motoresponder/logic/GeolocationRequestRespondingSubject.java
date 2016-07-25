package com.medziku.motoresponder.logic;


import java.util.Date;

public class GeolocationRequestRespondingSubject extends SMSRespondingSubject {

    public GeolocationRequestRespondingSubject(String phoneNumber, String message, Date date, Settings settings) {
        super(phoneNumber, message, date, settings);
    }

    @Override
    public int getAmountLimitForAutoresponses() {
        return this.settings.getLimitOfGeolocationResponses();
    }
}
