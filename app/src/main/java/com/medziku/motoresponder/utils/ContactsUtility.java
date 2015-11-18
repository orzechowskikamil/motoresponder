package com.medziku.motoresponder.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.List;


public class ContactsUtility {

    private Context context;

    public ContactsUtility(Context context) {
        this.context = context;
    }

    public boolean contactBookContainsContact(String phoneNumber) {
        // TODO K. Orzechowski: just for testing purposes

        this.readAllContactBookGroupNames();

        //String phoneNumber = "777 777 7777";
        String[] columns = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.NORMALIZED_NUMBER,
                ContactsContract.PhoneLookup.NUMBER};

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        // TODO K. Orzechowski: Learn about E164 representation and use it in other places instead of number!
        String selection = ContactsContract.PhoneLookup.NORMALIZED_NUMBER + " = ?";


        String phoneNumberNormalized = this.normalizePhoneNumber(phoneNumber);

        String[] selectionArgs = {phoneNumberNormalized};
        Cursor cursor = this.context.getContentResolver().query(
                uri, columns, selection, selectionArgs, null);

        boolean result = cursor.getCount() > 0;

        cursor.close();
        return result;
    }

    private String normalizePhoneNumber(String phoneNumber) {
        // TODO K. Orzechowski: Get iso country code (48) from locale
        // TODO K. Orzechowski: It require API 21 - do smth with it
        // TODO K. Orzechowski: it does not work, fix it

//        return PhoneNumberUtils.formatNumberToE164(phoneNumber, "48");
        return phoneNumber;
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
}
