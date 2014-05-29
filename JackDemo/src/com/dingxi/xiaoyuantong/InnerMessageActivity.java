package com.dingxi.xiaoyuantong;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dingxi.xiaoyuantong.dao.HomeWorkDao;
import com.dingxi.xiaoyuantong.model.ChildInfo;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo;
import com.dingxi.xiaoyuantong.model.HomeWorkInfo.HomeWorkEntry;
import com.dingxi.xiaoyuantong.model.ParentInfo;
import com.dingxi.xiaoyuantong.model.StudentInfo;
import com.dingxi.xiaoyuantong.model.TeacherInfo;
import com.dingxi.xiaoyuantong.model.UserInfo;
import com.dingxi.xiaoyuantong.model.UserInfo.UserType;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;
import com.dingxi.xiaoyuantong.util.Util;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

public class InnerMessageActivity extends Activity {

    public static final String TAG = "InnerMessageActivity";
    private static final String MESSAGE_TYPE_ALL = "0";
    private static final String MESSAGE_TYPE_SEND = "1";
    private static final String MESSAGE_TYPE_RESEVE = "2";

    private ImageButton mBackButton;
    private ImageButton mQueryMessageButton;
    private ImageButton mEditMessageButton;
    private Spinner mSpinner;

    private HomeWorkAdapter mHomeWorkAdapter;
    private ArrayList<HomeWorkInfo> mHomeWorkList;
    private ImageView loadingImageView;
    private AnimationDrawable loadingAnimation;
    private View emptyView;
    private static UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private ArrayList<ChildInfo> mStudentList;
    private ArrayAdapter<String> mSpinnerAdapter;
    private ArrayList<String> mSpinnerInfo;
    ListView mHomeWorkListView;
    PullToRefreshListView mPullToRefreshView;
    private boolean isFooterRefresh;
    int totalCount;
    int toatlPage;
    int curretCount;
    int curretPage;
    int pageCount = 5;
    public GetHomeWorTask mHomeWorTask;
  
    private String mEndTime;
    private String mEndDate;
    private String mStartTime;
    private String mStartDate;
   

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work);
        mPullToRefreshView = (PullToRefreshListView) findViewById(R.id.home_work_list);

        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.i(TAG, "onRefresh()");

                if (Util.IsNetworkAvailable(InnerMessageActivity.this)) {
                    String label = DateUtils.formatDateTime(getApplicationContext(),
                            System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                    | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                    // Update the LastUpdatedLabel
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                    if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {
                        TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
                       // String userId,String sOrR,String startTime,String endTime,String page,String rows
                        mHomeWorTask = new GetHomeWorTask(curretUserInfo.id,MESSAGE_TYPE_ALL,"", "",""+curretPage, ""+pageCount);
                    } else if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
                        ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                        mHomeWorTask = new GetHomeWorTask(curretUserInfo.id,MESSAGE_TYPE_ALL,"", "",""+curretPage, ""+pageCount);
                        
                    }
                    mHomeWorTask.execute((Void) null);
                    isFooterRefresh = true;
                } else {
                    mPullToRefreshView.onRefreshComplete();
                    Toast.makeText(InnerMessageActivity.this, R.string.not_network, Toast.LENGTH_SHORT)
                            .show();
                }

            }
        });

        // Add an end-of-list listener
        mPullToRefreshView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Log.i(TAG, "onLastItemVisible()");
                // if(totalCount==curretCount){
                // Toast.makeText(HomeWorkActivity.this, "End of List!",
                // Toast.LENGTH_SHORT).show();
                // mPullToRefreshView.onRefreshComplete();
                // }

            }
        });

        mPullToRefreshView.setMode(Mode.PULL_FROM_END);
        mHomeWorkListView = mPullToRefreshView.getRefreshableView();
        registerForContextMenu(mHomeWorkListView);
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
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.i(TAG, "arg2 " + arg2 + "arg3 " + arg3);
                if (Util.IsNetworkAvailable(InnerMessageActivity.this)) {
                    ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                   
                    mHomeWorkList.clear();
                    mHomeWorkAdapter.notifyDataSetChanged();
                    totalCount = 0;
                    toatlPage = 0;
                    curretCount = 0;
                    curretPage = 0;
                    loadingImageView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    loadingAnimation.start();

                    // ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                    mHomeWorTask = new GetHomeWorTask(curretUserInfo.id,MESSAGE_TYPE_ALL,"", "",""+curretPage, ""+pageCount);

                    mHomeWorTask.execute((Void) null);
                } else {
                    mPullToRefreshView.onRefreshComplete();
                    Toast.makeText(InnerMessageActivity.this, R.string.not_network, Toast.LENGTH_SHORT)
                            .show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Log.d(TAG, "mSpinner onNothingSelected()");
            }
        });

        if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
            ParentInfo parentInfo = (ParentInfo) curretUserInfo;

            if (parentInfo.childList != null) {
                mStudentList = parentInfo.childList;
            } else {
                mStudentList = new ArrayList<ChildInfo>();
            }

            if (parentInfo.nameList != null) {
                mSpinnerInfo = parentInfo.nameList;
            } else {
                mSpinnerInfo = new ArrayList<String>();
            }

            mSpinnerAdapter = new ArrayAdapter<String>(InnerMessageActivity.this,
                    R.layout.spinner_checked_text, mSpinnerInfo);
            mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mSpinner.setAdapter(mSpinnerAdapter);
            mSpinner.setSelection(parentInfo.curretSelectIndex);
            
            
        }

        mQueryMessageButton = (ImageButton) findViewById(R.id.query_button);
        mQueryMessageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InnerMessageActivity.this, SearchMessageActivity.class);
                intent.putExtra(SearchMessageActivity.SEARCH_TYPE, SearchMessageActivity.SEARCH_TYPE_HOME_WORK);
                startActivityForResult(intent, R.layout.activity_search_message);

            }
        });

        mEditMessageButton = (ImageButton) findViewById(R.id.edit_button);
        mEditMessageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(InnerMessageActivity.this, EditHomeWorkActivity.class);
                startActivity(intent);

            }
        });

        emptyView = findViewById(R.id.empty);
        mPullToRefreshView.setEmptyView(emptyView);
        mHomeWorkList = new ArrayList<HomeWorkInfo>();
        mHomeWorkAdapter = new HomeWorkAdapter(InnerMessageActivity.this, mHomeWorkList);
        loadingImageView = (ImageView) findViewById(R.id.loading_message_image);
        loadingImageView.setBackgroundResource(R.drawable.loading_howework_animation);
        loadingAnimation = (AnimationDrawable) loadingImageView.getBackground();

        mHomeWorkListView.setAdapter(mHomeWorkAdapter);
        mHomeWorkListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Log.i(TAG, "arg2 " + arg2 + " arg3 " + arg3);
                String id = mHomeWorkList.get(arg2 - 1).id;
                mHomeWorkList.get(arg2 - 1).isRead = 1;
                Log.i(TAG, "id " + id);
                Intent intent = new Intent(InnerMessageActivity.this, HomeWorkDetailActivity.class);
                intent.putExtra(HomeWorkEntry.COLUMN_NAME_ENTRY_ID, id);
                intent.putExtra(HomeWorkEntry.COLUMN_NAME_OPT_TIME, mHomeWorkList.get(arg2 - 1).optTime);
                intent.putExtra(HomeWorkEntry.COLUMN_NAME_CONTENT, mHomeWorkList.get(arg2 - 1).content);
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

        if (Util.IsNetworkAvailable(InnerMessageActivity.this)) {
            
            
            
            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

            SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date now = new Date();


            
            
            if (TextUtils.isEmpty(mStartDate)) {
                mStartDate = date.format(now);
            }

            if (TextUtils.isEmpty(mStartTime)) {
                mStartTime = " 00:00:00";//time.format(now);
            }
            
            if (TextUtils.isEmpty(mEndDate)) {
                mEndDate = date.format(now);
            }

            if (TextUtils.isEmpty(mEndTime)) {
                mEndTime = time.format(now);
            }

            
            mHomeWorTask = new GetHomeWorTask(curretUserInfo.id,MESSAGE_TYPE_ALL,"", "",""+curretPage, ""+pageCount);
            mHomeWorTask.execute((Void) null);
            /*
            if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {
                loadingImageView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                loadingAnimation.start();
                TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
                
                // String userId,String sOrR,String startTime,String endTime,String page,String rows
                mHomeWorTask = new GetHomeWorTask(curretUserInfo.id,MESSAGE_TYPE_ALL,mStartDate + mStartTime, mEndDate + mEndTime,""+curretPage, ""+pageCount,);
                mHomeWorTask.execute((Void) null);
            } else if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
                ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                mHomeWorTask = new GetHomeWorTask(curretPage, pageCount,
                curretUserInfo.fkSchoolId, "",
                "", parentInfo.defalutChild.id, "", true);
            }
            */
        } else {
            mPullToRefreshView.onRefreshComplete();
            Toast.makeText(InnerMessageActivity.this, R.string.not_network, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        Log.i(TAG, "onNewIntent");
        if (Util.IsNetworkAvailable(InnerMessageActivity.this)) {
            totalCount = 0;
            toatlPage = 0;
            curretCount = 0;
            curretPage = 0;
            mHomeWorkList.clear();
           
            loadingImageView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            loadingAnimation.start();
            if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {
                TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
                mHomeWorTask = new GetHomeWorTask(curretUserInfo.id,MESSAGE_TYPE_ALL,"", "",""+curretPage, ""+pageCount);
            } else if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
                ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                mHomeWorTask = new GetHomeWorTask(curretUserInfo.id,MESSAGE_TYPE_ALL,"", "",""+curretPage, ""+pageCount);
            }

            mHomeWorTask.execute((Void) null);
        } else {
            mPullToRefreshView.onRefreshComplete();
            Toast.makeText(InnerMessageActivity.this, R.string.not_network, Toast.LENGTH_SHORT).show();
        }

    }
    
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        
        if(mHomeWorkList.size()>0){
            mHomeWorkAdapter.notifyDataSetChanged();
        }
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
        public Object getItem(int position) {// HomeWorkInfo
            // TODO Auto-generated method stub

            // homeWorkList.get(position)
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            String id = homeWorkList.get(position).id;
            // Long.parseLong(id)
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HomeWorkHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.homework_item, null);
                viewHolder = new HomeWorkHolder();
                viewHolder.headerText = (TextView) convertView.findViewById(R.id.message_header);
                viewHolder.isRead = (TextView) convertView.findViewById(R.id.is_read);
                viewHolder.bodyText = (TextView) convertView.findViewById(R.id.message_body);
                viewHolder.sendTime = (TextView) convertView.findViewById(R.id.send_time);
                viewHolder.sendName = (TextView) convertView.findViewById(R.id.send_name);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (HomeWorkHolder) convertView.getTag();
            }

            // viewHolder.headerText.setText(homeWorkList.get(position).id);
            viewHolder.headerText.setText(R.string.home_work);
            viewHolder.bodyText.setText(homeWorkList.get(position).content);
            viewHolder.sendTime.setText(homeWorkList.get(position).optTime);
            viewHolder.sendName.setText(homeWorkList.get(position).sendName);
            if (homeWorkList.get(position).isRead == 0) {
                viewHolder.isRead.setText(R.string.unread);
            } else {
                viewHolder.isRead.setText(R.string.readed);
            }

            return convertView;
        }

        class HomeWorkHolder {

            TextView headerText;
            TextView isRead;
            TextView bodyText;
            TextView sendTime;
            TextView sendName;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == R.layout.activity_search_message) {

            if (Util.IsNetworkAvailable(InnerMessageActivity.this)) {
                totalCount = 0;
                toatlPage = 0;
                curretCount = 0;
                curretPage = 0;
                mHomeWorkList.clear();
                Bundle searchBundle = data.getExtras();
                searchBundle.getString("mGradeId");
                String classId2 = searchBundle.getString("mClassId");
                String studentId = searchBundle.getString("mStudentId");
                String date = searchBundle.getString("mData");

                mHomeWorkAdapter.notifyDataSetChanged();

                loadingImageView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                loadingAnimation.start();
                if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {
                    TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;

                    mHomeWorTask = new GetHomeWorTask(curretUserInfo.id,MESSAGE_TYPE_ALL,"", "",""+curretPage, ""+pageCount);
                } else {
                    ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                    mHomeWorTask = new GetHomeWorTask(curretUserInfo.id,MESSAGE_TYPE_ALL,"", "",""+curretPage, ""+pageCount);
                }
                mHomeWorTask.execute((Void) null);
            } else {
                mPullToRefreshView.onRefreshComplete();
                Toast.makeText(InnerMessageActivity.this, R.string.not_network, Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    public class GetHomeWorTask extends AsyncTask<Void, Void, ResponseMessage> {

        String userId;
        String sOrR;
        String startTime;
        String endTime;
        String page;
        String rows;

        public GetHomeWorTask(String userId,String sOrR,String startTime,String endTime,String page,String rows) {
            this.userId = userId;
            this.sOrR = sOrR;
            this.startTime = startTime;
            this.endTime = endTime;
            this.page = page;
            this.rows = rows;

        }

        @Override
        protected ResponseMessage doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.i(TAG, "totalCount " + totalCount);
            Log.i(TAG, "toatlPage " + toatlPage);
            Log.i(TAG, "curretCount " + curretCount);
            Log.i(TAG, "curretPage " + curretPage);

            ++curretPage;

            ResponseMessage responseMessage = new ResponseMessage();

            try {
                
                responseMessage.body = RestClient.getInnerMessages(curretUserInfo.id, sOrR, startTime, endTime, ""+curretPage, ""+pageCount);
                responseMessage.praseBody();

            } catch (ConnectTimeoutException stex) {
                responseMessage.message = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                responseMessage.message = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                responseMessage.message = getString(R.string.connection_server_error);
            } catch (JSONException e) {
                responseMessage.message = getString(R.string.connection_server_error);
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
            mHomeWorTask = null;
           
            // Log.i(TAG, "responseMessage.body " + responseMessage.body);
            if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                totalCount = responseMessage.total;

                if (totalCount > 0) {
                    toatlPage = totalCount % pageCount > 1 ? (totalCount / pageCount + 1)
                            : totalCount / pageCount;

                    ArrayList<HomeWorkInfo> homeWorkInfoList = null;
                    try {
                        homeWorkInfoList = JSONParser.praseHomeWorks(responseMessage.body);

                        if (homeWorkInfoList != null && homeWorkInfoList.size() > 0) {
                            
                            HomeWorkDao homeWorkDao = new HomeWorkDao(mXiaoYunTongApplication);
                            for (HomeWorkInfo homeWorkInfo : homeWorkInfoList) {
                                HomeWorkInfo info1  = homeWorkDao.queryHomeWorkByID(homeWorkInfo.id);
                                Log.i(TAG, "info1 " + info1);
                                if(info1 != null){
                                    homeWorkInfo.isRead = 1;
                                }
                                // Log.i(TAG, "HomeWork insertResult " + insertResult);
                            }
                            homeWorkDao.colseDb();
                            homeWorkDao = null;
                            mHomeWorkList.addAll(homeWorkInfoList);
                            // if (isCustomSearch) {
                            // mHomeWorkList.clear();
                            // mHomeWorkList.addAll(homeWorkInfoList);
                            // } else {
                            // ArrayList<HomeWorkInfo> homeWork = homeWorkDao
                            // .queryReadOrNotReadCountHomeWork(0);
                            // Log.i(TAG, "homeWork.size() " + homeWork.size());
                            // mHomeWorkList.clear();
                            // mHomeWorkList.addAll(homeWork);
                            //
                            // }
                        } else {
                            --curretPage;
                            if (totalCount == curretCount) {
                                Toast.makeText(InnerMessageActivity.this, R.string.no_more_data,
                                        Toast.LENGTH_SHORT).show();
                            }

                        }

                    } catch (JSONException e) {
                        --curretPage;
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    curretCount = mHomeWorkList.size();

                    

                    mHomeWorkAdapter.notifyDataSetChanged();
                    mHomeWorkListView.setSelection(curretPage * pageCount - 4);
                } else {
                    toatlPage = 0;
                    --curretPage;
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(InnerMessageActivity.this, R.string.no_data, Toast.LENGTH_LONG)
                            .show();
                }

            } else {
                --curretPage;
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(InnerMessageActivity.this, responseMessage.message, Toast.LENGTH_LONG)
                        .show();
            }
            Log.i(TAG, "isFooterRefresh " + isFooterRefresh);
            Log.i(TAG, "totalCount2 " + totalCount);
            Log.i(TAG, "toatlPage2 " + toatlPage);
            Log.i(TAG, "curretCount2 " + curretCount);
            Log.i(TAG, "curretPage2 " + curretPage);
            if (isFooterRefresh) {
                mPullToRefreshView.onRefreshComplete();

                isFooterRefresh = false;
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
