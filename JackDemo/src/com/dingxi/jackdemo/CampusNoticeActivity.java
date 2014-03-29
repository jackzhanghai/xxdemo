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

import com.dingxi.jackdemo.HomeWorkActivity.GetHomeWorTask;
import com.dingxi.jackdemo.HomeWorkActivity.HomeWorkAdapter;
import com.dingxi.jackdemo.HomeWorkActivity.HomeWorkAdapter.HomeWorkHolder;
import com.dingxi.jackdemo.dao.CampusNoticeDao;
import com.dingxi.jackdemo.model.CampusNotice;
import com.dingxi.jackdemo.model.HomeWorkInfo;
import com.dingxi.jackdemo.model.UserInfo;
import com.dingxi.jackdemo.model.HomeWorkInfo.HomeWorkEntry;
import com.dingxi.jackdemo.model.UserInfo.UserType;
import com.dingxi.jackdemo.network.JSONParser;
import com.dingxi.jackdemo.network.RestClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class CampusNoticeActivity extends Activity {

    
    
    public static final String TAG = "CampusNoticeActivity";
    private View emptyView;
    private ImageButton mBackButton;
    private ImageButton mQueryMessageButton;
    private ImageButton mEditMessageButton;
    private Spinner mSpinner;
    private ListView mHomeWorkListView;
    private CampusNoticeAdapter mCampusNoticeAdapter;
    private ArrayList<CampusNotice> mCampusNoticeList;
    private ImageView loadingImageView;
    private AnimationDrawable loadingAnimation;
    private static UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    
    
    public GetmCampusNoticeListTask mHomeWorTask;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campus_notice);
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
        
        mSpinner = (Spinner) findViewById(R.id.main_spinner);
        
        mQueryMessageButton = (ImageButton) findViewById(R.id.query_button);
        mQueryMessageButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CampusNoticeActivity .this,SearchMessageActivity.class);
                startActivityForResult(intent, R.layout.activity_search_message);
                
            }
        });
        
        mEditMessageButton = (ImageButton) findViewById(R.id.edit_button);
        mEditMessageButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(CampusNoticeActivity.this,EditCampusNoticeActivity.class);
                startActivity(intent);
                
            }
        });
        
        emptyView = findViewById(R.id.empty);
        mHomeWorkListView = (ListView) findViewById(R.id.home_work_list);
        mHomeWorkListView.setEmptyView(emptyView);
        mCampusNoticeList = new ArrayList<CampusNotice>();
        mCampusNoticeAdapter = new CampusNoticeAdapter(CampusNoticeActivity.this, mCampusNoticeList);
        loadingImageView = (ImageView) findViewById(R.id.loading_message_image);
        loadingImageView.setBackgroundResource(R.drawable.loading_howework_animation);
        loadingAnimation = (AnimationDrawable) loadingImageView.getBackground();
        mHomeWorkListView.setAdapter(mCampusNoticeAdapter);
        mHomeWorkListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                // TODO Auto-generated method stub
                Log.i(TAG, "arg2 " +arg2 + " arg3 " + arg3);
                String id = mCampusNoticeList.get(arg2).id;
                Log.i(TAG, "id " + id );
                Intent intent = new Intent(CampusNoticeActivity.this,CampusNoticeDetailActivity.class);
                intent.putExtra(HomeWorkEntry.COLUMN_NAME_ENTRY_ID, id);
                startActivity(intent);
            }
            
        });
        

        if (curretUserInfo.roleType.equals(UserType.ROLE_TEACHER)) {
            mQueryMessageButton.setVisibility(View.VISIBLE);
            mEditMessageButton.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
        } else {
            mQueryMessageButton.setVisibility(View.GONE);
            mEditMessageButton.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);
        }

        loadingImageView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        loadingAnimation.start();
        if(curretUserInfo.roleType == UserType.ROLE_TEACHER){
            mHomeWorTask = new GetmCampusNoticeListTask(1, 5, curretUserInfo.fkSchoolId, curretUserInfo.fkClassId, "", "", "");
        } else  if(curretUserInfo.roleType == UserType.ROLE_PARENT){
            mHomeWorTask = new GetmCampusNoticeListTask(1, 5, curretUserInfo.fkSchoolId, "", "", mXiaoYunTongApplication.deflutStudentInfo.id, "");
        }
       
        mHomeWorTask.execute((Void) null);
    }

    

    class CampusNoticeAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<CampusNotice> campusNoticeList;
        private LayoutInflater inflater;

        private CampusNoticeAdapter(Context context, ArrayList<CampusNotice> campusNoticeList) {

            this.context = context;
            inflater = LayoutInflater.from(context);
            this.campusNoticeList = campusNoticeList;
            // TODO Auto-generated method stub
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return campusNoticeList.size();
        }

        @Override
        public Object getItem(int position) {//HomeWorkInfo
            // TODO Auto-generated method stub
            
            //homeWorkList.get(position)
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            String id = campusNoticeList.get(position).id;
            //Long.parseLong(id)
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i(TAG, "getView()");
            HomeWorkHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.homework_item, null);
                viewHolder = new HomeWorkHolder();
                viewHolder.headerText = (TextView) convertView.findViewById(R.id.message_header);
                viewHolder.bodyText = (TextView) convertView.findViewById(R.id.message_body);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (HomeWorkHolder) convertView.getTag();
            }

            Log.i(TAG, "homeWorkList.get(position).getId() " + campusNoticeList.get(position).id);
            viewHolder.headerText.setText(campusNoticeList.get(position).id);
            Log.i(TAG, "homeWorkList.get(position).getContent() " + campusNoticeList.get(position).content);
            viewHolder.bodyText.setText(campusNoticeList.get(position).content);

            return convertView;
        }

        class HomeWorkHolder {

            TextView headerText;
            TextView bodyText;

        }
    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        
        if(resultCode == RESULT_OK && requestCode == R.layout.activity_search_message){
            Bundle  searchBundle =   data.getExtras();           
            searchBundle.getString("mGradeId");
           String classId2 = searchBundle.getString("mClassId");
          String studentId =  searchBundle.getString("mStudentId");
          String date =  searchBundle.getString("mData");
          mCampusNoticeList.clear();
            mCampusNoticeAdapter.notifyDataSetChanged();
            
            loadingImageView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            loadingAnimation.start();
            if(curretUserInfo.roleType == UserType.ROLE_TEACHER){
                mHomeWorTask = new GetmCampusNoticeListTask(1, 5, curretUserInfo.fkSchoolId, curretUserInfo.fkClassId, classId2, studentId, date);
            } else {
                mHomeWorTask = new GetmCampusNoticeListTask(1, 5, curretUserInfo.fkSchoolId, "", "", mXiaoYunTongApplication.deflutStudentInfo.id, date);
            }
            mHomeWorTask.execute((Void) null);
            
        }
    }
    
    
    
    public class GetmCampusNoticeListTask extends AsyncTask<Void, Void, String> {
        
        int page,row;
        String fkSchoolId ;
        String fkClassId;
        String fkClassId2;
        String fkStudentId;
        String queryTime;
        
        public GetmCampusNoticeListTask(int page, int rows, String fkSchoolId, String fkClassId,String fkClassId2, String fkStudentId, String queryTime) {
           this.page = page;
           this.row = rows;
           this.queryTime =  queryTime;
           this.fkSchoolId =  fkSchoolId;
           this.fkClassId =  fkClassId;
           this.fkClassId2 =  fkClassId2;
           this.fkStudentId =  fkStudentId;
        }
        
        
        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            StringBuilder info = new StringBuilder();
            info.append("{");
            info.append("\"fkSchoolId\":");
            info.append("\"" + fkSchoolId + "\"");
            info.append(",");
            if (curretUserInfo.roleType == UserInfo.UserType.ROLE_TEACHER) {
                info.append("\"fkClassId\":");
                info.append("\"" + fkClassId + "\"");
                info.append(",");

                info.append("\"fkClassId2\":");
                info.append("\"" + fkClassId2 + "\"");
                info.append(",");
            } else if (curretUserInfo.roleType == UserInfo.UserType.ROLE_PARENT) {
                info.append("\"fkStudentId\":");
                info.append("\"" + mXiaoYunTongApplication.deflutStudentInfo.id + "\"");
                info.append(",");
            }

            info.append("\"queryTime\":");
            info.append("\""+queryTime+"\"");
            info.append("}");

            Log.d(TAG, "info.toString() " + info.toString());
            
            String homeWorkInfo;
            try {
                homeWorkInfo = RestClient.getMessageInfos(curretUserInfo.id, curretUserInfo.ticket, curretUserInfo.roleType.toString(), info.toString(), page, row);
            } catch (ConnectTimeoutException stex) {
                homeWorkInfo = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                homeWorkInfo = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                homeWorkInfo = getString(R.string.connection_server_error);
            } catch (XmlPullParserException e) {
                homeWorkInfo = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (IOException e) {
                homeWorkInfo = getString(R.string.connection_error);
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return homeWorkInfo;
        }

        @Override
        protected void onPostExecute(String homeWorkInfo) {
            mHomeWorTask = null;
            Log.d(TAG, "result " + homeWorkInfo);
            if (!TextUtils.isEmpty(homeWorkInfo)) {

                try {
                    if (JSONParser.getIntByTag(homeWorkInfo, RestClient.RESULT_TAG_CODE) == RestClient.RESULT_TAG_SUCCESS) {

                        try {
                            if (JSONParser.getStringByTag(homeWorkInfo, "datas") != null) {
                                praseMessageInfos(homeWorkInfo);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        
                        Log.d(TAG, "mHomeWorkList.size() " + mCampusNoticeList.size());
                        loadingAnimation.stop();
                        loadingImageView.setVisibility(View.GONE);
                        mCampusNoticeAdapter.notifyDataSetChanged();

                    } else {
                        loadingAnimation.stop();
                        loadingImageView.setVisibility(View.GONE);
                        emptyView.setVisibility(View.VISIBLE);
                        String errorMessage = JSONParser.getStringByTag(homeWorkInfo,
                                RestClient.RESULT_TAG_MESSAGE);
                        if (!TextUtils.isEmpty(errorMessage)) {
                            Toast.makeText(CampusNoticeActivity.this, errorMessage, Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(CampusNoticeActivity.this, homeWorkInfo, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                } catch (JSONException e) {
                    loadingAnimation.stop();
                    loadingImageView.setVisibility(View.GONE);
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(CampusNoticeActivity.this, homeWorkInfo, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                loadingAnimation.stop();
                loadingImageView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);

            }
        }

        @Override
        protected void onCancelled() {
            mHomeWorTask = null;

        }
    }
    
    
    private void praseMessageInfos(String homeWorksInfo) throws JSONException {
        // TODO Auto-generated method stub
        JSONObject jsonObj = new JSONObject(homeWorksInfo);
        int total = jsonObj.getInt(RestClient.RESULT_TAG_TOTAL);

        if (jsonObj.has(RestClient.RESULT_TAG_DATAS) && total > 0) {
            JSONArray data = jsonObj.getJSONArray(RestClient.RESULT_TAG_DATAS);


            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);
                mCampusNoticeList.clear();
                // {"fkGradeId":"3","optTime":"2014-03-20 13:28:35","fkClassId":"2","status":"","fkSubjectId":0,"smsType":"1",
                // "fkSchoolId":2,"sendType":"1","id":"402881f344dd52b00144dd54f6680001",
                // "content":"我是测试数据","className":"初一年级1班","fkStudentId":"402881f144d8e6200144d90fea740011","stuName":"bbbbb"}
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

                mCampusNoticeList.add(campusNotice);
            }
            if (mCampusNoticeList.size() > 0) {

            }
        }

    }


}
