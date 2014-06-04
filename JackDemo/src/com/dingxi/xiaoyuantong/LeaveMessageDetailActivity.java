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
import com.dingxi.xiaoyuantong.dao.LeaveMessageDao;
import com.dingxi.xiaoyuantong.model.CampusNotice;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo;
import com.dingxi.xiaoyuantong.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo.HomeWorkEntry;
import com.dingxi.xiaoyuantong.model.LeaveMessage;
import com.dingxi.xiaoyuantong.model.LeaveMessage.LeaveMessageEntry;

public class LeaveMessageDetailActivity extends Activity {

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
        
        
       String homeWorkID =  getIntent().getStringExtra(LeaveMessageEntry.COLUMN_NAME_ENTRY_ID);
       String optTime =  getIntent().getStringExtra(LeaveMessageEntry.COLUMN_NAME_DATE);
       String content = getIntent().getStringExtra(LeaveMessageEntry.COLUMN_NAME_CONTENT);
       LeaveMessageDao  campusNoticeDao = new LeaveMessageDao (
               LeaveMessageDetailActivity.this);
       
       LeaveMessage  homeWorkInfo = campusNoticeDao.queryHomeWorkByID(homeWorkID);
       if(homeWorkInfo == null){

           homeWorkInfo =  new LeaveMessage();
           homeWorkInfo.messageId = homeWorkID;
           homeWorkInfo.content = content;
           homeWorkInfo.date = optTime;
          HomeWorkDao homeWorkDao = new HomeWorkDao(mXiaoYunTongApplication);
          long insertResult = campusNoticeDao.addLeaveMessage(homeWorkInfo);
          HomePageActivity.homeWorkTotal -= 1;
           Log.i("CampusNoticeDetailActivity", "updateResult " + insertResult);
       }

       homeWorkTite.setText(R.string.leave_message);
       homeWorkDate.setText(optTime);
       homeWorkContent.setText(content);
       confirmButton = (Button) findViewById(R.id.confirm_button);
       confirmButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			LeaveMessageDetailActivity.this.finish();
		}
	});
       
       
       
       
       
    }


}
