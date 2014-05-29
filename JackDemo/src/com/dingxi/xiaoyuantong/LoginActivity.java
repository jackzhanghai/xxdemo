package com.dingxi.xiaoyuantong;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.dingxi.xiaoyuantong.model.ParentInfo;
import com.dingxi.xiaoyuantong.model.SchoolInfo;
import com.dingxi.xiaoyuantong.model.TeacherInfo;
import com.dingxi.xiaoyuantong.model.UserInfo.UserType;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;
import com.dingxi.xiaoyuantong.util.Util;

public class LoginActivity extends Activity {

	protected static final String TAG = "LoginActivity";

	public static final String PREFS_USER_INFO = "user_info";
	public static final String LOGIN_SCHOOL_ID = "login_school_id";
	public static final String LOGIN_SCHOOL_NAME = "login_school_name";
	public static final String lOGIN_USER_NAME = "login_user_name";
	public static final String LOGIN_PASSWORD = "login_password";
	public static final String LOGIN_ROLE_ID = "login_role_id";
	public static final String IS_AUTO_LOGIN = "is_auto_login";
	public static final String IS_REMEMBER_PASSWORD = "is_remember_password";

	// UI references.
	private EditText mUserNameEditText;
	private EditText mPasswordEditText;
	private Button mLoginButton;
	private Button mCancelLoginButton;
	private UserLoginTask mAuthTask;
	private GetAllShoolTask mGetAllShoolTask;
	private CheckBox mAutoLoginCheckBox;
	private CheckBox mRemberCheckBox;

	// Values for email and password at the time of the login attempt.
	//private String mSchoolId;
	//private String mSchoolName;
	private String mUserName;
	private String mPassword;
	private UserType mUserType = UserType.ROLE_PARENT;
	private ArrayList<SchoolInfo> schoolList;
	private String schoolsString[];
	private boolean isAutoLogin = false;
	private boolean isRemberPassWord = false;
	private ProgressDialog mProgressDialog;
	 private XiaoYunTongApplication mXiaoYunTongApplication;;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_login);

		mUserNameEditText = (EditText) findViewById(R.id.user_name);
		mPasswordEditText = (EditText) findViewById(R.id.password);
		/*
		mPasswordEditText.setOnEditorActionListener(new OnEditorActionListener() {
            
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                // TODO Auto-generated method stub
                Log.i(TAG, "mPasswordEditText actionId " + actionId +" event "+ event.getKeyCode());
                return false;
            }
        });
        */
		mPasswordEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                // TODO Auto-generated method stub
                Log.i(TAG, "mPasswordEditText hasFocus " + hasFocus);
               String pass = mPasswordEditText.getText().toString();

                
            }
        });
		
		mAutoLoginCheckBox = (CheckBox) findViewById(R.id.auto_login_checkBox);
		mRemberCheckBox = (CheckBox) findViewById(R.id.remember_password_checkBox);

		mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();
		SharedPreferences settings = getSharedPreferences(PREFS_USER_INFO, 0);
		isAutoLogin = settings.getBoolean(IS_AUTO_LOGIN, false);
		isRemberPassWord = settings.getBoolean(IS_REMEMBER_PASSWORD, false);
		String roleId = settings.getString(LOGIN_ROLE_ID, "0");
		if("0".equalsIgnoreCase(roleId)){
			mUserType = UserType.ROLE_PARENT;
		} else if("1".equalsIgnoreCase(roleId)){
			mUserType = UserType.ROLE_TEACHER;
		}
		
		// isAutoLogin = getIntent().getBooleanExtra("auto", false);
		setAuthUI(mUserType);
		if (isRemberPassWord || isAutoLogin) {

			mUserName = settings.getString(lOGIN_USER_NAME, "");
			mPassword = settings.getString(LOGIN_PASSWORD, "");

			mUserNameEditText.setText(mUserName);
			mPasswordEditText.setText(mPassword);
			mAutoLoginCheckBox.setChecked(isAutoLogin);
			mRemberCheckBox.setChecked(isRemberPassWord);
			if (isAutoLogin) {

				if (Util.IsNetworkAvailable(LoginActivity.this)) {
					autoLogin();
				} else {
					Toast.makeText(LoginActivity.this, R.string.not_network,
							Toast.LENGTH_LONG).show();
				}

			}

		}

		String schoolsInfo = getIntent().getStringExtra("schoolInfo");
		if(!TextUtils.isEmpty(schoolsInfo)){
			parseSchoolInfo(schoolsInfo);
		}
		

		mPasswordEditText = (EditText) findViewById(R.id.password);
		mLoginButton = (Button) findViewById(R.id.confirm_login_button);
		mCancelLoginButton = (Button) findViewById(R.id.cancel_login_button);

		mLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				attemptLogin();

			}

		});

		mCancelLoginButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void parseSchoolInfo(String schools) {
		if (!TextUtils.isEmpty(schools)) {
			Log.d(TAG, "result schoolsInfo " + schools);
			try {
				if (JSONParser.getIntByTag(schools, ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

					schoolList = JSONParser.toParserSchoolList(schools);
				} else {
					JSONParser.getStringByTag(schools,
							ResponseMessage.RESULT_TAG_MESSAGE);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (schoolList != null && schoolList.size() > 0) {
			schoolsString = new String[schoolList.size()];

			for (int i = 0; i < schoolList.size(); i++) {
				schoolsString[i] = schoolList.get(i).name;

			}
		}
	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub
//		switch (id) {
//		case R.id.choose_school:
//
//			if (schoolList != null && schoolList.size() > 0) {
//				AlertDialog.Builder builder = new AlertDialog.Builder(
//						LoginActivity.this);
//				builder.setTitle(R.string.select_school).setItems(
//						schoolsString, new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//								Log.d(TAG, "which " + which);
//								mSchoolId = schoolList.get(which).id;
//								mSchoolName = schoolList.get(which).name;
//								mSchoolNameText.setText(mSchoolName);
//								Log.d(TAG, "mSchoolId " + mSchoolId);
//							}
//						});
//				return builder.create();
//			}
//
//			// break;
//		default:
//			break;
//		}
		return null;

	}

	public void onCheckBoxButtonClicked(View view) {

		boolean isSelect = ((CheckBox) view).isChecked();
		Log.d(TAG, "onCheckBoxButtonClicked() isSelect " + isSelect);
		switch (view.getId()) {
		case R.id.remember_password_checkBox:
			if (isSelect) {
				isRemberPassWord = true;
			} else {
				isRemberPassWord = false;
			}
			break;
		case R.id.auto_login_checkBox:
			if (isSelect) {
				isAutoLogin = true;
			} else {
				isAutoLogin = false;
			}
			break;
		}
		Log.d(TAG, "isAutoLogin " + isAutoLogin + " isRemberPassWord "
				+ isRemberPassWord);
	}

//	public void onChooseSchoolButtonClicked(View view) {
//
//		if (schoolsString != null && schoolsString.length > 0) {
//
//			showDialog(R.id.choose_school);
//		} else {
//			mProgressDialog = new ProgressDialog(LoginActivity.this);
//			mProgressDialog.setMessage(getString(R.string.loading_school));
//			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//			mProgressDialog.setOnCancelListener(new OnCancelListener() {
//
//				@Override
//				public void onCancel(DialogInterface dialog) {
//					// TODO Auto-generated method stub
//					if (mGetAllShoolTask != null && !mGetAllShoolTask.isCancelled()) {
//						mGetAllShoolTask.cancel(true);
//						// isCancelLogin = true;
//					}
//					mProgressDialog = null;
//					mGetAllShoolTask = null;
//				}
//			});
//			mProgressDialog.show();
//			mGetAllShoolTask = new GetAllShoolTask();
//			mGetAllShoolTask.execute((Void) null);
//		}
//	}

	public void onRadioButtonClicked(View view) {
		boolean checked = ((RadioButton) view).isChecked();
		Log.d(TAG, "onRadioButtonClicked() checked " + checked);
		switch (view.getId()) {
		case R.id.radio_teacher:
			if (checked)
				mUserType = UserType.ROLE_TEACHER;
			break;
		case R.id.radio_parent:
			if (checked)
				mUserType = UserType.ROLE_PARENT;
			break;
		}

		setAuthUI(mUserType);
		Log.d(TAG, "mRoleId " + mUserType);
	}

	private void setAuthUI(UserType userType) {

		TextView whoAuth = (TextView) findViewById(R.id.who_auth);
		if (UserType.ROLE_PARENT == userType) {
			findViewById(R.id.radio_parent).setBackgroundResource(
					R.drawable.parent_auth_select);
			findViewById(R.id.radio_teacher).setBackgroundResource(
					R.drawable.teacher_auth_nomal);
			whoAuth.setText(R.string.parent_authority);
		} else {
			findViewById(R.id.radio_teacher).setBackgroundResource(
					R.drawable.teacher_auth_selected);
			findViewById(R.id.radio_parent).setBackgroundResource(
					R.drawable.parent_auth_nomal);
			whoAuth.setText(R.string.teacher_authority);
		}

	}

	private void autoLogin() {
		// TODO Auto-generated method stub
		mProgressDialog = new ProgressDialog(LoginActivity.this);
		mProgressDialog.setMessage(getString(R.string.now_logining));
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				if (mAuthTask != null && !mAuthTask.isCancelled()) {
					mAuthTask.cancel(true);
					
					// isCancelLogin = true;
				}
			}
		});
		mProgressDialog.show();
		mAuthTask = new UserLoginTask();
		mAuthTask.execute((Void) null);
	}

	public void attemptLogin() {
		// Store values at the time of the login attempt.
		mUserName = mUserNameEditText.getText().toString();
		mPassword = mPasswordEditText.getText().toString();
		boolean isError = false;
		String errorMessage = null;
		
	if (TextUtils.isEmpty(mPassword)) {
			errorMessage = getString(R.string.input_password);
			isError = true;
		} else if (mPassword.length() < 6) {
			errorMessage = getString(R.string.error_invalid_password);
			isError = true;
		}else if(mPassword.length() >12){
		    errorMessage = getString(R.string.error_invalid_password);
            isError = true;
		}else if (TextUtils.isEmpty(mUserName)) {
			errorMessage = getString(R.string.input_accont);
			isError = true;
		}

		if (isError) {
			// There was an error; don't attempt login and focus the first
			Toast.makeText(LoginActivity.this, errorMessage,
					Toast.LENGTH_LONG).show();
		} else {
			autoLogin();
		}
	}


	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, ResponseMessage> {
		@Override
		protected ResponseMessage doInBackground(Void... params) {
		    ResponseMessage  responseMessage = new  ResponseMessage();
	
			try {
			    responseMessage.body = RestClient.login(mUserName, mPassword,
						mUserType.toString());
                responseMessage.praseBody();

			} catch (ConnectTimeoutException stex) {
			    responseMessage.message = getString(R.string.request_time_out);
				stex.printStackTrace();
			} catch (SocketTimeoutException stex) {			    
			    responseMessage.message = getString(R.string.server_time_out);
				stex.printStackTrace();
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
		protected void onPostExecute(ResponseMessage  responseMessage) {
			mAuthTask = null;
			Log.d(TAG, "login response " + responseMessage.body);
			
			boolean isLoginSuccss = false;
			
			if(responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS){

			    
			    // to do save userinfo;
               
                try {                    
                    
                    String id = JSONParser.getStringByTag(responseMessage.body, "id");
                    if(!TextUtils.isEmpty(id)){
                        if(mUserType == UserType.ROLE_PARENT){
                            mXiaoYunTongApplication.userInfo = new ParentInfo();                                
                        }else if(mUserType == UserType.ROLE_TEACHER){
                            String fkClassId = JSONParser.getStringByTag(responseMessage.body,
                                        "fkClassId");                      
                                String fkSchoolId = JSONParser.getStringByTag(responseMessage.body,
                                        "fkSchoolId");
                               TeacherInfo teacherInfo  = new TeacherInfo();
                               teacherInfo.defalutClassId = fkClassId;
                               teacherInfo.defalutSchoolId = fkSchoolId;
                               mXiaoYunTongApplication.userInfo = teacherInfo;                        
                                                         
                        }

                        mXiaoYunTongApplication.userInfo.id = id;
                        Log.d(TAG, "mXiaoYunTongApplication.userInfo.id " + mXiaoYunTongApplication.userInfo.id);
                        String ticket = JSONParser.getStringByTag(responseMessage.body,
                                "ticket");
                        mXiaoYunTongApplication.userInfo.ticket = ticket;
                        mXiaoYunTongApplication.userInfo.roleType =  mUserType;
                        Log.d(TAG, "userInfo.roleType " + mUserType);

                        SharedPreferences settings = getSharedPreferences(
                                PREFS_USER_INFO, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(lOGIN_USER_NAME, mUserName);
                        editor.putString(LOGIN_PASSWORD, mPassword);
                        editor.putString(LOGIN_ROLE_ID, mUserType.toString());
                        editor.putBoolean(IS_AUTO_LOGIN, isAutoLogin);
                        editor.putBoolean(IS_REMEMBER_PASSWORD,
                                isRemberPassWord);

                        editor.commit();
                        isLoginSuccss = true;  
                        
                    }
                    
                    
                    
                                       
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
               
                mProgressDialog.dismiss();
                if(isLoginSuccss){
                    Intent loginIntent = new Intent(LoginActivity.this,
                            HomePageActivity.class);
                    startActivity(loginIntent);
                    finish();
                } else {
                    
                    Toast.makeText(LoginActivity.this, responseMessage.message,
                            Toast.LENGTH_LONG).show(); 
                }
                

		} else {
		    mProgressDialog.dismiss();
		    Toast.makeText(LoginActivity.this, responseMessage.message,
                  Toast.LENGTH_LONG).show();
		    }
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;

		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class GetAllShoolTask extends AsyncTask<Void, Void, ResponseMessage> {
		@Override
		protected ResponseMessage doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			ResponseMessage responseMessage = new  ResponseMessage();
			try {
				responseMessage.body = RestClient.getAllSchool();
				responseMessage.praseBody();

			} catch (ConnectTimeoutException stex) {
				stex.printStackTrace();
				responseMessage.message = getString(R.string.request_time_out);
			} catch (SocketTimeoutException stex) {
				stex.printStackTrace();
				responseMessage.message = getString(R.string.server_time_out);
			} catch (HttpHostConnectException hhce) {
				hhce.printStackTrace();
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
			mGetAllShoolTask = null;
			Log.d(TAG, "GetAllShool result " + responseMessage.body);
			
			if(responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS){
				if(responseMessage.total>0){
					parseSchoolInfo(responseMessage.body);
				}else {
					Toast.makeText(LoginActivity.this, R.string.no_data,
							Toast.LENGTH_LONG).show();
				}				
				mProgressDialog.dismiss();
				//showDialog(R.id.choose_school);
			} else {
				mProgressDialog.dismiss();
				Toast.makeText(LoginActivity.this, responseMessage.message,
						Toast.LENGTH_LONG).show();
			}
			
		}

		@Override
		protected void onCancelled() {
			mGetAllShoolTask = null;
			mProgressDialog = null;
		}
	}
}
