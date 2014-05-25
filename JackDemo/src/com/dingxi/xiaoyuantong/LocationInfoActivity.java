package com.dingxi.xiaoyuantong;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.SeekBar;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.Symbol;
//import com.baidu.mapapi.map.Symbol.Color;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.dingxi.xiaoyuantong.model.LocationInfo;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;

public class LocationInfoActivity extends FragmentActivity {

    final static String TAG = "LocationInfoActivity";

    BMapManager mBMapMan = null;//
    MapView mMapView = null;
    String key = "YA9Y3C8usVBg9Eo4BezvO6Sn";
    GetLocationTask mGetLocationTask;
    GetHostryLocationTask mGetHostryLocationTask;
    GetRtlsTask mGetRtlsTask;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private ProgressDialog mProgressDialog;
    private String imei;
    private String chaildId;
    MapController mMapController;
    GeoPoint curretGeoPoint;
    PopupOverlay pop;
    private View viewCache = null;
    ArrayList<LocationInfo> locationInfos;
    private Button locationButton;
    private Button hostryButton;
    private SeekBar mSeekBar;
    private TextView mTimeTextView;
    private String mEndTime;
    private String mEndDate;
    private TextView mTopTextView;
    private CheckBox toolbarArrowsCheckBox;
    private LinearLayout mTimeLayout;
    private ImageButton mBackButton;
    private ImageButton mLoctionButton;
    private Handler handler = new Handler( );
    private Runnable runnable = new Runnable( ) {

        public void run ( ) {
            
            mLoctionButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.right_title_bar_selector));
            //mLoctionButton.setBackground(background);
            mLoctionButton.setClickable(true);

        //handler.postDelayed(this,1000);     //postDelayed(this,1000)方法安排一个Runnable对象到主线程队列中
        handler.removeCallbacks(runnable); 
        }

        };
        
        
    private CaldroidFragment dialogCaldroidFragment;

    // CalendarView calendarView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();

        mBMapMan = new BMapManager(mXiaoYunTongApplication);
        mBMapMan.init(key, null);
        // 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
        setContentView(R.layout.activity_location_info);

        mBackButton = (ImageButton) findViewById(R.id.back_button);
        mBackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        
        mLoctionButton = (ImageButton) findViewById(R.id.locaion_button);
        mLoctionButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                mLoctionButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.un_loction));
                //mLoctionButton.setBackground(background);
                mLoctionButton.setClickable(false);
                handler.postDelayed(runnable,60000); 
                mGetRtlsTask = new GetRtlsTask();
                mGetRtlsTask.execute((Void) null);
                
            }
        });
        
        
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        // Setup listener
        final CaldroidListener listener = new CaldroidListener() {

            @Override
            public void onSelectDate(Date date, View view) {

                mEndDate = formatter.format(date);
                Log.i(TAG, "time " + mEndDate);
                dialogCaldroidFragment.dismiss();
                hostryLocatonClick();
                // Toast.makeText(getApplicationContext(), mEndDate,
                // Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChangeMonth(int month, int year) {
            }

            @Override
            public void onLongClickDate(Date date, View view) {
                String time = formatter.format(date);
                Log.i(TAG, "Long click time " + time);
            }

            @Override
            public void onCaldroidViewCreated() {

                if (dialogCaldroidFragment.getLeftArrowButton() != null) {
                    dialogCaldroidFragment.setShowNavigationArrows(false);
                    dialogCaldroidFragment.setEnableSwipe(false);
                }

            }

        };

        mTopTextView = (TextView) findViewById(R.id.top_textview);
        final Bundle state = savedInstanceState;
        mTopTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                dialogCaldroidFragment = new CaldroidFragment();
                dialogCaldroidFragment.setCaldroidListener(listener);

                dialogCaldroidFragment.setMinDate(null);
                dialogCaldroidFragment.setMaxDate(null);

                // If activity is recovered from rotation
                final String dialogTag = "CALDROID_DIALOG_FRAGMENT";
                if (state != null) {
                    dialogCaldroidFragment.restoreDialogStatesFromKey(getSupportFragmentManager(),
                            state, "DIALOG_CALDROID_SAVED_STATE", dialogTag);
                    Bundle args = dialogCaldroidFragment.getArguments();
                    if (args == null) {
                        args = new Bundle();
                        dialogCaldroidFragment.setArguments(args);
                    }
                    args.putString(CaldroidFragment.DIALOG_TITLE, "Select a date");
                } else {
                    // Setup arguments
                    Bundle bundle = new Bundle();
                    // Setup dialogTitle
                    bundle.putString(CaldroidFragment.DIALOG_TITLE, "Select a date");
                    dialogCaldroidFragment.setArguments(bundle);
                }

                dialogCaldroidFragment.show(getSupportFragmentManager(), dialogTag);

            }
        });

        toolbarArrowsCheckBox = (CheckBox) findViewById(R.id.toolbar_arrows_checkbox);
        toolbarArrowsCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub

                if (isChecked) {
                    mTimeLayout.setVisibility(View.VISIBLE);
                } else {
                    mTimeLayout.setVisibility(View.GONE);
                }

            }
        });

        mTimeLayout = (LinearLayout) findViewById(R.id.time_layout);

        locationButton = (Button) findViewById(R.id.button_location);
        locationButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mTopTextView.setText(R.string.now_location);
                mTimeLayout.setVisibility(View.GONE);
                mTopTextView.setClickable(false);
                toolbarArrowsCheckBox.setClickable(false);
                locationCilck();
            }
        });

        hostryButton = (Button) findViewById(R.id.button_history);
        hostryButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mTopTextView.setText(R.string.history_location);
                // mTimeLayout.setVisibility(View.VISIBLE);
                mTopTextView.setClickable(true);
                toolbarArrowsCheckBox.setClickable(true);
                hostryLocatonClick();
            }
        });

        mTimeTextView = (TextView) findViewById(R.id.time_textview);
        mSeekBar = (SeekBar) findViewById(R.id.time_seekBar);

        mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onStopTrackingTouch()");
                int endTime = seekBar.getProgress();
                mEndTime = secToTime(endTime);
                hostryLocatonClick();
                Log.d(TAG, "mEndTime " + mEndTime);
                // private TextView mTimeTextView;
                // private String endTime;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onStartTrackingTouch()");
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                Log.d(TAG, "progress " + progress);
                mTimeTextView.setText(secToTime(progress));

            }
        });

        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.setBuiltInZoomControls(true);

        imei = getIntent().getStringExtra("imei");

        chaildId = getIntent().getStringExtra("childId");
        // 设置启用内置的缩放控件
        mMapController = mMapView.getController();
        // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));
        // 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
        mMapController.setCenter(point);// 设置地图中心点
        mMapController.setZoom(14);// 设置地图zoom级别

        // TODO Auto-generated method stub
        locationButton.performClick();

    }

    private void locationCilck() {

        if (mGetLocationTask != null && !mGetLocationTask.isCancelled()) {
            mGetLocationTask.cancel(true);
        }
        mProgressDialog = new ProgressDialog(LocationInfoActivity.this);
        mProgressDialog.setMessage(getString(R.string.now_loading_locaton));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (mGetLocationTask != null && !mGetLocationTask.isCancelled()) {
                    mGetLocationTask.cancel(true);
                }
            }
        });

        mProgressDialog.show();
        mGetLocationTask = new GetLocationTask();
        mGetLocationTask.execute((Void) null);

    }

    @SuppressLint("SimpleDateFormat")
    private void hostryLocatonClick() {

        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd");

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");

        Date now = new Date();

        if (TextUtils.isEmpty(mEndDate)) {
            mEndDate = date.format(now);
        }

        if (TextUtils.isEmpty(mEndTime)) {
            mEndTime = time.format(now);
        }

        if (mGetHostryLocationTask != null && !mGetHostryLocationTask.isCancelled()) {
            mGetHostryLocationTask.cancel(true);
        }

        mProgressDialog = new ProgressDialog(LocationInfoActivity.this);
        mProgressDialog.setMessage(getString(R.string.now_loading_locaton));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (mGetHostryLocationTask != null && !mGetHostryLocationTask.isCancelled()) {
                    mGetHostryLocationTask.cancel(true);
                }
            }
        });

        mProgressDialog.show();
        mGetHostryLocationTask = new GetHostryLocationTask();
        mGetHostryLocationTask.execute((Void) null);

    }

    @Override
    protected void onDestroy() {

        mMapView.destroy();

        if (mBMapMan != null) {
            mBMapMan.destroy();
            mBMapMan = null;
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        if (mBMapMan != null) {
            mBMapMan.stop();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        if (mBMapMan != null) {
            mBMapMan.start();
        }
        super.onResume();

    }
    
    
    
    public class GetRtlsTask extends AsyncTask<Void, Void, ResponseMessage> {

        @Override
        protected ResponseMessage doInBackground(Void... params) {
            ResponseMessage responseMessage = new ResponseMessage();
            Thread.currentThread().setName("GetRtlsTask");


            try {
                responseMessage.body = RestClient.getRtls(imei);// String id,
                Log.i(TAG, "getRtls  " + responseMessage.body); // String ticket,
                // String imei
                responseMessage.praseBody();
            } catch (ConnectTimeoutException stex) {
                responseMessage.message = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                responseMessage.message = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                responseMessage.message = getString(R.string.connection_server_error);
            } catch (XmlPullParserException e) {
                responseMessage.message = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (JSONException e) {
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
            mGetRtlsTask = null;
            Log.i(TAG, "responseMessage.body " + responseMessage.body);
            if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                Toast.makeText(LocationInfoActivity.this, R.string.send_command,
                        Toast.LENGTH_LONG).show();

                mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(LocationInfoActivity.this, responseMessage.message,
                        Toast.LENGTH_LONG).show();

            }

        }

        @Override
        protected void onCancelled() {
            if (mGetRtlsTask != null) {
                mGetRtlsTask = null;
            }

        }
    }
    

    public class GetLocationTask extends AsyncTask<Void, Void, ResponseMessage> {

        @Override
        protected ResponseMessage doInBackground(Void... params) {
            ResponseMessage responseMessage = new ResponseMessage();
            Thread.currentThread().setName("GetLocationTask()");
            try {

                responseMessage.body = RestClient.getGpsPosition(
                        mXiaoYunTongApplication.userInfo.id,
                        mXiaoYunTongApplication.userInfo.ticket, imei);// String id,
                Log.i(TAG, "getGpsPosition  " + responseMessage.body); // String ticket,
                // String imei
                responseMessage.praseBody();
            } catch (ConnectTimeoutException stex) {
                responseMessage.message = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                responseMessage.message = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                responseMessage.message = getString(R.string.connection_server_error);
            } catch (XmlPullParserException e) {
                responseMessage.message = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (JSONException e) {
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
            mGetLocationTask = null;
            Log.i(TAG, "responseMessage.body " + responseMessage.body);
            if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                if (responseMessage.total > 0) {

                    try {
                        locationInfos = JSONParser.toParserLocationInfo(responseMessage.body);
                        if (locationInfos.size() > 0) {
                            LocationInfo locationInfo = locationInfos.get(0);
                            Log.i(TAG, "locationInfo.address " + locationInfo.address);
                            if (locationInfo != null) {
                                if (TextUtils.isEmpty(locationInfo.lat)
                                        || TextUtils.isEmpty(locationInfo.lng)) {
                                    Toast.makeText(LocationInfoActivity.this,
                                            R.string.not_location_data, Toast.LENGTH_LONG).show();
                                } else {

                                    viewCache = getLayoutInflater().inflate(
                                            R.layout.custom_baidu_popo, null);
                                    TextView popoTimeText = (TextView) viewCache
                                            .findViewById(R.id.pop_time_value);
                                    TextView popoAddressText = (TextView) viewCache
                                            .findViewById(R.id.popo_address_value);
                                    popoTimeText.setText(locationInfo.optTime);
                                    popoAddressText.setText(locationInfo.address);
                                    double mLat1 = Double.parseDouble(locationInfo.lat);
                                    Log.i(TAG, "mLat1 " + mLat1);
                                    double mLon1 = Double.parseDouble(locationInfo.lng);
                                    Log.i(TAG, "mLon1 " + mLon1);
                                    curretGeoPoint = new GeoPoint((int) (mLat1 * 1E6),
                                            (int) (mLon1 * 1E6));
                                    Drawable mark = getResources().getDrawable(
                                            R.drawable.location_start);
                                    OverlayItem item = new OverlayItem(curretGeoPoint, "item2",
                                            "item2");

                                    OverlayGraphicItemized itemOverlay = new OverlayGraphicItemized(
                                            mark, mMapView);

                                    mMapView.getOverlays().clear();
                                    mMapView.getOverlays().add(itemOverlay);

                                    itemOverlay.addItem(item);
                                    mMapController.setZoom(12);// 设置地图zoom级别
                                    mMapController.setCenter(curretGeoPoint);// 设置地图中心点
                                    mMapView.refresh();

                                    MKSearch mMKSearch = new MKSearch();
                                    mMKSearch.init(mBMapMan, new MKSearchListener() {

                                        @Override
                                        public void onGetAddrResult(MKAddrInfo res, int error) {
                                            // TODO Auto-generated method stub
                                            if (error != 0) {
                                                // String str = String.format("错误号：%d",
                                                // error);
                                                // Toast.makeText(GeoCoderDemo.this, str,
                                                // Toast.LENGTH_LONG).show();
                                                return;
                                            }
                                            // 地图移动到该点
                                            if (res.type == MKAddrInfo.MK_GEOCODE) {
                                                // 地理编码：通过地址检索坐标点
                                                String strInfo = String.format("纬度：%f 经度：%f",
                                                        res.geoPt.getLatitudeE6() / 1e6,
                                                        res.geoPt.getLongitudeE6() / 1e6);
                                                Log.i(TAG, "strInfo " + strInfo);
                                                // Toast.makeText(LocationInfoActivity.this,
                                                // strInfo, Toast.LENGTH_LONG).show();
                                            }
                                            if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
                                                // 反地理编码：通过坐标点检索详细地址及周边poi
                                                String strInfo = res.strAddr;
                                                Log.i(TAG, "strInfo " + strInfo);
                                                // Toast.makeText(LocationInfoActivity.this,
                                                // strInfo, Toast.LENGTH_LONG).show();

                                            }
                                        }

                                        @Override
                                        public void onGetBusDetailResult(MKBusLineResult arg0,
                                                int arg1) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onGetDrivingRouteResult(
                                                MKDrivingRouteResult arg0, int arg1) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onGetPoiDetailSearchResult(int arg0, int arg1) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onGetPoiResult(MKPoiResult arg0, int arg1,
                                                int arg2) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onGetShareUrlResult(MKShareUrlResult arg0,
                                                int arg1, int arg2) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onGetSuggestionResult(MKSuggestionResult arg0,
                                                int arg1) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onGetTransitRouteResult(
                                                MKTransitRouteResult arg0, int arg1) {
                                            // TODO Auto-generated method stub

                                        }

                                        @Override
                                        public void onGetWalkingRouteResult(
                                                MKWalkingRouteResult arg0, int arg1) {
                                            // TODO Auto-generated method stub

                                        }

                                    });
                                    mMKSearch.reverseGeocode(curretGeoPoint);

                                }
                            }
                            /*
                             * else { Toast.makeText(LocationInfoActivity.this,
                             * R.string.not_location_data, Toast.LENGTH_LONG).show(); }
                             */
                        } else {
                            Toast.makeText(LocationInfoActivity.this, R.string.not_location_data,
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LocationInfoActivity.this, R.string.not_location_data,
                            Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(LocationInfoActivity.this, responseMessage.message,
                        Toast.LENGTH_LONG).show();

            }

        }

        @Override
        protected void onCancelled() {
            mGetLocationTask = null;

        }
    }

    public class GetHostryLocationTask extends AsyncTask<Void, Void, ResponseMessage> {

        @Override
        protected ResponseMessage doInBackground(Void... params) {
            ResponseMessage responseMessage = new ResponseMessage();
            Thread.currentThread().setName("GetHostryLocationTask");
            Log.i(TAG, "mEndDate " + mEndDate);
            Log.i(TAG, "mEndTime " + mEndTime);

            try {
                responseMessage.body = RestClient.getHisPosition(mEndDate, "00:00:00", mEndTime,
                        chaildId);// String id,
                Log.i(TAG, "getHisPosition  " + responseMessage.body); // String ticket,
                // String imei
                responseMessage.praseBody();
            } catch (ConnectTimeoutException stex) {
                responseMessage.message = getString(R.string.request_time_out);
            } catch (SocketTimeoutException stex) {
                responseMessage.message = getString(R.string.server_time_out);
            } catch (HttpHostConnectException hhce) {
                responseMessage.message = getString(R.string.connection_server_error);
            } catch (XmlPullParserException e) {
                responseMessage.message = getString(R.string.connection_error);
                e.printStackTrace();
            } catch (JSONException e) {
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
            mGetLocationTask = null;
            Log.i(TAG, "responseMessage.body " + responseMessage.body);
            if (responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS) {
                if (responseMessage.total > 0) {

                    try {
                        locationInfos.clear();
                        locationInfos = null;
                        locationInfos = JSONParser.toParserHostryLocationInfo(responseMessage.body);
                        Log.d(TAG, "locationInfos.size() " + locationInfos.size());
                        if (locationInfos.size() > 0) {
                            mMapView.getOverlays().clear();

                          
                            
                            showItemOverlayResult();
                            /*
                            GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
                            mMapView.getOverlays().add(graphicsOverlay);
                            graphicsOverlay.setData(drawLine());
                            */


                            mMapController.setZoom(12);// 设置地图zoom级别
                            mMapController.setCenter(curretGeoPoint);// 设置地图中心点
                            mMapView.refresh();

                        } else {
                            Toast.makeText(LocationInfoActivity.this, R.string.not_location_data,
                                    Toast.LENGTH_LONG).show();
                        }

                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(LocationInfoActivity.this, R.string.not_location_data,
                            Toast.LENGTH_LONG).show();
                }

                mProgressDialog.dismiss();
            } else {
                mProgressDialog.dismiss();
                Toast.makeText(LocationInfoActivity.this, responseMessage.message,
                        Toast.LENGTH_LONG).show();

            }

        }

        @Override
        protected void onCancelled() {
            if (mGetHostryLocationTask != null) {
                mGetHostryLocationTask = null;
            }

        }
    }

    OverlayGraphicItemized mOverlayGraphic;
    ArrayList<OverlayItem> mOverlayItemList;

    /** 显示被选联系人在地图中的轨迹信息 **/
    private void showItemOverlayResult() {
        mMapView.setLongClickable(true);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
        // mMapView.regMapViewListener(mBMapManager, mMapListener);
        mMapView.getOverlays().clear();
        Drawable marker = LocationInfoActivity.this.getResources().getDrawable(
                R.drawable.location_info);

        mOverlayGraphic = new OverlayGraphicItemized(marker, mMapView);
        mMapView.getOverlays().add(mOverlayGraphic);
        
        mOverlayItemList = new ArrayList<OverlayItem>();
        GeoPoint gp[] = new GeoPoint[locationInfos.size()];

        for (int i = 0; i < locationInfos.size(); i++) {
            LocationInfo locationInfo = locationInfos.get(i);
            Log.i(TAG, "locationInfo.lat " + locationInfo.lat);
            double mLat = Double.parseDouble(locationInfo.lat);
            Log.i(TAG, "mLat " + mLat);
            
            Log.i(TAG, "locationInfo.lng " + locationInfo.lng);
            double mLon = Double.parseDouble(locationInfo.lng);
            Log.i(TAG, "mLon " + mLon);
            GeoPoint geoPoint = new GeoPoint((int) (mLat * 1E6), (int) (mLon * 1E6));

            gp[i] = geoPoint;
            OverlayItem item = new OverlayItem(geoPoint, "overLay", "single user gps");
            if (i == 0) {
                curretGeoPoint = geoPoint;
                item.setMarker(getResources().getDrawable(R.drawable.location_start));
            } else if (i >= 1 && i == (locationInfos.size() - 1)) {
                item.setMarker(getResources().getDrawable(R.drawable.location_end));
            } else {
                item.setMarker(getResources().getDrawable(R.drawable.location_info));
            }

            mOverlayItemList.add(item);
        }

        if (gp != null) {
            Geometry lineGeometry = new Geometry();// 不可重用，要描别的线必须重新new
            lineGeometry.setPolyLine(gp);
            Symbol lineSymbol = new Symbol();// 不可重用，要描别的线必须重新new
            Symbol.Color lineColor = lineSymbol.new Color();
            lineColor.red = 150;
            lineColor.green = 0;
            lineColor.blue = 150;
            lineColor.alpha = 100;
            lineSymbol.setLineSymbol(lineColor, 5);
            Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
            
            
            GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
            graphicsOverlay.setData(lineGraphic);
            mMapView.getOverlays().add(graphicsOverlay);
        }

        if (mOverlayGraphic.size() < mOverlayItemList.size()) {
            mOverlayGraphic.addItem(mOverlayItemList);
        }
    }

    public Graphic drawLines() {

        GeoPoint[] linePoints = new GeoPoint[locationInfos.size()];
        for (int i = 0; i < locationInfos.size(); i++) {
            LocationInfo locationInfo = locationInfos.get(i);
            double mLat1 = Double.parseDouble(locationInfo.lat);
            Log.i(TAG, "mLat1 " + mLat1);
            double mLon1 = Double.parseDouble(locationInfo.lng);

            if (TextUtils.isEmpty(locationInfo.lat) || TextUtils.isEmpty(locationInfo.lng)) {
                Toast.makeText(LocationInfoActivity.this, R.string.not_location_data,
                        Toast.LENGTH_LONG).show();
            } else {

                Log.i(TAG, "mLon1 " + mLon1);
                GeoPoint geoPoint = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));

                // CoordinateConvert.fromWgs84ToBaidu(geoPoint);
                linePoints[i] = geoPoint;
                Drawable mark;
                if (i == 0) {
                    curretGeoPoint = geoPoint;
                    mark = getResources().getDrawable(R.drawable.location_start);
                } else if (i == (locationInfos.size() - 1)) {
                    mark = getResources().getDrawable(R.drawable.location_end);
                } else {
                    mark = getResources().getDrawable(R.drawable.location_info);
                }

                OverlayItem item = new OverlayItem(geoPoint, "item2", "item2");
                OverlayGraphicItemized itemOverlay = new OverlayGraphicItemized(mark, mMapView);
                itemOverlay.addItem(item);
                mMapView.getOverlays().add(itemOverlay);
            }

        }

        /*
         * for (int i = 0; i < locationInfos.size(); i++) { LocationInfo locationInfo =
         * locationInfos.get(i); double mLat1 = Double.parseDouble(locationInfo.lat);
         * Log.i(TAG, "mLat1 " + mLat1); double mLon1 =
         * Double.parseDouble(locationInfo.lng);
         * 
         * int lat = (int) (mLat1 * 1E6); int lon = (int) (mLon1 * 1E6);
         * 
         * GeoPoint pt1 = new GeoPoint(lat, lon); linePoints[i] = pt1; }
         */

        // 构建线
        Geometry lineGeometry = new Geometry();

        lineGeometry.setPolyLine(linePoints);
        // 设定样式
        Symbol lineSymbol = new Symbol();
        Symbol.Color lineColor = lineSymbol.new Color();
        lineColor.red = 255;
        lineColor.green = 0;
        lineColor.blue = 0;
        lineColor.alpha = 255;
        lineSymbol.setLineSymbol(lineColor, 10);
        // 生成Graphic对象
        Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
        return lineGraphic;
    }

    /*
     * 要处理overlay点击事件时需要继承ItemizedOverlay 不处理点击事件时可直接生成ItemizedOverlay.
     */
    class OverlayGraphicItemized extends ItemizedOverlay<OverlayItem> {
        // 用MapView构造ItemizedOverlay
        public OverlayGraphicItemized(Drawable mark, MapView mapView) {
            super(mark, mapView);
        }

        protected boolean onTap(int index) {
            Log.i(TAG, "item onTap2: " + index);
            // 在此处理MapView的点击事件，当返回 true时
            createPaopao();

            return true;
        }

        public boolean onTap(GeoPoint pt, MapView mapView) {

            super.onTap(pt, mapView);

            return false;
        }

    }

    /*
     * class PathOverlay extends ItemizedOverlay {
     * 
     * 
     * 
     * @Override public void draw(Canvas canvas, MapView mapView, boolean shadow) { //
     * TODO Auto-generated method stub super.draw(canvas, mapView, shadow); Projection
     * projection = mapView.getProjection();
     * 
     * // 连线 Path path = new Path();
     * 
     * // GeoPoint[] linePoints = new GeoPoint[locationInfos.size()]; Point[] points = new
     * Point[locationInfos.size()];
     * 
     * for (int i = 0; i < locationInfos.size(); i++) { LocationInfo locationInfo =
     * locationInfos.get(i); Point point = new Point(); double mLat1 =
     * Double.parseDouble(locationInfo.lat); Log.i(TAG, "mLat1 " + mLat1); double mLon1 =
     * Double.parseDouble(locationInfo.lng);
     * 
     * int lat = (int) (mLat1 * 1E6); int lon = (int) (mLon1 * 1E6); GeoPoint pt1 = new
     * GeoPoint(lat, lon); projection.toPixels(pt1, point);
     * 
     * if (i == 0) { path.moveTo(point.x, point.y); } else { path.lineTo(point.x,
     * point.y); }
     * 
     * // points[i] = point;
     * 
     * }
     * 
     * 
     * 
     * // 第二个画笔 画线 Paint paint = new Paint(); paint.setColor(android.graphics.Color.BLUE);
     * paint.setDither(true); paint.setStyle(Paint.Style.STROKE);
     * paint.setStrokeJoin(Paint.Join.ROUND); paint.setStrokeCap(Paint.Cap.ROUND);
     * paint.setStrokeWidth(4);
     * 
     * // 画出路径 canvas.drawPath(path, paint); } }
     */

    public void createPaopao() {

        // 泡泡点击响应回调
        PopupClickListener popListener = new PopupClickListener() {
            @Override
            public void onClickedPopup(int index) {
                Log.i(TAG, "onClickedPopup(index " + index + ") ");
                pop.hidePop();
                pop = null;
            }
        };
        pop = new PopupOverlay(mMapView, popListener);
        pop.showPopup(viewCache, curretGeoPoint, 10);

    }

    // a integer to xx:xx:xx
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0) {
            return "00:00";
        } else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                if (hour > 99)
                    return "99:59:59";
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

}
