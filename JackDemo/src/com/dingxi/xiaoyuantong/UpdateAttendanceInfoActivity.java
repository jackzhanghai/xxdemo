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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.dingxi.xiaoyuantong.model.AttendanceInfo.AttendanceInfoEntry;
import com.dingxi.xiaoyuantong.model.TeacherInfo;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;
import com.dingxi.xiaoyuantong.util.Util;

public class UpdateAttendanceInfoActivity extends Activity {

    public static final String TAG = "UpdateAttendanceInfoActivity";
    private ImageButton mBackButton;
    private TeacherInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    //private Button sendHomeWorkButton;
    private UpdateAttendanceInfoTask mUpdateAttendanceInfoTask;
    private ProgressDialog mProgressDialog;
    private TextView stuNameText;
    private TextView attTimeText;
    private Integer updateId;
    private Integer direc;
    private RadioButton inSchoolRadioButton;
    private RadioButton outSchoolRadioButton;
    private RadioButton unKownRadioButton;
    private Button confirmButton;
    private Button cancekButton;

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

        updateId = getIntent().getIntExtra(AttendanceInfoEntry.COLUMN_NAME_ENTRY_ID, 1);
        direc = getIntent().getIntExtra(AttendanceInfoEntry.COLUMN_NAME_DIREC,3);
        Log.i(TAG, "direc " + direc);
        String stuName = getIntent().getStringExtra(AttendanceInfoEntry.COLUMN_NAME_STU_NAME);
        String attTime = getIntent().getStringExtra(AttendanceInfoEntry.COLUMN_NAME_ATT_TIME);

        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();
        curretUserInfo = (TeacherInfo) mXiaoYunTongApplication.userInfo;

        stuNameText = (TextView) findViewById(R.id.stu_name_text);
        stuNameText.setText(stuName);
        attTimeText = (TextView) findViewById(R.id.att_time_text);
        attTimeText.setText(attTime);
        
        inSchoolRadioButton = (RadioButton) findViewById(R.id.radio_into_school);        
        outSchoolRadioButton = (RadioButton) findViewById(R.id.radio_out_school);
        unKownRadioButton = (RadioButton) findViewById(R.id.radio_unkown);
        Log.i(TAG, "direc " + direc);
        if(1== direc){
            inSchoolRadioButton.setChecked(true);
        } else if(2 == direc){
            outSchoolRadioButton.setChecked(true);
        } else {
            unKownRadioButton.setChecked(true);
        }
        
        

        confirmButton = (Button) findViewById(R.id.edit_ok_button);
        confirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Util.IsNetworkAvailable(UpdateAttendanceInfoActivity.this)) {
                    mProgressDialog = new ProgressDialog(UpdateAttendanceInfoActivity.this);
                    mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    mProgressDialog.setMessage(getString(R.string.now_modiy_attendance));
                    mProgressDialog.setOnCancelListener(new OnCancelListener() {

                        @Override
                        public void onCancel(DialogInterface dialog) {
                            // TODO Auto-generated method stub
                            if (mUpdateAttendanceInfoTask != null
                                    && !mUpdateAttendanceInfoTask.isCancelled()) {
                                mUpdateAttendanceInfoTask.cancel(true);
                                // isCancelLogin = true;
                            }
                        }
                    });
                    mProgressDialog.show();
                    mUpdateAttendanceInfoTask = new UpdateAttendanceInfoTask(updateId, direc);
                    mUpdateAttendanceInfoTask.execute((Void) null);
                }

            }
        });
        cancekButton = (Button) findViewById(R.id.edit_cancel_button);
        cancekButton.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                UpdateAttendanceInfoActivity.this.finish();
            }
        });
        

    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
        case R.id.radio_into_school:
            if (checked)
                direc = 1;
            break;
        case R.id.radio_out_school:
            if (checked)
                direc = 2;
            break;
        case R.id.radio_unkown:
            if (checked)
                direc = 3;
            break;
        }
    }

    public class UpdateAttendanceInfoTask extends AsyncTask<Void, Void, ResponseMessage> {

        Integer fkAttId;
        Integer direc;

        public UpdateAttendanceInfoTask(Integer fkAttId,Integer direc) {
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

                responseMessage.body = RestClient.updateAttendance(curretUserInfo.id,
                        curretUserInfo.ticket, info.toString());

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

            return responseMessage;
        }

        @Override
        protected void onPostExecute(ResponseMessage responseMessage) {
            mUpdateAttendanceInfoTask = null;

            Log.d(TAG, "responseMessage.code " + responseMessage.code);
            Log.d(TAG, "responseMessage.body " + responseMessage.body);
            if(responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS){
                Toast.makeText(UpdateAttendanceInfoActivity.this, R.string.modiy_attendance_sucess,
                        Toast.LENGTH_LONG).show();
                Intent backIntet = new Intent(UpdateAttendanceInfoActivity.this, AttendanceInfoActivity.class);
                startActivity(backIntet);
                UpdateAttendanceInfoActivity.this.finish();
            } else {
                Toast.makeText(UpdateAttendanceInfoActivity.this, responseMessage.message,
                        Toast.LENGTH_LONG).show();
            }
            mProgressDialog.dismiss();

        }

        @Override
        protected void onCancelled() {
            mUpdateAttendanceInfoTask = null;

        }
    }

}
