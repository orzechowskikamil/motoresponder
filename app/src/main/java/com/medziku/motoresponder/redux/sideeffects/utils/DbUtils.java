package com.medziku.motoresponder.redux.sideeffects.utils;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class DbUtils {
    private Context context;

    public DbUtils(Context context) {
        this.context = context;
    }

    public List<String[]> read(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = this.context.getContentResolver().query(
                android.provider.CallLog.Calls.CONTENT_URI,
                projection,
                selection,
                selectionArgs,
                sortOrder
        );

        int fieldsAmount = projection.length;
        int[] indexes = new int[fieldsAmount];

        for (int i = 0; i < fieldsAmount; i++) {
            indexes[i] = cursor.getColumnIndex(projection[i]);
        }

        List<String[]> callLog = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                String[] row = new String[fieldsAmount];

                for (int i = 0, max = fieldsAmount; i < max; i++) {
                    row[i] = cursor.getString(indexes[i]);
                }
                callLog.add(row);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return callLog;
    }
}
