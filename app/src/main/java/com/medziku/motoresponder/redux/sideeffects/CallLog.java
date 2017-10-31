package com.medziku.motoresponder.redux.sideeffects;

import android.content.Context;
import android.database.Cursor;
import com.medziku.motoresponder.redux.Actions;
import com.medziku.motoresponder.redux.Store;
import trikita.jedux.Action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CallLog implements SideEffect {

    private Context context;
    private Store store;
    private Runnable unsubscribe;

    public void start(Context context, Store store) {
        this.context = context;
        this.store = store;
        this.unsubscribe = this.store.subscribe(new Runnable() {
            public void run() {
                CallLog.this.onStoreChanged();
            }
        });
    }

    public void stop() {
        this.unsubscribe.run();
    }

    private void onStoreChanged() {
        if (this.store.getState().calls().isCallLogFresh() == false) {
            List<String[]> freshCallLog = this.getFreshCallLog();
            this.store.dispatch(new Action(Actions.Calls.CALL_LOG_UPDATE,freshCallLog));
        }
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
        Cursor cursor = this.context.getContentResolver().query(
                android.provider.CallLog.Calls.CONTENT_URI,
                projection,
                selections,
                selectionArgs,
                sortOrder
        );

        int phoneNumberColIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
        int dateColIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DATE);
        int typeColIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.TYPE);

        List<String[]> callLog = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String phoneNumber = cursor.getString(phoneNumberColIndex);
                String date = cursor.getString(dateColIndex);
                String type = cursor.getString(typeColIndex);
                callLog.add(new String[]{phoneNumber, date, type});
            } while (cursor.moveToNext());
        }
        cursor.close();
        return callLog;
    }
}
