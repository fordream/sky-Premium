package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;
import org.com.atmarkcafe.view.EcAddressBookItemView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.Base2Adialog;
import z.lib.base.BaseAdapter;
import z.lib.base.BaseItem;
import z.lib.base.BaseView;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Products;

public class EcUpdateAddressBookDialog extends Base2Adialog implements android.view.View.OnClickListener {
	View views;
	static RelativeLayout main_detail;
	protected static final String TAG = "EcUpdateAddressBookFragment";
	private BaseAdapter baseAdapter;
	List<BaseItem> baseItemsAddbook = new ArrayList<BaseItem>();
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	String address_type;
	static ListView listview;
	private static CheerzServiceCallBack callback;
	public EcUpdateAddressBookDialog(Context context, String addressType, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.address_type = addressType;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_address_book);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.ec_address_book_header, R.string.ec_address_book_header_j);
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);// icon_nav_back
		header.visibivityRight(true, R.drawable.header_v2_icon_add);//ec_address_new ec_credit_icon_new  ec_credit_icon_new
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(header, R.id.header_btn_right).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.mya_address_addnew).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		
		callback = new CheerzServiceCallBack() {

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
						LogUtils.e(TAG, "dataUPDATE==" + mainJsonObject.toString());
						String is_succes = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.is_success);
						String err_msg = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.err_msg);
						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							baseItemsAddbook.clear();
							try {
								JSONObject mainJsonObject_data = mainJsonObject.getJSONObject("data");
								JSONArray array = mainJsonObject_data.getJSONArray("address");
								for (int i = array.length() - 1; i >= 0  ; i--) {
									baseItemsAddbook.add(new BaseItem(array.getJSONObject(i)));
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							addView(baseItemsAddbook);
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
		
		onLoad();
	}
	
	@Override
	public void onBackPressed(){
		close(true);
	}
	
	public static void onLoad() {
		// TODO Auto-generated method stub
		HashMap<String, String> inputs = new HashMap<String, String>();
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_ADDRESS_BOOK, inputs, callback);
	}
	
	

	private void addView(List<BaseItem> baseItemsAddbook) {
		// TODO Auto-generated method stub
		listview = (ListView) main_detail.findViewById(R.id.listview_addbook);
		baseAdapter = new BaseAdapter(getActivity(), new ArrayList<Object>()) {
			@Override
			public BaseView getView(Context context, Object data) {
				final EcAddressBookItemView ecAddressBookItemView = new EcAddressBookItemView(context);
				ecAddressBookItemView.addFragment(EcUpdateAddressBookDialog.this);
				return ecAddressBookItemView;
			}
		};
		baseAdapter.clear();
		baseAdapter.addAllItems(baseItemsAddbook);
		baseAdapter.notifyDataSetChanged();
		/*listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BaseItem baseItem = (BaseItem) parent.getItemAtPosition(position);
//				JSONObject itemObj = new JSONObject();
//			    try
//			    {
//			    	itemObj.put("id_address", "");
//			        itemObj.put("phone", "");
//			        itemObj.put("other", "");
//			        itemObj.put("alias", "");
//			        itemObj.put("company", "");
//			        itemObj.put("address1", "");
//			        itemObj.put("address2", "");
//			        itemObj.put("lastname", "");
//			        itemObj.put("firstname", "");
//					itemObj.put("postcode", "");
//					itemObj.put("phone_mobile", "");
//					itemObj.put("city", "");
//			    }catch(Exception e){
//			    	LogUtils.e(TAG, "e2" +e.getMessage());
//			    }
				LogUtils.e(TAG,baseItem.getObject().toString());
				if(address_type.equals(Products.address_delivery)){
					EcAddressDialog.address_delivery_full = baseItem.getObject().toString();
					if(EcAddressDialog.onCheck)
						EcAddressDialog.address_invoice_temp = baseItem.getObject().toString();
				}else{
					EcAddressDialog.address_invoice_temp = baseItem.getObject().toString();
				}
				EcAddressDialog.addView(address_type);
				close(true);
			}
		});*/
		listview.setAdapter(baseAdapter);
	};
	
	public void selectAddress(BaseItem data) {
		LogUtils.e(TAG,data.getObject().toString());
		if(address_type.equals(Products.address_delivery_type)){
			EcAddressDialog.address_delivery_full = data.getObject().toString();
			if(EcAddressDialog.onCheck)
				EcAddressDialog.address_invoice_temp = data.getObject().toString();
		}else{
			EcAddressDialog.address_invoice_temp = data.getObject().toString();
		}
		EcAddressDialog.addView(address_type, EcAddressDialog.address_delivery_full,  EcAddressDialog.address_invoice_temp);
		close(true);
	}

	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.main_address_book), new AnimationAction() {

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
		return R.layout.ec_addressbook_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
//			onBackPressed(null);
			close(true);
			break;
		case R.id.header_btn_right:
//			startFragment(new EcUpdateAddressNewFragment(), null);
			showView(false);
			new EcUpdateAddressEditDialog(getActivity(),null, null, address_type ,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			break;
		case R.id.mya_address_addnew:
			showView(false);
			new EcUpdateAddressEditDialog(getActivity(),null, null, address_type ,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			break;
		default:
			break;
		}
	}
	
	private static FragmentActivity getActivity() {
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
	
	public static void showView(Boolean show){
		LinearLayout footer = CommonAndroid.getView(main_detail, R.id.footer);
		footer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		listview.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}

}