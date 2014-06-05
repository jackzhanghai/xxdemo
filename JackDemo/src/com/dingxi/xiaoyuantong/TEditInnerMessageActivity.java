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
import com.dingxi.xiaoyuantong.model.SubjectInfo;
import com.dingxi.xiaoyuantong.model.TeacherInfo;
import com.dingxi.xiaoyuantong.model.UserInfo;
import com.dingxi.xiaoyuantong.model.UserInfo.UserType;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;
import com.dingxi.xiaoyuantong.util.Util;

public class TEditInnerMessageActivity extends Activity implements OnClickListener {

	public static final String TAG = "EditLeaveMessageActivity";
	private ImageButton mBackButton;
	//private TeacherInfo curretUserInfo;
	private UserInfo curretUserInfo;
	private XiaoYunTongApplication mXiaoYunTongApplication;
	private ImageButton selectClassButton;
	private ImageButton selectGradeButton;
	private ImageButton selectStudentButton;
	//private ImageButton selectParentButton;
	
	private TextView classNameText;
	private TextView gradeNameText;
	private TextView studentNameText;
	private EditText contentEditText;
	
	private View selectGradeArea;
	private View selectClassArea;
	
	private Button sendHomeWorkButton;
	private Button editcancelButton;
	private GetAllInfoTask mGetAllInfoTask;
	private SendHomeWorkTask mSendHomeWorkTask;
	private ProgressDialog mProgressDialog;
	
	
	private String mClassId;
	private String mGradeId;
	private String mStudentId;
	private String mChild;
	//private String mSchoolId;
	private String mPrentId;
	private String mTeacherId;
	
	
	private SearchType curretSearchType;
	
	public List<ClassInfo> classInfoList;
	public String[] classNameList;
	
	public ArrayList<GradeInfo> gradeInfoList;
	public String[] gradeNameList;
	
	public List<StudentInfo> studentInfoList;
	public String[] studentNameList;
	
	public List<ParentInfo> parentInfoList;
    public String[] parentNameList;
    
    public List<TeacherInfo> techerInfoList;
    public String[] teacherNameList;
	
    
    public List<ChildInfo> childInfoList;
    public String[] childNameList;
    
	private enum SearchType {
		ClassInfo, GradeInfo, StudentInfo,ChildInfo
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_leave_message);

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
		
		gradeNameText = (TextView) findViewById(R.id.select_grade_text);
        classNameText = (TextView) findViewById(R.id.select_class_text);
        studentNameText = (TextView) findViewById(R.id.select_student_text);

        selectGradeArea = findViewById(R.id.select_grade_area);
        selectClassArea =  findViewById(R.id.select_class_area);
        
        selectGradeButton = (ImageButton) findViewById(R.id.select_grade_button);
        selectGradeButton.setOnClickListener(this);
        selectClassButton = (ImageButton) findViewById(R.id.select_class_button);
        selectClassButton.setOnClickListener(this);
        selectStudentButton = (ImageButton) findViewById(R.id.select_student_button);
        selectStudentButton.setOnClickListener(this);

        
		if (curretUserInfo.roleType == UserType.ROLE_PARENT) {
		    
		    selectGradeArea.setVisibility(View.GONE);
		    selectClassArea.setVisibility(View.GONE);
		    gradeNameText.setText(R.string.please_check_child);
		    //1.选择孩子 2. 选择老师集合。
		    ParentInfo parentInfo = (ParentInfo) curretUserInfo;
            //parentInfo.defalutChild;
            
            
		} else {
		    selectGradeArea.setVisibility(View.VISIBLE);
		    selectClassArea.setVisibility(View.VISIBLE);
		    studentNameText.setText(R.string.select_student);
		    //1.获取年级集合 2. 获取班级集合  ，3.获取学生集合  4. 获取学生家长集合
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
				TEditInnerMessageActivity.this.finish();
			}
		});
		
		sendHomeWorkButton = (Button) findViewById(R.id.edit_ok_button);
		sendHomeWorkButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				if(checkText()){
					if (Util.IsNetworkAvailable(TEditInnerMessageActivity.this)) {
						mProgressDialog = new ProgressDialog(
								TEditInnerMessageActivity.this);
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
						mSendHomeWorkTask = new SendHomeWorkTask("",mGradeId, mClassId, mStudentId, content);
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
                Toast.makeText(TEditInnerMessageActivity.this, R.string.please_input_content,
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(mStudentId)){
                Toast.makeText(TEditInnerMessageActivity.this, R.string.please_check_child,
                        Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(mTeacherId)){
                Toast.makeText(TEditInnerMessageActivity.this, R.string.please_check_teacher,
                        Toast.LENGTH_LONG).show();
            }else {//mPrentId
                isOk = true;
            } 
		    
		} else {
		    
		    if (TextUtils.isEmpty(content)) {
	            Toast.makeText(TEditInnerMessageActivity.this, R.string.please_input_content,
	                    Toast.LENGTH_LONG).show();
	        } else if (TextUtils.isEmpty(mClassId)) {
	            Toast.makeText(TEditInnerMessageActivity.this, R.string.select_class,
	                    Toast.LENGTH_LONG).show();
	        } else if (TextUtils.isEmpty(mGradeId)) {
	            Toast.makeText(TEditInnerMessageActivity.this, R.string.select_grade,
	                    Toast.LENGTH_LONG).show();
	        } else if (TextUtils.isEmpty(mStudentId)){
	            Toast.makeText(TEditInnerMessageActivity.this, R.string.select_student,
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

				if (curretSearchType == SearchType.GradeInfo) {
					responseMessage.body = RestClient.getMyGrade(
							curretUserInfo.id,
							curretUserInfo.roleType.toString());
				} else if (curretSearchType == SearchType.ClassInfo) {
					responseMessage.body = RestClient.getMyClassroomByGrade(
							curretUserInfo.id, curretUserInfo.roleType.toString(), mGradeId);
				} else if (curretSearchType == SearchType.StudentInfo) {
					responseMessage.body = RestClient.getStudentByClassroom(
					        mClassId);
				}  else if (curretSearchType == SearchType.ChildInfo) {
                    responseMessage.body = RestClient.getAllChilds(curretUserInfo.id, curretUserInfo.ticket);        
                }

				 responseMessage.praseBody(TEditInnerMessageActivity.this);

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

			    
				if (curretSearchType == SearchType.GradeInfo) {
					try {
						parseGradeInfo(responseMessage.body);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} 
				
				else if (curretSearchType == SearchType.ClassInfo) {
					try {
						parseClassInfo(responseMessage.body);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (curretSearchType == SearchType.StudentInfo) {
					try {
						parseStudentInfo(responseMessage.body);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if (curretSearchType == SearchType.ChildInfo) {
                    try {
                        parseChildInfo(responseMessage.body);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
				
				
				mProgressDialog.dismiss();
				if (curretSearchType == SearchType.GradeInfo) {
				    if(gradeNameList!=null && gradeNameList.length>0){
				        showDialog(R.id.select_grade_button);
				    }else {
				        Toast.makeText(TEditInnerMessageActivity.this,
		                        R.string.no_grade, Toast.LENGTH_LONG).show(); 
				    }
					
				} else if (curretSearchType == SearchType.ClassInfo) {
				    if(classNameList!=null && classNameList.length>0){
				        showDialog(R.id.select_class_button);
                    }else {
                        Toast.makeText(TEditInnerMessageActivity.this,
                                R.string.no_class, Toast.LENGTH_LONG).show(); 
                    }
					
				} else if (curretSearchType == SearchType.StudentInfo) {
					
					if(studentNameList!=null && studentNameList.length>0){
					    showDialog(R.id.select_student_button);
                    }else {
                        Toast.makeText(TEditInnerMessageActivity.this,
                                R.string.no_subject, Toast.LENGTH_LONG).show(); 
                    }
				}else if (curretSearchType == SearchType.ChildInfo) {
                    
                    if(childNameList!=null && childNameList.length>0){
                        showDialog(R.id.select_student_button);
                    }else {
                        Toast.makeText(TEditInnerMessageActivity.this,
                                R.string.no_subject, Toast.LENGTH_LONG).show(); 
                    }
                }
				
				/*
				*/
			} else {
				mProgressDialog.dismiss();
				Toast.makeText(TEditInnerMessageActivity.this,
						responseMessage.message, Toast.LENGTH_LONG).show();
			}
			

		}

		private void parseGradeInfo(String searchTypeReslut)
				throws JSONException {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(searchTypeReslut)) {
				Log.d(TAG, "result schoolsInfo " + searchTypeReslut);

				if (JSONParser.getIntByTag(searchTypeReslut,
						ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

					gradeInfoList = JSONParser
							.toParserGradeInfoList(searchTypeReslut);
				} else {
					JSONParser.getStringByTag(searchTypeReslut,
							ResponseMessage.RESULT_TAG_MESSAGE);
				}

			}
			if (gradeInfoList != null && gradeInfoList.size() > 0) {
				gradeNameList = new String[gradeInfoList.size()];

				for (int i = 0; i < gradeInfoList.size(); i++) {
					gradeNameList[i] = gradeInfoList.get(i).name;

				}
			}
		}

		private void parseClassInfo(String reslut) throws JSONException {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(reslut)) {
				Log.d(TAG, "result schoolsInfo " + reslut);

				if (JSONParser.getIntByTag(reslut,
						ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

					classInfoList = JSONParser.toParserCalssInfoList(reslut);
				} else {
					JSONParser.getStringByTag(reslut,
							ResponseMessage.RESULT_TAG_MESSAGE);
				}

			}
			if (classInfoList != null && classInfoList.size() > 0) {
				classNameList = new String[classInfoList.size()];

				for (int i = 0; i < classInfoList.size(); i++) {
					classNameList[i] = classInfoList.get(i).name;
				}
			}
		}

		private void parseStudentInfo(String reslut) throws JSONException {
			// TODO Auto-generated method stub
			if (!TextUtils.isEmpty(reslut)) {
				Log.d(TAG, "result schoolsInfo " + reslut);

				if (JSONParser.getIntByTag(reslut,
						ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

					studentInfoList = JSONParser.toParserStudentInfoList(reslut);
							//.toParserSubjectInfoList(reslut);
				} else {
					JSONParser.getStringByTag(reslut,
							ResponseMessage.RESULT_TAG_MESSAGE);
				}

			}
			if (studentInfoList != null && studentInfoList.size() > 0) {
				studentNameList = new String[studentInfoList.size()];

				for (int i = 0; i < studentInfoList.size(); i++) {
				    studentNameList[i] = studentInfoList.get(i).stuName;
				}
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

		private void parseParentInfo(String reslut) throws JSONException {
            // TODO Auto-generated method stub
            if (!TextUtils.isEmpty(reslut)) {
                Log.d(TAG, "result schoolsInfo " + reslut);

                if (JSONParser.getIntByTag(reslut,
                        ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

                    parentInfoList = JSONParser.parseParentInfo(reslut);
                } else {
                    JSONParser.getStringByTag(reslut,
                            ResponseMessage.RESULT_TAG_MESSAGE);
                }

            }
            if (parentInfoList != null && parentInfoList.size() > 0) {
                parentNameList = new String[parentInfoList.size()];

                for (int i = 0; i < parentInfoList.size(); i++) {
                    parentNameList[i] = parentInfoList.get(i).name;
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
				
				
				
				responseMessage.body = RestClient.getParentsByStudentId(mStudentId);
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
		           
					responseMessage.body =  RestClient.addInnerMessage(curretUserInfo.id, info.toString(), content, curretUserInfo.ticket);
					//responseMessage.body =  RestClient.addLeaveMessage(curretUserInfo.id, info.toString(), content, curretUserInfo.ticket, curretUserInfo.roleType.toString(), mStudentId);
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
			    Intent backIntent = new Intent(TEditInnerMessageActivity.this,LeaveMessageActivity.class);
			    startActivity(backIntent);
			    TEditInnerMessageActivity.this.finish();
							
			} else {
				mProgressDialog.dismiss();				
			}
			
			Toast.makeText(TEditInnerMessageActivity.this,
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
		if (Util.IsNetworkAvailable(TEditInnerMessageActivity.this)) {
			mProgressDialog = new ProgressDialog(TEditInnerMessageActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			int errorCode = -1;
			switch (v.getId()) {
			
			case R.id.select_grade_button:
					curretSearchType = SearchType.GradeInfo;
					mProgressDialog
							.setMessage(getString(R.string.now_geting_gradeinfo));
				break;
			case R.id.select_class_button:

                if (!TextUtils.isEmpty(mGradeId)) {
                    curretSearchType = SearchType.ClassInfo;
                    mProgressDialog
                            .setMessage(getString(R.string.now_geting_classinfo));
                } else {
                    errorCode = R.string.select_grade;
                }

                break;
			case R.id.select_student_button:
			    if(curretUserInfo.roleType == UserType.ROLE_PARENT){
	                    curretSearchType = SearchType.ChildInfo;
	                    mProgressDialog
	                            .setMessage(getString(R.string.now_geting_childinfo));           
			    } else {
			        if (!TextUtils.isEmpty(mClassId)) {
	                    curretSearchType = SearchType.StudentInfo;
	                    mProgressDialog
	                            .setMessage(getString(R.string.now_geting_studentinfo));
	                } else {
	                    errorCode = R.string.select_class;
	                }
			        
			    }
				

				break;
			
			default:
				break;
			}

			if (errorCode != -1) {
				Toast.makeText(TEditInnerMessageActivity.this, errorCode,
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
		case R.id.select_class_button:

			if (classInfoList != null && classInfoList.size() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TEditInnerMessageActivity.this);
				builder.setTitle(R.string.select_class).setItems(
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
						TEditInnerMessageActivity.this);
				builder.setTitle(R.string.select_grade).setItems(
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
		case R.id.select_student_button:

			if (studentInfoList != null && studentInfoList.size() > 0) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						TEditInnerMessageActivity.this);
				builder.setTitle(R.string.select_student).setItems(
				        studentNameList, new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								Log.d(TAG, "which " + which);
								mStudentId = studentInfoList.get(which).id;
								studentNameText.setText(studentInfoList
										.get(which).stuName);
								Log.d(TAG, "mStudentId " + mStudentId);
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
