package com.medziku.motoresponder.pseudotesting.utilities;


import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.ContactsUtility;
import com.medziku.motoresponder.utils.structures.ContactDefinition;

import java.util.List;

public class ContactsUtilityTest {

    private static final String TAG = "ContactsUtilityTest";
    private Context context;
    private ContactsUtility contactsUtility;
    /**
     * This number should be in device's contact book!
     */
    private String existingPhoneNumberInContacts = "791467855";
    private String existingPhoneNumberInContactsIsoCode = "+48791467855";
    private String existingPhoneNumberInContactsSpaces = "+48 791 467 855";
    private String existingPhoneNumberInContactsCode = "48 791 467 855";
    private String existingPhoneNumberInContactsZeroZero = "0048 791 467 855";
    /**
     * This number shouldn't be in device's contact book!
     */
    private String nonExistingPhoneNumberInContacts = "666666666";

    public ContactsUtilityTest(Context context) {
        this.context = context;
    }

    public void testContactBookContainsContact() {
        this.setUp();

        boolean resultExisting = this.contactsUtility.contactBookContainsNumber(this.existingPhoneNumberInContacts);
        boolean resultExistingIsoCode = this.contactsUtility.contactBookContainsNumber(this.existingPhoneNumberInContactsIsoCode);
        boolean resultExistingSpaces = this.contactsUtility.contactBookContainsNumber(this.existingPhoneNumberInContactsSpaces);
        boolean resultExistingCode = this.contactsUtility.contactBookContainsNumber(this.existingPhoneNumberInContactsCode);
        boolean resultExistingZeroZero = this.contactsUtility.contactBookContainsNumber(this.existingPhoneNumberInContactsZeroZero);
        boolean resultNonExisting = this.contactsUtility.contactBookContainsNumber(this.nonExistingPhoneNumberInContacts);


        Log.d(TAG, "Result of contactBookContainsNumber(" + this.existingPhoneNumberInContacts + ") is " + resultExisting);
        Log.d(TAG, "Result of contactBookContainsNumber(" + this.existingPhoneNumberInContactsIsoCode + ") is " + resultExistingIsoCode);
        Log.d(TAG, "Result of contactBookContainsNumber(" + this.existingPhoneNumberInContactsSpaces + ") is " + resultExistingSpaces);
        Log.d(TAG, "Result of contactBookContainsNumber(" + this.existingPhoneNumberInContactsZeroZero + ") is " + resultExistingZeroZero);
        Log.d(TAG, "Result of contactBookContainsNumber(" + this.existingPhoneNumberInContactsCode + ") is " + resultExistingCode);
        Log.d(TAG, "Result of contactBookContainsNumber(" + this.nonExistingPhoneNumberInContacts + ") is " + resultNonExisting);

        boolean isSuccess = resultExisting && resultExistingIsoCode && resultExistingSpaces && resultExistingCode
                && resultExistingZeroZero && !resultNonExisting;

        Log.d(TAG, "Result: " + (isSuccess ? "Success!" : "Failure!"));
    }

    public void testReadCurrentDevicePhoneNumber() {
        this.setUp();

        String currentDevicePhoneNumber;

        try {
            currentDevicePhoneNumber = this.contactsUtility.readCurrentDevicePhoneNumber();
        } catch (Exception e) {
            Log.d(TAG, "Failure: Exception thrown during reading currentDevicePhoneNumber");
            return;
        }

        Log.d(TAG, "Success! Current device phone number is: " + currentDevicePhoneNumber);
    }

    public void testReadAllContactBookGroupNames() {
        this.setUp();

        Log.d(TAG, "Starting readAllContactBookGroupNames test.");


        List<String> groups = this.contactsUtility.readAllContactBookGroupNames();

        Log.d(TAG, "Group length is " + groups.size());
        String msg = "Group names are: ";
        for (String groupName : groups) {
            msg += groupName + ", ";
        }

        Log.d(TAG, msg);
    }

    public void testReadGroupID() {
        String groupName = "motoresponder";

        this.setUp();

        Log.d(TAG, "Starting testReadGroupID");

        String groupID = this.contactsUtility.getGroupID(groupName);

        Log.d(TAG, "Group id for " + groupName + "=" + groupID);
    }

    public void testGetContactID() {
        String[] phoneNumbersToTest = {"791467855", "+48791467855", "0048791467855", "0791467855"};

        this.setUp();

        Log.d(TAG, "Starting testGetContactID");

        for (String phoneNumber : phoneNumbersToTest) {
            String contactID = this.contactsUtility.getContactID(phoneNumber);
            Log.d(TAG, "contact id for phone number " + phoneNumber + " is " + contactID);
        }
    }

    public void testGetContactName() {
        String[] phoneNumbersToTest = {"791467855", "+48791467855", "0048791467855", "0791467855"};

        this.setUp();

        Log.d(TAG, "Starting testGetContactID");

        for (String phoneNumber : phoneNumbersToTest) {
            String contactDisplayName = this.contactsUtility.getContactDisplayName(phoneNumber);
            Log.d(TAG, "contact display name for phone number " + phoneNumber + " is " + contactDisplayName);
        }
    }

    public void testHasGroupNumber() {
        String PHONE_NUMBER_IN_GROUP = "791 - 467 - 855";
        String GROUP_NAME = "motoresponder";
        String NON_EXISTING_GROUP = "nonexisting group";
        String PHONE_NUMBER_OUTSIDE_GROUP = "666 667 668";

        this.hasGroupNumberSubtest(PHONE_NUMBER_IN_GROUP, GROUP_NAME);
        this.hasGroupNumberSubtest(PHONE_NUMBER_IN_GROUP, NON_EXISTING_GROUP);
        this.hasGroupNumberSubtest(PHONE_NUMBER_OUTSIDE_GROUP, GROUP_NAME);
    }

    public void testReadCurrentMobileCountryCode() {
        this.setUp();

        Log.d(TAG, "Starting test of readCurrentMobileCountryCode");
        Integer mcc = this.contactsUtility.readCurrentMobileCountryCode();

        Log.d(TAG, "Result is: " + String.valueOf(mcc));
    }

    public void testGetAllContacts() {
        this.setUp();

        List<ContactDefinition> allContacts = this.contactsUtility.getAllContacts();

        Log.d(TAG, "tried to fetch all contacts. Fetched " + allContacts.size() + " contacts, first contact data is: number: " + allContacts.get(0).phoneNumber + ", name: " + allContacts.get(0).name);
    }

    private void setUp() {
        this.contactsUtility = new ContactsUtility(this.context);
    }

    private void hasGroupNumberSubtest(String phoneNumber, String groupName) {
        this.setUp();

        Log.d(TAG, "Starting testHasGroupNumber");

        boolean has = false;

        try {
            has = this.contactsUtility.hasGroupNumberByGroupName(groupName, phoneNumber);
        } catch (Exception e) {
            Log.d(TAG, "Group with given name '" + groupName + "' not found!");
        }

        Log.d(TAG, "Group '" + groupName + (has ? "" : " not") + "' has number " + phoneNumber + " = " + has);
    }

}
