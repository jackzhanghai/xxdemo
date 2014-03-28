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

import com.dingxi.jackdemo.HomePageActivity.ImageAdapter.ViewHolder;
import com.dingxi.jackdemo.LoginActivity.UserLoginTask;
import com.dingxi.jackdemo.model.HomeWorkInfo;
import com.dingxi.jackdemo.model.UserInfo;
import com.dingxi.jackdemo.model.UserInfo.UserType;
import com.dingxi.jackdemo.network.JSONParser;
import com.dingxi.jackdemo.network.RestClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class HomeWorkActivity extends Activity {

    public static final String TAG = "HomeWorkActivity";
    private View parentHeader;
    private ImageButton mBackButton;
    private ImageButton mQueryMessageButton;
    private ImageButton mEditMessageButton;
    private Spinner mSpinner;
    private ListView mHomeWorkListView;
    private HomeWorkAdapter mHomeWorkAdapter;
    private ArrayList<HomeWorkInfo> mHomeWorkList;
    private ImageView loadingImageView;
    private AnimationDrawable loadingAnimation;
    private static UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;

 
    public GetHomeWorTask mHomeWorTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work);

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

		curretUserInfo = mXiaoYunTongApplication.userInfo;
        
        mSpinner = (Spinner) findViewById(R.id.main_spinner);
        
        mQueryMessageButton = (ImageButton) findViewById(R.id.query_button);
        mQueryMessageButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
            }
        });
        
        mEditMessageButton = (ImageButton) findViewById(R.id.edit_button);
        mEditMessageButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
            }
        });
        
        mHomeWorkListView = (ListView) findViewById(R.id.home_work_list);
        mHomeWorkListView.setEmptyView(findViewById(R.id.empty));
        mHomeWorkList = new ArrayList<HomeWorkInfo>();
        mHomeWorkAdapter = new HomeWorkAdapter(HomeWorkActivity.this, mHomeWorkList);
        loadingImageView = (ImageView) findViewById(R.id.loading_message_image);
        loadingImageView.setBackgroundResource(R.drawable.loading_howework_animation);
        loadingAnimation = (AnimationDrawable) loadingImageView.getBackground();
        mHomeWorkListView.setAdapter(mHomeWorkAdapter);
        mHomeWorkListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				
				
				
				Intent intent = new Intent(HomeWorkActivity.this,HomeWorkDetailActivity.class);
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
        loadingAnimation.start();
        mHomeWorTask = new GetHomeWorTask(1, 5, "");
        mHomeWorTask.execute((Void) null);
    }

    class HomeWorkAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<HomeWorkInfo> homeWorkList;
        private LayoutInflater inflater;

        private HomeWorkAdapter(Context context, ArrayList<HomeWorkInfo> homeWorkList) {

            this.context = context;
            inflater = LayoutInflater.from(context);
            this.homeWorkList = homeWorkList;
            // TODO Auto-generated method stub
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return homeWorkList.size();
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
            String id = homeWorkList.get(position).getId();
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

            Log.i(TAG, "homeWorkList.get(position).getId() " + homeWorkList.get(position).getId());
            viewHolder.headerText.setText(homeWorkList.get(position).getId());
            Log.i(TAG, "homeWorkList.get(position).getContent() " + homeWorkList.get(position).getContent());
            viewHolder.bodyText.setText(homeWorkList.get(position).getContent());

            return convertView;
        }

        class HomeWorkHolder {

            TextView headerText;
            TextView bodyText;

        }
    }

    
    private void praseHomeWorks(String homeWorksInfo) throws JSONException {
        // TODO Auto-generated method stub
        JSONObject jsonObj = new JSONObject(homeWorksInfo);
        int total = jsonObj.getInt(RestClient.RESULT_TAG_TOTAL);

        if (jsonObj.has(RestClient.RESULT_TAG_DATAS) && total > 0) {
            JSONArray data = jsonObj.getJSONArray(RestClient.RESULT_TAG_DATAS);


            for (int i = 0; i < data.length(); i++) {
                JSONObject obj = (JSONObject) data.get(i);
                mHomeWorkList.clear();
                // {"fkGradeId":"3","optTime":"2014-03-20 13:28:35","fkClassId":"2","status":"","fkSubjectId":0,"smsType":"1",
                // "fkSchoolId":2,"sendType":"1","id":"402881f344dd52b00144dd54f6680001",
                // "content":"我是测试数据","className":"初一年级1班","fkStudentId":"402881f144d8e6200144d90fea740011","stuName":"bbbbb"}
                HomeWorkInfo homeWorkInfo = new HomeWorkInfo();

                homeWorkInfo.fkGradeId = obj.getInt("fkGradeId");
                homeWorkInfo.id = obj.getString("id");
                homeWorkInfo.content = obj.getString("content");
                homeWorkInfo.sendType = obj.getInt("sendType");
                homeWorkInfo.status = obj.getString("status");
                homeWorkInfo.fkClassId = obj.getInt("fkClassId");
                homeWorkInfo.fkSubjectId = obj.getInt("fkSubjectId");
                homeWorkInfo.smsType = obj.getInt("smsType");
                homeWorkInfo.fkSchoolId = obj.getInt("fkSchoolId");
                homeWorkInfo.className = obj.getString("id");
                homeWorkInfo.fkStudentId = obj.getString("fkStudentId");
                //homeWorkInfo.stuName = obj.getString("stuName");
                // studentInfo.optTime = obj.getInt("optTime");

                mHomeWorkList.add(homeWorkInfo);
            }
            if (mHomeWorkList.size() > 0) {

            }
        }

    }
    
    public class GetHomeWorTask extends AsyncTask<Void, Void, String> {
        
        int page,row;
        String queryTime;
        
        public GetHomeWorTask(int page, int rows, String queryTime) {
           this.page = page;
           this.row = rows;
           this.queryTime =  queryTime;
        }
        
        
        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            StringBuilder info = new StringBuilder();
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
            info.append("\""+queryTime+"\"");
            info.append("}");

            Log.d(TAG, "info.toString() " + info.toString());
            
            String homeWorkInfo;
            try {
                homeWorkInfo = RestClient.getHomeWorks(curretUserInfo.id, curretUserInfo.ticket, curretUserInfo.roleType.toString(), info.toString(), 1, 5);
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
                                praseHomeWorks(homeWorkInfo);
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        
                        Log.d(TAG, "mHomeWorkList.size() " + mHomeWorkList.size());
                        loadingAnimation.stop();
                        loadingImageView.setVisibility(View.GONE);
                        mHomeWorkAdapter.notifyDataSetChanged();

                    } else {
                        loadingAnimation.stop();
                        loadingImageView.setVisibility(View.GONE);
                        String errorMessage = JSONParser.getStringByTag(homeWorkInfo,
                                RestClient.RESULT_TAG_MESSAGE);
                        if (!TextUtils.isEmpty(errorMessage)) {
                            Toast.makeText(HomeWorkActivity.this, errorMessage, Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(HomeWorkActivity.this, homeWorkInfo, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                } catch (JSONException e) {
                    loadingAnimation.stop();
                    loadingImageView.setVisibility(View.GONE);
                    Toast.makeText(HomeWorkActivity.this, homeWorkInfo, Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            } else {
                loadingAnimation.stop();
                loadingImageView.setVisibility(View.GONE);

            }
        }

        @Override
        protected void onCancelled() {
            mHomeWorTask = null;

        }
    }

}
