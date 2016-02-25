package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.CallsUtility;
import com.medziku.motoresponder.utils.SMSUtility;

import java.util.Date;

/**
 * Class perform logic checking if user already responded to some number.
 */
public class UserResponded {

    private CallsUtility callsUtility;
    private SMSUtility smsUtility;

    public UserResponded(CallsUtility callsUtility, SMSUtility smsUtility) {
        this.callsUtility = callsUtility;
        this.smsUtility = smsUtility;
    }

    public boolean isUserRespondedSince(Date time, String phoneNumber) {
        if (this.callsUtility.isOutgoingCallAfterDate(time, phoneNumber)) {
            return true;
        }

        if (this.smsUtility.wasOutgoingSMSSentAfterDate(time, phoneNumber)) {
            return true;
        }

        return false;
    }
}
