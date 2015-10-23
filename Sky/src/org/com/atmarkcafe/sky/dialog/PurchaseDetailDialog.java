package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.fragment.ProfileFragment;
import org.com.atmarkcafe.view.PurchaseDetailItemView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.BaseAdapter;
import z.lib.base.BaseAdialog;
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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.acv.cheerz.base.view.LoadingView;

public class PurchaseDetailDialog extends BaseAdialog implements android.view.View.OnClickListener {
	private static final String TAG = "PurchaseDetailDialog";
	BaseItem baseItem;
	RelativeLayout main_detail;
	String order_id;
	private BaseAdapter baseAdapter;
	List<BaseItem> baseItemsPurchase = new ArrayList<BaseItem>();
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderView header;
	public PurchaseDetailDialog(Context context,String orderid, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.order_id = orderid;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initHeader();
		main_detail = (RelativeLayout) findViewById(R.id.main_purchase_detail);
		openPopActivity(findViewById(R.id.main_purchase_detail));
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.profile_purchased, R.string.profile_purchased_j);
		header.visibivityLeft(false, R.drawable.icon_nav_back);
		header.visibivityRight(false, R.drawable.icon_edit);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		onLoad();
	}

	private void onLoad() {
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][order_id]", order_id);
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_PURCHASEDETAIL, inputs, callbackdetail);
	}

	protected void addView(List<BaseItem> baseItems2) {
		ListView listview = (ListView) main_detail.findViewById(R.id.listview_purchase_detail);
		View header2 = getActivity().getLayoutInflater().inflate(R.layout.user_purchase_detail_header, null);
		listview.addHeaderView(header2);
        baseAdapter = new BaseAdapter(getActivity(), new ArrayList<Object>()) {
			@Override
			public BaseView getView(Context context, Object data) {
				final PurchaseDetailItemView purchaseDetailItemView = new PurchaseDetailItemView(context);
				return purchaseDetailItemView;
			}
		};
		baseAdapter.clear();
		baseAdapter.addAllItems(baseItems2);
		baseAdapter.notifyDataSetChanged();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BaseItem baseItem = (BaseItem) parent.getItemAtPosition(position);
				LogUtils.e(TAG,baseItem.toString());
				
			}
		});
		listview.setAdapter(baseAdapter);
	}
	
	private CheerzServiceCallBack callbackdetail = new CheerzServiceCallBack() {

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
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					String message = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.err_msg);
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						LogUtils.e(TAG, mainJsonObject.toString());
						baseItemsPurchase.clear();
						try {
							JSONArray array = mainJsonObject.getJSONArray("data");
							for (int i = 0; i < array.length() ; i++) {
								array.getJSONObject(i).put("no", i+1);
								baseItemsPurchase.add(new BaseItem(array.getJSONObject(i)));
								LogUtils.e(TAG,"purchase_detail" + array.getJSONObject(i).toString());
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						addView(baseItemsPurchase);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};
	
	private void initHeader() {
		
	}

	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.main_purchase_detail), new AnimationAction() {

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
		return R.layout.user_purchase_detail;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.header_btn_left){
			close(true);
		}
	}

	private FragmentActivity getActivity() {
		return ProfileFragment.activity;
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