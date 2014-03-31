package com.dingxi.jackdemo;


import com.dingxi.jackdemo.dao.HomeWorkDao;
import com.dingxi.jackdemo.model.HomeWorkInfo;
import com.dingxi.jackdemo.model.UserInfo;
import com.dingxi.jackdemo.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.jackdemo.model.HomeWorkInfo.HomeWorkEntry;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
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
    private Button confirmButton;
    
    
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
        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();

        curretUserInfo = mXiaoYunTongApplication.userInfo;
        
        
        homeWorkTite = (TextView) findViewById(R.id.homework_header);
        homeWorkDate = (TextView) findViewById(R.id.homework_date);
        homeWorkContent = (TextView) findViewById(R.id.homework_content);
        
        
       String homeWorkID =  getIntent().getStringExtra(HomeWorkEntry.COLUMN_NAME_ENTRY_ID);
       curretHomeDao =  new HomeWorkDao(mXiaoYunTongApplication);
       curretHomeWorkInfo =  curretHomeDao.queryHomeWorkByID(homeWorkID);
       homeWorkTite.setText("家庭作业");
       homeWorkDate.setText(curretHomeWorkInfo.optTime);
       homeWorkContent.setText(curretHomeWorkInfo.content);
       confirmButton = (Button) findViewById(R.id.confirm_button);
       confirmButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			HomeWorkDetailActivity.this.finish();
		}
	});
       
       HomeWorkDao campusNoticeDao = new HomeWorkDao(
    		   HomeWorkDetailActivity.this);
       ContentValues values = new ContentValues();
       values.put(CampusNoticeEntry.COLUMN_NAME_IS_READ, "1");
       
       
       long insertResult = campusNoticeDao
				.updateHomeWorkById(values,homeWorkID);
       
       
    }


}
