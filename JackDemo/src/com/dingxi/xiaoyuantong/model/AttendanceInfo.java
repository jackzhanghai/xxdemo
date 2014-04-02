package com.dingxi.xiaoyuantong.model;

import java.util.Date;

import android.provider.BaseColumns;

public class AttendanceInfo {


	public Integer id;
	public String gradeName;
	public Integer fkGradeId;
	public String attTime;
	public Integer fkClassId;
	public Integer direc;
	public Integer fkSchoolId;
	public String className;
	public String fkStudentId;
	public String stuName;
	
	
	
	
	 public static abstract class AttendanceInfoEntry implements BaseColumns {
	        public static final String TABLE_NAME = "campus_notice";
	        public static final String COLUMN_NAME_ENTRY_ID = "aid";
	        public static final String COLUMN_NAME_GRADE_NAME = "gradeName";
	        public static final String COLUMN_NAME_GRADE_ID = "fkGradeId";
	        public static final String COLUMN_NAME_ATT_TIME = "attTime";
	        public static final String COLUMN_NAME_CLASS_ID = "fkClassId";
	        public static final String COLUMN_NAME_DIREC = "direc";
	        public static final String COLUMN_NAME_SCHOOL_ID = "fkSchoolId";
	        public static final String COLUMN_NAME_CLASS_NAME = "className";
	        public static final String COLUMN_NAME_STUDDENT_ID = "fkStudentId";
	        public static final String COLUMN_NAME_STU_NAME = "stuName";

	        
	        public static final String COLUMN_NAME_NULLABLE = null;
	  
	    }
	
}
