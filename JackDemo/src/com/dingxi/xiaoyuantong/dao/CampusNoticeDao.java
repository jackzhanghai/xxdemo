package com.dingxi.xiaoyuantong.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dingxi.xiaoyuantong.db.XiaoyuantongDbHelper;
import com.dingxi.xiaoyuantong.model.CampusNotice;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo;
import com.dingxi.xiaoyuantong.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo.HomeWorkEntry;

public class CampusNoticeDao {

    private static final String TAG = "HomeWorkDao";

    XiaoyuantongDbHelper mDbHelper;

    Context mContext;

    public CampusNoticeDao(Context context) {
        mContext = context;
        mDbHelper = new XiaoyuantongDbHelper(mContext);
    }

    public long addCampusNotice(CampusNotice campusNotice) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(CampusNoticeEntry.COLUMN_NAME_ENTRY_ID, campusNotice.id);
        values.put(CampusNoticeEntry.COLUMN_NAME_CONTENT, campusNotice.content);
        values.put(CampusNoticeEntry.COLUMN_NAME_OPT_TIME, campusNotice.optTime);
        values.put(CampusNoticeEntry.COLUMN_NAME_GRADE_ID, campusNotice.fkGradeId);
        values.put(CampusNoticeEntry.COLUMN_NAME_STATUS, campusNotice.status);
        values.put(CampusNoticeEntry.COLUMN_NAME_CLASS_ID, campusNotice.fkClassId);
        values.put(CampusNoticeEntry.COLUMN_NAME_STU_NAME, campusNotice.stuName);
        values.put(CampusNoticeEntry.COLUMN_NAME_SMS_TYPE, campusNotice.smsType);
        values.put(CampusNoticeEntry.COLUMN_NAME_SCHOOL_ID, campusNotice.fkSchoolId);
        values.put(CampusNoticeEntry.COLUMN_NAME_CLASS_NAME, campusNotice.className);
        values.put(CampusNoticeEntry.COLUMN_NAME_SCHOOL_ID, campusNotice.sendType);
        values.put(CampusNoticeEntry.COLUMN_NAME_STUDDENT_ID, campusNotice.fkStudentId);
        values.put(CampusNoticeEntry.COLUMN_NAME_IS_READ, campusNotice.isRead);

        long result = db.insert(CampusNoticeEntry.TABLE_NAME,
                CampusNoticeEntry.COLUMN_NAME_NULLABLE, values);
        if (db != null) {
            db.close();
        }
        return result;
    }
    
    public int updateCampusNoticeById(ContentValues values,String rowId){
    	
    	 SQLiteDatabase db = mDbHelper.getReadableDatabase();
    	String selection = CampusNoticeEntry.COLUMN_NAME_ENTRY_ID + " = ?";
    	String[] selectionArgs = { String.valueOf(rowId) };

    	int count = db.update(
    			CampusNoticeEntry.TABLE_NAME,
    	    values,
    	    selection,
    	    selectionArgs);
    	
		return count;
    }

    public void queryCampusNoticeByDate() {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { CampusNoticeEntry.COLUMN_NAME_ENTRY_ID,
                CampusNoticeEntry.COLUMN_NAME_CONTENT, CampusNoticeEntry.COLUMN_NAME_OPT_TIME };
        String selection = null;
        String[] selectionArgs = null;
        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor c = db.query(CampusNoticeEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
                );
    }

    public CampusNotice queryCampusNoticeByID(String cid) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { CampusNoticeEntry.COLUMN_NAME_ENTRY_ID,
                CampusNoticeEntry.COLUMN_NAME_CONTENT, CampusNoticeEntry.COLUMN_NAME_OPT_TIME,
                CampusNoticeEntry.COLUMN_NAME_IS_READ};
        String selection = CampusNoticeEntry.COLUMN_NAME_ENTRY_ID + " = ?";
        String[] selectionArgs = { cid };
        String sortOrder = CampusNoticeEntry.COLUMN_NAME_ENTRY_ID + " DESC";
        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor cursor = db.query(CampusNoticeEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
                );

        CampusNotice campusNotice = null;
        if (cursor != null && cursor.getCount() > 0) {
            campusNotice = new CampusNotice();
            if (cursor.moveToNext()) {

                campusNotice.id = cursor.getString(cursor
                        .getColumnIndexOrThrow(CampusNoticeEntry.COLUMN_NAME_ENTRY_ID));
                campusNotice.content = cursor.getString(cursor
                        .getColumnIndexOrThrow(CampusNoticeEntry.COLUMN_NAME_CONTENT));
                campusNotice.optTime = cursor.getString(cursor
                        .getColumnIndexOrThrow(CampusNoticeEntry.COLUMN_NAME_OPT_TIME));
                campusNotice.isRead = cursor.getInt(cursor
                        .getColumnIndexOrThrow(CampusNoticeEntry.COLUMN_NAME_IS_READ));

            }

        }

        if (cursor != null) {
            cursor.close();
        }

        if (db != null) {
            db.close();
        }
        return campusNotice;
    }

    private void deleteHomeWork(int rowId) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        // Define 'where' part of query.
        String selection = "hid= ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { String.valueOf(rowId) };
        // Issue SQL statement.
        db.delete(CampusNoticeEntry.TABLE_NAME, selection, selectionArgs);

    }

    public int queryReadOrNotReadCount(int isread) {

        int count = 0;
        // select count(distinct subject) from t where grade = 'xxx';

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { CampusNoticeEntry.COLUMN_NAME_ENTRY_ID,
                CampusNoticeEntry.COLUMN_NAME_CONTENT, CampusNoticeEntry.COLUMN_NAME_OPT_TIME };
        String selection = CampusNoticeEntry.COLUMN_NAME_IS_READ + " = ?";
        String[] selectionArgs = { String.valueOf(isread) };
        String sortOrder = CampusNoticeEntry.COLUMN_NAME_ENTRY_ID + " DESC";
        // How you want the results sorted in the resulting Cursor
        // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

        Cursor c = db.query(CampusNoticeEntry.TABLE_NAME, // The table to query
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

    public ArrayList<String> queryNotReadCampusNoticeContents() {

        int count = 0;

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = { CampusNoticeEntry.COLUMN_NAME_ENTRY_ID,
                CampusNoticeEntry.COLUMN_NAME_CONTENT };
        String selection = CampusNoticeEntry.COLUMN_NAME_IS_READ + " = ?";
        String[] selectionArgs = { String.valueOf("0") };
        String sortOrder = CampusNoticeEntry.COLUMN_NAME_ENTRY_ID + " DESC";

        ArrayList<String> campusNoticeContents = new ArrayList<String>();
        Cursor cursor = db.query(CampusNoticeEntry.TABLE_NAME, // The table to query
                projection, // The columns to return
                selection, // The columns for the WHERE clause
                selectionArgs, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                sortOrder // The sort order
                );

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {

                String content = cursor.getString(cursor
                        .getColumnIndexOrThrow(CampusNoticeEntry.COLUMN_NAME_CONTENT));

                if (content != null) {
                    campusNoticeContents.add(content);
                }
            }
        }

        if (cursor != null) {
            cursor.close();
        }
        if (db != null) {
            db.close();
        }
        Log.i(TAG, "ReadOrNotReadCount " + count);
        return campusNoticeContents;

    }

    private void updateCampusNotice(String cid, int isRead) {
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        // New value for one column
        ContentValues values = new ContentValues();
        values.put(CampusNoticeEntry.COLUMN_NAME_IS_READ, isRead);

        // Which row to update, based on the ID
        String selection = " cid =  ?";
        String[] selectionArgs = { cid };

        int updateRestult = db.update(CampusNoticeEntry.TABLE_NAME, values, selection,
                selectionArgs);
    };

    public void colseDb() {
        if (mDbHelper != null) {
            mDbHelper.close();
        }
    }

	public ArrayList<CampusNotice> queryReadOrNotReadCampusNotice(int isread) {


	        SQLiteDatabase db = mDbHelper.getReadableDatabase();

	        // Define a projection that specifies which columns from the database
	        // you will actually use after this query.
	        String[] projection = { CampusNoticeEntry.COLUMN_NAME_ENTRY_ID,
	                CampusNoticeEntry.COLUMN_NAME_CONTENT, CampusNoticeEntry.COLUMN_NAME_OPT_TIME, CampusNoticeEntry.COLUMN_NAME_IS_READ};
	        String selection = CampusNoticeEntry.COLUMN_NAME_IS_READ + " = ?";
	        String[] selectionArgs = { String.valueOf(isread) };
	        String sortOrder = CampusNoticeEntry.COLUMN_NAME_ENTRY_ID + " DESC";
	        // How you want the results sorted in the resulting Cursor
	        // String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

	        Cursor cursor = db.query(CampusNoticeEntry.TABLE_NAME, // The table to query
	                projection, // The columns to return
	                selection, // The columns for the WHERE clause
	                selectionArgs, // The values for the WHERE clause
	                null, // don't group the rows
	                null, // don't filter by row groups
	                sortOrder // The sort order
	                );
	        ArrayList<CampusNotice> campusNotices = new ArrayList<CampusNotice>();
	        if(cursor!=null && cursor.getCount() > 0){
	        	while (cursor.moveToNext()) {
	        		CampusNotice CampusNotice = new CampusNotice();
	        		CampusNotice.id = cursor
							.getString(cursor
									.getColumnIndexOrThrow(CampusNoticeEntry.COLUMN_NAME_ENTRY_ID));
	        		CampusNotice.content = cursor
							.getString(cursor
									.getColumnIndexOrThrow(CampusNoticeEntry.COLUMN_NAME_CONTENT));
	        		CampusNotice.optTime = cursor
							.getString(cursor
									.getColumnIndexOrThrow(CampusNoticeEntry.COLUMN_NAME_OPT_TIME));
	        		CampusNotice.isRead = cursor
							.getInt(cursor
									.getColumnIndexOrThrow(CampusNoticeEntry.COLUMN_NAME_IS_READ));
	        		
	        		
	        		campusNotices.add(CampusNotice);
	        	}
	        }
		return campusNotices;
	}
}
