package com.dingxi.xiaoyuantong.db;

import com.dingxi.xiaoyuantong.model.AttendanceInfo.AttendanceInfoEntry;
import com.dingxi.xiaoyuantong.model.InnerMessage.InnerMessageEntry;
import com.dingxi.xiaoyuantong.model.LeaveMessage.LeaveMessageEntry;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class XiaoyuantongDbHelper extends SQLiteOpenHelper {

	// If you change the database schema, you must increment the database
	// version.
	public static final int DATABASE_VERSION = 3;
	public static final String DATABASE_NAME = "xiaoyuantong.db";

	private static final String TAG = "XiaoyuantongDbHelper";

	public XiaoyuantongDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		// db.execSQL(SQL_CREATE_ENTRIES);
		Log.d(TAG, "onCreat()");

		try {
			db.execSQL("CREATE TABLE IF NOT EXISTS campus_notice (id integer primary key autoincrement,cid integer unique, content varchar(600),optTime varchar(60), sendType integer,status varchar(60), "
					+ "smsType integer,className varchar(60),fkSchoolId integer,fkClassId integer,fkGradeId integer,stuName varchar(60),fkStudentId integer,is_read integer)");

			db.execSQL("CREATE TABLE IF NOT EXISTS home_work (id integer primary key autoincrement,hid integer unique, content varchar(600), optTime varchar(60), sendType integer,status varchar(60), "
					+ "smsType integer,className varchar(60),fkSchoolId integer,fkClassId integer,fkSubjectId integer,fkGradeId integer,fkStudentId integer, user_name varchar(60),is_read integer)");
			
			
			db.execSQL("CREATE TABLE IF NOT EXISTS "
					+ AttendanceInfoEntry.TABLE_NAME
					+ " (id integer primary key autoincrement, "
					+ AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID + " integer unique, "
					+ AttendanceInfoEntry.COLUMN_NAME_GRADE_NAME
					+ " varchar(60),"
					+ AttendanceInfoEntry.COLUMN_NAME_GRADE_ID + " integer, "
					+ AttendanceInfoEntry.COLUMN_NAME_ATT_TIME
					+ " varchar(60), "
					+ AttendanceInfoEntry.COLUMN_NAME_CLASS_ID
					+ " integer, " + AttendanceInfoEntry.COLUMN_NAME_DIREC
					+ " integer, "
					+ AttendanceInfoEntry.COLUMN_NAME_SCHOOL_ID
					+ " integer, "
					+ AttendanceInfoEntry.COLUMN_NAME_CLASS_NAME
					+ " varchar(60), "
					+ AttendanceInfoEntry.COLUMN_NAME_STUDDENT_ID
					+ " integer, "
					+ AttendanceInfoEntry.COLUMN_NAME_STU_NAME
					+ " varchar(60))");
			
			
			/*
             public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_SENDER = "sender";
        public static final String COLUMN_NAME_RECEIVER = "receiver";
        public static final String COLUMN_NAME_STUDENT = "student";
        public static final String COLUMN_NAME_DATE = "date";*/

			db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + LeaveMessageEntry.TABLE_NAME
                    + " (id integer primary key autoincrement, "
                    + LeaveMessageEntry.COLUMN_NAME_ENTRY_ID + " varchar(60) unique, "
                    + LeaveMessageEntry.COLUMN_NAME_CONTENT
                    + " varchar(60), "
                    + LeaveMessageEntry.COLUMN_NAME_SENDER
                    + " varchar(60), "
                    + LeaveMessageEntry.COLUMN_NAME_RECEIVER
                    + " varchar(60), "
                    + LeaveMessageEntry.COLUMN_NAME_STUDENT
                    + " varchar(60), "
                    + LeaveMessageEntry.COLUMN_NAME_DATE
                    + " varchar(60),"
                    + LeaveMessageEntry.COLUMN_NAME_IS_READ
                    + " integer)"
			        );
			
			
			db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + InnerMessageEntry.TABLE_NAME
                    + " (id integer primary key autoincrement, "
                    + InnerMessageEntry.COLUMN_NAME_ENTRY_ID + " varchar(60) unique, "
                    + InnerMessageEntry.COLUMN_NAME_CONTENT
                    + " varchar(60), "
                    + InnerMessageEntry.COLUMN_NAME_SENDER
                    + " varchar(60), "
                    + InnerMessageEntry.COLUMN_NAME_RECEIVER
                    + " varchar(60), "
                    + InnerMessageEntry.COLUMN_NAME_STUDENT
                    + " varchar(60), "
                    + InnerMessageEntry.COLUMN_NAME_DATE
                    + " varchar(60),"
                    + InnerMessageEntry.COLUMN_NAME_IS_READ
                    + " integer)"
			        );
			
		} catch (SQLException sQLException) {
			sQLException.printStackTrace();
			Log.e(TAG, sQLException.getMessage());
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		// This database is only a cache for online data, so its upgrade policy
		// is
		// to simply to discard the data and start over
		String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + "campus_notice";
		db.execSQL(SQL_DELETE_ENTRIES);
		SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + "home_work";
		db.execSQL(SQL_DELETE_ENTRIES);
		
		SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + LeaveMessageEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
		
		SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + InnerMessageEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
	}

}
