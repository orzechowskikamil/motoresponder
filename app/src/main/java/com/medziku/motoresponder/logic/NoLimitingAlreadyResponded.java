package com.medziku.motoresponder.logic;


public class NoLimitingAlreadyResponded implements AlreadyRespondedInterface {
    @Override
    public boolean isAutoResponsesLimitExceeded(RespondingSubject subject) {
        return false;
    }

    @Override
    public void rememberAboutAutoResponse(RespondingSubject subject) {
        // not needed.
    }

    @Override
    public boolean isUserRespondedSince(RespondingSubject subject)  {
        throw new UnsupportedOperationException("Not supported");
    }
}
