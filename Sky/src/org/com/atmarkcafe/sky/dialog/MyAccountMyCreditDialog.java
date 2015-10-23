package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.view.ECBlogView;
import org.com.atmarkcafe.view.EcCreditItemView2;
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
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import com.acv.cheerz.db.Setting;

public class MyAccountMyCreditDialog extends Base2Adialog implements android.view.View.OnClickListener {
	View views;
	static RelativeLayout main_detail;
	protected static final String TAG = "MyAccountMyCreditDialog";
	private BaseAdapter baseAdapter;
	List<BaseItem> baseItems = new ArrayList<BaseItem>();
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	LinearLayout content_main;;
	ScrollView detail;
	static FragmentActivity activity;
	static private  CheerzServiceCallBack callback;
	MyAccountMyCreditDialog mydialog;
	static ListView listview;
	public MyAccountMyCreditDialog(FragmentActivity context, OnClickListener clickListener) {
		super(context, clickListener);
		this.activity = context;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mydialog = this;
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.ec_credit_header, R.string.ec_credit_header_j);
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);//icon_nav_back
		header.visibivityRight(true, R.drawable.header_v2_icon_add);//ec_credit_icon_new
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(header, R.id.header_btn_right).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.mya_credit_add).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		content_main = CommonAndroid.getView(main_detail, R.id.content_main);
		View header_view = getLayoutInflater().inflate(R.layout.v2_myaccount_mycredit_header_layout, null);
		listview = (ListView) main_detail.findViewById(R.id.list_content);
		listview.addHeaderView(header_view);
		
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
						LogUtils.e(TAG, "LIST CREDIT==" + mainJsonObject.toString());
						String is_succes = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.is_success);
						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							baseItems.clear();
							try {
								JSONArray array = mainJsonObject.getJSONArray("data");
								for (int i = array.length()- 1; i >= 0 ; i--) {
									baseItems.add(new BaseItem(array.getJSONObject(i)));
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
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
	
	public static void onLoadData() {
		// TODO Auto-generated method stub
		HashMap<String, String> inputs = new HashMap<String, String>();
		SkyUtils.execute(activity, RequestMethod.GET, API.API_MYACCOUNT_CARD, inputs, callback);
	}
	
	private void addView(List<BaseItem> baseItems_) {
		// TODO Auto-generated method stub
		baseAdapter = new BaseAdapter(getActivity(), new ArrayList<Object>()) {
			@Override
			public BaseView getView(Context context, Object data) {
				final EcCreditItemView2 Itemview = new EcCreditItemView2(context);
				Itemview.addFragment(mydialog);
				return Itemview;
			}
		};
		baseAdapter.clear();
		baseAdapter.addAllItems(baseItems_);
		baseAdapter.notifyDataSetChanged();
//		listview.setOnItemClickListener(new OnItemClickListener() {
//
//			@Override
//			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				BaseItem baseItem = (BaseItem) parent.getItemAtPosition(position);
//
//			}
//		});
		listview.setAdapter(baseAdapter);
	};
	
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
		return R.layout.v2_myaccount_mycredit_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			close(true);
			break;
		case R.id.header_btn_right:
			new MyAccountCreditNewDialog(getActivity() , null, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			break;	
		case R.id.mya_credit_add:
			new MyAccountCreditNewDialog(getActivity() , null, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
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
	
	public void update(BaseItem baseItem){
		new MyAccountCreditNewDialog(getActivity() , baseItem, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();
	}

	public void delete(BaseItem data) {
		// TODO Auto-generated method stub
		final String id_card = data.getString("id_card");
		String[] items;
		String title = getActivity().getResources().getString(R.string.mya_credit_delete_confirm);
		items = new String[] { getActivity().getResources().getString(R.string.msg_no),getActivity().getResources().getString(R.string.msg_yes) };
		if (!new Setting(getActivity()).isLangEng()) {
			title = getActivity().getResources().getString(R.string.mya_credit_delete_confirm_j);
			items = new String[] { getActivity().getResources().getString(R.string.msg_no_j) ,getActivity().getResources().getString(R.string.msg_yes_j) };
		}
		new ProfileChangeOptionDialog(getActivity(), items, title, new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 1) {
					// yes-->delete
					HashMap<String, String> inputs = new HashMap<String, String>();
					inputs.put("req[param][id_card]", id_card );
					SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_MYACCOUNT_CARD_DEL, inputs, callbackdelete);
				} else if (which == 0) {
					// no
				}

			}
		}).show();
	}
	
	private CheerzServiceCallBack callbackdelete = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
			CommonAndroid.hiddenKeyBoard(getActivity());
		};

		public void onError(String message) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				SkyUtils.showDialog(getActivity(), SkyUtils.convertStringErrF(message),null);
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
					LogUtils.e(TAG, "update==" + mainJsonObject.toString());
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						String message_done = (new Setting(getActivity()).isLangEng()) ? getActivity().getString(R.string.delete_credit_done) : getActivity().getString(R.string.delete_credit_done_j);
						SkyUtils.showDialog(getActivity(), message_done , new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								onLoadData();
							}
						});
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};	
	
	public static void showView(Boolean show){
		LinearLayout footer = CommonAndroid.getView(main_detail, R.id.footer);
		footer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		listview.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}
}