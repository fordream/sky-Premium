package org.com.atmarkcafe.sky.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.customviews.charting.MEditText;
import org.com.atmarkcafe.sky.customviews.charting.MEditText.KeyImeChange;
import org.com.atmarkcafe.sky.dialog.EcAddressDialog;
import org.com.atmarkcafe.sky.dialog.EcUpdateAddressEditDialog;
import org.com.atmarkcafe.sky.dialog.ProfileChangeOptionDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.BaseAdapter;
import z.lib.base.BaseFragment;
import z.lib.base.BaseItem;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderUtils;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.Setting;

public class MyCart2Fragment extends BaseFragment {
	protected static final String TAG = "MyCart";
	View views;
	BaseItem baseItem;
	RelativeLayout main_detail;
	private BaseAdapter baseAdapter;
	List<BaseItem> baseItemsPurchase = new ArrayList<BaseItem>();
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	public int total_amount = 0;
	LinearLayout footer;
	static JSONArray array_product = null;
	public static ArrayList<String> listDelete = new ArrayList<String>();
	public static String address_delivery ="";
	public static String address_invoice = "";
	double tax = 0.08;
	String shipping_cost_total = "0";
	ScrollView scrolllistview;
	LinearLayout linearLayoutForm;
	public static int Next = 0; //0 - back, 1 - continue, 2 - checkout
	private String message_null_product = "";
	TextView bt_checkout;
	public static FragmentActivity activity;
	public static Boolean onChangeData = false;
	public static CheerzServiceCallBack callbackupdate, callbackget;
	private Boolean validate_quantity = true;
	private static boolean flagReloadMycart = false;
	private JSONArray product_not_available_detail = null;
	boolean showDialogValidate = true;
	public MyCart2Fragment() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.ec_mycart2;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
			case R.id.header_btn_left:
				/*contentFragment = new HomeFragment();
				switchFragment(contentFragment,nameFragment);*/
//				onBackPressed(null);
				RootActivity.back = true;
				Next = 0;
				try {
					if(/*array_product.length() > 0 && */onChangeData){
						checkout();
					}else{
						onBackPressed(null);
//						gotoEC();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					onBackPressed(null);
//					gotoEC();
					e.printStackTrace();
				}
				
				break;
			case R.id.bt_checkout:
//				if(!"".equals(message_null_product)){
//					SkyUtils.showDialog(getActivity(), "" + message_null_product,null);
//				}else{
					Next = 2;
					checkout();
//				}
				
				break;
			case R.id.bt_continue:
//				startFragment(new ECFragment(), null);
				Next = 1;
				if(onChangeData){
					checkout();
				}else{
					gotoEC();
				}
				break;
			default:
				break;
		}
	}

	private static void getMyCart() {
		// TODO Auto-generated method stub
		HashMap<String, String> inputs = new HashMap<String, String>();
		SkyUtils.execute(activity, RequestMethod.GET, API.API_EC_SHOWCART, inputs, callbackget);
	}

	@Override
	public void init(View view) {
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_dot_addcart, getResources().getColor(R.color.ec_icon_dot_addcart));//OK
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.btn_checkout, getResources().getColor(R.color.ec_btn_checkout));//OK
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.btn_continue, getResources().getColor(R.color.ec_btn_continue));//OK
		this.views = view;
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		linearLayoutForm = (LinearLayout) views.findViewById(R.id.linearLayoutForm);
		CommonAndroid.getView(view, R.id.bt_continue).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.bt_checkout).setOnClickListener(this);
		header = CommonAndroid.getView(view, R.id.header);
		header.initHeader(R.string.ec_mycart_header, R.string.ec_mycart_header_j);//ec_h
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);// icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		main_detail = (RelativeLayout) views.findViewById(R.id.main_mycart);
		footer = (LinearLayout) views.findViewById(R.id.footer);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		bt_checkout = CommonAndroid.getView(main_detail, R.id.bt_checkout);
		activity = getActivity();
		onChangeData = false;
		message_null_product =  (new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_mycart_empty) : getActivity().getString(R.string.ec_mycart_empty_j)); //jsonObject.getString("message");
		
		callbackupdate = new CheerzServiceCallBack() {

			public void onStart() {
				CommonAndroid.showView(true, loading);
				CommonAndroid.hiddenKeyBoard(getActivity());
			};

			public void onError(String message) {
				CommonAndroid.showView(false, loading);
				if (!isFinish()) {
					if(Next == 0){
						onBackPressed(null);
					}else if(Next == 1){
						gotoEC();
					}else{
						SkyUtils.showDialog(getActivity(), "" + message,null);
					}
				}
			};

			public void onSucces(String response) {
				CommonAndroid.showView(false, loading);
				if (!isFinish()) {
					JSONObject mainJsonObject;
					try {
						mainJsonObject = new JSONObject(response);
						LogUtils.e(TAG,"callbackupdate==" + mainJsonObject.toString());
						String is_succes = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.is_success);
						String err_msg = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.err_msg);
						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							//next
							if(total_amount == 0){
								bt_checkout.setEnabled(false);
								bt_checkout.setAlpha(0.5f);
								if(Next == 0 | Next == 1){
									onBackPressed(null);
								}else{
									SkyUtils.showDialog(getActivity(), "" + message_null_product,new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
//											if(Next == 0){
												onBackPressed(null);
//											}else if(Next == 1){
//												gotoEC();
//											}
										}
									});
								}
								
							}else{
								//0 - back, 1 - continue, 2 - checkout
								if(Next == 2){
									try {
										JSONObject jsonObject = mainJsonObject.getJSONObject("data");
										if (jsonObject.has(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE)) {
											String message = jsonObject.getString(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE);
											SkyUtils.showDialog(getActivity(), message, new OnClickListener() {
												
												@Override
												public void onClick(DialogInterface dialog, int which) {
													flagReloadMycart = true;
													getMyCart();
												}
											});	
											return;
										}
									} catch (Exception e) {
										
									}
									flagReloadMycart = false;
									
									if(validate_quantity){
										if(!"".equals(address_invoice) && !"".equals(address_delivery)){
											Products.address_invoice = address_invoice;
											Products.address_delivery = address_delivery;
										}
										if(!"".equals(Products.address_invoice) && !"".equals(Products.address_delivery)){
											Bundle extras = new Bundle();
											extras.putString(Products.address_invoice, address_invoice);
											extras.putString(Products.address_delivery, address_delivery);
//											startFragment(new EcUpdateAddressFragment(), extras);
//											startAddress(SCREEN.EC_ADDRESS, extras);
											
											MyCartActivity.EC_ADDRESS = new EcAddressDialog(getActivity(),Products.address_invoice, Products.address_delivery,new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
												}
											});
											MyCartActivity.EC_ADDRESS.show();
										}else{
											new EcUpdateAddressEditDialog(getActivity(),null, null, null ,new DialogInterface.OnClickListener() {
												@Override
												public void onClick(DialogInterface dialog, int which) {
												}
											}).show();
										}
										
										
										String ec_mycart_price_unit = getActivity().getResources().getString(R.string.ec_mycart_price_unit);
										if( !new Setting(getActivity()).isLangEng()){
											ec_mycart_price_unit = getActivity().getResources().getString(R.string.ec_mycart_price_unit_j);
										}
										Products.ec_credit_confirm_amount = ec_mycart_price_unit + " " + CommonAndroid.DecimalFormatPrice(total_amount + "") ;
									}else{
										String id_ = "";
										String quantity_ = "";
										new Products(getActivity()).updateQuantityProduct(getActivity(), id_, quantity_);
									}
								}else if(Next == 0){
									onBackPressed(null);
								}else if(Next == 1){
									gotoEC();
								}
							}
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
		
		callbackget = new CheerzServiceCallBack() {

			public void onStart() {
				CommonAndroid.showView(true, loading);
				CommonAndroid.hiddenKeyBoard(getActivity());
			};

			public void onError(String message) {
				CommonAndroid.showView(false, loading);
				if (!isFinish()) {
					SkyUtils.showDialog(getActivity(), "" + message,null);
					bt_checkout.setEnabled(false);
					bt_checkout.setAlpha(0.5f);
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
						String err_msg = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.err_msg);
						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							product_not_available_detail = null;
							JSONObject jsonObject = mainJsonObject.getJSONObject("data");
							if (jsonObject.has(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE)) {
								String message = jsonObject.getString(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE);
								if (!flagReloadMycart) {
									SkyUtils.showDialog(getActivity(), message, null);	
								}
								if(jsonObject.has(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE_DETAIL)){
									product_not_available_detail = jsonObject.getJSONArray(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE_DETAIL);
								}
							}
							LogUtils.e(TAG,"dataMYCART===" + jsonObject.toString());
							if(jsonObject.has("tax")){
								tax = jsonObject.getDouble("tax");
								LogUtils.e(TAG,"tax===" + tax);
								
							}
							if(jsonObject.has("shipping_cost_total")){
								shipping_cost_total = CommonAndroid.formatPriceEC(getActivity(), jsonObject.getString("shipping_cost_total"));
							}
							if(jsonObject.has("product")){
								listDelete.clear();
								array_product = jsonObject.getJSONArray("product");
								//address_delivery
								//address_invoice
								address_delivery ="";
								address_invoice = "";
								Products.address_delivery = "";
								Products.address_invoice = "";
								try {
									if(jsonObject.has("address_delivery"))
										address_delivery = jsonObject.getJSONObject("address_delivery").toString();
									if(jsonObject.has("address_invoice"))
										address_invoice = jsonObject.getJSONObject("address_invoice").toString();
									LogUtils.i("address_delivery======", jsonObject.getJSONObject("address_delivery").toString());
									LogUtils.i("address_invoice=====", jsonObject.getJSONObject("address_invoice").toString());
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
//								LogUtils.e(TAG, "address == " + "address_delivery:" + address_delivery + ":address_invoice:" +address_invoice);
								addView(array_product, "", "");
							}else{
								bt_checkout.setEnabled(false);
								bt_checkout.setAlpha(0.5f);
								SkyUtils.showDialog(getActivity(), "" + message_null_product, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										onBackPressed(null);
									}
								});
								
							}
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
		
		getMyCart();
	}

	private String checkNotAvailable(int productID){
		String qty_out = getString(R.string.mycart_validate_out);
		if(!new Setting(getActivity()).isLangEng())
			qty_out = getString(R.string.mycart_validate_out_j);
		String qty_in = getString(R.string.mycart_validate_in);
		if(!new Setting(getActivity()).isLangEng())
			qty_in = getString(R.string.mycart_validate_in_j);
		if(product_not_available_detail != null){
			for(int i= 0; i < product_not_available_detail.length(); i++){
				try {
					if(productID == product_not_available_detail.getJSONObject(i).getInt("id_product")){
						if(product_not_available_detail.getJSONObject(i).getInt("qty") > 0){
							return String.format(qty_in, product_not_available_detail.getJSONObject(i).getInt("qty") );
						}else{
							return qty_out;
						}
					}
				} catch (Exception e) {}
			}
		}
		return "";
	}

	protected void addView(JSONArray array, String id_product, String quantity_cart) {
		baseItemsPurchase.clear();
		total_amount = 0;
		if("".equals(quantity_cart))
			quantity_cart = "0";
		try {
			for (int i = 0; i < array.length() ; i++) {
				String id_check = array.getJSONObject(i).getString("id");
				if(!listDelete.contains(id_check)){
					array.getJSONObject(i).put("no", i);
					array.getJSONObject(i).put("is_removed", 0);
					if(!"".equals(quantity_cart) && id_check.equals(id_product)){
						array.getJSONObject(i).put("quantity_cart", quantity_cart);
					}
					baseItemsPurchase.add(new BaseItem(array.getJSONObject(i)));
					int temp_total_amount =  Integer.parseInt(  array.getJSONObject(i).getString("base_price") ) * Integer.parseInt( array.getJSONObject(i).getString("quantity_cart") );
					total_amount+= temp_total_amount;
				}else{
					array.getJSONObject(i).put("is_removed", 1);
				}
//					LogUtils.e(TAG,"mycart_detail" + array.getJSONObject(i).toString());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		footer.setVisibility(View.VISIBLE);
		String ec_mycart_price_unit = getActivity().getResources().getString(R.string.ec_mycart_price_unit);
		if( !new Setting(getActivity()).isLangEng()){
			ec_mycart_price_unit = getActivity().getResources().getString(R.string.ec_mycart_price_unit_j);
		}
		String total_amount_txt = ec_mycart_price_unit + " "  + CommonAndroid.DecimalFormatPrice(total_amount + "") ;
		((TextView) CommonAndroid.getView(views, R.id.ec_mycart_total)).setText(total_amount_txt);
		
		//news
		// x + x*tax = total ==> x = total / (1 + tax)
		Float temp_tax_total= (float) 0;
		Float temp_tax_total_tem = (float) (total_amount / (1 + tax));
		temp_tax_total = (float) (temp_tax_total_tem * tax);
		((TextView) CommonAndroid.getView(views, R.id.ec_mycart_subtotal)).setText(total_amount_txt);
//		((TextView) CommonAndroid.getView(views, R.id.ec_mycart_shipfee)).setText(shipping_cost_total);
		((TextView) CommonAndroid.getView(views, R.id.ec_mycart_tax)).setText( ec_mycart_price_unit + " " +CommonAndroid.DecimalFormatPrice((int)Math.round(temp_tax_total) +""));
		
		//endnews
		scrolllistview = (ScrollView) main_detail.findViewById(R.id.listview_mycart_detail);
		
//		linearLayoutForm.removeAllViewsInLayout();
		linearLayoutForm.removeAllViews();
		for(int i= 0; i < baseItemsPurchase.size();i++){
			addView2(baseItemsPurchase.get(i));
			LogUtils.e(TAG, i +"==" +baseItemsPurchase.get(i) );
		}
		
	}
	
	private void addView2(BaseItem baseItem2) {
		// TODO Auto-generated method stub
		final LinearLayout newView = (LinearLayout)getActivity().getLayoutInflater().inflate(R.layout.v2_ec_mycart_item, null);//ec_mycart_item2
		newView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		((TextView) CommonAndroid.getView(newView, R.id.name)).setText( baseItem2.getString("name") );
		
		String ec_mycart_price_unit = getActivity().getResources().getString(R.string.ec_mycart_price_unit);
		if( !new Setting(getActivity()).isLangEng()){
			ec_mycart_price_unit = getActivity().getResources().getString(R.string.ec_mycart_price_unit_j);
		}
//		((TextView) CommonAndroid.getView(newView, R.id.ec_mycart_price)).setText( CommonAndroid.DecimalFormatPrice(baseItem2.getString("base_price"))  + " " + ec_mycart_price_unit );
		String quantity_cart =  baseItem2.getString("quantity_cart");
		if("".equals(quantity_cart))
			quantity_cart = "0";
		((TextView) CommonAndroid.getView(newView, R.id.ec_mycart_quantity)).setText( baseItem2.getString("quantity_cart") );
		int ec_mycart_amount = Integer.parseInt( baseItem2.getString("base_price") ) * Integer.parseInt( baseItem2.getString("quantity_cart")  );
		((TextView) CommonAndroid.getView(newView, R.id.ec_mycart_amount)).setText( ec_mycart_price_unit  + " " + CommonAndroid.DecimalFormatPrice(ec_mycart_amount + "")  );
		ImageView imageView = (ImageView) newView.findViewById(R.id.image_product);
		ImageLoaderUtils.getInstance(getActivity()).displayImageMyCart( baseItem2.getString("thumbnail") , imageView );
		
		LinearLayout btnRemove = (LinearLayout) newView.findViewById(R.id.icon_delete);
		final BaseItem baseItem2_ = baseItem2;
		btnRemove.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				linearLayoutForm.removeView(newView);
//				delete(baseItem2_);
				confirm_delete(baseItem2_, newView);
			}
		});
		MEditText ec_mycart_quantity_1= (MEditText) newView.findViewById(R.id.ec_mycart_quantity);
		ec_mycart_quantity_1.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) /*|| (actionId == EditorInfo.IME_ACTION_SEARCH)  */) {
                    //Do your action
					MEditText ec_mycart_quantity_1 = (MEditText) newView.findViewById(R.id.ec_mycart_quantity);
					String temp = ec_mycart_quantity_1.getText().toString().trim();
					if(!"".equals(temp)){
						final int ec_mycart_quantity_new = Integer.parseInt(temp);
//						if(ec_mycart_quantity_new != 0 ){
//							SkyUtils.showDialog(getActivity(), "update==" + ec_mycart_quantity_new );
						Handler mHanler = new Handler();
						mHanler.postDelayed(new Runnable() {
							
							@Override
							public void run() {
								update(baseItem2_, ec_mycart_quantity_new);
							}
						}, 300);

//						}
					}
					
                }
				InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	            keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
				return false;
			}
        });
		
		ec_mycart_quantity_1.addTextChangedListener(new TextWatcher() {
				
			String value_default = "0";
	          public void afterTextChanged(Editable s) {
	        	  onChangeData = true;
	        	 /* MEditText ec_mycart_quantity_1 = (MEditText) newView.findViewById(R.id.ec_mycart_quantity);
	        	  	String input = ec_mycart_quantity_1.getText().toString().trim();
	        	  	input.replace("-%^@&*@**&@", "");
	        	  	int quantity = 0;
	        	  	try {
						quantity = Integer.parseInt(baseItem2_.getString("quantity"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	  	if(!"".equals(input) && quantity >0){
	        	  		int temp = Integer.parseInt( input );
						if(temp > quantity){
							SkyUtils.showDialog(getActivity(), new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_quantity_validate) : getActivity().getString(R.string.ec_quantity_validate_j) ,null);
//							ec_mycart_quantity_1.setText("1");
						}
	        	  	}else if( quantity <= 0){
//	        	  		ec_mycart_quantity_1.setText("0");
	        	  		LogUtils.i(TAG, "quantity==" + quantity);
	        	  	}*/
	          }

	          public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	        	  MEditText ec_mycart_quantity_1 = (MEditText) newView.findViewById(R.id.ec_mycart_quantity);
	        	  String input = ec_mycart_quantity_1.getText().toString().trim();
	        	  input.replace("-%^@&*@**&@", "");
	        	  value_default = input;	
	          }

	          public void onTextChanged(CharSequence s, int start, int before, int count) {
//	        	  	onChangeData = true;
	        	  	MEditText ec_mycart_quantity_1 = (MEditText) newView.findViewById(R.id.ec_mycart_quantity);
	        	  	String input = ec_mycart_quantity_1.getText().toString().trim();
	        	  	input.replace("-%^@&*@**&@", "");
	        	  	int quantity = 0;
	        	  	try {
						quantity = Integer.parseInt(baseItem2_.getString("quantity"));
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	  	if(!"".equals(input) && quantity >0){
	        	  		int temp = Integer.parseInt( input );
						if(temp > quantity){
							if(showDialogValidate ){
								showDialogValidate = false;
								SkyUtils.showDialog(getActivity(), new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_quantity_validate) : getActivity().getString(R.string.ec_quantity_validate_j) , new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										showDialogValidate = true;
									}
								} );
							}
							ec_mycart_quantity_1.setText(value_default);
							InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				            keyboard.hideSoftInputFromWindow(getView().getWindowToken(), 0);
						}
	        	  	}else if( quantity <= 0 && !"".equals(input)){
	        	  		int temp = Integer.parseInt( input );
	        	  		if(showDialogValidate && temp > 0){
							showDialogValidate = false;
							SkyUtils.showDialog(getActivity(), new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_quantity_validate) : getActivity().getString(R.string.ec_quantity_validate_j) , new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									showDialogValidate = true;
								}
							} );
							update(baseItem2_, 0);
		        	  		InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		    	            keyboard.hideSoftInputFromWindow(getView().getWindowToken(), 0);
						}
//	        	  		ec_mycart_quantity_1.setText("0");
	        	  		LogUtils.i(TAG, "quantity==" + quantity);
	        	  	}
	        	  	ec_mycart_quantity_1.setSelection(ec_mycart_quantity_1.getText().toString().trim().length());
	        	  	LogUtils.i(TAG, "start==" + start + "::before==" + before);
	          }
	       });
		
		ec_mycart_quantity_1.setKeyImeChangeListener(new KeyImeChange() {

			@Override
			public void onKeyIme(int keyCode, KeyEvent event) {
				// TODO Auto-generated method stub
        	  	TextView ec_mycart_quantity_1 = (EditText) newView.findViewById(R.id.ec_mycart_quantity);
        	  	String input = ec_mycart_quantity_1.getText().toString().trim();
        	  	input.replace("-%^@&*@**&@", "");
        	  	int quantity = 0;
        	  	try {
					quantity = Integer.parseInt(baseItem2_.getString("quantity"));
				} catch (NumberFormatException e) {}
        	  	if(quantity > 0){
        	  		if(!"".equals(input)){
            	  		int temp = Integer.parseInt( input );
    					if(temp > quantity){
//    						SkyUtils.showDialog(getActivity(), new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_quantity_validate) : getActivity().getString(R.string.ec_quantity_validate_j) ,null );
    						if(showDialogValidate ){
								showDialogValidate = false;
								SkyUtils.showDialog(getActivity(), new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_quantity_validate) : getActivity().getString(R.string.ec_quantity_validate_j) , new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										showDialogValidate = true;
									}
								} );
							}
    						//    						ec_mycart_quantity_1.setText("1");
    					}else{
//    						if(temp != 0 ){
    							onChangeData = true;
    							update(baseItem2_, temp);
//    						}
    					}
            	  	}
        	  	}
        	  	
        	  	InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
	            keyboard.hideSoftInputFromWindow(views.getWindowToken(), 0);
				
			}
			
		});

		TextView product_not_available_detail = (TextView) newView.findViewById(R.id.product_not_available_detail);
		int product_id = Integer.parseInt(baseItem2.getString("id"));
		if( "".equals(checkNotAvailable(product_id)) ){
			//hidden
			product_not_available_detail.setVisibility(View.GONE);
		}else{
			//show
			product_not_available_detail.setText(checkNotAvailable(product_id));
			product_not_available_detail.setVisibility(View.VISIBLE);
		}
		linearLayoutForm.addView(newView);
	}
	
	@Override
	public void onChangeLanguage() {
		// TODO Auto-generated method stub
		
	}
	
	private void confirm_delete(BaseItem baseItem2_, final LinearLayout newView) {
		// TODO Auto-generated method stub
		String[] items;
		String title = getActivity().getResources().getString(R.string.ec_delete_confirm);
		items = new String[] {  getActivity().getResources().getString(R.string.msg_no), getActivity().getResources().getString(R.string.msg_yes) };
		if (!new Setting(getActivity()).isLangEng()) {
			title = getActivity().getResources().getString(R.string.ec_delete_confirm_j);
			items = new String[] { getActivity().getResources().getString(R.string.msg_no_j), getActivity().getResources().getString(R.string.msg_yes_j) };
		}
		final BaseItem baseItem2_temp = baseItem2_;
		final LinearLayout newView2 = newView;
		new ProfileChangeOptionDialog(getActivity(), items, title, new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 1) {
					// yes-->logout
					linearLayoutForm.removeView(newView2);
					delete(baseItem2_temp);
					onChangeData = true;
				} else if (which == 0) {
					// no
				}

			}
		}).show();
	}

	public void delete(BaseItem data) {
		// TODO Auto-generated method stub
//		SkyUtils.showDialog(getActivity(), "delete==" + data.getString("id"));
		LogUtils.e(TAG,  "delete==" + data.getString("id"));
		String id_product 		= data.getString("id");
		String quantity_cart 	= data.getString("quantity_cart");
		try {
			listDelete.add(id_product);
			addView(array_product, id_product, quantity_cart);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void update(BaseItem data, int quantity_cart_new) {
		// TODO Auto-generated method stub
		String id_product 		= data.getString("id");
		final String quantity_cart 	= data.getString("quantity_cart");
		if(quantity_cart_new != Integer.parseInt(quantity_cart)  || quantity_cart_new == 0){
//			SkyUtils.showDialog(getActivity(), "update==" + quantity_cart_new);
			addView(array_product, id_product, quantity_cart_new + "");
		}
	}
	
	public static void checkout(){
		try {
			HashMap<String, String> inputs = new HashMap<String, String>();
			for (int i = 0; i < array_product.length() ; i++) {
				
					String id_product 	=  array_product.getJSONObject(i).getString("id");
					String qty 			=  array_product.getJSONObject(i).getString("quantity_cart");
					String is_removed 	=  array_product.getJSONObject(i).getString("is_removed");
					inputs.put("req[param][products][" + (i + 1) +"][id_product]", id_product );
					inputs.put("req[param][products][" + (i + 1) +"][qty]", qty );
					inputs.put("req[param][products][" + (i + 1) +"][is_removed]", is_removed );
				
			}
			LogUtils.i(TAG,"inputs update==" + inputs.toString());
			SkyUtils.execute(activity, RequestMethod.GET, API.API_EC_UPDATECART, inputs, callbackupdate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onBackPressed() {
		LogUtils.e(TAG, "test=========22222");
		if(MyCart2Fragment.onChangeData){
			MyCart2Fragment.Next = 1;
		}
//		RootActivity.back = true;
//		finish();
//		super.onBackPressed();
		return;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		LogUtils.e(TAG, "test=========onResume");
	}
	
//	public void startAddress(SCREEN screen, Bundle extras) {
//		if (extras == null) {
//			extras = new Bundle();
//		}
//		extras.putSerializable(SkyUtils.KEY.KEY_SCREEN, screen);
//		extras.putBoolean(SkyUtils.KEY.KEY_SCREEN_TAG, true);
//		Intent intent = new Intent(getActivity(), EcUpdateAddressActivity.class);
//		intent.putExtras(extras);
//
//		getActivity().startActivityForResult(intent, 0);
//		getActivity().overridePendingTransition(R.anim.right_in, 0);
//	}
	
	public static void reLoadMyCart(){
		if(MyCartActivity.EC_ADDRESS != null){
			MyCartActivity.EC_ADDRESS.dismiss();
		}
		if(MyCartActivity.EC_SHIP != null){
			MyCartActivity.EC_SHIP.dismiss();
		}
		if(MyCartActivity.EC_CREDIT != null){
			MyCartActivity.EC_CREDIT.dismiss();
		}
		if(MyCartActivity.EC_CREDIT_CONFIRM != null){
			MyCartActivity.EC_CREDIT_CONFIRM.dismiss();
		}
		if(MyCartActivity.EC_FINISH != null){
			MyCartActivity.EC_FINISH.dismiss();
		}
		flagReloadMycart = true;
		getMyCart();
	}
}