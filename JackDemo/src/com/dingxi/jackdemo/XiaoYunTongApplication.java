package com.dingxi.jackdemo;

import com.dingxi.jackdemo.model.StudentInfo;
import com.dingxi.jackdemo.model.UserInfo;

import android.app.Application;

public class XiaoYunTongApplication extends Application {

    
    public UserInfo userInfo;
    public StudentInfo deflutStudentInfo;
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        userInfo =  new UserInfo();
    }
    
    

}
