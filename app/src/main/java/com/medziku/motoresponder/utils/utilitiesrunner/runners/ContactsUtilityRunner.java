package com.medziku.motoresponder.utils.utilitiesrunner.runners;


import android.content.Context;
import android.util.Log;
import com.medziku.motoresponder.utils.ContactsUtility;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ContactsUtilityRunner {

    private static final String TAG = "ContactsUtilityRunner";
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

    public ContactsUtilityRunner(Context context) {
        this.context = context;
    }

    private void setUp() {
        this.contactsUtility = new ContactsUtility(this.context);
    }

    public void testContactBookContainsContact() {
        this.setUp();

        boolean resultExisting = this.contactsUtility.contactBookContainsContact(this.existingPhoneNumberInContacts);
        boolean resultExistingIsoCode = this.contactsUtility.contactBookContainsContact(this.existingPhoneNumberInContactsIsoCode);
        boolean resultExistingSpaces = this.contactsUtility.contactBookContainsContact(this.existingPhoneNumberInContactsSpaces);
        boolean resultExistingCode = this.contactsUtility.contactBookContainsContact(this.existingPhoneNumberInContactsCode);
        boolean resultExistingZeroZero = this.contactsUtility.contactBookContainsContact(this.existingPhoneNumberInContactsZeroZero);
        boolean resultNonExisting = this.contactsUtility.contactBookContainsContact(this.nonExistingPhoneNumberInContacts);


        Log.d(TAG, "Result of contactBookContainsContact(" + this.existingPhoneNumberInContacts + ") is " + resultExisting);
        Log.d(TAG, "Result of contactBookContainsContact(" + this.existingPhoneNumberInContactsIsoCode + ") is " + resultExistingIsoCode);
        Log.d(TAG, "Result of contactBookContainsContact(" + this.existingPhoneNumberInContactsSpaces + ") is " + resultExistingSpaces);
        Log.d(TAG, "Result of contactBookContainsContact(" + this.existingPhoneNumberInContactsZeroZero + ") is " + resultExistingZeroZero);
        Log.d(TAG, "Result of contactBookContainsContact(" + this.existingPhoneNumberInContactsCode + ") is " + resultExistingCode);
        Log.d(TAG, "Result of contactBookContainsContact(" + this.nonExistingPhoneNumberInContacts + ") is " + resultNonExisting);

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


}
