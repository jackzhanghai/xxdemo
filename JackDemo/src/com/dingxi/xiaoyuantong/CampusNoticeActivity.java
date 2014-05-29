package com.dingxi.xiaoyuantong;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

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

import com.dingxi.xiaoyuantong.HomeWorkActivity.GetHomeWorTask;
import com.dingxi.xiaoyuantong.dao.CampusNoticeDao;
import com.dingxi.xiaoyuantong.model.CampusNotice;
import com.dingxi.xiaoyuantong.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.xiaoyuantong.model.ChildInfo;
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

public class CampusNoticeActivity extends Activity {

    public static final String TAG = "CampusNoticeActivity";
    private View emptyView;
    private ImageButton mBackButton;
    private ImageButton mQueryMessageButton;
    private ImageButton mEditMessageButton;
    private Spinner mSpinner;
    private CampusNoticeAdapter mCampusNoticeAdapter;
    private ArrayList<CampusNotice> mCampusNoticeList;
    private ImageView loadingImageView;
    private AnimationDrawable loadingAnimation;
    private static UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private ArrayList<ChildInfo> mStudentList;
    private ArrayAdapter<String> mSpinnerAdapter;
    private ArrayList<String> mSpinnerInfo;
    ListView mCampusNoticeListView;
    PullToRefreshListView mPullToRefreshListView;
    int totalCount;
    int toatlPage;
    int curretCount;
    int curretPage;
    int pageCount = 5;
    private boolean isFooterRefresh;

    public GetmCampusNoticeListTask mGetmCampusNoticeTask;

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
        mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                Log.d(TAG, "mSpinner onItemSelected()");
                // TODO Auto-generated method stub
                if (Util.IsNetworkAvailable(CampusNoticeActivity.this)) {
                    ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                    parentInfo.defalutChild = mStudentList.get(arg2);

                    mCampusNoticeList.clear();
                    mCampusNoticeAdapter.notifyDataSetChanged();
                    totalCount = 0;
                    toatlPage  = 0;
                    curretCount = 0;
                    curretPage = 0;
                    loadingImageView.setVisibility(View.VISIBLE);
                    emptyView.setVisibility(View.GONE);
                    loadingAnimation.start();

                    // ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                    mGetmCampusNoticeTask = new GetmCampusNoticeListTask(curretPage, pageCount,
                            parentInfo.defalutChild.fkSchoolId, "", "", parentInfo.defalutChild.id, "", true);

                    mGetmCampusNoticeTask.execute((Void) null);  
                } else {
                    Toast.makeText(CampusNoticeActivity.this, R.string.not_network, Toast.LENGTH_SHORT).show();
                }
               
                
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                Log.d(TAG, "mSpinner onNothingSelected()");
            }
        });

        mQueryMessageButton = (ImageButton) findViewById(R.id.query_button);
        mQueryMessageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CampusNoticeActivity.this, SearchMessageActivity.class);
                intent.putExtra(SearchMessageActivity.SEARCH_TYPE, SearchMessageActivity.SEARCH_TYPE_CAMPUS_NOTE);
                startActivityForResult(intent, R.layout.activity_search_message);

            }
        });

        mEditMessageButton = (ImageButton) findViewById(R.id.edit_button);
        mEditMessageButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(CampusNoticeActivity.this,
                        EditCampusNoticeActivity.class);
                startActivity(intent);

            }
        });

        emptyView = findViewById(R.id.empty);
        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.campus_notie_list);
        // mHomeWorkListView = (ListView) findViewById(R.id.campus_notie_list);
        mPullToRefreshListView.setEmptyView(emptyView);

        mPullToRefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.i(TAG, "onRefresh()");
                if(Util.IsNetworkAvailable(CampusNoticeActivity.this)){
                    String label = DateUtils.formatDateTime(getApplicationContext(),
                            System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                    | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);

                    // Update the LastUpdatedLabel
                    refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);

                        if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {

                            TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
                            mGetmCampusNoticeTask = new GetmCampusNoticeListTask(curretPage, pageCount,
                                    teacherInfo.defalutSchoolId, teacherInfo.defalutClassId, "", "", "",
                                    true);
                        } else if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
                            ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                            mGetmCampusNoticeTask = new GetmCampusNoticeListTask(curretPage, pageCount,
                                    parentInfo.defalutChild.fkSchoolId, "", "", parentInfo.defalutChild.id, "",
                                    true);
                        }
                        isFooterRefresh = true;
                        mGetmCampusNoticeTask.execute((Void) null);
                    
                    
                } else {
                    mPullToRefreshListView.onRefreshComplete();
                    Toast.makeText(CampusNoticeActivity.this, R.string.not_network, Toast.LENGTH_SHORT).show();
                }
                
            }
        });

        // Add an end-of-list listener
        mPullToRefreshListView.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

            @Override
            public void onLastItemVisible() {
                Log.i(TAG, "onLastItemVisible()");
                

            }
        });

        mPullToRefreshListView.setMode(Mode.PULL_FROM_END);
        mCampusNoticeListView = mPullToRefreshListView.getRefreshableView();

        mCampusNoticeList = new ArrayList<CampusNotice>();
        mCampusNoticeAdapter = new CampusNoticeAdapter(CampusNoticeActivity.this, mCampusNoticeList);
        loadingImageView = (ImageView) findViewById(R.id.loading_message_image);
        loadingImageView.setBackgroundResource(R.drawable.loading_howework_animation);
        loadingAnimation = (AnimationDrawable) loadingImageView.getBackground();
        mCampusNoticeListView.setAdapter(mCampusNoticeAdapter);
        mCampusNoticeListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Log.i(TAG, "arg2 " + arg2 + " arg3 " + arg3);
                String id = mCampusNoticeList.get(arg2-1).id;
                mCampusNoticeList.get(arg2-1).isRead = 1;
                Log.i(TAG, "id " + id);
                Intent intent = new Intent(CampusNoticeActivity.this,
                        CampusNoticeDetailActivity.class);
                intent.putExtra(CampusNoticeEntry.COLUMN_NAME_ENTRY_ID, id);
                intent.putExtra(CampusNoticeEntry.COLUMN_NAME_CONTENT, mCampusNoticeList.get(arg2-1).content);
                intent.putExtra(CampusNoticeEntry.COLUMN_NAME_OPT_TIME, mCampusNoticeList.get(arg2-1).optTime);
                startActivity(intent);
            }

        });

        if (curretUserInfo.roleType.equals(UserType.ROLE_TEACHER)) {
            mQueryMessageButton.setVisibility(View.VISIBLE);
            mEditMessageButton.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
        } else {
            mSpinner.setVisibility(View.VISIBLE);
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

            mSpinnerAdapter = new ArrayAdapter<String>(CampusNoticeActivity.this,
                    R.layout.spinner_checked_text, mSpinnerInfo);
            mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mSpinner.setAdapter(mSpinnerAdapter);
            mQueryMessageButton.setVisibility(View.GONE);
            mEditMessageButton.setVisibility(View.GONE);
           
            mSpinner.setSelection(parentInfo.curretSelectIndex);
        }

        
        if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {
            loadingImageView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            loadingAnimation.start();
            TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
            mGetmCampusNoticeTask = new GetmCampusNoticeListTask(curretPage, pageCount,
                    teacherInfo.defalutSchoolId, teacherInfo.defalutClassId, "", "", "", true);
            mGetmCampusNoticeTask.execute((Void) null);
        } else if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
//            ParentInfo parentInfo = (ParentInfo) curretUserInfo;
//            mGetmCampusNoticeTask = new GetmCampusNoticeListTask(curretPage, pageCount,
//                    parentInfo.defalutChild.fkSchoolId, "", "", parentInfo.defalutChild.id, "", true);
        }

       
    }
    
    
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if(mCampusNoticeList.size()>0){
            mCampusNoticeAdapter.notifyDataSetChanged();
            
        }
    }
    
    
    @Override
    protected void onNewIntent(Intent intent) {
        // TODO Auto-generated method stub
        super.onNewIntent(intent);
        
       
        totalCount = 0;
        toatlPage = 0;
        curretCount = 0;
        curretPage = 0;
        mCampusNoticeList.clear();
        loadingImageView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        loadingAnimation.start();
        if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {

            TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
            mGetmCampusNoticeTask = new GetmCampusNoticeListTask(curretPage, pageCount,
                    teacherInfo.defalutSchoolId, teacherInfo.defalutClassId, "", "", "", true);
        } else if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
            ParentInfo parentInfo = (ParentInfo) curretUserInfo;
            mGetmCampusNoticeTask = new GetmCampusNoticeListTask(curretPage, pageCount,
                    parentInfo.defalutChild.fkSchoolId, "", "", parentInfo.defalutChild.id, "", true);
        }

        mGetmCampusNoticeTask.execute((Void) null);
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
        public Object getItem(int position) {// HomeWorkInfo
            // TODO Auto-generated method stub

            // homeWorkList.get(position)
            return null;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            String id = campusNoticeList.get(position).id;
            // Long.parseLong(id)
            return position;
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
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (HomeWorkHolder) convertView.getTag();
            }

            // viewHolder.headerText.setText(campusNoticeList.get(position).id);
            viewHolder.headerText.setText(R.string.system_note);

            viewHolder.bodyText.setText(campusNoticeList.get(position).content);
            if (campusNoticeList.get(position).isRead == 0) {
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

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == R.layout.activity_search_message) {
            
            totalCount = 0;
            toatlPage = 0;
            curretCount = 0;
            curretPage = 0;
            Bundle searchBundle = data.getExtras();
            searchBundle.getString("mGradeId");
            String classId2 = searchBundle.getString("mClassId");
            String studentId = searchBundle.getString("mStudentId");
            String date = searchBundle.getString("mData");
            mCampusNoticeList.clear();
            mCampusNoticeAdapter.notifyDataSetChanged();

            loadingImageView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            loadingAnimation.start();
            if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {

                TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
                mGetmCampusNoticeTask = new GetmCampusNoticeListTask(curretPage, pageCount,
                        teacherInfo.defalutSchoolId, teacherInfo.defalutClassId, classId2, studentId,
                        date, true);
            } else {
                ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                mGetmCampusNoticeTask = new GetmCampusNoticeListTask(curretPage, pageCount,
                        parentInfo.defalutChild.fkSchoolId, "", "", parentInfo.defalutChild.id, date, true);
            }
            mGetmCampusNoticeTask.execute((Void) null);

        }
    }

    public class GetmCampusNoticeListTask extends AsyncTask<Void, Void, ResponseMessage> {

        int page, row;
        String fkSchoolId;
        String fkClassId;
        String fkClassId2;
        String fkStudentId;
        String queryTime;
        boolean isCustomSearch;

        public GetmCampusNoticeListTask(int page, int rows, String fkSchoolId, String fkClassId,
                String fkClassId2, String fkStudentId, String queryTime, boolean isCustomSearch) {
            this.page = page;
            this.row = rows;
            this.queryTime = queryTime;
            this.fkSchoolId = fkSchoolId;
            this.fkClassId = fkClassId;
            this.fkClassId2 = fkClassId2;
            this.fkStudentId = fkStudentId;
            this.isCustomSearch = isCustomSearch;
        }

        @Override
        protected ResponseMessage doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.i(TAG, "totalCount " + totalCount);
            Log.i(TAG, "toatlPage " + toatlPage);
            Log.i(TAG, "curretCount " + curretCount);
            Log.i(TAG, "curretPage " + curretPage);
            Log.d(TAG, "mCampusNoticeList.size() " + mCampusNoticeList.size());
            
            ResponseMessage responseMessage = new ResponseMessage();
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
                ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                info.append("\"fkStudentId\":");
                info.append("\"" + parentInfo.defalutChild.id + "\"");
                info.append(",");
            }

            info.append("\"queryTime\":");
            info.append("\"" + queryTime + "\"");
            info.append("}");
            Log.d(TAG, "info.toString() " + info.toString());
            ++curretPage;

            try {

                responseMessage.body = RestClient.getMessageInfos(
                        mXiaoYunTongApplication.userInfo.id,
                        mXiaoYunTongApplication.userInfo.ticket,
                        curretUserInfo.roleType.toString(), info.toString(), curretPage, pageCount);
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
            mGetmCampusNoticeTask = null;

            Log.i(TAG, "responseMessage.body  " + responseMessage.body);
            CampusNoticeDao campusNoticeDao = new CampusNoticeDao(CampusNoticeActivity.this);
            if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                totalCount = responseMessage.total;
                if(totalCount > 0){
                    toatlPage = totalCount % pageCount > 1 ? (totalCount / pageCount + 1)
                            : totalCount / pageCount;
                    
//                    if(totalCount % pageCount >= 1){
//                        toatlPage = (totalCount / pageCount + 1);
//                    } else if(totalCount % pageCount > 0 && totalCount % pageCount < 1) {
//                        toatlPage = 1;
//                    } else {
//                        toatlPage = (totalCount / pageCount);
//                    }
                    
                    
                    try {

                        ArrayList<CampusNotice> campusNoticeList = JSONParser
                                .praseCampusNotice(responseMessage.body);

                        if (campusNoticeList != null && campusNoticeList.size() > 0) {

                            for (CampusNotice campusNotice : campusNoticeList) {
                                CampusNotice campus = campusNoticeDao.queryCampusNoticeByID(campusNotice.id);
                                Log.i(TAG, "campus " + campus);
                                if(campus != null){
                                    campusNotice.isRead = 1;
                                }

                            }

                            mCampusNoticeList.addAll(campusNoticeList);

                            // if(isCustomSearch){
                            // mCampusNoticeList.clear();
                            // mCampusNoticeList.addAll(campusNoticeList);
                            // mCampusNoticeAdapter.notifyDataSetChanged();
                            // } else {
                            // ArrayList<CampusNotice> campusNotice = campusNoticeDao
                            // .queryReadOrNotReadCampusNotice(0);
                            // Log.i(TAG, "campusNotice.size() " + campusNotice.size());
                            // mCampusNoticeList.clear();
                            // mCampusNoticeList.addAll(campusNotice);
                            // mCampusNoticeAdapter.notifyDataSetChanged();
                            // }

                        } else {
                            --curretPage;
                            if (totalCount == curretCount) {
                                Toast.makeText(CampusNoticeActivity.this, R.string.no_more_data,
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        // else {
                        // ArrayList<CampusNotice> campusNotice = campusNoticeDao
                        // .queryReadOrNotReadCampusNotice(0);
                        // Log.i(TAG, "campusNotice.size() " + campusNotice.size());
                        // mCampusNoticeList.clear();
                        // mCampusNoticeList.addAll(campusNotice);
                        // mCampusNoticeAdapter.notifyDataSetChanged();
                        // };

                        curretCount = mCampusNoticeList.size();                       
                        mCampusNoticeListView.setSelection(curretPage * pageCount - 4);

                    } catch (JSONException e) {
                        --curretPage;
                        e.printStackTrace();
                    }
                    
                    campusNoticeDao.colseDb();
                    campusNoticeDao = null;
                   
                    Log.i(TAG, "totalCount2 " + totalCount);
                    Log.i(TAG, "toatlPage2 " + toatlPage);
                    Log.i(TAG, "curretCount2 " + curretCount);
                    Log.i(TAG, "curretPage2 " + curretPage);
                    Log.d(TAG, "mCampusNoticeList.size() " + mCampusNoticeList.size());
                }else {
                    --curretPage;
                    emptyView.setVisibility(View.VISIBLE);
                    Toast.makeText(CampusNoticeActivity.this, R.string.no_data, Toast.LENGTH_LONG)
                            .show();
                }
               

            } else {
                --curretPage;
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(CampusNoticeActivity.this, responseMessage.message,
                        Toast.LENGTH_LONG).show();
            }

            if (isFooterRefresh) {
                mPullToRefreshListView.onRefreshComplete();
                isFooterRefresh = false;
            } else {
                loadingAnimation.stop();
                loadingImageView.setVisibility(View.GONE);
            }
            mCampusNoticeAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onCancelled() {
            mGetmCampusNoticeTask = null;

        }
    }

}
