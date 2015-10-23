package org.com.atmarkcafe.sky.dialog;

import java.util.HashMap;

import org.com.atmarkcafe.sky.fragment.LoginFragment;
import org.com.atmarkcafe.sky.fragment.ProfileFragment;
import org.json.JSONArray;
import org.json.JSONException;
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
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;

public class ContractUpdateAnnualDialog extends BaseAdialog implements android.view.View.OnClickListener {
	static RelativeLayout main_detail;
	private WebView credit;
	protected static final String TAG = "ContractUpdateAnnualDialog";
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderView header;
	HashMap<String, String> inputs = new HashMap<String, String>();
	private static FragmentActivity activity;
	public static int[] value_grade 			= {2,1}; //2 1
	public static int[] value_paymentmethod1 	= {1,2}; //1 2
	public static int[] value_paymentmethod2 	= {3,2}; //3 2
	private static int contract_grade_value_default 			= 0;
	private static int contract_paymentmethod1_value_default 	= 0;
	private static int contract_paymentmethod2_value_default 	= 0;
	private static int contract_grade_value 					= 0;
	private static int contract_paymentmethod1_value 			= 0;
	private static int contract_paymentmethod2_value 			= 0;
	private int Contract_key = 1;
	private static String[] items;
	private static String[] items2;
	private static String[] items3;
	TextView contract_update_header_des1_1, contract_update_header_des1_2, contract_coupon_code_des, contract_only_credit, contract_description1, contract_description2, button_submit, contract_paymentmethod_title, contract_description0;
	EditText contract_coupon_code;
	CheckBox checkbox;
	LinearLayout contract_coupon_code_title;
	RelativeLayout contract_paymentmethod1_g, contract_paymentmethod2_g;
	LinearLayout profile_main, footer;
	Boolean is_check_child = false;
	String axes_lang = "";
	JSONArray payment_info = null;
	public ContractUpdateAnnualDialog(FragmentActivity activity_ , int contract_key, OnClickListener clickListener) {
		super(activity_, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		activity = activity_;
		LogUtils.i(TAG, "page_code==" + contract_key);
		this.Contract_key = contract_key;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.visibivityLeft(false, R.drawable.icon_nav_back);
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(header, R.id.header_btn_right).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		
		profile_main = CommonAndroid.getView(main_detail, R.id.profile_main);
		footer = CommonAndroid.getView(main_detail, R.id.footer);
		CommonAndroid.getView(main_detail, R.id.contract_grade).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.contract_paymentmethod1).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.contract_paymentmethod2).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.button_submit).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.button_reset).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.tearm_content).setOnClickListener(this);
		
		contract_update_header_des1_1 	= CommonAndroid.getView(main_detail, R.id.contract_update_header_des1_1);
		contract_update_header_des1_2 	= CommonAndroid.getView(main_detail, R.id.contract_update_header_des1_2);
		contract_only_credit 			= CommonAndroid.getView(main_detail, R.id.contract_only_credit);
		contract_update_header_des1_1 	= CommonAndroid.getView(main_detail, R.id.contract_update_header_des1_1);
		contract_description1 			= CommonAndroid.getView(main_detail, R.id.contract_description1);
		contract_description2			= CommonAndroid.getView(main_detail, R.id.contract_description2);
		contract_coupon_code_title 		= CommonAndroid.getView(main_detail, R.id.contract_coupon_code_title);
		contract_coupon_code 			= CommonAndroid.getView(main_detail, R.id.contract_coupon_code);
		contract_coupon_code_des 		= CommonAndroid.getView(main_detail, R.id.contract_coupon_code_des);
		checkbox 						= CommonAndroid.getView(main_detail, R.id.checkbox);
		button_submit						= CommonAndroid.getView(main_detail, R.id.button_submit);
		contract_paymentmethod_title	= CommonAndroid.getView(main_detail, R.id.contract_paymentmethod_title);
		contract_paymentmethod1_g 		= CommonAndroid.getView(main_detail, R.id.contract_paymentmethod1_g);
		contract_paymentmethod2_g 		= CommonAndroid.getView(main_detail, R.id.contract_paymentmethod2_g);
		
		contract_description0 			= CommonAndroid.getView(main_detail, R.id.contract_description0);
		
		/*items = new String[] { getActivity().getResources().getString(R.string.contract_grade_value1),getActivity().getResources().getString(R.string.contract_grade_value2) };
		if (!new Setting(getActivity()).isLangEng()) {
			items = new String[] { getActivity().getResources().getString(R.string.contract_grade_value1_j) ,getActivity().getResources().getString(R.string.contract_grade_value2_j) };
		}
		items2 = new String[] { getActivity().getResources().getString(R.string.contract_update_paymentmethod_value11),getActivity().getResources().getString(R.string.contract_update_paymentmethod_value12) };
		if (!new Setting(getActivity()).isLangEng()) {
			items2 = new String[] { getActivity().getResources().getString(R.string.contract_update_paymentmethod_value11_j) ,getActivity().getResources().getString(R.string.contract_update_paymentmethod_value12_j) };
		}
		items3 = new String[] { getActivity().getResources().getString(R.string.contract_update_paymentmethod_value21),getActivity().getResources().getString(R.string.contract_update_paymentmethod_value22) };
		if (!new Setting(getActivity()).isLangEng()) {
			items3 = new String[] { getActivity().getResources().getString(R.string.contract_update_paymentmethod_value21_j) ,getActivity().getResources().getString(R.string.contract_update_paymentmethod_value22_j) };
		}
		
		contract_grade_value_default = value_grade[0];
		contract_paymentmethod1_value_default = value_paymentmethod1[0];
		contract_paymentmethod2_value_default = value_paymentmethod2[0];
		resetForm();*/
		
		switch (Contract_key) {
			case KEY_CONTRACT.extend_before_expire:
				header.initHeader(R.string.contract_update_annual, R.string.contract_update_annual_j);
				contract_coupon_code_title.setVisibility(View.VISIBLE);
				contract_coupon_code.setVisibility(View.VISIBLE);
				contract_coupon_code_des.setVisibility(View.VISIBLE);
				contract_paymentmethod_title.setVisibility(View.VISIBLE);
				contract_paymentmethod1_g.setVisibility(View.VISIBLE);
				contract_description2.setVisibility(View.VISIBLE);
				button_submit.setText(new Setting(getActivity()).isLangEng() ? getActivity().getResources().getString(R.string.contract_bt_extend) : getActivity().getResources().getString(R.string.contract_bt_extend_j)) ;
				break;
			case KEY_CONTRACT.update_contract_unpaid:
				header.initHeader(R.string.contract_update_unpaid, R.string.contract_update_unpaid_j);
				contract_paymentmethod_title.setVisibility(View.VISIBLE);
				contract_paymentmethod1_g.setVisibility(View.VISIBLE);
				contract_description2.setVisibility(View.VISIBLE);
				button_submit.setText(new Setting(getActivity()).isLangEng() ? getActivity().getResources().getString(R.string.contract_bt_register) : getActivity().getResources().getString(R.string.contract_bt_register_j)) ;
				break;
			case KEY_CONTRACT.extend_after_expire:
				header.initHeader(R.string.contract_extend_after_expire, R.string.contract_extend_after_expire_j);
				contract_update_header_des1_1.setVisibility(View.VISIBLE);
				contract_update_header_des1_2.setVisibility(View.VISIBLE);
				contract_paymentmethod2_g.setVisibility(View.VISIBLE);
				contract_only_credit.setVisibility(View.GONE);
				button_submit.setText(new Setting(getActivity()).isLangEng() ? getActivity().getResources().getString(R.string.contract_bt_register) : getActivity().getResources().getString(R.string.contract_bt_register_j)) ;
				break;
		default:
			break;
		}
		onLoadContract();
	}
	
	private void onLoadContract() {
		// TODO Auto-generated method stub
		try {
			JSONObject objParam = new JSONObject();//root key: param
			objParam.put("code", Contract_key);
			SkyUtils.execute(getActivity(), RequestMethod.POST, API.API_CONTRACT_FORM, SkyUtils.paramRequest(getActivity(), objParam), callbackContractForm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private CheerzServiceCallBack callbackContractForm = new CheerzServiceCallBack() {

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
					JSONObject data = mainJsonObject.getJSONObject("data");
					try {
						if(data.has("payment_info")){
							payment_info = data.getJSONArray("payment_info");
						}
					} catch (Exception e) {}
					viewDataFromServer(data);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};
	
	private void resetForm(){
		try {
			contract_grade_value 			= contract_grade_value_default;
			contract_paymentmethod1_value 	= contract_paymentmethod1_value_default;
			contract_paymentmethod2_value 	= contract_paymentmethod2_value_default;
			addViewOption1(contract_grade_value_default , contract_grade_value_default == value_grade[0] ? items[0] : items[1]);
			if(Contract_key == KEY_CONTRACT.update_contract_unpaid | Contract_key == KEY_CONTRACT.extend_before_expire){
				if(value_paymentmethod1.length > 1){
					addViewOption2(contract_paymentmethod1_value_default , contract_paymentmethod1_value_default == value_paymentmethod1[0] ? items2[0] :items2[1]);
				}
			}
			if(value_paymentmethod2.length > 1){
				addViewOption3(contract_paymentmethod2_value_default , contract_paymentmethod2_value_default == value_paymentmethod2[0] ? items3[0] : items3[1]);
			}
			
			contract_coupon_code.setText("");
			if(checkbox.isChecked()){
				checkbox.setChecked(false);
			}
			setFormPayment(contract_grade_value, contract_paymentmethod1_value);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void setFormPayment(int contract_type, int payment_method) {
//		payment_method = contract_paymentmethod1_value == 1 ? contract_paymentmethod1_value : 2;
		LogUtils.i(TAG, "contract_type==" + contract_type +":payment_method==" + payment_method);
		LinearLayout payment_f = (LinearLayout) CommonAndroid.getView(main_detail, R.id.payment_info);
		try {
			Boolean check_credit =  contract_paymentmethod1_value == value_paymentmethod1[0] | (contract_paymentmethod1_value == value_paymentmethod1[1]  &&  contract_paymentmethod2_value == value_paymentmethod2[0] ) ;
			if( Contract_key == KEY_CONTRACT.update_contract_unpaid | payment_info == null | payment_info.length() <= 0) {
				try {
					for(int i = 0; i < payment_info.length();i++){
						if(contract_type == payment_info.getJSONObject(i).getInt("contract_type")  &&  payment_method == payment_info.getJSONObject(i).getInt("payment_method") ){
							((TextView) CommonAndroid.getView(main_detail, R.id.payment_info_s_date)).setText( CommonAndroid.convertDate(getActivity(), payment_info.getJSONObject(i).getString("contract_start_date"),0) );
							((TextView) CommonAndroid.getView(main_detail, R.id.payment_info_e_date)).setText( CommonAndroid.convertDate(getActivity(), payment_info.getJSONObject(i).getString("contract_end_date"), 0) );
							((TextView) CommonAndroid.getView(main_detail, R.id.payment_info_amount)).setText( CommonAndroid.formatPriceEC(getActivity(), payment_info.getJSONObject(i).getString("amount")) );
							
							LinearLayout month_info = (LinearLayout) CommonAndroid.getView(main_detail, R.id.month_info);
							if(check_credit){
								month_info.setVisibility(View.VISIBLE);
								((TextView) CommonAndroid.getView(main_detail, R.id.payment_info_amount_month)).setText(  CommonAndroid.formatPriceEC(getActivity(), payment_info.getJSONObject(i).getString("amount_this_month")) );
								((TextView) CommonAndroid.getView(main_detail, R.id.payment_info_amount_month_next_text)).setText( payment_info.getJSONObject(i).getString("text_next_month") );
								((TextView) CommonAndroid.getView(main_detail, R.id.payment_info_amount_month_next)).setText(  CommonAndroid.formatPriceEC(getActivity(), payment_info.getJSONObject(i).getString("amount_next_month")) );
								RelativeLayout month_next_note = (RelativeLayout) CommonAndroid.getView(main_detail, R.id.month_next_note);
								if(payment_info.getJSONObject(i).has("text_next_month_note")){
									month_next_note.setVisibility(View.VISIBLE);
									((TextView) CommonAndroid.getView(main_detail, R.id.payment_info_amount_month_next_note)).setText( payment_info.getJSONObject(i).getString("text_next_month_note") );
								}else{
									month_next_note.setVisibility(View.GONE);
								}
							}else{
								month_info.setVisibility(View.GONE);
							}
							
							payment_f.setVisibility(View.VISIBLE);
							return;
						}
					}
				} catch (Exception e) {
					payment_f.setVisibility(View.GONE);
				}
				
			}else{
				payment_f.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			payment_f.setVisibility(View.GONE);
		}
		
	}

	public void addViewOption1(int option, String value) {
		// TODO Auto-generated method stub
		try {
			contract_grade_value = option;
			if(contract_paymentmethod1_value == value_paymentmethod1[0]){
				setFormPayment(contract_grade_value, contract_paymentmethod1_value);
			}else{
				setFormPayment(contract_grade_value, contract_paymentmethod2_value);
			}
			
			if(!"".equals(value)){
				((TextView) CommonAndroid.getView(main_detail, R.id.contract_grade)).setText( value );
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addViewOption2(int option, String value) {
		// TODO Auto-generated method stub
		try {
			contract_paymentmethod1_value = option;
			if(!"".equals(value)){
				((TextView) CommonAndroid.getView(main_detail, R.id.contract_paymentmethod1)).setText( value );
			}
			
//			if value_paymentmethod1 ==  option1 ==> value_paymentmethod2 = option1
			LogUtils.i(TAG,"option====" + option + "::" + value_paymentmethod1[0] + "::" + value_paymentmethod1[1]);
			if(is_check_child){
				if(option == value_paymentmethod1[0]){
					contract_paymentmethod2_value = value_paymentmethod2[0];
					addViewOption3(value_paymentmethod2[0], items3[0]);
					contract_paymentmethod2_g.setVisibility(View.GONE);
					contract_only_credit.setVisibility(View.VISIBLE);
					contract_description0.setVisibility(View.GONE);
					contract_description2.setVisibility(View.VISIBLE);
					setFormPayment(contract_grade_value, contract_paymentmethod1_value);
				}else{
					LogUtils.i(TAG,"is_check_child====++" + is_check_child);
					contract_paymentmethod2_g.setVisibility(View.VISIBLE);
					contract_only_credit.setVisibility(View.GONE);
					setFormPayment(contract_grade_value, value_paymentmethod2[0]);
				}
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addViewOption3(int option, String value) {
		// TODO Auto-generated method stub
		try {
			contract_paymentmethod2_value = option;
			setFormPayment(contract_grade_value, contract_paymentmethod2_value);
			if(!"".equals(value)){
				((TextView) CommonAndroid.getView(main_detail, R.id.contract_paymentmethod2)).setText( value );
			}
			if(option == value_paymentmethod2[0]){
				contract_description0.setVisibility(View.GONE);
				contract_description2.setVisibility(View.VISIBLE);
			}else{
				contract_description0.setVisibility(View.VISIBLE);
				contract_description2.setVisibility(View.GONE);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed(){
		try {
			if(Contract_key == KEY_CONTRACT.update_contract_unpaid){
				new Setting(getActivity()).saveLang(R.id.eng);
				LoginFragment.updateCheckBoxLanguage();
			}
		} catch (Exception e) {}
		if(Contract_key == KEY_CONTRACT.extend_before_expire){
			try {
				ProfileFragment.backLoad();
			} catch (Exception e) {}
		}
		close(true);
	}
	
	private void update() {
		// TODO Auto-generated method stub
//		inputs.put("req[param][holder_name]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_name)).getText().toString().trim() );
//		inputs.put("req[param][number]", 			((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_cardnum)).getText().toString().trim());
//		inputs.put("req[param][security_code]", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_code)).getText().toString().trim());
//		CommonAndroid.showView(true, loading);
//		close(true);
		axes_lang 		= "en";
		if(!new Setting(activity).isLangEng()){
			axes_lang 		= "ja";
		}
		LogUtils.i(TAG, "contract_grade_value==" + contract_grade_value + "::contract_paymentmethod1_value==" + contract_paymentmethod1_value +"::contract_paymentmethod2_value==" + contract_paymentmethod2_value);
		if(contract_paymentmethod1_value == value_paymentmethod1[0] | (contract_paymentmethod1_value == value_paymentmethod1[1]  &&  contract_paymentmethod2_value == value_paymentmethod2[0] )){
			//credit_AX
			String token = new Account(getActivity()).getToken();
			String code = "" +Contract_key;
			String md5TokenCode = CommonAndroid.createHash(String.format("%s%sspmcpaymentapi",token,code ));
			int _gradeValue = contract_grade_value; //new Account(getActivity()).getContractType();
			int payment_method = 0;
			if(contract_paymentmethod1_value == value_paymentmethod1[0])
				payment_method = value_paymentmethod1[0];
			else
				payment_method = value_paymentmethod2[0];
			String URL = String.format(SkyUtils.API.BASESERVER_ + "paymentApi?token_key=%s&code=%s&token_code=%s&contract_type=%s&payment_method=%s&lang=%s", token , code, md5TokenCode,_gradeValue, payment_method, axes_lang);
			Uri uri = Uri.parse(URL);
			LogUtils.i(TAG, "URL==" + URL);
	        getActivity().startActivity(new Intent(Intent.ACTION_VIEW, uri));
		}else{
			//bank
			updateBank();
		}
		
	}
	
	private void updateBank() {
		// TODO Auto-generated method stub
		try {
			String couponCode = contract_coupon_code.getText().toString().trim();
			JSONObject objParam = new JSONObject();
			objParam.put("contract_type", contract_grade_value /*new Account(getActivity()).getContractType()*/);
			objParam.put("code", Contract_key);
			objParam.put("coupon_code", couponCode);
			SkyUtils.execute(getActivity(), RequestMethod.POST, API.API_CONTARCT_UPDATE_BANK, SkyUtils.paramRequest(getActivity(), objParam), callbackContractBank);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private CheerzServiceCallBack callbackContractBank = new CheerzServiceCallBack() {

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
					LogUtils.i(TAG, "callbackContractBank==" + mainJsonObject.toString());
//					JSONObject data = mainJsonObject.getJSONObject("data");
					//not tran yes
					close(true);
					if(Contract_key != KEY_CONTRACT.extend_before_expire){
						new ContractUpdateNotTransferYetDialog(getActivity(), KEY_CONTRACT.not_transfer_yet , new DialogInterface.OnClickListener() {
							@SuppressLint("NewApi")
							@Override
							public void onClick(DialogInterface dialog, int which) {

							}
						}).show();
					}else{
						ProfileFragment.addminConfirm();
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};

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
		return R.layout.v2_update_annual_contract;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			try {
				if(Contract_key == KEY_CONTRACT.update_contract_unpaid){
					new Setting(getActivity()).saveLang(R.id.eng);
					LoginFragment.updateCheckBoxLanguage();
				}
			} catch (Exception e) {}
			if(Contract_key == KEY_CONTRACT.extend_before_expire){
				try {
					ProfileFragment.backLoad();
				} catch (Exception e) {}
			}
			close(true);
			break;
		case R.id.button_submit:
			if(checkbox.isChecked()){
				String[] items_confirm = new String[] { getActivity().getResources().getString(R.string.tlt_ok),getActivity().getResources().getString(R.string.tlt_cancel) };
				if (!new Setting(getActivity()).isLangEng()) {
					items_confirm = new String[] { getActivity().getResources().getString(R.string.tlt_ok_j) ,getActivity().getResources().getString(R.string.tlt_cancel_j) };
				}
				String title = getActivity().getResources().getString(R.string.contract_dialog_confirm);
				if (!new Setting(getActivity()).isLangEng()) {
					title = getActivity().getResources().getString(R.string.contract_dialog_confirm_j);
				}
				
				if(Contract_key == KEY_CONTRACT.extend_before_expire){
					title = getActivity().getResources().getString(R.string.contract_dialog_confirm_extend);
					if (!new Setting(getActivity()).isLangEng()) {
						title = getActivity().getResources().getString(R.string.contract_dialog_confirm_extend_j);
					}
				}
				new ContractUpdateConfirmDialog(getActivity(), items_confirm, title , new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							update();
						} else if (which == 1) {
							
						}
					}
				}).show();
				
			}else{
				String validate_team = getActivity().getResources().getString(R.string.membership_team);
				if(!new Setting(getActivity()).isLangEng()){
					validate_team = getActivity().getResources().getString(R.string.membership_team_j);
				}
				SkyUtils.showDialog(getActivity(), validate_team,null);
			}
			
			break;
		case R.id.button_reset:
			resetForm();
			break;
		case R.id.contract_grade:
//			ContractUpdateOption1Dialog popup = new ContractUpdateOption1Dialog();
//			popup.show(getActivity().getSupportFragmentManager(), "popup");
			final String option1 = items[0];
			final String option2 = items[1];
			new ContractUpdateOptionDialog(getActivity(), items, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						addViewOption1(value_grade[0], option1);
					} else if (which == 1) {
						addViewOption1(value_grade[1], option2);
					}
				}
			}).show();
			break;
		case R.id.contract_paymentmethod1:
//			ContractUpdateOption2Dialog popup2 = new ContractUpdateOption2Dialog();
//			popup2.show(getActivity().getSupportFragmentManager(), "popup");
			final String option12 = items2[0];
			final String option22 = items2[1];
			new ContractUpdateOptionDialog(getActivity(), items2, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						LogUtils.i(TAG, "select-----------1");
						addViewOption2(value_paymentmethod1[0], option12);
					} else if (which == 1) {
						LogUtils.i(TAG, "select-----------2");
						addViewOption2(value_paymentmethod1[1], option22);
					}
				}
			}).show();
			break;
		case R.id.contract_paymentmethod2:
//			ContractUpdateOption3Dialog popup3 = new ContractUpdateOption3Dialog();
//			popup3.show(getActivity().getSupportFragmentManager(), "popup");
			final String option13 = items3[0];
			final String option23 = items3[1];
			new ContractUpdateOptionDialog(getActivity(), items3, new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
						addViewOption3(value_paymentmethod2[0], option13);
					} else if (which == 1) {
						addViewOption3(value_paymentmethod2[1], option23);
					}
					
				}
			}).show();
			break;
		case R.id.tearm_content:
			String term_and_condition = "abc";
			//show team
			new ContractUpdateTearmDialog(getActivity(),term_and_condition ,new DialogInterface.OnClickListener() {
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
		return activity;
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
	
	protected void viewDataFromServer(JSONObject data) {
		// TODO Auto-generated method stub
		LogUtils.i(TAG, "data==" + data.toString());
		try {
			JSONArray group = data.getJSONArray("group");
			items = new String[group.length()];
			value_grade = new int[group.length()];
			for(int i = 0; i < group.length(); i++){
				items[i] 		= group.getJSONObject(i).getString("text");
				value_grade[i]	= Integer.parseInt( group.getJSONObject(i).getString("code") );
			}
			
			JSONArray payment_method = data.getJSONArray("payment_method");
			items2 = new String[payment_method.length()];
			value_paymentmethod1 = new int[payment_method.length()];
			for(int i = 0; i< payment_method.length();i++){
				items2[i] 		= payment_method.getJSONObject(i).getString("text");
				if(!payment_method.getJSONObject(i).has("code")){
					value_paymentmethod1[i] = 0/*i + 5*/;
				}else{
					value_paymentmethod1[i] = payment_method.getJSONObject(i).getInt("code");
				}
				try {
					if(payment_method.getJSONObject(i).has("child")){
						is_check_child = true;
						JSONArray child = payment_method.getJSONObject(i).getJSONArray("child");
						items3 = new String[child.length()];
						value_paymentmethod2 = new int[child.length()];
						for(int j = 0; j < child.length(); j++){
							items3[j] 				= child.getJSONObject(j).getString("text");
							value_paymentmethod2[j]	= Integer.parseInt( child.getJSONObject(j).getString("code") );
//							LogUtils.i(TAG,items3[j] + "--" + value_paymentmethod2[j] );
						}
						LogUtils.i(TAG,"is_check_child==" + is_check_child + ":::value_paymentmethod2==" + value_paymentmethod2[0] );
					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			if(!is_check_child){
				items3 = new String[value_paymentmethod1.length];
				value_paymentmethod2 = new int[value_paymentmethod1.length];
				for(int i = 0; i< value_paymentmethod1.length;i++){
					items3[i] = items2[i];
					value_paymentmethod2[i] = value_paymentmethod1[i];
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*items = new String[] { getActivity().getResources().getString(R.string.contract_grade_value1),getActivity().getResources().getString(R.string.contract_grade_value2) };
		if (!new Setting(getActivity()).isLangEng()) {
			items = new String[] { getActivity().getResources().getString(R.string.contract_grade_value1_j) ,getActivity().getResources().getString(R.string.contract_grade_value2_j) };
		}*/
		
		/*items2 = new String[] { getActivity().getResources().getString(R.string.contract_update_paymentmethod_value11),getActivity().getResources().getString(R.string.contract_update_paymentmethod_value12) };
		if (!new Setting(getActivity()).isLangEng()) {
			items2 = new String[] { getActivity().getResources().getString(R.string.contract_update_paymentmethod_value11_j) ,getActivity().getResources().getString(R.string.contract_update_paymentmethod_value12_j) };
		}*/
		
		/*items3 = new String[] { getActivity().getResources().getString(R.string.contract_update_paymentmethod_value21),getActivity().getResources().getString(R.string.contract_update_paymentmethod_value22) };
		if (!new Setting(getActivity()).isLangEng()) {
			items3 = new String[] { getActivity().getResources().getString(R.string.contract_update_paymentmethod_value21_j) ,getActivity().getResources().getString(R.string.contract_update_paymentmethod_value22_j) };
		}*/
		
		contract_grade_value_default = value_grade[0];
		contract_paymentmethod1_value_default = value_paymentmethod1[0];
		contract_paymentmethod2_value_default = value_paymentmethod2[0];
		resetForm();
		profile_main.setVisibility(View.VISIBLE);
		footer.setVisibility(View.VISIBLE);
		LogUtils.i(TAG,"viewDataFromServer=======");
	}

}