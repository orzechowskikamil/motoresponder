package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.ContactsUtility;


/**
 * This class is responsible for filtering numbers which shouldn't be answered.
 */
public class NumberRules {

    private ContactsUtility contactsUtility;
    private Settings settings;
    // TODO K. Orzechowski: not needed for 1.00, issue #50
//    private PhoneNumberVerifier numberVerifier;

    /**
     * This is for real usage
     *
     * @param contactsUtility
     */
    public NumberRules(ContactsUtility contactsUtility, Settings settings) {
        this.contactsUtility = contactsUtility;
        this.settings = settings;
        // TODO K. Orzechowski: not needed for 1.00, issue #50
//        this.numberVerifier = new PhoneNumberVerifier();
//        this.whiteListGroupNames = new List<String>();
//        this.blackListGrupNames = new List<String>();
    }

    /**
     * Responding current country or also abroad.
     */
    public int respondingCountrySettings = NumberRules.RESPONDING_COUNTRY_SETTINGS_ANY_COUNTRY;
    /**
     * Responding to everyone, every normal number (except for example sms premium), or only to contact book.
     */
    public int respondingSettings = NumberRules.RESPONDING_SETTINGS_RESPOND_ONLY_CONTACT_BOOK;

    /**
     * Responding only to whitelist? or disabling whitelist.
     */
    public int respondingWhitelist = NumberRules.RESPONDING_WHITELIST_DISABLED;
    public int respondingBlacklist = NumberRules.RESPONDING_BLACKLIST_DISABLED;

    public static final int RESPONDING_COUNTRY_SETTINGS_CURRENT_COUNTRY_ONLY = 0;
    public static final int RESPONDING_COUNTRY_SETTINGS_ANY_COUNTRY = 1;

    public static final int RESPONDING_SETTINGS_RESPOND_EVERYONE = 0;
    public static final int RESPONDING_SETTINGS_RESPOND_EVERY_NORMAL_NUMBER = 1;
    public static final int RESPONDING_SETTINGS_RESPOND_ONLY_CONTACT_BOOK = 2;

    public static final int RESPONDING_WHITELIST_DISABLED = 0;
    public static final int RESPONDING_WHITELIST_ENABLED = 1;

    public static final int RESPONDING_BLACKLIST_DISABLED = 0;
    public static final int RESPONDING_BLACKLIST_ENABLED = 1;




    // TODO K. Orzechowski: not needed for now, issue #50
//    public List<String> whiteListGroupNames;
//    public List<String> blackListGroupNames;


    public boolean numberRulesAllowResponding(String phoneNumber) {
        // TODO K. Orzechowski: issue #50, not needed right now
//        boolean respondingConstraintsMeet = false;
//        boolean countryRespondingConstraintsMeet = false;

        if (this.isWhiteListEnabled() && !this.isNumberOnWhitelist(phoneNumber)) {
            return false;
        }

        if (this.isBlackListEnabled() && this.isNumberOnBlacklist(phoneNumber)) {
            return false;

        }

        // TODO k.orzechowsk commented until 1.01 , issue #50
        // if (this.respondingCountrySettings == NumberRules.COUNTRY_SETTINGS_CURRENT_COUNTRY_ONLY && this.isNumberFromCurrentCountry(phoneNumber)==false){
        //     return false;
        // }
        
        /* TODO k.orzechowski, issue #50
           break method below into three sections
           First is BLACKLISTING with options: none, blacklist (put in application), contact book group
           Second is WHITELISTING with options: none, all contacts, whitelist (put in application), contact book group
           Third is NORMAL/SHORT numbers with options: everyone / normal numbers / short numbers (like sms premium)
        */

// TODO k.orzechowsk commented until 1.01, issue #50
// if (this.respondingSettings == RESPONDING_SETTINGS_RESPOND_EVERY_NORMAL_NUMBER && this.isNormalNumber(phoneNumber)==false){
// return false;

// }

        if (this.settings.isRespondingRestrictedToContactList() && this.isInContactBook(phoneNumber) == false) {
            return false;
        }

        if (this.isCurrentDevicePhoneNumber(phoneNumber)) {
            return false;
        }

        return true;
    }


    private boolean isCurrentDevicePhoneNumber(String phoneNumber) {
        // todo #71 add some prompt if device is not able to read phone number, OR disable field 
        // 
        try {
            return phoneNumber.equals(this.contactsUtility.readCurrentDevicePhoneNumber());
        } catch (Exception e) {
            return phoneNumber.equals(this.settings.getStoredDevicePhoneNumber());
        }
    }

    // TODO K. Orzechowski: not needed for 1.00, issue #50
//    private String getCurrentDeviceCountry() {
//        return numberVerifier.getUserCountry();
//    }

    // TODO K. Orzechowski: not needed for 1.00, issue #50
//    private String getCountryOfNumber(String phoneNumber) {
//        return this.numberVerifier.getCountryByPhoneNumber(null, phoneNumber);
//    }
//
//    private boolean isNumberOnWhiteList(String phoneNumber) {
//        for (String whiteListGroupName : this.whiteListGroupNames) {
//            if (this.contactsUtility.hasGroupNumberByGroupName(whiteListGroupName, phoneNumber) == true) {
//                return true;
//            }
//        }
//        return false;
//    }
//
    private boolean isNumberOnBlacklist(String phoneNumber) {
        try {
            if (this.contactsUtility.hasGroupNumberByGroupName(this.settings.getBlackListGroupName(), phoneNumber) == true) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    private boolean isNumberOnWhitelist(String phoneNumber) {
        try {
            if (this.contactsUtility.hasGroupNumberByGroupName(this.settings.getWhiteListGroupName(), phoneNumber) == true) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }



    // TODO K. Orzechowski: not needed for 1.00, issue #50
//    private boolean isNormalNumber(String phoneNumber) {
//        // todo add this to the project https://github.com/KingsMentor/PhoneNumberValidator , issue #50
//
//        // TODO K. Orzechowski: not needed for 1.00, issue #50
////        if (this.numberVerifier.isNumberValid(this.getCountryOfNumber(phoneNumber), phoneNumber) == false) {
////            return false;
////        }
//
//        // if (!PhoneNumberUtils.isGlobalPhoneNumber("+912012185234")){
//        // return false;
//        // }
//
//
//        // if (!android.util.Patterns.PHONE.matcher(phoneNumber).matches()){
//        //     return false;
//        // }
//
//        return true; // TODO K. Orzechowski:  return true if normal number - no sms premium or smth., issue #50
//    }

    // TODO K. Orzechowski: not needed for 1.00, issue #50
//    private boolean isNumberFromCurrentCountry(String phoneNumber) {
//        return this.numberVerifier.isNumberValid(this.getCurrentDeviceCountry(), phoneNumber);
//    }

    private boolean isInContactBook(String phoneNumber) {
        return this.contactsUtility.contactBookContainsNumber(phoneNumber);
    }

    public boolean isWhiteListEnabled() {
        return this.settings.getWhiteListGroupName() != null;
    }

    public boolean isBlackListEnabled() {
        return this.settings.getBlackListGroupName() != null;
    }
}
