package com.dingxi.xiaoyuantong.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dingxi.xiaoyuantong.db.XiaoyuantongDbHelper;
import com.dingxi.xiaoyuantong.model.LeaveMessage;
import com.dingxi.xiaoyuantong.model.LeaveMessage.LeaveMessageEntry;



public class LeaveMessageDao {
    
    private static final String TAG = "LeaveMessageDao";

    XiaoyuantongDbHelper mDbHelper;

    Context mContext;

    public LeaveMessageDao(Context context) {
        mContext = context;
        mDbHelper = new XiaoyuantongDbHelper(mContext);
    }
    
    public LeaveMessage queryLeaveMessageByID(String hid) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { LeaveMessageEntry.COLUMN_NAME_ENTRY_ID,
                LeaveMessageEntry.COLUMN_NAME_CONTENT, LeaveMessageEntry.COLUMN_NAME_DATE,
                LeaveMessageEntry.COLUMN_NAME_IS_READ };
        String selection = LeaveMessageEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        String[] selectionArgs = { hid };
       // String sortOrder = LeaveMessageEntry.COLUMN_NAME_ENTRY_ID + " DESC";
        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor cursor = db.query(LeaveMessageEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null// The sort order  sortOrder
                );

        LeaveMessage homeWorkInfo = null;
        if (cursor != null && cursor.getCount() > 0) {
            homeWorkInfo = new LeaveMessage();
            if (cursor.moveToNext()) {

                homeWorkInfo.messageId = cursor.getString(cursor
                        .getColumnIndexOrThrow(LeaveMessageEntry.COLUMN_NAME_ENTRY_ID));
                homeWorkInfo.content = cursor.getString(cursor
                        .getColumnIndexOrThrow(LeaveMessageEntry.COLUMN_NAME_CONTENT));
                homeWorkInfo.date = cursor.getString(cursor
                        .getColumnIndexOrThrow(LeaveMessageEntry.COLUMN_NAME_DATE));
                homeWorkInfo.isRead = cursor.getInt(cursor
                        .getColumnIndexOrThrow(LeaveMessageEntry.COLUMN_NAME_IS_READ));
            }

        }

        if (cursor != null) {
            cursor.close();
            cursor = null;
        }

        if (db != null) {
            db.close();
        }
        return homeWorkInfo;
    }
    
    
    public void colseDb() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }
}
