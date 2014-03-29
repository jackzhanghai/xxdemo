package com.dingxi.jackdemo.db;


import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class XiaoyuantongDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
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
    	
    	try{
    		db.execSQL("CREATE TABLE IF NOT EXISTS campus_notice (id integer primary key autoincrement,cid integer unique, content varchar(600),optTime varchar(60), sendType integer,status varchar(60), "
                    + "smsType integer,className varchar(60),fkSchoolId integer,fkClassId integer,fkGradeId integer,stuName varchar(60),fkStudentId integer,is_read integer)");
            
            
            db.execSQL("CREATE TABLE IF NOT EXISTS home_work (id integer primary key autoincrement,hid integer unique, content varchar(600), optTime varchar(60), sendType integer,status varchar(60), " +
            		"smsType integer,className varchar(60),fkSchoolId integer,fkClassId integer,fkSubjectId integer,fkGradeId integer,fkStudentId integer ,is_read integer)");
            // db.execSQL("CREATE TABLE IF NOT EXISTS AttendanceInfo (id integer primary key autoincrement, na varchar(60), it varchar(60),d varchar(60))");
    	}catch(SQLException sQLException){
    		sQLException.printStackTrace();
    		Log.e(TAG, sQLException.getMessage());
    	}
        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + "campus_notice";
        db.execSQL(SQL_DELETE_ENTRIES);
        SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + "home_work";
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    
    

}
