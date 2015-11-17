package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.CallsUtility;
import com.medziku.motoresponder.utils.SMSUtility;

import java.util.Date;

/**
 * Checks if user already responded to some number
 */
public class AlreadyResponded {

    private SMSUtility smsUtility;
    private CallsUtility callsUtility;

    public AlreadyResponded(SMSUtility smsUtility, CallsUtility callsUtility) {
        this.smsUtility = smsUtility;
        this.callsUtility = callsUtility;
    }

    public boolean isAlreadyResponded(String phoneNumber) {
        Date now = new Date();
        return this.smsUtility.wasOutgoingSMSSentAfterDate(now, phoneNumber);
    }
}
