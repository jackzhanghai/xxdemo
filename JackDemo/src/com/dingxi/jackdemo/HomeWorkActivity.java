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

import com.dingxi.jackdemo.HomePageActivity.GetAllNoteTask;
import com.dingxi.jackdemo.HomePageActivity.ImageAdapter.ViewHolder;
import com.dingxi.jackdemo.LoginActivity.UserLoginTask;
import com.dingxi.jackdemo.dao.HomeWorkDao;
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
import android.widget.AdapterView.OnItemSelectedListener;
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
	private View emptyView;
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
	private ArrayList<StudentInfo> mStudentList;
	private ArrayAdapter<String> mSpinnerAdapter;
    private ArrayList<String> mSpinnerInfo;

	public GetHomeWorTask mHomeWorTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_work);
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
				ParentInfo parentInfo = (ParentInfo) curretUserInfo;
				parentInfo.defalutChild = mStudentList.get(arg2);

				mHomeWorkList.clear();
				mHomeWorkAdapter.notifyDataSetChanged();

				loadingImageView.setVisibility(View.VISIBLE);
				emptyView.setVisibility(View.GONE);
				loadingAnimation.start();

				// ParentInfo parentInfo = (ParentInfo) curretUserInfo;
				mHomeWorTask = new GetHomeWorTask(1, 5,
						curretUserInfo.fkSchoolId, "", "",
						parentInfo.defalutChild.id, "", true);

				mHomeWorTask.execute((Void) null);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				Log.d(TAG, "mSpinner onNothingSelected()");
			}
		});

		if(curretUserInfo.roleType == UserType.ROLE_PARENT){
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
	        
	        mSpinnerAdapter = new ArrayAdapter<String>(HomeWorkActivity.this,
	                android.R.layout.simple_spinner_item, mSpinnerInfo);
	        mSpinnerAdapter
	                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

	        mSpinner.setAdapter(mSpinnerAdapter);
		}

		

		mQueryMessageButton = (ImageButton) findViewById(R.id.query_button);
		mQueryMessageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HomeWorkActivity.this,
						SearchMessageActivity.class);
				startActivityForResult(intent, R.layout.activity_search_message);

			}
		});

		mEditMessageButton = (ImageButton) findViewById(R.id.edit_button);
		mEditMessageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(HomeWorkActivity.this,
						EditHomeWorkActivity.class);
				startActivity(intent);

			}
		});

		emptyView = findViewById(R.id.empty);
		mHomeWorkListView = (ListView) findViewById(R.id.home_work_list);
		mHomeWorkListView.setEmptyView(emptyView);
		mHomeWorkList = new ArrayList<HomeWorkInfo>();
		mHomeWorkAdapter = new HomeWorkAdapter(HomeWorkActivity.this,
				mHomeWorkList);
		loadingImageView = (ImageView) findViewById(R.id.loading_message_image);
		loadingImageView
				.setBackgroundResource(R.drawable.loading_howework_animation);
		loadingAnimation = (AnimationDrawable) loadingImageView.getBackground();
		mHomeWorkListView.setAdapter(mHomeWorkAdapter);
		mHomeWorkListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(TAG, "arg2 " + arg2 + " arg3 " + arg3);
				String id = mHomeWorkList.get(arg2).id;
				Log.i(TAG, "id " + id);
				Intent intent = new Intent(HomeWorkActivity.this,
						HomeWorkDetailActivity.class);
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
		if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {
			TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
			mHomeWorTask = new GetHomeWorTask(1, 5, curretUserInfo.fkSchoolId,
					teacherInfo.defalutClassId, "", "", "", true);
		} else if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
			ParentInfo parentInfo = (ParentInfo) curretUserInfo;
			mHomeWorTask = new GetHomeWorTask(1, 5, curretUserInfo.fkSchoolId,
					"", "", parentInfo.defalutChild.id, "", true);
		}

		mHomeWorTask.execute((Void) null);
	}

	class HomeWorkAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<HomeWorkInfo> homeWorkList;
		private LayoutInflater inflater;

		private HomeWorkAdapter(Context context,
				ArrayList<HomeWorkInfo> homeWorkList) {

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
			Log.i(TAG, "getView()");
			HomeWorkHolder viewHolder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.homework_item, null);
				viewHolder = new HomeWorkHolder();
				viewHolder.headerText = (TextView) convertView
						.findViewById(R.id.message_header);
				viewHolder.dateText = (TextView) convertView
						.findViewById(R.id.message_date);
				viewHolder.bodyText = (TextView) convertView
						.findViewById(R.id.message_body);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (HomeWorkHolder) convertView.getTag();
			}

			Log.i(TAG,
					"homeWorkList.get(position).getId() "
							+ homeWorkList.get(position).id);
			// viewHolder.headerText.setText(homeWorkList.get(position).id);
			viewHolder.headerText.setText("家庭作业");
			Log.i(TAG, "homeWorkList.get(position).getContent() "
					+ homeWorkList.get(position).content);
			viewHolder.bodyText.setText(homeWorkList.get(position).content);
			viewHolder.dateText.setText(homeWorkList.get(position).optTime);
			return convertView;
		}

		class HomeWorkHolder {

			TextView headerText;
			TextView dateText;
			TextView bodyText;

		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK
				&& requestCode == R.layout.activity_search_message) {
			Bundle searchBundle = data.getExtras();
			searchBundle.getString("mGradeId");
			String classId2 = searchBundle.getString("mClassId");
			String studentId = searchBundle.getString("mStudentId");
			String date = searchBundle.getString("mData");
			mHomeWorkList.clear();
			mHomeWorkAdapter.notifyDataSetChanged();

			loadingImageView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
			loadingAnimation.start();
			if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {
				TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;

				mHomeWorTask = new GetHomeWorTask(1, 5,
						curretUserInfo.fkSchoolId, teacherInfo.defalutClassId,
						classId2, studentId, date, true);
			} else {
				ParentInfo parentInfo = (ParentInfo) curretUserInfo;
				mHomeWorTask = new GetHomeWorTask(1, 5,
						curretUserInfo.fkSchoolId, "", "",
						parentInfo.defalutChild.id, date, true);
			}
			mHomeWorTask.execute((Void) null);

		}
	}

	public class GetHomeWorTask extends AsyncTask<Void, Void, ResponseMessage> {

		int page, row;
		String fkSchoolId;
		String fkClassId;
		String fkClassId2;
		String fkStudentId;
		String queryTime;
		boolean isCustomSearch;

		public GetHomeWorTask(int page, int rows, String fkSchoolId,
				String fkClassId, String fkClassId2, String fkStudentId,
				String queryTime, boolean isCustomSearch) {
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

			try {
				responseMessage.body = RestClient.getHomeWorks(
						curretUserInfo.id, curretUserInfo.ticket,
						curretUserInfo.roleType.toString(), info.toString(),
						page, row);
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
			HomeWorkDao homeWorkDao = new HomeWorkDao(mXiaoYunTongApplication);
			if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS
					&& responseMessage.total > 0) {
				ArrayList<HomeWorkInfo> homeWorkInfoList = null;
				try {
					homeWorkInfoList = JSONParser
							.praseHomeWorks(responseMessage.body);

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (homeWorkInfoList != null && homeWorkInfoList.size() > 0) {

					for (HomeWorkInfo homeWorkInfo : homeWorkInfoList) {
						long insertResult = homeWorkDao
								.addHomeWork(homeWorkInfo);
						Log.i(TAG, "HomeWork insertResult " + insertResult);
					}

					if (isCustomSearch) {
						mHomeWorkList.clear();
						mHomeWorkList.addAll(homeWorkInfoList);
					} else {
						ArrayList<HomeWorkInfo> homeWork = homeWorkDao
								.queryReadOrNotReadCountHomeWork(0);
						Log.i(TAG, "homeWork.size() " + homeWork.size());
						mHomeWorkList.clear();
						mHomeWorkList.addAll(homeWork);

					}

				}

				homeWorkDao.colseDb();
				homeWorkDao = null;
				loadingAnimation.stop();
				loadingImageView.setVisibility(View.GONE);
				mHomeWorkAdapter.notifyDataSetChanged();

			} else {
				loadingAnimation.stop();
				loadingImageView.setVisibility(View.GONE);
				emptyView.setVisibility(View.VISIBLE);
				Toast.makeText(HomeWorkActivity.this, responseMessage.message,
						Toast.LENGTH_LONG).show();
			}
		}

		@Override
		protected void onCancelled() {
			mHomeWorTask = null;

		}
	}

}
