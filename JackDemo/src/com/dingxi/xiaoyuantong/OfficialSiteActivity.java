package com.dingxi.xiaoyuantong;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class OfficialSiteActivity extends Activity {

	WebView myWebView;
	ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_site);

        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.loadUrl("http://112.90.87.82:8090/");
        myWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    
    private class MyWebViewClient extends WebViewClient {
//        @Override
//        public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//
//            return true;
//        }
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
        	// TODO Auto-generated method stub
        	super.onPageStarted(view, url, favicon);
        	
        	 dialog = ProgressDialog.show(OfficialSiteActivity.this,null,"页面加载中，请稍后..");  
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
        	// TODO Auto-generated method stub
        	super.onPageFinished(view, url);
        	if(dialog!=null && dialog.isShowing()){
        	    dialog.dismiss(); 
        	}
        	 
        }
    }

}
