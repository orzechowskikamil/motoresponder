package com.medziku.motoresponder.logic;


import java.util.Date;

abstract public class RespondingSubject {

    private Date date;
    private String phoneNumber;


    public RespondingSubject(String phoneNumber, Date date) {
        this.phoneNumber = phoneNumber;
        this.date = date;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    abstract public int getAmountLimitForAutoresponses();

    abstract public int getDelayBetweenAutoresponsesMinutes();

    public Date getDate() {
        return this.date;
    }
}
