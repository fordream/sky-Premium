package org.com.atmarkcafe.sky.dialog;

import java.util.HashMap;

import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.Base2Adialog;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.Setting;

@SuppressLint("ClickableViewAccessibility")
public class EcUpdateAddressEditDialog extends Base2Adialog implements android.view.View.OnClickListener {
	RelativeLayout main_detail;
	protected static final String TAG = "EcUpdateAddressNewFragment";
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	private String address_delivery = null;
	private String address_invoice = null;
	private String address_type = null;
	private String id_address;
	String[] select_items_id = null;
	String[] select_items_name = null;
	String selected_items_id = "";
	private LinearLayout scrollView;
//	EditText edtCardName;
	private static Dialog SelectChooserDialog = null;
	private LinearLayout parentView;
	public EcUpdateAddressEditDialog(Context context, String address_delivery, String addressEditInvoice, String addressType, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.address_delivery = address_delivery;
		this.address_invoice = addressEditInvoice;
		this.address_type = addressType;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_addressupdate);
//		scrollView = (LinearLayout)main_detail.findViewById(R.id.main_addressupdate_sub_id);
//		scrollView.setOnClickListener(this);
		Log.i(TAG, "EcUpdateAddressEditDialog");
		header = CommonAndroid.getView(main_detail, R.id.header);
		if(address_delivery == null && address_invoice == null){
			header.initHeader(R.string.ec_address_new_header, R.string.ec_address_new_header_j);
		}else{
			header.initHeader(R.string.ec_address_edit_header, R.string.ec_address_edit_header_j);
			if(address_delivery != null){
				onLoadView(address_delivery);
			}else{
				onLoadView(address_invoice);
			}
		}
//		edtCardName = (EditText)main_detail.findViewById(R.id.ec_address_new_fname);
//		edtCardName.setOnClickListener(this);
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);// icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(header, R.id.header_btn_right).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.ec_address_bt_save).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.ec_address_new_state).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		parentView = CommonAndroid.getView(main_detail, R.id.main_addressupdate_id);
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
		try {
			EcUpdateAddressBookDialog.showView(true);
		} catch (Exception e) {}
		try {
			EcAddressDialog.showView(true);
		} catch (Exception e) {}
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
//		LogUtils.e(TAG, "data==" + inputs.toString());
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
			if(address_delivery == null && address_invoice == null){
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

	private void onLoadView(String address_info) {
		// TODO Auto-generated method stub
		JSONObject mainJsonObject;
		try {
			mainJsonObject = new JSONObject(address_info);
			id_address =  CommonAndroid.getString(mainJsonObject, "id_address");
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_fname)).setText( CommonAndroid.getString(mainJsonObject, "firstname") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_lname)).setText( CommonAndroid.getString(mainJsonObject, "lastname") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_company)).setText( CommonAndroid.getString(mainJsonObject, "company") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_address)).setText( CommonAndroid.getString(mainJsonObject, "address1") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_address2)).setText( CommonAndroid.getString(mainJsonObject, "address2") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_zipcode)).setText( CommonAndroid.getString(mainJsonObject, "postcode") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_city)).setText( CommonAndroid.getString(mainJsonObject, "city") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_state)).setText( CommonAndroid.getString(mainJsonObject, "state") );
//			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_country)).setText( CommonAndroid.getString(mainJsonObject, "country") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_homephone)).setText( CommonAndroid.getString(mainJsonObject, "phone") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_mobile)).setText( CommonAndroid.getString(mainJsonObject, "phone_mobile") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_info)).setText( CommonAndroid.getString(mainJsonObject, "additional_information") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_new_alias)).setText( CommonAndroid.getString(mainJsonObject, "alias") );
		} catch (JSONException e) {
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
					LogUtils.i(TAG, "update==" + mainJsonObject.toString());
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						String message_done = (new Setting(getActivity()).isLangEng()) ? getActivity().getString(R.string.create_address_done) : getActivity().getString(R.string.create_address_done_j);
						if(address_delivery == null && address_invoice == null){
//							startFragment(new EcUpdateAddressBookFragment(), null);
							//create:
							/*new EcUpdateAddressBookDialog(getActivity(), address_type ,new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();*/
							if(address_type != null){
								SkyUtils.showDialog(getActivity(), message_done , new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										EcUpdateAddressBookDialog.onLoad();
										try {
											EcUpdateAddressBookDialog.showView(true);
										} catch (Exception e) {}
										close(true);
									}
								});
								
							}
							else{
								final String address_new = mainJsonObject.getJSONObject("data").toString();
								SkyUtils.showDialog(getActivity(), message_done , new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										try {
											EcAddressDialog.showView(true);
										} catch (Exception e) {}
										Products.address_invoice = address_new;
										Products.address_delivery = address_new;
										new EcAddressDialog(getActivity(),address_new, address_new,new DialogInterface.OnClickListener() {
											@Override
											public void onClick(DialogInterface dialog, int which) {
											}
										}).show();
										close(true);
									}
								});
								
								
							}
						}else{
						    JSONObject mainJsonObjectAdd;
							try {
								if(address_type.equals(Products.address_delivery_type)){
									mainJsonObjectAdd = new JSONObject(address_delivery);
								}else{
									mainJsonObjectAdd = new JSONObject(address_invoice);
								}
								mainJsonObjectAdd.put("alias", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_alias)).getText().toString().trim() );
								mainJsonObjectAdd.put("firstname", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_fname)).getText().toString().trim());
								mainJsonObjectAdd.put("lastname", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_lname)).getText().toString().trim());
								mainJsonObjectAdd.put("company", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_company)).getText().toString().trim());
								mainJsonObjectAdd.put("address1", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_address)).getText().toString().trim());
								mainJsonObjectAdd.put("address2", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_address2)).getText().toString().trim() );
								mainJsonObjectAdd.put("postcode", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_zipcode)).getText().toString().trim());
								mainJsonObjectAdd.put("phone", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_homephone)).getText().toString().trim());
								mainJsonObjectAdd.put("phone_mobile", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_mobile)).getText().toString().trim());
								
								mainJsonObjectAdd.put("city", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_city)).getText().toString().trim());
								mainJsonObjectAdd.put("state", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_state)).getText().toString().trim());
								mainJsonObjectAdd.put("country", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_address_new_country)).getText().toString().trim());
								
								if(address_type.equals(Products.address_delivery_type)){
									EcAddressDialog.address_delivery_full = mainJsonObjectAdd.toString();//baseItem.getObject().toString();
									if(EcAddressDialog.onCheck)
										EcAddressDialog.address_invoice_temp = mainJsonObjectAdd.toString();
								}else{
									EcAddressDialog.address_invoice_temp = mainJsonObjectAdd.toString();//baseItem.getObject().toString();
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							SkyUtils.showDialog(getActivity(), message_done , new OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									try {
										EcAddressDialog.showView(true);
									} catch (Exception e) {}
									EcAddressDialog.addView(address_type, EcAddressDialog.address_delivery_full,  EcAddressDialog.address_invoice_temp);
									close(true);
								}
							});
							
						}
						
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
		return R.layout.ec_addressupdate_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			try {
				EcUpdateAddressBookDialog.showView(true);
			} catch (Exception e) {}
			try {
				EcAddressDialog.showView(true);
			} catch (Exception e) {}
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
		/*case R.id.ec_address_new_fname:
//			handlerKeyboardOnView(getActivity(), scrollView);
			CommonAndroid.hiddenKeyBoard(getActivity());
			Log.i(TAG, "HideHide");
			break;*/
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
		if (getActivity() != null) {
			getActivity().finish();
		}
	}
	
	/**
	 * hide keyboard
	 * @param activity
	 * @param v
	 */
	public void hideSoftKeyboard(Activity activity,View v) {
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    if (inputMethodManager.isAcceptingText() ) {
	    	Log.i(TAG, "hideSoftKeyboard" + v.getClass().getSimpleName());
	    	inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	/**
	 * handler keybord on view
	 * @param activity
	 * @param view
	 */
	public  void handlerKeyboardOnView(final Activity activity,View view) {

	    //Set up touch listener for non-text box views to hide keyboard.
		if (activity != null) {
		    if(!(view instanceof EditText)) {

		        view.setOnTouchListener(new OnTouchListener() {

		            public boolean onTouch(View v, MotionEvent event) {
		            	hideSoftKeyboard(activity,v);
		                return false;
		            }

		        });
		    }
		}
		 //If a layout container, iterate over children
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                handlerKeyboardOnView(activity,innerView);
            }
        }

	    
	}

}