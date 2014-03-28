package com.dingxi.jackdemo;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import com.dingxi.jackdemo.model.CampusNotice;
import com.dingxi.jackdemo.model.ClassInfo;
import com.dingxi.jackdemo.model.HomeWorkInfo;
import com.dingxi.jackdemo.model.StudentInfo;
import com.dingxi.jackdemo.model.UserInfo;
import com.dingxi.jackdemo.model.UserInfo.UserType;
import com.dingxi.jackdemo.network.JSONParser;
import com.dingxi.jackdemo.network.RestClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomePageActivity extends Activity implements OnItemSelectedListener {

    protected static final String TAG = "HomePageActivity";
    private Spinner mSpinner;
    // private String mRoleId = "0";
    private ImageButton backButton;
    // private ImageButton editButton;
    // private ImageButton queryButton;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private static UserInfo curretUserInfo;
    private GetAllClassTask mGetAllClassTask;
    private GetAllChildTask mGetAllChildTask;
    private ProgressDialog mProgressDialog;
    private ArrayList<ClassInfo> mClassInfoList;
    private ArrayList<StudentInfo> mStudentList;
    private ArrayList<String> mSpinnerInfo;
    private ArrayAdapter<String> mAdapter;
   
    private GetAllNoteTask mGetAllNoteTask;
    private int homeWorkTotal;
    private int campusNotieTotal;
    private ImageAdapter mImageAdapter;
    private TextView rollNoteText;
    private ArrayList<CampusNotice> campusNoticeList = new ArrayList<CampusNotice>();;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();

        curretUserInfo = mXiaoYunTongApplication.userInfo;

        GridView gridview = (GridView) findViewById(R.id.gridview);
        rollNoteText = (TextView) findViewById(R.id.roll_note_text);
        mImageAdapter = new ImageAdapter(this);
        gridview.setAdapter(mImageAdapter);

        backButton = (ImageButton) findViewById(R.id.back_button);

        backButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent backIntent = new Intent(HomePageActivity.this, LoginActivity.class);
                backIntent.putExtra("auto", false);
                startActivity(backIntent);
                finish();
            }
        });
        
        
      

        // editButton = (ImageButton) findViewById(R.id.edit_button);
        // editButton.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // // TODO Auto-generated method stub
        //
        // }
        // });
        //
        //
        //
        // queryButton = (ImageButton) findViewById(R.id.query_button);

        mSpinner = (Spinner) findViewById(R.id.main_spinner);
        mSpinner.setOnItemSelectedListener(this);
        // mSpinner.setEmptyView(emptyView)

        mSpinnerInfo = new ArrayList<String>();

        if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
            mSpinner.setVisibility(View.VISIBLE);
            mSpinner.setPromptId(R.string.please_check_child);
            mStudentList = new ArrayList<StudentInfo>();
            mAdapter = new ArrayAdapter<String>(HomePageActivity.this,
                    android.R.layout.simple_spinner_item, mSpinnerInfo);
        } else {// if (curretUserInfo.roleType == UserType.ROLE_TEACHER)
            mClassInfoList = new ArrayList<ClassInfo>();
            mSpinner.setVisibility(View.GONE);
            mSpinner.setPromptId(R.string.please_check_class);
            mAdapter = new ArrayAdapter<String>(HomePageActivity.this,
                    android.R.layout.simple_spinner_item, mSpinnerInfo);
        }
        mAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(mAdapter);

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
                    Intent intent = new Intent(HomePageActivity.this, OfficialSiteActivity.class);
                    startActivity(intent);
                } else if (position == 4) {
                    Intent intent = new Intent(HomePageActivity.this, ModifyPassWordActivity.class);
                    startActivity(intent);
                } else if (position == 5) {
                    if (!TextUtils.isEmpty("imei")) {
                        Intent intent = new Intent(HomePageActivity.this,
                                LocationInfoActivity.class);
                        Log.i(TAG, "deflutStudentInfo.name " + mXiaoYunTongApplication.deflutStudentInfo.name);
                        Log.i(TAG, "deflutStudentInfo.imei " + mXiaoYunTongApplication.deflutStudentInfo.imei);
                        Log.i(TAG, "deflutStudentInfo.id " + mXiaoYunTongApplication.deflutStudentInfo.id);

                        intent.putExtra("imei", mXiaoYunTongApplication.deflutStudentInfo.imei);
                        startActivity(intent);
                    }

                }

            }
        });

        mProgressDialog = new ProgressDialog(HomePageActivity.this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
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
        } else if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {

            mGetAllNoteTask = new GetAllNoteTask();
            mGetAllNoteTask.execute((Void) null);
            // mProgressDialog.setMessage(getString(R.string.now_geting_classinfo));
            //
            // mProgressDialog.setOnCancelListener(new OnCancelListener() {
            //
            // @Override
            // public void onCancel(DialogInterface dialog) {
            // // TODO Auto-generated method stub
            // if (mGetAllClassTask != null
            // && !mGetAllClassTask.isCancelled()) {
            // mGetAllClassTask.cancel(true);
            // // isCancelLogin = true;
            // }
            // }
            // });
            //
            // mProgressDialog.show();
            // mGetAllClassTask = new GetAllClassTask();
            // mGetAllClassTask.execute((Void) null);
        }

    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
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
     // Set other dialog properties

     // Create the AlertDialog
     AlertDialog dialog = builder.create();
        
    }

    class RollTextThread extends Thread {

        @Override
        public void run() {
            super.run();

        }
    }

    public class ImageAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public ImageAdapter(Context c) {
            inflater = LayoutInflater.from(HomePageActivity.this);
        }

        public int getCount() {
            int count = 0;
            if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
                count = parentPictures.length;
            } else {
                count = teacherPictures.length;
            }
            return count;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            int itemId;
            if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
                itemId = parentTitles[position];
            } else {
                itemId = teacherTitles[position];

            }
            return itemId;
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
            }
            //

            if (campusNotieTotal > 0 && position == 1) {

                viewHolder.numberText.setVisibility(View.VISIBLE);
                viewHolder.numberText.setText(String.valueOf(campusNotieTotal));
                convertView.setBackgroundResource(R.drawable.button_down);
            }

            if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
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

        // references to our images
        private Integer[] teacherPictures = { R.drawable.homework, R.drawable.message,
                R.drawable.check, R.drawable.officialweb, R.drawable.change_password };
        private Integer[] teacherTitles = { R.string.home_work, R.string.system_note,
                R.string.attendance_information, R.string.official_site, R.string.change_password };
        private Integer[] parentPictures = { R.drawable.homework, R.drawable.message,
                R.drawable.check, R.drawable.officialweb, R.drawable.change_password,
                R.drawable.position };
        private Integer[] parentTitles = { R.string.home_work, R.string.system_note,
                R.string.attendance_information, R.string.official_site, R.string.change_password,
                R.string.position_orientation };
    }

    public class GetAllClassTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String schoolsInfo = null;
            try {
                schoolsInfo = RestClient.getClassInfos(curretUserInfo.id, curretUserInfo.ticket,
                        curretUserInfo.fkClassId);
            } catch (ConnectTimeoutException stex) {
                schoolsInfo = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                schoolsInfo = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                schoolsInfo = getString(R.string.connection_server_error);
            } catch (XmlPullParserException e) {
                schoolsInfo = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (IOException e) {
                schoolsInfo = getString(R.string.connection_error);
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return schoolsInfo;
        }

        @Override
        protected void onPostExecute(String schoolsInfo) {
            mGetAllClassTask = null;
            Log.d(TAG, "result " + schoolsInfo);
            if (!TextUtils.isEmpty(schoolsInfo)) {

                try {
                    if (JSONParser.getIntByTag(schoolsInfo, RestClient.RESULT_TAG_CODE) == RestClient.RESULT_TAG_SUCCESS) {

                        try {
                            if (JSONParser.getStringByTag(schoolsInfo, "datas") != null) {

                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        parseClassInfo(schoolsInfo);

                        mProgressDialog.dismiss();

                    } else {
                        mProgressDialog.dismiss();
                        String errorMessage = JSONParser.getStringByTag(schoolsInfo,
                                RestClient.RESULT_TAG_MESSAGE);
                        if (!TextUtils.isEmpty(errorMessage)) {
                            Toast.makeText(HomePageActivity.this, errorMessage, Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(HomePageActivity.this, schoolsInfo, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                } catch (JSONException e) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }

                    Toast.makeText(HomePageActivity.this, schoolsInfo, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                mProgressDialog.dismiss();

            }
        }

        private void parseClassInfo(String schoolsInfo) {
            // TODO Auto-generated method stub
            try {
                if (JSONParser.getStringByTag(schoolsInfo, "") != null) {

                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            mGetAllClassTask = null;

        }
    }

    public class GetAllNoteTask extends AsyncTask<Void, String, String> {
        @Override
        protected String doInBackground(Void... params) {
            String homeWorksInfo = null;
            String messageInfos = null;
            StringBuilder info = new StringBuilder();
            try {

                // 老师info：
                // {"fkSchoolId":"2","fkClassId":"0","fkClassId2":"2",”queryTime”:”2014-03-24”}
                // 家长info:{"fkSchoolId":"2","fkStudentId":"402881f144d911a10144d916319c0002",”queryTime”:”2014-03-24”}

                info.append("{");
                info.append("\"fkSchoolId\":");
                info.append("\"" + curretUserInfo.fkSchoolId + "\"");
                info.append(",");
                if (curretUserInfo.roleType == UserInfo.UserType.ROLE_TEACHER) {
                    info.append("\"fkClassId\":");
                    info.append("\"" + curretUserInfo.fkClassId + "\"");
                    info.append(",");

                    info.append("\"fkClassId2\":");
                    info.append("\"\"");
                    info.append(",");
                } else if (curretUserInfo.roleType == UserInfo.UserType.ROLE_PARENT) {
                    info.append("\"fkStudentId\":");
                    info.append("\"" + mXiaoYunTongApplication.deflutStudentInfo.id + "\"");
                    info.append(",");
                }

                info.append("\"queryTime\":");
                info.append("\"\"");
                info.append("}");

                Log.d(TAG, "info.toString() " + info.toString());

                homeWorksInfo = RestClient.getHomeWorks(curretUserInfo.id, curretUserInfo.ticket,
                        curretUserInfo.roleType.toString(), info.toString(), 1, 5);
            } catch (ConnectTimeoutException stex) {
                homeWorksInfo = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                homeWorksInfo = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                homeWorksInfo = getString(R.string.connection_server_error);
            } catch (XmlPullParserException e) {
                homeWorksInfo = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (IOException e) {
                homeWorksInfo = getString(R.string.connection_error);
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(homeWorksInfo)) {

                try {
                    if (JSONParser.getIntByTag(homeWorksInfo, RestClient.RESULT_TAG_CODE) == RestClient.RESULT_TAG_SUCCESS) {

                        homeWorkTotal = JSONParser.getIntByTag(homeWorksInfo, "total");
                        try {
                            if (JSONParser.getStringByTag(homeWorksInfo, "datas") != null) {
                                praseHomeWorks(homeWorksInfo);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                } catch (Exception exception) {

                }
            }
            Log.i(TAG, "homeWorksinfo " + homeWorksInfo);

            // 老师info：
            // {"fkSchoolId":"2","fkClassId":"0","fkClassId2":"2",”queryTime”:”2014-03-24”}
            // 家长info:{"fkSchoolId":"2","fkStudentId":"402881f144d911a10144d916319c0002",”queryTime”:”2014-03-24”}
            try {

                messageInfos = RestClient.getMessageInfos(curretUserInfo.id, curretUserInfo.ticket,
                        curretUserInfo.roleType.toString(), info.toString(), 1, 5);
            } catch (ConnectTimeoutException stex) {
                messageInfos = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                messageInfos = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                messageInfos = getString(R.string.connection_server_error);
            } catch (XmlPullParserException e) {
                messageInfos = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (IOException e) {
                messageInfos = getString(R.string.connection_error);
                e.printStackTrace();
            }

            if (!TextUtils.isEmpty(homeWorksInfo)) {

                try {
                    if (JSONParser.getIntByTag(messageInfos, RestClient.RESULT_TAG_CODE) == RestClient.RESULT_TAG_SUCCESS) {

                        campusNotieTotal = JSONParser.getIntByTag(messageInfos, "total");
                        try {
                            if (JSONParser.getStringByTag(homeWorksInfo, "datas") != null) {
                                praseMessageInfos(messageInfos);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                    }
                } catch (Exception exception) {

                }
            }
            Log.i(TAG, "MessageInfos " + messageInfos);

            // TODO: register the new account here.
            return messageInfos;
        }

        private void praseMessageInfos(String messageInfos) throws JSONException {
            // TODO Auto-generated method stub
            JSONObject jsonObj = new JSONObject(messageInfos);
            int total = jsonObj.getInt(RestClient.RESULT_TAG_TOTAL);

            if (jsonObj.has(RestClient.RESULT_TAG_DATAS) && total > 0) {
                JSONArray data = jsonObj.getJSONArray(RestClient.RESULT_TAG_DATAS);
                campusNoticeList.clear();
                
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = (JSONObject) data.get(i);

                    CampusNotice campusNotice = new CampusNotice();
                    campusNotice.content = obj.getString("content");
                    campusNotice.id = obj.getString("id");                   
                    campusNotice.optTime = obj.getString("optTime");
                    campusNotice.fkGradeId = obj.getInt("fkGradeId");
                    campusNotice.status = obj.getString("status");
                    campusNotice.fkClassId = obj.getInt("fkClassId");
                    campusNotice.smsType = obj.getInt("smsType");
                    campusNotice.fkSchoolId = obj.getInt("fkSchoolId");
                    campusNotice.className = obj.getString("className");
                    campusNotice.sendType = obj.getInt("sendType");
                    campusNotice.stuName = obj.getString("stuName");
                    campusNotice.fkStudentId = obj.getString("fkStudentId");
                   
                    
                    
                    
                    
                    
                    

                    campusNoticeList.add(campusNotice);
                }
                Log.i(TAG, "campusNoticeList.size() " + campusNoticeList.size());
                if (campusNoticeList.size() > 0) {

                }
            }

        }

        private void praseHomeWorks(String homeWorksInfo) throws JSONException {
            // TODO Auto-generated method stub
            JSONObject jsonObj = new JSONObject(homeWorksInfo);
            int total = jsonObj.getInt(RestClient.RESULT_TAG_TOTAL);

            if (jsonObj.has(RestClient.RESULT_TAG_DATAS) && total > 0) {
                JSONArray data = jsonObj.getJSONArray(RestClient.RESULT_TAG_DATAS);

                ArrayList<HomeWorkInfo> homeWorkInfoList = new ArrayList<HomeWorkInfo>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = (JSONObject) data.get(i);

                    // {"fkGradeId":"3","optTime":"2014-03-20 13:28:35","fkClassId":"2","status":"","fkSubjectId":0,"smsType":"1",
                    // "fkSchoolId":2,"sendType":"1","id":"402881f344dd52b00144dd54f6680001",
                    // "content":"我是测试数据","className":"初一年级1班","fkStudentId":"402881f144d8e6200144d90fea740011","stuName":"bbbbb"}
                    HomeWorkInfo homeWorkInfo = new HomeWorkInfo();

                    homeWorkInfo.content = obj.getString("content");
                    homeWorkInfo.id = obj.getString("id");
                    homeWorkInfo.optTime = obj.getString("optTime");
                    homeWorkInfo.fkGradeId = obj.getInt("fkGradeId");
                    homeWorkInfo.status = obj.getString("status");
                    homeWorkInfo.fkClassId = obj.getInt("fkClassId");
                    homeWorkInfo.fkSubjectId = obj.getInt("fkSubjectId");
                    homeWorkInfo.smsType = obj.getInt("smsType");
                    homeWorkInfo.fkSchoolId = obj.getInt("fkSchoolId");
                    homeWorkInfo.className = obj.getString("className");
                    homeWorkInfo.sendType = obj.getInt("sendType");
                    homeWorkInfo.fkStudentId = obj.getString("fkStudentId");
                    

                    homeWorkInfoList.add(homeWorkInfo);
                }
                if (homeWorkInfoList.size() > 0) {

                }
            }

        }

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            Log.i(TAG, "GetAllNoteTask run()");
        }
     

        @Override
        protected void onPostExecute(String schoolsInfo) {
            mGetAllNoteTask = null;
            Log.d(TAG, "result " + schoolsInfo);
            Log.i(TAG, "homeWorkTotal " + homeWorkTotal);
            Log.i(TAG, "campusNotieTotal " + campusNotieTotal);
            Log.i(TAG, "campusNoticeList.size() " + campusNoticeList.size());
            
            if(campusNoticeList!=null && campusNoticeList.size()>0){
                
                if (campusNoticeList.size() > 1) {
                    new Thread(new Runnable() {
                        public void run() {
                            rollNoteText.post(new Runnable() {
                                public void run() {

                                    
                                    for (int i = 0; i < campusNoticeList.size(); i++) {
                                        CampusNotice campusNotice =  campusNoticeList.get(i);
                                        rollNoteText.setText(campusNotice.content);
                                        try {
                                            Thread.sleep(10000);
                                        } catch (InterruptedException e) {
                                            // TODO Auto-generated catch block
                                            e.printStackTrace();
                                        }
                                    }
                                    
                                }
                            });
                        }
                    }).start();
                } else if (campusNoticeList.size() == 1) {
                    CampusNotice campusNotice = campusNoticeList.get(0);
                    rollNoteText.setText(campusNotice.content);
                }
                
            }
            

            if (homeWorkTotal > 0 || campusNotieTotal > 0) {

                mImageAdapter.notifyDataSetChanged();
            }
            if (!TextUtils.isEmpty(schoolsInfo)) {

                try {
                    if (JSONParser.getIntByTag(schoolsInfo, RestClient.RESULT_TAG_CODE) == RestClient.RESULT_TAG_SUCCESS) {

                        try {
                            if (JSONParser.getStringByTag(schoolsInfo, "datas") != null) {
                                // parseChildInfo(schoolsInfo);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }

                        mProgressDialog.dismiss();

                    } else {
                        mProgressDialog.dismiss();
                        String errorMessage = JSONParser.getStringByTag(schoolsInfo,
                                RestClient.RESULT_TAG_MESSAGE);
                        if (!TextUtils.isEmpty(errorMessage)) {
                            Toast.makeText(HomePageActivity.this, errorMessage, Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(HomePageActivity.this, schoolsInfo, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                } catch (JSONException e) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }

                    Toast.makeText(HomePageActivity.this, schoolsInfo, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                mProgressDialog.dismiss();

            }
        }

        private void parseChildInfo(String childInfo) throws JSONException {
            // TODO Auto-generated method stub
            JSONObject jsonObj = new JSONObject(childInfo);
            int total = jsonObj.getInt(RestClient.RESULT_TAG_TOTAL);

            if (jsonObj.has(RestClient.RESULT_TAG_DATAS) && total > 0) {
                JSONArray data = jsonObj.getJSONArray(RestClient.RESULT_TAG_DATAS);
                mStudentList.clear();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = (JSONObject) data.get(i);

                    StudentInfo studentInfo = new StudentInfo();

                    studentInfo.imei = obj.getString("imei");
                    studentInfo.id = obj.getString("id");
                    studentInfo.name = obj.getString("stuName");
                    mStudentList.add(studentInfo);
                }
            }

            if (mStudentList.size() > 0) {
                mXiaoYunTongApplication.deflutStudentInfo = mStudentList.get(0);
                mSpinnerInfo.clear();
                for (StudentInfo studentInfo : mStudentList) {
                    mSpinnerInfo.add(studentInfo.name);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mGetAllNoteTask = null;

        }
    }

    public class GetAllChildTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String schoolsInfo = null;
            try {

                schoolsInfo = RestClient.getAllChilds(curretUserInfo.id, curretUserInfo.fkSchoolId,
                        curretUserInfo.ticket);
            } catch (ConnectTimeoutException stex) {
                schoolsInfo = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                schoolsInfo = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                schoolsInfo = getString(R.string.connection_server_error);
            } catch (XmlPullParserException e) {
                schoolsInfo = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (IOException e) {
                schoolsInfo = getString(R.string.connection_error);
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return schoolsInfo;
        }

        @Override
        protected void onPostExecute(String schoolsInfo) {
            mGetAllChildTask = null;
            Log.d(TAG, "result " + schoolsInfo);
            if (!TextUtils.isEmpty(schoolsInfo)) {

                try {
                    if (JSONParser.getIntByTag(schoolsInfo, RestClient.RESULT_TAG_CODE) == RestClient.RESULT_TAG_SUCCESS) {

                        try {
                            if (JSONParser.getStringByTag(schoolsInfo, "datas") != null) {
                                parseChildInfo(schoolsInfo);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        mGetAllNoteTask = new GetAllNoteTask();
                        mGetAllNoteTask.execute((Void) null);

                        mProgressDialog.dismiss();

                    } else {
                        mProgressDialog.dismiss();
                        String errorMessage = JSONParser.getStringByTag(schoolsInfo,
                                RestClient.RESULT_TAG_MESSAGE);
                        if (!TextUtils.isEmpty(errorMessage)) {
                            Toast.makeText(HomePageActivity.this, errorMessage, Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(HomePageActivity.this, schoolsInfo, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                } catch (JSONException e) {
                    if (mProgressDialog != null) {
                        mProgressDialog.dismiss();
                    }

                    Toast.makeText(HomePageActivity.this, schoolsInfo, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                mProgressDialog.dismiss();

            }
        }

        private void parseChildInfo(String childInfo) throws JSONException {
            // TODO Auto-generated method stub
            JSONObject jsonObj = new JSONObject(childInfo);
            int total = jsonObj.getInt(RestClient.RESULT_TAG_TOTAL);

            if (jsonObj.has(RestClient.RESULT_TAG_DATAS) && total > 0) {
                JSONArray data = jsonObj.getJSONArray(RestClient.RESULT_TAG_DATAS);
                mStudentList.clear();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject obj = (JSONObject) data.get(i);

                    StudentInfo studentInfo = new StudentInfo();

                    studentInfo.imei = obj.getString("imei");
                    studentInfo.id = obj.getString("id");
                    studentInfo.name = obj.getString("stuName");
                    mStudentList.add(studentInfo);
                }
            }

            if (mStudentList.size() > 0) {
                mXiaoYunTongApplication.deflutStudentInfo = mStudentList.get(0);
                mSpinnerInfo.clear();
                for (StudentInfo studentInfo : mStudentList) {
                    mSpinnerInfo.add(studentInfo.name);
                    mAdapter.notifyDataSetChanged();
                }
            }
        }

        @Override
        protected void onCancelled() {
            mGetAllChildTask = null;

        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // TODO Auto-generated method stub
        Log.i(TAG, " position " + position + " id " + id);
        if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
            if (mStudentList.size() < 0) {
                mSpinnerInfo.add("请选择孩子");
            } else {

            }
            mXiaoYunTongApplication.deflutStudentInfo = mStudentList.get(position);
        } else {

        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
        Log.i(TAG, " onNothingSelected() ");
    }

}
