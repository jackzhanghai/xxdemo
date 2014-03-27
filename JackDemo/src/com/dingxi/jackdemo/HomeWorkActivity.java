package com.dingxi.jackdemo;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
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
    private ListView mMessageListView;
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
        
        mMessageListView = (ListView) findViewById(R.id.home_work_list);
        mMessageListView.setEmptyView(findViewById(R.id.empty_date));
        mHomeWorkList = new ArrayList<HomeWorkInfo>();
        mHomeWorkAdapter = new HomeWorkAdapter(HomeWorkActivity.this, mHomeWorkList);
        loadingImageView = (ImageView) findViewById(R.id.loading_message_image);
        loadingImageView.setBackgroundResource(R.drawable.loading_howework_animation);
        loadingAnimation = (AnimationDrawable) loadingImageView.getBackground();
        mMessageListView.setAdapter(mHomeWorkAdapter);
        

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
        mHomeWorTask = new GetHomeWorTask();
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
        public HomeWorkInfo getItem(int position) {
            // TODO Auto-generated method stub
            return homeWorkList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            String id = homeWorkList.get(position).getId();
            return Long.parseLong(id);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

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

            viewHolder.headerText.setText(homeWorkList.get(position).getId());
            viewHolder.bodyText.setText(homeWorkList.get(position).getContent());

            return convertView;
        }

        class HomeWorkHolder {

            TextView headerText;
            TextView bodyText;

        }
    }

    public class GetHomeWorTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String schoolsInfo = null;
//            try {
//                schoolsInfo = RestClient.getHomeWorks(schoolsInfo, schoolsInfo, schoolsInfo, schoolsInfo, 0, 0, schoolsInfo);
//            } catch (ConnectTimeoutException stex) {
//                schoolsInfo = getString(R.string.request_time_out);
//            } catch (SocketTimeoutException stex) {
//                schoolsInfo = getString(R.string.server_time_out);
//            } catch (HttpHostConnectException hhce) {
//                schoolsInfo = getString(R.string.connection_server_error);
//            } catch (XmlPullParserException e) {
//                schoolsInfo = getString(R.string.connection_error);
//                e.printStackTrace();
//            } catch (IOException e) {
//                schoolsInfo = getString(R.string.connection_error);
//                e.printStackTrace();
//            }

            // TODO: register the new account here.
            return schoolsInfo;
        }

        @Override
        protected void onPostExecute(String schoolsInfo) {
            mHomeWorTask = null;
            Log.d(TAG, "result " + schoolsInfo);
            if (!TextUtils.isEmpty(schoolsInfo)) {

                try {
                    if (JSONParser.getIntByTag(schoolsInfo, RestClient.RESULT_TAG_CODE) == RestClient.RESULT_TAG_SUCCESS) {

                        loadingAnimation.stop();
                        loadingImageView.setVisibility(View.GONE);

                        showDialog(R.id.choose_school);
                    } else {
                        loadingAnimation.stop();
                        loadingImageView.setVisibility(View.GONE);
                        String errorMessage = JSONParser.getStringByTag(schoolsInfo,
                                RestClient.RESULT_TAG_MESSAGE);
                        if (!TextUtils.isEmpty(errorMessage)) {
                            Toast.makeText(HomeWorkActivity.this, errorMessage, Toast.LENGTH_LONG)
                                    .show();
                        } else {
                            Toast.makeText(HomeWorkActivity.this, schoolsInfo, Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                } catch (JSONException e) {
                    loadingAnimation.stop();
                    loadingImageView.setVisibility(View.GONE);
                    Toast.makeText(HomeWorkActivity.this, schoolsInfo, Toast.LENGTH_LONG).show();
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
