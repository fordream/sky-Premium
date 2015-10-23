package org.com.atmarkcafe.service;

import org.com.atmarkcafe.sky.SkyMainActivity;

import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;

@SuppressLint("NewApi")
public class VideoActivity extends Activity {
	private static final String TAG = "VideoActivity";
	private String ContentView = "";
	private String contentview_url = "";
	private String gallery_detail = "0";
	private LoadingView loading;
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
	    super.onSaveInstanceState(outState);
	 
	    // Save the state of the WebView
	    webView.saveState(outState);
	}
	   
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
	    super.onRestoreInstanceState(savedInstanceState);
	 
	    // Restore the state of the WebView
	    webView.restoreState(savedInstanceState);
	}
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
	                            WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    
		LogUtils.i(TAG, "url===kkkkkkkkkkkkkkkk" + ContentView);
		getWindow().requestFeature(Window.FEATURE_PROGRESS);
//		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		setContentView(R.layout.video_activity_main);
		loading = (LoadingView) findViewById(R.id.loading);//CommonAndroid.getView(view, R.id.loading);
		try {
			String get_str = (String) getIntent().getExtras().get("contentview");
			if(!"null".equals(get_str.toString()))
				ContentView = get_str;
		} catch (Exception e1) {}
		
		try {
			String get_str =  (String) getIntent().getExtras().get("contentview_url");
			if(!"null".equals(get_str.toString()))
				contentview_url = get_str;
		} catch (Exception e1) {}
		
		try {
			String get_str = (String) getIntent().getExtras().get("gallery_detail");
			if(!"null".equals(get_str.toString()))
				gallery_detail = get_str;
		} catch (Exception e1) {}
		// Save the web view
		webView = (VideoEnabledWebView) findViewById(R.id.webView);
		/*webView.getSettings().setAppCacheEnabled(true);
		webView.getSettings().setDomStorageEnabled(true);

		// how plugin is enabled change in API 8
		if (Build.VERSION.SDK_INT < 8) {
			webView.getSettings().setPluginsEnabled(true);
		} else {
			webView.getSettings().setPluginState(PluginState.ON);
		}*/
		// Initialize the VideoEnabledWebChromeClient and set event handlers
		View nonVideoLayout = findViewById(R.id.nonVideoLayout);
		ViewGroup videoLayout = (ViewGroup) findViewById(R.id.videoLayout);
		View loadingView = getLayoutInflater().inflate(R.layout.view_loading_video, null);
		webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView)
		{
			// Subscribe to standard events, such as onProgressChanged()...
			@Override
			public void onProgressChanged(WebView view, int progress) {
				if(progress >= 95)
					CommonAndroid.showView(false, loading);
			}
		};
		
		/*WindowManager.LayoutParams attrs = getWindow().getAttributes();
		attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		getWindow().setAttributes(attrs);
		if (android.os.Build.VERSION.SDK_INT >= 14) {
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		}
		*/
		/*webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
			@Override
			public void toggledFullscreen(boolean fullscreen) {
				// Your code to handle the full-screen change, for example
				// showing and hiding the title bar. Example:
				if (fullscreen) {
					
					WindowManager.LayoutParams attrs = getWindow().getAttributes();
					attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
					attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
					getWindow().setAttributes(attrs);
					if (android.os.Build.VERSION.SDK_INT >= 14) {
						getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
					}
				} else {
					
					WindowManager.LayoutParams attrs = getWindow().getAttributes();
					attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
					attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
					getWindow().setAttributes(attrs);
					if (android.os.Build.VERSION.SDK_INT >= 14) {
						getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
					}
				}

			}
		});*/
		
		webView.setWebChromeClient(webChromeClient);

		// Navigate everywhere you want, this classes have only been tested on
		// YouTube's mobile site
//		webView.loadUrl("file:///android_asset/test3.html");//http://m.youtube.com
		
		//if( !"".equals(ContentView)){
		if (savedInstanceState == null)
	    {
			CommonAndroid.showView(true, loading);
			if(!"".equals(contentview_url)){
//				contentview_url = contentview_url + "?player_id=player&autoplay=1&title=0&byline=0&portrait=0&api=1";
				LogUtils.i(TAG, "url1===" + contentview_url);
				webView.loadUrl(contentview_url);
			}
			else if(!"".equals(ContentView)){
				ContentView = SkyUtils.addStyleContent(ContentView);
				ContentView = ContentView.replaceAll("</ ", "</");
				LogUtils.i(TAG, "url2===" + ContentView);
				webView.loadDataWithBaseURL("file:///android_asset/", ContentView, "text/html", "UTF-8", null);
			}
	    }else{
	    	CommonAndroid.showView(false, loading);
	    }
		    
		
				
		//}

//		webView.loadUrl("http://m.youtube.com");
//		webView.loadUrl("https://player.vimeo.com/video/141030994");
		
	}

	VideoEnabledWebView webView;
	VideoEnabledWebChromeClient webChromeClient;

	@Override
	public void onBackPressed() {
		// Notify the VideoEnabledWebChromeClient, and handle it ourselves if it
		// doesn't handle it
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		if (!webChromeClient.onBackPressed()) {
			if (webView.canGoBack()) {
				webView.goBack();
			} else {
				// Close app (presumably)
//				super.onBackPressed();
		    	
		    	Intent intent = new Intent();
		    	if("1".equals(gallery_detail)){
		    		intent.putExtra("videoback", SkyMainActivity.FLAG_CHECK_OPEN_GALLERY);
		    	}else{
		    		intent.putExtra("videoback", SkyMainActivity.FLAG_CHECK_OPEN_ABOUT);
		    	}
				setResult(RESULT_OK, intent);
				finish();
			}
		}
	}
}
