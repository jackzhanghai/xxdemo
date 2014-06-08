package com.dingxi.xiaoyuantong.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.dingxi.xiaoyuantong.db.XiaoyuantongDbHelper;
import com.dingxi.xiaoyuantong.model.InnerMessage;
import com.dingxi.xiaoyuantong.model.InnerMessage.InnerMessageEntry;


public class InnerMessageDao {

	XiaoyuantongDbHelper mDbHelper;
    Context mContext;
	public InnerMessageDao(Context context) {
		// TODO Auto-generated constructor stub
		 mContext = context;
	        mDbHelper = new XiaoyuantongDbHelper(mContext);
	}
	
	/*
	public InnerMessage queryInnerMessageByID(String hid) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { InnerMessageEntry.COLUMN_NAME_ENTRY_ID,
        		InnerMessageEntry.COLUMN_NAME_CONTENT, InnerMessageEntry.COLUMN_NAME_DATE,
        		InnerMessageEntry.COLUMN_NAME_IS_READ };
        String selection = InnerMessageEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        String[] selectionArgs = { hid };
       // String sortOrder = InnerMessageEntry.COLUMN_NAME_ENTRY_ID + " DESC";
        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor cursor = db.query(InnerMessageEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null// The sort order  sortOrder
                );

        InnerMessage homeWorkInfo = null;
        if (cursor != null && cursor.getCount() > 0) {
            homeWorkInfo = new InnerMessage();
            if (cursor.moveToNext()) {

                homeWorkInfo.messageId = cursor.getString(cursor
                        .getColumnIndexOrThrow(InnerMessageEntry.COLUMN_NAME_ENTRY_ID));
                homeWorkInfo.content = cursor.getString(cursor
                        .getColumnIndexOrThrow(InnerMessageEntry.COLUMN_NAME_CONTENT));
                homeWorkInfo.date = cursor.getString(cursor
                        .getColumnIndexOrThrow(InnerMessageEntry.COLUMN_NAME_DATE));
                homeWorkInfo.isRead = cursor.getInt(cursor
                        .getColumnIndexOrThrow(InnerMessageEntry.COLUMN_NAME_IS_READ));
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
    */
    
    public void colseDb() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

    public InnerMessage queryInnerMessageByID(String homeWorkID) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { InnerMessageEntry.COLUMN_NAME_ENTRY_ID,
                InnerMessageEntry.COLUMN_NAME_CONTENT, InnerMessageEntry.COLUMN_NAME_DATE,
                InnerMessageEntry.COLUMN_NAME_IS_READ };
        String selection = InnerMessageEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        String[] selectionArgs = { homeWorkID };
        String sortOrder = InnerMessageEntry.COLUMN_NAME_ENTRY_ID + " DESC";
        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor cursor = db.query(InnerMessageEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, //// The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
                );

        InnerMessage homeWorkInfo = null;
        if (cursor != null && cursor.getCount() > 0) {
            homeWorkInfo = new InnerMessage();
            if (cursor.moveToNext()) {

                homeWorkInfo.messageId = cursor.getString(cursor
                        .getColumnIndexOrThrow(InnerMessageEntry.COLUMN_NAME_ENTRY_ID));
                homeWorkInfo.content = cursor.getString(cursor
                        .getColumnIndexOrThrow(InnerMessageEntry.COLUMN_NAME_CONTENT));
                homeWorkInfo.date = cursor.getString(cursor
                        .getColumnIndexOrThrow(InnerMessageEntry.COLUMN_NAME_DATE));
                homeWorkInfo.isRead = cursor.getInt(cursor
                        .getColumnIndexOrThrow(InnerMessageEntry.COLUMN_NAME_IS_READ));
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

    public long addInnerMessage(InnerMessage homeWorkInfo) {
        // TODO Auto-generated method stub

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        
        values.put(InnerMessageEntry.COLUMN_NAME_ENTRY_ID, homeWorkInfo.messageId);
        values.put(InnerMessageEntry.COLUMN_NAME_CONTENT, homeWorkInfo.content);
        values.put(InnerMessageEntry.COLUMN_NAME_SENDER, homeWorkInfo.sender);
        values.put(InnerMessageEntry.COLUMN_NAME_RECEIVER, homeWorkInfo.receiver);
        values.put(InnerMessageEntry.COLUMN_NAME_STUDENT, homeWorkInfo.student);
        values.put(InnerMessageEntry.COLUMN_NAME_DATE, homeWorkInfo.date);
        values.put(InnerMessageEntry.COLUMN_NAME_IS_READ, homeWorkInfo.isRead);

        long result = db.insert(InnerMessageEntry.TABLE_NAME, InnerMessageEntry.COLUMN_NAME_NULLABLE,
                values);
        if (db != null) {
            db.close();
        }
        return result;
    }


}
