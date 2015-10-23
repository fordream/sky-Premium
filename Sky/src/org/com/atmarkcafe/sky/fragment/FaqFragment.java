package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;

import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.customviews.charting.MTextView;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;


public class FaqFragment extends BaseFragment implements OnClickListener{

	private WebView webview;
	private String content;
	private ImageButton btnRight;
	private HeaderView txtTitleHeader;
	private Setting setting;
	private LoadingView loading;
	private ImageButton btnMenu;
	private MTextView txtBack;
	@Override
	public void onCreate(Bundle savedInstanceState) {
//		if(getActivity() instanceof SkyMainActivity) {
//		    if (getActivity().getActionBar().isShowing()) getActivity().getActionBar().hide();
//		} 
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public int getLayout() {
	
		return R.layout.faq_layout;
	}

	@Override
	public void init(View view) {

		webview = (WebView)view.findViewById(R.id.faq_webview_id);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setDefaultTextEncodingName("utf-8");     
		loading = (LoadingView)view.findViewById(R.id.loading_id);
		CommonAndroid.showView(false, loading);
		getData();
		
		btnMenu = (ImageButton) view.findViewById(R.id.header_btn_left);
		btnMenu.setOnClickListener(this);
		btnRight = (ImageButton)view.findViewById(R.id.header_btn_right);
		btnRight.setVisibility(View.INVISIBLE);
		
		txtTitleHeader = CommonAndroid.getView(view, R.id.header_faq_id);
		setting = new Setting(getActivity());
		txtTitleHeader.initHeader(R.string.menu_faq, R.string.menu_faq_j);
		
		txtBack = (MTextView)view.findViewById(R.id.faq_btn_back_id);
		txtBack.setOnClickListener(this);
	}

	@Override
	public void onChangeLanguage() {
		getData();
		
	}

	private void getData(){
		HashMap<String , String > params = new HashMap<String, String>();
		params.put("req[param][id]", "");
		params.put("req[param][slug]", "faq");
		params.put("req[param][type]", "post");
		SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_SERVICE_FAQ, params, new CheerzServiceCallBack(){
			
			

			@Override
			public void onStart() {
				CommonAndroid.showView(true, loading);
				super.onStart();
			}
			
			@Override
			public void onError(String message) {
				CommonAndroid.showView(false, loading);
				super.onError(message);
			}
			
			@Override
			public void onSucces(String response) {
				System.out.println(response.toString());
				CommonAndroid.showView(false, loading);
//				writeFile(response.toString());
				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jObj = jsonArray.getJSONObject(i);
						String content = jObj.getString("content");
						if (content == null || content.equalsIgnoreCase("")) {
							content = "";
						}
						content = SkyUtils.addStyleContent(content);
						webview.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
						webview.setWebViewClient(new WebViewClient() {
							public boolean shouldOverrideUrlLoading(WebView view, String url) {
								LogUtils.i("AAAAAAAAAAAA", "url==" + url);
								if (url.startsWith("tel:") ){
									openActionCall(url);
								}else if(url.startsWith("mailto:")) { 
									sendEmail(url);
						        }else if(url.startsWith("http:") || url.startsWith("https:")) {
//						        	url = "http://docs.google.com/gview?embedded=true&url="+url;
						        	openBrowserDefault(url);
						        }
						        return true;
							}

							public void onPageFinished(WebView view, String url) {
								return;
							}

						});
						content = jObj.getString("content");
						
						
					}
				} catch (Exception e) {
					
				}
				super.onSucces(response);
			}
			
		});
	}
	
	@Override
	public void onClick(View v) {
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			SkyMainActivity.sm.toggle();
			break;
		case R.id.faq_btn_back_id:
			if (getActivity() instanceof SkyMainActivity) {
				SkyMainActivity context = (SkyMainActivity)getActivity();
				context.switchContent(new HomeFragment(), "HomeFragment");
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	/*private void writeFile(String data){
		try {
            File myFile = new File("/sdcard/ASkyfile.html");
            myFile.createNewFile();
            FileOutputStream fOut = new FileOutputStream(myFile);
            OutputStreamWriter myOutWriter = 
                                    new OutputStreamWriter(fOut);
            myOutWriter.append(data);
            myOutWriter.close();
            fOut.close();
            Log.i("Write json to file", " DONE");
        } catch (Exception e) {
            e.printStackTrace();
        }
	}*/
	
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

}
