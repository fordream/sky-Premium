package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.view.ECBlogView;
import org.com.atmarkcafe.view.PurchaseHistoryItemView;
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
import z.lib.base.SkyUtils.SCREEN;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;

public class MyAccountOrderHistoryDialog extends Base2Adialog implements android.view.View.OnClickListener {
	View views;
	RelativeLayout main_detail;
	protected static final String TAG = "MyAccountOrderHistoryDialog";
	private BaseAdapter baseAdapter;
	List<BaseItem> baseItems = new ArrayList<BaseItem>();
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	LinearLayout content_main;;
	ScrollView detail;
	FragmentActivity activity;
	public MyAccountOrderHistoryDialog(FragmentActivity context, OnClickListener clickListener) {
		super(context, clickListener);
		this.activity = context;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	private static CheerzServiceCallBack callback;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.mya_history, R.string.mya_history_j);
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);//icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		content_main = CommonAndroid.getView(main_detail, R.id.content_main);
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
						LogUtils.i(TAG, "data==" + mainJsonObject.toString());
						String is_succes = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.is_success);
						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							baseItems.clear();
							try {
								JSONArray array = mainJsonObject.getJSONArray("data");
								for (int i = 0; i < array.length() ; i++) {
									baseItems.add(new BaseItem(array.getJSONObject(i)));
								}
							} catch (JSONException e) {
								try {
									if(mainJsonObject.getJSONObject("data").has("message")){
										SkyUtils.showDialog(getActivity(), "" + mainJsonObject.getJSONObject("data").getString("message"),null);
									}
								} catch (Exception e1) {
								}
							}
							addView(baseItems);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}

		
		};
		onLoadData();
	}
	
	private void addView(List<BaseItem> baseItemsAddbook) {
		// TODO Auto-generated method stub
		ListView listview = (ListView) main_detail.findViewById(R.id.list_content);
		baseAdapter = new BaseAdapter(getActivity(), new ArrayList<Object>()) {
			@Override
			public BaseView getView(Context context, Object data) {
				final PurchaseHistoryItemView Itemview = new PurchaseHistoryItemView(context);
				Itemview.addFragment(MyAccountOrderHistoryDialog.this);
				return Itemview;
			}
		};
		baseAdapter.clear();
		baseAdapter.addAllItems(baseItemsAddbook);
		baseAdapter.notifyDataSetChanged();
		/*listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BaseItem baseItem = (BaseItem) parent.getItemAtPosition(position);
				new MyAccountOrderHistoryDetailDialog(getActivity() , baseItem.getString("id"), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).show();
				close(true);
			}
		});*/
		listview.setAdapter(baseAdapter);
	};
	
	
	private void onLoadData() {
		// TODO Auto-generated method stub
		HashMap<String, String> inputs = new HashMap<String, String>();
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_MYACCOUNT_HISTORY, inputs, callback);
	}

	@Override
	public void onBackPressed(){
		close(true);
	}
	
	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.main_detail), new AnimationAction() {

			@Override
			public void onAnimationEnd() {
				ECBlogView.setautoslide = true;
				dismiss();
				if (isOnClick) {
					clickListener.onClick(null, 0);
				}
			}
		});
	}

	@Override
	public int getLayout() {
		return R.layout.v2_myaccount_orderhistory_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			close(true);
			break;
		default:
			break;
		}
	}
	
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

	public void detail(BaseItem data) {
		// TODO Auto-generated method stub
		RootActivity.PJ_HistoryDetailDialog = new MyAccountOrderHistoryDetailDialog(getActivity() , data.getString("id"), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		RootActivity.PJ_HistoryDetailDialog.show();
	}

	public void reorder(BaseItem data) {
		// TODO Auto-generated method stub
		try {
			JSONObject objParam = new JSONObject();//root key: param
			objParam.put("id_order", data.getString("id"));
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
//						onLoadData();
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