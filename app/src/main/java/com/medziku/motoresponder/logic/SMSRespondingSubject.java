package com.medziku.motoresponder.logic;

import java.util.Date;

public class SMSRespondingSubject extends RespondingSubject {
    protected Settings settings;
    private String message;

    public SMSRespondingSubject(String phoneNumber, String message, Date date, Settings settings) {
        super(phoneNumber, date);
        this.message = message;
        this.settings = settings;
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public int getAmountLimitForAutoresponses() {
        return this.settings.getLimitOfResponses();
    }

    @Override
    public int getDelayBetweenAutoresponsesMinutes() {
        return this.settings.getDelayBetweenResponsesMinutes();
    }
}
