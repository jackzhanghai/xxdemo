package com.dingxi.xiaoyuantong;


import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dingxi.xiaoyuantong.dao.HomeWorkDao;
import com.dingxi.xiaoyuantong.model.CampusNotice;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo;
import com.dingxi.xiaoyuantong.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo.HomeWorkEntry;

public class HomeWorkDetailActivity extends Activity {

    private View parentHeader;
    private ImageButton mBackButton;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    //private HomeWorkInfo curretHomeWorkInfo;
    //private HomeWorkDao curretHomeDao;
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

        homeWorkTite = (TextView) findViewById(R.id.homework_header);
        homeWorkDate = (TextView) findViewById(R.id.homework_date);
        homeWorkContent = (TextView) findViewById(R.id.homework_content);
        
        
       String homeWorkID =  getIntent().getStringExtra(HomeWorkEntry.COLUMN_NAME_ENTRY_ID);
       String optTime =  getIntent().getStringExtra(HomeWorkEntry.COLUMN_NAME_OPT_TIME);
       String content = getIntent().getStringExtra(HomeWorkEntry.COLUMN_NAME_CONTENT);
       HomeWorkDao campusNoticeDao = new HomeWorkDao(
               HomeWorkDetailActivity.this);
       
       HomeWorkInfo homeWorkInfo = campusNoticeDao.queryHomeWorkByID(homeWorkID);
       if(homeWorkInfo == null){

           homeWorkInfo =  new HomeWorkInfo();
           homeWorkInfo.id = homeWorkID;
           homeWorkInfo.content = content;
           homeWorkInfo.optTime = optTime;
          HomeWorkDao homeWorkDao = new HomeWorkDao(mXiaoYunTongApplication);
          long insertResult = homeWorkDao.addHomeWork(homeWorkInfo);
          HomePageActivity.homeWorkTotal -= 1;
           Log.i("CampusNoticeDetailActivity", "updateResult " + insertResult);
       }

       homeWorkTite.setText(R.string.home_work);
       homeWorkDate.setText(optTime);
       homeWorkContent.setText(content);
       confirmButton = (Button) findViewById(R.id.confirm_button);
       confirmButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			HomeWorkDetailActivity.this.finish();
		}
	});
       
       
       
       
       
    }


}
