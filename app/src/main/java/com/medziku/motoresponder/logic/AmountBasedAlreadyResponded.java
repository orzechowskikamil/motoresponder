package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.CallsUtility;
import com.medziku.motoresponder.utils.SMSUtility;

import java.util.Date;

/**
 * Class perform logic checking if user already responded to some number.
 */
public class AmountBasedAlreadyResponded implements AlreadyRespondedInterface {

    private CallsUtility callsUtility;
    private SMSUtility smsUtility;


    public AmountBasedAlreadyResponded(CallsUtility callsUtility, SMSUtility smsUtility) {
        this.callsUtility = callsUtility;
        this.smsUtility = smsUtility;
    }

    public boolean isAutoResponsesLimitExceeded(RespondingSubject subject) {
        int limitOfAutoResponsesForRespondingSubject = subject.getAmountLimitForAutoresponses();
        int automaticalSMSSent = this.getAmountOfAutomaticalResponsesSinceUserResponded(subject.getPhoneNumber());

        boolean limitExceeded = automaticalSMSSent >= limitOfAutoResponsesForRespondingSubject;

        return limitExceeded;

    }

    public void rememberAboutAutoResponse(RespondingSubject subject) {
        // in this method it's not needed.
    }

    public boolean isUserRespondedSince(RespondingSubject subject) {
        Date time = subject.getDate();
        String phoneNumber = subject.getPhoneNumber();

        if (this.callsUtility.wasOutgoingCallAfterDate(time, phoneNumber)) {
            return true;
        }

        if (this.smsUtility.wasOutgoingSMSSentAfterDate(time, phoneNumber, false)) {
            return true;
        }

        return false;
    }

    private int getAmountOfAutomaticalResponsesSinceUserResponded(String phoneNumber) {
        boolean SENT_BY_USER = false;
        boolean SENT_BY_APP = true;

        Date dateOfLastSmsSentByUser = this.smsUtility.getDateOfLastSMSSent(phoneNumber, SENT_BY_USER);

        if (dateOfLastSmsSentByUser == null) {
            // if dateOfLastSMSSentByUser is null, it means that no responses were sent.
            // so we should check amountOfResponsesSentByApplication since beginning of the universe.
            dateOfLastSmsSentByUser = new Date(0);
        }

        int amountOfResponsesSentByApplication = this.smsUtility.howManyOutgoingSMSSentAfterDate(dateOfLastSmsSentByUser, phoneNumber, SENT_BY_APP);

        return amountOfResponsesSentByApplication;
    }
}
