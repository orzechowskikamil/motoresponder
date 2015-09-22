package com.medziku.motoresponder;

/**
 * Created by medziku on 22.09.15.
 */
public interface SendSMSCallback {
    void onSMSSent(String status);

    void onSMSDelivered(String status);
}
