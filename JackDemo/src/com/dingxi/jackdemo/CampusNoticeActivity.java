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
import com.dingxi.jackdemo.model.CampusNotice.CampusNoticeEntry;
import com.dingxi.jackdemo.model.HomeWorkInfo;
import com.dingxi.jackdemo.model.ParentInfo;
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

		mQueryMessageButton = (ImageButton) findViewById(R.id.query_button);
		mQueryMessageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(CampusNoticeActivity.this,
						SearchMessageActivity.class);
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
		mHomeWorkListView = (ListView) findViewById(R.id.home_work_list);
		mHomeWorkListView.setEmptyView(emptyView);
		mCampusNoticeList = new ArrayList<CampusNotice>();
		mCampusNoticeAdapter = new CampusNoticeAdapter(
				CampusNoticeActivity.this, mCampusNoticeList);
		loadingImageView = (ImageView) findViewById(R.id.loading_message_image);
		loadingImageView
				.setBackgroundResource(R.drawable.loading_howework_animation);
		loadingAnimation = (AnimationDrawable) loadingImageView.getBackground();
		mHomeWorkListView.setAdapter(mCampusNoticeAdapter);
		mHomeWorkListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i(TAG, "arg2 " + arg2 + " arg3 " + arg3);
				String id = mCampusNoticeList.get(arg2).id;
				Log.i(TAG, "id " + id);
				Intent intent = new Intent(CampusNoticeActivity.this,
						CampusNoticeDetailActivity.class);
				intent.putExtra(CampusNoticeEntry.COLUMN_NAME_ENTRY_ID, id);
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
			mGetmCampusNoticeTask = new GetmCampusNoticeListTask(1, 5,
					curretUserInfo.fkSchoolId, teacherInfo.defalutClassId, "",
					"", "",false);
		} else if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
			ParentInfo parentInfo = (ParentInfo) curretUserInfo;
			mGetmCampusNoticeTask = new GetmCampusNoticeListTask(1, 5,
					curretUserInfo.fkSchoolId, "", "",
					parentInfo.defalutChild.id, "",false);
		}

		mGetmCampusNoticeTask.execute((Void) null);
	}

	class CampusNoticeAdapter extends BaseAdapter {

		private Context context;
		private ArrayList<CampusNotice> campusNoticeList;
		private LayoutInflater inflater;

		private CampusNoticeAdapter(Context context,
				ArrayList<CampusNotice> campusNoticeList) {

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
				viewHolder.dateText  = (TextView) convertView
						.findViewById(R.id.message_date);
				viewHolder.bodyText = (TextView) convertView
						.findViewById(R.id.message_body);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (HomeWorkHolder) convertView.getTag();
			}

			Log.i(TAG,
					"homeWorkList.get(position).getId() "
							+ campusNoticeList.get(position).id);
			//viewHolder.headerText.setText(campusNoticeList.get(position).id);
			viewHolder.headerText.setText("校园通知");
			Log.i(TAG, "homeWorkList.get(position).getContent() "
					+ campusNoticeList.get(position).content);
			viewHolder.bodyText.setText(campusNoticeList.get(position).content);
			viewHolder.dateText.setText(campusNoticeList.get(position).optTime);
			

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
			mCampusNoticeList.clear();
			mCampusNoticeAdapter.notifyDataSetChanged();

			loadingImageView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.GONE);
			loadingAnimation.start();
			if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {

				TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
				mGetmCampusNoticeTask = new GetmCampusNoticeListTask(1, 5,
						curretUserInfo.fkSchoolId, teacherInfo.defalutClassId,
						classId2, studentId, date,true);
			} else {
				ParentInfo parentInfo = (ParentInfo) curretUserInfo;
				mGetmCampusNoticeTask = new GetmCampusNoticeListTask(1, 5,
						curretUserInfo.fkSchoolId, "", "",
						parentInfo.defalutChild.id, date,true);
			}
			mGetmCampusNoticeTask.execute((Void) null);

		}
	}

	public class GetmCampusNoticeListTask extends
			AsyncTask<Void, Void, ResponseMessage> {

		int page, row;
		String fkSchoolId;
		String fkClassId;
		String fkClassId2;
		String fkStudentId;
		String queryTime;
		boolean isCustomSearch;

		public GetmCampusNoticeListTask(int page, int rows, String fkSchoolId,
				String fkClassId, String fkClassId2, String fkStudentId,
				String queryTime,boolean isCustomSearch) {
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

				responseMessage.body = RestClient.getMessageInfos(
						mXiaoYunTongApplication.userInfo.id,
						mXiaoYunTongApplication.userInfo.ticket,
						curretUserInfo.roleType.toString(), info.toString(), page,
						row);
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

			CampusNoticeDao campusNoticeDao = new CampusNoticeDao(
					CampusNoticeActivity.this);
			if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS
					&& responseMessage.total > 0) {

				try {

					ArrayList<CampusNotice> campusNoticeList = JSONParser
							.praseCampusNotice(responseMessage.body);

					if (campusNoticeList != null && campusNoticeList.size() > 0) {

						for (CampusNotice campusNotice : campusNoticeList) {
							long insertResult = campusNoticeDao
									.addCampusNotice(campusNotice);
							Log.i(TAG, "campusNotice insertResult "
									+ insertResult);
						}

						if(isCustomSearch){
							mCampusNoticeList.clear();
							mCampusNoticeList.addAll(campusNoticeList);
							mCampusNoticeAdapter.notifyDataSetChanged();
						} else {
							ArrayList<CampusNotice> campusNotice = campusNoticeDao
									.queryReadOrNotReadCampusNotice(0);
							Log.i(TAG, "campusNotice.size() " + campusNotice.size());
							mCampusNoticeList.clear();
							mCampusNoticeList.addAll(campusNotice);
							mCampusNoticeAdapter.notifyDataSetChanged();
						}
						
					}else {
						ArrayList<CampusNotice> campusNotice = campusNoticeDao
								.queryReadOrNotReadCampusNotice(0);
						Log.i(TAG, "campusNotice.size() " + campusNotice.size());
						mCampusNoticeList.clear();
						mCampusNoticeList.addAll(campusNotice);
						mCampusNoticeAdapter.notifyDataSetChanged();
					};

				} catch (JSONException e) {
					
					e.printStackTrace();
				}
				
				loadingAnimation.stop();
				loadingImageView.setVisibility(View.GONE);
				campusNoticeDao.colseDb();
				campusNoticeDao = null;

				Log.d(TAG,
						"mCampusNoticeList.size() " + mCampusNoticeList.size());

			} else {

				emptyView.setVisibility(View.VISIBLE);
				Toast.makeText(CampusNoticeActivity.this,
						responseMessage.message, Toast.LENGTH_LONG).show();
			}
			
			loadingAnimation.stop();
			loadingImageView.setVisibility(View.GONE);
			
			

		}

		@Override
		protected void onCancelled() {
			mGetmCampusNoticeTask = null;

		}
	}

}
