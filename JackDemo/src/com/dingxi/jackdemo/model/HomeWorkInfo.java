package com.dingxi.jackdemo.model;

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

    //public java.util.Date sendTime;// 发送时间

    public String status;// 作业评分

    public String className;


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
//        this.sendTime = sendTime;
//        this.fkParentId = fkParentId;
        this.status = status;
    }

    // Property accessors

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getSmsType() {
        return this.smsType;
    }

    public void setSmsType(Integer smsType) {
        this.smsType = smsType;
    }

    public Integer getSendType() {
        return this.sendType;
    }

    public void setSendType(Integer sendType) {
        this.sendType = sendType;
    }

    public Integer getFkSchoolId() {
        return this.fkSchoolId;
    }

    public void setFkSchoolId(Integer fkSchoolId) {
        this.fkSchoolId = fkSchoolId;
    }

    public Integer getFkGradeId() {
        return this.fkGradeId;
    }

    public void setFkGradeId(Integer fkGradeId) {
        this.fkGradeId = fkGradeId;
    }

    public Integer getFkClassId() {
        return this.fkClassId;
    }

    public void setFkClassId(Integer fkClassId) {
        this.fkClassId = fkClassId;
    }

    public String getFkStudentId() {
        return this.fkStudentId;
    }

    public void setFkStudentId(String fkStudentId) {
        this.fkStudentId = fkStudentId;
    }

    public Integer getFkSubjectId() {
        return this.fkSubjectId;
    }

    public void setFkSubjectId(Integer fkSubjectId) {
        this.fkSubjectId = fkSubjectId;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOptTime() {
        return this.optTime;
    }

    public void setOptTime(String optTime) {
        this.optTime = optTime;
    }

//    public java.util.Date getSendTime() {
//        return this.sendTime;
//    }
//
//    public void setSendTime(java.util.Date sendTime) {
//        this.sendTime = sendTime;
//    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

//    public String getFkParentId() {
//        return fkParentId;
//    }
//
//    public void setFkParentId(String fkParentId) {
//        this.fkParentId = fkParentId;
//    }

}
