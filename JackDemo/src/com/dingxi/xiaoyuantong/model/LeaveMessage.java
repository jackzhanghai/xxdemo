package com.dingxi.xiaoyuantong.model;

import android.provider.BaseColumns;


//{"content":"dasgdag","sender":"张三4","id":"402880e64603f5eb014603f734a40001","receiver":"张老师","student":"","date":"2014-05-16 15:37:50"},
public class LeaveMessage {

    
    public String content;
    public String sender;
    public String messageId;
    public String receiver;
    public String student;
    public String date;
    public int isRead;
    
    
    public static abstract class LeaveMessageEntry implements BaseColumns {
        public static final String TABLE_NAME = "leave_message";
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
