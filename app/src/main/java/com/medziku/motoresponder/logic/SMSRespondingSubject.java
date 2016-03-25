package com.medziku.motoresponder.logic;

public class SMSRespondingSubject extends RespondingSubject {
    private String message;

    public SMSRespondingSubject(String phoneNumber, String message) {
        super(phoneNumber);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
