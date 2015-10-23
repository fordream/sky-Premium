package org.com.atmarkcafe.sky.dialog;

import org.com.atmarkcafe.sky.MyCartActivity;
import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.fragment.ECFragment;
import org.json.JSONObject;

import z.lib.base.Base2Adialog;
import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderUtils;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyLoader;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.SCREEN;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ImageView.ScaleType;

import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.Setting;

public class EcAddToCartDialog extends Base2Adialog implements android.view.View.OnClickListener {
	protected static final String TAG = "EcCreditDialog";
	static RelativeLayout main_detail;
	private org.com.atmarkcafe.view.HeaderECView header;
	private ImageView flip;
	String quantity_addcart ="";
	String Products_id = "";
	String data_respon = "";
	private String msgAvailable;
	private boolean isAvailable = true;
	
	public EcAddToCartDialog(Context context, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		quantity_addcart = ECFragment.addtocart_quantityaddcart;
		Products_id = ECFragment.addtocart_Productsid;
		data_respon = ECFragment.addtocart_datarespon;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_dot_addcart, getActivity().getResources().getColor(R.color.ec_addtocard_dot));//OK
//		CommonAndroid.changeColorImageView(getActivity(), R.drawable.btn_checkout, getActivity().getResources().getColor(R.color.ec_btn_checkout));//OK
//		CommonAndroid.changeColorImageView(getActivity(), R.drawable.btn_continue, getActivity().getResources().getColor(R.color.ec_btn_continue));//OK
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.add_to_cart, R.string.add_to_cart_j);//ec_h
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);// icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.text_4).setOnClickListener(this);
		CommonAndroid.setText(CommonAndroid.getView(main_detail, R.id.text_1), "");
		CommonAndroid.setText(CommonAndroid.getView(main_detail, R.id.text_2), "");
		CommonAndroid.setText(CommonAndroid.getView(main_detail, R.id.text_3), "");
		CommonAndroid.setText(CommonAndroid.getView(main_detail, R.id.edt_1), quantity_addcart);
		CommonAndroid.getView(main_detail, R.id.bt_continue).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.bt_checkout).setOnClickListener(this);
//		loading = CommonAndroid.getView(view, R.id.loading);
//		CommonAndroid.showView(false, loading);
		flip = (ImageView) CommonAndroid.getView(main_detail, R.id.flip);
		updateData();
	}
	
	private void updateData() {
		loadSkypeLoader(new SkyLoader() {
			Cursor cursorProduct;
//			Cursor cursorGallery;

			@Override
			public void loadSucess(Object data) {
				Cursor cursor = cursorProduct;
				if (cursor != null && cursor.moveToFirst()) {
					LogUtils.e(TAG, "cursorProduct==done");
					CommonAndroid.setText((TextView) CommonAndroid.getView(main_detail, R.id.text_1), cursor, Products.name);
					CommonAndroid.setText((TextView) CommonAndroid.getView(main_detail, R.id.text_2),  CommonAndroid.formatPriceEC(getActivity(), CommonAndroid.getString(cursor, Products.base_price)) );
					int total = 0;
					try {
						total = Integer.parseInt(quantity_addcart) * Integer.parseInt( CommonAndroid.getString(cursor, Products.base_price) );
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					((TextView) CommonAndroid.getView(main_detail, R.id.text_5)).setText( CommonAndroid.formatPriceEC(getActivity(), total + ""));
//					CommonAndroid.setText( (TextView) CommonAndroid.getView(getView(), R.id.text_1), total);
//					CommonAndroid.setText((TextView) CommonAndroid.getView(getView(), R.id.text_3), cursor, Products.description);
					
					JSONObject mainJsonObject;
					try {
						mainJsonObject = new JSONObject(data_respon);
						LogUtils.e(TAG,mainJsonObject.toString());
						String is_succes = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.is_success);
						String err_msg = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.err_msg);
						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							JSONObject product_info =  mainJsonObject.getJSONObject("data").getJSONObject("product_info");
							String url_thumbnail = CommonAndroid.getString(product_info, "thumbnail");
//							flip.setScaleType(ScaleType.FIT_CENTER);
							ImageLoaderUtils.getInstance(getActivity()).displayImageMyCart( url_thumbnail , flip );
							
							JSONObject cart_summary =  mainJsonObject.getJSONObject("data").getJSONObject("cart_summary");
							String ec_mycart_add_totalcart = String.format( new Setting( getActivity()).isLangEng() ? getActivity().getString(R.string.ec_mycart_add_totalcart) : getActivity().getString(R.string.ec_mycart_add_totalcart_j), CommonAndroid.getString(cart_summary, "qty") );
							((TextView) CommonAndroid.getView(main_detail, R.id.ec_mycart_add_totalcart)).setText( ec_mycart_add_totalcart );
							String ship =  mainJsonObject.getJSONObject("data").getString("shipping_cost");
							int total_and_ship = Integer.parseInt(CommonAndroid.getString(cart_summary, "total")) /*- Integer.parseInt(ship)*/;
							((TextView) CommonAndroid.getView(main_detail, R.id.ec_mycart_add_totaltax)).setText( CommonAndroid.formatPriceEC(getActivity(), total_and_ship + "" ));
							
//							((TextView) CommonAndroid.getView(main_detail, R.id.ec_mycart_add_ship)).setText( CommonAndroid.formatPriceEC(getActivity(), ship ) ); 
							
//							int ec_mycart_add_tax = Integer.parseInt(CommonAndroid.getString(cart_summary, "total")) - Integer.parseInt(CommonAndroid.getString(cart_summary, "total_tax_excluded"));
							String tax =  mainJsonObject.getJSONObject("data").getString("shipping_cost");
							((TextView) CommonAndroid.getView(main_detail, R.id.ec_mycart_add_tax)).setText( CommonAndroid.formatPriceEC( getActivity(), tax )  );
							int total_and_ship_fee = total_and_ship + Integer.parseInt(ship);
//							((TextView) CommonAndroid.getView(main_detail, R.id.ec_mycart_add_total)).setText( CommonAndroid.formatPriceEC(getActivity(), CommonAndroid.getString(cart_summary, "total")));
							((TextView) CommonAndroid.getView(main_detail, R.id.ec_mycart_add_total)).setText( CommonAndroid.formatPriceEC(getActivity(), String.valueOf(total_and_ship_fee)));
						}else{
							SkyUtils.showDialog(getActivity(), err_msg , null);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					cursor.close();
				}

			}

			@Override
			public Object loadData() {
				if (getActivity() == null) {
					return null;
				}

				String where = String.format("%s = '%s' and %s = '%s'"//
						, Products.user_id, new Account(getActivity()).getUserId()//
						, Products.id, Products_id//
						);
				LogUtils.e(TAG, "Products_id2==" + Products_id);
				cursorProduct = new Products(getActivity()).querry(where);
				return null;
			}
		}, 500);
	}
	
	
	private void loadSkypeLoader(SkyLoader skyLoader, int i) {
		// TODO Auto-generated method stub
		skyLoader.executeLoader(i);
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
		return R.layout.ec_addtocart_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_checkout:
//			startFragment(new MyCart2Fragment(), null);
			Bundle extras = new Bundle();
			startActivityForResult(SCREEN.MYCART, extras);
			break;
		case R.id.bt_continue:
			RootActivity.goTOEC();
			break;
		case R.id.header_btn_left:
			close(true);
			break;	
		default:
			break;
		}
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
	

}