package com.dingxi.xiaoyuantong;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import com.dingxi.xiaoyuantong.HomeWorkActivity.GetHomeWorTask;
import com.dingxi.xiaoyuantong.model.AttendanceInfo;
import com.dingxi.xiaoyuantong.model.ChildInfo;
import com.dingxi.xiaoyuantong.model.ParentInfo;
import com.dingxi.xiaoyuantong.model.StudentInfo;
import com.dingxi.xiaoyuantong.model.TeacherInfo;
import com.dingxi.xiaoyuantong.model.UserInfo;
import com.dingxi.xiaoyuantong.model.AttendanceInfo.AttendanceInfoEntry;
import com.dingxi.xiaoyuantong.model.UserInfo.UserType;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;


/**
 * 考勤信息
 * @author think
 *
 */
public class AttendanceInfoActivity extends Activity {

	
	public static final String TAG = "AttendanceInfoActivity";
    private View emptyView;
    private ImageButton mBackButton;
    private ImageButton mQueryMessageButton;
    private Spinner mSpinner;
    private ListView mAttendanceInfoListView;
    private AttendanceAdapter mAttendanceAdapter;
    private ArrayList<AttendanceInfo> mAttendanceInfoList;
    private ImageView loadingImageView;
    private AnimationDrawable loadingAnimation;
    private static UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private ArrayList<ChildInfo> mStudentList;
    private ArrayAdapter<String> mSpinnerAdapter;
    private ArrayList<String> mSpinnerInfo;

    PullToRefreshListView mPullToRefreshView;
    private boolean isFooterRefresh;
    int totalCount;
    int toatlPage;
    int curretCount;
    int curretPage;
    int pageCount = 5;
    public GetAttendanceInfoTask mGetAttendanceInfoTask;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendance_info);

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
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                Log.d(TAG, "mSpinner onItemSelected()");
                ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                parentInfo.defalutChild = mStudentList.get(arg2);
                totalCount = 0;
                toatlPage  = 0;
                curretCount = 0;
                curretPage = 0;
                mAttendanceInfoList.clear();
                mAttendanceAdapter.notifyDataSetChanged();

                loadingImageView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                loadingAnimation.start();
                mGetAttendanceInfoTask = new GetAttendanceInfoTask(curretPage, pageCount,parentInfo.defalutChild.fkSchoolId, "", "", parentInfo.defalutChild.id, "");


                mGetAttendanceInfoTask.execute((Void) null);
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
                Intent intent = new Intent(AttendanceInfoActivity.this,SearchMessageActivity.class);
                intent.putExtra(SearchMessageActivity.SEARCH_TYPE, SearchMessageActivity.SEARCH_TYPE_ATTENDACE);
                startActivityForResult(intent, R.layout.activity_search_message);               
            }
        });
        
        mPullToRefreshView = (PullToRefreshListView) findViewById(R.id.attendanceinfo_list);
        emptyView = findViewById(R.id.empty);
        mAttendanceInfoListView = mPullToRefreshView.getRefreshableView();
        mAttendanceInfoListView.setEmptyView(emptyView);
       

        mPullToRefreshView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.i(TAG, "onRefresh()");
                String label = DateUtils.formatDateTime(getApplicationContext(),
                        System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                                | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL);
                // Update the LastUpdatedLabel
                refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
                if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {
                    TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
                    mGetAttendanceInfoTask = new GetAttendanceInfoTask(curretPage, pageCount,
                            teacherInfo.defalutSchoolId, teacherInfo.defalutClassId, "", "", "");
                } else if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
                    ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                    mGetAttendanceInfoTask = new GetAttendanceInfoTask(curretPage, pageCount,
                            parentInfo.defalutChild.fkSchoolId, "", "", parentInfo.defalutChild.id, "");
                }
                mGetAttendanceInfoTask.execute((Void) null);
                isFooterRefresh = true;

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
        registerForContextMenu(mAttendanceInfoListView);
        mAttendanceInfoList = new ArrayList<AttendanceInfo>();
        mAttendanceAdapter = new AttendanceAdapter(AttendanceInfoActivity.this, mAttendanceInfoList);
        loadingImageView = (ImageView) findViewById(R.id.loading_message_image);
        loadingImageView.setBackgroundResource(R.drawable.loading_howework_animation);
        loadingAnimation = (AnimationDrawable) loadingImageView.getBackground();
        mAttendanceInfoListView.setAdapter(mAttendanceAdapter);
        mAttendanceInfoListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			    Log.i(TAG, "arg2 " +arg2 + " arg3 " + arg3);
			    int index = arg2-1;
			    if (curretUserInfo.roleType.equals(UserType.ROLE_TEACHER)) {
					Intent intent = new Intent(AttendanceInfoActivity.this,UpdateAttendanceInfoActivity.class);
					intent.putExtra(AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID,  mAttendanceInfoList.get(index).id);
					intent.putExtra(AttendanceInfoEntry.COLUMN_NAME_DIREC,  mAttendanceInfoList.get(index).direc);
					Log.i(TAG, "mAttendanceInfoList.get(arg2).direc " + mAttendanceInfoList.get(index).direc);
					intent.putExtra(AttendanceInfoEntry.COLUMN_NAME_STU_NAME,  mAttendanceInfoList.get(index).stuName);
					intent.putExtra(AttendanceInfoEntry.COLUMN_NAME_ATT_TIME,  mAttendanceInfoList.get(index).attTime);
					startActivity(intent);
		        }

			}
        	
		});
        

        if (curretUserInfo.roleType.equals(UserType.ROLE_TEACHER)) {
            mQueryMessageButton.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
        } else {
            mQueryMessageButton.setVisibility(View.GONE);
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
            
            mSpinnerAdapter = new ArrayAdapter<String>(AttendanceInfoActivity.this,
                    R.layout.spinner_checked_text, mSpinnerInfo);
            mSpinnerAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mSpinner.setAdapter(mSpinnerAdapter);
            mSpinner.setSelection(parentInfo.curretSelectIndex);
            
        }

        
        if(curretUserInfo.roleType == UserType.ROLE_TEACHER){
            loadingImageView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            loadingAnimation.start();
        	TeacherInfo teacherInfo  = (TeacherInfo) curretUserInfo;       	
        	mGetAttendanceInfoTask = new GetAttendanceInfoTask(curretPage, pageCount, teacherInfo.defalutSchoolId, teacherInfo.defalutClassId, "", "", "");
        	mGetAttendanceInfoTask.execute((Void) null);
        } else  if(curretUserInfo.roleType == UserType.ROLE_PARENT){
//        	ParentInfo  parentInfo = (ParentInfo) curretUserInfo;
//        	mGetAttendanceInfoTask = new GetAttendanceInfoTask(curretPage, pageCount, parentInfo.defalutChild.fkSchoolId, "", "", parentInfo.defalutChild.id, "");
        }
       
        
	}



    class AttendanceAdapter extends BaseAdapter {

        private Context context;
        private ArrayList<AttendanceInfo> attendanceInfos;
        private LayoutInflater inflater;

        private AttendanceAdapter(Context context, ArrayList<AttendanceInfo> attendanceInfos) {

            this.context = context;
            inflater = LayoutInflater.from(context);
            this.attendanceInfos = attendanceInfos;
            // TODO Auto-generated method stub
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return attendanceInfos.size();
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
            Integer id = attendanceInfos.get(position).id;
            //Long.parseLong(id)
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Log.i(TAG, "getView()");
            AttendanceHolder viewHolder;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.attendance_item, null);
                viewHolder = new AttendanceHolder();
                viewHolder.studentName = (TextView) convertView.findViewById(R.id.student_name);
                viewHolder.inOutText = (TextView) convertView.findViewById(R.id.in_out_school);
                viewHolder.dateText = (TextView) convertView.findViewById(R.id.date_time);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (AttendanceHolder) convertView.getTag();
            }

            Log.i(TAG, "homeWorkList.get(position).getId() " + attendanceInfos.get(position).id);
            viewHolder.studentName.setText(attendanceInfos.get(position).stuName);
            
            Log.i(TAG, "homeWorkList.get(position).getContent() " + attendanceInfos.get(position).direc);
            
            if(attendanceInfos.get(position).direc == 1){
            	viewHolder.inOutText.setText(R.string.into_school);
            } else if (attendanceInfos.get(position).direc == 2){
            	viewHolder.inOutText.setText(R.string.out_school);
            } else {
            	viewHolder.inOutText.setText(R.string.unkown_time);
            }
            
            viewHolder.dateText.setText(attendanceInfos.get(position).attTime);
            return convertView;
        }

        class AttendanceHolder {

            TextView studentName;
            TextView dateText;
            TextView inOutText;

        }
    }

    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        
        if(resultCode == RESULT_OK && requestCode == R.layout.activity_search_message){
            Bundle  searchBundle =   data.getExtras();           
            
            totalCount = 0;
            toatlPage = 0;
            curretCount = 0;
            curretPage = 0;
            
            searchBundle.getString("mGradeId");
           String classId2 = searchBundle.getString("mClassId");
          String studentId =  searchBundle.getString("mStudentId");
          String date =  searchBundle.getString("mData");
            mAttendanceInfoList.clear();
            mAttendanceAdapter.notifyDataSetChanged();
            
            loadingImageView.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
            loadingAnimation.start();
            if(curretUserInfo.roleType == UserType.ROLE_TEACHER){
            	TeacherInfo teacherInfo  = (TeacherInfo) curretUserInfo;
            	
            	mGetAttendanceInfoTask = new GetAttendanceInfoTask(curretPage, pageCount, teacherInfo.defalutSchoolId, teacherInfo.defalutClassId, classId2, studentId, date);
            } else {
            	ParentInfo  parentInfo = (ParentInfo) curretUserInfo;
            	mGetAttendanceInfoTask = new GetAttendanceInfoTask(curretPage, pageCount, parentInfo.defalutChild.fkSchoolId, "", "", parentInfo.defalutChild.id, date);
            }
            mGetAttendanceInfoTask.execute((Void) null);
            
        }
    }
    
    
    
    public class GetAttendanceInfoTask extends AsyncTask<Void, Void, ResponseMessage> {
        
        int page,row;
        String fkSchoolId ;
        String fkClassId;
        String fkClassId2;
        String fkStudentId;
        String queryTime;
        
        public GetAttendanceInfoTask(int page, int rows, String fkSchoolId, String fkClassId,String fkClassId2, String fkStudentId, String queryTime) {
           this.page = page;
           this.row = rows;
           this.queryTime =  queryTime;
           this.fkSchoolId =  fkSchoolId;
           this.fkClassId =  fkClassId;
           this.fkClassId2 =  fkClassId2;
           this.fkStudentId =  fkStudentId;
        }
        
        
        @Override
        protected ResponseMessage doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            Log.i(TAG, "totalCount1 " + totalCount);
            Log.i(TAG, "toatlPage1 " + toatlPage);
            Log.i(TAG, "curretCount1 " + curretCount);
            Log.i(TAG, "curretPage1 " + curretPage);
            ++curretPage;
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
            	ParentInfo  parentInfo = (ParentInfo) curretUserInfo;
                info.append("\"fkStudentId\":");
                info.append("\"" +  parentInfo.defalutChild.id + "\"");
                info.append(",");
            }

            info.append("\"queryTime\":");
            info.append("\""+queryTime+"\"");
            info.append("}");

            Log.d(TAG, "info.toString() " + info.toString());
            
            try {
            	responseMessage.body = RestClient.getAttendanceInfos(curretUserInfo.id, curretUserInfo.roleType.toString(), curretUserInfo.ticket,info.toString(), curretPage, pageCount, queryTime);
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
			}catch (XmlPullParserException e) {
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
        	mGetAttendanceInfoTask = null;           
        	//AttendanceInfoDao attendanceInfoDao = new AttendanceInfoDao(mXiaoYunTongApplication);
            if(responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS ){
                totalCount = responseMessage.total;
                if(responseMessage.total>0){
                    toatlPage = totalCount % pageCount > 1 ? (totalCount / pageCount + 1)
                            : totalCount / pageCount;
                    try {                
                        ArrayList<AttendanceInfo> attendanceInfoList = JSONParser.praseAttendanceInfo(responseMessage.body);
                        if (attendanceInfoList != null && attendanceInfoList.size() > 0) {

//                            for (AttendanceInfo homeWorkInfo : attendanceInfoList) {
//                                long insertResult = attendanceInfoDao.addAttendanceInfo(homeWorkInfo);
//                                Log.i(TAG, "HomeWork insertResult " + insertResult);
//                            }
                            
                            mAttendanceInfoList.addAll(attendanceInfoList);
                            
//                            attendanceInfoDao.colseDb();
//                            attendanceInfoDao = null;
                        } else {
                            --curretPage;
                            if (totalCount == curretCount) {
                                Toast.makeText(AttendanceInfoActivity.this, R.string.no_more_data, Toast.LENGTH_SHORT)
                                        .show();
                            }
                        }
                        
                    } catch (JSONException e) {
                        --curretPage;
                        e.printStackTrace();
                    }          
                    curretCount = mAttendanceInfoList.size();
                   
                   
                } else if(responseMessage.total == 0){
                    curretCount = 0;
                    --curretPage;
                    mAttendanceInfoList.clear();
                    emptyView.setVisibility(View.VISIBLE);
                     Toast.makeText(AttendanceInfoActivity.this, R.string.no_data, Toast.LENGTH_LONG)
                     .show();
                } 

            }else {
                curretCount = 0;
                --curretPage;
                mAttendanceInfoList.clear();
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(AttendanceInfoActivity.this, responseMessage.message, Toast.LENGTH_LONG)
                .show();                          	
            }    
            if(mAttendanceInfoList.size()>0){
                mAttendanceInfoListView.setSelection(curretPage * pageCount - 4);
            }
            mAttendanceAdapter.notifyDataSetChanged();
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
        	mGetAttendanceInfoTask = null;

        }
    }

}
