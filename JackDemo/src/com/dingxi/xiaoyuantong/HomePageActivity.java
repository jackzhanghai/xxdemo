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
import com.dingxi.xiaoyuantong.db.XiaoyuantongDbHelper;
import com.dingxi.xiaoyuantong.model.CampusNotice;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo;
import com.dingxi.xiaoyuantong.model.ParentInfo;
import com.dingxi.xiaoyuantong.model.StudentInfo;
import com.dingxi.xiaoyuantong.model.TeacherInfo;
import com.dingxi.xiaoyuantong.model.UserInfo;
import com.dingxi.xiaoyuantong.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.xiaoyuantong.model.UserInfo.UserType;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;

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
    public static int homeWorkTotal;
    public static int campusNotieTotal;
    private RollTextThread rollTextThread;
    GridView gridview;
    private TextView rollNoteText;
    private ArrayList<StudentInfo> mStudentList;
    //private ArrayList<String> campusNoticeContentList;
   private ArrayList<CampusNotice> campusNoticeLists;
   private CampusNotice curretNotice;
    
    private XiaoyuantongDbHelper xiaoyuantongDbHelpers;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private Button exitAccountButton;

    boolean isFinish = false;
    private Handler rollTextHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            
            switch (msg.what) {
            case MSG_ROLL_TEXT:
                String text = (String) msg.obj;
                Log.d(TAG, "text " + text);
                rollNoteText.setClickable(true);
                rollNoteText.setText(text);
                break;
            case MSG_NO_DATA:                
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

        gridview = (GridView) findViewById(R.id.gridview);
        rollNoteText = (TextView) findViewById(R.id.roll_note_text);
        rollNoteText.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(HomePageActivity.this,
                        CampusNoticeDetailActivity.class);
                intent.putExtra(CampusNoticeEntry.COLUMN_NAME_ENTRY_ID, curretNotice.id);
                intent.putExtra(CampusNoticeEntry.COLUMN_NAME_CONTENT, curretNotice.content);
                intent.putExtra(CampusNoticeEntry.COLUMN_NAME_OPT_TIME, curretNotice.optTime);
                startActivity(intent);
//                /
                
            }
        });
        
        
        mImageAdapter = new ImageAdapter(this);
        mSpinnerInfo = new ArrayList<String>();
        mStudentList = new ArrayList<StudentInfo>();
        //campusNoticeContentList = new ArrayList<String>();
        campusNoticeLists = new ArrayList<CampusNotice>();
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

                homeWorkTotal = 0;
                campusNotieTotal = 0;
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

                // private GetAllChildTask mGetAllChildTask;
                finish();
            }
        });

        xiaoyuantongDbHelpers = new XiaoyuantongDbHelper(getApplicationContext());

        mSpinner = (Spinner) findViewById(R.id.main_spinner);
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                Log.i(TAG, "mStudentList position " + position + " id " + id);

                homeWorkTotal = 0;
                campusNotieTotal = 0;
                campusNoticeLists.clear();
                if(rollTextThread!=null && rollTextThread.isAlive()){
                    rollTextThread.interrupt(); 
                }
               
               // mImageAdapter.notifyDataSetChanged();

                ParentInfo parentInfo = (ParentInfo) userInfo;
               
                parentInfo.defalutChild = mStudentList.get(position);
                parentInfo.curretSelectIndex = position;
                mGetAllNoteTask = new GetAllNoteTask();
                mGetAllNoteTask.execute((Void) null);
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
                    android.R.layout.simple_spinner_item, mSpinnerInfo);
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
                }
//                else if (position == 3) {
//                    Intent intent = new Intent(HomePageActivity.this, OfficialSiteActivity.class);
//                    startActivity(intent);
//                } 
                else if (position == 3) {
                    Intent intent = new Intent(HomePageActivity.this, ModifyPassWordActivity.class);
                    startActivity(intent);
                } else if (position == 4) {
                    ParentInfo parentInfo = (ParentInfo) userInfo;
                    if (!TextUtils.isEmpty(parentInfo.defalutChild.imei)) {
                        Intent intent = new Intent(HomePageActivity.this,
                                LocationInfoActivity.class);
                  
                        Log.i(TAG, "deflutStudentInfo.name " + parentInfo.defalutChild.stuName);
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

            }
        });

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
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.i(TAG, "OnStart()");
        Log.i(TAG, "homeWorkTotal " + homeWorkTotal);
        Log.i(TAG, "campusNotieTotal " + campusNotieTotal);

       // mImageAdapter.notifyDataSetChanged();
       

        getPoPoCount();

    }
    
    
    
    private void getPoPoCount(){
        View homeWorkView = gridview.getChildAt(0);
        if (homeWorkView != null) {

            ViewHolder viewHolder = (ViewHolder) homeWorkView.getTag();
            if (homeWorkTotal > 0) {

                viewHolder.numberText.setVisibility(View.VISIBLE);
                viewHolder.numberText.setText(String.valueOf(homeWorkTotal));
                homeWorkView.setBackgroundResource(R.drawable.button_down);
            } else {
                viewHolder.numberText.setVisibility(View.GONE);
                homeWorkView.setBackgroundResource(R.drawable.button_ordinary);
            }

        }

        View convertView = gridview.getChildAt(1);
        if (convertView != null) {

            ViewHolder convertViewHolder = (ViewHolder) convertView.getTag();
            if (campusNotieTotal > 0) {

                convertViewHolder.numberText.setVisibility(View.VISIBLE);
                convertViewHolder.numberText.setText(String.valueOf(campusNotieTotal));
                convertView.setBackgroundResource(R.drawable.button_down);
            } else {
                convertViewHolder.numberText.setVisibility(View.GONE);
                convertView.setBackgroundResource(R.drawable.button_ordinary);
                
                rollTextHandler.sendEmptyMessage(MSG_NO_DATA);
                campusNoticeLists.clear();
            }

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
            // TODO Auto-generated method stub
            super.run();

            Thread.currentThread().setName("RollTextThread");
            while (campusNoticeLists != null && campusNoticeLists.size() > 0
                    && (!isFinish)) {
                for (int i = 0; i < campusNoticeLists.size(); i++) {

                    Message message = Message.obtain();
                    message.what = MSG_ROLL_TEXT;
                    message.obj = campusNoticeLists.get(i).content;
                    curretNotice = campusNoticeLists.get(i);
                    //message.arg1 = campusNoticeList.get(i).id;
                    Log.d(TAG, "rollText " + campusNoticeLists.get(i).content);
                    rollTextHandler.sendMessage(message);
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
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

            if (homeWorkTotal > 0 && position == 0) {

                viewHolder.numberText.setVisibility(View.VISIBLE);
                viewHolder.numberText.setText(String.valueOf(homeWorkTotal));
                convertView.setBackgroundResource(R.drawable.button_down);
            } else {
                convertView.setBackgroundResource(R.drawable.button_ordinary);
                viewHolder.numberText.setVisibility(View.GONE);
            }
            

            if (campusNotieTotal > 0 && position == 1) {

                viewHolder.numberText.setVisibility(View.VISIBLE);
                viewHolder.numberText.setText(String.valueOf(campusNotieTotal));
                convertView.setBackgroundResource(R.drawable.button_down);
                
            } else {
                convertView.setBackgroundResource(R.drawable.button_ordinary);
                viewHolder.numberText.setVisibility(View.GONE);
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
                R.drawable.check,  R.drawable.change_password };
        private Integer[] teacherTitles = { R.string.home_work, R.string.system_note,
                R.string.attendance_information, R.string.change_password };
        private Integer[] parentPictures = { R.drawable.homework, R.drawable.message,
                R.drawable.check, R.drawable.change_password,
                R.drawable.position };
        private Integer[] parentTitles = { R.string.home_work, R.string.system_note,
                R.string.attendance_information,  R.string.change_password,
                R.string.position_orientation };
    }

    // public class GetAllClassTask extends AsyncTask<Void, Void, String> {
    // @Override
    // protected String doInBackground(Void... params) {
    // // TODO: attempt authentication against a network service.
    // String schoolsInfo = null;
    // try {
    // schoolsInfo = ResponseMessage.getClassInfos(curretUserInfo.id,
    // curretUserInfo.ticket,
    // curretUserInfo.fkClassId);
    // } catch (ConnectTimeoutException stex) {
    // schoolsInfo = getString(R.string.request_time_out);
    // } catch (SocketTimeoutException stex) {
    // schoolsInfo = getString(R.string.server_time_out);
    // } catch (HttpHostConnectException hhce) {
    // schoolsInfo = getString(R.string.connection_server_error);
    // } catch (XmlPullParserException e) {
    // schoolsInfo = getString(R.string.connection_error);
    // e.printStackTrace();
    // } catch (IOException e) {
    // schoolsInfo = getString(R.string.connection_error);
    // e.printStackTrace();
    // }
    //
    // // TODO: register the new account here.
    // return schoolsInfo;
    // }
    //
    // @Override
    // protected void onPostExecute(String schoolsInfo) {
    // mGetAllClassTask = null;
    // Log.d(TAG, "result " + schoolsInfo);
    // if (!TextUtils.isEmpty(schoolsInfo)) {
    //
    // try {
    // if (JSONParser.getIntByTag(schoolsInfo, ResponseMessage.RESULT_TAG_CODE) ==
    // ResponseMessage.RESULT_TAG_SUCCESS) {
    //
    // try {
    // if (JSONParser.getStringByTag(schoolsInfo, "datas") != null) {
    //
    // }
    // } catch (JSONException e) {
    // // TODO Auto-generated catch block
    // e.printStackTrace();
    // }
    // parseClassInfo(schoolsInfo);
    //
    // mProgressDialog.dismiss();
    //
    // } else {
    // mProgressDialog.dismiss();
    // String errorMessage = JSONParser.getStringByTag(schoolsInfo,
    // ResponseMessage.RESULT_TAG_MESSAGE);
    // if (!TextUtils.isEmpty(errorMessage)) {
    // Toast.makeText(HomePageActivity.this, errorMessage, Toast.LENGTH_LONG)
    // .show();
    // } else {
    // Toast.makeText(HomePageActivity.this, schoolsInfo, Toast.LENGTH_LONG)
    // .show();
    // }
    // }
    // } catch (JSONException e) {
    // if (mProgressDialog != null) {
    // mProgressDialog.dismiss();
    // }
    //
    // Toast.makeText(HomePageActivity.this, schoolsInfo, Toast.LENGTH_LONG).show();
    // e.printStackTrace();
    // }
    // } else {
    // mProgressDialog.dismiss();
    //
    // }
    // }
    //
    // @Override
    // protected void onCancelled() {
    // mGetAllClassTask = null;
    //
    // }
    // }

    public class GetAllNoteTask extends AsyncTask<Void, String, ResponseMessage> {
        @Override
        protected ResponseMessage doInBackground(Void... params) {
            Thread.currentThread().setName("GetAllNoteTask");
            ResponseMessage responseMessage = null;
            StringBuilder info = new StringBuilder();
            try {
                responseMessage = new ResponseMessage();
                // 老师info�?
                // {"fkSchoolId":"2","fkClassId":"0","fkClassId2":"2",”queryTime�?�?014-03-24”}
                // 家长info:{"fkSchoolId":"2","fkStudentId":"402881f144d911a10144d916319c0002",”queryTime�?�?014-03-24”}

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

                responseMessage.body = RestClient.getHomeWorks(mXiaoYunTongApplication.userInfo.id,
                        mXiaoYunTongApplication.userInfo.ticket, userInfo.roleType.toString(),
                        info.toString(), 1, 5);
                responseMessage.praseBody();
                Log.i(TAG, "HomeWorks response  " + responseMessage.body);

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
            

            if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS
                    && responseMessage.total > 0) {

                try {
                    ArrayList<HomeWorkInfo> homeWorkInfoList = JSONParser
                            .praseHomeWorks(responseMessage.body);
                    if (homeWorkInfoList != null && homeWorkInfoList.size() > 0) {
                        HomeWorkDao homeWorkDao = new HomeWorkDao(mXiaoYunTongApplication);
                        for (HomeWorkInfo homeWorkInfo : homeWorkInfoList) {
                            Log.i(TAG, "homeWorkInfo.id " + homeWorkInfo.id);
                            HomeWorkInfo info1  = homeWorkDao.queryHomeWorkByID(homeWorkInfo.id);
                            Log.i(TAG, "info1 " + info1);
                            if(info1 == null){
                                homeWorkTotal += 1;
                            }
                            Log.i(TAG, "homeWorkTotal " + homeWorkTotal);
//                            long insertResult = homeWorkDao.addHomeWork(homeWorkInfo);
//                            Log.i(TAG, "HomeWork insertResult " + insertResult);
                        }
                        homeWorkDao.colseDb();
                        homeWorkDao = null;
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

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

           
            if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS
                    && responseMessage.total > 0) {

                try {

                    ArrayList<CampusNotice> campusNoticeList = JSONParser
                            .praseCampusNotice(responseMessage.body);

                    if (campusNoticeList != null && campusNoticeList.size() > 0) {
                        CampusNoticeDao campusNoticeDao = new CampusNoticeDao(HomePageActivity.this);
                        for (CampusNotice campusNotice : campusNoticeList) {
                            Log.i(TAG, "campusid " + campusNotice.id);
                            CampusNotice campus = campusNoticeDao.queryCampusNoticeByID(campusNotice.id);
                            Log.i(TAG, "campus " + campus);
                            if(campus==null){
                                
                                campusNotieTotal += 1;
                                campusNoticeLists.add(0, campusNotice);
                            }
                            Log.i(TAG, "campusNotieTotal " + campusNotieTotal);
                        }
                        campusNoticeDao.colseDb();
                        campusNoticeDao = null;
                    };

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
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
            Log.i(TAG, "homeWorkTotal " + homeWorkTotal);
            Log.i(TAG, "campusNotieTotal " + campusNotieTotal);
            Log.i(TAG, "campusNoticeContentList.size() " + campusNoticeLists.size());
           
            if (campusNoticeLists != null && campusNoticeLists.size() > 0) {

                
                
                if (campusNoticeLists.size() > 0) {
                    if (rollTextThread != null && rollTextThread.isAlive()) {

                    } else {
                        rollTextThread = new RollTextThread();
                        rollTextThread.start();
                    }
                } 
//                else if (campusNoticeList.size() == 1) {
//                    String content = campusNoticeList.get(0).content;
//                    Log.i(TAG, "content " + content);
//                    rollNoteText.setText(content);
//                }

            }
            if (homeWorkTotal > 0 || campusNotieTotal > 0) {               
                //mImageAdapter.notifyDataSetChanged();
                getPoPoCount();
            }
            
        }

        @Override
        protected void onCancelled() {
            mGetAllNoteTask = null;

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

                mStudentList.clear();

                try {
                    ArrayList<StudentInfo> studentList = JSONParser
                            .parseChildInfo(responseMessage.body);
                    mStudentList.addAll(studentList);
                } catch (JSONException exception) {
                    exception.printStackTrace();
                }

                if (mStudentList.size() > 0) {
                    ParentInfo parentInfo = (ParentInfo) userInfo;

                    parentInfo.childList = mStudentList;
                    parentInfo.defalutChild = mStudentList.get(0);
                    mSpinnerInfo.clear();
                    for (StudentInfo studentInfo : mStudentList) {
                        mSpinnerInfo.add(studentInfo.stuName);
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
