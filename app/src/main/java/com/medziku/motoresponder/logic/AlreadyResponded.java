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

    /**
     * For real usage
     *
     * @param callsUtility
     * @param smsUtility
     */
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

    public int getAmountOfAutomaticalResponsesSinceUserResponded(String phoneNumber) {
        boolean SENT_BY_USER = false;
        boolean SENT_BY_APP = true;

        Date dateOfLastSmsSentByUser = this.smsUtility.getDateOfLastSMSSent(phoneNumber, SENT_BY_USER);

        int amountOfResponsesSentByApplication = this.smsUtility.howManyOutgoingSMSSentAfterDate(dateOfLastSmsSentByUser, phoneNumber, SENT_BY_APP);

        return amountOfResponsesSentByApplication;
    }


    /**
     * Check if user answered given number with SMS or call after application answered automatically
     */
    public boolean isAutomaticalResponseLast(String phoneNumber) {
        Date dateOfLastAutomaticalAnswer = this.getDateOfLastAutomaticalResponse(phoneNumber);

        if (dateOfLastAutomaticalAnswer == null) {
            // if no automatical response before, no problem, user response can't be older than automatical response
            return false;
        }


        return !this.isUserRespondedSince(dateOfLastAutomaticalAnswer, phoneNumber);
    }


    private Date getDateOfLastAutomaticalResponse(String phoneNumber) {
        return this.smsUtility.getDateOfLastSMSSent(phoneNumber, true);
    }

//    private Date getDateOfLastUserResponse(String phoneNumber) {
//        return this.smsUtility.getDateOfLastSMSSent(phoneNumber, false);
//    }
}
