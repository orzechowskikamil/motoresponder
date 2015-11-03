package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.CallsUtility;

import java.util.Date;

/**
 * Class perform logic checking if user already responded to some number.
 */
public class UserResponded {

    private final CallsUtility callsUtility;

    public UserResponded(CallsUtility callsUtility) {
        this.callsUtility = callsUtility;
    }

    public boolean userAlreadyResponded(String phoneNumber) {
        Date now = new Date();


        if (this.callsUtility.isOutgoingCallAfterDate(now, phoneNumber)) {
            return true;
        }

        return false;
    }
}
