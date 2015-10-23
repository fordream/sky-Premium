/*package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;
import java.util.Map;

import org.com.atmarkcafe.service.CallbackListenner;
import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.SkyMainActivity;

import com.SkyPremiumLtd.SkyPremium.R;

import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.dialog.ECProductsDialog;
import org.com.atmarkcafe.sky.dialog.ECSearchProductsDialog;
import org.com.atmarkcafe.sky.dialog.EcProductsDetailDialog;
import org.com.atmarkcafe.view.ECBlogView;
import org.com.atmarkcafe.view.ECBlogViewUpdate12052015;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyLoader;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.SkyUtils.SCREEN;
import z.lib.base.callback.RestClient.RequestMethod;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Categories;
import com.acv.cheerz.db.MainGallery;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.ProductsOfCategories;

public class ECFragmentUpdate12052015 extends BaseFragment implements OnClickListener{
	private static final String TAG = "ECFragment";
	private LinearLayout list;
	private LoadingView loading;
	Boolean onLoad = true;
	Boolean DataCache = false;
	Boolean back = false;
	public static String addtocart_quantityaddcart ="";
	public static String addtocart_Productsid = "";
	public static String addtocart_datarespon ="";
	public static String list_id_category = "";
	public static String list_name_category = "";
	public static FragmentActivity activity;
	private LinearLayout llSearch;
	private View llSearchViewChild;
	private TextView txtConfirmSearch,edtSearch;
	private ImageButton btnSearch,btnMycart,btnBack;
	
	public ECFragmentUpdate12052015() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.ec;
	}

	@Override
	public void onClickHeaderLeft() {
		super.onClickHeaderLeft();
		finish();
//		onBackPressed(new Bundle());
	}

	@Override
	public void onClickHeaderRight() {
		super.onClickHeaderRight();
		Bundle extras = new Bundle();
		gotoMycart(SCREEN.MYCART, extras);
//		startFragment(new MyCart2Fragment(), null);
	}
	
	public static void gotoMycart(SCREEN screen, Bundle extras) {
		if (extras == null) {
			extras = new Bundle();
		}
		extras.putSerializable(SkyUtils.KEY.KEY_SCREEN, screen);
		extras.putBoolean(SkyUtils.KEY.KEY_SCREEN_TAG, true);
		Intent intent = new Intent(activity, MyCartActivity.class);
		intent.putExtras(extras);
		activity.startActivityForResult(intent, 0);
		activity.overridePendingTransition(R.anim.right_in, 0);
	}

	private void callApi() {
		LogUtils.e(TAG, "callApi===========");
		CommonAndroid.showView(false, loading);
		if(onLoad){
			onLoad = false;
			HashMap<String, String> inputs = new HashMap<String, String>();
			inputs.put("req[param][id]", "");
			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_PRODUCTLIST, inputs, callback);
		}
		
	}

	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {

		public void onStart() {
			if(!DataCache)
				CommonAndroid.showView(true, loading);
		}

		public void onError(String message) {
			if (!isFinish()) {
				updateData();
				CommonAndroid.showView(false, loading);
			}
		}

		public void onSucces(String response) {
			if (!isFinish()) {
				if(!DataCache)
					updateData();
				CommonAndroid.showView(false, loading);
			}
			Log.i(TAG, "dataEC = " +response.toString());
		}
	};

	@Override
	public void init(View view) {
		initHeaderView(view);
		activity = getActivity();
		//initHeader(view, R.id.header, R.string.ec_home, R.string.ec_home_j, R.drawable.icon_nav_back, R.drawable.ic_mycart);
		loading = CommonAndroid.getView(view, R.id.loading);
		list = CommonAndroid.getView(view, R.id.list);
		llSearch = (LinearLayout)view.findViewById(R.id.ec_ll_search_id);
		LayoutInflater inf = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		llSearchViewChild = inf.inflate(R.layout.search_layout, null);
		edtSearch = (EditText)llSearchViewChild.findViewById(R.id.search_edt_id);
		txtConfirmSearch = (TextView)llSearchViewChild.findViewById(R.id.search_txtconfirm_id);
		txtConfirmSearch.setOnClickListener(this);
		
		// updateData();
		CommonAndroid.showView(false, loading);
		try {
			if("back".equals(getArguments().getString("back") )){
				back = true;
				LogUtils.e(TAG, "back==" + back);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!back){
			CommonAndroid.showView(true, loading);
		}
		updateData();
		
		
		
	}
	
	private void initHeaderView(View mainview){
		View view = mainview.findViewById(R.id.header);
		btnMycart = (ImageButton)view.findViewById(R.id.header_btn_right_left);
		btnSearch = (ImageButton)view.findViewById(R.id.header_btn_right);
		btnBack = (ImageButton)view.findViewById(R.id.header_btn_left);
		
		btnMycart.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnMycart.setVisibility(View.VISIBLE);
		btnSearch.setVisibility(View.VISIBLE);
		btnMycart.setImageResource(R.drawable.ic_mycart);
		btnSearch.setImageResource(R.drawable.icon_search);
		btnBack.setImageResource(R.drawable.icon_nav_back);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			finish();
//			processSearch("g");
			break;
		case R.id.header_btn_right_left:
			Log.i(TAG, "goto MyCart");
			Bundle extras = new Bundle();
			gotoMycart(SCREEN.MYCART, extras);
			break;
		case R.id.header_btn_right:
			Log.i(TAG, "goto Search");
			if (llSearch.getChildCount() > 0) {
				llSearch.removeAllViews();
			}else{
				edtSearch.setText("");
				llSearch.addView(llSearchViewChild);
			}
			break;
		case R.id.search_txtconfirm_id:
			Log.i(TAG, "process Search");
			String contentSearch = edtSearch.getText().toString();
			processSearch(contentSearch);
			setupUI(txtConfirmSearch);
			break;
		default:
			break;
		}
	}
	
	*//**
	 * this method will process search
	 * @param contentSearch
	 * @return return data search
	 *//*
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
				//GlobalFunction.writeText2File(response,"DATA_SEARCH");
//				parserDataSearch(response);
				gotoResultScreen(response);
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

	@Override
	public void onResume(){
		super.onResume();
		LogUtils.e(TAG, "test=========onResume");
		if(RootActivity.onLoadEC){
			onLoad = true;
			DataCache = false;
			RootActivity.onLoadEC = false;
//			callApi();
		}
	}

	private void updateData() {
		if(!back)
			CommonAndroid.showView(true, loading);
		loadSkypeLoader(new SkyLoader() {
			
			@Override
			public void loadSucess(Object data) {
				if(!back)
					list.removeAllViews();
				Cursor cursor = (Cursor) data;
				if (cursor != null) {
					
					while (cursor.moveToNext()) {
						
						final ECBlogView blogView = new ECBlogView(getActivity()) {
							@Override
							public void openProduct(String id) {
								Bundle extras = new Bundle();
								extras.putString(Products.id, id);
//								startFragment(new EcProductsDetailFragment(), extras);
								ECFragmentUpdate12052015.addtocart_Productsid = id;
								startDetail(SCREEN.PJDETAIL, extras);
							}

							@Override
							public void onCLick(String id_ec, String name) {
//								Bundle extras = new Bundle();
//								extras.putString("name", name);
//								extras.putString(ProductsOfCategories.id_category, id_ec);
								ECFragmentUpdate12052015.list_id_category = id_ec;
								ECFragmentUpdate12052015.list_name_category = name;
//								startFragment(new ECProductsFragment(), extras);
								RootActivity.PJ_ListCat = new ECProductsDialog(getActivity(),new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								});
								RootActivity.PJ_ListCat.show();
								
							}
						};
						
						
						DataCache = true;
						blogView.update(getActivity(),CommonAndroid.getString(cursor, Categories.id), CommonAndroid.getString(cursor, Categories.name));
						list.addView(blogView);
					}
					LogUtils.e(TAG, "===updateData");
					cursor.close();
				}
				callApi();
			}

			@Override
			public Object loadData() {
				Cursor cursor = new Categories(getActivity()).querry(String.format("%s = '%s' and %s = 'ec'"//
						, Categories.user_id, new Account(getActivity()).getUserId()//
						, Categories.type //
						));

				return cursor;
			}
		}, 500);

	}

	public void startDetail(SCREEN screen, Bundle extras) {
		if (extras == null) {
			extras = new Bundle();
		}
		extras.putBoolean(SkyUtils.KEY.KEY_SCREEN_TAG, true);
		Intent intent = new Intent(getActivity(), ECDetailActivity.class);
		intent.putExtras(extras);

		getActivity().startActivityForResult(intent, 0);
		getActivity().overridePendingTransition(R.anim.right_in, 0);
//		RootActivity.PJ_Detail = new EcProductsDetailDialog(getActivity(),new DialogInterface.OnClickListener() {
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//			}
//		},CallbackListenner);
//		RootActivity.PJ_Detail.show();
	}
	
	@Override
	public void onChangeLanguage() {
		onLoad = true;
		DataCache = false;
		LogUtils.e(TAG, "onChangeLanguage==");
		callApi();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		super.onItemClick(arg0, arg1, arg2, arg3);

	}

	@Override
	public void reload() {
		super.reload();
	}
	
	*//**
	 * this metho should parser result search and save to list
	 * @param response
	 *//*
	private void parserDataSearch(String response) {
		try {
			JSONObject jsonData = new JSONObject(response);
			JSONArray jsonArrProducts = jsonData.getJSONArray("products");
			int size = jsonArrProducts.length();
			for (int i = 0; i < size; i++) {
				JSONObject jsonObj = jsonArrProducts.getJSONObject(i);
				
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void setupUI(View view) {

	    //Set up touch listener for non-text box views to hide keyboard.
	    if(!(view instanceof EditText)) {

	        view.setOnTouchListener(new OnTouchListener() {

	            public boolean onTouch(View v, MotionEvent event) {
	            	hideSoftKeyboard();
	                return false;
	            }

	        });
	    }
	    
	}
	
	*//**
	 * hide softkeyboard
	 *//*
	private void hideSoftKeyboard(){
			InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (inputMethodManager.isAcceptingText()) {
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
			}
	}
}*/