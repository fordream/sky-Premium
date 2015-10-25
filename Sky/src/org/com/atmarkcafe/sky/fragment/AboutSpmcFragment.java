package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;

import org.com.atmarkcafe.service.VideoActivity;
import org.com.atmarkcafe.service.VideoEnabledWebChromeClient;
import org.com.atmarkcafe.service.VideoEnabledWebView;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderSquareUtils;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

public class AboutSpmcFragment extends BaseFragment implements OnClickListener{

	private String TAG = "AboutSpmcFragment";
	private WebView webContent;
	
	private String strTileContent;
	private String strContent = "";
	private HeaderView txtTitleHeader;
	private Setting setting;
	private LoadingView loading;
	private ImageButton btnRight;
	private View btnMenu;
	private ImageView imgBaner;
	private String urlThumb;
	private View views;
	
	@Override
	public int getLayout() {
		return R.layout.about_spmc_layout;
	}

//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		getActivity().getWindow().requestFeature(Window.FEATURE_PROGRESS);
//	}
//	
	@Override
	public void init(View view) {
		views = view;
		loading = CommonAndroid.getView(view, R.id.loading);
		CommonAndroid.showView(false, loading);
		String abc = "<div style=\"position: fixed;width:100%;height:auto\">";
		abc += "<iframe src = \"https://player.vimeo.com/video/141030994\" width= \"100%\" height= \"auto\" > </iframe></div>";
		abc += "<a href=\"https://player.vimeo.com/video/141030994\"><div style= \"width:100%; height:100px;position: relative;background-color : red;\" onclick=\"alert('https://player.vimeo.com/video/141030994');\"></div></a>";
		abc += "<script type=\"text/javascript\">";
		abc += "	if (window.android){";
				abc += "	   var name = window.android.getName();";
						//abc += "	   alert(name);";
						abc += "	}";
		
		abc += "</script>";
		
		//strContent = abc;
		//setContentView();//test
		getData();
		
		/*webContent = (WebView)view.findViewById(R.id.about_webcontent_id);
		webContent.getSettings().setJavaScriptEnabled(true);
		
		webContent.getSettings().setAllowFileAccess(true);
		webContent.getSettings().setPluginsEnabled(true);
		
//		webContent.getSettings().setDomStorageEnabled(true);
//		webContent.getSettings().setAllowFileAccess(true);
//		webContent.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
	     
		//webContent.getSettings().setUseWideViewPort(true);
		//webContent.getSettings().setSupportMultipleWindows(true);
        
		webContent.getSettings().setDefaultTextEncodingName("utf-8"); 
		webContent.setBackgroundColor(Color.parseColor("#ededed"));*/
		imgBaner = (ImageView)view.findViewById(R.id.about_banner_id);
		
		
		btnMenu = (ImageButton) view.findViewById(R.id.header_btn_left);
		btnMenu.setOnClickListener(this);
		
		btnRight = (ImageButton)view.findViewById(R.id.header_btn_right);
		btnRight.setVisibility(View.INVISIBLE);
		
		setting = new Setting(getActivity());
		txtTitleHeader = CommonAndroid.getView(view,R.id.contact_header_id);
		txtTitleHeader.initHeader(R.string.menu_about_spmc, R.string.menu_about_spmc_j);
		
	}

	@Override
	public void onChangeLanguage() {
	Log.i(TAG, "Changelanguage");
	getData();
	}
	
	@Override
	public void onClick(View v) {
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			SkyMainActivity.sm.toggle();
			break;

		default:
			break;
		}
		super.onClick(v);
	}

	private void getData(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("req[param][slug]", "about-sky-premium");
		params.put("req[param][type]", "post");
		SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_SERVICE_ABOUT, params, new CheerzServiceCallBack(){
			@Override
			public void onStart() {
				CommonAndroid.showView(true, loading);
				super.onStart();
			}
			
			@Override
			public void onError(String message) {
				CommonAndroid.showView(false, loading);
				if (setting.isLangEng()) {
					SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure), null);
				}else{
					SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure_j), null);
				}
				super.onError(message);
			}
			
			@Override
			public void onSucces(String response) {
				CommonAndroid.showView(false, loading);
//				Log.i(TAG,"data = " + response.toString());
				try {
					JSONObject jsonRes = new JSONObject(response);
					JSONArray jsonArray = jsonRes.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jsonObj = jsonArray.getJSONObject(i);
						strTileContent = jsonObj.getString("title");
						strContent = jsonObj.getString("content");
						urlThumb = jsonObj.getString("thumbnail");
					}
					Log.i(TAG, "content ===== " + strContent);
					if(!"".equals(strContent) && !strContent.equalsIgnoreCase("null")){
						
						setContentView();
						//webView.loadUrl("file:///android_asset/test4.html");
						
						/*strContent = "<iframe   src=\"https://player.vimeo.com/video/141030994\"  width=\"100%\"  height=\"auto\"  frameborder=\"0\" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>";
						strContent = SkyUtils.addStyleContent(strContent);
						webContent.setOnTouchListener(new View.OnTouchListener() {

					        public boolean onTouch(View v, MotionEvent event) {
					            WebView.HitTestResult hr = ((WebView)v).getHitTestResult();
					            Log.i(TAG, "getExtra = "+ hr.getExtra() + "\t\t Type=" + hr.getType() + "\t\t" + hr.toString());
					            return false;
					        }
					    });
						webContent.setWebChromeClient(new WebChromeClient(){
							
						});
						
						webContent.setWebViewClient(new WebViewClient(){
							@Override
							public boolean shouldOverrideUrlLoading(WebView view, String url) {
								LogUtils.i(TAG, "=== Back Back Bacm url = " + url);
								Log.i(TAG, "URL = " + url);
								
								return true;
							}
						});
						
						webContent.loadDataWithBaseURL(null, strContent, "text/html", "utf-8", null);*/
						
						
					}
					
//					ImageLoaderSquareUtils.getInstance(getActivity()).displayImageAbout(urlThumb, imgBaner);
					ImageLoaderSquareUtils.getInstance(getActivity()).displayImageHomeSlide(urlThumb, imgBaner);
				} catch (Exception e) {
					// TODO: handle exception
				}
				super.onSucces(response);
			}
			
		});
	}
	
	private void setContentView(){
		LogUtils.i(TAG, "AAAAAAAAAAAAAAAAAAAAAAAAAAA==");
		strContent = SkyUtils.addStyleContent(strContent);
		strContent = strContent.replaceAll("</ ", "</");
		strContent = strContent.replaceAll("fixed", "absolute");
//		strContent = strContent.replaceAll('  width="100%" 500"  height="auto" 281" frameborder="0', "'");
//		for(int i= 0; i < strContent.length(); i++){
//			LogUtils.i(TAG, "ABC==" + );
//		}
		webContent = (WebView) views.findViewById(R.id.about_webcontent_id);
		webContent.getSettings().setJavaScriptEnabled(true);
		webContent.getSettings().setAllowFileAccess(true);
		//webContent.getSettings().setPluginsEnabled(true);
		webContent.getSettings().setDefaultTextEncodingName("utf-8"); 
		webContent.setBackgroundColor(Color.parseColor("#ededed"));
		webContent.addJavascriptInterface(new JavaScriptInterface(), "android");
		webContent.setWebChromeClient(new WebChromeClient(){
			
		});
		
		webContent.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtils.i(TAG, "=== Back Back Bacm url = " + url);
				Log.i(TAG, "URL = " + url);
				
				return true;
			}
		});
//		webContent.loadDataWithBaseURL("file:///android_asset/", strContent, "text/html", "utf-8", null);
		
//		String str = strContent;
		LogUtils.i(TAG, "=== Back Back Bacm url = " + strContent);
		//str += CommonAndroid.loadResouceHTMLFromAssets(getActivity(),"test3.html");
		CommonAndroid.writeTextToFile(strContent);
		webContent.loadDataWithBaseURL("file:///android_asset/", strContent , "text/html", "utf-8", null);
		
//		product_descreption2.html
//		webContent.loadUrl("file:///android_asset/product_descreption2.html");
//		webContent.loadUrl("file:///android_asset/test3.html");
		LogUtils.i(TAG, "BBBBBBBBBBBBBBBBBBBBBBBBBBBB==");
		
		
		/*webView = (VideoEnabledWebView) views.findViewById(R.id.about_webcontent_id);
		webView.addJavascriptInterface(new JavaScriptInterface(), "android");
		View nonVideoLayout = views.findViewById(R.id.nonVideoLayout); 
		ViewGroup videoLayout = (ViewGroup) views.findViewById(R.id.videoLayout); 
		View loadingView = getActivity().getLayoutInflater().inflate(R.layout.view_loading_video, null); 
		webChromeClient = new VideoEnabledWebChromeClient(nonVideoLayout, videoLayout, loadingView, webView) // See
		{
			@Override
			public void onProgressChanged(WebView view, int progress) {
			}
		};
		webChromeClient.setOnToggledFullscreen(new VideoEnabledWebChromeClient.ToggledFullscreenCallback() {
			@Override
			public void toggledFullscreen(boolean fullscreen) {
				try {
					if (fullscreen) {
					//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
						WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
						attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
						attrs.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
						getActivity().getWindow().setAttributes(attrs);
						if (android.os.Build.VERSION.SDK_INT >= 14) {
							getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
						}
					} else {
						WindowManager.LayoutParams attrs = getActivity().getWindow().getAttributes();
						attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
						attrs.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
						getActivity().getWindow().setAttributes(attrs);
						if (android.os.Build.VERSION.SDK_INT >= 14) {
							getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			}
		});
		
		webView.setWebChromeClient(webChromeClient);
		String str = "";
//		str += CommonAndroid.loadResouceHTMLFromAssets(getActivity(),"about_detail_header.html");
		str = strContent;
//		str += CommonAndroid.loadResouceHTMLFromAssets(getActivity(),"about_detail_footer.html");
		webView.loadDataWithBaseURL("file:///android_asset/", str, "text/html", "UTF-8", null);
//		webView.loadUrl("file:///android_asset/test3.html");
*/		
	}
	
	VideoEnabledWebView webView;
	VideoEnabledWebChromeClient webChromeClient;

	@Override
	public boolean onBackPressed(Bundle extras) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onBack");
		
		return super.onBackPressed(extras);
	}
	String name = "John";
	String lastName = "Doe";
	private class JavaScriptInterface {
		@JavascriptInterface
		public String getName() {
	        return name;
	    }
	    @JavascriptInterface
		public String getLastName() {
	        return lastName;
	    }
	    @JavascriptInterface
		public void onJavascriptPlayVideo(String link) {
	    	LogUtils.i(TAG, "xxxxxxxxxxxxxxxxxxxxxx==" + link);
	       	Intent intent = new Intent(getActivity(), VideoActivity.class);
	       	intent.putExtra("contentview_url", link);
			startActivityForResult(intent, 0);
	    }
	}
	
}


