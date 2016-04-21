package com.medziku.motoresponder.logic;


abstract public class RespondingSubject {

    private String phoneNumber;

    public RespondingSubject(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }


}
