package com.dingxi.xiaoyuantong;


import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dingxi.xiaoyuantong.dao.HomeWorkDao;
import com.dingxi.xiaoyuantong.dao.InnerMessageDao;
import com.dingxi.xiaoyuantong.model.InnerMessage;
import com.dingxi.xiaoyuantong.model.InnerMessage.InnerMessageEntry;

public class InnnerMessageDetailActivity extends Activity {

    private View parentHeader;
    private ImageButton mBackButton;
    private XiaoYunTongApplication mXiaoYunTongApplication;
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
        
        
       String homeWorkID =  getIntent().getStringExtra(InnerMessageEntry.COLUMN_NAME_ENTRY_ID);
       String optTime =  getIntent().getStringExtra(InnerMessageEntry.COLUMN_NAME_DATE);
       String content = getIntent().getStringExtra(InnerMessageEntry.COLUMN_NAME_CONTENT);
       InnerMessageDao  campusNoticeDao = new InnerMessageDao (
               InnnerMessageDetailActivity.this);
       
       InnerMessage  homeWorkInfo = campusNoticeDao.queryInnerMessageByID(homeWorkID);
       if(homeWorkInfo == null){

           homeWorkInfo =  new InnerMessage();
           homeWorkInfo.messageId = homeWorkID;
           homeWorkInfo.content = content;
           homeWorkInfo.date = optTime;
          long insertResult = campusNoticeDao.addInnerMessage(homeWorkInfo);
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
			InnnerMessageDetailActivity.this.finish();
		}
	});
       
       
       
       
       
    }


}
