package com.dingxi.jackdemo;


import com.dingxi.jackdemo.dao.CampusNoticeDao;
import com.dingxi.jackdemo.model.CampusNotice;
import com.dingxi.jackdemo.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.jackdemo.model.UserInfo;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

public class CampusNoticeDetailActivity extends Activity {

    private View parentHeader;
    private ImageButton mBackButton;
    private static UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private CampusNotice curretCampusNotice;
    private CampusNoticeDao curretCampusNoticeDao;
    private TextView noticeTite;
    private TextView noticeDate;
    private TextView noticeContent;
    
    
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
       curretCampusNoticeDao =  new CampusNoticeDao(mXiaoYunTongApplication);
       curretCampusNotice =  curretCampusNoticeDao.queryCampusNoticeByID(noticeID);
       noticeTite.setText("校园通知");
       noticeDate.setText(curretCampusNotice.optTime);
       noticeContent.setText(curretCampusNotice.content);
       

    }


}
