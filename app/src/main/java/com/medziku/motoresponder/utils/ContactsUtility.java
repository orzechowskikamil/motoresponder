package com.medziku.motoresponder.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class ContactsUtility {

    private Context context;


    public ContactsUtility(Context context) {
        this.context = context;
    }

    public boolean contactBookContainsContact(String phoneNumber) {
        String[] projection = new String[]{ContactsContract.PhoneLookup.NORMALIZED_NUMBER};
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        // it's interesting property because it looks like it accepts number in any format, while always returning
        // when selected number as +<ISO_CODE><PHONE_NUMBER>, so no manual normalization is needed here.
        String selection = ContactsContract.PhoneLookup.NORMALIZED_NUMBER + " = ?";
        String[] selectionArgs = {phoneNumber};

        Cursor cursor = this.context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

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
     *
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

}
