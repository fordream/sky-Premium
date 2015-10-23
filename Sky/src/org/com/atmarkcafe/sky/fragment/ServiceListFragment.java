package org.com.atmarkcafe.sky.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.ServiceList;
import com.acv.cheerz.db.Setting;
@SuppressLint("ValidFragment")
public class ServiceListFragment extends BaseFragment{

	private static String TAG = "ServiceListFragment";
	private static String mSlug;
	private static String mType;
	private String mTitle;
	private ImageButton btnHome;
	private ImageButton btnRight;
	private HeaderView txtTile;
	private WebView webview;
	private LoadingView loading;
	private ImageView imgThum;
	private TextView txtSubtile;
	private int sWidth,sHeight;
	private Setting setting;
	private TextView txtMsgUpgrate;
	private TextView txtConfirmUpgrate;
	private String strMsgUpdate = "";
	private int flagCheckUpgrate = 0;
	private LinearLayout llUpgrate;
//	private List<SlugObject> listSlugObject;
	private ScrollView mainScrollView;
	public static SkyMainActivity getactivity ;
	public ServiceListFragment() {
		super();
	}
	public ServiceListFragment(String slug,String type,String title){
		this.mSlug = slug;
		this.mType = type;
		this.mTitle = title;
		Log.i(TAG, "slug = " + slug + " and type = " + type + " and title = " + title);
	}
	@Override
	public int getLayout() {
		
		return R.layout.service_list_layout;
	}
	
	int countMap = 0;

	@SuppressLint("NewApi")
	@Override
	public void init(View view) {
		getactivity = (SkyMainActivity)getActivity();
		loading = (LoadingView)view.findViewById(R.id.loading);
		CommonAndroid.showView(false, loading);
		llUpgrate = (LinearLayout)view.findViewById(R.id.servicelist_ll_bottom_id);
		llUpgrate.setVisibility(View.INVISIBLE);
		getProductList();
		btnHome = (ImageButton)view.findViewById(R.id.header_btn_left);
		btnHome.setImageResource(R.drawable.icon_back);
		btnHome.setOnClickListener(this);
		
		btnRight = (ImageButton)view.findViewById(R.id.header_btn_right);
		btnRight.setVisibility(View.INVISIBLE);
		
		txtTile = CommonAndroid.getView(view, R.id.header_id);
//		txtTile.initHeader(mTitle, mTitle);
		
		imgThum = (ImageView)view.findViewById(R.id.servicelist_thumnail_id);
		txtSubtile = (TextView)view.findViewById(R.id.servicelist_subtitle_id);
		
		webview = (WebView)view.findViewById(R.id.servicelist_webview_id);
		webview.getSettings().setJavaScriptEnabled(true);
		setting = new Setting(getActivity());
		webview.setWebChromeClient(new WebChromeClient(){
			
		});
		
		mainScrollView = CommonAndroid.getView(view, R.id.maindata);
		webview.setWebViewClient(new WebViewClient(){
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				LogUtils.i(TAG, "=== Back Back Bacm url = " + url);
				Log.i(TAG, "URL = " + url);
				if (url.contains("tel")) {
					openDialer(url);
				}
				if (url.contains("mailto")) {
					openEmail(url);
				}
				if (url.contains("gallery")) {
					openGallery();
				}
//				listSlugObject = new ArrayList<ServiceListFragment.SlugObject>();
				if(url.contains("http")){
					//news/portfolio/second-anniversary-tour/
					//news/life-planning/
					try {
						String StringURL = SkyUtils.API.BASESERVER_;
						String newStringURL = StringURL.replace("http://", "");
						newStringURL = StringURL.replace("https://", "");
						if( !url.contains(newStringURL) || url.endsWith("jpg") || url.endsWith("png")){
							txtSubtile.setVisibility(View.GONE);
							imgThum.setVisibility(View.GONE);
							webview.loadUrl(url);
							LogUtils.i(TAG, "http=====1");
						}else{
							ServiceListFragment productListFragment = null;
							mSlug = "";
							int spaceChar = url.indexOf("/");
							if(spaceChar > 0){
								String[] items = url.split("/");
								mSlug = items[items.length - 1].toString().trim();
								String check_new = items[items.length - 2].toString().trim();
								if(url.contains("/news/portfolio")){
									mType = "post";
									productListFragment = new ServiceListFragment(mSlug
											,mType 
											,"");
//									getProductList();
									LogUtils.i(TAG, "http=====2");
								}else if(check_new.contains("news")){
									mType = "page";
									productListFragment = new ServiceListFragment(mSlug
											,mType
											,"");
//									getProductList();
									LogUtils.i(TAG, "http=====3");
								}else{
									txtSubtile.setVisibility(View.GONE);
									imgThum.setVisibility(View.GONE);
									webview.loadUrl(url);
									LogUtils.i(TAG, "http=====4");
								}
							}
							LogUtils.i(TAG, "1xxxxxxxxxxxxx Slug = " + mSlug);
							
							if (productListFragment != null) {
								if (getActivity() instanceof SkyMainActivity) {
									SkyMainActivity skyMain = (SkyMainActivity)getActivity();
									skyMain.switchContent(productListFragment,"");
									
									LogUtils.i(TAG, "http=====5");
								}
								
							}
							
						}
					} catch (Exception e) {
						txtSubtile.setVisibility(View.GONE);
						imgThum.setVisibility(View.GONE);
						webview.loadUrl(url);
						LogUtils.i(TAG, "http=====6");
					}
					
				}
				return true;
			}
		});
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		sWidth = size.x;
		sHeight = size.y;
		txtMsgUpgrate = (TextView) view.findViewById(R.id.servicelist_msg_update_id);
		txtConfirmUpgrate = (TextView) view	.findViewById(R.id.servicelist_img_upgrate_id);
		txtConfirmUpgrate.setOnClickListener(this);
		
		
	}
	
	/**
	 * open gallery screen
	 */
	protected void openGallery() {
		if (getActivity() instanceof SkyMainActivity) {
			SkyMainActivity skyActivity = (SkyMainActivity)getActivity();
			skyActivity.switchContent(new GalleryFragment(), "");
		}
		
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
	public void onBackPressed() {
		goback();
		super.onBackPressed();
		Log.i(TAG, "BAKC BACK");
	}
	@Override
	public boolean onBackPressed(Bundle extras) {
		// TODO Auto-generated method stub
		Log.i(TAG, "BAKC 123");
		goback();
		super.onBackPressed(extras);
		return true;
	}
	
	
	
	int countBack = 0;
	@Override
	public void onClick(View v) {
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			/*HomeFragment homeFragment = new HomeFragment();
			if (getActivity() instanceof SkyMainActivity) {
				SkyMainActivity skyActivity = (SkyMainActivity)getActivity();
				skyActivity.switchContent(homeFragment, "");
			}*/
			
//			mSlug = "second-anniversary-tour";
//			mType = "post";
			
//			onBackPressed(null);
			
			goback();
			break;
		case R.id.servicelist_img_upgrate_id:
			ContactFragment contactFragment = new ContactFragment();
			Log.i(TAG, "sang contact us cho a ");
			if (getActivity() instanceof SkyMainActivity) {
				SkyMainActivity skyActivity = (SkyMainActivity) getActivity();
				skyActivity.switchContent(contactFragment, "");
				Log.i(TAG, "sang contact us cho a ");
			}
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	@Override
	public void onChangeLanguage() {
		getProductList();
	}
	
	public static void goback(){
		try {
			mSlug = HomeFragment.listSlugObject.get(HomeFragment.listSlugObject.size()- 2);
			mType = HomeFragment.listmTypeObject.get(HomeFragment.listmTypeObject.size()-2);
			LogUtils.i(TAG, "=== Back Back Bacm all = " + HomeFragment.listSlugObject.toString());
			LogUtils.i(TAG, "=== Back Back Bacm mSlug = " + mSlug);
			if(mSlug != null){
				ServiceListFragment productListFragment = new ServiceListFragment(mSlug,mType ,"");
				if (productListFragment != null) {
						HomeFragment.listSlugObject.remove(HomeFragment.listSlugObject.size()- 1);
						HomeFragment.listmTypeObject.remove(HomeFragment.listmTypeObject.size()-1);
						getactivity.switchContent(productListFragment, "");
					
				}
			}else{
				HomeFragment homeFragment = new HomeFragment();
				getactivity.switchContent(homeFragment, "");
			}
		} catch (Exception e) {
			HomeFragment homeFragment = new HomeFragment();
			getactivity.switchContent(homeFragment, "");
		}
	}
	
	private void getProductList(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("req[param][slug]", mSlug);
		params.put("req[param][type]", mType);
		SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_SERVICE_LIST, params, new CheerzServiceCallBack(){
			

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
				super.onSucces(response);
				if(!HomeFragment.listSlugObject.contains(mSlug)){
					HomeFragment.listSlugObject.add(mSlug);
					HomeFragment.listmTypeObject.add(mType);
					LogUtils.i(TAG, "=== Back Back Bacm add = " + HomeFragment.listSlugObject.toString());
				}
				
				CommonAndroid.showView(false, loading);
//				mainScrollView.fullScroll(ScrollView.FOCUS_UP);
				mainScrollView.smoothScrollTo(0,0);
				Log.i(TAG, "data = " + response);
				try {
					JSONObject jsonRes = new JSONObject(response);
					JSONArray jsonData = jsonRes.getJSONArray("data");
					String content = null;
					String urlThum = null;
					String subTitle = null;
					for (int i = 0; i < jsonData.length(); i++) {
						JSONObject jobj = jsonData.getJSONObject(i);
						content = jobj.getString("content");
						urlThum = jobj.getString("thumbnail");
						mTitle = jobj.getString("title");
						subTitle = jobj.getString("subtitle");
						txtSubtile.setText(subTitle);
						strMsgUpdate = jobj.getString("platinum_accessed_msg");
						flagCheckUpgrate = jobj.getInt("is_platinum_accessed");
					}
					if (content.equalsIgnoreCase("null") || content.equalsIgnoreCase("")) {
						content = "";
					}
					if (subTitle.equalsIgnoreCase("")) {
						LayoutParams parasm = txtSubtile.getLayoutParams();
						parasm.width = 0;
						parasm.height = 0;
						txtSubtile.setLayoutParams(parasm);
					}
					if (mSlug.equalsIgnoreCase("platinum-vacations")) {
						content = content.replaceAll("<p>&nbsp;</p>", "<br>");
						processData(content);
					}else{
						content = SkyUtils.addStyleContent(content);
//						content = content.replaceAll("#0099ff", "#2a2a2a");
//						content = content.replaceAll("#D8D8D8", "#2a2a2a");
						String URLBASE = SkyUtils.API.BASESERVER_;
						try {
							URLBASE = URLBASE.replaceAll("samuraistation-clone/", "");
						} catch (Exception e) {}
						webview.loadDataWithBaseURL(URLBASE, content, "text/html", "utf-8", null);
					}
					
					
					// gold account
					final String contract_type = new Account(getActivity()).getContractType();
//					flagCheckUpgrate = 1;
//					final String contract_type = "2";
					new Handler().postDelayed(new Runnable() {
						
						@Override
						public void run() {
							if (flagCheckUpgrate == 1 && "2".equalsIgnoreCase(contract_type)) {
								// show
								txtMsgUpgrate.setText(strMsgUpdate);
								llUpgrate.setVisibility(View.VISIBLE);
								LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(android.widget.LinearLayout.LayoutParams.MATCH_PARENT,	
										android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
								params.topMargin = 10;
								llUpgrate.setLayoutParams(params);
							} else {
								// hide
								llUpgrate.setVisibility(View.INVISIBLE);
								LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0,	0);
								llUpgrate.setLayoutParams(params);
							}
						}
					}, 500);
					
					txtTile.initHeader(mTitle, mTitle);
					if(urlThum == null || urlThum.equals("") || urlThum.equals("null")){
//						LayoutParams params = imgThum.getLayoutParams();
//						params.height = 20;
//						params.width = 320;
//						imgThum.setLayoutParams(params);
						imgThum.setVisibility(View.GONE);
					}else{
						ImageLoaderSquareUtils.getInstance(getActivity()).displayImageHomeSlide(urlThum, imgThum);
					}
					mainScrollView.smoothScrollTo(0,0);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		});
	}
	
	private void processData(String content){
		content = content.replace("width=\"1280\"", "width=\"100%\"");
//		content = content.replaceAll("font-family: Georgia", "font-family: Helvetica");
//		content = content.replaceAll("font-family:Georgia", "font-family: Helvetica");
		String str = "";
		str += loadResouceHTMLFromAssets("html/project_detail_header.html");
		if (content != null) {
			str+= content;
		}
		str += loadResouceHTMLFromAssets("html/project_detail_footer.html");
		webview.loadDataWithBaseURL("file:///android_asset/", str, "text/html", "UTF-8", null);
	}
	
	public String loadResouceHTMLFromAssets(String filename) {
		String tmp = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getActivity().getAssets().open(filename)));
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
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
	    ImageView bmImage;

	    public DownloadImageTask(ImageView bmImage) {
	        this.bmImage = bmImage;
	    }

	    protected Bitmap doInBackground(String... urls) {
	        String urldisplay = urls[0];
	        Bitmap mIcon11 = null;
	        try {
	            InputStream in = new java.net.URL(urldisplay).openStream();
	            mIcon11 = BitmapFactory.decodeStream(in);
	        } catch (Exception e) {
	            Log.e("Error", e.getMessage());
	            e.printStackTrace();
	        }
	        return mIcon11;
	    }

	    protected void onPostExecute(Bitmap result) {
	    	
	        bmImage.setImageBitmap(result);
	    }
	}
	
	private class SlugObject{
		private String slug;
		private String type;
		private String title;
		public String getSlug() {
			return slug;
		}
		public void setSlug(String slug) {
			this.slug = slug;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
	}
	 
}
