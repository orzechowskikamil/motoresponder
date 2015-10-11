package com.medziku.motoresponder.callbacks;

/**
 * Created by medziku on 22.09.15.
 */
public interface SMSReceivedCallback {
    void onSMSReceived(String phoneNumber, String message);
}
