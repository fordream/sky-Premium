package org.com.atmarkcafe.sky.dialog;

import java.util.HashMap;
import java.util.Map;

import org.com.atmarkcafe.service.CallbackListenner;
import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.SkyMainActivity;

import com.SkyPremiumLtd.SkyPremium.R;

import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.fragment.ECFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.Base2Adialog;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderUtils;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyLoader;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.SCREEN;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.ProductsOfCategories;
import com.acv.cheerz.db.Setting;

public class ECProductsDialog extends Base2Adialog implements android.view.View.OnClickListener {
	protected static final String TAG = "EcProductsDetailDialog";
	static RelativeLayout main_detail;
	private org.com.atmarkcafe.view.HeaderECView header;
	private GridView list;
	private ImageButton imgBtnSearch,imgBtnMycart;
	private LinearLayout llSearch;
	private View llSearchViewChild;
	private TextView txtConfirmSearch,edtSearch;
	private LoadingView loading;
	private ImageButton btnSearch,btnMycart,btnBack;
	private ImageView btnClearSearch;
	private LinearLayout header_btn_right_main;
	CallbackListenner callbackListenner = new CallbackListenner() {
		
		@Override
		public void onLoad(boolean onload) {
			// TODO Auto-generated method stub
			
		}
	};
	public ECProductsDialog(Context context, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.bgr_green, getActivity().getResources().getColor(R.color.ec_btn_search));//OK
		
		initHeaderView();
		llSearch = (LinearLayout)main_detail.findViewById(R.id.ec_ll_search_id);
		LayoutInflater inf = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		llSearchViewChild = inf.inflate(R.layout.search_layout, null);
		edtSearch = (EditText)llSearchViewChild.findViewById(R.id.search_edt_id);
		txtConfirmSearch = (TextView)llSearchViewChild.findViewById(R.id.search_txtconfirm_id);
		txtConfirmSearch.setOnClickListener(this);
		
		btnClearSearch = CommonAndroid.getView(llSearchViewChild, R.id.clear_search);
		btnClearSearch.setOnClickListener(this);
		edtSearch.clearFocus();
		CommonAndroid.showView(false, btnClearSearch);
		edtSearch.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && !"".equalsIgnoreCase(edtSearch.getText().toString())) {
					CommonAndroid.showView(true, btnClearSearch);
				}
				
			}
		});
		edtSearch.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				CommonAndroid.showView(true, btnClearSearch);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					CommonAndroid.showView(false, btnClearSearch);
				}else{
					CommonAndroid.showView(true, btnClearSearch);
				}
			}
		});
		// handler action done
		edtSearch.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String contentSearch = edtSearch.getText().toString();
					CommonAndroid.hiddenKeyBoard(getActivity(), llSearch);
					processSearch(contentSearch);
					/*if (llSearch.getChildCount() > 0) {
						llSearch.removeAllViews();
					}*/
				}
				return false;
			}
		});
		
		CommonAndroid.TouchViewhiddenKeyBoard(getActivity(), llSearch);
		
		/*header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(ECFragment.list_name_category ,ECFragment.list_name_category );
		header.visibivityLeft(true,R.drawable.icon_nav_back );//header_v2_icon_back
		header.visibivityRight(true,R.drawable.ic_mycart );//header_v2_icon_mycart
		CommonAndroid.getView(main_detail, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.header_btn_right).setOnClickListener(this);*/
//		CommonAndroid.getView(main_detail, R.id.header_btn_right).setOnClickListener(this);
		// init button header
		/*imgBtnMycart = (ImageButton)header.findViewById(R.id.header_btn_right_left);
		imgBtnMycart.setVisibility(View.VISIBLE);
		imgBtnMycart.setImageResource(R.drawable.header_v2_icon_mycart);
		imgBtnMycart.setOnClickListener(this);
		
		imgBtnSearch = (ImageButton)header.findViewById(R.id.header_btn_right);
		imgBtnSearch.setVisibility(View.VISIBLE);
		imgBtnSearch.setImageResource(R.drawable.header_v2_icon_search);
		imgBtnSearch.setOnClickListener(this);*/
		
		list = CommonAndroid.getView(main_detail, R.id.list);
		list.setOnItemClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		updateData();
	}
	
	private void initHeaderView(){
		header = CommonAndroid.getView(main_detail, R.id.header);
		btnMycart = (ImageButton)header.findViewById(R.id.header_btn_right_left);
		btnSearch = (ImageButton)header.findViewById(R.id.header_btn_right);
		btnBack = (ImageButton)header.findViewById(R.id.header_btn_left);
		
		header.initHeader(ECFragment.list_name_category ,ECFragment.list_name_category );
		
		btnMycart.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnMycart.setVisibility(View.VISIBLE);
		btnSearch.setVisibility(View.VISIBLE);
		btnMycart.setImageResource(R.drawable.header_v2_icon_mycart);// ic_mycart
		btnSearch.setImageResource(R.drawable.header_v2_icon_search);// icon_search
		btnBack.setImageResource(R.drawable.header_v2_icon_back);// icon_nav_back
		
		header_btn_right_main = (LinearLayout) header.findViewById(R.id.header_btn_right_main);
		header_btn_right_main.setBackgroundColor(0);
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		super.onItemClick(arg0, arg1, arg2, arg3);
		Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
		Bundle extras = new Bundle();
		extras.putString(Products.id, CommonAndroid.getString(cursor, Products.id));
		ECFragment.addtocart_Productsid = CommonAndroid.getString(cursor, Products.id);
		startDetail(SCREEN.PJDETAIL, extras);
	}
	
	public void startDetail(SCREEN screen, Bundle extras) {
		RootActivity.PJ_Detail = new EcProductsDetailDialog(getActivity(),new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		},callbackListenner);
		RootActivity.PJ_Detail.show();
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
		return R.layout.ec_products;
	}

	@Override
	public void onClick(View v) {
		CommonAndroid.hiddenKeyBoard(getActivity(), llSearch);
		/*if(v.getId() == R.id.header_btn_left){
			close(true);
		}else if(v.getId() == R.id.header_btn_right_left){
			
		}else if(v.getId() == R.id.header_btn_right){
//			addViewSearch();
			Bundle extras = new Bundle();
			startActivityForResult(SCREEN.MYCART, extras);
		}*/
		
		
		int idView = v.getId();
		switch (idView) {
		case R.id.clear_search:
			edtSearch.setText("");
			CommonAndroid.showView(false, btnClearSearch);
			break;
		case R.id.header_btn_left:
			close(true);
			break;
		case R.id.header_btn_right_left:
			Bundle extras = new Bundle();
			startActivityForResult(SCREEN.MYCART, extras);
			break;
		case R.id.header_btn_right:
			Log.i(TAG, "goto Search");
			if (llSearch.getChildCount() > 0) {
				llSearch.removeAllViews();
				header_btn_right_main.setBackgroundColor(0);
			}else{
				edtSearch.setText("");
				llSearch.addView(llSearchViewChild);
				header_btn_right_main.setBackgroundColor(getActivity().getResources().getColor(R.color.ec_bg_search));
			}
			break;
		case R.id.search_txtconfirm_id:
			String contentSearch = edtSearch.getText().toString();
			processSearch(contentSearch);
			/*if (llSearch.getChildCount() > 0) {
				llSearch.removeAllViews();
			}*/
			break;
		default:
			break;
		}
		
	}
	
	/*private void addViewSearch(){
		llSearch = (LinearLayout)main_detail.findViewById(R.id.ec_ll_search_id);
		LayoutInflater inf = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		llSearchViewChild = inf.inflate(R.layout.search_layout, null);
		edtSearch = (EditText)llSearchViewChild.findViewById(R.id.search_edt_id);
		txtConfirmSearch = (TextView)llSearchViewChild.findViewById(R.id.search_txtconfirm_id);
	}*/
	
	private static FragmentActivity getActivity() {
		return ECFragment.activity;
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

	private void updateData() {

		new SkyLoader() {
			@Override
			public void loadSucess(Object data) {
				list.setAdapter(new CursorAdapter(getActivity(), (Cursor) data) {

					@Override
					public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
						return CommonAndroid.initView(arg2.getContext(), R.layout.ec_product_item, null);
					}

					@Override
					public void bindView(View arg0, Context context, Cursor arg2) {
						if (arg0 == null) {
							arg0 = CommonAndroid.initView(context, R.layout.ec_product_item, null);
						}
						Setting setting = new Setting(context);
						String price = String.format("%s %s %s"//
								, setting.isLangEng() ? getActivity().getString(R.string.ec_price_unit) : getActivity().getString(R.string.ec_price_unit_j)//
								, CommonAndroid.DecimalFormatPrice(CommonAndroid.getString(arg2, Products.base_price))//
								, setting.isLangEng() ? getActivity().getString(R.string.ec_taxincl) : getActivity().getString(R.string.ec_taxincl_j)//
								);

						CommonAndroid.setText(arg0.findViewById(R.id.text_1), CommonAndroid.getString(arg2, Products.name));//Products.description_short
						CommonAndroid.setText(arg0.findViewById(R.id.text_2), price);
						String status_text = "";
						String status_text_in = " - " + (setting.isLangEng() ? getActivity().getString(R.string.ec_in_stock) : getActivity().getString(R.string.ec_in_stock_j));
						String status_text_out = " - " + (setting.isLangEng() ? getActivity().getString(R.string.ec_out_stock) : getActivity().getString(R.string.ec_out_stock_j));
//						String status_text_tem = ((String[]) list.get(position))[4];
//						if( status_text_tem.equals(getResources().getString(R.string.ec_in_stock)) || status_text_tem.equals(getResources().getString(R.string.ec_in_stock_j)) ){
						int status_text_tem = 0;
						try {
							status_text_tem = Integer.parseInt(CommonAndroid.getString(arg2, Products.quantity));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						/*String status_text_color = "#036898";
						if(status_text_tem > 0){
							status_text_color = "#036898";
							status_text = status_text_in;
						}else{
							status_text_color = "#ee0000";
							status_text = status_text_out;
						}*/
						
						int status_text_color = getActivity().getResources().getColor(R.color.ec_selection_status_text_in);
						if (status_text_tem > 0) {
							status_text_color = getActivity().getResources().getColor(R.color.ec_selection_status_text_in);
							status_text = status_text_in;
						} else {
							status_text_color = getActivity().getResources().getColor(R.color.ec_selection_status_text_out);
							status_text = status_text_out;
						}
						
						TextView text_color = (TextView) arg0.findViewById(R.id.text_3);
						text_color.setTextColor(status_text_color);
						CommonAndroid.setText(arg0.findViewById(R.id.text_3), status_text /*((String[]) list.get(position))[4]*/);
						
						ImageLoaderUtils.getInstance(context).displayImageEcProduct(CommonAndroid.getString(arg2, Products.thumbnail), (ImageView) CommonAndroid.getView(arg0, R.id.img));
					}
				});
				
			}

			@Override
			public Object loadData() {
//				Cursor productOfCategory = new ProductsOfCategories(getActivity()).querry(String.format("%s = '%s' and %s = '%s'", ProductsOfCategories.user_id, new Account(getActivity()).getUserId()//
//						, ProductsOfCategories.id_category, ECFragment.list_id_category ));
//
//				String products = "";
//
//				while (productOfCategory != null && productOfCategory.moveToNext()) {
//					if (CommonAndroid.isBlank(products)) {
//						products = CommonAndroid.getString(productOfCategory, ProductsOfCategories.id_product);
//					} else {
//						products = String.format("%s,%s", products, CommonAndroid.getString(productOfCategory, ProductsOfCategories.id_product));
//					}
//				}

//				if (productOfCategory != null) {
//					productOfCategory.close();
//				}
				
//				String where = String.format("%s = '%s' and %s in(%s) "//
//						, Products.user_id, new Account(getActivity()).getUserId() //
//						, Products.id, products);//
				
				String where = String.format("%s = '%s' and %s = '%s' "//
						, Products.user_id, new Account(getActivity()).getUserId() //
						, Products.id_ec, ECFragment.list_id_category );//

				Cursor cursor = new Products(getActivity()).querry(where);
				return cursor;
			}
		}.executeLoader(500);

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
	
	/**
	 * this method will process search
	 * @param contentSearch
	 * @return return data search
	 */
	private void processSearch(String contentSearch) {
		Map<String, String> params = new HashMap<String, String>();
		params.put("req[param][s]", edtSearch.getText().toString());
		SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_EC_SEARCH, params, new CheerzServiceCallBack(){
			
			@Override
			public void onStart() {
				CommonAndroid.showView(true, loading);
				super.onStart();
			}
			
			@Override
			public void onError(String message) {
				super.onError(message);
				CommonAndroid.showView(false, loading);
				Log.i(TAG, "ERROR : " + message);
			}
			
			@Override
			public void onSucces(String response) {
				super.onSucces(response);
				Log.i(TAG, "Success : " + response);
				CommonAndroid.showView(false, loading);
				try {
					JSONObject jsonRes = new JSONObject(response);
					if (jsonRes.has("data")) {
						JSONObject jsonData = jsonRes.getJSONObject("data");
						JSONArray jsonArray = jsonData.getJSONArray("products");
						if (jsonArray.length() > 0) {
							gotoResultScreen(response);
						}else{
							String message = getActivity().getResources().getString(R.string.msg_result_search_not_found);
							if (!new Setting(getActivity()).isLangEng()) {
								message = getActivity().getResources().getString(R.string.msg_result_search_not_found_j);
							}
							SkyUtils.showDialog(getActivity(), message, null);
						}
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		});
		
	}

	private void gotoResultScreen(String response) {
		RootActivity.PJ_SearchDialog = new ECSearchProductsDialog(getActivity(), response,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		RootActivity.PJ_SearchDialog.show();
	}
}