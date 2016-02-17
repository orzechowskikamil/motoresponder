package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.CallsUtility;
import com.medziku.motoresponder.utils.SMSUtility;

import java.util.Date;

/**
 * Class perform logic checking if user already responded to some number.
 */
public class AlreadyResponded {

    private CallsUtility callsUtility;
    private SMSUtility smsUtility;

    public AlreadyResponded(CallsUtility callsUtility, SMSUtility smsUtility) {
        this.callsUtility = callsUtility;
        this.smsUtility = smsUtility;
    }

    /**
     * Return if user responded with call or SMS since given time
     */
    public boolean isUserRespondedSince(Date time, String phoneNumber) {
        if (this.callsUtility.wasOutgoingCallAfterDate(time, phoneNumber)) {
            return true;
        }

        if (this.smsUtility.wasOutgoingSMSSentAfterDate(time, phoneNumber, false)) {
            return true;
        }

        return false;
    }


    /**
     * Check if user answered given number with SMS or call after application answered automatically
     */
    public boolean isUserNotAnsweredSinceLastAutomaticalResponse(String phoneNumber) {
        Date dateOfLastAutomaticalAnswer = this.getDateOfLastAutomaticalResponse(phoneNumber);

        if (dateOfLastAutomaticalAnswer == null) {
            // if no automatical response before, no problem, user response can't be more old than automatical response
            return false;
        }


        return this.isUserRespondedSince(dateOfLastAutomaticalAnswer, phoneNumber);
    }


    private Date getDateOfLastAutomaticalResponse(String phoneNumber) {
        return this.smsUtility.getDateOfLastSMSSent(phoneNumber, true);
    }

    private Date getDateOfLastUserResponse(String phoneNumber) {
        return this.smsUtility.getDateOfLastSMSSent(phoneNumber, false);
    }
}
