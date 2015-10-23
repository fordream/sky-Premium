package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;
import java.util.Map;

import org.com.atmarkcafe.service.CallbackListenner;
import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.dialog.ECProductsDialog;
import org.com.atmarkcafe.sky.dialog.ECSearchProductsDialog;
import org.com.atmarkcafe.sky.dialog.EcProductsDetailDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountFavoriteDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountMyAddressDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountMyCreditDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountOptionDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountOrderHistoryDialog;
import org.com.atmarkcafe.view.ECBlogView;
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
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Categories;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.Setting;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class ECFragment extends BaseFragment implements OnClickListener{
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
	private LinearLayout llSearchViewChild;
	private TextView txtConfirmSearch,edtSearch,txtHeader;
	private ImageButton btnSearch,btnMycart,btnBack;
	private Setting setting;
	DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	private ImageView btnClearSearch;
	static RelativeLayout main_block;
	private LinearLayout header_btn_right_main;
	CallbackListenner callbackListenner = new CallbackListenner() {
		
		@Override
		public void onLoad(boolean onload) {
			LogUtils.i(TAG, "AAAAAAAAAAAA===callbackListenner ");
			DataCache = false;
			list.removeAllViews();
			HashMap<String, String> inputs = new HashMap<String, String>();
			inputs.put("req[param][id]", "");
			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_PRODUCTLIST, inputs, callback);
			EcProductsDetailDialog.flagResetEc = false;
		}
	};
	
	public ECFragment() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.ec;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		Log.i(TAG, "onActivityResult");
	}
	@Override
	public void onClickHeaderLeft() {
		super.onClickHeaderLeft();
		finish();
	}
	
	@Override
	public void onClickHeaderRight() {
		super.onClickHeaderRight();
		Bundle extras = new Bundle();
		gotoMycart(SCREEN.MYCART, extras);
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
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				updateData();
			}
		}

		public void onSucces(String response) {
			CommonAndroid.showView(false, loading);
			LogUtils.i(TAG, "AAAAAAAAAAAA===callbackListenner success : " + DataCache);
			if (!isFinish()) {
				if(!DataCache)
					LogUtils.i(TAG, "dataEC update" );
					updateData();
			}
			LogUtils.i(TAG, "dataEC = " +response.toString());
		}
	};

	@Override
	public void init(View view) {
		setting = new Setting(getActivity());
		initHeaderView(view);
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.btn_checkout, getResources().getColor(R.color.ec_btn_checkout));//OK
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.bgr_green, getResources().getColor(R.color.ec_btn_search));//OK
		activity = getActivity();
		loading = CommonAndroid.getView(view, R.id.loading);
		list = CommonAndroid.getView(view, R.id.list);
		main_block = CommonAndroid.getView(view, R.id.main_block);
		llSearch = (LinearLayout)view.findViewById(R.id.ec_ll_search_id);
		LayoutInflater inf = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		llSearchViewChild = (LinearLayout) inf.inflate(R.layout.search_layout, null);
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
		// handler action done
		edtSearch.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					String contentSearch = edtSearch.getText().toString();
					hideSoftKeyboard();
					setupUI(llSearch);
					processSearch(contentSearch);
					/*if (llSearch.getChildCount() > 0) {
						llSearch.removeAllViews();
					}*/
				}
				return false;
			}
		});
		
//		updateData();
		callApi();
		
		CommonAndroid.getView(view, R.id.btn_myaccount).setOnClickListener(this);
		
		//Handler scroll of listview
		
	}
	
	private void initHeaderView(View mainview){
		View view = mainview.findViewById(R.id.header);
		btnMycart = (ImageButton)view.findViewById(R.id.header_btn_right_left);
		btnSearch = (ImageButton)view.findViewById(R.id.header_btn_right);
		btnBack = (ImageButton)view.findViewById(R.id.header_btn_left);
		
		/*txtHeader = (TextView)view.findViewById(R.id.header_title);
		txtHeader.setVisibility(View.VISIBLE);
		if (setting.isLangEng()) {
			txtHeader.setText(getResources().getString(R.string.ec_home));
		}else{
			txtHeader.setText(getResources().getString(R.string.ec_home_j));
		}*/
		
		btnMycart.setOnClickListener(this);
		btnSearch.setOnClickListener(this);
		btnBack.setOnClickListener(this);
		btnMycart.setVisibility(View.VISIBLE);
		btnSearch.setVisibility(View.VISIBLE);
		btnMycart.setImageResource(R.drawable.header_v2_icon_mycart);//  ic_mycart
		btnSearch.setImageResource(R.drawable.header_v2_icon_search);//  icon_search
		btnBack.setImageResource(R.drawable.header_v2_icon_back);//  icon_nav_back
		header_btn_right_main = (LinearLayout) view.findViewById(R.id.header_btn_right_main);
		header_btn_right_main.setBackgroundColor(0);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		int idView = v.getId();
		switch (idView) {
		case R.id.clear_search:
			edtSearch.setText("");
			CommonAndroid.showView(false, btnClearSearch);
			break;
		case R.id.header_btn_left:
			Intent intent = new Intent();
			intent.putExtra("ec_back", "1");
			getActivity().setResult(getActivity().RESULT_OK, intent);
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
				header_btn_right_main.setBackgroundColor(0);
			}else{
				edtSearch.setText("");
				llSearch.addView(llSearchViewChild);
				header_btn_right_main.setBackgroundColor(getResources().getColor(R.color.ec_bg_search));
			}
			break;
		case R.id.search_txtconfirm_id:
			String contentSearch = edtSearch.getText().toString();
			hideSoftKeyboard();
			setupUI(llSearch);
			processSearch(contentSearch);
			/*if (llSearch.getChildCount() > 0) {
				llSearch.removeAllViews();
			}*/
			break;
		case R.id.btn_myaccount:
			ECBlogView.setautoslide = false;
			new MyAccountOptionDialog(getActivity(), new DialogInterface.OnClickListener() {
				@SuppressLint("NewApi")
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (which == 0) {
//						myaccount_history
						RootActivity.PJ_HistoryDialog = new MyAccountOrderHistoryDialog(getActivity(), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
							
						});
						RootActivity.PJ_HistoryDialog.show();
					
					} else if (which == 1) {
//						myaccount_address
						new MyAccountMyAddressDialog(getActivity(), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
							
						}).show();
					} else if (which == 2) {
//						myaccount_info
//						Intent intent = new Intent();
//				        intent.putExtra(SkyMainActivity.EXTRA_CHECK_BACK_PROFILE, SkyMainActivity.EXTRA_CHECK_BACK_PROFILE);
//				        getActivity().setResult(getActivity().RESULT_OK, intent);
//						finish();
				        Intent intent = new Intent(activity, SkyMainActivity.class);
				        intent.putExtra(SkyMainActivity.EXTRA_CHECK_BACK_PROFILE, SkyMainActivity.EXTRA_CHECK_BACK_PROFILE);
				        activity.startActivityForResult(intent, 0);
						/*new MyAccountInfoDialog(getActivity(), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
							
						}).show();*/
					} else if (which == 3) {
//						myaccount_favorite
						RootActivity.PJ_FavoriteDialog = new MyAccountFavoriteDialog(getActivity(), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
							
						});
						RootActivity.PJ_FavoriteDialog.show();
					} else if (which == 4) {
//						LogUtils.i(TAG, "which==" + which);
//						myaccount_credit
						new MyAccountMyCreditDialog(getActivity(), new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
							}
							
						}).show();
					} else{
						ECBlogView.setautoslide = true;
					}
				}
			}).show();
			break;
		default:
			break;
		}
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
							String message = getResources().getString(R.string.msg_result_search_not_found);
							if (!setting.isLangEng()) {
								message = getResources().getString(R.string.msg_result_search_not_found_j);
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

	int countLine = 0;
	int countItem = 0;
	private void updateData() {
		if(!back)
			CommonAndroid.showView(true, loading);
		loadSkypeLoader(new SkyLoader() {
			
			@Override
			public void loadSucess(Object data) {
//				if(!back)
//					list.removeAllViews();
				Cursor cursor = (Cursor) data;
				if (cursor != null) {
					
					while (cursor.moveToNext()) {
						countLine++;
						countItem = cursor.getCount();
						Log.i(TAG, "======X Count : " +countItem);
						ECBlogView blogView = new ECBlogView(getActivity(),countLine) {
							@Override
							public void openProduct(String id) {
								Bundle extras = new Bundle();
								extras.putString(Products.id, id);
								ECFragment.addtocart_Productsid = id;
								startDetail(SCREEN.PJDETAIL, extras);
							}

							@Override
							public void onCLick(String id_ec, String name) {
								ECFragment.list_id_category = id_ec;
								ECFragment.list_name_category = name;
								RootActivity.PJ_ListCat = new ECProductsDialog(getActivity(),new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
									}
								});
								RootActivity.PJ_ListCat.show();
								
							}
						};
						
						DataCache = true;
						
						blogView.update(getActivity(),CommonAndroid.getString(cursor, Categories.id), CommonAndroid.getString(cursor, Categories.name),countLine+1,countItem-1);
						list.addView(blogView);
						
					}
					LogUtils.e(TAG, "===updateData");
					CommonAndroid.showView(false, loading);
					cursor.close();
				}else{
					callApi();
				}
				
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
		/*if (extras == null) {
			extras = new Bundle();
		}
		extras.putBoolean(SkyUtils.KEY.KEY_SCREEN_TAG, true);
		Intent intent = new Intent(getActivity(), ECDetailActivity.class);
		intent.putExtras(extras);

		getActivity().startActivityForResult(intent, 0);
		getActivity().overridePendingTransition(R.anim.right_in, 0);*/
		RootActivity.PJ_Detail = new EcProductsDetailDialog(getActivity(),new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		},callbackListenner);
		showView(false);
		RootActivity.PJ_Detail.show();
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
	
	/**
	 * this metho should parser result search and save to list
	 * @param response
	 */
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
	    if(!(view instanceof ViewGroup)) {

	        view.setOnTouchListener(new OnTouchListener() {

	            public boolean onTouch(View v, MotionEvent event) {
	            	hideSoftKeyboard();
	                return false;
	            }

	        });
	    }
	    
	}
	
	/**
	 * hide softkeyboard
	 */
	private void hideSoftKeyboard(){
			InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (inputMethodManager.isAcceptingText()) {
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
				Log.i(TAG, "HIDE KEYBOARD");
			}
	}
	
	public static void showView(Boolean show){
		LinearLayout footer = CommonAndroid.getView(main_block, R.id.block_myaccount);
		footer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		main_block.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}
}