package com.medziku.motoresponder.redux.sideeffects;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import com.medziku.motoresponder.redux.Actions;
import com.medziku.motoresponder.redux.sideeffects.base.SubscribedContextSideEffect;
import com.medziku.motoresponder.redux.sideeffects.utils.DbUtils;
import trikita.jedux.Action;

import java.util.Date;
import java.util.List;

public class MessageLog extends SubscribedContextSideEffect {


    @Override
    public void onStoreChanged() {
        if (this.store.getState().outgoingMessagesLog() == null) {
            this.store.dispatch(new Action(Actions.Messages.OUTGOING_MESSAGES_LOG, this.readOutgoingMessageLog()));
        }
    }

    @Override
    protected void afterStart() {

    }

    @Override
    protected void beforeStop() {

    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected List<String[]> readOutgoingMessageLog() {
        final int THREE_DAYS = 3 * 24 * 60 * 60 * 1000;
        int timeRange = THREE_DAYS;

        String[] projection = {
                Telephony.Sms.DATE,
                Telephony.Sms.ADDRESS,
                Telephony.Sms.CREATOR
        };
        String selection = Telephony.Sms.DATE + ">?";
        String[] selectionArgs = {String.valueOf(new Date().getTime() - timeRange)};
        String sortOrder = Telephony.Sms.DATE + " DESC";
        Uri contentUri = Telephony.Sms.Sent.CONTENT_URI;

        return new DbUtils(this.context).read(
                contentUri,
                projection,
                selection,
                selectionArgs,
                sortOrder);
    }
}