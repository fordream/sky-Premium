package org.com.atmarkcafe.sky.dialog;

import java.util.HashMap;

import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.Base2Adialog;
import z.lib.base.BaseItem;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

public class MyAccountAddressUpdateEditDialog extends Base2Adialog implements android.view.View.OnClickListener {
	RelativeLayout main_detail;
	protected static final String TAG = "MyAccountAddressUpdateEditDialog";
	private static Dialog SelectChooserDialog = null;
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	private BaseItem address = null;
	private String id_address;
	FragmentActivity activity;
	String[] select_items_id = null;
	String[] select_items_name = null;
	String selected_items_id = "";
	private LinearLayout childView;
	private LinearLayout parentView;
	public MyAccountAddressUpdateEditDialog(FragmentActivity context, BaseItem baseItem, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.address = baseItem;
		this.activity = context;
		LogUtils.e(TAG, "address_delivery==" + address);
		MyAccountMyAddressDialog.showView(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_addressupdate);
		childView = (LinearLayout)main_detail.findViewById(R.id.main_addressupdate_id);
		header = CommonAndroid.getView(main_detail, R.id.header);
		if(address == null){
			header.initHeader(R.string.ec_address_new_header, R.string.ec_address_new_header_j);
		}else{
			header.initHeader(R.string.myaccount_address_edit_header, R.string.myaccount_address_edit_header_j);
			onLoadView(address);
		}
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);//icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(header, R.id.header_btn_right).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.ec_address_bt_save).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.ec_address_new_state).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		Log.i(TAG, "MyAccountAddressUpdateEditDialog == onCreate");
		parentView = CommonAndroid.getView(main_detail, R.id.main_addressupdate_sub_id);
		CommonAndroid.TouchViewhiddenKeyBoard(getActivity(), parentView);
		getListState();
	}
	
	private void getListState(){
		HashMap<String, String> inputs = new HashMap<String, String>();
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_GET_LIST_STATE, inputs, new CheerzServiceCallBack() {
			public void onStart() {
				CommonAndroid.showView(true, loading);
				CommonAndroid.hiddenKeyBoard(getActivity());
			};

			public void onError(String message) {
				CommonAndroid.showView(false, loading);
				if (!isFinish()) {
					SkyUtils.showDialog(getActivity(), "" + message,null);
				}
			};

			public void onSucces(String response) {
				CommonAndroid.showView(false, loading);
				if (!isFinish()) {
					JSONObject mainJsonObject;
					try {
						mainJsonObject = new JSONObject(response);
						LogUtils.e(TAG, "LIST STATE==" + mainJsonObject.toString());
						String is_succes = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.is_success);
						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							try {
								JSONArray array = mainJsonObject.getJSONArray("data");
								select_items_id = new String[array.length()];
								select_items_name = new String[array.length()];
								String state_name_default = "";
								String state = ((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_state)).getText().toString().trim();
								for (int i = 0; i < array.length() ; i++) {
									select_items_id[i] = array.getJSONObject(i).getString("id");
									select_items_name[i] = array.getJSONObject(i).getString("name");
									if(state.equals(array.getJSONObject(i).getString("name"))){
										selected_items_id 	=	array.getJSONObject(i).getString("id");
										state_name_default 	= 	array.getJSONObject(i).getString("name");
									}
								}
								if(!"".equals(state_name_default)){
									((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_state)).setText( state_name_default );
								}else{
									((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_state)).setText("");
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}

		
		});
	}
	
	@Override
	public void onBackPressed(){
		MyAccountMyAddressDialog.showView(true);
		close(true);
	}
	
	private void update() {
		// TODO Auto-generated method stub
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][alias]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_alias)).getText().toString().trim() );
		inputs.put("req[param][firstname]", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_fname)).getText().toString().trim());
		inputs.put("req[param][lastname]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_lname)).getText().toString().trim());
		inputs.put("req[param][company]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_company)).getText().toString().trim());
		inputs.put("req[param][address1]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_address)).getText().toString().trim());
		inputs.put("req[param][address2]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_address2)).getText().toString().trim() );
		inputs.put("req[param][postcode]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_zipcode)).getText().toString().trim());
		inputs.put("req[param][phone]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_homephone)).getText().toString().trim());
		inputs.put("req[param][phone_mobile]", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_mobile)).getText().toString().trim());
		
		inputs.put("req[param][city]", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_city)).getText().toString().trim());
		inputs.put("req[param][id_state]", 	selected_items_id);
		inputs.put("req[param][country]", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_country)).getText().toString().trim());

//		inputs.put("req[param][id_address]", id_address);
		LogUtils.e(TAG, "data==" + inputs.toString());
		boolean checkNull_ = false;
		if(checkNull("firstname", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_new_fname) : getActivity().getString(R.string.ec_address_new_fname_j));
		}else if(checkNull("lastname", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_new_lname) : getActivity().getString(R.string.ec_address_new_lname_j));
		}
		else if(checkNull("address1", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_new_address) : getActivity().getString(R.string.ec_address_new_address_j));
		}
		else if(checkNull("postcode", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_new_zipcode) : getActivity().getString(R.string.ec_address_new_zipcode_j));
		}
		else if(checkNull("city", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_new_city) : getActivity().getString(R.string.ec_address_new_city_j));
		}
		else if("".equals(selected_items_id)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_new_state) : getActivity().getString(R.string.ec_address_new_state_j));
		}
		else if(checkNull("phone", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_new_homephone) : getActivity().getString(R.string.ec_address_new_homephone_j));
		}
		else if(checkNull("phone_mobile", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_new_mobile) : getActivity().getString(R.string.ec_address_new_mobile_j));
		}
		else if(checkNull("alias", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_new_alias) : getActivity().getString(R.string.ec_address_new_alias_j));
		}
		
		else{
			checkNull_ = true;
		}
		
		if(checkNull_){
			if(address == null){
				SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_ADDRESS_UPDATE_NEW, inputs, callbackupdate);
			}else{
				inputs.put("req[param][id_address]", id_address );
				SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_ADDRESS_UPDATE, inputs, callbackupdate);
			}
		}
	}
	
	private boolean checkNull(String param_key, HashMap<String, String> inputs){
		String value = inputs.get("req[param][" + param_key + "]");
		if("".equals(value)){
			return true;
		}
		return false;
	}
	
	private void showErr(String err_message){
		String message = err_message + (new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_validate) : getActivity().getString(R.string.ec_address_validate_j));
		SkyUtils.showDialog(getActivity(), message ,null);
	}

	private void onLoadView(BaseItem address2) {
		// TODO Auto-generated method stub
		JSONObject mainJsonObject;
		try {
			id_address =  address2.getString("id_address");
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_fname)).setText( address2.getString("firstname") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_lname)).setText( address2.getString("lastname") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_company)).setText( address2.getString("company") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_address)).setText( address2.getString("address1") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_address2)).setText( address2.getString("address2") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_zipcode)).setText( address2.getString("postcode") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_city)).setText( address2.getString("city") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_state)).setText( address2.getString("state") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_country)).setText( address2.getString("country") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_homephone)).setText( address2.getString("phone") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_mobile)).setText( address2.getString("phone_mobile") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_info)).setText( address2.getString("additional_information") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_alias)).setText( address2.getString("alias") );
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private CheerzServiceCallBack callbackupdate = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
			CommonAndroid.hiddenKeyBoard(getActivity());
		};

		public void onError(String message) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				SkyUtils.showDialog(getActivity(), SkyUtils.convertStringErrF(message),null);
			}
		};

		public void onSucces(String response) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				JSONObject mainJsonObject;
				try {
					mainJsonObject = new JSONObject(response);
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					String message = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.err_msg);
					LogUtils.e(TAG, "update==" + mainJsonObject.toString());
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						String message_done = (new Setting(getActivity()).isLangEng()) ? getActivity().getString(R.string.create_address_done) : getActivity().getString(R.string.create_address_done_j);
						SkyUtils.showDialog(getActivity(), message_done , new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								MyAccountMyAddressDialog.onLoadData();
								MyAccountMyAddressDialog.showView(true);
								close(true);
							}
						});
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};

	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.main_addressupdate), new AnimationAction() {

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
		return R.layout.v2_ec_addressupdate_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			MyAccountMyAddressDialog.showView(true);
			close(true);
			break;
		case R.id.ec_address_bt_save:
			update();
			break;
		case R.id.ec_address_new_state:
			try {
				SelectChooserDialog.dismiss();
			} catch (Exception e1) {
			}
			SelectChooserDialog = new SelectChooserDialog(getActivity(), select_items_name) {
				@Override
				public void onItemClick(int item, String text) {
					try {
						LogUtils.i(TAG, "select==" + text);
						((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_state)).setText(text);
						selected_items_id = select_items_id[item];
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			SelectChooserDialog.show();
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
	

}