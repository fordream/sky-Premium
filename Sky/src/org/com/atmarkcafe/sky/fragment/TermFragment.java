package org.com.atmarkcafe.sky.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;
import android.view.View;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageButton;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

public class TermFragment extends BaseFragment{

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

		btnHome.setOnClickListener(this);

		btnRight = (ImageButton) view.findViewById(R.id.header_btn_right);
		btnRight.setVisibility(View.INVISIBLE);

		txtTile = CommonAndroid.getView(view, R.id.header_id);
		setting = new Setting(getActivity());
		updateTitle();
		getContentTerm();
	}

	@Override
	public void onChangeLanguage() {
		// TODO Auto-generated method stub
		updateTitle();
		getContentTerm();
	}
	
	private void updateTitle(){
		if (setting.isLangEng()) {
			txtTile.setTextHeader(R.string.help_term);
		}else{
			txtTile.setTextHeader(R.string.help_term_j);
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
	
	private void getContentTerm(){
		
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("req[param][slug]", "terms-and-conditions-of-use");
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
					// TODO Auto-generated catch block
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
