package com.dingxi.jackdemo;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import com.dingxi.jackdemo.model.ClassInfo;
import com.dingxi.jackdemo.model.GradeInfo;
import com.dingxi.jackdemo.model.StudentInfo;
import com.dingxi.jackdemo.model.UserInfo;
import com.dingxi.jackdemo.model.UserInfo.UserType;
import com.dingxi.jackdemo.network.JSONParser;
import com.dingxi.jackdemo.network.ResponseMessage;
import com.dingxi.jackdemo.network.RestClient;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class SearchMessageActivity extends Activity {

    public static final String TAG = "SearchMessageActivity";
    // private View parentHeader;
    // private ImageButton mBackButton;
    private static UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private ImageButton selectClassButton;
    private ImageButton selectGradeButton;
    private ImageButton selectStudentButton;
    private ImageButton selectTimeButton;
    private static String mSchoolId;
    private static String mGradeId;
    private static String mClassId;
    private static String mStudentId;
    private static String mData;
    private ProgressDialog mProgressDialog;
    private TextView selectClassTextView;
    private TextView selectGradeTextView;
    private TextView selectStudentTextView;
    private TextView selectTimeTextView;
    private ArrayList<ClassInfo> calssInfoList;
    private String calssNameList[];
    private ArrayList<GradeInfo> gradeInfoList;
    private String gradeNameList[];
    private ArrayList<StudentInfo> studentInfoList;
    private String studentNameList[];
    private SearchTask mSearchTask;
    private SearchType curretSearchType;
    private Button sreachConfirmButton;
    private Button sreachCancelButton;
    private DatePickerDialog mTimePickerDialog;

    private enum SearchType {
        ClassInfo, GradeInfo, StudentInfo
    }

    private Calendar cal = Calendar.getInstance(); 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_message);

        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();
        curretUserInfo = mXiaoYunTongApplication.userInfo;

        sreachConfirmButton = (Button) findViewById(R.id.sreach_confirm_button);
        sreachConfirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                
                if(TextUtils.isEmpty(mGradeId)){
                    Toast.makeText(SearchMessageActivity.this, R.string.select_grade,
                            Toast.LENGTH_LONG).show();
                } else if(TextUtils.isEmpty(mClassId)){
                    Toast.makeText(SearchMessageActivity.this, R.string.select_class,
                            Toast.LENGTH_LONG).show();
                } else if(TextUtils.isEmpty(mStudentId)){
                    Toast.makeText(SearchMessageActivity.this, R.string.select_student,
                            Toast.LENGTH_LONG).show();
                } else if(TextUtils.isEmpty(mData)){
                    Toast.makeText(SearchMessageActivity.this, R.string.select_time,
                            Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(SearchMessageActivity.this, HomeWorkActivity.class);
                    
                    Bundle  searchBundle = new Bundle();
                    searchBundle.putString("mGradeId", mGradeId);
                    searchBundle.putString("mClassId", mClassId);
                    searchBundle.putString("mStudentId", mStudentId);
                    searchBundle.putString("mData", mData);
                    intent.putExtras(searchBundle);
                    setResult(RESULT_OK, intent);
                    finish();   
                }
                
                

            }
        });

        sreachCancelButton = (Button) findViewById(R.id.sreach_cancel_button);
        sreachCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SearchMessageActivity.this.finish();
            }
        });

        selectGradeButton = (ImageButton) findViewById(R.id.select_grade_button);
        selectGradeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "selectGradeButton OnClick");
                if (!TextUtils.isEmpty(mSchoolId)) {
                    curretSearchType = SearchType.GradeInfo;
                    mProgressDialog = new ProgressDialog(SearchMessageActivity.this);
                    mProgressDialog.setMessage(getString(R.string.now_loading_grade));
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            if (mSearchTask != null && !mSearchTask.isCancelled()) {
                                mSearchTask.cancel(true);

                                // isCancelLogin = true;
                            }
                        }
                    });
                    mProgressDialog.show();
                    mSearchTask = new SearchTask();
                    mSearchTask.execute((Void) null);
                }

            }
        });

        selectClassButton = (ImageButton) findViewById(R.id.select_class_button);
        selectClassButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "selectClassButton OnClick");
                if (!TextUtils.isEmpty(mGradeId)) {
                    curretSearchType = SearchType.ClassInfo;
                    mProgressDialog = new ProgressDialog(SearchMessageActivity.this);
                    mProgressDialog.setMessage(getString(R.string.now_loading_class));
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            if (mSearchTask != null && !mSearchTask.isCancelled()) {
                                mSearchTask.cancel(true);

                                // isCancelLogin = true;
                            }
                        }
                    });
                    mProgressDialog.show();
                    mSearchTask = new SearchTask();
                    mSearchTask.execute((Void) null);
                } else {
                    Toast.makeText(SearchMessageActivity.this, R.string.select_grade,
                            Toast.LENGTH_LONG).show();
                }

            }
        });

        selectStudentButton = (ImageButton) findViewById(R.id.select_student_button);
        selectStudentButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "selectStudentButton OnClick");
                if (!TextUtils.isEmpty(mClassId)) {
                    curretSearchType = SearchType.StudentInfo;
                    mProgressDialog = new ProgressDialog(SearchMessageActivity.this);
                    mProgressDialog.setMessage(getString(R.string.now_loading_student));
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            if (mSearchTask != null && !mSearchTask.isCancelled()) {
                                mSearchTask.cancel(true);

                                // isCancelLogin = true;
                            }
                        }
                    });
                    mProgressDialog.show();
                    mSearchTask = new SearchTask();
                    mSearchTask.execute((Void) null);
                } else {
                    Toast.makeText(SearchMessageActivity.this, R.string.select_class,
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        
        
        selectTimeButton = (ImageButton) findViewById(R.id.select_time_button);
        selectTimeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                
              mTimePickerDialog =  new DatePickerDialog(SearchMessageActivity.this, new OnDateSetListener() {
                    
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        cal.set(Calendar.YEAR, year); 
                        
                        cal.set(Calendar.MONTH, monthOfYear); 
               
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth); 
                        
                        updateDate();
                    }
                }, year, month, day);
                
                mTimePickerDialog.setTitle(R.string.select_time);
                mTimePickerDialog.show();
                // TODO Auto-generated method stub

            }
        });
        
        
        selectClassTextView = (TextView) findViewById(R.id.select_class_text);
        selectGradeTextView = (TextView) findViewById(R.id.select_grade_text);
        selectStudentTextView = (TextView) findViewById(R.id.select_student_text);
        selectTimeTextView = (TextView) findViewById(R.id.select_time_text);

        if (curretUserInfo.roleType == UserType.ROLE_TEACHER) {
            mSchoolId = curretUserInfo.fkSchoolId;
            Log.i(TAG, "mSchoolId " + mSchoolId);
            if (TextUtils.isEmpty(mSchoolId)) {

            }
        }

    }

    private void updateDate(){ 
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
        mData = simpleDateFormat.format(cal.getTime());
        selectTimeTextView.setText(mData); 
        Log.i(TAG, "mData " + mData);
    } 
    
    
    @Override
    protected Dialog onCreateDialog(int id, Bundle args) {
        // TODO Auto-generated method stub
        switch (id) {
        case R.id.select_class_button:

            if (calssInfoList != null && calssInfoList.size() > 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchMessageActivity.this);
                builder.setTitle(R.string.select_school).setItems(calssNameList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "which " + which);
                                mClassId = calssInfoList.get(which).id;
                                selectClassTextView.setText(calssInfoList.get(which).name);
                                Log.d(TAG, "mClassId " + mSchoolId);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchMessageActivity.this);
                builder.setTitle(R.string.select_school).setItems(gradeNameList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "which " + which);
                                mGradeId = gradeInfoList.get(which).id;
                                selectGradeTextView.setText(gradeInfoList.get(which).name);
                                Log.d(TAG, "mGradeId " + mSchoolId);
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
                AlertDialog.Builder builder = new AlertDialog.Builder(SearchMessageActivity.this);
                builder.setTitle(R.string.select_school).setItems(studentNameList,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(TAG, "which " + which);
                                mStudentId = studentInfoList.get(which).id;
                                selectStudentTextView.setText(studentInfoList.get(which).name);
                                Log.d(TAG, "mStudentId " + mSchoolId);
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

    public class SearchTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            String searchTypeReslut = null;
            try {
                if (curretSearchType == SearchType.GradeInfo) {
                    searchTypeReslut = RestClient.getGradeInfos(curretUserInfo.id,
                            curretUserInfo.ticket, mSchoolId);
                } else if (curretSearchType == SearchType.ClassInfo) {
                    searchTypeReslut = RestClient.getClassInfos(curretUserInfo.id,
                            curretUserInfo.ticket, mGradeId);
                } else if (curretSearchType == SearchType.StudentInfo) {
                    searchTypeReslut = RestClient.getStudentInfos(curretUserInfo.id,
                            curretUserInfo.ticket, mClassId);
                }

            } catch (ConnectTimeoutException stex) {
                searchTypeReslut = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                searchTypeReslut = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                searchTypeReslut = getString(R.string.connection_server_error);
            } catch (XmlPullParserException e) {
                searchTypeReslut = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (IOException e) {
                searchTypeReslut = getString(R.string.connection_error);
                e.printStackTrace();
            }

            // TODO: register the new account here.
            return searchTypeReslut;
        }

        @Override
        protected void onPostExecute(String searchTypeReslut) {
            mSearchTask = null;
            Log.d(TAG, "result " + searchTypeReslut);
            if (!TextUtils.isEmpty(searchTypeReslut)) {

                try {
                    if (searchTypeReslut.startsWith("{") && searchTypeReslut.endsWith("}")) {
                        if (JSONParser.getIntByTag(searchTypeReslut, ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {
                            int total = JSONParser.getIntByTag(searchTypeReslut,
                            		ResponseMessage.RESULT_TAG_TOTAL);
                            if (curretSearchType == SearchType.GradeInfo) {
                                if (total == 0) {

                                }else if(total > 0) {
                                    parseGradeInfo(searchTypeReslut); 
                                }
                                
                            } else if (curretSearchType == SearchType.ClassInfo) {
                               
                                if (total == 0) {

                                } else if(total > 0){
                                    parseClassInfo(searchTypeReslut);
                                }
                            } else if (curretSearchType == SearchType.StudentInfo) {
                                
                                if (total == 0) {

                                }else if(total > 0){
                                    parseStudentInfo(searchTypeReslut);
                                }
                            }
                            mProgressDialog.dismiss();
                            if (curretSearchType == SearchType.GradeInfo) {
                                showDialog(R.id.select_grade_button);
                            } else if (curretSearchType == SearchType.ClassInfo) {
                                showDialog(R.id.select_class_button);
                            } else if (curretSearchType == SearchType.StudentInfo) {
                                showDialog(R.id.select_student_button);
                            }

                        } else {
                            mProgressDialog.dismiss();
                            String errorMessage = JSONParser.getStringByTag(searchTypeReslut,
                            		ResponseMessage.RESULT_TAG_MESSAGE);
                            if (!TextUtils.isEmpty(errorMessage)) {
                                Toast.makeText(SearchMessageActivity.this, errorMessage,
                                        Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(SearchMessageActivity.this, searchTypeReslut,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        mProgressDialog.dismiss();
                        Toast.makeText(SearchMessageActivity.this, searchTypeReslut,
                                Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    mProgressDialog.dismiss();
                    Toast.makeText(SearchMessageActivity.this, searchTypeReslut, Toast.LENGTH_LONG)
                            .show();
                    e.printStackTrace();
                }
            } else {
                mProgressDialog.dismiss();

            }
        }

        private void parseStudentInfo(String searchTypeReslut) {
            // TODO Auto-generated method stub
            // TODO Auto-generated method stub
            if (!TextUtils.isEmpty(searchTypeReslut)) {
                Log.d(TAG, "result schoolsInfo " + searchTypeReslut);
                try {
                    if (JSONParser.getIntByTag(searchTypeReslut, ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

                        studentInfoList = JSONParser.toParserStudentInfoList(searchTypeReslut);
                    } else {
                        JSONParser.getStringByTag(searchTypeReslut, ResponseMessage.RESULT_TAG_MESSAGE);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (studentInfoList != null && studentInfoList.size() > 0) {
                studentNameList = new String[studentInfoList.size()];

                for (int i = 0; i < studentInfoList.size(); i++) {
                    studentNameList[i] = studentInfoList.get(i).name;

                }
            }
        }

        private void parseClassInfo(String searchTypeReslut) {
            // TODO Auto-generated method stub
            if (!TextUtils.isEmpty(searchTypeReslut)) {
                Log.d(TAG, "result schoolsInfo " + searchTypeReslut);
                try {
                    if (JSONParser.getIntByTag(searchTypeReslut, ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

                        calssInfoList = JSONParser.toParserCalssInfoList(searchTypeReslut);
                    } else {
                        JSONParser.getStringByTag(searchTypeReslut, ResponseMessage.RESULT_TAG_MESSAGE);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (calssInfoList != null && calssInfoList.size() > 0) {
                calssNameList = new String[calssInfoList.size()];

                for (int i = 0; i < calssInfoList.size(); i++) {
                    calssNameList[i] = calssInfoList.get(i).name;

                }
            }
        }

        private void parseGradeInfo(String searchTypeReslut) {
            // TODO Auto-generated method stub
            if (!TextUtils.isEmpty(searchTypeReslut)) {
                Log.d(TAG, "result schoolsInfo " + searchTypeReslut);
                try {
                    if (JSONParser.getIntByTag(searchTypeReslut, ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

                        gradeInfoList = JSONParser.toParserGradeInfoList(searchTypeReslut);
                    } else {
                        JSONParser.getStringByTag(searchTypeReslut, ResponseMessage.RESULT_TAG_MESSAGE);
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (gradeInfoList != null && gradeInfoList.size() > 0) {
                gradeNameList = new String[gradeInfoList.size()];

                for (int i = 0; i < gradeInfoList.size(); i++) {
                    gradeNameList[i] = gradeInfoList.get(i).name;

                }
            }
        }

        @Override
        protected void onCancelled() {
            mSearchTask = null;

        }
    }

}
