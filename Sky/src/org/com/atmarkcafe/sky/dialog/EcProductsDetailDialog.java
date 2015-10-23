package org.com.atmarkcafe.sky.dialog;

import java.util.HashMap;

import org.com.atmarkcafe.service.CallbackListenner;
import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.customviews.charting.MTextView;
import org.com.atmarkcafe.sky.fragment.ContactFragment;
import org.com.atmarkcafe.sky.fragment.ECFragment;
import org.com.atmarkcafe.sky.fragment.HomeFragment;
import org.json.JSONObject;

import z.lib.base.Base2Adialog;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderUtils;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyLoader;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.SkyUtils.SCREEN;
import z.lib.base.callback.RestClient.RequestMethod;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.ECGallery;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.Setting;

public class EcProductsDetailDialog extends Base2Adialog implements android.view.View.OnClickListener {
	protected static final String TAG = "EcProductsDetailDialog";
	static RelativeLayout main_detail;
	private org.com.atmarkcafe.view.HeaderECView header;
	private EditText edt;
	private se.emilsjolander.flipview.FlipView flip;
	private LoadingView loading;
	int quantity = 0;
	private ScrollView parentView;
	RelativeLayout rl;
	private View views;
	TextView text_4, text_4_validate_gold;
	private WebView credit;
	private String is_access ="1";
	RelativeLayout temp_layout_id;
	private WebView webview;
	LinearLayout block_header;
	private MTextView txtTitleSp,txtSubtitleSp;
	private int is_favorite = 0;
	public static boolean flagResetEc = false;
	private static Context mContext;
	private CallbackListenner mCallback;
	public EcProductsDetailDialog(Context context, OnClickListener clickListener,CallbackListenner callback) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		mContext = context;
		mCallback = callback;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.bgr_add_cart, getActivity().getResources().getColor(R.color.ec_btn_addtocard));//OK
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(getActivity().getString(R.string.product_detail) ,getActivity().getString(R.string.product_detail_j));
		header.visibivityLeft(true,R.drawable.header_v2_icon_back );// icon_nav_back
		header.visibivityRight(true,R.drawable.header_v2_icon_mycart );// ic_mycart
		CommonAndroid.getView(main_detail, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.header_btn_right).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.text_4).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.text_4_validate_gold).setOnClickListener(this);
		txtTitleSp = (MTextView)main_detail.findViewById(R.id.text_1_name);
		
		txtSubtitleSp = (MTextView)main_detail.findViewById(R.id.text_1);
		boolean isTablet = GlobalFunction.isTablet(getActivity());
		Log.i(TAG, "isTablet = " + isTablet);
		if (isTablet) {
			txtTitleSp.setTextSize(20);
			txtSubtitleSp.setTextSize(12);
		}
		webview = (WebView)main_detail.findViewById(R.id.webview_id);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.setBackgroundColor(Color.parseColor("#ededed"));
		
		credit = new WebView(getActivity());
		credit.getSettings().setJavaScriptEnabled(true);
		credit.getSettings().setPluginState(PluginState.ON);
		credit.getSettings().setAllowFileAccess(true);
		// credit.requestFocus(View.FOCUS_DOWN);
		credit.setHorizontalScrollBarEnabled(false);
		credit.setBackgroundColor(Color.parseColor("#ededed"));
		// test
		credit.getSettings().setRenderPriority(RenderPriority.HIGH);
		credit.getSettings().setSaveFormData(true);
		credit.getSettings().setAppCacheMaxSize(1024 * 1024 * 10);
		credit.getSettings().setAppCacheEnabled(true);
		
//		test
//		credit.getSettings().setLoadWithOverviewMode(true);
//		credit.getSettings().setUseWideViewPort(true); 
//		credit.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
		rl = (RelativeLayout) main_detail.findViewById(R.id.view_detail);
		rl.addView(credit);
		
//		initHeader(view, R.id.header, getString(R.string.product_detail), getString(R.string.product_detail_j), R.drawable.icon_nav_back, R.drawable.cart);
//		initHeader(main_detail, R.id.header, "", "", R.drawable.icon_nav_back, R.drawable.cart);
//		CommonAndroid.getView(main_detail, R.id.text_4).setOnClickListener(this);
		CommonAndroid.setText(CommonAndroid.getView(main_detail, R.id.text_1), "");
		CommonAndroid.setText(CommonAndroid.getView(main_detail, R.id.text_2), "");
//		CommonAndroid.setText(CommonAndroid.getView(view, R.id.text_3), "");
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		CommonAndroid.showView(false, loading);
		edt = CommonAndroid.getView(main_detail, R.id.edt_1);
		flip = CommonAndroid.getView(main_detail, R.id.flip);
		text_4 = (TextView) main_detail.findViewById(R.id.text_4);
		temp_layout_id = CommonAndroid.getView(main_detail, R.id.temp_layout_id);
		temp_layout_id.setOnClickListener(this);
		getProductDetail();
		// HashMap<String, String> inputs = new HashMap<String, String>();
		// inputs.put("req[param][id_product]",
		// getArguments().getString(Products.id));
		// SkyUtils.execute(getActivity(), RequestMethod.GET,
		// API.API_EC_PRODUCTDETAIL, inputs, callback);
		edt.setOnEditorActionListener(new EditText.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER))
                        || (actionId == EditorInfo.IME_ACTION_DONE) || (actionId == EditorInfo.IME_ACTION_NEXT) /*|| (actionId == EditorInfo.IME_ACTION_SEARCH)  */) {
                    //Do your action
//					addToCart();
					
                }
				CommonAndroid.hiddenKeyBoard(getActivity());
				return false;
			}
        });
		
		edt.addTextChangedListener(new TextWatcher() {

	          public void afterTextChanged(Editable s) {
	        	  	String input = edt.getText().toString().trim();
	        	  	input.replace("-%^@&*@**&@", "");
	        	  	int count = 0;
	        	  	if(!"".equals(input)){
	        	  		int temp = Integer.parseInt( input );
	        	  		count = temp;
						if(temp > quantity && temp > 0){
							SkyUtils.showDialog(getActivity(), new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_quantity_validate) : getActivity().getString(R.string.ec_quantity_validate_j) ,null);
							edt.setText("");
						}
	        	  	}
	        	  	
	        	  	validEditext(count);
	          }

	          public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	          public void onTextChanged(CharSequence s, int start, int before, int count) {
//	        	  int position = edt.getSelectionStart();
	        	  edt.setSelection(edt.getText().toString().trim().length());
	          }
	       });
		
		
		parentView = CommonAndroid.getView(main_detail, R.id.scroll);
		CommonAndroid.TouchViewhiddenKeyBoard(getActivity(),parentView);
		block_header = CommonAndroid.getView(main_detail, R.id.block_header);
		CommonAndroid.getView(main_detail, R.id.favorite_click).setOnClickListener(this);
	}
	
	private void validEditext(int number){
		if (number == 0) {
			text_4.setAlpha(0.5f);
			text_4.setEnabled(false);
//			edt.setEnabled(false);
			temp_layout_id.setAlpha(0.5f);
			temp_layout_id.setEnabled(false);
			ImageView icon_add_cart_id = (ImageView) CommonAndroid.getView(main_detail, R.id.icon_add_cart_id);
			icon_add_cart_id.setAlpha(0.5f);
			icon_add_cart_id.setEnabled(false);
		}else{
			text_4.setAlpha(1.0f);
			text_4.setEnabled(true);
//			edt.setEnabled(false);
			temp_layout_id.setAlpha(1.0f);
			temp_layout_id.setEnabled(true);
			ImageView icon_add_cart_id = (ImageView) CommonAndroid.getView(main_detail, R.id.icon_add_cart_id);
			icon_add_cart_id.setAlpha(0.5f);
			icon_add_cart_id.setEnabled(true);
		}
	}
	private void loadSkypeLoader(SkyLoader skyLoader, int i) {
		// TODO Auto-generated method stub
		skyLoader.executeLoader(i);
	} 

	@Override
	public void onBackPressed(){
		try {
			ECFragment.showView(true);
		} catch (Exception e) {}
		close(true);
	}
	

	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.main_detail), new AnimationAction() {
			
			@Override
			public void onAnimationEnd() {
				RootActivity.PJ_Detail = null;
				dismiss();
				if (isOnClick) {
					clickListener.onClick(null, 0);
				}
			}
		});
	}

	@Override
	public int getLayout() {
		return R.layout.ec_product_detail;
	}

	@Override
	public void onClick(View v) {
		CommonAndroid.hiddenKeyBoard(getActivity());
		if (v.getId() == R.id.text_4_validate_gold || v.getId() == R.id.text_4 || v.getId() == R.id.temp_layout_id) {
			//add to cart
			addToCart();
		}else if(v.getId() == R.id.header_btn_left){
			if (flagResetEc) {
				mCallback.onLoad(flagResetEc);
			}
			try {
				ECFragment.showView(true);
			} catch (Exception e) {}
			close(true);
		}else if(v.getId() == R.id.header_btn_right){
			Bundle extras = new Bundle();
			startActivityForResult(SCREEN.MYCART, extras);
		}else if(v.getId() == R.id.favorite_click){
			changeFavorite();
		}
	}
	
	private void changeFavorite() {
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][id_product]", ECFragment.addtocart_Productsid );
		SkyUtils.execute(getActivity(), RequestMethod.GET, is_favorite == 0 ? API.API_MYACCOUNT_FAVORITE_ADD : API.API_MYACCOUNT_FAVORITE_DEL, inputs, new CheerzServiceCallBack() {

			public void onStart() {
				CommonAndroid.showView(true, loading);
			}

			public void onError(String message) {
				if (!isFinish()) {
					CommonAndroid.showView(false, loading);
					SkyUtils.showDialog(getActivity(), "" + message,null);
				}
			}
			
			
			public void onSucces(String response) {
				if (!isFinish()) {
					CommonAndroid.showView(false, loading);
					JSONObject mainJsonObject;
					try {
						mainJsonObject = new JSONObject(response);
						String is_succes = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.is_success);
						String err_msg = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.err_msg);
						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							if(is_favorite == 0)
								is_favorite = 1;
							else
								is_favorite = 0;
							setStatusFavorite();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					
				}
			}

		});
	}

	private static FragmentActivity getActivity() {
		return (FragmentActivity) mContext/*ECFragment.activity*/;
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
		loadSkypeLoader(new SkyLoader() {
			Cursor cursorProduct;
			Cursor cursorGallery;

			@Override
			public void loadSucess(Object data) {
				parentView.setVisibility(View.VISIBLE);
				Cursor cursor = cursorProduct;
				if (cursor != null && cursor.moveToFirst()) {
//					CommonAndroid.setText((TextView) CommonAndroid.getView(getView(), R.id.text_1), cursor, Products.description_short/*Products.name*/);
					CommonAndroid.setText((TextView) CommonAndroid.getView(main_detail, R.id.text_2), String.format("%s %s %s", 
							new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_price_unit) : getActivity().getString(R.string.ec_price_unit_j),
							CommonAndroid.DecimalFormatPrice( CommonAndroid.getString(cursor, Products.base_price)), 
							new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_taxincl) : getActivity().getString(R.string.ec_taxincl_j)) //
							);
//					CommonAndroid.setText((TextView) CommonAndroid.getView(getView(), R.id.text_3), cursor, Products.description);
					// CommonAndroid.setText((TextView)
					// CommonAndroid.getView(getView(), R.id.edt_1), cursor,
					// Products.quantity);
					String div_block = CommonAndroid.getString(cursor, Products.description_short);
					div_block = div_block.replaceAll("<p>", "");
					div_block = div_block.replaceAll("</p>", "");
					CommonAndroid.setText((TextView) CommonAndroid.getView(main_detail, R.id.text_1), div_block);
					CommonAndroid.setText((TextView) CommonAndroid.getView(main_detail, R.id.text_1_name), CommonAndroid.getString(cursor, Products.name));
					quantity = Integer.parseInt(CommonAndroid.getString(cursor, Products.quantity));
					updateStatus();
					updataDetail(CommonAndroid.getString(cursor, Products.description));
//					CommonAndroid.writeTextToFile(CommonAndroid.getString(cursor, Products.description));
					/*if(quantity == 0){
						edt.setText("0");
						text_4.setAlpha(0.5f);
						text_4.setEnabled(false);
						edt.setEnabled(false);
						temp_layout_id.setAlpha(0.5f);
						temp_layout_id.setEnabled(false);
						ImageView icon_add_cart_id = (ImageView) CommonAndroid.getView(main_detail, R.id.icon_add_cart_id);
						icon_add_cart_id.setAlpha(0.5f);
						icon_add_cart_id.setEnabled(false);
					}*/
//					initHeader(views, R.id.header, CommonAndroid.getString(cursor, Products.name)  , CommonAndroid.getString(cursor, Products.name)  , R.drawable.icon_nav_back, R.drawable.cart);
//					header.initHeader(CommonAndroid.getString(cursor, Products.name),  CommonAndroid.getString(cursor, Products.name) );
					cursor.close();
				}

				if (cursorGallery != null) {
					CursorAdapter cursorAdapter = new CursorAdapter(getActivity(), cursorGallery) {

						@Override
						public View newView(Context context, Cursor cursor, ViewGroup parent) {
							return new ImageView(context);
						}

						@Override
						public void bindView(View view, Context context, Cursor cursor) {
							if (view == null) {
								view = new ImageView(context);
							}

							((ImageView) view).setScaleType(ScaleType.FIT_CENTER);
//							Log.i(TAG, "URL = " + CommonAndroid.getString(cursor, ECGallery.url));	
							ImageLoaderUtils.getInstance(context).displayImageEcProduct(CommonAndroid.getString(cursor, ECGallery.url), (ImageView) view);
						}
					};

					flip.setAdapter(cursorAdapter);
				}
			}

			@Override
			public Object loadData() {

				if (getActivity() == null) {
					return null;
				}

				String where = String.format("%s = '%s' and %s = '%s'"//
						, Products.user_id, new Account(getActivity()).getUserId()//
						, Products.id, ECFragment.addtocart_Productsid//
						);

				cursorProduct = new Products(getActivity()).querry(where);

				String whereGallery = String.format("%s = '%s' and %s = '%s'"//
						, ECGallery.user_id, new Account(getActivity()).getUserId()//
						, ECGallery.id_products, ECFragment.addtocart_Productsid//
						);

				cursorGallery = new ECGallery(getActivity()).querry(whereGallery);
				return null;
			}
		}, 500);
	}
	
	private void updataDetail(String div_block) {
		try {
//			Log.i(TAG, "div_block = " + div_block);
			//width:860px
//			div_block = div_block.replaceAll("width:860px", "width:100%");
			/*div_block = div_block.replaceAll("<br />", "<br/>");
			div_block = div_block.replaceAll("<br/>", "");
			div_block = div_block.replaceAll("<p>&nbsp;</p>", "");
			div_block = div_block.replaceAll("<p>　</p>", "");
			div_block = div_block.replaceAll("<p>  </p>", "");
			div_block = div_block.replaceAll("<p>&#12288;</p>", "");
			div_block = div_block.replaceAll("&nbsp;", "");
			div_block = div_block.replaceAll("\r\n", "\n");
			div_block = div_block.replaceAll("margin-left:", "margin-top:");
			div_block = div_block.replaceAll("margin-right:", "margin-bottom:");

			for (int i = 0; i < 10; i++) {
				div_block = div_block.replaceAll("\n\n", "\n");
			}
			div_block = div_block.replaceAll("\n</p>\n<p>", "</p>\n<p>");
			div_block = div_block.replaceAll("</p>\n<p>\n", "</p>\n<p>");
			div_block = div_block.replaceAll("\n<h1>\n", "<h1>\n");
			div_block = div_block.replaceAll("\n", "");*/
//
////			div_block = div_block.replaceAll("<img", "<img style=\"background:#ffffff url('./ajax_loader.gif') no-repeat center center;width:100%;\" ");
			div_block = div_block.replaceAll("<img", "<img width=\"100%;\" ");
			
			
			// CommonAndroid.showDialog(getActivity(), div_block , null);
			String str = "";
			str += CommonAndroid.loadResouceHTMLFromAssets(getActivity(),"project_detail_header_new.html");
			str = div_block;
			str += CommonAndroid.loadResouceHTMLFromAssets(getActivity(),"project_detail_footer_new.html");
//			LogUtils.e(TAG, str);
//			Log.e("TIME", System.currentTimeMillis() + "");
//			str = "sfsadfasdfsad";
//			credit.loadDataWithBaseURL("file:///android_asset/", str, "text/html", "UTF-8", null);
			webview.loadDataWithBaseURL("file:///android_asset/", str, "text/html", "UTF-8", null);
//			String URL = "contract_term.html";
//			credit.loadUrl("file:///android_asset/contract_term/" + URL);
			credit.setWebChromeClient(new WebChromeClient() {
			});

			credit.setLongClickable(false);
			credit.setOnLongClickListener(new OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					return true;
				}
			});

			credit.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return false;
				}

				public void onPageFinished(WebView view, String url) {
//					credit.stopLoading();
//					Log.e("TIME1", "2 : " + System.currentTimeMillis() + "");
					return;
					// credit.refreshDrawableState();
					// Toast.makeText(context, "Page Load Finished",
					// Toast.LENGTH_SHORT).show();
				}

			});
			
		} catch (Exception e) {
//			Log.e("TIME3", System.currentTimeMillis() + "");
			// credit.stopLoading();
		} catch (OutOfMemoryError e) {
//			Log.e("TIME4", System.currentTimeMillis() + "");
		}
	}

	private void addToCart(){
		CommonAndroid.hiddenKeyBoard(getActivity());
		String input = edt.getText().toString().trim();
//		is_access = "0";//test
	  	
  		if("1".equals(is_access)){
  			if(!"".equals(input)){
  				HashMap<String, String> inputs = new HashMap<String, String>();
  				inputs.put("req[param][products][1][id_product]", ECFragment.addtocart_Productsid );
  				inputs.put("req[param][products][1][qty]",input /*quantity_addcart*/ );
  				LogUtils.i(TAG, "param=="+inputs.toString());
  				SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_ADDTOCART, inputs, callback);
  			}	
  			
  		}else{
  			String message = new Setting(getContext()).isLangEng() ? getActivity().getResources().getString(
					R.string.ec_addtocart_error) : getActivity().getResources().getString(
					R.string.ec_addtocart_error_j);
					/*
					Intent intent = new Intent();
			        intent.putExtra(SkyMainActivity.EXTRA_CHECK_BACK_CONTACTUS, SkyMainActivity.EXTRA_CHECK_BACK_CONTACTUS);
			        getActivity().setResult(getActivity().RESULT_OK, intent);
			        finish();*/
			        
			        Intent launcherApp = getContext().getPackageManager().getLaunchIntentForPackage("com.SkyPremiumLtd.SkyPremium");
			    	SkyMainActivity.FLAG_CHECK_OPEN_CONTACT = true;
			    	launcherApp.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			    	getActivity().startActivity(launcherApp);
///
//					ContactFragment contactFragment = new ContactFragment();
//					Log.i(TAG, "sang contact us cho a ");
//					if (getActivity() instanceof SkyMainActivity) {
//						SkyMainActivity skyActivity = (SkyMainActivity) getActivity();
//						skyActivity.switchContent(contactFragment, "");
//						Log.i(TAG, "sang contact us cho a ");
//					}
  		}
		
	}
	
	private void onClickInscription() {
//	    dismiss();
//	    ContactFragment frag = new ContactFragment();
//	    FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
//	    ft.replace(R.id.main, frag);
//	    ft.addToBackStack(null);
//	    ft.commit();
	}
	
	private void switchFragment(Fragment newFragment){
		if (getActivity() == null)
			return;

		if (getActivity() instanceof SkyMainActivity) {
			RootActivity fca = (RootActivity) getActivity();
			fca.switchContent(newFragment);
		}

	}
	
	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
		}

		public void onError(String message) {
			if (!isFinish()) {
				CommonAndroid.showView(false, loading);
				SkyUtils.showDialog(getActivity(), "" + message,null);
			}
		}

		public void onSucces(String response) {
			if (!isFinish()) {
//				updateData();
				CommonAndroid.showView(false, loading);
				
				JSONObject mainJsonObject;
				try {
					mainJsonObject = new JSONObject(response);
//					LogUtils.e(TAG,mainJsonObject.toString());
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					String err_msg = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.err_msg);
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						JSONObject jsonData = mainJsonObject.getJSONObject("data");
						if (jsonData.has("product_not_available")) {
							String msg = CommonAndroid.getString(jsonData, "product_not_available");
							SkyUtils.showDialog(getActivity(), msg, null);
							text_4.setAlpha(0.5f);
							text_4.setEnabled(false);
							flagResetEc = true;
//							mCallback.onLoad(flagResetEc);
							return;
						}else{
							LogUtils.i(TAG, "AAAAAAAAAAAA===add to card done");
							flagResetEc = false;
							text_4.setAlpha(1.0f);
							text_4.setEnabled(true);
							ECFragment.addtocart_quantityaddcart = edt.getText().toString().trim();
							ECFragment.addtocart_datarespon = response;
							RootActivity.PJ_AddToCart = new EcAddToCartDialog(getActivity(),new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							});
							RootActivity.PJ_AddToCart.show();
						}
						
					}else{
						SkyUtils.showDialog(getActivity(), err_msg,null );
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}
	};

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
	
	private void getProductDetail(){
	  		HashMap<String, String> inputs = new HashMap<String, String>();
			inputs.put("req[param][id_product]", ECFragment.addtocart_Productsid );
			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_PRODUCTDETAIL, inputs, callbackgetdetail);
	}
	
	private CheerzServiceCallBack callbackgetdetail = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
		}

		public void onError(String message) {
			if (!isFinish()) {
				CommonAndroid.showView(false, loading);
				SkyUtils.showDialog(getActivity(), "" + message,null);
//				updateData();
			}
		}

		public void onSucces(String response) {
			if (!isFinish()) {
				CommonAndroid.showView(false, loading);
				block_header.setVisibility(View.VISIBLE);
				JSONObject mainJsonObject;
				try {
					mainJsonObject = new JSONObject(response);
//					LogUtils.e(TAG,"detail=="+mainJsonObject.toString());
//					CommonAndroid.writeTextToFile(mainJsonObject.toString());
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					String err_msg = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.err_msg);
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						JSONObject data = new JSONObject(CommonAndroid.getString(mainJsonObject, "data"));
//						Log.i(TAG, "===>" + data.toString());
//						contract_type::: 1: Premium, 2: Gold
						String contract_type = new Account(getActivity()).getContractType();
						if("2".equals(contract_type)){
							is_access = CommonAndroid.getString(data, "is_access");
						}
						
						if(data.has("carrier")){
							JSONObject carrier = new JSONObject(CommonAndroid.getString(data, "carrier"));
							CommonAndroid.setText(CommonAndroid.getView(main_detail, R.id.carrier_name), CommonAndroid.getString(carrier, "name") );
						}
						if(data.has("ship_clock")){
							CommonAndroid.setText(CommonAndroid.getView(main_detail, R.id.ship_clock), CommonAndroid.getString(data, "ship_clock") );
						}
						updateData();
						updataDetail(CommonAndroid.getString(data, "description"));
//						LogUtils.e(TAG,"is_access==" + is_access);
						
						//TODO:is_favorite
						is_favorite = data.getInt("is_favorite");
						setStatusFavorite();
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}

	};
	
	private void setStatusFavorite(){
		ImageView favorite_icon = (ImageView) main_detail.findViewById(R.id.favorite_icon);
		TextView favorite_text = (TextView) main_detail.findViewById(R.id.favorite_text);
		String text_add = getActivity().getString(R.string.favorite_add);
		if (!new Setting(getActivity()).isLangEng())
			text_add = getActivity().getString(R.string.favorite_add_j);
		String text_remove = getActivity().getString(R.string.favorite_remove);
		if (!new Setting(getActivity()).isLangEng())
			text_remove = getActivity().getString(R.string.favorite_remove_j);
		LogUtils.e(TAG, "is_favorite==" + is_favorite);
		if(is_favorite == 0){
			//add
			favorite_icon.setBackgroundResource(R.drawable.favorite_on);
			favorite_text.setText(text_add);
		}else{
			//remove
			favorite_icon.setBackgroundResource(R.drawable.favorite_off);
			favorite_text.setText(text_remove);
		}
		favorite_text.setPaintFlags(favorite_text.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
	}
	
	private void updateStatus() {
		// TODO Auto-generated method stub
		LogUtils.i(TAG,"is_access==" + is_access + "::quantity==" + quantity);
		if(quantity <= 0){
			edt.setText("0");
			text_4.setAlpha(0.5f);
			text_4.setEnabled(false);
			edt.setEnabled(false);
			temp_layout_id.setAlpha(0.5f);
			temp_layout_id.setEnabled(false);
			ImageView icon_add_cart_id = (ImageView) CommonAndroid.getView(main_detail, R.id.icon_add_cart_id);
			icon_add_cart_id.setAlpha(0.5f);
			icon_add_cart_id.setEnabled(false);
		}
		if("1".equals(is_access)){
			CommonAndroid.getView(main_detail, R.id.text_validate_gold).setVisibility(View.GONE);
			CommonAndroid.getView(main_detail, R.id.icon_add_cart_id).setVisibility(View.VISIBLE);
			CommonAndroid.getView(main_detail, R.id.text_4).setVisibility(View.VISIBLE);
			CommonAndroid.getView(main_detail, R.id.text_4_validate_gold).setVisibility(View.GONE);
		}else{
			text_4.setAlpha(1.0f);
			text_4.setEnabled(true);
			temp_layout_id.setAlpha(1.0f);
			temp_layout_id.setEnabled(true);
			CommonAndroid.getView(main_detail, R.id.text_validate_gold).setVisibility(View.VISIBLE);
			CommonAndroid.getView(main_detail, R.id.icon_add_cart_id).setVisibility(View.GONE);
			CommonAndroid.getView(main_detail, R.id.text_4).setVisibility(View.GONE);
			CommonAndroid.getView(main_detail, R.id.text_4_validate_gold).setVisibility(View.VISIBLE);
		}
		
	}
}