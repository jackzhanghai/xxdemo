package com.dingxi.xiaoyuantong.model;

import android.provider.BaseColumns;

public class InnerMessage {
    
    public static abstract class HomeWorkEntry implements BaseColumns {
        public static final String TABLE_NAME = "home_work";
        public static final String COLUMN_NAME_ENTRY_ID = "hid";
        public static final String COLUMN_NAME_CONTENT = "content";
        public static final String COLUMN_NAME_OPT_TIME = "optTime";
        public static final String COLUMN_NAME_GRADE_ID = "fkGradeId";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_CLASS_ID = "fkClassId";
        public static final String COLUMN_NAME_SUBJECT_ID = "fkSubjectId";
        public static final String COLUMN_NAME_SMS_TYPE = "smsType";
        public static final String COLUMN_NAME_SCHOOL_ID = "fkSchoolId";
        public static final String COLUMN_NAME_CLASS_NAME = "className";
        public static final String COLUMN_NAME_SEND_TYPE = "sendType";
        public static final String COLUMN_NAME_STUDDENT_ID = "fkStudentId";
        public static final String COLUMN_NAME_IS_READ = "is_read";
        public static final String COLUMN_NAME_NULLABLE = null;

    }

}
