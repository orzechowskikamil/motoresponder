package com.medziku.motoresponder.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import com.medziku.motoresponder.callbacks.SMSReceivedCallback;
import com.medziku.motoresponder.callbacks.SendSMSCallback;

import java.util.Date;

public class SMSUtility {

    private Context context;
    private SmsManager sms;

    public SMSUtility(Context context) {
        this.context = context;
        this.sms = SmsManager.getDefault();
    }

    public void sendSMS(String phoneNumber, String message, final SendSMSCallback sendSMSCallback) throws Exception {
        if (phoneNumber == null || phoneNumber.length() == 0) {
            throw new Exception("Phone number empty or zero length");
        }


        PendingIntent sentPI = this.createPendingIntent("SMS_SENT", new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String status = null;

                switch (this.getResultCode()) {
                    case Activity.RESULT_OK:
                        status = "SMS sent";
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
                    sendSMSCallback.onSMSSent(status);
                }


            }
        });


        PendingIntent deliveredPI = this.createPendingIntent("SMS_DELIVERED", new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String status = null;
                switch (this.getResultCode()) {
                    case Activity.RESULT_OK:
                        status = "SMS delivered";
                        break;
                    case Activity.RESULT_CANCELED:
                        status = "SMS not delivered";
                        break;
                }

                if (sendSMSCallback != null) {
                    sendSMSCallback.onSMSDelivered(status);
                }

            }
        });


        this.sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }

    public void listenForSMS(SMSReceivedCallback smsReceivedCallback) {
        this.context.registerReceiver(
                new IncomingSMSReceiver(smsReceivedCallback),
                new IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        );
    }

    private PendingIntent createPendingIntent(String SENT, BroadcastReceiver broadcastReceiver) {
        PendingIntent sentPI = PendingIntent.getBroadcast(this.context, 0, new Intent(SENT), 0);
        this.context.registerReceiver(broadcastReceiver, new IntentFilter(SENT));
        return sentPI;
    }

    private String normalizeNumber(String phoneNumber, String defaultCountryIso) {
        // TODO K. Orzechowski: nothing work...
        // TODO K. Orzechowski: for now , plain number, think about something better
        return phoneNumber;

//        if (Build.VERSION.SDK_INT >= 21) {
//            return PhoneNumberUtils.formatNumberToE164(phoneNumber, defaultCountryIso);
//        } else {
//            return PhoneNumberUtils.formatNumber(phoneNumber);
//        }
    }

    public boolean wasOutgoingSMSSentAfterDate(Date date, String phoneNumber) {
        String[] whichColumns = {Telephony.Sms.ADDRESS};

        String selections = Telephony.Sms.DATE + " > ?";

        String[] selectionArgs = {String.valueOf(date.getTime())};

        String sortOrder = Telephony.Sms.DATE + " DESC";
        Cursor cursor = context.getContentResolver().query(Telephony.Sms.Sent.CONTENT_URI,
                whichColumns, selections, selectionArgs, sortOrder);

        boolean result = false;

        // TODO K. Orzechowski: get country code from locale
        String phoneNumberNormalized = this.normalizeNumber(phoneNumber, "48");
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String sentMsgPhoneNumber = cursor.getString(cursor.getColumnIndex(Telephony.Sms.ADDRESS));
                    String sentMsgPhoneNumberNormalized = this.normalizeNumber(sentMsgPhoneNumber, "48");

                    if (sentMsgPhoneNumberNormalized.equals(phoneNumberNormalized)) {
                        result = true;
                        break;
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return result;
    }


    // TODO K. Orzechowski: please inline it in some spare time because it looks like shit separated from the context.
    private class IncomingSMSReceiver extends BroadcastReceiver {

        private SMSReceivedCallback smsReceivedCallback;

        public IncomingSMSReceiver(SMSReceivedCallback smsReceivedCallback) {
            this.smsReceivedCallback = smsReceivedCallback;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            boolean receivedIntentIsSMS = intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED");

            if (receivedIntentIsSMS) {
                Bundle bundle = intent.getExtras();

                if (bundle != null) {
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        SmsMessage[] smsMessages = null;
                        smsMessages = new SmsMessage[pdus.length];

                        for (int i = 0; i < smsMessages.length; i++) {
                            smsMessages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);

                            String phoneNumber = smsMessages[i].getOriginatingAddress();
                            String message = smsMessages[i].getMessageBody();

                            if (this.smsReceivedCallback != null) {
                                this.smsReceivedCallback.onSMSReceived(phoneNumber, message);
                            }
                        }
                    } catch (Exception e) {
                        Log.d("Exception caught", e.getMessage());
                    }
                }
            }
        }
    }

}
