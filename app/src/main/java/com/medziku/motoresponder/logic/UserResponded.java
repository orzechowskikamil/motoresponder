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
        Date current = new Date();

        phoneNumber = "605429570";

        if (this.callsUtility.isOutgoingCallAfterDate(current, phoneNumber)) {
            return true;
        }

        return false;
    }
}
