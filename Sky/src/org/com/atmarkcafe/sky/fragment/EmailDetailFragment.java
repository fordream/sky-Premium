package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.object.ItemMail;
import org.com.atmarkcafe.service.CallbackListenner;
import org.com.atmarkcafe.sky.GcmBroadcastReceiver;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.dialog.EcProductsDetailDialog;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.SCREEN;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.Setting;

@SuppressLint("ValidFragment")
public class EmailDetailFragment extends BaseFragment implements OnClickListener{

	private String TAG = "EmailDetailFragment"; 
	private int emailId = 0;		
	private TextView txtSubjectEmail;
	private WebView webContentEmail;
	private ImageButton btnBack;
	private ImageButton btnNext,btnPrevious;
	private HeaderView titleFragment;
	private LoadingView loading;
	private Setting setting;
	private int currentIndex; // index of email
	private List<ItemMail> listData;
	private SharedPreferences sharePref;
	private boolean flagSearch = false;
	private String subjectValue,dateFromValue,dateToValue;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	public EmailDetailFragment(int emailIndex,List<ItemMail> data,boolean flag) {
		this.currentIndex = emailIndex;
		this.listData = data;
		this.flagSearch = flag;
		Log.i(TAG, "Flag search = " + flagSearch);
	}
	
	public EmailDetailFragment() {
	}
	
	long DeltaTime = 0;
	private void getDataEmailDetail(int idEmail){
		switchStatusNextPrevious(idEmail);
		if (getActivity() != null) {
			HashMap<String , String> params = new HashMap<String, String>();
			params.put("req[param][id]", String.valueOf(idEmail));
			SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_USER_MAILDETAIL, params, new CheerzServiceCallBack(){
				
				@Override
				public void onStart() {
					CommonAndroid.showView(true, loading);
					super.onStart();
				}
				
				@Override
				public void onError(String message) {
					System.out.println("Error: "+ message.toString());
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
					super.onSucces(response);
					Log.i(TAG, "respone : " + response.toString());
					CommonAndroid.showView(false, loading);
					try {
						JSONObject jsonObject = new JSONObject(response);
						JSONObject jObj = jsonObject.getJSONObject("data");
						txtSubjectEmail.setText(jObj.getString("subject").toString());
						txtSubjectEmail.setVisibility(View.VISIBLE);
//						txtContentEmail.setText(Html.fromHtml(jObj.getString("content").toString()));
						String str = jObj.getString("content");
						if (str== null || str.equalsIgnoreCase("")) {
							str = "";
						}
						str = SkyUtils.addStyleContentWithBackgoundWhite(str);
//						webContentEmail.loadDataWithBaseURL(null, str, "text/html", "utf-8", null);
						webContentEmail.loadDataWithBaseURL(SkyUtils.API.BASESERVER_, str, "text/html", "utf-8", null);
						webContentEmail.setWebChromeClient(new WebChromeClient() {
							 public void onProgressChanged(WebView view, int progress) {
					            try {
									getActivity().setProgress(progress * 100);
									LogUtils.e(TAG, "11111111111==progress==" + progress);
									if(progress >= 80){
										CommonAndroid.showView(false, loading);
									}
								} catch (Exception e) {}
					            /*if(DeltaTime > 0){
					            	long time_temp = (long) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
					            	int time_check = (int) (time_temp - DeltaTime);
					            	Log.i(TAG, "kkk==" + time_check);
					            	if(time_check > 20){
					            		Log.e(TAG, "11111111111");
					            		CommonAndroid.showView(false, loading);
					            		DeltaTime = 0;
					            	}
					            }*/
					          }
						});

						webContentEmail.setLongClickable(false);
						webContentEmail.setOnLongClickListener(new OnLongClickListener() {
							@Override
							public boolean onLongClick(View v) {
								return true;
							}
						});

						webContentEmail.setWebViewClient(new WebViewClient() {
							public boolean shouldOverrideUrlLoading(WebView view, String url) {
//								DeltaTime = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis());
								if (url.contains("http") && !url.contains("mailto") ) {
									txtSubjectEmail.setVisibility(View.GONE);
								}else{
									txtSubjectEmail.setVisibility(View.VISIBLE);
								}
								
//								return false;
								

								LogUtils.i(TAG, "=== Back Back Bacm url = " + url);
								if (url.contains("tel")) {
									openDialer(url);
								}
								if (url.contains("mailto")) {
									openEmail(url);
								}
								if (url.contains("gallery")) {
									openGallery();
								}
								
//								http://pgcafe.asia/samuraistation-clone/shop/en/product-1426855492/296-page-1442994920.html
								if(url.contains("http")){
									try {
										/*String StringURL = SkyUtils.API.BASESERVER_;
										String newStringURL = StringURL.replace("http://", "");
										newStringURL = StringURL.replace("https://", "");*/
										String shop = "";
										String product = "";
										String id_page = "";
										int spaceChar = url.indexOf("/");
										if(spaceChar > 0){
											String[] items = url.split("/");
											//check id
											String id_temp = items[items.length - 1].toString().trim();
											String[] items_id = id_temp.split("-");
											id_page = items_id[0].toString().trim();
											//check product
											String product_temp = items[items.length - 2].toString().trim();
											String[] items_product = product_temp.split("-");
											product = items_product[0].toString().trim();
											//check shop
											String shop_temp = items[items.length - 4].toString().trim();
											String[] items_shop = shop_temp.split("-");
											shop = items_shop[0].toString().trim();
											
											//https://member.skypremium.com.sg/shop/ja/
											//https://pgcafe.asia/samuraistation-clone/shop/ja
											String shop_check1 = items[items.length - 3].toString().trim();
											String shop_check2 = items[items.length - 2].toString().trim();
											if("shop".equals(shop_check1) || "shop".equals(shop_check2)){
												shop = "shop";
											}
										}
										
										LogUtils.i(TAG, "data==" /*+newStringURL */+ ":" + shop + ":" + product + ":" + id_page );
										if( /*url.contains(newStringURL) &&*/ shop.equals("shop") && product.equals("product") && !id_page.equals("")){
//											go to pj detail
											LogUtils.i(TAG, "http=====11==");
											Bundle extras = new Bundle();
											extras.putString(Products.id, id_page);
											ECFragment.addtocart_Productsid = id_page;
//											startDetail(SCREEN.PJDETAIL, extras);
											LogUtils.i(TAG, "http=====11");
											
											Bundle extras_detail = new Bundle();
											extras_detail.putString(Products.id, id_page);
											startActivityForResult(SCREEN.EC__PRODUCT_LIST, extras_detail);
										}else if(shop.equals("shop")){
											Bundle extras = new Bundle();
											startActivityForResult(SCREEN.EC__PRODUCT_LIST, extras);
										}else{
											CommonAndroid.showView(true, loading);
											webContentEmail.loadUrl(url);
											LogUtils.i(TAG, "http=====22");
										}
									} catch (Exception e) {
										CommonAndroid.showView(true, loading);
										webContentEmail.loadUrl(url);
										e.printStackTrace();
										LogUtils.i(TAG, "http=====6");
									}
									
								}
								return true;
							
							}

							public void onPageFinished(WebView view, String url) {
//								DeltaTime = 0;
								CommonAndroid.showView(false, loading);
								return;
							}
							
							public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
//								DeltaTime = 0;
								CommonAndroid.showView(false, loading);
								return;
							}

						});
						
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	CallbackListenner callbackListenner = new CallbackListenner() {
		@Override
		public void onLoad(boolean onload) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public void startDetail(SCREEN screen, Bundle extras) {
		RootActivity.PJ_Detail = new EcProductsDetailDialog(getActivity(),new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		},callbackListenner);
		RootActivity.PJ_Detail.show();
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
	
	private void stopLoad(){
		int secondsDelayed = 10;
		new Handler().postDelayed(new Runnable() {
			public void run() {
				Log.e(TAG, "bbbbbbbbbb");
				CommonAndroid.showView(false, loading);
			}
		}, secondsDelayed * 1000);
	}
	
	public boolean getFlagSearch(){
		return flagSearch;
	}
	
	public void logDataSearch(String subject,String dateFrom, String dateTo){
		subjectValue = subject;
		dateFromValue = dateFrom;
		dateToValue = dateTo;
	}
	
	@Override
	public void onClick(View v) {
	
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			MailboxFragment emailbox = new MailboxFragment();
			emailbox.updateFlagSearch(flagSearch);
			emailbox.updateDataSearch(subjectValue, dateFromValue, dateToValue);
//			getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, emailbox).commit();
//			onBackPressed();
			getActivity().getSupportFragmentManager().popBackStack();
			break;
		case R.id.header_btn_right:
			// next email
			if (currentIndex < listData.size() - 1) {
				currentIndex +=1;
				int idEmail = listData.get(currentIndex).getId();
				getDataEmailDetail(idEmail);
			}
			break;
		case R.id.header_btn_right_left:
			// previous email
			if (currentIndex > 0) {
				currentIndex -=1;
				int idEmail = listData.get(currentIndex).getId();
				getDataEmailDetail(idEmail);
			}
			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void onStop() {
		Log.i(TAG, "Stop email detail");
		MailboxFragment.flagBackFromDetailEmail = true;
		super.onStop();
	}
	
	/**
	 * 
	 * @param index = 0 : disable previous
	 * @param index = maxlist : enable previous,disable next
	 * @param index  = (0-maxlist) : endable all
	 */
	private void switchStatusNextPrevious(int idEmail){
		int index = -1;
		for (int i = 0; i < listData.size(); i++) {
			if (idEmail == listData.get(i).getId()) {
				index = i;
			}
		}
		if (index == 0) {
			btnPrevious.setImageResource(R.drawable.ico_previous_selecteded);
			btnPrevious.setEnabled(false);
			btnNext.setImageResource(R.drawable.ico_next);
			btnNext.setEnabled(true);
		}else if (index == listData.size() - 1) {
			btnPrevious.setImageResource(R.drawable.ico_previous);
			btnPrevious.setEnabled(true);
			btnNext.setImageResource(R.drawable.ico_next_selecteded);
			btnNext.setEnabled(false);
		}else{
			btnPrevious.setImageResource(R.drawable.ico_previous);
			btnPrevious.setEnabled(true);
			btnNext.setImageResource(R.drawable.ico_next);
			btnNext.setEnabled(true);
		}
	}

	@Override
	public int getLayout() {
		return R.layout.email_detail_layout;
	}

	@Override
	public void init(View detailView) {
		btnBack = (ImageButton)detailView.findViewById(R.id.header_btn_left);
		btnBack.setImageResource(R.drawable.icon_nav_back);
		btnBack.setOnClickListener(this);
		
		btnNext = (ImageButton)detailView.findViewById(R.id.header_btn_right);
		btnNext.setVisibility(View.VISIBLE);
		btnNext.setImageResource(R.drawable.ico_next);
		btnNext.setOnClickListener(this);
		
		btnPrevious = (ImageButton)detailView.findViewById(R.id.header_btn_right_left);
		btnPrevious.setVisibility(View.VISIBLE);
		btnPrevious.setImageResource(R.drawable.ico_previous);
		btnPrevious.setOnClickListener(this);
		
		loading = (LoadingView)detailView.findViewById(R.id.loading);
		CommonAndroid.showView(false, loading);
		
		titleFragment = CommonAndroid.getView(detailView, R.id.header_maildetail_id); 
		titleFragment.initHeader(R.string.menu_mailbox, R.string.menu_mailbox_j);
		
		txtSubjectEmail = (TextView)detailView.findViewById(R.id.email_detail_subject_id);
		webContentEmail = (WebView)detailView.findViewById(R.id.email_detail_content_id);
		webContentEmail.getSettings().setJavaScriptEnabled(true);
		
		setting = new Setting(getActivity());
		emailId = listData.get(currentIndex).getId();
		
		if (currentIndex == 0) {
			btnPrevious.setImageResource(R.drawable.ico_previous_selecteded);
			btnPrevious.setEnabled(false);
		} else if(currentIndex == listData.size() - 1){
			btnNext.setEnabled(false);
			btnNext.setImageResource(R.drawable.ico_next_selecteded);
		}else{
			btnPrevious.setImageResource(R.drawable.ico_previous);
			btnPrevious.setEnabled(true);
			btnNext.setImageResource(R.drawable.ico_next);
			btnNext.setEnabled(true);
		}
		
		getDataEmailDetail(emailId);
		updateBadge();
		MailboxFragment.listEmail.get(currentIndex).setStatusOpen(0);
		MailboxFragment.listEmail.get(currentIndex).setNewsStatus(0);
	}
	
	/**
	 * this methos will update badge when read new email
	 */
	private void updateBadge(){
		if (listData.get(currentIndex).getNewsStatus() == 1) {
			/*sharePref = getActivity().getSharedPreferences(SkyUtils.shareKey, Context.MODE_PRIVATE);
			int countBadgeEmail = sharePref.getInt(SkyUtils.BADGE_EMAIL, 0);
			countBadgeEmail -= 1;
			sharePref.edit().putInt(SkyUtils.BADGE_EMAIL, countBadgeEmail).commit();
			CommonAndroid.updateBadgeLauncher(getActivity(),  sharePref.getInt(SkyUtils.BADGE_EMAIL, 0) + sharePref.getInt(SkyUtils.BADGE_NEWS, 0));*/
			GcmBroadcastReceiver.notifiServiceUpdate(getActivity(),1,1, false, true);
			updateBadgeServer();
		}
	}
	
	private void updateBadgeServer(){
		HashMap<String, String>params = new HashMap<String, String>();
		params.put("type", String.valueOf(1));
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
	public void onChangeLanguage() {
		// TODO Auto-generated method stub
		
	}
}
