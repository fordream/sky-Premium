package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.sky.MyCartActivity;
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
import z.lib.base.SkyUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyUtils.API;
import z.lib.base.SkyUtils.SCREEN;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

public class MyAccountOrderHistoryDetailDialog extends Base2Adialog implements android.view.View.OnClickListener {
	View views;
	static RelativeLayout main_detail;
	protected static final String TAG = "MyAccountOrderHistoryDetailDialog";
	private BaseAdapter baseAdapter;
	List<BaseItem> baseItemsAddbook = new ArrayList<BaseItem>();
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	LinearLayout content_main;;
	ScrollView detail;
	FragmentActivity activity;
	String id_order = "";
	LinearLayout listProduct, listShip, listStatus, listMessage;
	EditText mya_purchase_message_send_value;
	JSONArray listproduct;
	static String id_product="";
	String[] select_items_id = null;
	String[] select_items_name = null;
	String selected_items_id = "";
	public MyAccountOrderHistoryDetailDialog(FragmentActivity context, String id_order_, OnClickListener clickListener) {
		super(context, clickListener);
		this.activity = context;
		this.id_order = id_order_;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.mya_purchase, R.string.mya_purchase_j);
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);//icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		content_main = CommonAndroid.getView(main_detail, R.id.content_main);
		listStatus = CommonAndroid.getView(main_detail, R.id.block2);
		listProduct = CommonAndroid.getView(main_detail, R.id.block5);
		listShip = CommonAndroid.getView(main_detail, R.id.block7);
		listMessage = CommonAndroid.getView(main_detail, R.id.block8);
		mya_purchase_message_send_value = CommonAndroid.getView(main_detail, R.id.mya_purchase_message_send_value);
		CommonAndroid.getView(main_detail, R.id.mya_purchase_message_send_btn).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.select_product).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.mya_history_reorder).setOnClickListener(this);
		onloadData();
	}
	
	
	private void onloadData() {
		// TODO Auto-generated method stub
		try {
			JSONObject objParam = new JSONObject();//root key: param
			objParam.put("id_order", id_order);
			SkyUtils.execute(activity, RequestMethod.POST, API.API_MYACCOUNT_HISTORY_DETAIL, SkyUtils.paramRequest(getActivity(), objParam) , callback);
			LogUtils.i(TAG, "param==" + SkyUtils.paramRequest(getActivity(), objParam).toString() );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	private  CheerzServiceCallBack callback = new CheerzServiceCallBack() {
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
					LogUtils.i(TAG, "HISTORY DETAIL==" + mainJsonObject.toString());
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					mya_purchase_message_send_value.setText("");
					((TextView) CommonAndroid.getView(main_detail, R.id.select_product_value)).setText("");
					id_product = "";
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						addView(mainJsonObject.getJSONObject("data"));
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}

	
	};
	
	@Override
	public void onBackPressed(){
		close(true);
	}
	
	protected void addView(JSONObject data) {
		// TODO Auto-generated method stub
		try {
			//block1
			((TextView) CommonAndroid.getView(main_detail, R.id.mya_purchase_orderreference)).setText(data.getString("reference"));
			((TextView) CommonAndroid.getView(main_detail, R.id.mya_purchase_shipping)).setText(data.getString("carrier"));
			((TextView) CommonAndroid.getView(main_detail, R.id.mya_purchase_payment)).setText(data.getString("payment_method"));//payment_method
			
			//block2-->liststatus
			JSONArray order_state = data.getJSONArray("order_state");
			listStatus.removeAllViews();
			for(int i=0; i< order_state.length();i++){
				addViewState(order_state.getJSONObject(i));
			}
			
			//block3-->delivery
			JSONObject delivery = data.getJSONObject("address_delivery");
			String delivery_alias = getActivity().getString(R.string.mya_purchase_delivery);
			if (!new Setting(getActivity()).isLangEng())
				delivery_alias = getActivity().getString(R.string.mya_purchase_delivery_j);
			((TextView) CommonAndroid.getView(main_detail, R.id.mya_purchase_delivery)).setText( String.format(delivery_alias, delivery.getString("alias")));
			
			if(validateData(delivery,"fullname"))
				((TextView) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_name)).setText( delivery.getString("fullname"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_name_)).setVisibility(View.GONE);
			
			if(validateData(delivery,"company"))
				((TextView) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_company)).setText( delivery.getString("company"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_company_)).setVisibility(View.GONE);
			
			if(validateData(delivery,"address1"))
				((TextView) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_address1)).setText( delivery.getString("address1"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_address1_)).setVisibility(View.GONE);
			
			if(validateData(delivery,"postcode"))
				((TextView) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_code)).setText( delivery.getString("postcode"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_code_)).setVisibility(View.GONE);
			
			if(validateData(delivery,"country"))
				((TextView) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_country)).setText( delivery.getString("country"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_country_)).setVisibility(View.GONE);
			
			if(validateData(delivery,"phone"))
				((TextView) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_tel)).setText( delivery.getString("phone"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_tel_)).setVisibility(View.GONE);
			
			if(validateData(delivery,"phone_mobile"))
				((TextView) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_mobilephone)).setText( delivery.getString("phone_mobile"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.delivery_mya_address_mobilephone_)).setVisibility(View.GONE);
			
			//block4-->invoice
			JSONObject invoice = data.getJSONObject("address_invoice");
			String invoice_alias = getActivity().getString(R.string.mya_purchase_invoice);
			if (!new Setting(getActivity()).isLangEng())
				invoice_alias = getActivity().getString(R.string.mya_purchase_invoice_j);
			((TextView) CommonAndroid.getView(main_detail, R.id.mya_purchase_invoice)).setText( String.format(invoice_alias, invoice.getString("alias")));
			
			
			if(validateData(invoice,"fullname"))
				((TextView) CommonAndroid.getView(main_detail, R.id.mya_address_name)).setText( invoice.getString("fullname"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.mya_address_name_)).setVisibility(View.GONE);
			
			if(validateData(invoice,"company"))
				((TextView) CommonAndroid.getView(main_detail, R.id.mya_address_company)).setText( invoice.getString("company"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.mya_address_company_)).setVisibility(View.GONE);
			
			if(validateData(invoice,"address1"))
				((TextView) CommonAndroid.getView(main_detail, R.id.mya_address_address1)).setText( invoice.getString("address1"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.mya_address_address1_)).setVisibility(View.GONE);
			
			if(validateData(invoice,"postcode"))
				((TextView) CommonAndroid.getView(main_detail, R.id.mya_address_code)).setText( invoice.getString("postcode"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.mya_address_code_)).setVisibility(View.GONE);
			
			if(validateData(invoice,"country"))
				((TextView) CommonAndroid.getView(main_detail, R.id.mya_address_country)).setText( invoice.getString("country"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.mya_address_country_)).setVisibility(View.GONE);
			
			if(validateData(invoice,"phone"))
				((TextView) CommonAndroid.getView(main_detail, R.id.mya_address_tel)).setText( invoice.getString("phone"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.mya_address_tel_)).setVisibility(View.GONE);
			
			if(validateData(invoice,"phone_mobile"))
				((TextView) CommonAndroid.getView(main_detail, R.id.mya_address_mobilephone)).setText( invoice.getString("phone_mobile"));
			else
				((LinearLayout) CommonAndroid.getView(main_detail, R.id.mya_address_mobilephone_)).setVisibility(View.GONE);
			
			//block5--listproduct
			listproduct = data.getJSONArray("order_detail");
			listProduct.removeAllViews();
			select_items_id = new String[listproduct.length()];
			select_items_name = new String[listproduct.length()];
			for(int i=0; i< listproduct.length();i++){
				addViewProduct(listproduct.getJSONObject(i));
				select_items_id[i] = listproduct.getJSONObject(i).getString("id");
				select_items_name[i] = listproduct.getJSONObject(i).getString("name");
			}
			
			//block6-->total
			((TextView) CommonAndroid.getView(main_detail, R.id.mya_purchase_taxincl)).setText( CommonAndroid.formatPriceEC(getActivity(),data.getString("total_product_with_taxes")) );
			((TextView) CommonAndroid.getView(main_detail, R.id.mya_purchase_shipandhand)).setText( CommonAndroid.formatPriceEC(getActivity(),data.getString("shipping_and_handling")) );
			((TextView) CommonAndroid.getView(main_detail, R.id.mya_purchase_total)).setText( CommonAndroid.formatPriceEC(getActivity(),data.getString("total")) );
			
			//block7-->listship
			JSONArray order_shipping = data.getJSONArray("order_shipping");
			listShip.removeAllViews();
			for(int i=0; i< order_shipping.length();i++){
				addViewShip(order_shipping.getJSONObject(i));
			}
			
			
			//block8-->listmessage
			JSONArray order_message = data.getJSONArray("order_message");
			listMessage.removeAllViews();
			for(int i=0; i< order_message.length();i++){
				addViewMessage(order_message.getJSONObject(i));
			}
			
			//block9
			content_main.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addViewState(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			final LinearLayout newView = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.v2_history_detail_block2_layout, null);
			newView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_date)).setText( jsonObject.getString("date") );
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_status)).setText( jsonObject.getString("name") );
			listStatus.addView(newView);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean validateData(JSONObject obj, String string) {
		// TODO Auto-generated method stub
		try {
			if(obj.has(string)){
				String string_ = obj.getString(string);
				if(!"".equals(string_) && !"null".equals(string_))
					return true;
				else
					return false;
			}else{
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			return false;
		}
	}

	private void addViewMessage(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			final LinearLayout newView = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.v2_history_detail_block8_layout, null);
			newView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_message_liststart)).setText( jsonObject.getString("author") );
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_message_listvalue)).setText( jsonObject.getString("content") );
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_message_liststart_date)).setText( jsonObject.getString("date") );
			listMessage.addView(newView);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addViewShip(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			final LinearLayout newView = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.v2_history_detail_block7_layout, null);
			newView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_ship_date)).setText( jsonObject.getString("date") );
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_ship_carrier)).setText( jsonObject.getString("name") );
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_ship_weight)).setText( jsonObject.getString("weight") );
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_ship_cost)).setText( CommonAndroid.formatPriceEC(getActivity(),jsonObject.getString("cost")) );
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_ship_number)).setText( jsonObject.getString("tracking_number") );
			listShip.addView(newView);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void addViewProduct(JSONObject jsonObject) {
		// TODO Auto-generated method stub
		try {
			final LinearLayout newView = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.v2_history_detail_block5_layout, null);
			newView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_product)).setText( jsonObject.getString("name") );
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_quantity)).setText( jsonObject.getString("qty") );
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_unitprice)).setText( CommonAndroid.formatPriceEC(getActivity(),jsonObject.getString("price_unit")) );
			((TextView) CommonAndroid.getView(newView, R.id.mya_purchase_totalprice)).setText( CommonAndroid.formatPriceEC(getActivity(),jsonObject.getString("price_total")) );
			listProduct.addView(newView);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
		return R.layout.v2_history_detail_layout;
	}

	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			close(true);
			break;
		case R.id.mya_purchase_message_send_btn:
			String message = mya_purchase_message_send_value.getText().toString().trim();
//			if(!"".equals(message)){
				sendMessage(message);
//			}
			break;
		case R.id.select_product:
			/*new MyAccountOrderHistorySelectProductDialog(getActivity(), listproduct, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			}).show();*/
			new SelectChooserDialog(getActivity(), select_items_name) {
				@Override
				public void onItemClick(int item, String text) {
					try {
						LogUtils.i(TAG, "select==" + text);
						((TextView) CommonAndroid.getView(main_detail, R.id.select_product_value)).setText(text);
						id_product = select_items_id[item];
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.show();
			break;
		case R.id.mya_history_reorder:
			reOrder();
			break;
		default:
			break;
		}
	}
	
	private void reOrder() {
		// TODO Auto-generated method stub
		try {
			JSONObject objParam = new JSONObject();//root key: param
			objParam.put("id_order", id_order);
			SkyUtils.execute(activity, RequestMethod.POST, API.API_MYACCOUNT_HISTORY_RE, SkyUtils.paramRequest(getActivity(), objParam) , callback_reorder);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private  CheerzServiceCallBack callback_reorder = new CheerzServiceCallBack() {
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
					LogUtils.e(TAG, "send message==" + mainJsonObject.toString());
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
//						onloadData();
						Bundle extras = new Bundle();
						startActivityForResult(SCREEN.MYCART, extras);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}

	
	};

	private void sendMessage(String message) {
		// TODO Auto-generated method stub
		try {
			JSONObject objParam = new JSONObject();//root key: param
			objParam.put("id_order", id_order);
			objParam.put("id_product", id_product);
			objParam.put("message", message);
			SkyUtils.execute(activity, RequestMethod.POST, API.API_MYACCOUNT_HISTORY_MESSAGE, SkyUtils.paramRequest(getActivity(), objParam) , callback_send);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private  CheerzServiceCallBack callback_send = new CheerzServiceCallBack() {
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
					LogUtils.e(TAG, "send message==" + mainJsonObject.toString());
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						onloadData();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}

	
	};

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

	public static void setSelectProduct(String value) {
		// TODO Auto-generated method stub
		((TextView) CommonAndroid.getView(main_detail, R.id.select_product_value)).setText(value);
	}
	
	public void startActivityForResult(SCREEN screen, Bundle extras) {
		if (extras == null) {
			extras = new Bundle();
		}
		extras.putSerializable(SkyUtils.KEY.KEY_SCREEN, screen);
		extras.putBoolean(SkyUtils.KEY.KEY_SCREEN_TAG, true);
		Intent intent = new Intent(getActivity(), MyCartActivity.class);
		intent.putExtras(extras);

		getActivity().startActivityForResult(intent, 0);
		getActivity().overridePendingTransition(R.anim.right_in, 0);
	}

}