package com.dingxi.xiaoyuantong.model;

import android.provider.BaseColumns;

public class CampusNotice {

    
    public String id;
    public String content;
    public int sendType;
    public String status;   
    public int smsType;
    public String optTime;
    public String className;
    public int fkSchoolId;
    public String fkStudentId;
    public int fkClassId;
    public String stuName;
    public int fkGradeId;
    public int isRead = 0;
    public String userName;


    
    public static abstract class CampusNoticeEntry implements BaseColumns {
        public static final String TABLE_NAME = "campus_notice";
        public static final String COLUMN_NAME_ENTRY_ID = "cid";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_SEND_TYPE = "sendType";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_SMS_TYPE = "smsType";
        public static final String COLUMN_NAME_OPT_TIME = "optTime";
        public static final String COLUMN_NAME_CLASS_NAME = "className";
        public static final String COLUMN_NAME_SCHOOL_ID = "fkSchoolId";
        public static final String COLUMN_NAME_STUDDENT_ID = "fkStudentId";
        public static final String COLUMN_NAME_CLASS_ID = "fkClassId";
        public static final String  COLUMN_NAME_STU_NAME = "stuName";
        public static final String COLUMN_NAME_GRADE_ID = "fkGradeId";
        public static final String COLUMN_NAME_IS_READ = "is_read";
        public static final String COLUMN_NAME_USER_NAME = "user_name";
       
        //public static final String COLUMN_NAME_SUBJECT_ID = "fkSubjectId";

        
        public static final String COLUMN_NAME_NULLABLE = null;
  
    }

}
