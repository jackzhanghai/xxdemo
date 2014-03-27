package com.dingxi.jackdemo.dao;

import com.dingxi.jackdemo.db.FeedReaderContract.FeedEntry;
import com.dingxi.jackdemo.db.XiaoyuantongDbHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class HomeWorkDao {

    XiaoyuantongDbHelper  mDbHelper;

    Context mContext;
    public HomeWorkDao(Context context) {
        mContext = context;
        mDbHelper = new XiaoyuantongDbHelper(mContext);
    }
    
    
    private void insert(String id, String title, String content) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_NAME_ENTRY_ID, id);
        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
        //values.put(FeedEntry.COLUMN_NAME_CONTENT, content);

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(FeedEntry.TABLE_NAME, FeedEntry.COLUMN_NAME_NULLABLE, values);
    }

    private void query() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { FeedEntry._ID, FeedEntry.COLUMN_NAME_TITLE, FeedEntry.COLUMN_NAME_UPDATED, };
        String selection = null;
        String[] selectionArgs = null;
        // How you want the results sorted in the resulting Cursor
        String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor c = db.query(FeedEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
                );
    }
}
