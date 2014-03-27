package com.dingxi.jackdemo.db;

import com.dingxi.jackdemo.db.FeedReaderContract.FeedEntry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class XiaoyuantongDbHelper extends SQLiteOpenHelper {

    
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "xiaoyuantong.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
        FeedEntry._ID + " INTEGER PRIMARY KEY," +
        FeedEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
        FeedEntry.COLUMN_NAME_TITLE + TEXT_TYPE + COMMA_SEP +
         // Any other options for the CREATE command
        " )";

    private static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
    
    public XiaoyuantongDbHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        // TODO Auto-generated constructor stub
    }


    public XiaoyuantongDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        
       
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        //db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL("CREATE TABLE IF NOT EXISTS CampusNotice (id integer primary key autoincrement, content varchar(60), sendType integer,status varchar(60), smsType integer," +
        		"optTime varchar(60),className varchar(60),fkSchoolId integer,fkClassId integer,fkSubjectId integer,fkGradeId integer,fkStudentId integer)");
        db.execSQL("CREATE TABLE IF NOT EXISTS HomeWorkInfo (id integer primary key autoincrement, content varchar(60), sendType integer,status varchar(60), smsType integer, " +
        		"optTime varchar(60),className varchar(60),stuName varchar(60),fkSchoolId integer,fkClassId integer,fkSubjectId integer,fkGradeId integer,fkStudentId integer)");
        //db.execSQL("CREATE TABLE IF NOT EXISTS AttendanceInfo (id integer primary key autoincrement, na varchar(60), it varchar(60),d varchar(60))");        
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
     // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    
    

}
