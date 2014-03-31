package com.dingxi.jackdemo;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import com.dingxi.jackdemo.CampusNoticeActivity.GetmCampusNoticeListTask;
import com.dingxi.jackdemo.HomeWorkActivity.GetHomeWorTask;
import com.dingxi.jackdemo.HomeWorkActivity.HomeWorkAdapter;
import com.dingxi.jackdemo.HomeWorkActivity.HomeWorkAdapter.HomeWorkHolder;
import com.dingxi.jackdemo.dao.AttendanceInfoDao;
import com.dingxi.jackdemo.dao.HomeWorkDao;
import com.dingxi.jackdemo.model.AttendanceInfo;
import com.dingxi.jackdemo.model.HomeWorkInfo;
import com.dingxi.jackdemo.model.ParentInfo;
import com.dingxi.jackdemo.model.StudentInfo;
import com.dingxi.jackdemo.model.TeacherInfo;
import com.dingxi.jackdemo.model.UserInfo;
import com.dingxi.jackdemo.model.HomeWorkInfo.HomeWorkEntry;
import com.dingxi.jackdemo.model.UserInfo.UserType;
import com.dingxi.jackdemo.network.JSONParser;
import com.dingxi.jackdemo.network.ResponseMessage;
import com.dingxi.jackdemo.network.RestClient;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
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

	
	public static final String TAG = "HomeWorkActivity";
    private View emptyView;
    private ImageButton mBackButton;
    private ImageButton mQueryMessageButton;
    private ImageButton mEditMessageButton;
    private Spinner mSpinner;
    private ListView mHomeWorkListView;
    private AttendanceAdapter mAttendanceAdapter;
    private ArrayList<AttendanceInfo> mAttendanceInfoList;
    private ImageView loadingImageView;
    private AnimationDrawable loadingAnimation;
    private static UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private ArrayList<StudentInfo> mStudentList;
    private ArrayAdapter<String> mSpinnerAdapter;
    private ArrayList<String> mSpinnerInfo;

 
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
                // TODO Auto-generated method stub
                ParentInfo parentInfo = (ParentInfo) curretUserInfo;
                parentInfo.defalutChild = mStudentList.get(arg2);

                mAttendanceInfoList.clear();
                mAttendanceAdapter.notifyDataSetChanged();

                loadingImageView.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
                loadingAnimation.start();

               // ParentInfo  parentInfo = (ParentInfo) curretUserInfo;
                mGetAttendanceInfoTask = new GetAttendanceInfoTask(1, 5, curretUserInfo.fkSchoolId, "", "", parentInfo.defalutChild.id, "");


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
                startActivityForResult(intent, R.layout.activity_search_message);
                
            }
        });
        
        mEditMessageButton = (ImageButton) findViewById(R.id.edit_button);
        mEditMessageButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(AttendanceInfoActivity.this,UpdateAttendanceInfoActivity.class);
                startActivity(intent);
                
            }
        });
        
        emptyView = findViewById(R.id.empty);
        mHomeWorkListView = (ListView) findViewById(R.id.attendanceinfo_list);
        mHomeWorkListView.setEmptyView(emptyView);
        mAttendanceInfoList = new ArrayList<AttendanceInfo>();
        mAttendanceAdapter = new AttendanceAdapter(AttendanceInfoActivity.this, mAttendanceInfoList);
        loadingImageView = (ImageView) findViewById(R.id.loading_message_image);
        loadingImageView.setBackgroundResource(R.drawable.loading_howework_animation);
        loadingAnimation = (AnimationDrawable) loadingImageView.getBackground();
        mHomeWorkListView.setAdapter(mAttendanceAdapter);
        mHomeWorkListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
			    Log.i(TAG, "arg2 " +arg2 + " arg3 " + arg3);
//			    Integer id = mAttendanceInfoList.get(arg2).id;
//			    Log.i(TAG, "id " + id );
//				Intent intent = new Intent(AttendanceInfoActivity.this,HomeWorkDetailActivity.class);
//				intent.putExtra(HomeWorkEntry.COLUMN_NAME_ENTRY_ID, id);
//				startActivity(intent);
			}
        	
		});
        

        if (curretUserInfo.roleType.equals(UserType.ROLE_TEACHER)) {
            mQueryMessageButton.setVisibility(View.VISIBLE);
            mEditMessageButton.setVisibility(View.VISIBLE);
            mSpinner.setVisibility(View.GONE);
        } else {
            
            ParentInfo parentInfo = (ParentInfo) curretUserInfo;

            if (parentInfo.childList != null) {
                mStudentList = parentInfo.childList;
            } else {
                mStudentList = new ArrayList<StudentInfo>();
            }

            if (parentInfo.nameList != null) {
                mSpinnerInfo = parentInfo.nameList;
            } else {
                mSpinnerInfo = new ArrayList<String>();
            }
            
            mSpinnerAdapter = new ArrayAdapter<String>(AttendanceInfoActivity.this,
                    android.R.layout.simple_spinner_item, mSpinnerInfo);
            mSpinnerAdapter
                    .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            mSpinner.setAdapter(mSpinnerAdapter);
            mQueryMessageButton.setVisibility(View.GONE);
            mEditMessageButton.setVisibility(View.GONE);
            mSpinner.setVisibility(View.VISIBLE);
        }

        loadingImageView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        loadingAnimation.start();
        if(curretUserInfo.roleType == UserType.ROLE_TEACHER){
        	TeacherInfo teacherInfo  = (TeacherInfo) curretUserInfo;       	
        	mGetAttendanceInfoTask = new GetAttendanceInfoTask(1, 5, curretUserInfo.fkSchoolId, teacherInfo.defalutClassId, "", "", "");
        } else  if(curretUserInfo.roleType == UserType.ROLE_PARENT){
        	ParentInfo  parentInfo = (ParentInfo) curretUserInfo;
        	mGetAttendanceInfoTask = new GetAttendanceInfoTask(1, 5, curretUserInfo.fkSchoolId, "", "", parentInfo.defalutChild.id, "");
        }
       
        mGetAttendanceInfoTask.execute((Void) null);
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
            return 0;
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
            	viewHolder.inOutText.setText(R.string.unkown_school);
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
            	
            	mGetAttendanceInfoTask = new GetAttendanceInfoTask(1, 5, curretUserInfo.fkSchoolId, teacherInfo.defalutClassId, classId2, studentId, date);
            } else {
            	ParentInfo  parentInfo = (ParentInfo) curretUserInfo;
            	mGetAttendanceInfoTask = new GetAttendanceInfoTask(1, 5, curretUserInfo.fkSchoolId, "", "", parentInfo.defalutChild.id, date);
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
            	responseMessage.body = RestClient.getAttendanceInfos(curretUserInfo.id, curretUserInfo.roleType.toString(), curretUserInfo.ticket,info.toString(), page, row, queryTime);
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
        	AttendanceInfoDao attendanceInfoDao = new AttendanceInfoDao(mXiaoYunTongApplication);
            if(responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS && responseMessage.total>0){
            	
            	try {           		
            		ArrayList<AttendanceInfo> attendanceInfoList = JSONParser.praseAttendanceInfo(responseMessage.body);
                    if (attendanceInfoList != null && attendanceInfoList.size() > 0) {

                        for (AttendanceInfo homeWorkInfo : attendanceInfoList) {
                            long insertResult = attendanceInfoDao.addAttendanceInfo(homeWorkInfo);
                            Log.i(TAG, "HomeWork insertResult " + insertResult);
                        }
                        
                        mAttendanceInfoList.clear();
                        mAttendanceInfoList.addAll(attendanceInfoList);
                        attendanceInfoDao.colseDb();
                        attendanceInfoDao = null;
                    } 
                    
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            	
                
                loadingAnimation.stop();
                loadingImageView.setVisibility(View.GONE);
                mAttendanceAdapter.notifyDataSetChanged();

            } else if(responseMessage.total == 0){
            	loadingAnimation.stop();
                loadingImageView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
            	 Toast.makeText(AttendanceInfoActivity.this, R.string.no_data, Toast.LENGTH_LONG)
                 .show();
            }else {
            	loadingAnimation.stop();
                loadingImageView.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                Toast.makeText(AttendanceInfoActivity.this, responseMessage.message, Toast.LENGTH_LONG)
                .show();                          	
            }                                            
        }

        @Override
        protected void onCancelled() {
        	mGetAttendanceInfoTask = null;

        }
    }

}
