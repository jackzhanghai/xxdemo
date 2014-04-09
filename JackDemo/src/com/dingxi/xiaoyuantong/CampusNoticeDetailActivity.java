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

import com.dingxi.xiaoyuantong.dao.CampusNoticeDao;
import com.dingxi.xiaoyuantong.model.CampusNotice;
import com.dingxi.xiaoyuantong.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.xiaoyuantong.model.UserInfo;

public class CampusNoticeDetailActivity extends Activity {

    private View parentHeader;
    private ImageButton mBackButton;
    private static UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;

    private TextView noticeTite;
    private TextView noticeDate;
    private TextView noticeContent;
    private Button confirmButton;
 
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campusnotice_detail);
        
        parentHeader = findViewById(R.id.main_title_bar);
        mBackButton = (ImageButton) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        
        noticeTite = (TextView) findViewById(R.id.homework_header);
        noticeDate = (TextView) findViewById(R.id.homework_date);
        noticeContent = (TextView) findViewById(R.id.homework_content);
        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();

        curretUserInfo = mXiaoYunTongApplication.userInfo;
        
       String noticeID =  getIntent().getStringExtra(CampusNoticeEntry.COLUMN_NAME_ENTRY_ID);
       String content =  getIntent().getStringExtra(CampusNoticeEntry.COLUMN_NAME_CONTENT);
       String optTime =  getIntent().getStringExtra(CampusNoticeEntry.COLUMN_NAME_OPT_TIME);

       noticeTite.setText(R.string.system_note);
       noticeDate.setText(optTime);
       noticeContent.setText(content);
       
       confirmButton = (Button) findViewById(R.id.confirm_button);
       confirmButton.setOnClickListener(new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			CampusNoticeDetailActivity.this.finish();
		}
	});
       
       CampusNoticeDao campusNoticeDao = new CampusNoticeDao(
    		   CampusNoticeDetailActivity.this);
       
       CampusNotice campusNotice = campusNoticeDao.queryCampusNoticeByID(noticeID);
       if(campusNotice == null){
           campusNotice = new CampusNotice();
           campusNotice.id = noticeID;
           campusNotice.content = content;
           campusNotice.optTime = optTime;
           long insertResult = campusNoticeDao.addCampusNotice(campusNotice);
           HomePageActivity.campusNotieTotal -=1;
           Log.i("CampusNoticeDetailActivity", "updateResult " + insertResult);  
       }
       
      
      
       

    }


}
