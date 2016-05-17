package com.medziku.motoresponder.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.GroupMembership;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Groups;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.TelephonyManager;
import com.medziku.motoresponder.logic.PhoneNumbersComparator;

import java.util.ArrayList;
import java.util.List;


public class ContactsUtility {

    private Context context;


    public ContactsUtility(Context context) {
        this.context = context;
    }

    public boolean contactBookContainsNumber(String phoneNumber) {
        String[] projection = new String[]{PhoneLookup.NORMALIZED_NUMBER};
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        // it's interesting property because it looks like it accepts number in any format, while always returning
        // when selected number as +<ISO_CODE><PHONE_NUMBER>, so no manual normalization is needed here.
        String selection = PhoneLookup.NORMALIZED_NUMBER + " = ?";
        String[] selectionArgs = {phoneNumber};

        Cursor cursor = this.context.getContentResolver().query(uri, projection, selection, selectionArgs, null);

        boolean result = cursor.getCount() > 0;

        cursor.close();
        return result;
    }


    public List<String> readAllContactBookGroupNames() {
        List<String> names = new ArrayList<>();

        String[] projection = new String[]{Groups._ID, Groups.TITLE};

        Cursor cursor = this.context.getContentResolver().query(Groups.CONTENT_URI, projection, null, null, null);


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String groupName = cursor.getString(cursor.getColumnIndex(Groups.TITLE));
                    names.add(groupName);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return names;
    }


    public String getGroupID(String groupName) {
        String groupID = null;

        String[] projection = new String[]{Groups._ID};
        String selection = Groups.TITLE + "=?";
        String[] selectionArgs = {groupName};

        Cursor cursor = this.context.getContentResolver().query(Groups.CONTENT_URI, projection, selection, selectionArgs, null);


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    groupID = cursor.getString(cursor.getColumnIndex(Groups._ID));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return groupID;
    }

    public String getContactID(String phoneNumber) {
        String contactID = null;

        phoneNumber = PhoneNumbersComparator.normalizeNumber(phoneNumber);

        String[] projection = {Phone.CONTACT_ID};
        String selection = Phone.NUMBER + "=? OR " + Phone.NORMALIZED_NUMBER + "=?";
        String[] selectionArgs = {phoneNumber, phoneNumber};

        Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI,
                projection, selection, selectionArgs, null);

        if (cursor.moveToFirst()) {
            do {
                contactID = cursor.getString(cursor.getColumnIndex(Phone.CONTACT_ID));

            } while (cursor.moveToNext());
            cursor.close();
        }
        return contactID;
    }


    public String getContactDisplayName(String phoneNumber) {
        String displayName = null;

        phoneNumber = PhoneNumbersComparator.normalizeNumber(phoneNumber);

        String[] projection = {Phone.DISPLAY_NAME};
        String selection = Phone.NUMBER + "=? OR " + Phone.NORMALIZED_NUMBER + "=?";
        String[] selectionArgs = {phoneNumber, phoneNumber};

        Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI,
                projection, selection, selectionArgs, null);

        if (cursor.moveToFirst()) {
            do {
                displayName = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));

            } while (cursor.moveToNext());
            cursor.close();
        }
        return displayName;
    }


    public boolean hasGroupNumberByGroupID(String groupID, String phoneNumber) {
        boolean contains = false;

        String contactID = this.getContactID(phoneNumber);

        if (contactID == null) {
            return false;
        }


        String[] projection = {};
        String selection = GroupMembership.GROUP_ROW_ID + "=? AND " + GroupMembership.CONTACT_ID + "=?";
        String[] selectionArgs = {groupID, contactID};


        Cursor cursor = this.context.getContentResolver().query(ContactsContract.Data.CONTENT_URI,
                projection, selection, selectionArgs, null);

        if (cursor.moveToFirst()) {
            do {
                contains = true;
            } while (cursor.moveToNext());

            cursor.close();
        }

        return contains;
    }

    /**
     * @param groupName
     * @param phoneNumber
     * @return
     * @throws Exception When there is no group with given name
     */
    public boolean hasGroupNumberByGroupName(String groupName, String phoneNumber) throws Exception {
        String groupID = this.getGroupID(groupName);

        if (groupID == null) {
            throw new Exception("There is no group with given name");
        }
        return this.hasGroupNumberByGroupID(groupID, phoneNumber);
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
