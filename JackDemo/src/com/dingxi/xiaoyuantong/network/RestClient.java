package com.dingxi.xiaoyuantong.network;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Log;

public class RestClient {

    public static final String WEB_INTERFACE_LOGIN = "login?";
    public static final String WEB_INTERFACE_GET_ALL_SCHOOL = "getAllSchools";
    private static final String TAG = "RestClient";
    private static String nameSpace = "http://webservice.school.htht.com";// http://webservice.school.htht.com
    // http://webservice.school.htht.com
    //private static String servicePoint = "http://112.90.87.82:8090/phoneService.ws";
   //private static String servicePoint = "http://hcyforget.vicp.cc:9009/M-School/phoneService.ws";
    // http://hcyforget.vicp.cc:9009/M-School/phoneService.ws?wsdl
   //private static String servicePoint = "http://114.215.170.121:80/phoneService.ws";
   private static String servicePoint = "http://114.215.170.121:8080/M-School/phoneService.ws";


    public static String getAllSchool() throws IOException, XmlPullParserException {

        String soapAction = nameSpace + "/getAllSchools";

        SoapObject rpc = new SoapObject(nameSpace, WEB_INTERFACE_GET_ALL_SCHOOL);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;

        AndroidHttpTransportSE transport = new AndroidHttpTransportSE(servicePoint,60000);

        transport.call(soapAction, envelope);

        String result;
        if (envelope.getResponse() != null) {

            SoapObject object = (SoapObject) envelope.bodyIn;
            result = object.getProperty(0).toString();

            Log.i(TAG, "getAllSchool result " + result);
            return result;
        }
        return null;
    }

    // login(String loginName,String password,String fkSchoolId,String fkRoleId)
    public static String login(String loginName, String password, String fkRoleId)
            throws IOException, XmlPullParserException {
        String soapAction = nameSpace + "/login";

        SoapObject rpc = new SoapObject(nameSpace, "login");
        rpc.addProperty("loginName", loginName);
        rpc.addProperty("password", password);
        rpc.addProperty("fkRoleId", fkRoleId);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;

        AndroidHttpTransportSE transport = new AndroidHttpTransportSE(servicePoint,60000);
        

        transport.call(soapAction, envelope);

        String result;
        if (envelope.getResponse() != null) {

            SoapObject object = (SoapObject) envelope.bodyIn;
            result = object.getProperty(0).toString();

            Log.i(TAG, "login result " + result);
            return result;
        }
        return null;

    }

    private static String requestData(Map<String, String> map, String url) throws IOException,
            XmlPullParserException {

        String result = null;
        SoapObject rpc = new SoapObject(nameSpace, url);
        Set<String> keys = map.keySet();

        for (String key : keys) {
            Log.d(TAG, "key " + key);
            Log.d(TAG, "value " + map.get(key));
            rpc.addProperty(key, map.get(key));
        }

        String soapAction = nameSpace + "/" + url;

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;

        HttpTransportSE transport = new HttpTransportSE(servicePoint);

        transport.call(soapAction, envelope);

        if (envelope.getResponse() != null) {

            SoapObject object = (SoapObject) envelope.bodyIn;
            int count = object.getPropertyCount();
            Log.i(TAG, "PropertyCount " + count);
            for (int i = 0; i < count; i++) {

                String property = object.getProperty(i).toString();
                Log.i(TAG, "requestData property " + property);
            }
            result = object.getProperty(0).toString();
        }
        Log.i(TAG, "request " + url + " result " + result);
        return result;
    }

    public static String modifyPwd(String id, String fkRoleId, String ticket, String oldPassword,
            String newPassword) throws IOException, XmlPullParserException {

        String soapAction = nameSpace + "/modifyPwd";

        String result = null;
        SoapObject rpc = new SoapObject(nameSpace, "modifyPwd");
        rpc.addProperty("id", id);
        rpc.addProperty("fkRoleId", fkRoleId);
        rpc.addProperty("ticket", ticket);
        rpc.addProperty("oldPassword", oldPassword);
        rpc.addProperty("newPassword", newPassword);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;

        HttpTransportSE transport = new HttpTransportSE(servicePoint);

        transport.call(soapAction, envelope);

        if (envelope.getResponse() != null) {

            SoapObject object = (SoapObject) envelope.bodyIn;
            int count = object.getPropertyCount();
            Log.i(TAG, "PropertyCount " + count);
            for (int i = 0; i < count; i++) {

                String property = object.getProperty(i).toString();
                Log.i(TAG, "requestData property " + property);
            }
            result = object.getProperty(0).toString();
        }
        Log.i(TAG, "modifyPwd result " + result);
        return result;

    }

    public static String exit(String id, String fkRoleId, String ticket) throws IOException,
            XmlPullParserException {

        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("fkRoleId", fkRoleId);
        map.put("ticket", ticket);

        String response = requestData(map, "exit");
        return response;

    };

    public static String getAllChilds(String id,String ticket)
            throws IOException, XmlPullParserException {

        Log.d(TAG, "getAllChilds()");
        Map<String, String> map = new HashMap<String, String>();
        id.trim();
        map.put("id", id);
        Log.d(TAG, "id " + id);
        ticket.trim();
        map.put("ticket", ticket);
        Log.d(TAG, "ticket " + ticket);

        String response = requestData(map, "getAllChilds");
        Log.i(TAG, "getAllChilds response " + response);
        return response;

    };

    public static String getGpsPosition(String id, String ticket, String imei) throws IOException,
            XmlPullParserException {

        String soapAction = nameSpace + "/getGpsPosition";

        String result = null;
        SoapObject rpc = new SoapObject(nameSpace, "getGpsPosition");
        rpc.addProperty("id", id);
        rpc.addProperty("ticket", ticket);
        rpc.addProperty("imei", imei);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;

        HttpTransportSE transport = new HttpTransportSE(servicePoint);

        transport.call(soapAction, envelope);

        if (envelope.getResponse() != null) {

            SoapObject object = (SoapObject) envelope.bodyIn;
            int count = object.getPropertyCount();
            Log.i(TAG, "PropertyCount " + count);
            for (int i = 0; i < count; i++) {

                String property = object.getProperty(i).toString();
                Log.i(TAG, "requestData property " + property);
            }
            result = object.getProperty(0).toString();
        }
        Log.i(TAG, "getGpsPosition result " + result);
        return result;

    };

    // 学校年级联动
    public static String getGradeInfos(String id, String ticket, String fkSchoolId)
            throws IOException, XmlPullParserException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("fkSchoolId", fkSchoolId);
        map.put("ticket", ticket);

        String response = requestData(map, "getGradeInfos");
        return response;

    };

    // 年级班级联动
    public static String getClassInfos(String id, String ticket, String fkGradeId)
            throws IOException, XmlPullParserException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("fkGradeId", fkGradeId);
        map.put("ticket", ticket);

        String response = requestData(map, "getClassInfos");
        return response;

    };

    // 班级学生联动
    public static String getStudentInfos(String id, String ticket, String fkClassId)
            throws IOException, XmlPullParserException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("fkRoleId", fkClassId);
        map.put("ticket", ticket);
        String response = requestData(map, "getStudentInfos");
        return response;
    }

    // 获取科目
    public static String getSubjectInfoT(String id, String ticket, String fkSchoolId)
            throws IOException, XmlPullParserException {
     
    	 Log.i(TAG, "getSubjectInfoT");
        String soapAction = nameSpace + "/getSubjectInfoT";

        String result = null;
        SoapObject rpc = new SoapObject(nameSpace, "getSubjectInfoT");
        rpc.addProperty("id", id);       
        rpc.addProperty("ticket", ticket);
        rpc.addProperty("fkSchoolId", fkSchoolId);
       

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;

        HttpTransportSE transport = new HttpTransportSE(servicePoint);

        transport.call(soapAction, envelope);

        if (envelope.getResponse() != null) {

            SoapObject object = (SoapObject) envelope.bodyIn;
            int count = object.getPropertyCount();
            Log.i(TAG, "PropertyCount " + count);
            for (int i = 0; i < count; i++) {

                String property = object.getProperty(i).toString();
                Log.i(TAG, "requestData property " + property);
            }
            result = object.getProperty(0).toString();
        }
        Log.i(TAG, "getSubjectInfoT result " + result);
        return result;
    }

    // 获取家庭作业
    public static String getHomeWorks(String id, String ticket, String fkRoleId, String info,
            int page, int rows) throws IOException, XmlPullParserException {

        String soapAction = nameSpace + "/getHomeWorks";

        String result = null;
        SoapObject rpc = new SoapObject(nameSpace, "getHomeWorks");
        rpc.addProperty("id", id);
        rpc.addProperty("ticket", ticket);
        rpc.addProperty("fkRoleId", fkRoleId);
        rpc.addProperty("info", info);
        rpc.addProperty("page", page);//String.valueOf(page)
        rpc.addProperty("rows", rows);//String.valueOf(rows)

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;

        HttpTransportSE transport = new HttpTransportSE(servicePoint);

        transport.call(soapAction, envelope);

        if (envelope.getResponse() != null) {

            SoapObject object = (SoapObject) envelope.bodyIn;
            int count = object.getPropertyCount();
            Log.i(TAG, "PropertyCount " + count);
            for (int i = 0; i < count; i++) {

                String property = object.getProperty(i).toString();
                //Log.i(TAG, "requestData property " + property);
            }
            result = object.getProperty(0).toString();
        }
        Log.i(TAG, "getHomeWorks result " + result);
        return result;
    }

    // 添加家庭作业
    public static String addHomeWorks(String id, String ticket, String info)
            throws IOException, XmlPullParserException {
  
    	Log.i(TAG, "addHomeWorks()");
        String soapAction = nameSpace + "/addHomeWorks";
        String result = null;
        SoapObject rpc = new SoapObject(nameSpace, "addHomeWorks");
        rpc.addProperty("id", id);
        rpc.addProperty("ticket", ticket);
        rpc.addProperty("info", info);


        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;

        HttpTransportSE transport = new HttpTransportSE(servicePoint);

        transport.call(soapAction, envelope);

        if (envelope.getResponse() != null) {

            SoapObject object = (SoapObject) envelope.bodyIn;
            int count = object.getPropertyCount();
            Log.i(TAG, "PropertyCount " + count);
            for (int i = 0; i < count; i++) {

                String property = object.getProperty(i).toString();
               // Log.i(TAG, "requestData property " + property);
            }
            result = object.getProperty(0).toString();
        }
        Log.i(TAG, "addHomeWorks result " + result);
        return result;
    }

    // 获取校园通知
    public static String getMessageInfos(String id, String ticket, String fkRoleId, String info,
            int page, int rows) throws IOException, XmlPullParserException {
        String soapAction = nameSpace + "/getMessageInfos";

        String result = null;
        SoapObject rpc = new SoapObject(nameSpace, "getMessageInfos");
        rpc.addProperty("id", id);
        rpc.addProperty("ticket", ticket);
        rpc.addProperty("fkRoleId", fkRoleId);
        rpc.addProperty("info", info);
        rpc.addProperty("page", page);//String.valueOf(page)
        rpc.addProperty("rows", rows);//String.valueOf(rows)

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;

        HttpTransportSE transport = new HttpTransportSE(servicePoint);

        transport.call(soapAction, envelope);

        if (envelope.getResponse() != null) {

            SoapObject object = (SoapObject) envelope.bodyIn;
            int count = object.getPropertyCount();
            Log.i(TAG, "PropertyCount " + count);
            for (int i = 0; i < count; i++) {

                String property = object.getProperty(i).toString();
            }
            result = object.getProperty(0).toString();
        }
        Log.i(TAG, "getMessageInfos result " + result);
        return result;
    }

    // 添加校园通知
    public static String addMessageInfos(String id, String ticket, String info)
            throws IOException, XmlPullParserException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("ticket", ticket);
        map.put("info", info);
        String response = requestData(map, "addMessageInfos");
        return response;
    }

    // 获取考勤信息
    public static String getAttendanceInfos(String id, String fkRoleId, String ticket, String info,int page,
            int rows, String queryTime) throws IOException, XmlPullParserException {
      
        String soapAction = nameSpace + "/getAttendanceInfos";

        String result = null;
        SoapObject rpc = new SoapObject(nameSpace, "getAttendanceInfos");
        rpc.addProperty("id", id);        
        rpc.addProperty("fkRoleId", fkRoleId);
        rpc.addProperty("ticket", ticket);
        rpc.addProperty("info", info);
        rpc.addProperty("page", page);//String.valueOf(page)
        rpc.addProperty("rows", rows);//String.valueOf(rows)

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

        envelope.bodyOut = rpc;

        HttpTransportSE transport = new HttpTransportSE(servicePoint);

        transport.call(soapAction, envelope);

        if (envelope.getResponse() != null) {

            SoapObject object = (SoapObject) envelope.bodyIn;
            int count = object.getPropertyCount();
            Log.i(TAG, "PropertyCount " + count);
            for (int i = 0; i < count; i++) {

                String property = object.getProperty(i).toString();
            }
            result = object.getProperty(0).toString();
        }
        Log.i(TAG, "getAttendanceInfos result " + result);
        return result;
    }

    // 修改考勤信息
    public static String updateAttendance(String id, String ticket, String info)
            throws IOException, XmlPullParserException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("id", id);
        map.put("ticket", ticket);
        map.put("info", info);
        String response = requestData(map, "updateAttendance");
        return response;
    }

}
