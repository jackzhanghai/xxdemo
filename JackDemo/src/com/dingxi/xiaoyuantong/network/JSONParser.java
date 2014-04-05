package com.dingxi.xiaoyuantong.network;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dingxi.xiaoyuantong.model.AttendanceInfo;
import com.dingxi.xiaoyuantong.model.CampusNotice;
import com.dingxi.xiaoyuantong.model.ClassInfo;
import com.dingxi.xiaoyuantong.model.GradeInfo;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo;
import com.dingxi.xiaoyuantong.model.LocationInfo;
import com.dingxi.xiaoyuantong.model.SchoolInfo;
import com.dingxi.xiaoyuantong.model.StudentInfo;
import com.dingxi.xiaoyuantong.model.SubjectInfo;

import android.content.Context;
import android.util.Log;

public class JSONParser {

    public static int jsonToInt(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key))
            return jsonObject.getInt(key);
        return 0;
    }

    public static String jsonToString(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key)) {
            String value = jsonObject.getString(key);
            // jsonObject.isNull(key)没有判断出当natIP为"null"时返回true，所以这里再次判断，如果值为null要返回空字符串
            if (value.equalsIgnoreCase("null") == false) {
                return value;
            }
        }
        return "";
    }

    public static long jsonToLong(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key))
            return jsonObject.getLong(key);
        return 0;
    }

    public static boolean jsonToBoolean(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key))
            return jsonObject.getBoolean(key);
        return false;
    }

    public static JSONArray jsonToArray(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key))
            return jsonObject.getJSONArray(key);
        return new JSONArray();
    }

    public static JSONObject jsonToJSON(JSONObject jsonObject, String key) throws Exception {
        if (!jsonObject.isNull(key))
            return jsonObject.getJSONObject(key);
        return null;
    }

//    public static String toParserError(String json) throws JSONException {
//        JSONObject jsonObj = new JSONObject(json);
//        if (!jsonObj.isNull(ResponseMessage.RESULT_TAG_CODE))
//            return jsonObj.getString(HttpRequest.HUB_TAG_MESSAGE);
//        else
//            return jsonObj.getString(HttpRequest.SERVER_TAG_ERRMSG);
//    }
    
    public static int getIntByTag(String json, String tag) throws JSONException {
		JSONObject jsonObj = new JSONObject(json);
		if (jsonObj.has(tag)) {
			return jsonObj.getInt(tag);
		}
		return -1;
	}
    
    public static String getStringByTag(String json, String tag) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        if(jsonObj.has(tag)){
            return jsonObj.getString(tag);
        }
        return null;
    }

    public static ArrayList<SchoolInfo> toParserSchoolList(String json) throws JSONException {
        JSONObject jsonObj = new JSONObject(json);
        int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);

        ArrayList<SchoolInfo> schoolList = new ArrayList<SchoolInfo>(total);

        if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS)) {
            JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);

                SchoolInfo school = new SchoolInfo();

                school.name = obj.getString("name");
                school.id = obj.getString("id");
                schoolList.add(school);
            }
        }
        return schoolList;
    }

    public static ArrayList<StudentInfo> toParserStudentInfoList(String searchTypeReslut) throws JSONException {
        JSONObject jsonObj = new JSONObject(searchTypeReslut);
        int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);

        ArrayList<StudentInfo> schoolList = new ArrayList<StudentInfo>(total);

        if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS)) {
            JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);

                StudentInfo studentInfo = new StudentInfo();

                studentInfo.stuName = obj.getString("stuName");
                studentInfo.id = obj.getString("id");
                schoolList.add(studentInfo);
            }
        }
        return schoolList;
    }

    public static ArrayList<StudentInfo>  parseChildInfo(String childInfo) throws JSONException {
        // TODO Auto-generated method stub
        JSONObject jsonObj = new JSONObject(childInfo);
        int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);
        ArrayList<StudentInfo> studentList = new ArrayList<StudentInfo>();
        if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS) && total > 0) {
            JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);

                StudentInfo studentInfo = new StudentInfo();

                studentInfo.imei = obj.getString("imei");
                studentInfo.id = obj.getString("id");
                studentInfo.stuName = obj.getString("stuName");
                studentInfo.fkGradeId = obj.getString("fkGradeId");
                studentInfo.fkClassId = obj.getString("fkClassId");
                studentInfo.fkSchoolId = obj.getString("fkSchoolId");
                studentList.add(studentInfo);
            }
        }
		return studentList;

        
    }
    
    
    
    
    public static ArrayList<ClassInfo> toParserCalssInfoList(String searchTypeReslut) throws JSONException {
        JSONObject jsonObj = new JSONObject(searchTypeReslut);
        int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);

        ArrayList<ClassInfo> classInfoList = new ArrayList<ClassInfo>(total);

        if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS)) {
            JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);

                ClassInfo classInfo = new ClassInfo();

                classInfo.name = obj.getString("name");
                classInfo.id = obj.getString("id");
                classInfoList.add(classInfo);
            }
        }
        return classInfoList;
    }

    public static ArrayList<GradeInfo> toParserGradeInfoList(String searchTypeReslut) throws JSONException {
        JSONObject jsonObj = new JSONObject(searchTypeReslut);
        int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);

        ArrayList<GradeInfo> gradeInfoList = new ArrayList<GradeInfo>(total);

        if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS)) {
            JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);

                GradeInfo school = new GradeInfo();

                school.name = obj.getString("name");
                school.id = obj.getString("id");
                gradeInfoList.add(school);
            }
        }
        return gradeInfoList;
    }
    
    public static ArrayList<SubjectInfo> toParserSubjectInfoList(String searchTypeReslut) throws JSONException {
        JSONObject jsonObj = new JSONObject(searchTypeReslut);
        int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);

        ArrayList<SubjectInfo> gradeInfoList = new ArrayList<SubjectInfo>(total);

        if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS)) {
            JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);
            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);

                SubjectInfo school = new SubjectInfo();

                school.name = obj.getString("name");
                school.id = obj.getString("id");
                gradeInfoList.add(school);
            }
        }
        return gradeInfoList;
    }
    
    
    public static ArrayList<HomeWorkInfo> praseHomeWorks(String homeWorksInfo) throws JSONException {
        // TODO Auto-generated method stub
    	ArrayList<HomeWorkInfo> mHomeWorkList = new ArrayList<HomeWorkInfo>();
        JSONObject jsonObj = new JSONObject(homeWorksInfo);
        int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);

        if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS) && total > 0) {
            JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);

            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);
                // {"fkGradeId":"3","optTime":"2014-03-20 13:28:35","fkClassId":"2","status":"","fkSubjectId":0,"smsType":"1",
                // "fkSchoolId":2,"sendType":"1","id":"402881f344dd52b00144dd54f6680001",
                // "content":"我是测试数据","className":"初一年级1班","fkStudentId":"402881f144d8e6200144d90fea740011","stuName":"bbbbb"}
                HomeWorkInfo homeWorkInfo = new HomeWorkInfo();

                homeWorkInfo.fkGradeId = obj.getInt("fkGradeId");
                homeWorkInfo.id = obj.getString("id");
                homeWorkInfo.content = obj.getString("content");
                homeWorkInfo.sendType = obj.getInt("sendType");
                //homeWorkInfo.status = obj.getString("status");
                homeWorkInfo.fkClassId = obj.getInt("fkClassId");
                homeWorkInfo.fkSubjectId = obj.getInt("fkSubjectId");
                homeWorkInfo.smsType = obj.getInt("smsType");
                homeWorkInfo.fkSchoolId = obj.getInt("fkSchoolId");
                homeWorkInfo.className = obj.getString("id");
                homeWorkInfo.fkStudentId = obj.getString("fkStudentId");
                //homeWorkInfo.stuName = obj.getString("stuName");
                homeWorkInfo.optTime = obj.getString("optTime");

                mHomeWorkList.add(homeWorkInfo);
            }
            if (mHomeWorkList.size() > 0) {

            }
        }
		return mHomeWorkList;

    }
    
    
    private void praseHomeWorksss(String homeWorksInfo) {

        JSONObject jsonObj;
        
        try {
            jsonObj = new JSONObject(homeWorksInfo);
            int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);

            if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS) && total > 0) {
                JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);

                ArrayList<HomeWorkInfo> homeWorkInfoList = new ArrayList<HomeWorkInfo>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = (JSONObject) data.get(i);

                    // {"fkGradeId":"3","optTime":"2014-03-20 13:28:35","fkClassId":"2","status":"","fkSubjectId":0,"smsType":"1",
                    // "fkSchoolId":2,"sendType":"1","id":"402881f344dd52b00144dd54f6680001",
                    // "content":"我是测试数据","className":"初一年级1班","fkStudentId":"402881f144d8e6200144d90fea740011","stuName":"bbbbb"}
                    HomeWorkInfo homeWorkInfo = new HomeWorkInfo();

                    homeWorkInfo.content = obj.getString("content");
                    homeWorkInfo.id = obj.getString("id");
                    homeWorkInfo.optTime = obj.getString("optTime");
                    homeWorkInfo.fkGradeId = obj.getInt("fkGradeId");
                    //homeWorkInfo.status = obj.getString("status");
                    homeWorkInfo.fkClassId = obj.getInt("fkClassId");
                    homeWorkInfo.fkSubjectId = obj.getInt("fkSubjectId");
                    homeWorkInfo.smsType = obj.getInt("smsType");
                    homeWorkInfo.fkSchoolId = obj.getInt("fkSchoolId");
                    homeWorkInfo.className = obj.getString("className");
                    homeWorkInfo.sendType = obj.getInt("sendType");
                    homeWorkInfo.fkStudentId = obj.getString("fkStudentId");

                    homeWorkInfoList.add(homeWorkInfo);
                }

              
                
               
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{
        }

    }
    
    
    public static ArrayList<CampusNotice> praseCampusNotice(String messageInfos) throws JSONException {
        // TODO Auto-generated method stub
    	 ArrayList<CampusNotice> campusNoticeList =  new ArrayList<CampusNotice>();
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(messageInfos);
            
            int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);

            if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS) && total > 0) {
                JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);
               
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = (JSONObject) data.get(i);

                    CampusNotice campusNotice = new CampusNotice();
                    campusNotice.content = obj.getString("content");
                    campusNotice.id = obj.getString("id");
                    campusNotice.optTime = obj.getString("optTime");
                    campusNotice.fkGradeId = obj.getInt("fkGradeId");
                    campusNotice.status = obj.getString("status");
                    campusNotice.fkClassId = obj.getInt("fkClassId");
                    campusNotice.smsType = obj.getInt("smsType");
                    campusNotice.fkSchoolId = obj.getInt("fkSchoolId");
                    campusNotice.className = obj.getString("className");
                    campusNotice.sendType = obj.getInt("sendType");                       
                    campusNotice.fkStudentId = obj.getString("fkStudentId");
                   // campusNotice.stuName = obj.getString("stuName");
                    

                    campusNoticeList.add(campusNotice);
                } 
                
            }
        } catch (JSONException e) {
            e.printStackTrace();

            throw e;
           
        } finally{
 
        }
		return campusNoticeList;
        
    }

	public static ArrayList<AttendanceInfo> praseAttendanceInfo(String body) throws JSONException {
		 ArrayList<AttendanceInfo> attendanceInfoList =  new ArrayList<AttendanceInfo>();
	        JSONObject jsonObj;
	        try {
	            jsonObj = new JSONObject(body);
	            
	            int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);

	            if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS) && total > 0) {
	                JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);
	               
	                for (int i = 0; i < data.length(); i++) {
	                    JSONObject obj = (JSONObject) data.get(i);

	                    AttendanceInfo attendanceInfo = new AttendanceInfo();
	          
	                    attendanceInfo.id = obj.getInt("id");
	                    attendanceInfo.gradeName = obj.getString("gradeName");
	                    attendanceInfo.fkGradeId = obj.getInt("fkGradeId");
	                    attendanceInfo.attTime = obj.getString("attTime");
	                    attendanceInfo.fkClassId = obj.getInt("fkClassId");
	                    attendanceInfo.direc = obj.getInt("direc");
	                    attendanceInfo.fkSchoolId = obj.getInt("fkSchoolId");
	                    attendanceInfo.className = obj.getString("className");
	                    attendanceInfo.fkStudentId = obj.getString("fkStudentId");
	                    attendanceInfo.stuName = obj.getString("stuName");
	                   
	                    attendanceInfoList.add(attendanceInfo);
	                } 
	                
	            }
	        } catch (JSONException e) {
	            e.printStackTrace();

	            throw e;
	           
	        } finally{
	 
	        }
			return attendanceInfoList;
	}

	public static ArrayList<LocationInfo> toParserLocationInfo(String locationInfo) throws JSONException {
		// TODO Auto-generated method stub
		ArrayList<LocationInfo> attendanceInfoList =  new ArrayList<LocationInfo>();
        JSONObject jsonObj;
        try {
            jsonObj = new JSONObject(locationInfo);
            
            int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);

            if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS) && total > 0) {
                JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);
               
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = (JSONObject) data.get(i);

                    LocationInfo LocationInfo = new LocationInfo();
                    LocationInfo.lat = obj.getString("lat");
                    LocationInfo.lng = obj.getString("lng");
                    LocationInfo.optTime = obj.getString("optTime");
  
                    attendanceInfoList.add(LocationInfo);
                } 
                
            }
        } catch (JSONException e) {
            e.printStackTrace();

            throw e;
           
        } finally{
 
        }
		return attendanceInfoList;
	}
    
    
//    private void praseMessageInfos(String homeWorksInfo) throws JSONException {
//        // TODO Auto-generated method stub
//        JSONObject jsonObj = new JSONObject(homeWorksInfo);
//        int total = jsonObj.getInt(ResponseMessage.RESULT_TAG_TOTAL);
//
//        if (jsonObj.has(ResponseMessage.RESULT_TAG_DATAS) && total > 0) {
//            JSONArray data = jsonObj.getJSONArray(ResponseMessage.RESULT_TAG_DATAS);
//
//
//            for (int i = 0; i < data.length(); i++) {
//                JSONObject obj = (JSONObject) data.get(i);
//                mCampusNoticeList.clear();
//                // {"fkGradeId":"3","optTime":"2014-03-20 13:28:35","fkClassId":"2","status":"","fkSubjectId":0,"smsType":"1",
//                // "fkSchoolId":2,"sendType":"1","id":"402881f344dd52b00144dd54f6680001",
//                // "content":"我是测试数据","className":"初一年级1班","fkStudentId":"402881f144d8e6200144d90fea740011","stuName":"bbbbb"}
//                CampusNotice campusNotice = new CampusNotice();
//                campusNotice.content = obj.getString("content");
//                campusNotice.id = obj.getString("id");
//                campusNotice.optTime = obj.getString("optTime");
//                campusNotice.fkGradeId = obj.getInt("fkGradeId");
//                campusNotice.status = obj.getString("status");
//                campusNotice.fkClassId = obj.getInt("fkClassId");
//                campusNotice.smsType = obj.getInt("smsType");
//                campusNotice.fkSchoolId = obj.getInt("fkSchoolId");
//                campusNotice.className = obj.getString("className");
//                campusNotice.sendType = obj.getInt("sendType");
//                campusNotice.stuName = obj.getString("stuName");
//                campusNotice.fkStudentId = obj.getString("fkStudentId");
//
//                mCampusNoticeList.add(campusNotice);
//            }
//            if (mCampusNoticeList.size() > 0) {
//
//            }
//        }
//
//    }






}
