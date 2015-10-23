package org.com.atmarkcafe.sky.dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.Base2Adialog;
import z.lib.base.BaseAdapter;
import z.lib.base.BaseItem;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.Setting;

public class EcShipDialog extends Base2Adialog implements android.view.View.OnClickListener {
	View views;
	RelativeLayout main_detail;
	protected static final String TAG = "EcShipDialog";
	private BaseAdapter baseAdapter;
	List<BaseItem> baseItemsAddbook = new ArrayList<BaseItem>();
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	ScrollView scrolllistview;
	LinearLayout linearLayoutForm;
	private WebView credit;
	CheckBox check_team;
	ScrollView detail;
	String term_and_condition = "";
	private String msgNotAvailable;
	private boolean isContinue = true;
	public EcShipDialog(Context context, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.ec_ship_header, R.string.ec_ship_header_j);
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);// icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.bt_continue).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.bt_checkout).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.tearm_content).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		linearLayoutForm = (LinearLayout) findViewById(R.id.linearLayoutForm);
		onLoad();
		
		/*credit = new WebView(getContext());
		credit.setBackgroundColor(Color.parseColor("#ededed"));
//		credit.getSettings().setUseWideViewPort(true);
//		credit.setVerticalScrollBarEnabled(true);
//		credit.setHorizontalScrollBarEnabled(true);
		
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.term_value);
		rl.addView(credit);*/
		check_team = CommonAndroid.getView(main_detail, R.id.checkbox);
		detail = CommonAndroid.getView(main_detail, R.id.detail);
		CommonAndroid.getView(main_detail, R.id.checkbox_update).setOnClickListener(this);
	}
	
	private void viewTeam(String content) {
		try {
//			String URL = "contract_term.html";
//			if( !new Setting( getActivity()).isLangEng()){
//				URL = "contract_term_j.html";
//			}
//			credit.loadUrl("file:///android_asset/contract_term/" + URL);
			Log.i(TAG, "content term : " + content);
			//content = SkyUtils.addStyleContentTeam(content);
			credit.loadDataWithBaseURL(null, content,"text/html", "utf-8", null);
			credit.setWebChromeClient(new WebChromeClient() {
			});

			credit.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return false;
				}

				public void onPageFinished(WebView view, String url) {
					credit.stopLoading();
					Log.e("TIME1", "2 : " + System.currentTimeMillis() + "");
					return;
				}

			});
		} catch (Exception e) {
			Log.e("TIME3", System.currentTimeMillis() + "");
			// credit.stopLoading();
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.e("TIME4", System.currentTimeMillis() + "");
		}
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
	
	@Override
	public void onBackPressed(){
		close(true);
	}
	
	private void onLoad() {
		// TODO Auto-generated method stub
//		HashMap<String, String> inputs = new HashMap<String, String>();
//		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_CARRIER, inputs, callback);
		JSONObject objParam = new JSONObject();//root key: param
		SkyUtils.execute(getActivity(), RequestMethod.POST, API.API_EC_CARRIER, SkyUtils.paramRequest(getActivity(), objParam) , callback);
		LogUtils.e(TAG, SkyUtils.paramRequest(getActivity(), objParam) + "");
	}
	
	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
			CommonAndroid.hiddenKeyBoard(getActivity());
		};

		public void onError(String message) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				SkyUtils.showDialog(getActivity(), "" + message,null);
				CommonAndroid.getView(main_detail, R.id.bt_checkout).setEnabled(false);
				CommonAndroid.getView(main_detail, R.id.bt_checkout).setAlpha(0.5f);
			}
		};

		public void onSucces(String response) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				JSONObject mainJsonObject;
				try {
					mainJsonObject = new JSONObject(response);
					LogUtils.e(TAG, "dataUPDATE==" + mainJsonObject.toString());
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					String err_msg = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.err_msg);
					JSONObject jsonData = mainJsonObject.getJSONObject("data");
					if (jsonData.has(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE)) {
						msgNotAvailable = jsonData.getString(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE);
						SkyUtils.showDialog(getActivity(), msgNotAvailable,new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								MyCart2Fragment.reLoadMyCart();
							}
						});
						isContinue = false;
					}else{
						isContinue = true;
					}
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						detail.setVisibility(View.VISIBLE);
						baseItemsAddbook.clear();
						try {
							JSONObject mainJsonObject_data = mainJsonObject.getJSONObject("data");
							linearLayoutForm.removeAllViewsInLayout();
							JSONArray array = mainJsonObject_data.getJSONArray("product");
							for (int i = 0; i < array.length() ; i++) {
//								baseItemsAddbook.add(new BaseItem(array.getJSONObject(i)));
								JSONObject itemObj = new JSONObject();
							    try
							    {
							    	itemObj.put("ec_ship_list_carrier",  array.getJSONObject(i).getString("name") );
							        itemObj.put("ec_ship_list_product",array.getJSONObject(i).getJSONObject("carrier").getString("name")  );
//							        baseItemsAddbook.add(new BaseItem(itemObj));
							        addView(new BaseItem(itemObj));
							    }catch(Exception e){
							    	LogUtils.e(TAG, "e2" +e.getMessage());
							    }
							}
							
							/*for(int i = 0; i < 5; i++){
								JSONObject itemObj = new JSONObject();
							    try
							    {
							    	itemObj.put("ec_ship_list_carrier", "ec_ship_list_carrier " + i);
							        itemObj.put("ec_ship_list_product","ec_ship_list_product " + i);
//							        baseItemsAddbook.add(new BaseItem(itemObj));
							        addView(new BaseItem(itemObj));
							    }catch(Exception e){
							    	LogUtils.e(TAG, "e2" +e.getMessage());
							    }
								
							}*/
							
							String shipping_cost = "0";
							String total = "0";
							shipping_cost = mainJsonObject_data.getString("shipping_cost");
							total = mainJsonObject_data.getString("total");
							CommonAndroid.setText(CommonAndroid.getView(main_detail, R.id.shipping_cost), CommonAndroid.formatPriceEC(getActivity(), shipping_cost) );
							CommonAndroid.setText(CommonAndroid.getView(main_detail, R.id.ship_total), CommonAndroid.formatPriceEC(getActivity(), total) );
							Products.ec_credit_confirm_amount = CommonAndroid.formatPriceEC(getActivity(), total) ;
							term_and_condition = mainJsonObject_data.getString("term_and_condition");
//							viewTeam(mainJsonObject_data.getString("term_and_condition"));
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						SkyUtils.showDialog(getActivity(), "" + err_msg,null);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}

	};
	
	private void addView(BaseItem baseItem) {
		// TODO Auto-generated method stub
		final RelativeLayout newView = (RelativeLayout)getActivity().getLayoutInflater().inflate(R.layout.ec_ship_item_layout, null);
		newView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//		((TextView) CommonAndroid.getView(newView, R.id.ec_ship_list_product)).setText( baseItem.getString("ec_ship_list_product") );
//		((TextView) CommonAndroid.getView(newView, R.id.ec_ship_list_carrier)).setText( baseItem.getString("ec_ship_list_carrier") );
		((TextView) CommonAndroid.getView(newView, R.id.ec_ship_list_carrier)).setText( baseItem.getString("ec_ship_list_product") );
		((TextView) CommonAndroid.getView(newView, R.id.ec_ship_list_product)).setText( baseItem.getString("ec_ship_list_carrier") );
		linearLayoutForm.addView(newView);
	}

	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.main_detail), new AnimationAction() {

			@Override
			public void onAnimationEnd() {
				dismiss();
				if (isOnClick) {
					clickListener.onClick(null, 0);
				}
			}
		});
	}

	@Override
	public int getLayout() {
		return R.layout.ec_ship_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			close(true);
			break;
		case R.id.bt_continue:
			LogUtils.e(TAG, "bt_continue");
			finish();
//			gotoEC();
			break;
		case R.id.bt_checkout:
			LogUtils.e(TAG, "check out");
			if (isContinue) {
				if(!check_team.isChecked()){
					String validate_team = getActivity().getResources().getString(R.string.ec_ship_team);
					if(!new Setting(getActivity()).isLangEng()){
						validate_team = getActivity().getResources().getString(R.string.ec_ship_team_j);
					}
					SkyUtils.showDialog(getActivity(), validate_team,null);
				}else{
					MyCartActivity.EC_CREDIT = new EcCreditDialog(getActivity(),new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
						
					});
					MyCartActivity.EC_CREDIT.show();
				}
			}else{
				SkyUtils.showDialog(getActivity(), msgNotAvailable,   null);
			}
			
			
			break;	
		case R.id.checkbox_update:
			check_team.setChecked(check_team.isChecked() ? false : true);
			break;
		case R.id.tearm_content:
			//show team
			new EcShipTearmDialog(getActivity(),term_and_condition , new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
				
			}).show();
			break;
		default:
			break;
		}
	}
	
	private FragmentActivity getActivity() {
		return MyCart2Fragment.activity;
	}
	
	public boolean isFinish() {
		if (getActivity() == null) {
			return true;
		}

		return getActivity().isFinishing();
	}

	public void finish() {
		try {
			if(RootActivity.PJ_Detail != null){
				RootActivity.PJ_Detail.dismiss();
				RootActivity.PJ_Detail = null;
			}
			if(RootActivity.PJ_AddToCart != null){
				RootActivity.PJ_AddToCart.dismiss();
			}
			if(RootActivity.PJ_ListCat != null){
				RootActivity.PJ_ListCat.dismiss();
			}
			if(RootActivity.PJ_FavoriteDialog != null){
				RootActivity.PJ_FavoriteDialog.dismiss();
			}
			if(RootActivity.PJ_HistoryDialog != null){
				RootActivity.PJ_HistoryDialog.dismiss();
			}
			if(RootActivity.PJ_HistoryDetailDialog != null){
				RootActivity.PJ_HistoryDetailDialog.dismiss();
			}
		} catch (Exception e) {}
		if (getActivity() != null) {
			getActivity().finish();
		}
	}
	

}