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
        boolean resultNonExisting = this.contactsUtility.contactBookContainsContact(this.nonExistingPhoneNumberInContacts);

        Log.d(TAG, "Result of contactBookContainsContact(" + this.existingPhoneNumberInContacts + ") is " + resultExisting);
        Log.d(TAG, "Result of contactBookContainsContact(" + this.nonExistingPhoneNumberInContacts + ") is " + resultNonExisting);

        boolean isSuccess = resultExisting && !resultNonExisting;

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
