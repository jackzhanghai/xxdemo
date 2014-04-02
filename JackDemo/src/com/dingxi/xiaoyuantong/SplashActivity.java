package com.dingxi.xiaoyuantong;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.dingxi.xiaoyuantong.model.SchoolInfo;
import com.dingxi.xiaoyuantong.network.JSONParser;
import com.dingxi.xiaoyuantong.network.ResponseMessage;
import com.dingxi.xiaoyuantong.network.RestClient;

public class SplashActivity extends Activity {

	protected static final String TAG = "SplashActivity";
	private ImageView splashImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_splash);

		splashImageView = (ImageView) this.findViewById(R.id.id_splash);

		/**
		 * 设置3*1000ms动画，动画结束进入StartActivity界面
		 */
		AlphaAnimation aa = new AlphaAnimation(0.5f, 1.0f);
		aa.setDuration(3 * 1000);
		splashImageView.startAnimation(aa);

		aa.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			    
			    

				new Thread(new Runnable() {

					@Override
					public void run() {
						String schoolsInfo = null;
						try {
							schoolsInfo = RestClient.getAllSchool();
							Log.d(TAG, "result " + schoolsInfo);
							if (JSONParser.getIntByTag(schoolsInfo,
									ResponseMessage.RESULT_TAG_CODE) == ResponseMessage.RESULT_TAG_SUCCESS) {

								ArrayList<SchoolInfo> schools = JSONParser
										.toParserSchoolList(schoolsInfo);
							} else {
								String errorMessage = JSONParser
										.getStringByTag(schoolsInfo,
												ResponseMessage.RESULT_TAG_MESSAGE);
							}

						} catch (JSONException e) {
							Log.e(TAG, "JSONException " + e.getMessage());
							e.printStackTrace();
						} catch (XmlPullParserException e) {
							Log.e(TAG, "XmlPullParserException " + e.getMessage());
							e.printStackTrace();
						} catch (IOException e) {
							Log.e(TAG, "IOException " +  e.getMessage());
							e.printStackTrace();
						} finally {
							Message message = Message.obtain();
							message.obj = schoolsInfo;
							mHideHandler.sendMessage(message);
						}
					}
				}).start();

			}
		});
		
	}

	Handler mHideHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			Intent loginIntent = new Intent(SplashActivity.this,
					LoginActivity.class);
			loginIntent.putExtra("schoolInfo", (String) msg.obj);
			startActivity(loginIntent);

			finish();
		}

	};

}
