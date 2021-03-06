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

import com.dingxi.xiaoyuantong.model.ClassInfo;
import com.dingxi.xiaoyuantong.model.GradeInfo;
import com.dingxi.xiaoyuantong.model.TeacherInfo;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;
import com.dingxi.xiaoyuantong.util.Util;

public class EditCampusNoticeActivity extends Activity implements OnClickListener {

    public static final String TAG = "EditCampusNoticeActivity";
    private ImageButton mBackButton;
    private TeacherInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private ImageButton selectClassButton;
    private ImageButton selectGradeButton;
    private ImageButton selectSendTypeButton;
    private TextView classNameText;
    private TextView gradeNameText;
    private TextView sendTypeText;
    private EditText contentEditText;
    private Button sendHomeWorkButton;
    private GetTypeTask mGetTypeTask;
    private AddCampusNoticeTask mAddCampusNoticeTask;
    private ProgressDialog mProgressDialog;
    private String mClassId;
    private String mGradeId;
    private int mSendType;
    private SearchType curretSearchType;
    public List<ClassInfo> classInfoList;
    public String[] classNameList;
    public ArrayList<GradeInfo> gradeInfoList;
    public String[] gradeNameList;
    private Button editcancelButton;

    private enum SearchType {
        ClassInfo, GradeInfo
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_campus_notice);

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
        sendTypeText = (TextView) findViewById(R.id.select_sendtype_text);
        

        selectClassButton = (ImageButton) findViewById(R.id.select_class_button);
        selectClassButton.setOnClickListener(this);
        selectGradeButton = (ImageButton) findViewById(R.id.select_grade_button);
        selectGradeButton.setOnClickListener(this);
        selectSendTypeButton = (ImageButton) findViewById(R.id.select_sendtype_button);
        selectSendTypeButton.setOnClickListener(this);

        contentEditText = (EditText) findViewById(R.id.edit_campus_notice_content);
        editcancelButton = (Button) findViewById(R.id.edit_cancel_button);
        editcancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                EditCampusNoticeActivity.this.finish();
            }
        });
        sendHomeWorkButton = (Button) findViewById(R.id.edit_ok_button);
        sendHomeWorkButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                if (checkText()) {
                    if (Util.IsNetworkAvailable(EditCampusNoticeActivity.this)) {
                        mProgressDialog = new ProgressDialog(EditCampusNoticeActivity.this);
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.setMessage(getString(R.string.now_adding_notice));
                        mProgressDialog.setOnCancelListener(new OnCancelListener() {

                            @Override
                            public void onCancel(DialogInterface dialog) {
                                // TODO Auto-generated method stub
                                if (mAddCampusNoticeTask != null
                                        && !mAddCampusNoticeTask.isCancelled()) {
                                    mAddCampusNoticeTask.cancel(true);
                                    // isCancelLogin = true;
                                }
                            }
                        });
                        String content = contentEditText.getText().toString();

                        mProgressDialog.show();
                        mAddCampusNoticeTask = new AddCampusNoticeTask(mSendType,
                                curretUserInfo.defalutSchoolId, mGradeId, mClassId, content);
                        mAddCampusNoticeTask.execute((Void) null);
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
       
        
        if(mSendType == 1){            
            if (TextUtils.isEmpty(mClassId)) {
                Toast.makeText(EditCampusNoticeActivity.this, R.string.select_class, Toast.LENGTH_LONG)
                        .show();
            } else if (TextUtils.isEmpty(mGradeId)) {
                Toast.makeText(EditCampusNoticeActivity.this, R.string.select_grade, Toast.LENGTH_LONG)
                        .show();
            }  else {
                if (TextUtils.isEmpty(content)) {
                    Toast.makeText(EditCampusNoticeActivity.this, R.string.please_input_content,
                            Toast.LENGTH_LONG).show();
                } else{
                    isOk = true; 
                }
                
            }
            
            
        }  else {
            
            if (TextUtils.isEmpty(content)) {
                Toast.makeText(EditCampusNoticeActivity.this, R.string.please_input_content,
                        Toast.LENGTH_LONG).show();
            } else {
                isOk = true; 
            }
        }
        
         

        return isOk;

    }

    public class GetTypeTask extends AsyncTask<Void, Void, ResponseMessage> {

        @Override
        protected ResponseMessage doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            ResponseMessage responseMessage = new ResponseMessage();
            try {

                if (curretSearchType == SearchType.GradeInfo) {
                    responseMessage.body = RestClient.getGradeInfos(curretUserInfo.id,
                            curretUserInfo.ticket, curretUserInfo.defalutSchoolId);
                } else if (curretSearchType == SearchType.ClassInfo) {
                    responseMessage.body = RestClient.getClassInfos(curretUserInfo.id,
                            curretUserInfo.ticket, mGradeId);
                }

                if (TextUtils.isEmpty(responseMessage.body)) {

                } else {
                    responseMessage.praseBody();

                }

            } catch (ConnectTimeoutException stex) {
                responseMessage.code = -1;
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
            mGetTypeTask = null;

            Log.d(TAG, "responseMessage.code " + responseMessage.code);
            if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {

                if (curretSearchType == SearchType.GradeInfo) {
                    try {
                        parseGradeInfo(responseMessage.body);

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else if (curretSearchType == SearchType.ClassInfo) {
                    try {
                        parseClassInfo(responseMessage.body);
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                mProgressDialog.dismiss();
                if (curretSearchType == SearchType.GradeInfo) {
                    if (gradeNameList != null && gradeNameList.length > 0) {
                        showDialog(R.id.select_grade_button);
                    } else {
                        Toast.makeText(EditCampusNoticeActivity.this, R.string.no_grade,
                                Toast.LENGTH_LONG).show();
                    }

                } else if (curretSearchType == SearchType.ClassInfo) {
                    if (classNameList != null && classNameList.length > 0) {
                        showDialog(R.id.select_class_button);
                    } else {
                        Toast.makeText(EditCampusNoticeActivity.this, R.string.no_class,
                                Toast.LENGTH_LONG).show();
                    }

                }
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(EditCampusNoticeActivity.this, responseMessage.message,
                        Toast.LENGTH_LONG).show();
            }

        }

        private void parseGradeInfo(String searchTypeReslut) throws JSONException {
            // TODO Auto-generated method stub
            if (!TextUtils.isEmpty(searchTypeReslut)) {
                Log.d(TAG, "result schoolsInfo " + searchTypeReslut);

                if (JSONParser.getIntByTag(searchTypeReslut, ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

                    gradeInfoList = JSONParser.toParserGradeInfoList(searchTypeReslut);
                } else {
                    JSONParser.getStringByTag(searchTypeReslut, ResponseMessage.RESULT_TAG_MESSAGE);
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

                if (JSONParser.getIntByTag(reslut, ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

                    classInfoList = JSONParser.toParserCalssInfoList(reslut);
                } else {
                    JSONParser.getStringByTag(reslut, ResponseMessage.RESULT_TAG_MESSAGE);
                }

            }
            if (classInfoList != null && classInfoList.size() > 0) {
                classNameList = new String[classInfoList.size()];

                for (int i = 0; i < classInfoList.size(); i++) {
                    classNameList[i] = classInfoList.get(i).name;
                }
            }
        }

        @Override
        protected void onCancelled() {
            mGetTypeTask = null;

        }
    }

    public class AddCampusNoticeTask extends AsyncTask<Void, Void, ResponseMessage> {

        String fkSchoolId, fkGradeId, fkClassId, content;
        Integer sendType;

        public AddCampusNoticeTask(int mSendType, String fkSchoolId, String fkGradeId,
                String fkClassId, String content) {
            this.sendType = mSendType;
            this.fkClassId = fkClassId;
            this.fkGradeId = fkGradeId;
            this.content = content;
            this.fkSchoolId = fkSchoolId;
        }

        @Override
        protected ResponseMessage doInBackground(Void... params) {

            StringBuilder info = new StringBuilder();
            info.append("{");
            info.append("\"sendType\":");
            info.append("\"" + sendType + "\"");
            info.append(",");

            info.append("\"fkSchoolId\":");
            info.append("\"" + fkSchoolId + "\"");
            info.append(",");
            if(sendType == 1){
                info.append("\"fkGradeId\":");
                info.append("\"" + fkGradeId + "\"");
                info.append(",");

                info.append("\"fkClassId\":");
                info.append("\"" + fkClassId + "\"");
                info.append(","); 
            }
            
            info.append("\"content\":");
            info.append("\"" + content + "\"");
            info.append("}");

            Log.d(TAG, "info.toString() " + info.toString());

            ResponseMessage responseMessage = new ResponseMessage();
            try {

                responseMessage.body = RestClient.addMessageInfos(curretUserInfo.id,
                        curretUserInfo.ticket, info.toString());
                Log.d(TAG, "AddCampusNoticeTask response " + responseMessage.body);

                if (TextUtils.isEmpty(responseMessage.body)) {

                } else {
                    responseMessage.praseBody();
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
            mAddCampusNoticeTask = null;

            Log.d(TAG, "responseMessage.code " + responseMessage.code);
            if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                mProgressDialog.dismiss();
                Intent backIntent = new Intent(EditCampusNoticeActivity.this,
                        CampusNoticeActivity.class);
                startActivity(backIntent);
                EditCampusNoticeActivity.this.finish();
            } else {
                mProgressDialog.dismiss();
            }

            Toast.makeText(EditCampusNoticeActivity.this, responseMessage.message,
                    Toast.LENGTH_LONG).show();

        }

        @Override
        protected void onCancelled() {
            mAddCampusNoticeTask = null;

        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (Util.IsNetworkAvailable(EditCampusNoticeActivity.this)) {
            mProgressDialog = new ProgressDialog(EditCampusNoticeActivity.this);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            Log.i(TAG, "v.getId() " + v.getId());
            switch (v.getId()) {
            case R.id.select_sendtype_button:
                mProgressDialog = null;
                showDialog(R.id.select_sendtype_button);
                break;
            case R.id.select_class_button:

                if (!TextUtils.isEmpty(mGradeId)) {
                    curretSearchType = SearchType.ClassInfo;
                    mProgressDialog.setMessage(getString(R.string.now_geting_classinfo));
                    startGetType();
                } else {
                    Toast.makeText(EditCampusNoticeActivity.this, R.string.select_grade,
                            Toast.LENGTH_LONG).show();
                    mProgressDialog = null;
                }

                break;
            case R.id.select_grade_button:
                if (!TextUtils.isEmpty(curretUserInfo.defalutSchoolId)) {
                    curretSearchType = SearchType.GradeInfo;
                    mProgressDialog.setMessage(getString(R.string.now_geting_classinfo));
                    startGetType();
                } else {
                    Toast.makeText(EditCampusNoticeActivity.this, R.string.select_school,
                            Toast.LENGTH_LONG).show();
                    mProgressDialog = null;
                }

                break;

            default:
                break;
            }

        }
    }

    private void startGetType() {
        mProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (mGetTypeTask != null && !mGetTypeTask.isCancelled()) {
                    mGetTypeTask.cancel(true);
                    // isCancelLogin = true;
                }
            }
        });
        mProgressDialog.show();
        mGetTypeTask = new GetTypeTask();
        mGetTypeTask.execute((Void) null);
    }

    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        // TODO Auto-generated method stub
        switch (id) {
        case R.id.select_class_button:

            if (classInfoList != null && classInfoList.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EditCampusNoticeActivity.this);
                builder.setTitle(R.string.select_class).setItems(classNameList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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
                AlertDialog.Builder builder = new AlertDialog.Builder(EditCampusNoticeActivity.this);
                builder.setTitle(R.string.select_grade).setItems(gradeNameList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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
        case R.id.select_sendtype_button: {
            AlertDialog.Builder builder = new AlertDialog.Builder(EditCampusNoticeActivity.this);
            builder.setTitle(R.string.select_send_type).setItems(R.array.array_sendtype,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                mSendType = 0;
                                sendTypeText.setText(R.string.all_parent);
                                findViewById(R.id.select_class_layout).setVisibility(View.GONE);
                                findViewById(R.id.select_grade_layout).setVisibility(View.GONE);
                                
                            } else if (which == 1) {
                                mSendType = 1;
                                sendTypeText.setText(R.string.select_parent);
                                findViewById(R.id.select_class_layout).setVisibility(View.VISIBLE);
                                findViewById(R.id.select_grade_layout).setVisibility(View.VISIBLE);
                               
                            }
                            // The 'which' argument contains the index position
                            // of the selected item
                        }
                    });
            return builder.create();
        }

        default:
            break;
        }
        return null;

    }

}
