package com.dingxi.jackdemo.dao;

import com.dingxi.jackdemo.db.XiaoyuantongDbHelper;
import com.dingxi.jackdemo.model.HomeWorkInfo;
import com.dingxi.jackdemo.model.HomeWorkInfo.HomeWorkEntry;

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
    
    
    private long addHomeWork(HomeWorkInfo homeWork) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(HomeWorkEntry.COLUMN_NAME_ENTRY_ID, homeWork.id);
        values.put(HomeWorkEntry.COLUMN_NAME_CONTENT, homeWork.content);
        values.put(HomeWorkEntry.COLUMN_NAME_OPT_TIME, homeWork.optTime);
        values.put(HomeWorkEntry.COLUMN_NAME_GRADE_ID, homeWork.fkGradeId);
        values.put(HomeWorkEntry.COLUMN_NAME_STATUS, homeWork.status);
        values.put(HomeWorkEntry.COLUMN_NAME_CLASS_ID, homeWork.fkClassId);
        values.put(HomeWorkEntry.COLUMN_NAME_SUBJECT_ID, homeWork.fkSubjectId);
        values.put(HomeWorkEntry.COLUMN_NAME_SMS_TYPE, homeWork.smsType);
        values.put(HomeWorkEntry.COLUMN_NAME_SCHOOL_ID, homeWork.fkSchoolId);
        values.put(HomeWorkEntry.COLUMN_NAME_CLASS_NAME, homeWork.className);
        values.put(HomeWorkEntry.COLUMN_NAME_SCHOOL_ID, homeWork.sendType);
        values.put(HomeWorkEntry.COLUMN_NAME_STUDDENT_ID, homeWork.fkStudentId);
        

        return db.insert(HomeWorkEntry.TABLE_NAME, HomeWorkEntry.COLUMN_NAME_NULLABLE, values);
    }

    private void queryHomeWorkByDate() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { HomeWorkEntry.COLUMN_NAME_ENTRY_ID, HomeWorkEntry.COLUMN_NAME_CONTENT, HomeWorkEntry.COLUMN_NAME_OPT_TIME };
        String selection = null;
        String[] selectionArgs = null;
        // How you want the results sorted in the resulting Cursor
       // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor c = db.query(HomeWorkEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
                );
    }
    
    private void deleteHomeWork(int rowId){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
     // Define 'where' part of query.
        String selection = "" + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(rowId) };
        // Issue SQL statement.
        db.delete(HomeWorkEntry.TABLE_NAME, selection, selectionArgs);
        
    }
}
