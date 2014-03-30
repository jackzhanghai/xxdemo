package com.dingxi.jackdemo;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.dingxi.jackdemo.model.LocationInfo;
import com.dingxi.jackdemo.network.JSONParser;
import com.dingxi.jackdemo.network.ResponseMessage;
import com.dingxi.jackdemo.network.RestClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class LocationInfoActivity extends Activity {

	final static String TAG = "LocationInfoActivity";
	
	BMapManager mBMapMan = null;//
	MapView mMapView = null;
	String key = "GPaW4uhS7hx62Zgtm6ZrP45j";
	GetLocationTask mGetLocationTask;
	private XiaoYunTongApplication mXiaoYunTongApplication;
	private ProgressDialog mProgressDialog;
	private String imei;
    MapController mMapController;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mXiaoYunTongApplication = (XiaoYunTongApplication) getApplication();
        mBMapMan = new BMapManager(getApplication());
        mBMapMan.init(key, null);  
        //注意：请在试用setContentView前初始化BMapManager对象，否则会报错
        setContentView(R.layout.activity_location_info);
        mMapView=(MapView)findViewById(R.id.bmapView);
        mMapView.setBuiltInZoomControls(true);
        
        imei =  getIntent().getStringExtra("imei");
        //设置启用内置的缩放控件
        mMapController = mMapView.getController();
        // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
        GeoPoint point =new GeoPoint((int)(39.915* 1E6),(int)(116.404* 1E6));
        //用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
        mMapController.setCenter(point);//设置地图中心点
        mMapController.setZoom(12);//设置地图zoom级别
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

    }
    
    @Override
    protected void onDestroy(){
            mMapView.destroy();
            if(mBMapMan!=null){
                    mBMapMan.destroy();
                    mBMapMan=null;
            }
            super.onDestroy();
    }
    @Override
    protected void onPause(){
            mMapView.onPause();
            if(mBMapMan!=null){
                   mBMapMan.stop();
            }
            super.onPause();
    }
    @Override
    protected void onResume(){
            mMapView.onResume();
            if(mBMapMan!=null){
                    mBMapMan.start();
            }
           super.onResume();
    }


    public class GetLocationTask extends AsyncTask<Void, Void, ResponseMessage> {
    	
    	
        @Override
        protected ResponseMessage doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
        	ResponseMessage responseMessage  = new ResponseMessage();

            try {
                Log.i(TAG, "GetLocationTask imei "  + imei);
                responseMessage.body = RestClient.getGpsPosition(mXiaoYunTongApplication.userInfo.id, mXiaoYunTongApplication.userInfo.ticket, imei);//String id, String ticket, String imei
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
            }  catch (JSONException e) {
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
            
            if(responseMessage.code == ResponseMessage.RESULT_TAG_SUCCESS  && responseMessage.total >0){
            	

            		ArrayList<LocationInfo> locationInfos;
					try {
						locationInfos = JSONParser.toParserLocationInfo(responseMessage.body);
						LocationInfo locationInfo = locationInfos.get(0);
					       
	            		
	            		if(locationInfo!=null ){
	            			if(TextUtils.isEmpty(locationInfo.lat) || TextUtils.isEmpty(locationInfo.lng)){
	                            Toast.makeText(LocationInfoActivity.this, R.string.not_location_data
	                                    ,Toast.LENGTH_LONG).show();
	                        } else {
	                            double mLat1 = Double.parseDouble(locationInfo.lat);
	                            double mLon1 = Double.parseDouble(locationInfo.lng);
	                            GeoPoint p2 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));  
	                            Drawable mark= getResources().getDrawable(R.drawable.location_info);  
	                            OverlayItem item = new OverlayItem(p2,"item2","item2"); 
	      
	                            OverlayTest itemOverlay = new OverlayTest(mark, mMapView); 
	                            
	                            mMapView.getOverlays().clear(); 
	                            mMapView.getOverlays().add(itemOverlay);
	                            
	                            itemOverlay.addItem(item);           
	                            GeoPoint point =new GeoPoint((int)(mLat1 * 1E6),(int)(mLon1* 1E6));
	                            mMapController.setCenter(point);//设置地图中心点
	                            mMapView.refresh();

	                        }
	            		} else {
	            			Toast.makeText(LocationInfoActivity.this, R.string.not_location_data
	                                ,Toast.LENGTH_LONG).show();
	            		}
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            		

            	 mProgressDialog.dismiss();
            }else {
            	 mProgressDialog.dismiss();
            	Toast.makeText(LocationInfoActivity.this, responseMessage.message
                        ,Toast.LENGTH_LONG).show();
            	
            }
            
         
        }


        @Override
        protected void onCancelled() {
            mGetLocationTask = null;

        }
    }
    
    

    /*
     * 要处理overlay点击事件时需要继承ItemizedOverlay
     * 不处理点击事件时可直接生成ItemizedOverlay.
     */
    class OverlayTest extends ItemizedOverlay<OverlayItem> {
        //用MapView构造ItemizedOverlay
        public OverlayTest(Drawable mark,MapView mapView){
                super(mark,mapView);
        }
        protected boolean onTap(int index) {
            //在此处理item点击事件
            System.out.println("item onTap: "+index);
            return true;
        }
            public boolean onTap(GeoPoint pt, MapView mapView){
                    //在此处理MapView的点击事件，当返回 true时
                    super.onTap(pt,mapView);
                    return false;
            }
            // 自2.1.1 开始，使用 add/remove 管理overlay , 无需重写以下接口
            /*
            @Override
            protected OverlayItem createItem(int i) {
                    return mGeoList.get(i);
            }
           
            @Override
            public int size() {
                    return mGeoList.size();
            }
            */
    }  
    
}
