package com.medziku.motoresponder.logic;

/**
 * This class is responsible for filtering numbers which shouldn't be answered.
 */
public class NumberRules {


    public NumberRules() {
    }

    /**
     * Responding current country or also abroad.
     */
    public int respondingCountrySettings = 0;
    /**
     * Responding to group, contact book, normal numbers or everyone.
     */
    public int respondingSettings = 2;

    public static final int RESPONDING_COUNTRY_SETTINGS_CURRENT_COUNTRY_ONLY = 0;
    public static final int RESPONDING_COUNTRY_SETTINGS_ANY_COUNTRY = 1;

    public static final int RESPONDING_SETTINGS_RESPOND_EVERYONE = 0;
    public static final int RESPONDING_SETTINGS_RESPOND_EVERY_NORMAL_NUMBER = 1;
    public static final int RESPONDING_SETTINGS_RESPOND_ONLY_CONTACT_BOOK = 2;
    public static final int RESPONDING_SETTINGS_RESPOND_ONLY_GROUP = 3;


    public boolean shouldRespondToThisNumber(String phoneNumber) {
        boolean respondingConstraintsMeet = false;
        boolean countryRespondingConstraintsMeet = false;

        switch (this.respondingCountrySettings) {
            case NumberRules.RESPONDING_COUNTRY_SETTINGS_ANY_COUNTRY:
                countryRespondingConstraintsMeet = true;
                break;
            case NumberRules.RESPONDING_COUNTRY_SETTINGS_CURRENT_COUNTRY_ONLY:
                countryRespondingConstraintsMeet = this.isNumberFromCurrentCountry(phoneNumber);
                break;
        }


        /* TODO k.orzechowski
           break method below into three sections
           First is BLACKLISTING with options: none, blacklist (put in application), contact book group
           Second is WHITELISTING with options: none, all contacts, whitelist (put in application), contact book group
           Third is NORMAL/SHORT numbers with options: everyone / normal numbers / short numbers (like sms premium)
        */

        switch (this.respondingSettings) {
            case NumberRules.RESPONDING_SETTINGS_RESPOND_EVERYONE:
                respondingConstraintsMeet = true;
                break;
            case NumberRules.RESPONDING_SETTINGS_RESPOND_EVERY_NORMAL_NUMBER:
                respondingConstraintsMeet = this.isNormalNumber(phoneNumber);
                break;
            case NumberRules.RESPONDING_SETTINGS_RESPOND_ONLY_CONTACT_BOOK:
                respondingConstraintsMeet = this.isInContactBook(phoneNumber);
                break;
            case NumberRules.RESPONDING_SETTINGS_RESPOND_ONLY_GROUP:
                respondingConstraintsMeet = this.isInGroup(phoneNumber);
                break;
        }

        // TODO K. Orzechowski: loop when you will got message from your own device!!!!

        // TODO K. Orzechowski: create smth like no more than 2 responses in one day
        // TODO K. Orzechowski: or no more than 2 responses from one unlock to another
        boolean respondingAllowed = respondingConstraintsMeet && countryRespondingConstraintsMeet;

        // TODO K. Orzechowski: for development, always true, change to real value later
        respondingAllowed = true;
        return respondingAllowed;
    }


    private boolean isNormalNumber(String phoneNumber) {
        return true; // TODO K. Orzechowski:  return true if normal number - no sms premium or smth.
    }

    private boolean isNumberFromCurrentCountry(String phoneNumber) {
        // TODO K. Orzechowski: implement real logic
        return true;
    }

    private boolean isInContactBook(String phoneNumber) {
        return true;// TODO K. Orzechowski:  check if in contact book
    }

    private boolean isInGroup(String phoneNumber) {
        return true;
        // TODO K. Orzechowski:  probably we need one special group, or selector from exisiting groups allowing user to choose many groups.
    }

}
