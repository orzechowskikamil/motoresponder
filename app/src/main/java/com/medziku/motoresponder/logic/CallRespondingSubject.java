package com.medziku.motoresponder.logic;

import java.util.Date;

public class CallRespondingSubject extends RespondingSubject {

    private final Settings settings;

    public CallRespondingSubject(String phoneNumber, Date date, Settings settings) {
        super(phoneNumber, date);
        this.settings = settings;
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
