package com.dingxi.jackdemo.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.dingxi.jackdemo.db.XiaoyuantongDbHelper;
import com.dingxi.jackdemo.model.AttendanceInfo;
import com.dingxi.jackdemo.model.AttendanceInfo.AttendanceInfoEntry;
import com.dingxi.jackdemo.model.HomeWorkInfo;

public class AttendanceInfoDao {
	private static final String TAG = "AttendanceInfoDao";

	XiaoyuantongDbHelper mDbHelper;

	Context mContext;

	public AttendanceInfoDao(Context context) {
		mContext = context;
		mDbHelper = new XiaoyuantongDbHelper(mContext);
	}

	public long addAttendanceInfo(AttendanceInfo attendanceInfo) {

		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		// Create a new map of values, where column names are the keys
		ContentValues values = new ContentValues();
		values.put(AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID, attendanceInfo.id);
		values.put(AttendanceInfoEntry.COLUMN_NAME_GRADE_NAME, attendanceInfo.gradeName);
		values.put(AttendanceInfoEntry.COLUMN_NAME_GRADE_ID, attendanceInfo.fkGradeId);
		values.put(AttendanceInfoEntry.COLUMN_NAME_ATT_TIME, attendanceInfo.attTime);
		values.put(AttendanceInfoEntry.COLUMN_NAME_CLASS_ID, attendanceInfo.fkClassId);
		values.put(AttendanceInfoEntry.COLUMN_NAME_DIREC, attendanceInfo.direc);
		values.put(AttendanceInfoEntry.COLUMN_NAME_SCHOOL_ID, attendanceInfo.fkSchoolId);
		values.put(AttendanceInfoEntry.COLUMN_NAME_CLASS_NAME,
				attendanceInfo.className);
		values.put(AttendanceInfoEntry.COLUMN_NAME_STUDDENT_ID,
				attendanceInfo.fkStudentId);
		values.put(AttendanceInfoEntry.COLUMN_NAME_STU_NAME, attendanceInfo.stuName);
	

		long result = db.insert(AttendanceInfoEntry.TABLE_NAME,
				AttendanceInfoEntry.COLUMN_NAME_NULLABLE, values);
		if (db != null) {
			db.close();
		}
		return result;
	}

//	public void queryHomeWorkByDate() {
//		SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//		// Define a projection that specifies which columns from the database
//		// you will actually use after this query.
//		String[] projection = { AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID,
//				AttendanceInfoEntry.COLUMN_NAME_CONTENT,
//				AttendanceInfoEntry.COLUMN_NAME_OPT_TIME };
//		String selection = null;
//		String[] selectionArgs = null;
//		// How you want the results sorted in the resulting Cursor
//		// String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";
//
//		Cursor c = db.query(AttendanceInfoEntry.TABLE_NAME, // The table to
//															// query
//				projection, // The columns to return
//				selection, // The columns for the WHERE clause
//				selectionArgs, // The values for the WHERE clause
//				null, // don't group the rows
//				null, // don't filter by row groups
//				null // The sort order
//				);
//	}

	public AttendanceInfo queryAttendanceInfobyID(String hid) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID,
				AttendanceInfoEntry.COLUMN_NAME_ATT_TIME,
				AttendanceInfoEntry.COLUMN_NAME_DIREC,
				AttendanceInfoEntry.COLUMN_NAME_STU_NAME, AttendanceInfoEntry.COLUMN_NAME_STU_NAME};
		String selection = AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID + " = ?";
		String[] selectionArgs = { hid };
		String sortOrder = AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID + " DESC";
		// How you want the results sorted in the resulting Cursor
		// String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

		Cursor cursor = db.query(AttendanceInfoEntry.TABLE_NAME, // The table to
																	// query
				projection, // The columns to return
				selection, // The columns for the WHERE clause
				selectionArgs, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);

		AttendanceInfo attendanceInfo = null;
		if (cursor != null && cursor.getCount() > 0) {
			attendanceInfo = new AttendanceInfo();
			if (cursor.moveToNext()) {

//				attendanceInfo.id = cursor
//						.getInt(cursor
//								.getColumnIndexOrThrow(AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID));
//				attendanceInfo.content = cursor
//						.getString(cursor
//								.getColumnIndexOrThrow(AttendanceInfoEntry.COLUMN_NAME_CONTENT));
//				attendanceInfo.optTime = cursor
//						.getString(cursor
//								.getColumnIndexOrThrow(AttendanceInfoEntry.COLUMN_NAME_OPT_TIME));
//				attendanceInfo.isRead = cursor
//						.getInt(cursor
//								.getColumnIndexOrThrow(AttendanceInfoEntry.COLUMN_NAME_IS_READ));
			}

		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		if(db!=null ){
			db.close();
		}
		return attendanceInfo;
	}
	
	public ArrayList<AttendanceInfo> queryAttendanceInfobyStudentID(String stuid) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = { AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID,
				AttendanceInfoEntry.COLUMN_NAME_ATT_TIME,
				AttendanceInfoEntry.COLUMN_NAME_DIREC,
				AttendanceInfoEntry.COLUMN_NAME_STU_NAME, AttendanceInfoEntry.COLUMN_NAME_STUDDENT_ID};
		String selection = AttendanceInfoEntry.COLUMN_NAME_STUDDENT_ID + " = ?";
		String[] selectionArgs = { stuid };
		String sortOrder = AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID + " DESC";
		// How you want the results sorted in the resulting Cursor
		// String sortOrder = FeedEntry.COLUMN_NAME_UPDATED + " DESC";

		Cursor cursor = db.query(AttendanceInfoEntry.TABLE_NAME, // The table to
																	// query
				projection, // The columns to return
				selection, // The columns for the WHERE clause
				selectionArgs, // The values for the WHERE clause
				null, // don't group the rows
				null, // don't filter by row groups
				sortOrder // The sort order
				);

		ArrayList<AttendanceInfo>  attendanceInfoList = new ArrayList<AttendanceInfo>();
		if (cursor != null && cursor.getCount() > 0) {
			
			while (cursor.moveToNext()) {
				AttendanceInfo attendanceInfo = new AttendanceInfo();
				
				attendanceInfo.id = cursor
				.getInt(cursor
						.getColumnIndexOrThrow(AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID));
		attendanceInfo.attTime = cursor
				.getString(cursor
						.getColumnIndexOrThrow(AttendanceInfoEntry.COLUMN_NAME_ATT_TIME));
		attendanceInfo.direc = cursor
				.getInt(cursor
						.getColumnIndexOrThrow(AttendanceInfoEntry.COLUMN_NAME_DIREC));
		attendanceInfo.stuName = cursor
				.getString(cursor
						.getColumnIndexOrThrow(AttendanceInfoEntry.COLUMN_NAME_STU_NAME));
		attendanceInfo.fkStudentId = cursor
				.getString(cursor
						.getColumnIndexOrThrow(AttendanceInfoEntry.COLUMN_NAME_STUDDENT_ID));
		
		
		attendanceInfoList.add(attendanceInfo);
			}
			


		}

		if (cursor != null) {
			cursor.close();
			cursor = null;
		}
		
		if(db!=null ){
			db.close();
		}
		return attendanceInfoList;
	}

	private void deleteAttendanceInfo(int rowId) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		// Define 'where' part of query.
		String selection = "hid= ?";
		// Specify arguments in placeholder order.
		String[] selectionArgs = { String.valueOf(rowId) };
		// Issue SQL statement.
		db.delete(AttendanceInfoEntry.TABLE_NAME, selection, selectionArgs);

	}



//	private void updateHomeWork(String cid, int isRead) {
//		SQLiteDatabase db = mDbHelper.getReadableDatabase();
//
//		// New value for one column
//		ContentValues values = new ContentValues();
//		values.put(AttendanceInfoEntry.COLUMN_NAME_IS_READ, isRead);
//
//		// Which row to update, based on the ID
//		String selection = " cid =  ?";
//		String[] selectionArgs = { cid };
//
//		int updateRestult = db.update(AttendanceInfoEntry.TABLE_NAME, values,
//				selection, selectionArgs);
//	};

	public void colseDb() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}
}
