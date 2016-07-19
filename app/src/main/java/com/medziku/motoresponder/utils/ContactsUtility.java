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
import com.medziku.motoresponder.utils.structures.ContactDefinition;

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

        // Looks like there is no reliable way of selecting all groups on all android devices, because some devices
        // has second group with same title and with zero contacts. To overcome this, I select only groups which
        // have at least one contacts.
        String[] projection = new String[]{Groups._ID, Groups.TITLE, Groups.SUMMARY_COUNT};

        Cursor cursor = this.context.getContentResolver().query(Groups.CONTENT_SUMMARY_URI, projection, null, null, null);

        String debugRow = null;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    // querying SUMMARY_COUNT doesn't work, needs to be filtered in code.
                    int contactsCount = Integer.parseInt(cursor.getString(cursor.getColumnIndex(Groups.SUMMARY_COUNT)));
                    if (contactsCount > 0) {
                        String groupName = cursor.getString(cursor.getColumnIndex(Groups.TITLE));
                        names.add(groupName);
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return names;
    }

    public List<ContactDefinition> getAllContacts() {
        String contactID = null;


        String[] projection = {Phone.DISPLAY_NAME, Phone.NORMALIZED_NUMBER};
        String[] selectionArgs = {};

        Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI, projection, null, selectionArgs, null);
        List<ContactDefinition> contacts = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                ContactDefinition contactDefinition = new ContactDefinition();
                contactDefinition.name = cursor.getString(cursor.getColumnIndex(Phone.DISPLAY_NAME));
                contactDefinition.phoneNumber = cursor.getString(cursor.getColumnIndex(Phone.NORMALIZED_NUMBER));
                contacts.add(contactDefinition);

            } while (cursor.moveToNext());
            cursor.close();
        }
        return contacts;
    }


    public String getGroupID(String groupName) {
        String groupID = null;

        String[] projection = new String[]{Groups._ID};
        String selection = Groups.TITLE + " = ? AND " + Groups.SUMMARY_COUNT + " > 0";
        String[] selectionArgs = {groupName};

        Cursor cursor = this.context.getContentResolver().query(Groups.CONTENT_SUMMARY_URI, projection, selection, selectionArgs, null);


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

        if (phoneNumber == null || phoneNumber.equals("")) {
            throw new UnsupportedOperationException("Not capable to read phone number from API on this device");
        }

        return phoneNumber;
    }

    public boolean isAbleToReadCurrentDeviceNumber() {
        try {
            this.readCurrentDevicePhoneNumber();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * It returns current mcc. Mcc is always unique. For example for Poland it's 260.
     *
     * @return
     */
    public Integer readCurrentMobileCountryCode() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String simOperator = telephonyManager.getSimOperator();
        if (!simOperator.isEmpty()) {
            return context.getResources().getConfiguration().mcc;
        }
        return null;
    }


}

