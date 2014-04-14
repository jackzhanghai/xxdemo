package com.dingxi.xiaoyuantong;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
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

public class LocationInfoActivity extends Activity {

    final static String TAG = "LocationInfoActivity";

    BMapManager mBMapMan = null;//
    MapView mMapView = null;
    String key = "YA9Y3C8usVBg9Eo4BezvO6Sn";
    GetLocationTask mGetLocationTask;
    private XiaoYunTongApplication mXiaoYunTongApplication;
    private ProgressDialog mProgressDialog;
    private String imei;
    MapController mMapController;
    GeoPoint curretGeoPoint;
    PopupOverlay pop;
    private View viewCache = null;
    ArrayList<LocationInfo> locationInfos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate()");
        
        long startTime = java.lang.System.currentTimeMillis();
        long endTime;
        Log.d(TAG, "startTime " + startTime);
        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();
        endTime = java.lang.System.currentTimeMillis();
        Log.d(TAG, "1 " + (endTime - startTime));
        mBMapMan = new BMapManager(mXiaoYunTongApplication);
        mBMapMan.init(key, null);
        // 注意：请在试用setContentView前初始化BMapManager对象，否则会报错
        setContentView(R.layout.activity_location_info);
        endTime = java.lang.System.currentTimeMillis();
        Log.d(TAG, "2 " + (endTime - startTime));
        
        mMapView = (MapView) findViewById(R.id.bmapView);
        mMapView.setBuiltInZoomControls(true);
        endTime = java.lang.System.currentTimeMillis();
        Log.d(TAG, "3 " + (endTime - startTime));
        imei = getIntent().getStringExtra("imei");
        // 设置启用内置的缩放控件
        mMapController = mMapView.getController();
        // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        GeoPoint point = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));
        // 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
        mMapController.setCenter(point);// 设置地图中心点
        mMapController.setZoom(14);// 设置地图zoom级别
        endTime = java.lang.System.currentTimeMillis();
        Log.d(TAG, "4 " + (endTime - startTime));
        // TODO Auto-generated method stub
        mProgressDialog = new ProgressDialog(LocationInfoActivity.this);
        mProgressDialog.setMessage(getString(R.string.now_loading_locaton));
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setOnCancelListener(new OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                // TODO Auto-generated method stub
                if (mGetLocationTask != null && !mGetLocationTask.isCancelled()) {
                    mGetLocationTask.cancel(true);
                    // isCancelLogin = true;
                }
            }
        });
        mProgressDialog.show();
        mGetLocationTask = new GetLocationTask();
        mGetLocationTask.execute((Void) null);
        endTime = java.lang.System.currentTimeMillis();
        Log.d(TAG, "5 " + (endTime - startTime));
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

    public class GetLocationTask extends AsyncTask<Void, Void, ResponseMessage> {

        @Override
        protected ResponseMessage doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            ResponseMessage responseMessage = new ResponseMessage();

            try {
                Log.i(TAG, "GetLocationTask imei " + imei);
                responseMessage.body = RestClient.getGpsPosition(
                        mXiaoYunTongApplication.userInfo.id,
                        mXiaoYunTongApplication.userInfo.ticket, imei);// String id,
                                                                       // String ticket,
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
                        LocationInfo locationInfo = locationInfos.get(0);
                        Log.i(TAG, "locationInfo.address " + locationInfo.address);
                        if (locationInfo != null) {
                            if (TextUtils.isEmpty(locationInfo.lat)
                                    || TextUtils.isEmpty(locationInfo.lng)) {
                                Toast.makeText(LocationInfoActivity.this,
                                        R.string.not_location_data, Toast.LENGTH_LONG).show();
                            } else {
                                
                                viewCache = getLayoutInflater().inflate(R.layout.custom_baidu_popo, null);
                                TextView   popoTimeText = (TextView) viewCache.findViewById(R.id.pop_time_value);
                                TextView  popoAddressText = (TextView) viewCache.findViewById(R.id.popo_address_value);
                                popoTimeText.setText(locationInfo.optTime);       
                                popoAddressText.setText(locationInfo.address);
                                double mLat1 = Double.parseDouble(locationInfo.lat);
                                Log.i(TAG, "mLat1 " + mLat1);
                                double mLon1 = Double.parseDouble(locationInfo.lng);
                                Log.i(TAG, "mLon1 " + mLon1);
                                curretGeoPoint = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));
                                Drawable mark = getResources()
                                        .getDrawable(R.drawable.location_info);
                                OverlayItem item = new OverlayItem(curretGeoPoint, "item2", "item2");

                                OverlayTest itemOverlay = new OverlayTest(mark, mMapView);

                                mMapView.getOverlays().clear();
                                mMapView.getOverlays().add(itemOverlay);

                                itemOverlay.addItem(item);
                                mMapController.setZoom(12);// 设置地图zoom级别
                                mMapController.setCenter(curretGeoPoint);// 设置地图中心点
                                mMapView.refresh();
                                
                                MKSearch mMKSearch = new MKSearch(); 
                                mMKSearch.init(mBMapMan, new MKSearchListener(){

                                    @Override
                                    public void onGetAddrResult(MKAddrInfo res, int error) {
                                        // TODO Auto-generated method stub
                                        if (error != 0) {
//                                            String str = String.format("错误号：%d", error);
//                                            Toast.makeText(GeoCoderDemo.this, str, Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        // 地图移动到该点
                                        if (res.type == MKAddrInfo.MK_GEOCODE) {
                                            // 地理编码：通过地址检索坐标点
                                            String strInfo = String.format("纬度：%f 经度：%f", res.geoPt.getLatitudeE6() / 1e6,
                                                    res.geoPt.getLongitudeE6() / 1e6);
                                            Log.i(TAG, "strInfo " + strInfo);
                                            //Toast.makeText(LocationInfoActivity.this, strInfo, Toast.LENGTH_LONG).show();
                                        }
                                        if (res.type == MKAddrInfo.MK_REVERSEGEOCODE) {
                                            // 反地理编码：通过坐标点检索详细地址及周边poi
                                            String strInfo = res.strAddr;
                                            Log.i(TAG, "strInfo " + strInfo);
                                           // Toast.makeText(LocationInfoActivity.this, strInfo, Toast.LENGTH_LONG).show();

                                        }
                                    }

                                    @Override
                                    public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        
                                    }

                                    @Override
                                    public void onGetDrivingRouteResult(MKDrivingRouteResult arg0,
                                            int arg1) {
                                        // TODO Auto-generated method stub
                                        
                                    }

                                    @Override
                                    public void onGetPoiDetailSearchResult(int arg0, int arg1) {
                                        // TODO Auto-generated method stub
                                        
                                    }

                                    @Override
                                    public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
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
                                    public void onGetTransitRouteResult(MKTransitRouteResult arg0,
                                            int arg1) {
                                        // TODO Auto-generated method stub
                                        
                                    }

                                    @Override
                                    public void onGetWalkingRouteResult(MKWalkingRouteResult arg0,
                                            int arg1) {
                                        // TODO Auto-generated method stub
                                        
                                    }
                                    
                                });  
                                mMKSearch.reverseGeocode(curretGeoPoint);

                            }
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

    
   
    /*
     * 要处理overlay点击事件时需要继承ItemizedOverlay 不处理点击事件时可直接生成ItemizedOverlay.
     */
    class OverlayTest extends ItemizedOverlay<OverlayItem> {
        // 用MapView构造ItemizedOverlay
        public OverlayTest(Drawable mark, MapView mapView) {
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

}
