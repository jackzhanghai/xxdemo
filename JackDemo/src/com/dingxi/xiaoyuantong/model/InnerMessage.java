package com.dingxi.xiaoyuantong.model;

import android.provider.BaseColumns;

public class InnerMessage  extends Notice{
    
	//public String content;
    public String sender;
    //public String messageId;
    public String receiver;
    public String student;
    public String date;
    public int isRead;
	
    public static abstract class InnerMessageEntry implements BaseColumns {
    	public static final String TABLE_NAME = "inner_message";
        public static final String COLUMN_NAME_ENTRY_ID = "lid";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_SENDER = "sender";
        public static final String COLUMN_NAME_RECEIVER = "receiver";
        public static final String COLUMN_NAME_STUDENT = "student";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_IS_READ = "is_read";
        public static final String COLUMN_NAME_NULLABLE = null;

    }

}
