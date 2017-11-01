package com.medziku.motoresponder.redux.sideeffects;

import android.database.Cursor;
import com.medziku.motoresponder.redux.Actions;
import com.medziku.motoresponder.redux.sideeffects.base.SubscribedContextSideEffect;
import com.medziku.motoresponder.redux.sideeffects.utils.DbUtils;
import trikita.jedux.Action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallLog extends SubscribedContextSideEffect {

    public void onStoreChanged() {
        if (this.store.getState().calls().isCallLogFresh() == false) {
            List<String[]> freshCallLog = this.getFreshCallLog();
            this.store.dispatch(new Action(Actions.Calls.CALL_LOG_UPDATE, freshCallLog));
        }
    }

    @Override
    protected void afterStart() {
    }

    @Override
    protected void beforeStop() {
    }

    private List<String[]> getFreshCallLog() {
        final int THREE_DAYS = 3 * 24 * 60 * 60 * 1000;
        int timeRange = THREE_DAYS;
        String[] projection = {
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.TYPE
        };
        String selections = android.provider.CallLog.Calls.DATE + ">?";
        String[] selectionArgs = {String.valueOf(new Date().getTime() - timeRange)};
        String sortOrder = android.provider.CallLog.Calls.DATE + " DESC";

        return new DbUtils(this.context).read(
                android.provider.CallLog.Calls.CONTENT_URI,
                projection,
                selections,
                selectionArgs,
                sortOrder);
    }
}
