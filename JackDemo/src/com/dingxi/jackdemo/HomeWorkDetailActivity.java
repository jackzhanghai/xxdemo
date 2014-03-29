package com.dingxi.jackdemo;


import com.dingxi.jackdemo.dao.HomeWorkDao;
import com.dingxi.jackdemo.model.HomeWorkInfo;
import com.dingxi.jackdemo.model.UserInfo;
import com.dingxi.jackdemo.model.HomeWorkInfo.HomeWorkEntry;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class HomeWorkDetailActivity extends Activity {

    private View parentHeader;
    private ImageButton mBackButton;
    private static UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private HomeWorkInfo curretHomeWorkInfo;
    private HomeWorkDao curretHomeDao;
    private TextView homeWorkTite;
    private TextView homeWorkDate;
    private TextView homeWorkContent;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work_detail);
        
        parentHeader = findViewById(R.id.main_title_bar);
        mBackButton = (ImageButton) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        
        homeWorkTite = (TextView) findViewById(R.id.homework_header);
        homeWorkDate = (TextView) findViewById(R.id.homework_date);
        homeWorkContent = (TextView) findViewById(R.id.homework_content);
        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();

        curretUserInfo = mXiaoYunTongApplication.userInfo;
        
       String homeWorkID =  getIntent().getStringExtra(HomeWorkEntry.COLUMN_NAME_ENTRY_ID);
       curretHomeDao =  new HomeWorkDao(mXiaoYunTongApplication);
       curretHomeWorkInfo =  curretHomeDao.queryHomeWorkByID(homeWorkID);
       homeWorkTite.setText("家庭作业");
       homeWorkDate.setText(curretHomeWorkInfo.optTime);
       homeWorkContent.setText(curretHomeWorkInfo.content);
       

    }


}
