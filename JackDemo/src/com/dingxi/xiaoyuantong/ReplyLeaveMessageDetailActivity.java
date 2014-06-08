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
import android.content.Intent;
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

import com.dingxi.xiaoyuantong.model.UserInfo;
import com.dingxi.xiaoyuantong.model.LeaveMessage.LeaveMessageEntry;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;
import com.dingxi.xiaoyuantong.util.Util;

public class ReplyLeaveMessageDetailActivity extends Activity {

	public static final String TAG = "ReplyLeaveMessageDetailActivity";
	private View parentHeader;
	private ImageButton mBackButton;
	private XiaoYunTongApplication mXiaoYunTongApplication;
	
	private Button confirmButton;
	private ProgressDialog mProgressDialog;
	public UserInfo userInfo;
	private EditText contentEditText;
	private SendHomeWorkTask mSendHomeWorkTask;
	String messagekID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reply_leave_message_detail);

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
		userInfo = mXiaoYunTongApplication.userInfo;
		contentEditText = (EditText) findViewById(R.id.edit_reply_content);

		 messagekID = getIntent().getStringExtra(
				LeaveMessageEntry.COLUMN_NAME_ENTRY_ID);
		//String optTime = getIntent().getStringExtra(LeaveMessageEntry.COLUMN_NAME_DATE);
		//String content = getIntent().getStringExtra(LeaveMessageEntry.COLUMN_NAME_CONTENT);
		confirmButton = (Button) findViewById(R.id.confirm_reply_button);
		confirmButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (checkText()) {
					if (Util.IsNetworkAvailable(ReplyLeaveMessageDetailActivity.this)) {
						mProgressDialog = new ProgressDialog(
								ReplyLeaveMessageDetailActivity.this);
						mProgressDialog
								.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						mProgressDialog
								.setMessage(getString(R.string.now_reply_message));
						mProgressDialog
								.setOnCancelListener(new OnCancelListener() {

									@Override
									public void onCancel(DialogInterface dialog) {
										// TODO Auto-generated method stub
										if (mSendHomeWorkTask != null
												&& !mSendHomeWorkTask
														.isCancelled()) {
											mSendHomeWorkTask.cancel(true);
											// isCancelLogin = true;
										}
									}
								});
						String content = contentEditText.getText().toString();
						mProgressDialog.show();
						mSendHomeWorkTask = new SendHomeWorkTask(
								messagekID,content);
						mSendHomeWorkTask.execute((Void) null);
					}
				}
			}
		});

	}

	protected boolean checkText() {
		// TODO Auto-generated method stub
		boolean isOk = false;
		// String title = titleEditText.getText().toString();
		String content = contentEditText.getText().toString();

		if (TextUtils.isEmpty(content)) {
			Toast.makeText(ReplyLeaveMessageDetailActivity.this,
					R.string.please_input_content, Toast.LENGTH_LONG).show();
		} else {
			isOk = true;
		}

		return isOk;

	}

	public class SendHomeWorkTask extends
			AsyncTask<Void, Void, ResponseMessage> {

		String messagekID, content;

		public SendHomeWorkTask(String messagekID,String content) {
			this.messagekID = messagekID;
			this.content = content;
		}

		@Override
		protected ResponseMessage doInBackground(Void... params) {

			ResponseMessage responseMessage = new ResponseMessage();
			try {

				responseMessage.body = RestClient.replyLeaveMessage(messagekID, userInfo.id, content, userInfo.ticket, userInfo.roleType.toString());
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
			mSendHomeWorkTask = null;

			Log.d(TAG, "responseMessage.code " + responseMessage.code);
			if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {

				mProgressDialog.dismiss();
				Intent backIntent = new Intent(ReplyLeaveMessageDetailActivity.this,
						LeaveMessageDetailActivity.class);
				// startActivity(backIntent);
				ReplyLeaveMessageDetailActivity.this.finish();
				responseMessage.message = getString(R.string.reply_ok);

			} else {
				mProgressDialog.dismiss();
			}

			Toast.makeText(ReplyLeaveMessageDetailActivity.this, responseMessage.message,
					Toast.LENGTH_LONG).show();

		}

		@Override
		protected void onCancelled() {
			mSendHomeWorkTask = null;

		}
	}

}
