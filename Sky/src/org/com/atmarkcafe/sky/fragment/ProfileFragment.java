package org.com.atmarkcafe.sky.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.dialog.ContractUpdateAnnualDialog;
import org.com.atmarkcafe.sky.dialog.ProfileRecommentDialog;
import org.com.atmarkcafe.sky.dialog.ProfileUpdateDialog;
import org.com.atmarkcafe.sky.dialog.PurchaseDetailDialog;
import org.com.atmarkcafe.view.PurchaseItemView;
import org.json.JSONObject;

import z.lib.base.BaseAdapter;
import z.lib.base.BaseFragment;
import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.SkyUtils.KEY_CONTRACT;
import z.lib.base.SkyUtils.SCREEN;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;
import com.parse.ParseException;

@SuppressLint("SimpleDateFormat")
public class ProfileFragment extends BaseFragment {
	protected static final String TAG = "ProfileFragment";
	BaseItem baseItem;
	BaseItem baseItemContract;
	static View view;
	public static FragmentActivity activity;
	LinearLayout linearLayoutFormContract;
	LinearLayout linearLayoutFormProfile;
	LinearLayout linearLayoutFormPurchase;
	static ScrollView Scomain;
	List<BaseItem> baseItemsPurchase = new ArrayList<BaseItem>();
	Boolean onloadPuchase = false;
	static Boolean onloadContract = false;
	public static Boolean onloadProfile = false;
	private LoadingView loading;
	String contract_code;
	private org.com.atmarkcafe.view.HeaderView header;
	LinearLayout action_profile;
	LinearLayout action_contract;
//	LinearLayout action_purchase;
	ImageView icon_p_profile, icon_p_contract/*, icon_p_puchase*/;
	TextView text_p_profile, text_p_contract/*, text_p_puchase*/;
	private int isTab = 0;
	private Boolean isLangEng = false;
	static int recommend = 0;
	private static CheerzServiceCallBack callbackprofile;
	static RelativeLayout contract_bt_extend;
	static TextView contract_extend_confirm_admin;
	private static CheerzServiceCallBack callbackContract;
	public ProfileFragment() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.user_profile_view;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.contract_bt_extend:
				new ContractUpdateAnnualDialog(getActivity(), KEY_CONTRACT.extend_before_expire, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
				break;
			case R.id.header_btn_right:
				if(baseItem != null){
					new ProfileUpdateDialog(getActivity(), baseItem, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
	
						}
					}).show();
				}
				break;
			case R.id.action_contract:
				linearLayoutFormContract.setVisibility(View.VISIBLE);
				linearLayoutFormProfile.setVisibility(View.GONE);
				linearLayoutFormPurchase.setVisibility(View.GONE);
//				Scomain.setVisibility(View.VISIBLE);
				header.initHeader(R.string.profile_contract, R.string.profile_contract_j);
				header.visibivityRight(false, R.drawable.icon_edit);
				onLoadContract();
				changeTab(R.id.action_contract);
				isTab = 1;
				onloadContract = false;
				break;
			case R.id.action_profile:
				linearLayoutFormContract.setVisibility(View.GONE);
				linearLayoutFormProfile.setVisibility(View.VISIBLE);
				linearLayoutFormPurchase.setVisibility(View.GONE);
//				Scomain.setVisibility(View.VISIBLE);
				header.initHeader(R.string.profile_information, R.string.profile_information_j);
				header.visibivityRight(true, R.drawable.icon_edit);
				onLoadProfile();
				changeTab(R.id.action_profile);
				isTab = 0;
				onloadProfile = false;
				break;	
			/*case R.id.action_purchase:
				linearLayoutFormContract.setVisibility(View.GONE);
				linearLayoutFormProfile.setVisibility(View.GONE);
				linearLayoutFormPurchase.setVisibility(View.VISIBLE);
				Scomain.setVisibility(View.GONE);
				header.initHeader(R.string.profile_purchased, R.string.profile_purchased_j);
				header.visibivityRight(false, R.drawable.icon_edit);
				onLoadPurchase();
				changeTab(R.id.action_purchase);
				isTab = 2;
				break;*/
			case R.id.header_btn_left:
				if(SkyMainActivity.FLAG_EXTRA_CHECK_BACK_PROFILE){
					SkyMainActivity.FLAG_EXTRA_CHECK_BACK_PROFILE = false;
					Bundle extras = new Bundle();
					startActivityForResult(SCREEN.EC__PRODUCT_LIST, extras);
				}else{
					SkyMainActivity.sm.toggle();
				}
				
				break;
			/*case R.id.button_upgrade:
				new ContractDialog(getActivity(), "", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
				break;*/
			case R.id.profile_recommend_list:
				if(recommend > 0){
					new ProfileRecommentDialog(getActivity() , new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
				}
		default:
			break;
		}
	}

	private void changeTab(int Tab) {
		// TODO Auto-generated method stub
		switch (Tab) {
		case R.id.action_profile:
			action_profile.setBackgroundColor(Color.parseColor("#dfdcdc"));
			action_contract.setBackgroundColor(Color.parseColor("#ffffff"));
//			action_purchase.setBackgroundColor(Color.parseColor("#ffffff"));
			icon_p_profile.setBackgroundResource(R.drawable.icon_p_profile_1);
			icon_p_contract.setBackgroundResource(R.drawable.icon_p_contract_2);
//			icon_p_puchase.setBackgroundResource(R.drawable.icon_p_puchase_2);
			text_p_profile.setTextColor(Color.parseColor("#000000"));
			text_p_contract.setTextColor(getResources().getColor(R.color.profile_view));
//			text_p_puchase.setTextColor(Color.parseColor("#016ea4"));
			break;
		case R.id.action_contract:
			action_profile.setBackgroundColor(Color.parseColor("#ffffff"));
			action_contract.setBackgroundColor(Color.parseColor("#dfdcdc"));
//			action_purchase.setBackgroundColor(Color.parseColor("#ffffff"));
			icon_p_profile.setBackgroundResource(R.drawable.icon_p_profile_2);
			icon_p_contract.setBackgroundResource(R.drawable.icon_p_contract_1);
//			icon_p_puchase.setBackgroundResource(R.drawable.icon_p_puchase_2);
			text_p_profile.setTextColor(getResources().getColor(R.color.profile_view));
			text_p_contract.setTextColor(Color.parseColor("#000000"));
//			text_p_puchase.setTextColor(Color.parseColor("#016ea4"));
			break;
		case R.id.action_purchase:
			action_profile.setBackgroundColor(Color.parseColor("#ffffff"));
			action_contract.setBackgroundColor(Color.parseColor("#ffffff"));
//			action_purchase.setBackgroundColor(Color.parseColor("#dfdcdc"));
			icon_p_profile.setBackgroundResource(R.drawable.icon_p_profile_2);
			icon_p_contract.setBackgroundResource(R.drawable.icon_p_contract_2);
//			icon_p_puchase.setBackgroundResource(R.drawable.icon_p_puchase_1);
			text_p_profile.setTextColor(Color.parseColor("#016ea4"));
			text_p_contract.setTextColor(Color.parseColor("#016ea4"));
//			text_p_puchase.setTextColor(Color.parseColor("#000000"));
			break;	
		default:
			break;
		}
	}

	public static void onLoadProfile() {
		// TODO Auto-generated method stub
		if(!onloadProfile){
			LogUtils.e(TAG, "onloadprofile");
			HashMap<String, String> inputs = new HashMap<String, String>();
			SkyUtils.execute(activity, RequestMethod.GET, API.API_USER_PROFILE, inputs, callbackprofile);
		}else{
			Scomain.setVisibility(View.VISIBLE);
		}
	}

	/*private void onLoadPurchase() {
		// TODO Auto-generated method stub
		if(!onloadPuchase){
			HashMap<String, String> inputs = new HashMap<String, String>();
			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_PURCHASEHISTORY, inputs, callbackPurchase);
		}
	}*/

	private BaseAdapter baseAdapter;
	private void addPurchaseView(List<BaseItem> baseItems2) {
		// TODO Auto-generated method stub
		ListView listview = (ListView) view.findViewById(R.id.listview_purchase);
		View header2 = getActivity().getLayoutInflater().inflate(R.layout.user_purchase_header, null);
		listview.removeHeaderView(header2);
		listview.addHeaderView(header2);
        baseAdapter = new BaseAdapter(getActivity(), new ArrayList<Object>()) {
			@Override
			public BaseView getView(Context context, Object data) {
				final PurchaseItemView purchaseItemView = new PurchaseItemView(context);
				return purchaseItemView;
			}
		};
		baseAdapter.clear();
		baseAdapter.addAllItems(baseItems2);
		baseAdapter.notifyDataSetChanged();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BaseItem baseItem = (BaseItem) parent.getItemAtPosition(position);
				LogUtils.e(TAG,baseItem.getString("id"));
				String order_id = baseItem.getString("id");
				new PurchaseDetailDialog(getActivity(), order_id, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				}).show();
			}
		});
		listview.setAdapter(baseAdapter);
		onloadPuchase = true;
//		onloadContract = true;
	}

	private static void onLoadContract() {
		// TODO Auto-generated method stub
		if(!onloadContract){
			HashMap<String, String> inputs = new HashMap<String, String>();
			SkyUtils.execute(activity, RequestMethod.GET, API.API_USER_CONTRACT_INFO, inputs, callbackContract);
		}else{
			Scomain.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void init(View view) {
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_p_profile_2, getResources().getColor(R.color.profile_view));//OK
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_p_contract_2, getResources().getColor(R.color.profile_view));//OK
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.btn_checkout, getResources().getColor(R.color.ec_btn_checkout));//OK
		
		isLangEng = new Setting(getActivity()).isLangEng();
		action_profile = (LinearLayout) view.findViewById(R.id.action_profile);
		action_contract = (LinearLayout) view.findViewById(R.id.action_contract);
//		action_purchase = (LinearLayout) view.findViewById(R.id.action_purchase);
		icon_p_profile = (ImageView) view.findViewById(R.id.icon_p_profile);
		icon_p_contract = (ImageView) view.findViewById(R.id.icon_p_contract);
//		icon_p_puchase = (ImageView) view.findViewById(R.id.icon_p_puchase);
		text_p_profile 	= (TextView) view.findViewById(R.id.text_p_profile);
		text_p_contract = (TextView) view.findViewById(R.id.text_p_contract);
//		text_p_puchase 	= (TextView) view.findViewById(R.id.text_p_puchase);
		onloadPuchase = false;
		onloadContract = false;
		onloadProfile = false;
		loading = CommonAndroid.getView(view, R.id.loading);
		CommonAndroid.showView(false, loading);
		CommonAndroid.getView(view, R.id.profile_first_name_v).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.action_profile).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.action_contract).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.action_purchase).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.profile_recommend_list).setOnClickListener(this);
		this.view = view;
		linearLayoutFormProfile 	= (LinearLayout) view.findViewById(R.id.form_profile);
		linearLayoutFormContract 	= (LinearLayout) view.findViewById(R.id.form_contract);
		linearLayoutFormPurchase 	= (LinearLayout) view.findViewById(R.id.form_purchase);
		Scomain 					= (ScrollView) view.findViewById(R.id.main);
		final LinearLayout newViewContract = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.user_contract_form, null);
		newViewContract.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		linearLayoutFormContract.addView(newViewContract);
		contract_bt_extend = CommonAndroid.getView(view, R.id.contract_bt_extend);
		contract_extend_confirm_admin = CommonAndroid.getView(view, R.id.contract_extend_confirm_admin);
		contract_bt_extend.setOnClickListener(this);
		
		final LinearLayout newViewPurchase = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.user_purchase_form, null);
		newViewPurchase.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		linearLayoutFormPurchase.addView(newViewPurchase);
		activity = getActivity();
		header = CommonAndroid.getView(view, R.id.header);
		header.initHeader(R.string.profile_information, R.string.profile_information_j);
		if(SkyMainActivity.FLAG_EXTRA_CHECK_BACK_PROFILE){
			header.visibivityLeft(true, R.drawable.icon_nav_back);//header_v2_icon_back
		}
		
		header.visibivityRight(true, R.drawable.icon_edit);
		CommonAndroid.getView(header, R.id.header_btn_right).setOnClickListener(this);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		
		contract_code = new Account(getActivity()).getCode();
		LogUtils.e(TAG, "contract_code==" + contract_code);
//		CommonAndroid.getView(view, R.id.button_upgrade).setOnClickListener(this);
		callbackprofile = new CheerzServiceCallBack() {

			public void onStart() {
				CommonAndroid.showView(true, loading);
				CommonAndroid.hiddenKeyBoard(getActivity());
			};

			public void onError(String message) {
				CommonAndroid.showView(false, loading);
				if (!isFinish()) {
//					SkyUtils.showDialog(getActivity(), "" + message);
					CommonAndroid.showDialog(getActivity(), SkyUtils.convertStringErr(message) , null);
				}
			};

			public void onSucces(String response) {
				CommonAndroid.showView(false, loading);
				if (!isFinish()) {
					Scomain.setVisibility(View.VISIBLE);
//					{"is_success":1,
//					"data":{"user":{"first_name":"Trang","middle_name":"Thi","last_name":"Nguyen",
//					"email":"acvtestatmark+yyy@gmail.com","mobile_mail":"aaacvtestatmark+yyy@gmail.com",
//					"lang":"en","country_phone_code":"376","tel":"05656565655",
//					"birth_date":"1988-12-14","sex":"2","country":"Viet Nam","city":"Ha Noi",
//					"postal_code":"121","address":"Ha Noi - Viet Nam","address_kana":"",
//					"recommend":"0","recommended_by":{"id":"8","name":"ITOYUYA"}}}}
					JSONObject mainJsonObject;
					try {
						mainJsonObject = new JSONObject(response);
						String is_succes = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.is_success);

						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							JSONObject jsonObject = mainJsonObject
									.getJSONObject("data").getJSONObject("user");
							baseItem = new BaseItem(jsonObject);
							onloadProfile = true;
							addView(baseItem);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			};
		};
		onLoadProfile();
		callbackContract = new CheerzServiceCallBack() {

			public void onStart() {
				CommonAndroid.showView(true, loading);
				CommonAndroid.hiddenKeyBoard(getActivity());
//				addViewContract();//test
			};

			public void onError(String message) {
				CommonAndroid.showView(false, loading);
				if (!isFinish()) {
					CommonAndroid.showDialog(getActivity(), SkyUtils.convertStringErr(message) , null);
				}
			};

			public void onSucces(String response) {
				CommonAndroid.showView(false, loading);
				if (!isFinish()) {
					Scomain.setVisibility(View.VISIBLE);
					JSONObject mainJsonObject;
					try {
						mainJsonObject = new JSONObject(response);
						String is_succes = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.is_success);

						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							JSONObject jsonObject = mainJsonObject
									.getJSONObject("data")/*.getJSONObject("contract")*/;
							baseItemContract = new BaseItem(jsonObject);
							addViewContract();
							LogUtils.i(TAG,"contract" + jsonObject.toString());
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			};
		};
	}

	/*private CheerzServiceCallBack callbackPurchase = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
			CommonAndroid.hiddenKeyBoard(getActivity());
		};

		public void onError(String message) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
//				SkyUtils.showDialog(getActivity(), "" + message);
				CommonAndroid.showDialog(getActivity(), SkyUtils.convertStringErr(message) , null);
			}
		};

		public void onSucces(String response) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				JSONObject mainJsonObject;
				try {
					mainJsonObject = new JSONObject(response);
					LogUtils.e(TAG,"purchase" + mainJsonObject.toString());
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);

					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						baseItemsPurchase.clear();
						try {
							JSONArray array = mainJsonObject.getJSONArray("data");
							for (int i = 0; i < array.length() ; i++) {
								array.getJSONObject(i).put("no", i+1);
								baseItemsPurchase.add(new BaseItem(array.getJSONObject(i)));
//								LogUtils.e(TAG,"purchase2" + array.getJSONObject(i).toString());
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						addPurchaseView(baseItemsPurchase);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};
*/
	@Override
	public void onChangeLanguage() {
		// TODO Auto-generated method stub
		LogUtils.e(TAG,"onChangeLanguage====isLangEng:" + new Setting(getActivity()).isLangEng());
		if(isLangEng != new Setting(getActivity()).isLangEng()){
			switch (isTab) {
			case 0:
				onloadProfile = false;
				onLoadProfile();
				break;
			case 1:
				onloadContract = false;
				onLoadContract();
				break;
			default:
				break;
			}
			isLangEng = new Setting(getActivity()).isLangEng();
		}
		
	}

	public static void addView(BaseItem baseItem2_) throws ParseException {
		// TODO Auto-generated method stub
		try {
			JSONObject data = baseItem2_.getObject();
//			LogUtils.e(TAG, "response" + data.getString("first_name"));
			((TextView) CommonAndroid.getView(view, R.id.profile_full_name_v)).setText(data.getString("full_name"));
			((TextView) CommonAndroid.getView(view, R.id.profile_id_v)).setText(data.getString("id"));
			((TextView) CommonAndroid.getView(view, R.id.profile_first_name_v)).setText(data.getString("first_name"));
			((TextView) CommonAndroid.getView(view, R.id.profile_middle_name_v)).setText(data.getString("middle_name"));
			((TextView) CommonAndroid.getView(view, R.id.profile_last_name_v)).setText(data.getString("last_name"));
			((TextView) CommonAndroid.getView(view, R.id.profile_email_v)).setText(data.getString("email"));
			((TextView) CommonAndroid.getView(view, R.id.profile_mobile_mail_v)).setText(data.getString("mobile_mail"));
			((TextView) CommonAndroid.getView(view, R.id.profile_tel_v)).setText(data.getString("tel"));
			String dtStart = data.getString("birth_date");  
			try {
//				SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy");  //yyyy-MM-dd
//				Date date = format.parse(dtStart);  
//				LogUtils.e(TAG,"date" + date.getTime());
				((TextView) CommonAndroid.getView(view, R.id.profile_date_of_birth_v)).setText( CommonAndroid.convertDate(activity, dtStart, 0)/*new SimpleDateFormat("dd/MM/yyyy")
				.format(date.getTime())*/);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(data.getString("sex").equals("1")){
				if(new Setting(activity).isLangEng()){
					((TextView) CommonAndroid.getView(view, R.id.profile_sex_v)).setText(activity.getResources().getString(R.string.profile_sex_male));
				}else{
					((TextView) CommonAndroid.getView(view, R.id.profile_sex_v)).setText(activity.getResources().getString(R.string.profile_sex_male_j));
				}
			}else{
				if(new Setting(activity).isLangEng()){
					((TextView) CommonAndroid.getView(view, R.id.profile_sex_v)).setText(activity.getResources().getString(R.string.profile_sex_female));
				}else{
					((TextView) CommonAndroid.getView(view, R.id.profile_sex_v)).setText(activity.getResources().getString(R.string.profile_sex_female_j));
				}
			}
			
			((TextView) CommonAndroid.getView(view, R.id.profile_country_v)).setText(data.getString("country"));
			((TextView) CommonAndroid.getView(view, R.id.profile_city_v)).setText(data.getString("city"));
			((TextView) CommonAndroid.getView(view, R.id.profile_postal_code_v)).setText(data.getString("postal_code"));
			((TextView) CommonAndroid.getView(view, R.id.profile_address_v)).setText(data.getString("address"));
			((TextView) CommonAndroid.getView(view, R.id.profile_address_kana_v)).setText(data.getString("address_kana"));
			String profile_recommended_by_v = "";
			try {
				profile_recommended_by_v = data.getJSONObject("recommended_by").getString("name");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//			((TextView) CommonAndroid.getView(view, R.id.profile_recommended_by_v)).setText(profile_recommended_by_v /*data.getString("recommended_by")*/ );
			try {
				recommend = Integer.parseInt(data.getString("recommend"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			((TextView) CommonAndroid.getView(view, R.id.profile_recommend_v)).setText(data.getString("recommend"));//recommend
			/*if(data.getString("lang").equals("en")){
				((TextView) CommonAndroid.getView(view, R.id.profile_language_v)).setText(getActivity().getResources().getString(R.string.menu_lang_english));
			}else{
				((TextView) CommonAndroid.getView(view, R.id.profile_language_v)).setText(getActivity().getResources().getString(R.string.menu_lang_japanese));
			}*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void addViewContract() {
		// TODO Auto-generated method stub
		try {
			onloadContract = true;
			JSONObject data = baseItemContract.getObject();
			((TextView) CommonAndroid.getView(view, R.id.contract_status_v)).setText(data.getString("status"));
			((TextView) CommonAndroid.getView(view, R.id.contract_grade_v)).setText(data.getString("grade"));
			if("1".equals( data.getString("extendable") )){
				contract_bt_extend.setVisibility(View.VISIBLE);
			}else{
				contract_bt_extend.setVisibility(View.GONE);
			}
			if("1".equals( data.getString("extend_cancelable"))){
				contract_extend_confirm_admin.setVisibility(View.VISIBLE);
			}else{
				contract_extend_confirm_admin.setVisibility(View.GONE);
			}
//			((TextView) CommonAndroid.getView(view, R.id.contract_payment_method_v)).setText(data.getString("payment_method"));
//			((TextView) CommonAndroid.getView(view, R.id.contract_registered_date_v)).setText( CommonAndroid.convertDate(getActivity(),data.getString("registered_date"),0) );
//			((TextView) CommonAndroid.getView(view, R.id.contract_start_date_v)).setText( CommonAndroid.convertDate(getActivity(), data.getString("start_date"),1) );
//			((TextView) CommonAndroid.getView(view, R.id.contract_next_update_date_v)).setText( CommonAndroid.convertDate(getActivity(), data.getString("next_update_date"),1) );
//			((TextView) CommonAndroid.getView(view, R.id.contract_update_time_v)).setText( CommonAndroid.convertDate(getActivity(), data.getString("update_time"),0) );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void addminConfirm(){
		contract_bt_extend.setVisibility(View.GONE);
		contract_extend_confirm_admin.setVisibility(View.VISIBLE);
		onloadContract = false;
		onLoadContract();
	}
	
	public static void backLoad(){
		onloadContract = false;
		onLoadContract();
	}
	
	
}