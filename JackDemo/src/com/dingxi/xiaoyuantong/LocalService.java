/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dingxi.xiaoyuantong;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.dingxi.xiaoyuantong.HomePageActivity.RollTextThread;
import com.dingxi.xiaoyuantong.dao.CampusNoticeDao;
import com.dingxi.xiaoyuantong.dao.HomeWorkDao;
import com.dingxi.xiaoyuantong.dao.InnerMessageDao;
import com.dingxi.xiaoyuantong.dao.LeaveMessageDao;
import com.dingxi.xiaoyuantong.model.CampusNotice;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo;
import com.dingxi.xiaoyuantong.model.InnerMessage;
import com.dingxi.xiaoyuantong.model.LeaveMessage;
import com.dingxi.xiaoyuantong.model.ParentInfo;
import com.dingxi.xiaoyuantong.model.TeacherInfo;
import com.dingxi.xiaoyuantong.model.UserInfo;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.

/**
 * This is an example of implementing an application service that runs locally
 * in the same process as the application.  The {@link LocalServiceActivity.Controller}
 * and {@link LocalServiceActivity.Binding} classes show how to interact with the
 * service.
 *
 * <p>Notice the use of the {@link NotificationManager} when interesting things
 * happen in the service.  This is generally how background services should
 * interact with the user, rather than doing something more disruptive such as
 * calling startActivity().
 */

public class LocalService extends Service {


    public static final String TAG = LocalService.class.getSimpleName();
    // Unique Identification Number for the Notification.
    // We use it on Notification start, and to cancel it.
    private int NOTIFICATION = 1234;
    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private GetAllMessageThread rollTextThread;
    private final IBinder mBinder = new LocalBinder();
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private UserInfo userInfo;
    private boolean isStop = false;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        LocalService getService() {
            return LocalService.this;
        }
    }
    
    @Override
    public void onCreate() {
  
        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();
        userInfo = mXiaoYunTongApplication.userInfo;
       
       
    }

    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	Log.d(TAG, "LocalService start()");
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        
        intent.getStringExtra("");
        rollTextThread = new GetAllMessageThread();
        rollTextThread.start();
        
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
    	Log.d(TAG, "LocalService stop()");
    	isStop = true;
    	if(rollTextThread !=null && rollTextThread.isAlive()){
    		
    		rollTextThread.interrupt();
    	}

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

   

    /**
     * Show a notification while this service is running.
     */
 
    class GetAllMessageThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();

            Thread.currentThread().setName("GetAllMessageThread");
            
            ResponseMessage responseMessage = null;
            StringBuilder info = new StringBuilder();
            info.append("{");

            if (userInfo.roleType == UserInfo.UserType.ROLE_TEACHER) {
             // 老师info�?
                // {"fkSchoolId":"2","fkClassId":"0","fkClassId2":"2",”queryTime�?�?014-03-24”}
                // 家长info:{"fkSchoolId":"2","fkStudentId":"402881f144d911a10144d916319c0002",”queryTime�?�?014-03-24”}
                TeacherInfo teacherInfo = (TeacherInfo) userInfo;
                info.append("\"fkSchoolId\":");
                info.append("\"" + teacherInfo.defalutSchoolId + "\"");
                info.append(",");
                info.append("\"fkClassId\":");
                info.append("\"" + teacherInfo.defalutClassId + "\"");// curretUserInfo.fkClassId
                info.append(",");

                info.append("\"fkClassId2\":");
                info.append("\"\"");
                info.append(",");
            } else if (userInfo.roleType == UserInfo.UserType.ROLE_PARENT) {
             // 老师info�?
                // {"fkSchoolId":"2","fkClassId":"0","fkClassId2":"2",”queryTime�?�?014-03-24”}
                // 家长info:{"fkSchoolId":"2","fkStudentId":"402881f144d911a10144d916319c0002",”queryTime�?�?014-03-24”}
                ParentInfo parentInfo = (ParentInfo) userInfo;
                info.append("\"fkSchoolId\":");
                info.append("\"" + parentInfo.defalutChild.fkSchoolId + "\"");
                info.append(",");
                info.append("\"fkStudentId\":");
                info.append("\"" + parentInfo.defalutChild.id + "\"");
                info.append(",");
            }

            info.append("\"queryTime\":");
            info.append("\"\"");
            info.append("}");

            Log.d(TAG, " HomeWorks info.toString() " + info.toString());
            
            
            while(!isStop){
            	
            	try {
                    Thread.sleep(50000);
                } catch (InterruptedException e) {
                    
                    // TODO Auto-generated catch block
                	
                    e.printStackTrace();
                    break;
                }
                
                try {
                    responseMessage = new ResponseMessage();
                    responseMessage.body = RestClient.getHomeWorks(mXiaoYunTongApplication.userInfo.id,
                            mXiaoYunTongApplication.userInfo.ticket, userInfo.roleType.toString(),
                            info.toString(), 1, 5);
                    responseMessage.praseBody();
                    
                    if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                             ArrayList<HomeWorkInfo> homeWorkInfoList = JSONParser
                                     .praseHomeWorks(responseMessage.body);
                             if (homeWorkInfoList != null && homeWorkInfoList.size() > 0) {
                                 HomeWorkDao homeWorkDao = new HomeWorkDao(mXiaoYunTongApplication);
                                 for (HomeWorkInfo homeWorkInfo : homeWorkInfoList) {
                                     HomeWorkInfo info1  = homeWorkDao.queryHomeWorkByID(homeWorkInfo.id);
                                     Log.i(TAG, "info1 " + info1);
                                     if(info1 == null){
                                         //homeWorkTotal += 1;
                                     }
                                     //Log.i(TAG, "homeWorkTotal " + homeWorkTotal);
//                                     long insertResult = homeWorkDao.addHomeWork(homeWorkInfo);
//                                     Log.i(TAG, "HomeWork insertResult " + insertResult);
                                 }
                                 homeWorkDao.colseDb();
                                 homeWorkDao = null;
                             }
                     }
                    
                    Log.i(TAG, "HomeWorks response  " + responseMessage.body);
                    
                } catch (Exception e) {
                	
                	 e.printStackTrace();
                	Log.e(TAG, "getHomeWorks "+ e.getMessage());
  
                }
               

                responseMessage = null;
                
                try {
                    responseMessage = new ResponseMessage();
                    responseMessage.body = RestClient.getMessageInfos(
                            mXiaoYunTongApplication.userInfo.id,
                            mXiaoYunTongApplication.userInfo.ticket, userInfo.roleType.toString(),
                            info.toString(), 1, 5);
                    Log.i(TAG, "MessageInfos response  " + responseMessage.body);
                    responseMessage.praseBody();
                    if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                            ArrayList<CampusNotice> campusNoticeList = JSONParser
                                    .praseCampusNotice(responseMessage.body);

                            if (campusNoticeList != null && campusNoticeList.size() > 0) {
                                CampusNoticeDao campusNoticeDao = new CampusNoticeDao(mXiaoYunTongApplication);
                                for (CampusNotice campusNotice : campusNoticeList) {                          
                                    CampusNotice campus = campusNoticeDao.queryCampusNoticeByID(campusNotice.id);
                                    Log.i(TAG, "campus " + campus);
                                    if(campus==null){
                                        
                                        //campusNotieTotal += 1;
                                        //campusNoticeLists.add(0, campusNotice);
                                    }
                                    //Log.i(TAG, "campusNotieTotal " + campusNotieTotal);
                                }
                                campusNoticeDao.colseDb();
                                campusNoticeDao = null;
                            };
                    }

                } catch (Exception e) {
                	 e.printStackTrace();
                 	Log.e(TAG, "etMessageInfos "+ e.getMessage());
                }
                
                responseMessage = null;
                
                try {
                    responseMessage = new ResponseMessage();
                    responseMessage.body = RestClient.getLeaveMessages(mXiaoYunTongApplication.userInfo.id, "1", "", "", mXiaoYunTongApplication.userInfo.roleType.toString(), "1", "6");
                    Log.i(TAG, "LeaveMessages response  " + responseMessage.body);
                    responseMessage.praseBody();
                    if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                        ArrayList<LeaveMessage> campusNoticeList = JSONParser
                                .praseLeaveMessage(responseMessage.body);
                        if (campusNoticeList != null && campusNoticeList.size() > 0) {
                            LeaveMessageDao campusNoticeDao = new LeaveMessageDao(mXiaoYunTongApplication);
                            for (LeaveMessage leaveMessage : campusNoticeList) {                          
                                LeaveMessage campus = campusNoticeDao.queryLeaveMessageByID(leaveMessage.messageId);
                                Log.i(TAG, "campus " + campus);
                                if(campus==null){
                                    
                                    //campusNotieTotal += 1;
                                    //campusNoticeLists.add(0, campusNotice);
                                }
                                //Log.i(TAG, "campusNotieTotal " + campusNotieTotal);
                            }
                            campusNoticeDao.colseDb();
                            campusNoticeDao = null;
                        };
                        
                    }
                    
                } catch (Exception e) {
                    
                	 e.printStackTrace();
                 	Log.e(TAG, "getLeaveMessages "+ e.getMessage());
                }
                
                responseMessage = null;
                try {
                    responseMessage = new ResponseMessage();
                    responseMessage.body = RestClient.getInnerMessages(mXiaoYunTongApplication.userInfo.id, "1", "", "", "1", "6");
                    //responseMessage.body = RestClient.getLeaveMessages(mXiaoYunTongApplication.userInfo.id, "1", "", "", mXiaoYunTongApplication.userInfo.roleType.toString(), "1", "6");
                    Log.i(TAG, "InnerMessages response  " + responseMessage.body);
                    responseMessage.praseBody();
                    if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                        ArrayList<InnerMessage> campusNoticeList = JSONParser
                                .praseInnerMessage(responseMessage.body);
                        
                        if (campusNoticeList != null && campusNoticeList.size() > 0) {
                        	InnerMessageDao campusNoticeDao = new InnerMessageDao(mXiaoYunTongApplication);
                            for (InnerMessage campusNotice : campusNoticeList) {                          
                            	InnerMessage campus = campusNoticeDao.queryInnerMessageByID(campusNotice.messageId);
                             
                            	Log.i(TAG, "campus " + campus);
                                if(campus==null){
                                    
                                    //campusNotieTotal += 1;
                                    //campusNoticeLists.add(0, campusNotice);
                                }
                                //Log.i(TAG, "campusNotieTotal " + campusNotieTotal);
                            }
                            campusNoticeDao.colseDb();
                            campusNoticeDao = null;
                        };
                    }
                } catch (Exception e) {
                	 e.printStackTrace();
                  	Log.e(TAG, "getInnerMessages "+ e.getMessage());
                }
            }
                  
            
            
        }
    }
    
    
}

