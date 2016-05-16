package com.medziku.motoresponder.utils;

public class SMSObject {
    public String message;
    public String phoneNumber;

    public SMSObject(String phoneNumber, String message) {
        this.message = message;
        this.phoneNumber = phoneNumber;
    }

}
