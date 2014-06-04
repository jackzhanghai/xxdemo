package com.dingxi.xiaoyuantong;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

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

import com.dingxi.xiaoyuantong.model.ChildInfo;
import com.dingxi.xiaoyuantong.model.ClassInfo;
import com.dingxi.xiaoyuantong.model.GradeInfo;
import com.dingxi.xiaoyuantong.model.ParentInfo;
import com.dingxi.xiaoyuantong.model.StudentInfo;
import com.dingxi.xiaoyuantong.model.TeacherInfo;
import com.dingxi.xiaoyuantong.model.UserInfo;
import com.dingxi.xiaoyuantong.model.UserInfo.UserType;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;
import com.dingxi.xiaoyuantong.util.Util;

public class ParentEditLeaveMessageActivity extends Activity implements OnClickListener {

	public static final String TAG = "ParentEditLeaveMessageActivity";
	private ImageButton mBackButton;
	private UserInfo curretUserInfo;
	private XiaoYunTongApplication mXiaoYunTongApplication;
	private ImageButton selectChildButton;
	private ImageButton selectTeacherButton;
	
	private TextView childNameText;
	private TextView teacherNameText;
	
	private EditText contentEditText;
	
	
	private Button sendHomeWorkButton;
	private Button editcancelButton;
	private GetAllInfoTask mGetAllInfoTask;
	private SendHomeWorkTask mSendHomeWorkTask;
	private ProgressDialog mProgressDialog;
	
	
	//private String mStudentId;
	private String mChild;
	//private String mSchoolId;
	//private String mPrentId;
	private String mTeacherId;
	
	
	private SearchType curretSearchType;
	
    
    public List<TeacherInfo> techerInfoList;
    public String[] teacherNameList;
	
    
    public List<ChildInfo> childInfoList;
    public String[] childNameList;
    
	private enum SearchType {
		ChildInfo,TeacherInfo
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_parent_edit_leave_message);

		mBackButton = (ImageButton) findViewById(R.id.back_button);
		mBackButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();
		curretUserInfo = (TeacherInfo) mXiaoYunTongApplication.userInfo;
		
		childNameText = (TextView) findViewById(R.id.select_child_text);
		teacherNameText = (TextView) findViewById(R.id.select_teacher_text);
		


        
        selectChildButton = (ImageButton) findViewById(R.id.select_child_button);
        selectChildButton.setOnClickListener(this);
        selectTeacherButton = (ImageButton) findViewById(R.id.select_teacher_button);
        selectTeacherButton.setOnClickListener(this);
       


        
		if (curretUserInfo.roleType == UserType.ROLE_PARENT) {

		    //1.选择孩子 2. 选择老师集合。
		    ParentInfo parentInfo = (ParentInfo) curretUserInfo;
            //parentInfo.defalutChild;
            
            
		} else {

		    TeacherInfo teacherInfo = (TeacherInfo) curretUserInfo;
		    //teacherInfo.defalutClassId;
		    
		    
		}
		//mSchoolId = curretUserInfo.defalutSchoolId;

		contentEditText = (EditText) findViewById(R.id.edit_homework_content);
		editcancelButton = (Button) findViewById(R.id.edit_cancel_button);
		editcancelButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ParentEditLeaveMessageActivity.this.finish();
			}
		});
		
		sendHomeWorkButton = (Button) findViewById(R.id.edit_ok_button);
		sendHomeWorkButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(checkText()){
					if (Util.IsNetworkAvailable(ParentEditLeaveMessageActivity.this)) {
						mProgressDialog = new ProgressDialog(
								ParentEditLeaveMessageActivity.this);
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
						mSendHomeWorkTask = new SendHomeWorkTask("","", "", "", content);
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

		
		if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
		    if (TextUtils.isEmpty(content)) {
                Toast.makeText(ParentEditLeaveMessageActivity.this, R.string.please_input_content,
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(mChild)){
                Toast.makeText(ParentEditLeaveMessageActivity.this, R.string.please_check_child,
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(mTeacherId)){
                Toast.makeText(ParentEditLeaveMessageActivity.this, R.string.please_check_teacher,
                        Toast.LENGTH_LONG).show();
            }else {//mPrentId
                isOk = true;
            } 
		    
		} else {
		    
		    if (TextUtils.isEmpty(content)) {
	            Toast.makeText(ParentEditLeaveMessageActivity.this, R.string.please_input_content,
	                    Toast.LENGTH_LONG).show();
	        }else if (TextUtils.isEmpty(mChild)) {
	            Toast.makeText(ParentEditLeaveMessageActivity.this, R.string.please_check_child,
	                    Toast.LENGTH_LONG).show();
	        } else if (TextUtils.isEmpty(mTeacherId)){
	            Toast.makeText(ParentEditLeaveMessageActivity.this, R.string.please_check_teacher,
	                    Toast.LENGTH_LONG).show();
	        } else {//mPrentId
	            isOk = true;
	        } 
		    
		}
		
		
				
		return isOk;

	}

	public class GetAllInfoTask extends AsyncTask<Void, Void, ResponseMessage> {

		@Override
		protected ResponseMessage doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			ResponseMessage responseMessage = new ResponseMessage();
			try {

				if (curretSearchType == SearchType.TeacherInfo) {
				    responseMessage.body = RestClient.getTeacherByStudentId(mChild, "0");
	
				}else if (curretSearchType == SearchType.ChildInfo) {
                    responseMessage.body = RestClient.getAllChilds(curretUserInfo.id, curretUserInfo.ticket);        
                }

				 responseMessage.praseBody(ParentEditLeaveMessageActivity.this);

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
		    mGetAllInfoTask = null;

			Log.d(TAG, "responseMessage.code " + responseMessage.code);
			if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {

			    
				if (curretSearchType == SearchType.TeacherInfo) {
					try {
						parseTeacherInfo(responseMessage.body);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (curretSearchType == SearchType.ChildInfo) {
                    try {
                        parseChildInfo(responseMessage.body);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
				
				
				mProgressDialog.dismiss();
				if (curretSearchType == SearchType.TeacherInfo) {
	
				    
				    
				    if(teacherNameList!=null && teacherNameList.length>0){
				        showDialog(R.id.select_teacher_button);
				    }else {
				        Toast.makeText(ParentEditLeaveMessageActivity.this,
		                        R.string.no_grade, Toast.LENGTH_LONG).show(); 
				    }
					
				} else if (curretSearchType == SearchType.ChildInfo) {
                    
                    if(childNameList!=null && childNameList.length>0){
                        showDialog(R.id.select_student_button);
                    }else {
                        Toast.makeText(ParentEditLeaveMessageActivity.this,
                                R.string.no_subject, Toast.LENGTH_LONG).show(); 
                    }
                }
				
				/*
				*/
			} else {
				mProgressDialog.dismiss();
				Toast.makeText(ParentEditLeaveMessageActivity.this,
						responseMessage.message, Toast.LENGTH_LONG).show();
			}
			

		}

		
		private void parseTeacherInfo(String reslut) throws JSONException {
            // TODO Auto-generated method stub
            if (!TextUtils.isEmpty(reslut)) {
                Log.d(TAG, "result schoolsInfo " + reslut);

                if (JSONParser.getIntByTag(reslut,
                        ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

                    techerInfoList = JSONParser.parseTeacherInfo(reslut);
                } else {
                    JSONParser.getStringByTag(reslut,
                            ResponseMessage.RESULT_TAG_MESSAGE);
                }

            }
            if (techerInfoList != null && techerInfoList.size() > 0) {
                teacherNameList = new String[techerInfoList.size()];

                for (int i = 0; i < techerInfoList.size(); i++) {
                    teacherNameList[i] = techerInfoList.get(i).name;
                }
            }
        }
		
		private void parseChildInfo(String reslut) throws JSONException {
		    
		    if (!TextUtils.isEmpty(reslut)) {
                Log.d(TAG, "result schoolsInfo " + reslut);

                if (JSONParser.getIntByTag(reslut,
                        ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

                    childInfoList = JSONParser.parseChildInfo(reslut);
                } else {
                    JSONParser.getStringByTag(reslut,
                            ResponseMessage.RESULT_TAG_MESSAGE);
                }

            }
            if (childInfoList != null && childInfoList.size() > 0) {
                childNameList = new String[childInfoList.size()];

                for (int i = 0; i < childInfoList.size(); i++) {
                    childNameList[i] = childInfoList.get(i).name;
                }
            }
		}
		
		
		@Override
		protected void onCancelled() {
		    mGetAllInfoTask = null;

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

		
			ResponseMessage responseMessage = new ResponseMessage();
			try {    
				
				
				
				//responseMessage.body = RestClient.getParentsByStudentId(mStudentId);
				responseMessage.body = RestClient.getTeacherByStudentId(mChild, "0");
				responseMessage.praseBody();
				if(responseMessage.datas !=null){
					ArrayList<ParentInfo> parentInfos = JSONParser.parseParentInfo(responseMessage.datas);
					
					StringBuilder info = new StringBuilder();
					int i = 0;
					for (ParentInfo parentInfo : parentInfos) {
						if (i == parentInfos.size()) {
							info.append(parentInfo.id);
						} else {					
							info.append(parentInfo.id);
			                info.append(",");	
						}
						 
					}
					
					Log.i(TAG, "info"+ info);
		           
					responseMessage.body =  RestClient.addLeaveMessage(curretUserInfo.id, info.toString(), content, curretUserInfo.ticket, curretUserInfo.roleType.toString(), mChild);
					responseMessage.praseBody();
				} else {
					
					responseMessage.message = getString(R.string.no_parents);
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
			    Intent backIntent = new Intent(ParentEditLeaveMessageActivity.this,LeaveMessageActivity.class);
			    startActivity(backIntent);
			    ParentEditLeaveMessageActivity.this.finish();
							
			} else {
				mProgressDialog.dismiss();				
			}
			
			Toast.makeText(ParentEditLeaveMessageActivity.this,
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
		if (Util.IsNetworkAvailable(ParentEditLeaveMessageActivity.this)) {
			mProgressDialog = new ProgressDialog(ParentEditLeaveMessageActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			int errorCode = -1;
			switch (v.getId()) {
			
			case R.id.select_teacher_button:
					curretSearchType = SearchType.TeacherInfo;
					mProgressDialog
							.setMessage(getString(R.string.now_geting_techerinfo));
				break;
			case R.id.select_child_button:
			
	                    curretSearchType = SearchType.ChildInfo;
	                    mProgressDialog
	                            .setMessage(getString(R.string.now_geting_childinfo));           

				break;
			
			default:
				break;
			}

			if (errorCode != -1) {
				Toast.makeText(ParentEditLeaveMessageActivity.this, errorCode,
						Toast.LENGTH_LONG).show();
				mProgressDialog = null;
			} else {
				mProgressDialog.setOnCancelListener(new OnCancelListener() {

					@Override
					public void onCancel(DialogInterface dialog) {
						// TODO Auto-generated method stub
						if (mGetAllInfoTask != null
								&& !mGetAllInfoTask.isCancelled()) {
						    mGetAllInfoTask.cancel(true);
							// isCancelLogin = true;
						}
					}
				});
				mProgressDialog.show();
				mGetAllInfoTask = new GetAllInfoTask();
				mGetAllInfoTask.execute((Void) null);
			}
		}

	}

	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		// TODO Auto-generated method stub
		switch (id) {
		case R.id.select_teacher_button:

			if (techerInfoList != null && techerInfoList.size() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ParentEditLeaveMessageActivity.this);
				builder.setTitle(R.string.select_class).setItems(//techerInfoList
				        teacherNameList, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Log.d(TAG, "which " + which);
								mTeacherId = techerInfoList.get(which).id;
								teacherNameText.setText(techerInfoList.get(which).name);
								Log.d(TAG, "mClassId " + mTeacherId);
								// The 'which' argument contains the index
								// position
								// of the selected item
							}
						});
				return builder.create();
			}
			break;
		case R.id.select_child_button:

			if (childInfoList != null && childInfoList.size() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						ParentEditLeaveMessageActivity.this);
				builder.setTitle(R.string.select_student).setItems(
				        childNameList, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Log.d(TAG, "which " + which);
								mChild = childInfoList.get(which).id;
								childNameText.setText(childInfoList
										.get(which).name);
								Log.d(TAG, "mStudentId " + mChild);
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
