package com.dingxi.xiaoyuantong;


import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
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
    private Button mReplyButton;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leave_message_detail);
        
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
        final String homeWorkID =  getIntent().getStringExtra(LeaveMessageEntry.COLUMN_NAME_ENTRY_ID);
        String optTime =  getIntent().getStringExtra(LeaveMessageEntry.COLUMN_NAME_DATE);
        String content = getIntent().getStringExtra(LeaveMessageEntry.COLUMN_NAME_CONTENT);
        mReplyButton = (Button) findViewById(R.id.reply_button);
        mReplyButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
            	Intent backIntent = new Intent(LeaveMessageDetailActivity.this,
            			ReplyLeaveMessageDetailActivity.class);
            			backIntent.putExtra(LeaveMessageEntry.COLUMN_NAME_ENTRY_ID, homeWorkID);
				 startActivity(backIntent);
            }
        });
        
        
       LeaveMessageDao  campusNoticeDao = new LeaveMessageDao (
               LeaveMessageDetailActivity.this);
       
       LeaveMessage  homeWorkInfo = campusNoticeDao.queryHomeWorkByID(homeWorkID);
       if(homeWorkInfo == null){

           homeWorkInfo =  new LeaveMessage();
           homeWorkInfo.id = homeWorkID;
           homeWorkInfo.content = content;
           homeWorkInfo.date = optTime;
          long insertResult = campusNoticeDao.addLeaveMessage(homeWorkInfo);
          //HomePageActivity.homeWorkTotal -= 1;
           Log.i("CampusNoticeDetailActivity", "updateResult " + insertResult);
       } else {
    	   

    	   ContentValues values = new ContentValues();
    	   values.put(LeaveMessageEntry.COLUMN_NAME_IS_READ, "1");
    	   campusNoticeDao.updateLeaveMessage(values,homeWorkID);
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
