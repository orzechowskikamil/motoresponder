package com.medziku.motoresponder.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Telephony;
import android.provider.Telephony.Sms;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import com.google.common.base.Predicate;
import com.medziku.motoresponder.BuildConfig;
import com.medziku.motoresponder.logic.PhoneNumbersComparator;

import java.util.ArrayList;
import java.util.Date;

public class SMSUtility {

    protected VersionDependentSMSAPIs versionDependentSMSAPIs;
    private Context context;
    private SmsManager smsManager;
    private boolean isCurrentlyListening;
    private IncomingSMSReceiver incomingSMSReceiver;

    /**
     * This constructor is used only for mocking and testing
     */
    public SMSUtility() {
    }

    /**
     * This constructor is dedicated for real usage
     *
     * @param context
     */
    public SMSUtility(Context context) {
        this.context = context;
        this.smsManager = SmsManager.getDefault();
        this.versionDependentSMSAPIs = this.isBeforeKitkat() ? new BeforeKitkatAPI() : new AfterKitkatAPI();
    }

    public void sendSMS(String phoneNumber, String message, final Predicate<String> sendSMSCallback) throws RuntimeException {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            throw new RuntimeException("Phone number empty or zero length");
        }

        PendingIntent sentPI = this.createPendingIntent("SMS_SENT", new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String status = null;

                switch (this.getResultCode()) {
                    case Activity.RESULT_OK:
                        status = null;
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        status = "Generic failure";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        status = "No service";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        status = "Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        status = "Radio off";
                        break;
                }

                if (sendSMSCallback != null) {
                    sendSMSCallback.apply(status);
                }
            }
        });

        ArrayList<String> messageParts = this.smsManager.divideMessage(message);

        ArrayList<PendingIntent> sentPIList = new ArrayList<>();
        sentPIList.add(sentPI);

        this.smsManager.sendMultipartTextMessage(phoneNumber, null, messageParts, sentPIList, null);
    }

    /**
     * You can listen only with one smsReceivedCallback. If You wish to listen with another smsReceivedCallback,
     * you need to unregister old with stopListeningForSMS() method.
     *
     * @param smsReceivedCallback
     */
    public void listenForSMS(Predicate<SMSObject> smsReceivedCallback) {
        if (this.isCurrentlyListening == true) {
            return;
        }
        this.isCurrentlyListening = true;


        this.incomingSMSReceiver = new IncomingSMSReceiver(smsReceivedCallback);
        this.context.registerReceiver(this.incomingSMSReceiver, new IntentFilter(Sms.Intents.SMS_RECEIVED_ACTION));
    }

    public void stopListeningForSMS() {
        if (this.isCurrentlyListening == false) {
            return;
        }

        this.isCurrentlyListening = false;

        this.context.unregisterReceiver(this.incomingSMSReceiver);
    }

    /**
     * This method query for last SMS sent to given phone number.
     *
     * @param phoneNumber          phone number which should receive message
     * @param shouldBeSentByOurApp If true, method will look for messages sent by our app, while if false, it will look for
     *                             messages sent by user himself.
     */
    public Date getDateOfLastSMSSent(String phoneNumber, boolean shouldBeSentByOurApp) {
        String creator = this.getApplicationPackageName();

        String[] whichColumns = {Sms.DATE, Sms.ADDRESS};
        String selections = Sms.CREATOR + (shouldBeSentByOurApp ? "=" : "!=") + "?";
        String[] selectionArgs = {creator};

        String sortOrder = Sms.DATE + " DESC";
        Cursor cursor = context.getContentResolver().query(this.versionDependentSMSAPIs.getContentUri(),
                whichColumns, selections, selectionArgs, sortOrder);

        Date sentMsgDate = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long millisecondsTimestampOfSentDate = cursor.getLong(cursor.getColumnIndex(Sms.DATE));
                    String sentMsgPhoneNumber = cursor.getString(cursor.getColumnIndex(Sms.ADDRESS));
                    if (this.areNumbersEqual(sentMsgPhoneNumber, phoneNumber)) {
                        sentMsgDate = new Date(millisecondsTimestampOfSentDate);
                        break;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return sentMsgDate;
    }

    /**
     * This method check if outgoing SMS was sent after date.
     * Because it performs normalization on phone number, note that it will query ALL smsManager messages since
     * given date, and then iterate through them in order to find fitting phoneNumber, so do not use this method for
     * date very far away from current
     */
    public int howManyOutgoingSMSSentAfterDate(Date date, String phoneNumber, boolean shouldBeSentByOurApp) {
        String creator = this.getApplicationPackageName();

        String[] whichColumns = {Sms.ADDRESS};

        String selections = Sms.DATE + ">? AND " + Sms.CREATOR + (shouldBeSentByOurApp ? "=" : "!=") + "?";
        String[] selectionArgs = {String.valueOf(date.getTime()), creator};

        String sortOrder = Sms.DATE + " DESC";
        Cursor cursor = context.getContentResolver().query(this.versionDependentSMSAPIs.getContentUri(),
                whichColumns, selections, selectionArgs, sortOrder);

        int result = 0;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String sentMsgPhoneNumber = cursor.getString(cursor.getColumnIndex(Sms.ADDRESS));
                    if (this.areNumbersEqual(phoneNumber, sentMsgPhoneNumber)) {
                        result++;
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return result;
    }

    public boolean wasOutgoingSMSSentAfterDate(Date date, String phoneNumber, boolean shouldBeSentByOurApp) {
        return this.howManyOutgoingSMSSentAfterDate(date, phoneNumber, shouldBeSentByOurApp) > 0;
    }

    private boolean isBeforeKitkat() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }


    private PendingIntent createPendingIntent(String SENT, BroadcastReceiver broadcastReceiver) {
        PendingIntent sentPI = PendingIntent.getBroadcast(this.context, 0, new Intent(SENT), 0);
        this.context.registerReceiver(broadcastReceiver, new IntentFilter(SENT));
        return sentPI;
    }

    private String getApplicationPackageName() {
        return BuildConfig.APPLICATION_ID;
    }

    private boolean areNumbersEqual(String firstPhoneNumber, String secondPhoneNuber) {
        return PhoneNumbersComparator.areNumbersEqual(firstPhoneNumber, secondPhoneNuber);
    }

    interface VersionDependentSMSAPIs {
        Uri getContentUri();

        SmsMessage[] getMessagesFromIntent(Intent intent);
    }

    // TODO K. Orzechowski: please inline it in some spare time because it looks like shit separated from the context. #Issue not needed
    class IncomingSMSReceiver extends BroadcastReceiver {

        private Predicate<SMSObject> smsReceivedCallback;

        public IncomingSMSReceiver(Predicate<SMSObject> smsReceivedCallback) {
            this.smsReceivedCallback = smsReceivedCallback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean receivedIntentIsSMS = intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);

            if (!receivedIntentIsSMS) {
                return;
            }
            Bundle bundle = intent.getExtras();

            if (bundle != null) {
                try {
                    String phoneNumber = null;
                    String multipartMessage = "";

                    SmsMessage[] smsMessages = SMSUtility.this.versionDependentSMSAPIs.getMessagesFromIntent(intent);

                    for (SmsMessage message : smsMessages) {
                        phoneNumber = message.getOriginatingAddress();
                        multipartMessage += message.getMessageBody();

                    }
                    if (this.smsReceivedCallback != null) {
                        this.smsReceivedCallback.apply(new SMSObject(phoneNumber, multipartMessage));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    class BeforeKitkatAPI implements VersionDependentSMSAPIs {

        @Override
        public Uri getContentUri() {
            return Uri.parse("content://sms/sent");
        }

        @Override
        public SmsMessage[] getMessagesFromIntent(Intent intent) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] smsMessages = null;
            if (bundle != null) {
                try {
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    smsMessages = new SmsMessage[pdus.length];

                    for (int i = 0; i < smsMessages.length; i++) {
                        smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                    }
                } catch (Exception e) {

                }

            }
            return smsMessages;
        }
    }

    class AfterKitkatAPI implements VersionDependentSMSAPIs {

        @Override
        public Uri getContentUri() {
            return Sms.Sent.CONTENT_URI;
        }

        @Override
        public SmsMessage[] getMessagesFromIntent(Intent intent) {
            return Telephony.Sms.Intents.getMessagesFromIntent(intent);
        }
    }

}



