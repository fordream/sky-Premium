package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;
import java.util.Map;

import org.com.atmarkcafe.object.NewsObject;
import org.com.atmarkcafe.sky.GcmBroadcastReceiver;
import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.customviews.charting.MTextView;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

@SuppressLint("ValidFragment")
public class NewsDetailFragment extends BaseFragment implements OnClickListener{

	private String content;
	private NewsObject newObj;
	private ImageButton btn1;
	private ImageButton btn2;
	private MTextView txtTitleHeader;
	private TextView txtTitleEmail,txtDateEmail;
	private WebView webview;
	private ImageButton btnBack;
	private String TAG = "NewsDetailFragment";
	private Setting setting;
	private LoadingView loading;
	private SharedPreferences sharePref;
	private LinearLayout header_view;
	public NewsDetailFragment(NewsObject obj){
		this.newObj = obj;
	}
	
	@Override
	public int getLayout() {
		
		return R.layout.news_detail_fragment;
	}

	@Override
	public void init(View view) {
		header_view = (LinearLayout) view.findViewById(R.id.header_view);
		btn1 = (ImageButton) view.findViewById(R.id.header_btn_right);
		btn1.setVisibility(View.INVISIBLE);
		
		btn2 = (ImageButton) view.findViewById(R.id.header_btn_right_left);
		btn2.setVisibility(View.INVISIBLE);
		
		txtTitleHeader = (MTextView) view.findViewById(R.id.header_title);
		txtTitleEmail = (TextView)view.findViewById(R.id.news_detail_titlecontent_id);
		txtDateEmail = (TextView)view.findViewById(R.id.news_detail_titledate_id);
		
		setting = new Setting(getActivity());
		if (setting.isLangEng()) {
			txtTitleHeader.setText(getActivity().getResources().getString(R.string.menu_news));
		}else{
			txtTitleHeader.setText(getActivity().getResources().getString(R.string.menu_news_j));
		}
		loading = (LoadingView)view.findViewById(R.id.loading);
		CommonAndroid.showView(false, loading);
		
		txtTitleEmail.setText(newObj.getTitle());
		txtDateEmail.setText(newObj.getTime());
		
		getDataNews();
		
		webview = (WebView)view.findViewById(R.id.news_detail_content_id);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDefaultTextEncodingName("utf-8");  
		
		//webview.loadDataWithBaseURL(null, newObj.getContent(), "text/html", "utf-8", null);
		
		btnBack = (ImageButton)view.findViewById(R.id.header_btn_left);
		btnBack.setImageResource(R.drawable.icon_back);
		btnBack.setOnClickListener(this);
		// update badge
		if (newObj.getNewsStatus() == 1) {
			/*sharePref = getActivity().getSharedPreferences(SkyUtils.shareKey, Context.MODE_PRIVATE);
			int countBadgeNews = sharePref.getInt(SkyUtils.BADGE_NEWS, 0);
			countBadgeNews -= 1;
			sharePref.edit().putInt(SkyUtils.BADGE_NEWS, countBadgeNews).commit();
//			GcmBroadcastReceiver.setBadgeDevice(getActivity(), type, countBadgeNews);
			 CommonAndroid.updateBadgeLauncher(getActivity(), sharePref.getInt(SkyUtils.BADGE_EMAIL, 0) + sharePref.getInt(SkyUtils.BADGE_NEWS, 0));*/
			GcmBroadcastReceiver.notifiServiceUpdate(getActivity(),1,2, false, true);
			 updateBadge();
		}
	}	
	
	private void updateBadge(){
		HashMap<String, String>params = new HashMap<String, String>();
		params.put("type", String.valueOf(2));
		SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_UPDATE_BADGE, params, new CheerzServiceCallBack(){
			@Override
			public void onError(String message) {
				super.onError(message);
				LogUtils.e(TAG, "updateBadge : " + message);
			}
			
			@Override
			public void onSucces(String response) {
				
				super.onSucces(response);
				LogUtils.i(TAG, "updateBadge : " + response);
			}
			
		});
	}

	@Override
	public void onClick(View v) {
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			gotoNewsFragment();
			break;

		default:
			break;
		}
		super.onClick(v);
	}
	
	private void gotoNewsFragment() {
		NewsFragment newsFragment = new NewsFragment();
		if (getActivity() instanceof SkyMainActivity) {
			SkyMainActivity fca = (SkyMainActivity) getActivity();
			fca.switchContent(newsFragment, "");
		}	
		
	}
	
	private void getDataNews(){
		Map<String, String> params = new HashMap<String, String>();
		params.put("req[param][id]", String.valueOf(newObj.getId()));
		SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_SERVICE_NEWS_DETAIL, params, new CheerzServiceCallBack(){
			
			

			@Override
			public void onStart() {
				CommonAndroid.showView(true, loading);
				super.onStart();
			}
			
			@Override
			public void onError(String message) {
				if (!isFinish()) {
					CommonAndroid.showView(false, loading);
					if (setting.isLangEng()) {
						SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure), null);
					}else{
						SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure_j), null);
					}
					super.onError(message);
				}
				
			}
			
			@Override
			public void onSucces(String response) {
				if (!isFinish()) {
					LogUtils.i(TAG, "Data = " + response);
					CommonAndroid.showView(false, loading);
					
					try {
						response = GlobalFunction.formatRespone(response);
						JSONObject jsonRes = new JSONObject(response);
						JSONObject jsonData = jsonRes.getJSONObject("data");
						String str = jsonData.getString("content");
						String content = "";
						if (str.equalsIgnoreCase("") || str == null) {
							str = " ";
						}
						content = SkyUtils.addStyleContentNewDetail(str);
						
//						webview.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
						webview.loadDataWithBaseURL(SkyUtils.API.BASESERVER_, content, "text/html", "utf-8", null);
						webview.setWebChromeClient(new WebChromeClient() {
						});

						webview.setLongClickable(false);
						webview.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								return true;
							}
						});

						webview.setWebViewClient(new WebViewClient() {
							public boolean shouldOverrideUrlLoading(WebView view, String url) {
								LogUtils.i(TAG, "Data url= " + url);
								if (url.contains("tel")) {
									openDialer(url);
								}
								if (url.contains("mailto")) {
									openEmail(url);
								}
								if(url.contains("http")){
									if(url.endsWith("pdf") || url.endsWith("PDF") ){
										openBrowserDefault(url);
									}else{
										header_view.setVisibility(View.GONE);
										webview.loadUrl(url);
									}
									
								}
								return false;
							}

							public void onPageFinished(WebView view, String url) {
								return;
							}

						});
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					super.onSucces(response);
				}
				
			}
			
		});
	}

	public void openBrowserDefault(String url){
		Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
		getActivity().startActivity(browserIntent);
	}
	
	private void openDialer(String phoneNumber){
		if (SkyUtils.isSimExist(getActivity())) {
			Intent intent = new Intent(Intent.ACTION_DIAL);
			intent.setData(Uri.parse(phoneNumber));
			startActivity(intent);	
		}else{
			if (setting.isLangEng()) {
				SkyUtils.showDialog(getActivity(), getResources().getString(R.string.call_error), null);
			}else{
				SkyUtils.showDialog(getActivity(), getResources().getString(R.string.call_error_j), null);
			}
		}
		 
	}
	private void openEmail(String url){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data = Uri.parse(url);
		intent.setData(data);
		startActivity(intent);
	}
	
	@Override
	public void onChangeLanguage() {
		
		
	}

}
