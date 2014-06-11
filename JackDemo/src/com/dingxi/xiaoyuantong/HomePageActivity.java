package com.dingxi.xiaoyuantong;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dingxi.xiaoyuantong.HomePageActivity.ImageAdapter.ViewHolder;
import com.dingxi.xiaoyuantong.dao.CampusNoticeDao;
import com.dingxi.xiaoyuantong.dao.HomeWorkDao;
import com.dingxi.xiaoyuantong.dao.InnerMessageDao;
import com.dingxi.xiaoyuantong.dao.LeaveMessageDao;
import com.dingxi.xiaoyuantong.db.XiaoyuantongDbHelper;
import com.dingxi.xiaoyuantong.model.CampusNotice;
import com.dingxi.xiaoyuantong.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.xiaoyuantong.model.ChildInfo;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo.HomeWorkEntry;
import com.dingxi.xiaoyuantong.model.InnerMessage;
import com.dingxi.xiaoyuantong.model.InnerMessage.InnerMessageEntry;
import com.dingxi.xiaoyuantong.model.LeaveMessage;
import com.dingxi.xiaoyuantong.model.LeaveMessage.LeaveMessageEntry;
import com.dingxi.xiaoyuantong.model.Notice;
import com.dingxi.xiaoyuantong.model.ParentInfo;
import com.dingxi.xiaoyuantong.model.TeacherInfo;
import com.dingxi.xiaoyuantong.model.UserInfo;
import com.dingxi.xiaoyuantong.model.UserInfo.UserType;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;
import com.dingxi.xiaoyuantong.util.Util;

public class HomePageActivity extends Activity {

    protected static final String TAG = "HomePageActivity";

    private static final int MSG_ROLL_TEXT = 0;
    private static final int MSG_NO_DATA = 1;
    private Spinner mSpinner;
    private UserInfo userInfo;

    private ProgressDialog mProgressDialog;
    private ArrayList<String> mSpinnerInfo;
    private ArrayAdapter<String> mSpinnerAdapter;
    private ImageAdapter mImageAdapter;
    private GetAllNoteTask mGetAllNoteTask;
    private GetAllChildTask mGetAllChildTask;
    private RollTextThread rollTextThread;
    GridView gridview;
    private View adsView;
    private TextView rollNoteText;
    private ArrayList<ChildInfo> mChildInfoList;
    private ArrayList<Notice> mNoticesLists;
    private Notice curretNotice;
    
    private XiaoyuantongDbHelper xiaoyuantongDbHelpers;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private Button exitAccountButton;

    boolean isFinish = false;
    boolean isFirstStart  = true;
    HomeWorkDao homeWorkDao;
    CampusNoticeDao campusNoticeDao;
    LeaveMessageDao leaveMessageDao;
    InnerMessageDao innerMessageDao;
    
    private Handler rollTextHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            
            switch (msg.what) {
            case MSG_ROLL_TEXT:
                adsView.setVisibility(View.VISIBLE);
                curretNotice = (Notice) msg.obj;
               String content = curretNotice.content;
                Log.d(TAG, "curretNotice.content " + content);
                rollNoteText.setClickable(true);
                rollNoteText.setText(content);
                break;
            case MSG_NO_DATA:  
                adsView.setVisibility(View.GONE);
                rollNoteText.setText("");
                rollNoteText.setClickable(false);
                break;
            default:
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();
        userInfo = mXiaoYunTongApplication.userInfo;
        xiaoyuantongDbHelpers = new XiaoyuantongDbHelper(getApplicationContext());
        gridview = (GridView) findViewById(R.id.gridview);
        adsView = (View)findViewById(R.id.ads_view);
        rollNoteText = (TextView) findViewById(R.id.roll_note_text);
        rollNoteText.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                

                if(curretNotice  instanceof CampusNotice){
                    Log.i(TAG, "instanceof CampusNotice " );
                    Intent intent = new Intent(HomePageActivity.this,
                            CampusNoticeDetailActivity.class);
                    intent.putExtra(CampusNoticeEntry.COLUMN_NAME_ENTRY_ID, curretNotice.id);
                    intent.putExtra(CampusNoticeEntry.COLUMN_NAME_CONTENT, curretNotice.content);
                    //intent.putExtra(CampusNoticeEntry.COLUMN_NAME_OPT_TIME, curretNotice.optTime);
                    startActivity(intent);
                } else if(curretNotice  instanceof HomeWorkInfo){
                    Log.i(TAG, "instanceof HomeWorkInfo " ); 
                    Intent intent = new Intent(HomePageActivity.this,
                            HomeWorkDetailActivity.class);
                    intent.putExtra(HomeWorkEntry.COLUMN_NAME_ENTRY_ID, curretNotice.id);
                    intent.putExtra(HomeWorkEntry.COLUMN_NAME_CONTENT, curretNotice.content);
                    //intent.putExtra(CampusNoticeEntry.COLUMN_NAME_OPT_TIME, curretNotice.optTime);
                    startActivity(intent);

                } else if(curretNotice  instanceof InnerMessage){
                    
                    Log.i(TAG, "instanceof InnerMessage " );
                    Intent intent = new Intent(HomePageActivity.this,
                            InnnerMessageDetailActivity.class);
                    intent.putExtra(InnerMessageEntry.COLUMN_NAME_ENTRY_ID, curretNotice.id);
                    intent.putExtra(InnerMessageEntry.COLUMN_NAME_CONTENT, curretNotice.content);
                    //intent.putExtra(CampusNoticeEntry.COLUMN_NAME_OPT_TIME, curretNotice.optTime);
                    startActivity(intent);
                } else if(curretNotice  instanceof LeaveMessage){
                    
                    Log.i(TAG, "instanceof LeaveMessage " );
                    Intent intent = new Intent(HomePageActivity.this,
                            LeaveMessageDetailActivity.class);
                    intent.putExtra(LeaveMessageEntry.COLUMN_NAME_ENTRY_ID, curretNotice.id);
                    intent.putExtra(LeaveMessageEntry.COLUMN_NAME_CONTENT, curretNotice.content);
                    //intent.putExtra(CampusNoticeEntry.COLUMN_NAME_OPT_TIME, curretNotice.optTime);
                    startActivity(intent);
                } else {
                    Log.i(TAG, "instanceof Object " );
                    
                }
                

                // TODO Auto-generated method stub
                
                
            }
        });
        
        
        mImageAdapter = new ImageAdapter(this);
        mSpinnerInfo = new ArrayList<String>();
        mChildInfoList = new ArrayList<ChildInfo>();
        //campusNoticeContentList = new ArrayList<String>();
        mNoticesLists = new ArrayList<Notice>();
  
        
        gridview.setAdapter(mImageAdapter);

        exitAccountButton = (Button) findViewById(R.id.exit_account);
        exitAccountButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                userInfo = null;

                if (mGetAllNoteTask != null && !mGetAllNoteTask.isCancelled()) {
                    mGetAllNoteTask.cancel(true);
                    mGetAllNoteTask = null;
                }
                if (mGetAllChildTask != null && !mGetAllChildTask.isCancelled()) {
                    mGetAllChildTask.cancel(true);
                    mGetAllChildTask = null;
                }

                mImageAdapter = null;
                mNoticesLists = null;
                //homeWorkTotal = 0;
                //campusNotieTotal = 0;
                userInfo = null;
                gridview = null;
                
                if (rollTextThread != null && rollTextThread.isAlive()) {
                    rollTextThread.interrupt();
                    rollTextThread = null;
                }

                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_USER_INFO, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean(LoginActivity.IS_AUTO_LOGIN, false);
                editor.commit();
                Intent loginIntent = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(loginIntent);
                
                stopService(new Intent(HomePageActivity.this,
                        LocalService.class));

                // private GetAllChildTask mGetAllChildTask;
                finish();
            }
        });

       

        mSpinner = (Spinner) findViewById(R.id.main_spinner);
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Log.i(TAG, "mStudentList position " + position + " id " + id);

               // homeWorkTotal = 0;
                //campusNotieTotal = 0;
                mNoticesLists.clear();
                if(rollTextThread!=null && rollTextThread.isAlive()){
                    rollTextThread.interrupt(); 
                }
               
               // mImageAdapter.notifyDataSetChanged();
                
                
                mProgressDialog = new ProgressDialog(HomePageActivity.this);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

                    mProgressDialog.setMessage(getString(R.string.now_geting_childinfo));
                    mProgressDialog.setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {

                            // TODO Auto-generated method stub
                            if (mGetAllChildTask != null && !mGetAllChildTask.isCancelled()) {
                                mGetAllChildTask.cancel(true);
                                // isCancelLogin = true;
                            }
                        }
                    });
                   

                
                ParentInfo parentInfo = (ParentInfo) userInfo;
               
                parentInfo.defalutChild = mChildInfoList.get(position);
                parentInfo.curretSelectIndex = position;
                mGetAllNoteTask = new GetAllNoteTask();
                mGetAllNoteTask.execute((Void) null);
                mProgressDialog.show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
                Log.i(TAG, " onNothingSelected() ");
            }
        });
        // mSpinner.setEmptyView(emptyView)

        if (userInfo.roleType == UserType.ROLE_PARENT) {
            mSpinner.setVisibility(View.VISIBLE);
            mSpinner.setPromptId(R.string.please_check_child);
            mSpinnerAdapter = new ArrayAdapter<String>(HomePageActivity.this,
                    R.layout.spinner_checked_text, mSpinnerInfo);
            mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mSpinner.setAdapter(mSpinnerAdapter);
        } else {// if (curretUserInfo.roleType == UserType.ROLE_TEACHER)
            // mClassInfoList = new ArrayList<ClassInfo>();
            mSpinner.setVisibility(View.GONE);
            // mSpinner.setPromptId(R.string.please_check_class);
            // mAdapter = new ArrayAdapter<String>(HomePageActivity.this,
            // android.R.layout.simple_spinner_item, mSpinnerInfo);
        }

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent(HomePageActivity.this, HomeWorkActivity.class);
                    startActivity(intent);
                } else if (position == 1) {
                    Intent intent = new Intent(HomePageActivity.this, CampusNoticeActivity.class);
                    startActivity(intent);
                } else if (position == 2) {
                    Intent intent = new Intent(HomePageActivity.this, AttendanceInfoActivity.class);
                    startActivity(intent);
                } else if (position == 3) {
                    Intent intent = new Intent(HomePageActivity.this, ModifyPassWordActivity.class);
                    startActivity(intent);
                } else if (position == 4) {
                    Intent intent = new Intent(HomePageActivity.this, LeaveMessageActivity.class);
                    startActivity(intent);
                } else if(position == 5){
                    
                    
                    if (userInfo.roleType == UserType.ROLE_PARENT) {
                        if(Util.IsNetworkAvailable(HomePageActivity.this)){
                            ParentInfo parentInfo = (ParentInfo) userInfo;
                            if(parentInfo.defalutChild == null){
                                Toast.makeText(HomePageActivity.this, R.string.please_check_child,
                                        Toast.LENGTH_LONG).show();
                            }else {
                                
                                if (!TextUtils.isEmpty(parentInfo.defalutChild.imei)) {
                                    Intent intent = new Intent(HomePageActivity.this,
                                            LocationInfoActivity.class);
                              
                                    Log.i(TAG, "deflutStudentInfo.name " + parentInfo.defalutChild.name);
                                    Log.i(TAG, "deflutStudentInfo.imei " + parentInfo.defalutChild.imei);
                                    Log.i(TAG, "deflutStudentInfo.id " + parentInfo.defalutChild.id);
                                    intent.putExtra("childId", parentInfo.defalutChild.id);
                                    intent.putExtra("imei", parentInfo.defalutChild.imei);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(HomePageActivity.this, R.string.no_bound_phone,
                                            Toast.LENGTH_LONG).show();
                                }
                                
                            } 
                        } else {        
                             Toast.makeText(HomePageActivity.this, R.string.not_network, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Intent intent = new Intent(HomePageActivity.this, InnerMessageActivity.class);
                        startActivity(intent);
                    }
                    
                   
                } else if(position == 6){
                	
                	
                    
                }

            }
        });
        
        
        homeWorkDao = new HomeWorkDao(mXiaoYunTongApplication);
        campusNoticeDao = new CampusNoticeDao(HomePageActivity.this);
        leaveMessageDao = new LeaveMessageDao(mXiaoYunTongApplication);
        innerMessageDao = new InnerMessageDao(mXiaoYunTongApplication);
        
        mProgressDialog = new ProgressDialog(HomePageActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (userInfo.roleType == UserType.ROLE_PARENT) {
            mProgressDialog.setMessage(getString(R.string.now_geting_childinfo));
            mProgressDialog.setOnCancelListener(new OnCancelListener() {

                @Override
                public void onCancel(DialogInterface dialog) {

                    // TODO Auto-generated method stub
                    if (mGetAllChildTask != null && !mGetAllChildTask.isCancelled()) {
                        mGetAllChildTask.cancel(true);
                        // isCancelLogin = true;
                    }
                }
            });
            mProgressDialog.show();
            mGetAllChildTask = new GetAllChildTask();
            mGetAllChildTask.execute((Void) null);
        } else if (userInfo.roleType == UserType.ROLE_TEACHER) {
            mGetAllNoteTask = new GetAllNoteTask();
            mGetAllNoteTask.execute((Void) null);
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        // super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);
        builder.setTitle(R.string.exit_app);
        // Add the buttons
        builder.setPositiveButton(R.string.ok_exit_app, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                
                userInfo = null;

                if (mGetAllNoteTask != null && !mGetAllNoteTask.isCancelled()) {
                    mGetAllNoteTask.cancel(true);
                    mGetAllNoteTask = null;
                }
                if (mGetAllChildTask != null && !mGetAllChildTask.isCancelled()) {
                    mGetAllChildTask.cancel(true);
                    mGetAllChildTask = null;
                }

                mImageAdapter = null;

                //homeWorkTotal = 0;
                //campusNotieTotal = 0;
                userInfo = null;
                gridview = null;
                
                if (rollTextThread != null && rollTextThread.isAlive()) {
                    rollTextThread.interrupt();
                    rollTextThread = null;
                }
                
                stopService(new Intent(HomePageActivity.this,
                        LocalService.class));
                HomePageActivity.this.finish();
            }
        });
        builder.setNegativeButton(R.string.cancel_exit_app, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
       // mImageAdapter.notifyDataSetChanged();
       
        if(isFirstStart){
            isFirstStart =  false;
        } else {
            getNotReadCounts();
        }

    }
    
    
    
    
    
    private void getNotReadCounts(){
        
        
        
        mNoticesLists.clear();
        isFinish = true;
        
        ArrayList<HomeWorkInfo> homeWorks = homeWorkDao.queryReadOrNotReadCountHomeWork(0);
        if(homeWorks!=null && homeWorks.size()>0){
            
            
            Log.i(TAG, "HomeWork  notReadCount " + homeWorks.size());
            View homeWorkView = gridview.getChildAt(0);
            if (homeWorkView != null) {

                ViewHolder viewHolder = (ViewHolder) homeWorkView.getTag();
                if (homeWorks.size() > 0) {
                    viewHolder.numberText.setVisibility(View.VISIBLE);
                    viewHolder.numberText.setText(String.valueOf(homeWorks.size()));
                    homeWorkView.setBackgroundResource(R.drawable.button_down);
                } else {
                    viewHolder.numberText.setVisibility(View.GONE);
                    homeWorkView.setBackgroundResource(R.drawable.button_ordinary);
                }
            }
            for (HomeWorkInfo homeWorkInfo2 : homeWorks) {
                mNoticesLists.add(0, homeWorkInfo2);
            }
            
        }

        
        //notReadCount = 0;
        //notReadCount =  campusNoticeDao.queryReadOrNotReadCount(0);
        ArrayList<CampusNotice> campusNotices  =  campusNoticeDao.queryNotReadCampusNoticeCount(0);
        
        if(campusNotices !=null && campusNotices.size() > 0){
            Log.i(TAG, "CampusNotice  notReadCount " + campusNotices.size());
            View campusNoteView = gridview.getChildAt(1);
            if (campusNoteView != null) {

                ViewHolder convertViewHolder = (ViewHolder) campusNoteView.getTag();
                if (campusNotices.size() > 0) {

                    convertViewHolder.numberText.setVisibility(View.VISIBLE);
                    convertViewHolder.numberText.setText(String.valueOf(campusNotices.size()));
                    campusNoteView.setBackgroundResource(R.drawable.button_down);
                } else {
                    convertViewHolder.numberText.setVisibility(View.GONE);
                    campusNoteView.setBackgroundResource(R.drawable.button_ordinary);
                    
                    rollTextHandler.sendEmptyMessage(MSG_NO_DATA);
                    mNoticesLists.clear();
                }

            }
            
            for (CampusNotice campusNotice : campusNotices) {
                mNoticesLists.add(0, campusNotice);
            }

        }
       
        
        //notReadCount = 0;
        //notReadCount =  leaveMessageDao.queryReadOrNotReadCount(0);
        ArrayList<LeaveMessage> leaveMessages = leaveMessageDao.queryNotReadLeaveMessageCount(0);
        
        if(leaveMessages!=null && leaveMessages.size() > 0){
            Log.i(TAG, "LeaveMessage  notReadCount " + leaveMessages.size());
            View leaveMessageView = null;
            
            if (userInfo.roleType == UserType.ROLE_PARENT) {
                leaveMessageView = gridview.getChildAt(4);
            }else{
                leaveMessageView = gridview.getChildAt(4);        
            }
            
            if (leaveMessageView != null) {

                ViewHolder convertViewHolder = (ViewHolder) leaveMessageView.getTag();
                if (leaveMessages.size() > 0) {

                    convertViewHolder.numberText.setVisibility(View.VISIBLE);
                    convertViewHolder.numberText.setText(String.valueOf(leaveMessages.size()));
                    leaveMessageView.setBackgroundResource(R.drawable.button_down);
                } else {
                    convertViewHolder.numberText.setVisibility(View.GONE);
                    leaveMessageView.setBackgroundResource(R.drawable.button_ordinary);
                    
                }

            }
            
            for (LeaveMessage leaveMessage : leaveMessages) {
                mNoticesLists.add(0, leaveMessage);
            }
        }
        
       
        if(userInfo.roleType == UserType.ROLE_TEACHER){
            
            ArrayList<InnerMessage> innerMessages =  innerMessageDao.queryNotReadInnerMessageCount(0);
            if(innerMessages != null && innerMessages.size() > 0){
                Log.i(TAG, "InnerMessage  notReadCount " + innerMessages.size());
                View innerMeassageView = gridview.getChildAt(5);
                if (innerMeassageView!= null) {

                    ViewHolder convertViewHolder = (ViewHolder) innerMeassageView.getTag();
                    if (innerMessages.size() > 0) {

                        convertViewHolder.numberText.setVisibility(View.VISIBLE);
                        convertViewHolder.numberText.setText(String.valueOf(innerMessages.size()));
                        innerMeassageView.setBackgroundResource(R.drawable.button_down);
                    } else {
                        convertViewHolder.numberText.setVisibility(View.GONE);
                        innerMeassageView.setBackgroundResource(R.drawable.button_ordinary);               
                    }

                }  
                
                for (InnerMessage innerMessage : innerMessages) {
                    mNoticesLists.add(0, innerMessage);
                }
                
            }
                     
        }
        
        
        if (mNoticesLists != null && mNoticesLists.size() > 0) {

            if (mNoticesLists.size() > 0) {

                if (rollTextThread != null && rollTextThread.isAlive()) {
                    rollTextThread.interrupt();
                    isFinish = true;
                    rollTextThread = null;
                }
                
                rollTextThread = new RollTextThread();
                rollTextThread.start();
                
            } 
//            else if (campusNoticeList.size() == 1) {
//                String content = campusNoticeList.get(0).content;
//                Log.i(TAG, "content " + content);
//                rollNoteText.setText(content);
//            }

        }
    }

    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        isFinish = true;
        xiaoyuantongDbHelpers.close();
    }

    class RollTextThread extends Thread {

        @Override
        public void run() {
            super.run();
            Log.d(TAG, "RollTextThread run()" );
            Thread.currentThread().setName("RollTextThread");
            while (mNoticesLists != null && mNoticesLists.size() > 0
                    && (!isFinish)) {
                for (int i = 0; i < mNoticesLists.size(); i++) {

                    
                    Notice notice = mNoticesLists.get(i);
                    Log.d(TAG, "rollText " + notice.content);
                    Message message = Message.obtain();
                    message.what = MSG_ROLL_TEXT;
                    message.obj = notice;
                    //message.arg1 = campusNoticeList.get(i).id;
                    
                    rollTextHandler.sendMessage(message);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        
                        isFinish = true;
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

            }
            
            if(rollTextHandler!=null){
                rollTextHandler.sendEmptyMessage(MSG_NO_DATA);
            }

        }
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public ImageAdapter(Context c) {
            inflater = LayoutInflater.from(HomePageActivity.this);
        }

        public int getCount() {
            int count = 0;
            if(userInfo!=null){
                if (userInfo.roleType == UserType.ROLE_PARENT) {
                    count = parentPictures.length;
                } else {
                    count = teacherPictures.length;
                }
            }
            
            return count;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            int itemId;
            if (userInfo.roleType == UserType.ROLE_PARENT) {
                itemId = parentTitles[position];
            } else {
                itemId = teacherTitles[position];

            }
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.gridview_picture_item, null);
                viewHolder = new ViewHolder();
                viewHolder.title = (TextView) convertView.findViewById(R.id.title);
                viewHolder.image = (ImageView) convertView.findViewById(R.id.image);
                viewHolder.numberText = (TextView) convertView.findViewById(R.id.number_text);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            if (userInfo.roleType == UserType.ROLE_PARENT) {
                viewHolder.title.setText(parentTitles[position]);
                viewHolder.image.setImageResource(parentPictures[position]);

            } else {
                viewHolder.title.setText(teacherTitles[position]);
                viewHolder.image.setImageResource(teacherPictures[position]);
            }

            return convertView;
        }

        class ViewHolder {
            public TextView title;
            public TextView numberText;
            public ImageView image;
        }
        

        //R.drawable.officialweb,R.string.official_site,
        private Integer[] teacherPictures = { R.drawable.homework, R.drawable.message,
                R.drawable.check,  R.drawable.change_password,R.drawable.leave_message,R.drawable.inner_message };
        private Integer[] teacherTitles = { R.string.home_work, R.string.system_note,
                R.string.attendance_information, R.string.change_password, R.string.leave_message,R.string.inner_message};
        
        
        
        private Integer[] parentPictures = { R.drawable.homework, R.drawable.message,
                R.drawable.check, R.drawable.change_password,
                R.drawable.leave_message,R.drawable.position};
        private Integer[] parentTitles = { R.string.home_work, R.string.system_note,
                R.string.attendance_information,  R.string.change_password,
                R.string.leave_message,R.string.position_orientation};
    }

    public class GetAllNoteTask extends AsyncTask<Void, String, ResponseMessage> {
        @Override
        protected ResponseMessage doInBackground(Void... params) {
            Thread.currentThread().setName("GetAllNoteTask");
            ResponseMessage responseMessage = null;
            StringBuilder info = new StringBuilder();

            info.append("{");

            if (userInfo.roleType == UserInfo.UserType.ROLE_TEACHER) {

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
            
            
            
            try {
                responseMessage = new ResponseMessage();
                // 老师info�?
                // {"fkSchoolId":"2","fkClassId":"0","fkClassId2":"2",”queryTime�?�?014-03-24”}
                // 家长info:{"fkSchoolId":"2","fkStudentId":"402881f144d911a10144d916319c0002",”queryTime�?�?014-03-24”}


                responseMessage.body = RestClient.getHomeWorks(mXiaoYunTongApplication.userInfo.id,
                        mXiaoYunTongApplication.userInfo.ticket, userInfo.roleType.toString(),
                        info.toString(), 1, 5);
                responseMessage.praseBody();
                Log.i(TAG, "HomeWorks response  " + responseMessage.body);
                if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS
                        && responseMessage.total > 0) {

                        ArrayList<HomeWorkInfo> homeWorkInfoList = JSONParser
                                .praseHomeWorks(responseMessage.body);
                        if (homeWorkInfoList != null && homeWorkInfoList.size() > 0) {
                           
                            for (HomeWorkInfo homeWorkInfo : homeWorkInfoList) {
                               
                                Notice info1  = homeWorkDao.queryHomeWorkByID(homeWorkInfo.id);
                                if(info1 == null){
                                   // homeWorkTotal += 1;
                                    mNoticesLists.add(0, homeWorkInfo);
                                  long insertResult = homeWorkDao.addHomeWork(homeWorkInfo);
                                  Log.i(TAG, "HomeWork insertResult " + insertResult);
                                }
                               // Log.i(TAG, "homeWorkTotal " + homeWorkTotal);

                            }
                            //homeWorkDao.colseDb();
                            //homeWorkDao = null;
                        }
                   

                }
                
            }catch (Exception e) {
            	 e.printStackTrace();
               	Log.e(TAG, "getHomeWorks "+ e.getMessage());
            }
            

            
           

            responseMessage = null;
            // 老师info�?
            // {"fkSchoolId":"2","fkClassId":"0","fkClassId2":"2",”queryTime�?�?014-03-24”}
            // 家长info:{"fkSchoolId":"2","fkStudentId":"402881f144d911a10144d916319c0002",”queryTime�?�?014-03-24”}
            try {
                responseMessage = new ResponseMessage();
                responseMessage.body = RestClient.getMessageInfos(
                        mXiaoYunTongApplication.userInfo.id,
                        mXiaoYunTongApplication.userInfo.ticket, userInfo.roleType.toString(),
                        info.toString(), 1, 5);
                Log.i(TAG, "MessageInfos response  " + responseMessage.body);
                responseMessage.praseBody();
                
                if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS
                        && responseMessage.total > 0) {


                        ArrayList<CampusNotice> campusNoticeList = JSONParser
                                .praseCampusNotice(responseMessage.body);

                        if (campusNoticeList != null && campusNoticeList.size() > 0) {
                           
                            for (CampusNotice campusNotice : campusNoticeList) {
                                CampusNotice campus = campusNoticeDao.queryCampusNoticeByID(campusNotice.id);
                                Log.i(TAG, "campus " + campus);
                                if(campus==null){
                                    //campusNotieTotal += 1;
                                    mNoticesLists.add(0, campusNotice);
                                    long insertResult = campusNoticeDao.addCampusNotice(campusNotice);
                                    Log.i(TAG, "MessageInfos insertResult " + insertResult);
                                }
                              
                               // Log.i(TAG, "campusNotieTotal " + campusNotieTotal);
                            }

                        };

                    
                }

            } catch (Exception e) {
            	 e.printStackTrace();
            	 Log.e(TAG, "getMessageInfos "+ e.getMessage());
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
                        
                        for (LeaveMessage leaveMessage : campusNoticeList) {                          
                            LeaveMessage leave = leaveMessageDao.queryLeaveMessageByID(leaveMessage.id);
                            Log.i(TAG, "leave " + leave);
                            if(leave==null){
                                mNoticesLists.add(0, leaveMessage);
                                long insertResult = leaveMessageDao.addLeaveMessage(leaveMessage);
                                Log.i(TAG, "LeaveMessage insertResult " + insertResult);
                                //campusNotieTotal += 1;
                                //campusNoticeLists.add(0, campusNotice);
                            }
                            //Log.i(TAG, "campusNotieTotal " + campusNotieTotal);
                        }

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
                Log.i(TAG, "InnerMessages response  " + responseMessage.body);
                //responseMessage.body = RestClient.getLeaveMessages(mXiaoYunTongApplication.userInfo.id, "1", "", "", mXiaoYunTongApplication.userInfo.roleType.toString(), "1", "6");
                responseMessage.praseBody();
                if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                    ArrayList<InnerMessage> campusNoticeList = JSONParser
                            .praseInnerMessage(responseMessage.body);
                    
                    if (campusNoticeList != null && campusNoticeList.size() > 0) {
                    	
                        for (InnerMessage innerMessage : campusNoticeList) {                          
                        	InnerMessage inner = innerMessageDao.queryInnerMessageByID(innerMessage.id);
                        	  Log.i(TAG, "inner " + inner);
                            if(inner==null){
                                mNoticesLists.add(0, innerMessage);
                                long insertResult = innerMessageDao.addInnerMessage(innerMessage);
                                Log.i(TAG, "InnerMessage insertResult " + insertResult);
                            }
                        }
                    };
                }
            } catch (Exception e) {
            	e.printStackTrace();
            	Log.e(TAG, "getInnerMessages "+ e.getMessage());
            }
            

            // TODO: register the new account here.
            return responseMessage;
        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            Log.i(TAG, "GetAllNoteTask run()");
        }

        @Override
        protected void onPostExecute(ResponseMessage ResponseMessage) {
            mGetAllNoteTask = null;
            //Log.i(TAG, "homeWorkTotal " + homeWorkTotal);
            //Log.i(TAG, "campusNotieTotal " + campusNotieTotal);
            Log.i(TAG, "mNoticesLists.size() " + mNoticesLists.size());
            getNotReadCounts();
            
            
            
              
                
  
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
             }
           startService(new Intent(HomePageActivity.this,LocalService.class));
         
            
        }

        @Override
        protected void onCancelled() {
            mGetAllNoteTask = null;
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
             }

        }
    }

    public class GetAllChildTask extends AsyncTask<Void, Void, ResponseMessage> {
        @Override
        protected ResponseMessage doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            Thread.currentThread().setName("GetAllChildTask");
            ResponseMessage responseMessage = new ResponseMessage();
            try {

                responseMessage.body = RestClient.getAllChilds(userInfo.id, userInfo.ticket);
                responseMessage.praseBody();

            } catch (ConnectTimeoutException stex) {
                responseMessage.message = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                responseMessage.message = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                responseMessage.message = getString(R.string.connection_server_error);
            } catch (JSONException e) {
                responseMessage.message = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (XmlPullParserException e) {
                responseMessage.message = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (IOException e) {
                responseMessage.message = getString(R.string.connection_error);
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return responseMessage;
        }

        @Override
        protected void onPostExecute(ResponseMessage responseMessage) {
            mGetAllChildTask = null;
            if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {

                mChildInfoList.clear();

                try {
                    ArrayList<ChildInfo> studentList = JSONParser
                            .parseChildInfo(responseMessage.body);
                    mChildInfoList.addAll(studentList);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }

                if (mChildInfoList.size() > 0) {
                    ParentInfo parentInfo = (ParentInfo) userInfo;

                    parentInfo.childList = mChildInfoList;
                    parentInfo.defalutChild = mChildInfoList.get(0);
                    mSpinnerInfo.clear();
                    for (ChildInfo studentInfo : mChildInfoList) {
                        mSpinnerInfo.add(studentInfo.name);
                    }
                    parentInfo.nameList = mSpinnerInfo;
//                    mGetAllNoteTask = new GetAllNoteTask();
//                    mGetAllNoteTask.execute((Void) null);
                    mSpinnerAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(HomePageActivity.this, R.string.no_child, Toast.LENGTH_LONG)
                            .show();
                }
                mProgressDialog.dismiss();

            } else {
                mProgressDialog.dismiss();
                Toast.makeText(HomePageActivity.this, responseMessage.message, Toast.LENGTH_LONG)
                        .show();
            }
            
            

        }

        @Override
        protected void onCancelled() {
            mGetAllChildTask = null;

        }
    }

}
