package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.ContactsUtility;


/**
 * This class is responsible for filtering numbers which shouldn't be answered.
 */
public class NumberRules {

    private CustomLog log;
    private ContactsUtility contactsUtility;
    private Settings settings;
    private CountryPrefix countryPrefix;

    public NumberRules(ContactsUtility contactsUtility, CountryPrefix countryPrefix, Settings settings, CustomLog log) {
        this.contactsUtility = contactsUtility;
        this.countryPrefix = countryPrefix;
        this.settings = settings;
        this.log = log;
    }


    public boolean numberRulesAllowResponding(String phoneNumber) {
        this.log.add("Measuring if number " + phoneNumber + " is allowed to get autoresponse.");

        if (!this.isNumberNormal(phoneNumber)) {
            this.log.add("This number is not normal. Not allowed.");
            return false;
        }

        if (this.isWhiteListEnabled() && !this.isNumberOnWhitelist(phoneNumber)) {
            this.log.add("Whitelist is enabled and number is not on whitelist. Not allowed.");
            return false;
        }

        if (this.isBlackListEnabled() && this.isNumberOnBlacklist(phoneNumber)) {
            this.log.add("Blacklist is enabled and number is on blacklist. Not allowed.");
            return false;
        }

        if (this.settings.isRespondingRestrictedToCurrentCountry()) {
            boolean numberForeign = false;
            try {
                numberForeign = this.isNumberForeign(phoneNumber);
            } catch (Exception e) {
                this.log.add("Filtering foreign numbers is not possible in this country.");
            }
            if (numberForeign) {
                this.log.add("Number is foreign and filtering foreign number is enabled. Not allowed.");
                return false;
            }
        }

        if (this.settings.isRespondingRestrictedToContactList() && !this.isInContactBook(phoneNumber)) {
            this.log.add("Filtering only to contact book is enabled, and number is outside contact book. Not allowed.");
            return false;
        }

        if (this.isCurrentDevicePhoneNumber(phoneNumber)) {
            this.log.add("This number is current device number (itself). Not allowed.");
            return false;
        }

        this.log.add("Number seems to be ok and number rules allow responding.");
        return true;
    }

    public boolean isAbleToFilterForeignNumbers() {
        try {
            this.isNumberForeign("");
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean isWhiteListEnabled() {
        return this.settings.isWhiteListEnabled();
    }

    public boolean isBlackListEnabled() {
        return this.settings.isBlackListEnabled();
    }

    private boolean isNumberNormal(String phoneNumber) {
        if (phoneNumber == null) {
            return false; // null number is not normal...
        }
        return PhoneNumbersComparator.isNumberNormal(phoneNumber);
    }

    private boolean isCurrentDevicePhoneNumber(String phoneNumber) {
        String currentDevicePhoneNumber;
        try {
            currentDevicePhoneNumber = this.contactsUtility.readCurrentDevicePhoneNumber();
        } catch (Exception e) {
            currentDevicePhoneNumber = this.settings.getStoredDevicePhoneNumber();
        }

        return phoneNumber.equals(currentDevicePhoneNumber);
    }

    private boolean isNumberOnBlacklist(String phoneNumber) {
        try {
            if (this.contactsUtility.hasGroupNumberByGroupName(this.settings.getBlackListGroupName(), phoneNumber) == true) {
                return true;
            }

            for (String phoneNumberFromBlacklist : this.settings.getBlacklistedContactsList()) {
                if (PhoneNumbersComparator.areNumbersEqual(phoneNumberFromBlacklist, phoneNumber)) {
                    return true;
                }
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

            for (String phoneNumberFromWhitelist : this.settings.getWhitelistedContactsList()) {
                if (PhoneNumbersComparator.areNumbersEqual(phoneNumberFromWhitelist, phoneNumber)) {
                    return true;
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    private boolean isNumberForeign(String phoneNumber) throws Exception {
        String currentCountryCode = this.getCurrentCountryCode();

        if (currentCountryCode == null) {
            throw new Exception("Not able to check if number is foreign");
        }

        return PhoneNumbersComparator.isNumberForeign(phoneNumber, currentCountryCode);
    }

    private String getCurrentCountryCode() {
        return countryPrefix.getCountryPrefix();
    }

    private boolean isInContactBook(String phoneNumber) {
        return this.contactsUtility.contactBookContainsNumber(phoneNumber);
    }
}
