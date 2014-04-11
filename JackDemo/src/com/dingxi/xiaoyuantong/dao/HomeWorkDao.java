package com.dingxi.xiaoyuantong.dao;

import java.util.ArrayList;
import java.util.List;

import com.dingxi.xiaoyuantong.db.XiaoyuantongDbHelper;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo;
import com.dingxi.xiaoyuantong.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo.HomeWorkEntry;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class HomeWorkDao {

    private static final String TAG = "HomeWorkDao";

    XiaoyuantongDbHelper mDbHelper;

    Context mContext;

    public HomeWorkDao(Context context) {
        mContext = context;
        mDbHelper = new XiaoyuantongDbHelper(mContext);
    }

    public long addHomeWork(HomeWorkInfo homeWork) {

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
        values.put(HomeWorkEntry.COLUMN_NAME_IS_READ, homeWork.isRead);

        long result = db.insert(HomeWorkEntry.TABLE_NAME, HomeWorkEntry.COLUMN_NAME_NULLABLE,
                values);
        if (db != null) {
            db.close();
        }
        return result;
    }

    public int updateHomeWorkById(ContentValues values, String rowId) {

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String selection = HomeWorkEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        String[] selectionArgs = { rowId };

        int count = db.update(HomeWorkEntry.TABLE_NAME, values, selection, selectionArgs);

        if (db != null) {
            db.close();
        }
        return count;
    }

    public void queryHomeWorkByDate() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { HomeWorkEntry.COLUMN_NAME_ENTRY_ID,
                HomeWorkEntry.COLUMN_NAME_CONTENT, HomeWorkEntry.COLUMN_NAME_OPT_TIME };
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
        
        if (c != null) {
            c.close();
        }
        if (db != null) {
            db.close();
        }
    }

    public HomeWorkInfo queryHomeWorkByID(String hid) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { HomeWorkEntry.COLUMN_NAME_ENTRY_ID,
                HomeWorkEntry.COLUMN_NAME_CONTENT, HomeWorkEntry.COLUMN_NAME_OPT_TIME,
                HomeWorkEntry.COLUMN_NAME_IS_READ };
        String selection = HomeWorkEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        String[] selectionArgs = { hid };
        String sortOrder = HomeWorkEntry.COLUMN_NAME_ENTRY_ID + " DESC";
        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor cursor = db.query(HomeWorkEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
                );

        HomeWorkInfo homeWorkInfo = null;
        if (cursor != null && cursor.getCount() > 0) {
            homeWorkInfo = new HomeWorkInfo();
            if (cursor.moveToNext()) {

                homeWorkInfo.id = cursor.getString(cursor
                        .getColumnIndexOrThrow(HomeWorkEntry.COLUMN_NAME_ENTRY_ID));
                homeWorkInfo.content = cursor.getString(cursor
                        .getColumnIndexOrThrow(HomeWorkEntry.COLUMN_NAME_CONTENT));
                homeWorkInfo.optTime = cursor.getString(cursor
                        .getColumnIndexOrThrow(HomeWorkEntry.COLUMN_NAME_OPT_TIME));
                homeWorkInfo.isRead = cursor.getInt(cursor
                        .getColumnIndexOrThrow(HomeWorkEntry.COLUMN_NAME_IS_READ));
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

    private void deleteHomeWork(int rowId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define 'where' part of query.
        String selection = "hid= ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(rowId) };
        // Issue SQL statement.
        db.delete(HomeWorkEntry.TABLE_NAME, selection, selectionArgs);

    }

    public int queryReadOrNotReadCount(int isread) {

        int count = 0;
        // select count(distinct subject) from t where grade = 'xxx';

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { HomeWorkEntry.COLUMN_NAME_ENTRY_ID,
                HomeWorkEntry.COLUMN_NAME_CONTENT, HomeWorkEntry.COLUMN_NAME_OPT_TIME };
        String selection = HomeWorkEntry.COLUMN_NAME_IS_READ + " = ?";
        String[] selectionArgs = { String.valueOf(isread) };
        String sortOrder = HomeWorkEntry.COLUMN_NAME_ENTRY_ID + " DESC";
        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor c = db.query(HomeWorkEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
                );

        if (c != null && c.getCount() > 0) {
            count = c.getCount();

        }

        if (c != null) {
            c.close();
        }

        if (db != null) {
            db.close();
        }
        Log.i(TAG, "ReadOrNotReadCount " + count);
        return count;

    }

    public ArrayList<HomeWorkInfo> queryReadOrNotReadCountHomeWork(int isread) {

        // select count(distinct subject) from t where grade = 'xxx';

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { HomeWorkEntry.COLUMN_NAME_ENTRY_ID,
                HomeWorkEntry.COLUMN_NAME_CONTENT, HomeWorkEntry.COLUMN_NAME_OPT_TIME,
                HomeWorkEntry.COLUMN_NAME_IS_READ };
        String selection = HomeWorkEntry.COLUMN_NAME_IS_READ + " = ?";
        String[] selectionArgs = { String.valueOf(isread) };
        String sortOrder = HomeWorkEntry.COLUMN_NAME_ENTRY_ID + " DESC";
        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor cursor = db.query(HomeWorkEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
                );

        ArrayList<HomeWorkInfo> homeWorkInfos = new ArrayList<HomeWorkInfo>();

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                HomeWorkInfo homeWorkInfo = new HomeWorkInfo();
                homeWorkInfo.id = cursor.getString(cursor
                        .getColumnIndexOrThrow(HomeWorkEntry.COLUMN_NAME_ENTRY_ID));
                homeWorkInfo.content = cursor.getString(cursor
                        .getColumnIndexOrThrow(HomeWorkEntry.COLUMN_NAME_CONTENT));
                homeWorkInfo.optTime = cursor.getString(cursor
                        .getColumnIndexOrThrow(HomeWorkEntry.COLUMN_NAME_OPT_TIME));
                homeWorkInfo.isRead = cursor.getInt(cursor
                        .getColumnIndexOrThrow(HomeWorkEntry.COLUMN_NAME_IS_READ));
                homeWorkInfos.add(homeWorkInfo);
            }

        }

        if (db != null) {
            db.close();
        }

        if (cursor != null) {
            cursor.close();
        }

        return homeWorkInfos;

    }

    private void updateHomeWork(String cid, int isRead) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(HomeWorkEntry.COLUMN_NAME_IS_READ, isRead);

        // Which row to update, based on the ID
        String selection = " cid =  ?";
        String[] selectionArgs = { cid };

        int updateRestult = db.update(HomeWorkEntry.TABLE_NAME, values, selection, selectionArgs);
    };

    public void colseDb() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }
}
