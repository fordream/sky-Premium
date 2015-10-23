package org.com.atmarkcafe.sky.dialog;

import java.util.HashMap;

import org.com.atmarkcafe.sky.fragment.LoginFragment;
import org.json.JSONObject;

import z.lib.base.BaseAdialog;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.SkyUtils.KEY_CONTRACT;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

public class ContractUpdateNotTransferYetDialog extends BaseAdialog implements android.view.View.OnClickListener {
	View views;
	RelativeLayout main_detail;
	protected static final String TAG = "ContractUpdateNotTransferYetDialog";
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderView header;
	ScrollView scrolllistview;
	LinearLayout linearLayoutForm, register_another_credit_card_only;
	FragmentActivity activity;
	ScrollView main_layout;
	JSONObject mainJsonObject_profile, mainJsonObject_contract;
	private int Contract_key = 0;
	TextView contract_register_another_credit_card_2, contract_register_another_credit_card_3;
	String axes_site_code 	= "";
	String axes_lang = "";
	public ContractUpdateNotTransferYetDialog(FragmentActivity activity_, int code, OnClickListener clickListener) {
		super(activity_, clickListener);
		activity = activity_;
		Contract_key = code;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		main_layout = CommonAndroid.getView(main_detail, R.id.main_layout);
		header.visibivityLeft(false, R.drawable.icon_nav_back);
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		register_another_credit_card_only = CommonAndroid.getView(main_detail, R.id.register_another_credit_card_only);
		CommonAndroid.getView(main_detail, R.id.contract_register_another_credit_card_2).setOnClickListener(this);
		contract_register_another_credit_card_3 = CommonAndroid.getView(main_detail, R.id.contract_register_another_credit_card_3);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		linearLayoutForm = (LinearLayout) findViewById(R.id.linearLayoutForm);
		switch (Contract_key) {
		case KEY_CONTRACT.not_transfer_yet:
			header.initHeader(R.string.contract_not_tranf_yet, R.string.contract_not_tranf_yet_j);
			break;
		case KEY_CONTRACT.register_another_credit_card:
			header.initHeader(R.string.contract_register_another_credit_card, R.string.contract_register_another_credit_card_j);
			register_another_credit_card_only.setVisibility(View.VISIBLE);
		default:
			break;
		}
		onLoadProfile();
	}
	

	@Override
	public void onBackPressed(){
		new Setting(getActivity()).saveLang(R.id.eng);
		close(true);
	}
	
	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.main_detail), new AnimationAction() {

			@Override
			public void onAnimationEnd() {
				try {
					new Setting(getActivity()).saveLang(R.id.eng);
					LoginFragment.updateCheckBoxLanguage();
				} catch (Exception e) {}
				dismiss();
				if (isOnClick) {
					clickListener.onClick(null, 0);
				}
			}
		});
	}

	@Override
	public int getLayout() {
		return R.layout.contract_not_transfer_yet_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			new Setting(getActivity()).saveLang(R.id.eng);
			close(true);
			break;
		case R.id.contract_register_another_credit_card_2:
			String URL = String.format( "https://secure.axes-payments.com/cgi-bin/autok/autok.cgi?act=card_change&site_code=%s&lang=%s", axes_site_code , axes_lang);
			Uri uri = Uri.parse(URL);
			LogUtils.e(TAG, "URL==" + URL);
	        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, uri));
			break;
		default:
			break;
		}
	}
	
	private void onLoadProfile() {
		// TODO Auto-generated method stub
			HashMap<String, String> inputs = new HashMap<String, String>();
			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_PROFILE, inputs, callbackprofile);
	}
	
	private CheerzServiceCallBack callbackprofile = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
			CommonAndroid.hiddenKeyBoard(getActivity());
		};

		public void onError(String message) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				CommonAndroid.showDialog(getActivity(), SkyUtils.convertStringErr(message) , null);
			}
		};

		public void onSucces(String response) {
			if (!isFinish()) {
				JSONObject mainJsonObject;
				try {
					mainJsonObject = new JSONObject(response);
					mainJsonObject_profile = mainJsonObject
							.getJSONObject("data").getJSONObject("user");
					onLoadContract();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};
	
	private void onLoadContract() {
		// TODO Auto-generated method stub
			HashMap<String, String> inputs = new HashMap<String, String>();
			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_CONTRACT_INFO, inputs, callbackContract);
	}
	
	private CheerzServiceCallBack callbackContract = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
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
				JSONObject mainJsonObject;
				try {
					mainJsonObject = new JSONObject(response);
					mainJsonObject_contract = mainJsonObject
							.getJSONObject("data");
//					LogUtils.e(TAG, "contract==" +mainJsonObject.toString() );
					setData();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};
	
	private FragmentActivity getActivity() {
		return activity;
	}
	
	protected void setData() {
		// TODO Auto-generated method stub
//		{"is_success":1,
//		"data":{"user":{"first_name":"Trang","middle_name":"Thi","last_name":"Nguyen",
//		"email":"acvtestatmark+yyy@gmail.com","mobile_mail":"aaacvtestatmark+yyy@gmail.com",
//		"lang":"en","country_phone_code":"376","tel":"05656565655",
//		"birth_date":"1988-12-14","sex":"2","country":"Viet Nam","city":"Ha Noi",
//		"postal_code":"121","address":"Ha Noi - Viet Nam","address_kana":"",
//		"recommend":"0","recommended_by":{"id":"8","name":"ITOYUYA"}}}}
		
//		{"data":{
//		"update_time":"06\/05\/2015",
//		"extend_cancelable":0,
//		"status":"Not transfer yet",
//		"grade":"Gold",
//		"extendable":0,
//		"start_date":"04\/2013",
//		"registered_date":"01\/04\/2014",
//		"payment_method":"Bank transfer (for 12 months)",
//		"next_update_date":"04\/2015",
//		"upgrade_cancelable":0,
//		"upgradeable":0},
//		"is_success":1}
		main_layout.setVisibility(View.VISIBLE);
		try {
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_firstname)).setText(		mainJsonObject_profile.getString("first_name"));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_middlename)).setText(	mainJsonObject_profile.getString("middle_name"));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_lastname)).setText(		mainJsonObject_profile.getString("last_name"));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_email)).setText(			mainJsonObject_profile.getString("email"));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_tel)).setText(			mainJsonObject_profile.getString("tel"));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_date)).setText( 	CommonAndroid.convertDate(getActivity(),mainJsonObject_profile.getString("birth_date"), 0));	
			
			if(mainJsonObject_profile.getString("sex").equals("1")){
				if(new Setting(activity).isLangEng()){
					((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_sex)).setText(activity.getResources().getString(R.string.profile_sex_male));
				}else{
					((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_sex)).setText(activity.getResources().getString(R.string.profile_sex_male_j));
				}
			}else{
				if(new Setting(activity).isLangEng()){
					((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_sex)).setText(activity.getResources().getString(R.string.profile_sex_female));
				}else{
					((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_sex)).setText(activity.getResources().getString(R.string.profile_sex_female_j));
				}
			}
//			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_sex)).setText(			mainJsonObject_profile.getString("sex"));
			
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_status)).setText(		mainJsonObject_contract.getString("status"));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_grade)).setText(			mainJsonObject_contract.getString("grade"));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_paymentmethod)).setText(	mainJsonObject_contract.getString("payment_method"));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_regdate)).setText(		CommonAndroid.convertDate(getActivity(),mainJsonObject_contract.getString("registered_date"), 0));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_startdate)).setText(		CommonAndroid.convertDate(getActivity(),mainJsonObject_contract.getString("start_date"), 1));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_nextdate)).setText(		CommonAndroid.convertDate(getActivity(),mainJsonObject_contract.getString("next_update_date"), 1));
			((TextView) CommonAndroid.getView(main_detail, R.id.contract_not_tranf_yet_updatetime)).setText(	CommonAndroid.convertDate(getActivity(),mainJsonObject_contract.getString("update_time"), 0));
			
			if(mainJsonObject_contract.has("axes_id")){
				String axes_id = activity.getResources().getString(R.string.contract_register_another_credit_card_3);
				if(!new Setting(activity).isLangEng()){
					axes_id = activity.getResources().getString(R.string.contract_register_another_credit_card_3_j);
				}
				((TextView) CommonAndroid.getView(main_detail, R.id.contract_register_another_credit_card_3)).setText(	String.format(axes_id, mainJsonObject_contract.getString("axes_id") )	);
//				LogUtils.e(TAG, String.format(axes_id, mainJsonObject_contract.getString("axes_id") ) );
			}
			if(mainJsonObject_contract.has("axes_site_code")){
				axes_site_code 	= mainJsonObject_contract.getString("axes_site_code");
				axes_lang 		= "en";
				if(!new Setting(activity).isLangEng()){
					axes_lang 		= "ja";
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public boolean isFinish() {
		if (getActivity() == null) {
			return true;
		}

		return getActivity().isFinishing();
	}

	public void finish() {
		if (getActivity() != null) {
			getActivity().finish();
		}
	}
}