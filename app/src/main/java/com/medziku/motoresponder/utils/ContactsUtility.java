package com.medziku.motoresponder.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;

class ContactsUtility {

    private Context context;

    public ContactsUtility(Context context) {
        this.context = context;
    }

    public boolean contactBookContainsContact(String phoneNumber) {

        //String phoneNumber = "777 777 7777";

        String[] columns = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME};

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));

        // TODO K. Orzechowski: Learn about E164 representation and use it in other places instead of number!
        String selection = ContactsContract.PhoneLookup.NORMALIZED_NUMBER + " = ?";

        // TODO K. Orzechowski: it can be not true! (this 48 thing)
        // TODO K. Orzechowski: It require API 21 - do smth with it
        String phoneNumberNormalized = PhoneNumberUtils.formatNumberToE164(phoneNumber, "48");

        String[] selectionArgs = {phoneNumberNormalized};
        Cursor cursor = this.context.getContentResolver().query(
                uri, columns, selection, selectionArgs, null);

        boolean result = cursor.getCount() > 0;

        cursor.close();
        return result;
    }

    public String[] readAllContactBookGroupNames() {
        final String[] GROUP_PROJECTION = new String[]{
                ContactsContract.Groups._ID, ContactsContract.Groups.TITLE};

        Cursor cursor = this.context.getContentResolver().query(
                ContactsContract.Groups.CONTENT_URI, GROUP_PROJECTION, null, null, null);

        cursor.moveToFirst();
        String[] names = new String[cursor.getCount()];

        int titleColumn = cursor.getColumnIndex(ContactsContract.Groups.TITLE);

        int i = 0;
        while (!cursor.isAfterLast()) {
            String groupName = cursor.getString(titleColumn);
            names[i] = groupName;
            i++;
        }

        cursor.close();
        return names;
    }

}
