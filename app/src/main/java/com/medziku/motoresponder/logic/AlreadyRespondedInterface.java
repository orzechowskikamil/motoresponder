package com.medziku.motoresponder.logic;

public interface AlreadyRespondedInterface {

    /**
     * Return true if auto responses limit is exceeded for given number/type of response
     */
    boolean isAutoResponsesLimitExceeded(RespondingSubject subject);

    /**
     * Allow to mark number/type of response as already auto-answered.
     */
    void rememberAboutAutoResponse(RespondingSubject subject);

    /**
     * Return true if user made response himself since arrival of subject until now.
     */
    boolean isUserRespondedSince(RespondingSubject subject) throws UnsupportedOperationException;
}
