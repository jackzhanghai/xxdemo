package com.dingxi.xiaoyuantong.model;

import android.provider.BaseColumns;

public class HomeWorkInfo {

    private static final long serialVersionUID = -5452191060956188291L;

    public String id;

    public Integer smsType;// 短信类型

    public Integer sendType;// 发送类型

    public Integer fkSchoolId;// 学校ID

    public Integer fkGradeId;// 年级ID

    public Integer fkClassId;// 班级ID

    public String fkStudentId;// 学生ID

    public Integer fkSubjectId;// 科目

    public String content;// 短信内容

    public String optTime;// 操作时间
    public String sendName;// 操作时间
    // public java.util.Date sendTime;// 发送时间

    public String status;// 作业评分

    public String className;

    public Integer isRead = 0;

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

    // Constructors

    /** default constructor */
    public HomeWorkInfo() {
    }

    /** full constructor */
    public HomeWorkInfo(Integer smsType, Integer sendType, Integer fkSchoolId, Integer fkGradeId,
            Integer fkClassId, String fkStudentId, Integer fkSubjectId, String content,
            String optTime, java.util.Date sendTime, String status, String fkParentId) {
        this.smsType = smsType;
        this.sendType = sendType;
        this.fkSchoolId = fkSchoolId;
        this.fkGradeId = fkGradeId;
        this.fkClassId = fkClassId;
        this.fkStudentId = fkStudentId;
        this.fkSubjectId = fkSubjectId;
        this.content = content;
        this.optTime = optTime;
        // this.sendTime = sendTime;
        // this.fkParentId = fkParentId;
        this.status = status;
    }


}
