package com.dingxi.xiaoyuantong;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.dingxi.xiaoyuantong.model.UserInfo;
import com.dingxi.xiaoyuantong.model.UserInfo.UserType;

@SuppressLint("SimpleDateFormat")
public class TSearchInnerMessageActivity extends Activity {

    public static final String TAG = "SearchMessageActivity";
    // private View parentHeader;
    // private ImageButton mBackButton;
    
    
    public static final int SEARCH_TYPE_HOME_WORK = 1;
    public static final int SEARCH_TYPE_ATTENDACE = 2;
    public static final int SEARCH_TYPE_CAMPUS_NOTE = 3;
    public static final  String SEARCH_TYPE = "searchType";
    private  UserInfo curretUserInfo;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private ImageButton selectStartTimeButton;
    private ImageButton selectTypeButton;
    private ImageButton selectEndTimeButton;

    private String mTypeId = "0";
    private String mEndData = "";
    private String mStartData = "";
    private TextView selectStartTimeTextView;
    private TextView selectTypeTextView;
    private TextView selectEndTimeTextView;
   
    private Button sreachConfirmButton;
    private Button sreachCancelButton;
    private DatePickerDialog mTimePickerDialog;

    private Calendar cal = Calendar.getInstance(); 
    
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_leave_message);

        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();
        curretUserInfo = mXiaoYunTongApplication.userInfo;
        
        sreachConfirmButton = (Button) findViewById(R.id.sreach_confirm_button);
        sreachConfirmButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                
                if(TextUtils.isEmpty(mStartData) ){
                    Toast.makeText(TSearchInnerMessageActivity.this, R.string.select_start_time,
                            Toast.LENGTH_LONG).show();
                }  else if(TextUtils.isEmpty(mEndData)){
                    Toast.makeText(TSearchInnerMessageActivity.this, R.string.select_end_time,
                            Toast.LENGTH_LONG).show();
                    
                }else {
                    Intent intent = new Intent(TSearchInnerMessageActivity.this, InnerMessageActivity.class);

                    Log.i(TAG, "mTypeId " + mTypeId);
                    Log.i(TAG, "mStartData " + mStartData);
                    Log.i(TAG, "mEndData " + mEndData);
                    Bundle  searchBundle = new Bundle();
                    searchBundle.putString("mTypeId", mTypeId);
                    searchBundle.putString("mStartData", mStartData);
                    searchBundle.putString("mEndData", mEndData);
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
                TSearchInnerMessageActivity.this.finish();
            }
        });

        selectTypeButton = (ImageButton) findViewById(R.id.select_type_button);
        selectTypeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.d(TAG, "selectGradeButton OnClick");
               String typeNameList[] = {getString(R.string.search_type_all),getString(R.string.search_type_receiver),getString(R.string.search_type_sender)};;
                    AlertDialog.Builder builder = new AlertDialog.Builder(TSearchInnerMessageActivity.this);
                    builder.setTitle(R.string.select_search_type).setItems(typeNameList,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    Log.d(TAG, "which " + which);
                                    switch (which) {
                                    case 0:
                                        mTypeId = "0";
                                        selectTypeTextView.setText(getString(R.string.search_type_all));
                                        break;
                                    case 1:
                                        mTypeId = "1";
                                        selectTypeTextView.setText(getString(R.string.search_type_receiver));
                                        break;
                                    case 2:
                                        mTypeId = "2";
                                        selectTypeTextView.setText(getString(R.string.search_type_sender));
                                        break;
                                    default:
                                        break;
                                    }
 
                                    // The 'which' argument contains the index
                                    // position
                                    // of the selected item
                                }
                            });
                     builder.create().show();


            }
        });

        selectStartTimeButton = (ImageButton) findViewById(R.id.select_start_time_button);
        selectStartTimeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "selectStartTimeButton OnClick");
              
                
                
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                
              mTimePickerDialog =  new DatePickerDialog(TSearchInnerMessageActivity.this, new OnDateSetListener() {
                    
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        //cal.set(Calendar.YEAR, year); 
                        
                        //cal.set(Calendar.MONTH, monthOfYear); 
               
                        //cal.set(Calendar.DAY_OF_MONTH, dayOfMonth); 
                        cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        updateDate(1);
                    }
                }, year, month, day);
                
                mTimePickerDialog.setTitle(R.string.select_time);
                mTimePickerDialog.show();

            }
        });

        
        
        selectEndTimeButton = (ImageButton) findViewById(R.id.select_end_time_button);
        selectEndTimeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "selectEndTimeButton OnClick");
                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);
                
              mTimePickerDialog =  new DatePickerDialog(TSearchInnerMessageActivity.this, new OnDateSetListener() {
                    
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        // TODO Auto-generated method stub
                        //cal.set(Calendar.YEAR, year); 
                        //cal.set(Calendar.MONTH, monthOfYear);
                        //cal.set(Calendar.DAY_OF_MONTH, dayOfMonth); 
                        cal.set(year, monthOfYear, dayOfMonth, 0, 0, 0);
                        
                        updateDate(2);
                    }
                }, year, month, day);
                
                mTimePickerDialog.setTitle(R.string.select_time);
                mTimePickerDialog.show();
                // TODO Auto-generated method stub

            }
        });
        
       
        selectStartTimeTextView = (TextView) findViewById(R.id.select_start_time_text);
        selectTypeTextView = (TextView) findViewById(R.id.select_type_text);
        selectEndTimeTextView = (TextView) findViewById(R.id.select_end_time_text);

        

    }

    private void updateDate(int time){ 
        
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if(time == 1){
            mStartData = simpleDateFormat.format(cal.getTime());
            selectStartTimeTextView.setText(dateFormat.format(cal.getTime())); 
            Log.i(TAG, "mStartData " + mStartData);
        } else {
            mEndData = simpleDateFormat.format(cal.getTime());
            selectEndTimeTextView.setText(dateFormat.format(cal.getTime())); 
            Log.i(TAG, "mEndData " + mEndData);
            
        }
        
    }

}
