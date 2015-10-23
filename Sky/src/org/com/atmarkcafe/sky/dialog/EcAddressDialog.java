package org.com.atmarkcafe.sky.dialog;

import java.util.HashMap;

import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.RootActivity;

import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;
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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Products;

public class EcAddressDialog extends Base2Adialog implements android.view.View.OnClickListener {
	protected static final String TAG = "EcUpdateAddressFragment";
	static RelativeLayout main_detail;
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	private static CheckBox checkbox;
	private static int id_address_delivery;
	private static int id_address_invoice;
	private static int id_address_invoice_temp;
	public static String address_invoice_temp;
	public static String address_delivery_full;
	public static String address_invoice_full;
	static TextView bt_update_b;
	static TextView bt_choose_b;
	public static Boolean onCheck = false;
	public EcAddressDialog(Context context, String address_invoice, String address_delivery, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		address_invoice_full = address_invoice;
		address_delivery_full = address_delivery;
		address_invoice_temp = address_invoice;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.ec_address_header, R.string.ec_address_header_j);//ec_h
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);// 
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
//		getAddressInfo();
		CommonAndroid.getView(main_detail, R.id.bt_update_d).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.bt_choose_d).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.bt_update_b).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.bt_choose_b).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.bt_continue).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.bt_checkout).setOnClickListener(this);
		checkbox = CommonAndroid.getView(main_detail, R.id.checkbox);
		CommonAndroid.getView(main_detail, R.id.checkbox_update).setOnClickListener(this);
		checkbox.setOnClickListener(this);
		bt_update_b = CommonAndroid.getView(main_detail, R.id.bt_update_b);
		bt_choose_b = CommonAndroid.getView(main_detail, R.id.bt_choose_b);
		addView(Products.address_delivery_type, address_delivery_full,  address_invoice_temp);
	}
	
	@Override
	public void onBackPressed(){
		close(true);
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
		return R.layout.ec_address_layout;
	}

	@Override
	public void onClick(View v) {

		Bundle extras;
		switch (v.getId()) {
		case R.id.header_btn_left:
			close(true);
			break;
		case R.id.bt_update_d:
			showView(false);
//			extras = new Bundle();
//			extras.putString(Products.address_delivery, address_delivery_full);
//			startFragment(new EcUpdateAddressNewFragment(), extras);
			new EcUpdateAddressEditDialog(getActivity(),address_delivery_full, null, Products.address_delivery_type ,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			break;
		case R.id.bt_choose_d:
			/*extras = new Bundle();
			extras.putString(Products.address_delivery, address_delivery_full);
			extras.putString(Products.address_invoice, address_invoice_full);
			extras.putString(Products.address_type, Products.address_delivery);
			startFragment(new EcUpdateAddressBookFragment(), extras);*/
			new EcUpdateAddressBookDialog(getActivity(), Products.address_delivery_type,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			break;
		case R.id.bt_update_b:
			showView(false);
//			extras = new Bundle();
//			extras.putString(Products.address_invoice, address_invoice_full);
//			startFragment(new EcUpdateAddressNewFragment(), extras);
			new EcUpdateAddressEditDialog(getActivity(),null, address_invoice_full, Products.address_invoice_type ,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			break;
		case R.id.bt_choose_b:
//			extras = new Bundle();
//			extras.putString(Products.address_delivery, address_delivery_full);
//			extras.putString(Products.address_invoice, address_invoice_full);
//			extras.putString(Products.address_type, Products.address_invoice);
//			startFragment(new EcUpdateAddressBookFragment(), extras);
			new EcUpdateAddressBookDialog(getActivity(), Products.address_invoice_type, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).show();
			break;
		case R.id.bt_continue:
			LogUtils.i(TAG, "bt_continue");
			finish();
//			gotoEC();
			break;
		case R.id.bt_checkout:
//			startFragment(new ECFragment(), null);
			updateAddress();
			break;
		case R.id.checkbox:	
			LogUtils.i(TAG,"checkbox_update1==" + onCheck);
			if(onCheck){
				onCheck = false;
				bt_update_b.setEnabled(true);
				bt_choose_b.setEnabled(true);
				bt_update_b.setAlpha(1f);
				bt_choose_b.setAlpha(1f);
				address_invoice_temp = address_invoice_full;
			}else{
				onCheck = true;
				bt_update_b.setEnabled(false);
				bt_choose_b.setEnabled(false);
				bt_update_b.setAlpha(0.5f);
				bt_choose_b.setAlpha(0.5f);
				address_invoice_temp = address_delivery_full;
			}
			checkbox.setChecked(onCheck);
			addView(null, address_delivery_full,  address_invoice_temp);
			break;
		case R.id.checkbox_update:
			LogUtils.i(TAG,"checkbox_update2==" + onCheck);
			if(onCheck){
				onCheck = false;
				bt_update_b.setEnabled(true);
				bt_choose_b.setEnabled(true);
				bt_update_b.setAlpha(1f);
				bt_choose_b.setAlpha(1f);
				address_invoice_temp = address_invoice_full;
			}else{
				onCheck = true;
				bt_update_b.setEnabled(false);
				bt_choose_b.setEnabled(false);
				bt_update_b.setAlpha(0.5f);
				bt_choose_b.setAlpha(0.5f);
				address_invoice_temp = address_delivery_full;
			}
			checkbox.setChecked(onCheck);
			addView(null, address_delivery_full,  address_invoice_temp);
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
	
	public static void addView(String type, String address_delivery_full2, String address_invoice_temp2) {
		// TODO Auto-generated method stub
//		LogUtils.i(TAG, "address_delivery==" + address_delivery_full );
//		LogUtils.i(TAG, "address_invoice==" + address_invoice_full );
		
//		if(checkbox.isChecked()){
//			address_invoice_full = address_delivery_full;
//		}
		checkIDAddress(address_delivery_full2, address_invoice_temp2/*address_invoice_full*/);
		LogUtils.i(TAG, id_address_invoice + "===" + id_address_delivery);
		if(type != null){
			if(id_address_invoice == id_address_delivery){
				onCheck = true;
				bt_update_b.setEnabled(false);
				bt_choose_b.setEnabled(false);
				bt_update_b.setAlpha(0.5f);
				bt_choose_b.setAlpha(0.5f);
				if(type.equals(Products.address_delivery)){
					address_invoice_temp = address_delivery_full;
				}else{
					address_delivery_full = address_invoice_temp;
				}
			}else{
				onCheck = false;
				bt_update_b.setEnabled(true);
				bt_choose_b.setEnabled(true);
				bt_update_b.setAlpha(1f);
				bt_choose_b.setAlpha(1f);
			}
			checkbox.setChecked(onCheck);
		}	
		
		addViewDelivery(address_delivery_full2);
		addViewInvoice(address_invoice_temp2);
		
	}

	private static void checkIDAddress(String address_delivery, String address_invoice) {
		JSONObject mainJsonObject, mainJsonObject2;
		try {
			mainJsonObject = new JSONObject(address_delivery);
			id_address_delivery =  Integer.parseInt(CommonAndroid.getString(mainJsonObject, "id_address"));
			mainJsonObject2 = new JSONObject(address_invoice);
			id_address_invoice = Integer.parseInt(CommonAndroid.getString(mainJsonObject2, "id_address"));
			id_address_invoice_temp = id_address_invoice;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void addViewDelivery(String address_delivery) {
		// TODO Auto-generated method stub
		JSONObject mainJsonObject;
		try {
			mainJsonObject = new JSONObject(address_delivery);
//			id_address_delivery =  CommonAndroid.getInt(mainJsonObject, "id_address");
			String name = CommonAndroid.getString(mainJsonObject, "firstname") + " " + CommonAndroid.getString(mainJsonObject, "lastname");
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_name_d)).setText( name );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_tel_d)).setText( CommonAndroid.getString(mainJsonObject, "phone") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_shipping_d)).setText( CommonAndroid.getString(mainJsonObject, "address1") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void addViewInvoice(String address_invoice) {
		// TODO Auto-generated method stub
		JSONObject mainJsonObject;
		try {
			mainJsonObject = new JSONObject(address_invoice);
//			id_address_invoice = CommonAndroid.getInt(mainJsonObject, "id_address");
//			id_address_invoice_temp = id_address_invoice;
			String name = CommonAndroid.getString(mainJsonObject, "firstname") + " " + CommonAndroid.getString(mainJsonObject, "lastname");
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_name_b)).setText( name );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_tel_b)).setText( CommonAndroid.getString(mainJsonObject, "phone") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_address_shipping_b)).setText( CommonAndroid.getString(mainJsonObject, "address1") );
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void updateAddress() {
		// TODO Auto-generated method stub
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][id_address_delivery]", "" +id_address_delivery );
		inputs.put("req[param][id_address_invoice]", "" +id_address_invoice_temp );
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_ADDRESS_UPDATE_ORDER, inputs, callbackupdate);
	}
	
	private CheerzServiceCallBack callbackupdate = new CheerzServiceCallBack() {

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
					LogUtils.i(TAG, "dataUPDATE==" + mainJsonObject.toString());
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					String err_msg = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.err_msg);
//					JSONObject jsonData = mainJsonObject.getJSONObject("data");
//					if (jsonData.has(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE)) {
//						String message = jsonData.getString(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE);
//						SkyUtils.showDialog(getActivity(), message, R.string.tlt_ok, R.string.tlt_ok_j, null);
//					}
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						MyCart2Fragment.address_delivery 	= address_delivery_full;
						if(id_address_delivery == id_address_invoice_temp){
							MyCart2Fragment.address_invoice 	= address_delivery_full;
						}else{
							MyCart2Fragment.address_invoice 	= address_invoice_full;
						}
//						startFragment(new EcUpdateCreditFragment(), null);
						
						/*MyCartActivity.EC_CREDIT = new EcCreditDialog(getActivity(),new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						MyCartActivity.EC_CREDIT.show();*/
						
						//TODO: ship
						MyCartActivity.EC_SHIP = new EcShipDialog(getActivity(),new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
							}
						});
						MyCartActivity.EC_SHIP.show();
						
//						JSONObject jsonObject = mainJsonObject.getJSONObject("data");
//						if(jsonObject.has("product")){
//							listDelete.clear();
//							array_product = jsonObject.getJSONArray("product");
//							addView(array_product, "", "");
//						}else{
//							String message = jsonObject.getString("message");
//							SkyUtils.showDialog(getActivity(), "" + message);
//						}
					}else{
						SkyUtils.showDialog(getActivity(), "" + err_msg,null);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};

	public static void showView(Boolean show){
		LinearLayout footer = CommonAndroid.getView(main_detail, R.id.footer);
		footer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		LinearLayout billing = CommonAndroid.getView(main_detail, R.id.billing);
		billing.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		LinearLayout delivery = CommonAndroid.getView(main_detail, R.id.delivery);
		delivery.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}

}