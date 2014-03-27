package com.dingxi.jackdemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class HomeWorkDBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME_HOEMWORK = "homework";
    private static final String DATABASE_NAME = null;
    private static final String KEY_WORD = null;
    private static final String KEY_DEFINITION = null;
    private static final String DICTIONARY_TABLE_CREATE =
                "CREATE TABLE " + TABLE_NAME_HOEMWORK + " (" +
                KEY_WORD + " TEXT, " +
                KEY_DEFINITION + " TEXT);";
  

    HomeWorkDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DICTIONARY_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        
    }
}
