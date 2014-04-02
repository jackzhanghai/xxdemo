package com.dingxi.xiaoyuantong;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
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
import android.widget.Toast;

import com.dingxi.xiaoyuantong.model.UserInfo;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;

public class ModifyPassWordActivity extends Activity {

	public static final String TAG = "ModifyPassWordActivity";
	private ModifyPassWordTask mModifyPassWordTask;
	private EditText oldPasswordEdit;
	private EditText newPasswordEdit;
	private EditText newPasswordAgainEdit;
	private ProgressDialog mProgressDialog;
	private static UserInfo curretUserInfo;
	private XiaoYunTongApplication  mXiaoYunTongApplication;
	private Button confirmModifyButton;
	private ImageButton backButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_modify_pass_word);
		mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();

		curretUserInfo = mXiaoYunTongApplication.userInfo;
		
		backButton = (ImageButton) findViewById(R.id.back_button);

		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		oldPasswordEdit = (EditText) findViewById(R.id.edit_old_password);
		newPasswordEdit = (EditText) findViewById(R.id.edit_new_password);
		newPasswordAgainEdit = (EditText) findViewById(R.id.edit_new_password_again);
		confirmModifyButton = (Button) findViewById(R.id.confirm_modify_button);
		confirmModifyButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String oldPassword = oldPasswordEdit.getText().toString();
				String newPassword = newPasswordEdit.getText().toString();
				String newPasswordAgain = newPasswordAgainEdit.getText().toString();
				
				
				boolean isRight = true;
				if(TextUtils.isEmpty(oldPassword)){
					
					Toast.makeText(ModifyPassWordActivity.this, "旧密码不能为空", Toast.LENGTH_LONG).show();
					 isRight = false;
				} else if(TextUtils.isEmpty(newPassword)){
					Toast.makeText(ModifyPassWordActivity.this, "新密码不能为空", Toast.LENGTH_LONG).show();
					 isRight = false;
				} else if(TextUtils.isEmpty(newPasswordAgain)) {
					Toast.makeText(ModifyPassWordActivity.this, "新密码不能为空", Toast.LENGTH_LONG).show();
					 isRight = false;
				} else if(!newPasswordAgain.equals(newPassword)){
					Toast.makeText(ModifyPassWordActivity.this, "两次新密码不一致请重新输入", Toast.LENGTH_LONG).show();
					isRight = false;
				}
				
				if(isRight){
					modifyPassWord(oldPassword,newPassword);
				}
			}
		});
	}

//	public void confirmModifyPassWordClick(View view) {
//		
//		
//	}

	private void modifyPassWord(String oldPassword, String newPassword) {
		
	    mProgressDialog = new ProgressDialog(ModifyPassWordActivity.this);
        mProgressDialog.setMessage(getString(R.string.now_modiy_password));
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (mModifyPassWordTask != null && !mModifyPassWordTask.isCancelled()) {
                    mModifyPassWordTask.cancel(true);
                    // isCancelLogin = true;
                }
            }
        });
        mProgressDialog.show();
        mModifyPassWordTask = new ModifyPassWordTask(oldPassword, newPassword);
        mModifyPassWordTask.execute((Void) null);

	}

	public void cancelModifyPassWordClick(View view) {
	    this.finish();

	}

	public class ModifyPassWordTask extends AsyncTask<Void, Void, String> {
		
		String oldPassWord;
		String newPassWord;
		public ModifyPassWordTask(String oldPassWord,String newPassWord) {
			// TODO Auto-generated constructor stub
			this.oldPassWord = oldPassWord;
			this.newPassWord = newPassWord;
		}
		@Override
		protected String doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			String schoolsInfo = null;
			try {

				schoolsInfo = RestClient.modifyPwd(curretUserInfo.id, curretUserInfo.roleType.toString(), curretUserInfo.ticket, oldPassWord, newPassWord);
			} catch (ConnectTimeoutException stex) {
				schoolsInfo = getString(R.string.request_time_out);
			} catch (SocketTimeoutException stex) {
				schoolsInfo = getString(R.string.server_time_out);
			} catch (HttpHostConnectException hhce) {
				schoolsInfo = getString(R.string.connection_server_error);
			} catch (XmlPullParserException e) {
				schoolsInfo = getString(R.string.connection_error);
				e.printStackTrace();
			} catch (IOException e) {
				schoolsInfo = getString(R.string.connection_error);
				e.printStackTrace();
			}

			// TODO: register the new account here.
			return schoolsInfo;
		}

		@Override
		protected void onPostExecute(String schoolsInfo) {
			mModifyPassWordTask = null;
			Log.d(TAG, "ModifyPassWord result " + schoolsInfo);
			if (!TextUtils.isEmpty(schoolsInfo)) {

				try {
					if (JSONParser.getIntByTag(schoolsInfo,
							ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {
					    mProgressDialog.dismiss();
						    Toast.makeText(ModifyPassWordActivity.this, R.string.modify_password_sucess,
                                    Toast.LENGTH_LONG).show();						
						

					} else {
						mProgressDialog.dismiss();
						String errorMessage = JSONParser.getStringByTag(
								schoolsInfo, ResponseMessage.RESULT_TAG_MESSAGE);
						if (!TextUtils.isEmpty(errorMessage)) {
							Toast.makeText(ModifyPassWordActivity.this, errorMessage,
									Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(ModifyPassWordActivity.this, schoolsInfo,
									Toast.LENGTH_LONG).show();
						}
					}
				} catch (JSONException e) {
					if (mProgressDialog != null) {
						mProgressDialog.dismiss();
					}

					Toast.makeText(ModifyPassWordActivity.this, schoolsInfo,
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			} else {
				mProgressDialog.dismiss();

			}
		}

		

		@Override
		protected void onCancelled() {
			mModifyPassWordTask = null;

		}
	}

}
