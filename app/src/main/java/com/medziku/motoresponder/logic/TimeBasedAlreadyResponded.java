package com.medziku.motoresponder.logic;

import java.util.Date;
import java.util.HashMap;

class TimeBasedAlreadyResponded implements AlreadyRespondedInterface {

    private Settings settings;

    public TimeBasedAlreadyResponded(Settings settings) {
        this.settings = settings;
    }

    public boolean isAutoResponsesLimitExceeded(RespondingSubject subject) {
        String phoneNumber = subject.getPhoneNumber();
        int timeLimitMs = subject.getDelayBetweenAutoresponsesMinutes() * 60 * 1000;

        long timestampOfLastResponseMs = this.getTimestampOfLastResponseMs(phoneNumber);
        long respondingWillBeAllowedTimestampMs = timestampOfLastResponseMs + timeLimitMs;
        long currentTimestampMs = new Date().getTime();
        if (currentTimestampMs > respondingWillBeAllowedTimestampMs ) {
            return false;
        }
        return true;
    }

    public boolean isUserRespondedSince(RespondingSubject subject) {
        // it's technically not possible to check if user already responded himself using this method
        throw new UnsupportedOperationException("It's not possible in this case");
    }

    public void rememberAboutAutoResponse(RespondingSubject subject) {
        this.setTimestampOfLastResponse(subject.getPhoneNumber());
    }

    private long getTimestampOfLastResponseMs(String phoneNumber) {
        HashMap<String, Long> timestamps = this.settings.getTimestampsOfLastResponses();

        Long timestampForPhoneNumberSeconds = timestamps.get(phoneNumber);

        if (timestampForPhoneNumberSeconds == null) {
            timestampForPhoneNumberSeconds = (long) 0;
        }
        long timestampForPhoneNumberMiliseconds = timestampForPhoneNumberSeconds * 1000;

        return timestampForPhoneNumberMiliseconds;
    }

    private void setTimestampOfLastResponse(String phoneNumber) {
        String phoneNumberNormalized = PhoneNumbersComparator.normalizeNumber(phoneNumber);
        HashMap<String, Long> timestamps = this.settings.getTimestampsOfLastResponses();

        long timestampSeconds = new Date().getTime() / 1000;

        timestamps.put(phoneNumberNormalized, timestampSeconds);

        this.settings.setTimestampsOfLastResponses(timestamps);
    }
}
