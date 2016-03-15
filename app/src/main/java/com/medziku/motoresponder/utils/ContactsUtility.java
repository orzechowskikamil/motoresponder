package com.medziku.motoresponder.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import java.util.ArrayList;
import java.util.List;


public class ContactsUtility {

    private Context context;


    public ContactsUtility(Context context) {
        this.context = context;
    }

    public boolean contactBookContainsContact(String phoneNumber) {
        String[] columns = new String[]{ContactsContract.PhoneLookup.NUMBER};

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        // TODO K. Orzechowski: Learn about E164 representation and use it in other places instead of number! Issue #33
        String selection = ContactsContract.PhoneLookup.NORMALIZED_NUMBER + " = ?";
//        String phoneNumberNormalized = this.normalizePhoneNumber(phoneNumber);
        String phoneNumberNormalized = phoneNumber;

        String[] selectionArgs = {phoneNumberNormalized};

        Cursor cursor = this.context.getContentResolver().query(
                uri, columns, selection, selectionArgs, null);

        boolean result = cursor.getCount() > 0;

        cursor.close();
        return result;
    }


    public List<String> readAllContactBookGroupNames() {
        final String[] GROUP_PROJECTION = new String[]{
                ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};

        Cursor cursor = this.context.getContentResolver().query(
                ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, null, null, null);

        List<String> names = new ArrayList<>();

        int titleColumnIndex = cursor.getColumnIndex(ContactsContract.Groups.TITLE);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String groupName = cursor.getString(titleColumnIndex);
                    names.add(groupName);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return names;
    }

    public boolean isGroupContainingContact(String groupName, String phoneNumberOfContact) {
        // TODO fill me Issue #16
        return false;
    }

    /**
     * Reads current SIM card phone number.
     * @return SIM card phone number.
     * @throws UnsupportedOperationException When SIM card doesn't allow reading it's phone number.
     */
    public String readCurrentDevicePhoneNumber() throws UnsupportedOperationException {
        TelephonyManager telephonyManager =
                (TelephonyManager) this.context.getSystemService(Context.TELEPHONY_SERVICE);

        String phoneNumber = telephonyManager.getLine1Number();

        // TODO K. Orzechowski: Issue #71 - phone number sometimes can't be read from api, it must be stored then in app
        if (phoneNumber == null || phoneNumber.equals("")) {
            throw new UnsupportedOperationException("Not capable to read phone number from API on this device");
        }

        return phoneNumber;
    }

    private String normalizePhoneNumber(String phoneNumber) {
        // TODO K. Orzechowski: Get iso country code (48) from locale
        // TODO K. Orzechowski: It require API 21 - do smth with it
        // TODO K. Orzechowski: it does not work, fix it Issue #33

//        return PhoneNumberUtils.formatNumberToE164(phoneNumber, "48");
        return phoneNumber;
    }

}
