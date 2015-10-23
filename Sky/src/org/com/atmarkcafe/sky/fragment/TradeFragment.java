package org.com.atmarkcafe.sky.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONException;
import org.json.JSONObject;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;

public class TradeFragment extends BaseFragment{

	protected static final String TAG = "TradeFragment";
	private WebView mWebview;
	private Setting setting;
	private LoadingView loading;
	private ImageButton btnHome,btnRight;
	private HeaderView txtTile;
	private String mTitle;
	@Override
	public int getLayout() {
		// TODO Auto-generated method stub
		return R.layout.term_service_list_layout;
	}

	@Override
	public void init(View view) {
		// TODO Auto-generated method stub
		loading = (LoadingView) view.findViewById(R.id.loading);
		CommonAndroid.showView(false, loading);
		
		mWebview = (WebView)view.findViewById(R.id.term_service_webview_id);
		mWebview.getSettings().setJavaScriptEnabled(true);
		mWebview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);

		btnHome = (ImageButton) view.findViewById(R.id.header_btn_left);
//		btnHome.setImageResource(R.drawable.menu_icon);
		btnHome.setOnClickListener(this);

		btnRight = (ImageButton) view.findViewById(R.id.header_btn_right);
		btnRight.setVisibility(View.INVISIBLE);

		txtTile = CommonAndroid.getView(view, R.id.header_id);
		setting = new Setting(getActivity());
		updateTitle();
		getContent();
		
	}

	@Override
	public void onChangeLanguage() {
		// TODO Auto-generated method stub
		updateTitle();
		getContent();
	}
	
	private void updateTitle(){
		if (setting.isLangEng()) {
			txtTile.setTextHeader(R.string.help_trade_law);
		}else{
			txtTile.setTextHeader(R.string.help_trade_law_j);
		}
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			SkyMainActivity.sm.toggle();
			break;

		default:
			break;
		}
	}
	
	private void getContent(){
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("req[param][slug]", "trade-law");
		SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_HELP, params, new CheerzServiceCallBack(){
			@Override
			public void onStart() {
				CommonAndroid.showView(true, loading);
				super.onStart();
			}
			
			@Override
			public void onError(String message) {
				CommonAndroid.showView(false, loading);
				super.onError(message);
				LogUtils.i(TAG, "Error = "+ message);
				CommonAndroid.showDialog(getActivity(), message, null);
			}
			
			@Override
			public void onSucces(String response) {
				super.onSucces(response);
				CommonAndroid.showView(false, loading);
				try {
					JSONObject jsonRes = new JSONObject(response);
					JSONObject jsonData = jsonRes.getJSONObject("data");
					String content = jsonData.getString("content");
					String mTitle = jsonData.getString("title");
					processData(content);
					//txtTile.initHeader(mTitle, mTitle);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private void processData(String content) {
		content = content.replace("width=\"1280\"", "width=\"100%\"");
		String str = "";
		str += loadResouceHTMLFromAssets("html/project_term_header.html");
		if (content != null) {
			str += content;
		}
		str += loadResouceHTMLFromAssets("html/project_term_footer.html");
		mWebview.loadDataWithBaseURL("file:///android_asset/", str, "text/html",
				"UTF-8", null);
		mWebview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtils.i(TAG, "url==" + url);
				if (url.startsWith("tel:") ){
					openActionCall(url);
				}else if(url.startsWith("mailto:")) { 
					sendEmail(url);
		        }else if(url.startsWith("http:") || url.startsWith("https:")) {
		        	openBrowserDefault(url);
		        }
		        return true;
			}

			public void onPageFinished(WebView view, String url) {
				return;
			}

		});
	}
	
	protected void sendEmail(String mailto) {
		String temp_TO = mailto.replaceAll("mailto:", "");
		String[] TO = { temp_TO };
		String[] CC = { temp_TO };
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse(mailto));
		emailIntent.setType("text/plain");

		emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
		emailIntent.putExtra(Intent.EXTRA_CC, CC);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SKY PREMIUM");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");
		emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		try {
			getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			//finish();
			LogUtils.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	
	public void openBrowserDefault(String url){
		Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse(url));
		getActivity().startActivity(browserIntent);
	}
	
	private void openActionCall(String url) {
		if (SkyUtils.isSimExist(getActivity())) {
			try {
				Intent intenCall = new Intent(Intent.ACTION_DIAL);//ACTION_CALL
				intenCall.setData(Uri.parse(url));
				getActivity().startActivity(intenCall);
			} catch (Exception e) {}
		}else{
			if (new Setting(getActivity()).isLangEng()) {
				SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.call_error), null);
			}else{
				SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.call_error_j), null);
			}
		}
	}

	public String loadResouceHTMLFromAssets(String filename) {
		String tmp = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getActivity()
					.getAssets().open(filename)));
			String word;
			while ((word = br.readLine()) != null) {
				if (!word.equalsIgnoreCase("")) {
					tmp += word;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close(); // stop reading
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return tmp;
	}

}
