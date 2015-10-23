package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.List;

import org.com.atmarkcafe.object.ObjectSearch;
import org.com.atmarkcafe.service.CallbackListenner;
import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.MyCartActivity;

import com.SkyPremiumLtd.SkyPremium.R;

import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.fragment.ECFragment;
import org.json.JSONArray;
import org.json.JSONException;
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
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.ProductsOfCategories;
import com.acv.cheerz.db.Setting;

public class ECSearchProductsDialog extends Base2Adialog implements android.view.View.OnClickListener {
	protected static final String TAG = "EcProductsDetailDialog";
	static RelativeLayout main_detail;
	private org.com.atmarkcafe.view.HeaderECView header;
	private GridView list;
	private String strData;
	private List<ObjectSearch> listResultSearch;
	
	CallbackListenner callbackListenner = new CallbackListenner() {
		
		@Override
		public void onLoad(boolean onload) {
			// TODO Auto-generated method stub
			
		}
	};
	public ECSearchProductsDialog(Context context,String data, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.strData = data;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		
		header.initHeader(getActivity().getResources().getString(R.string.msg_result_search),
				getActivity().getResources().getString(R.string.msg_result_search_j));
		header.visibivityLeft(true,R.drawable.header_v2_icon_back );// icon_nav_back
		header.visibivityRight(true,R.drawable.header_v2_icon_mycart );// ic_mycart
		
		list = CommonAndroid.getView(main_detail, R.id.list);
		list.setOnItemClickListener(this);
		parserData(strData);
		CommonAndroid.getView(main_detail, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.header_btn_right).setOnClickListener(this);
	}
	
	private void parserData(String data){
		listResultSearch = new ArrayList<ObjectSearch>();
		try {
			JSONObject jsonObject= new JSONObject(strData);
			JSONObject jsonData = jsonObject.getJSONObject("data");
			JSONArray jsonArray = jsonData.getJSONArray("products");
			int length = jsonArray.length();
			Log.i(TAG, "length == " + length);
			for (int i = 0; i < length; i++) {
				ObjectSearch obj = new ObjectSearch();
				JSONObject json = jsonArray.getJSONObject(i);
				obj.setContent(json);
				listResultSearch.add(obj);
			}
			Log.i(TAG, "length listResultSearch == " + listResultSearch.size());
			ResultSearchAdapter adapter = new ResultSearchAdapter(getActivity(), listResultSearch);
			list.setAdapter(adapter);
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		super.onItemClick(arg0, arg1, arg2, arg3);
		try {
			String productId = listResultSearch.get(arg2).getContent().getString("id");
			Bundle extras = new Bundle();
			extras.putString(Products.id,productId);
			ECFragment.addtocart_Productsid = productId;
			startDetail(SCREEN.PJDETAIL, extras);
		} catch (JSONException e) {
			e.printStackTrace();
		}
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
		CommonAndroid.hiddenKeyBoard(getActivity());
		if(v.getId() == R.id.header_btn_left){
			close(true);
		}else if(v.getId() == R.id.header_btn_right){
			Bundle extras = new Bundle();
			startActivityForResult(SCREEN.MYCART, extras);
		}
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
	
	private class ResultSearchAdapter extends BaseAdapter{
		private Context mContext;
		private List<ObjectSearch> listData;
		public ResultSearchAdapter(Context ctx,List<ObjectSearch> data) {
			this.mContext = ctx;
			this.listData = data;
		}
		@Override
		public int getCount() {
			
			return listData.size();
		}

		@Override
		public Object getItem(int position) {
			
			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {
			
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			View view = convertView;
			if (view == null) {
				holder = new Holder();
				LayoutInflater inf = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = inf.inflate(R.layout.ec_product_item, null);
				holder.txtView1 = (TextView)view.findViewById(R.id.text_1);
				holder.txtView2 = (TextView)view.findViewById(R.id.text_2);
				holder.txtView3 = (TextView)view.findViewById(R.id.text_3);
				holder.imgView = (ImageView)view.findViewById(R.id.img);
				view.setTag(holder);
			}else{
				holder = (Holder)view.getTag();
			}
			
			Setting setting = new Setting(mContext);
			ObjectSearch obj = listData.get(position);
			String basePrice = "";
			String productName = "";
			String urlThumb = "";
			try {
				basePrice = obj.getContent().getString("base_price");
				productName = obj.getContent().getString("name");
				urlThumb = obj.getContent().getString("thumbnail");
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			String price = String.format("%s %s %s"//
					, setting.isLangEng() ? getActivity().getString(R.string.ec_price_unit) : getActivity().getString(R.string.ec_price_unit_j)//
					, CommonAndroid.DecimalFormatPrice(basePrice)//
					, setting.isLangEng() ? getActivity().getString(R.string.ec_taxincl) : getActivity().getString(R.string.ec_taxincl_j)//
					);

			holder.txtView1.setText(productName);
			holder.txtView2.setText(price);
			String status_text = "";
			String status_text_in = " - " + (setting.isLangEng() ? getActivity().getString(R.string.ec_in_stock) : getActivity().getString(R.string.ec_in_stock_j));
			String status_text_out = " - " + (setting.isLangEng() ? getActivity().getString(R.string.ec_out_stock) : getActivity().getString(R.string.ec_out_stock_j));
			int status_text_tem = 0;
			try {
				
				status_text_tem = Integer.parseInt(obj.getContent().getString("quantity"));
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
			
			holder.txtView3.setTextColor(status_text_color);
			holder.txtView3.setText(status_text);
			
			ImageLoaderUtils.getInstance(mContext).displayImageEcProduct(urlThumb, holder.imgView);
			return view;
		}
		
	}
	
	private class Holder{
		TextView txtView1;
		TextView txtView2;
		TextView txtView3;
		ImageView imgView;
	}
}