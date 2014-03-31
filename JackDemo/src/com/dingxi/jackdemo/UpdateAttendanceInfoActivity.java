package com.dingxi.jackdemo;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import com.dingxi.jackdemo.model.ClassInfo;
import com.dingxi.jackdemo.model.GradeInfo;
import com.dingxi.jackdemo.model.ParentInfo;
import com.dingxi.jackdemo.model.SubjectInfo;
import com.dingxi.jackdemo.model.TeacherInfo;
import com.dingxi.jackdemo.model.UserInfo;
import com.dingxi.jackdemo.model.AttendanceInfo.AttendanceInfoEntry;
import com.dingxi.jackdemo.network.JSONParser;
import com.dingxi.jackdemo.network.ResponseMessage;
import com.dingxi.jackdemo.network.RestClient;
import com.dingxi.jackdemo.util.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class UpdateAttendanceInfoActivity extends Activity implements OnClickListener {

	public static final String TAG = "EditHomeWorkActivity";
	private ImageButton mBackButton;
	private TeacherInfo curretUserInfo;
	private XiaoYunTongApplication mXiaoYunTongApplication;
	private ImageButton selectClassButton;
	private ImageButton selectGradeButton;
	private ImageButton selectSubjectButton;
	private TextView classNameText;
	private TextView gradeNameText;
	private TextView subjectNameText;
	private EditText titleEditText;
	private EditText contentEditText;
	private Button sendHomeWorkButton;
	private UpdateAttendanceInfo mUpdateAttendanceInfo;
	private SendHomeWorkTask mSendHomeWorkTask;
	private ProgressDialog mProgressDialog;
	private String mClassId;
	private String mGradeId;
	private String mSubjectId;
	private SearchType curretSearchType;
	public List<ClassInfo> classInfoList;
	public String[] classNameList;
	public ArrayList<GradeInfo> gradeInfoList;
	public String[] gradeNameList;
	public List<SubjectInfo> subjectInfoList;
	public String[] subjectNameList;

	private enum SearchType {
		ClassInfo, GradeInfo, SubjectInfo
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_update_attendance);

		mBackButton = (ImageButton) findViewById(R.id.back_button);
		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		String updateId = getIntent().getStringExtra(AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID);
		mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();
		curretUserInfo = (TeacherInfo) mXiaoYunTongApplication.userInfo;

		gradeNameText = (TextView) findViewById(R.id.select_grade_text);
		classNameText = (TextView) findViewById(R.id.select_class_text);
		subjectNameText = (TextView) findViewById(R.id.select_subject_text);

		selectClassButton = (ImageButton) findViewById(R.id.select_class_button);
		selectClassButton.setOnClickListener(this);
		selectGradeButton = (ImageButton) findViewById(R.id.select_grade_button);
		selectGradeButton.setOnClickListener(this);
		selectSubjectButton = (ImageButton) findViewById(R.id.select_subject_button);
		selectSubjectButton.setOnClickListener(this);

		titleEditText = (EditText) findViewById(R.id.edit_homework_title);
		contentEditText = (EditText) findViewById(R.id.edit_homework_content);
		sendHomeWorkButton = (Button) findViewById(R.id.edit_ok_button);
		sendHomeWorkButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(checkText()){
					if (Util.IsNetworkAvailable(UpdateAttendanceInfoActivity.this)) {
						mProgressDialog = new ProgressDialog(
								UpdateAttendanceInfoActivity.this);
						mProgressDialog
								.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						mProgressDialog
								.setMessage(getString(R.string.now_adding_homework));
						mProgressDialog.setOnCancelListener(new OnCancelListener() {

							@Override
							public void onCancel(DialogInterface dialog) {
								// TODO Auto-generated method stub
								if (mSendHomeWorkTask != null
										&& !mSendHomeWorkTask.isCancelled()) {
									mSendHomeWorkTask.cancel(true);
									// isCancelLogin = true;
								}
							}
						});
						String content = contentEditText.getText().toString();
						mProgressDialog.show();
						mSendHomeWorkTask = new SendHomeWorkTask(curretUserInfo.fkSchoolId,
								mGradeId, mClassId, mSubjectId, content);
						mSendHomeWorkTask.execute((Void) null);
					}
				}
				
			}
		});

	}

	protected boolean checkText() {
		// TODO Auto-generated method stub
		boolean isOk = false;
		//String title = titleEditText.getText().toString();
		String content = contentEditText.getText().toString();
//		if (TextUtils.isEmpty(title)) {
//
//		} else 
		if (TextUtils.isEmpty(content)) {
			Toast.makeText(UpdateAttendanceInfoActivity.this, R.string.please_input_content,
					Toast.LENGTH_LONG).show();
		} else if (TextUtils.isEmpty(mClassId)) {
			Toast.makeText(UpdateAttendanceInfoActivity.this, R.string.select_class,
					Toast.LENGTH_LONG).show();
		} else if (TextUtils.isEmpty(mGradeId)) {
			Toast.makeText(UpdateAttendanceInfoActivity.this, R.string.select_grade,
					Toast.LENGTH_LONG).show();
		} else if (TextUtils.isEmpty(mSubjectId)){
			Toast.makeText(UpdateAttendanceInfoActivity.this, R.string.select_subject,
					Toast.LENGTH_LONG).show();
		} else {
			isOk = true;
		}
				
		return isOk;

	}

	public class UpdateAttendanceInfo extends AsyncTask<Void, Void, ResponseMessage> {

		String fkAttId;
		String direc;
		public UpdateAttendanceInfo(String fkAttId,String direc) {
			// TODO Auto-generated constructor stub
			this.fkAttId = fkAttId;
			this.direc = direc;
		}
		@Override
		protected ResponseMessage doInBackground(Void... params) {
			
			
			StringBuilder info = new StringBuilder();
			info.append("{");
			info.append("\"fkAttId\":");
			info.append("\"" + fkAttId + "\"");
			info.append(",");
			info.append("\"direc\":");
			info.append("\"" + direc + "\"");			
			info.append("}");
			
			
			// TODO: attempt authentication against a network service.
			ResponseMessage responseMessage = new ResponseMessage();
			try {

				
					responseMessage.body = RestClient.updateAttendance(
							curretUserInfo.id, curretUserInfo.ticket,
							info.toString());	
					
					responseMessage.praseBody();

				if (TextUtils.isEmpty(responseMessage.body)) {

				} else {
					

				}

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
			mUpdateAttendanceInfo = null;

			Log.d(TAG, "responseMessage.code " + responseMessage.code);
			if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
			
				mProgressDialog.dismiss();
				if (curretSearchType == SearchType.GradeInfo) {
					showDialog(R.id.select_grade_button);
				} else if (curretSearchType == SearchType.ClassInfo) {
					showDialog(R.id.select_class_button);
				} else if (curretSearchType == SearchType.SubjectInfo) {
					showDialog(R.id.select_subject_button);
				}
			} else {
				mProgressDialog.dismiss();
				Toast.makeText(UpdateAttendanceInfoActivity.this,
						responseMessage.message, Toast.LENGTH_LONG).show();
			}

		}

		@Override
		protected void onCancelled() {
			mUpdateAttendanceInfo = null;

		}
	}

	public class SendHomeWorkTask extends
			AsyncTask<Void, Void, ResponseMessage> {

		String fkSchoolId, fkGradeId, fkClassId, fkSubjectId, content;

		public SendHomeWorkTask(String fkSchoolId, String fkGradeId,
				String fkClassId, String fkSubjectId, String content) {
			this.fkClassId = fkClassId;
			this.fkGradeId = fkGradeId;
			this.content = content;
			this.fkSubjectId = fkSubjectId;
			this.fkSchoolId = fkSchoolId;
		}

		@Override
		protected ResponseMessage doInBackground(Void... params) {

			StringBuilder info = new StringBuilder();
			info.append("{");
			info.append("\"fkSchoolId\":");
			info.append("\"" + fkSchoolId + "\"");
			info.append(",");

			info.append("\"fkGradeId\":");
			info.append("\"" + fkGradeId + "\"");
			info.append(",");

			info.append("\"fkClassId\":");
			info.append("\"" + fkClassId + "\"");
			info.append(",");
			info.append("\"fkSubjectId\":");
			info.append("\"" + fkSubjectId + "\"");
			info.append(",");

			info.append("\"content\":");
			info.append("\"" + content + "\"");
			info.append("}");

			Log.d(TAG, "info.toString() " + info.toString());

			ResponseMessage responseMessage = new ResponseMessage();
			try {

				responseMessage.body = RestClient.addHomeWorks(
						curretUserInfo.id, curretUserInfo.ticket,
						info.toString());
				Log.d(TAG, "SendHomeWorkTask response " + responseMessage.body);

				if (TextUtils.isEmpty(responseMessage.body)) {

				} else {
					responseMessage.code = JSONParser.getIntByTag(
							responseMessage.body,
							ResponseMessage.RESULT_TAG_CODE);
					responseMessage.message = JSONParser.getStringByTag(
							responseMessage.body,
							ResponseMessage.RESULT_TAG_MESSAGE);
					responseMessage.datas = JSONParser.getStringByTag(
							responseMessage.body,
							ResponseMessage.RESULT_TAG_DATAS);
					responseMessage.total = JSONParser.getIntByTag(
							responseMessage.body,
							ResponseMessage.RESULT_TAG_TOTAL);

				}

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
			mSendHomeWorkTask = null;

			Log.d(TAG, "responseMessage.code " + responseMessage.code);
			if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
				mProgressDialog.dismiss();				
			} else {
				mProgressDialog.dismiss();				
			}
			
			Toast.makeText(UpdateAttendanceInfoActivity.this,
					responseMessage.message, Toast.LENGTH_LONG).show();

		}

		@Override
		protected void onCancelled() {
			mSendHomeWorkTask = null;

		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (Util.IsNetworkAvailable(UpdateAttendanceInfoActivity.this)) {
			mProgressDialog = new ProgressDialog(UpdateAttendanceInfoActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			int errorCode = -1;
			switch (v.getId()) {
			case R.id.select_class_button:

				if (!TextUtils.isEmpty(mGradeId)) {
					curretSearchType = SearchType.ClassInfo;
					mProgressDialog
							.setMessage(getString(R.string.now_geting_classinfo));
				} else {
					errorCode = R.string.select_grade;
				}

				break;
			case R.id.select_grade_button:
				if (!TextUtils.isEmpty(curretUserInfo.fkSchoolId)) {
					curretSearchType = SearchType.GradeInfo;
					mProgressDialog
							.setMessage(getString(R.string.now_geting_classinfo));
				} else {
					errorCode = R.string.select_school;
				}

				break;
			case R.id.select_subject_button:
				if (!TextUtils.isEmpty(mClassId)) {
					curretSearchType = SearchType.SubjectInfo;
					mProgressDialog
							.setMessage(getString(R.string.now_geting_classinfo));
				} else {
					errorCode = R.string.select_class;
				}

				break;
			default:
				break;
			}

			if (errorCode != -1) {
				Toast.makeText(UpdateAttendanceInfoActivity.this, errorCode,
						Toast.LENGTH_LONG).show();
				mProgressDialog = null;
			} else {
				mProgressDialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						if (mUpdateAttendanceInfo != null
								&& !mUpdateAttendanceInfo.isCancelled()) {
							mUpdateAttendanceInfo.cancel(true);
							// isCancelLogin = true;
						}
					}
				});
				mProgressDialog.show();
				mUpdateAttendanceInfo = new UpdateAttendanceInfo(curretUserInfo.id, curretUserInfo.ticket);
				mUpdateAttendanceInfo.execute((Void) null);
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.select_class_button:

			if (classInfoList != null && classInfoList.size() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						UpdateAttendanceInfoActivity.this);
				builder.setTitle(R.string.select_school).setItems(
						classNameList, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Log.d(TAG, "which " + which);
								mClassId = classInfoList.get(which).id;
								classNameText.setText(classInfoList.get(which).name);
								Log.d(TAG, "mClassId " + mClassId);
								// The 'which' argument contains the index
								// position
								// of the selected item
							}
						});
				return builder.create();
			}
			break;
		case R.id.select_grade_button:

			if (gradeInfoList != null && gradeInfoList.size() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						UpdateAttendanceInfoActivity.this);
				builder.setTitle(R.string.select_school).setItems(
						gradeNameList, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Log.d(TAG, "which " + which);
								mGradeId = gradeInfoList.get(which).id;
								gradeNameText.setText(gradeInfoList.get(which).name);
								Log.d(TAG, "mGradeId " + mGradeId);
								// The 'which' argument contains the index
								// position
								// of the selected item
							}
						});
				return builder.create();
			}
			break;
		case R.id.select_subject_button:

			if (subjectNameList != null && subjectNameList.length > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						UpdateAttendanceInfoActivity.this);
				builder.setTitle(R.string.select_school).setItems(
						subjectNameList, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Log.d(TAG, "which " + which);
								mSubjectId = subjectInfoList.get(which).id;
								subjectNameText.setText(subjectInfoList
										.get(which).name);
								Log.d(TAG, "mSubjectId " + mSubjectId);
								// The 'which' argument contains the index
								// position
								// of the selected item
							}
						});
				return builder.create();
			}
			break;
		default:
			break;
		}
		return null;

	}

}
