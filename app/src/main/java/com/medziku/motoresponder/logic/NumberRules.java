package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.ContactsUtility;


/**
 * This class is responsible for filtering numbers which shouldn't be answered.
 */
public class NumberRules {

    private ContactsUtility contactsUtility;
    private Settings settings;

    public NumberRules(ContactsUtility contactsUtility, Settings settings) {
        this.contactsUtility = contactsUtility;
        this.settings = settings;

    }


    public boolean numberRulesAllowResponding(String phoneNumber) {
        if (!this.isNumberNormal(phoneNumber)) {
            return false;
        }

        if (this.isWhiteListEnabled() && !this.isNumberOnWhitelist(phoneNumber)) {
            return false;
        }

        if (this.isBlackListEnabled() && this.isNumberOnBlacklist(phoneNumber)) {
            return false;
        }

        if (this.settings.isRespondingRestrictedToCurrentCountry() && this.isNumberForeign(phoneNumber)) {
            return false;
        }

        if (this.settings.isRespondingRestrictedToContactList() && !this.isInContactBook(phoneNumber)) {
            return false;
        }

        if (this.isCurrentDevicePhoneNumber(phoneNumber)) {
            return false;
        }

        return true;
    }

    private boolean isNumberNormal(String phoneNumber) {
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


    private boolean isNumberForeign(String phoneNumber) {
        return PhoneNumbersComparator.isNumberForeign(phoneNumber, this.getCurrentCountryCode());
    }

    private String getCurrentCountryCode() {
        // Hardcoded for Poland. Implement automatic checking based on mcc when releasing to whole world.
        // Issue #169
        return "48";
    }

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
