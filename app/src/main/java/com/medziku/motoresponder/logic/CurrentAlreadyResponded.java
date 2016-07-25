package com.medziku.motoresponder.logic;

import com.medziku.motoresponder.utils.CallsUtility;
import com.medziku.motoresponder.utils.SMSUtility;

public class CurrentAlreadyResponded {

    private Settings settings;
    private AlreadyRespondedInterface currentInstance;
    private CallsUtility callsUtility;
    private SMSUtility SMSUtility;

    public CurrentAlreadyResponded(Settings settings, CallsUtility callsUtility, SMSUtility smsUtility) {
        this.settings = settings;
        this.callsUtility = callsUtility;
        SMSUtility = smsUtility;
    }

    public AlreadyRespondedInterface get() {
        if (this.settings.getMethodOfLimitingResponses() == this.settings.METHOD_OF_LIMITING_RESPONSES_TIME_BASED
                && !(this.currentInstance instanceof TimeBasedAlreadyResponded)) {
            this.currentInstance = new TimeBasedAlreadyResponded(this.settings);

        } else if (this.settings.getMethodOfLimitingResponses() == this.settings.METHOD_OF_LIMITING_RESPONSES_AMOUNT_BASED
                && !(this.currentInstance instanceof AmountBasedAlreadyResponded)) {
            this.currentInstance = new AmountBasedAlreadyResponded(this.callsUtility, this.SMSUtility);

        } else if (this.settings.getMethodOfLimitingResponses() == this.settings.METHOD_OF_LIMITING_RESPONSES_NO_LIMITING
                && !(this.currentInstance instanceof NoLimitingAlreadyResponded)) {
            this.currentInstance = new NoLimitingAlreadyResponded();
        }
        return this.currentInstance;
    }

}
