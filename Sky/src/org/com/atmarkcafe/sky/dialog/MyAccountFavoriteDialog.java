package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.service.CallbackListenner;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.fragment.ECFragment;
import org.com.atmarkcafe.view.ECBlogView;
import org.com.atmarkcafe.view.FavoriteItemView;
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
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
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

public class MyAccountFavoriteDialog extends Base2Adialog implements android.view.View.OnClickListener {
	View views;
	RelativeLayout main_detail;
	protected static final String TAG = "MyAccountFavoriteDialog";
	private BaseAdapter baseAdapter;
	List<BaseItem> baseItems = new ArrayList<BaseItem>();
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	LinearLayout content_main;;
	ScrollView detail;
	FragmentActivity activity;
	
	CallbackListenner callbackListenner = new CallbackListenner() {
		
		@Override
		public void onLoad(boolean onload) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public MyAccountFavoriteDialog(FragmentActivity context, OnClickListener clickListener) {
		super(context, clickListener);
		this.activity = context;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.favorite_list, R.string.favorite_list_j);
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);//icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		content_main = CommonAndroid.getView(main_detail, R.id.content_main);
		onloadData();
	}
	
	
	private void onloadData() {
		// TODO Auto-generated method stub

		HashMap<String, String> inputs = new HashMap<String, String>();
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_MYACCOUNT_FAVORITE, inputs, new CheerzServiceCallBack() {

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
//						CommonAndroid.writeTextToFile("list favorite===" +mainJsonObject.toString());
						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							baseItems.clear();
							try {
								JSONArray array = mainJsonObject.getJSONArray("data");
								for (int i = array.length() - 1; i >= 0  ; i--) {
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

		});
	
	}

	private void addView(List<BaseItem> baseItemsAddbook) {
		// TODO Auto-generated method stub
		ListView listview = (ListView) main_detail.findViewById(R.id.list_content);
		baseAdapter = new BaseAdapter(getActivity(), new ArrayList<Object>()) {
			@Override
			public BaseView getView(Context context, Object data) {
				final FavoriteItemView Itemview = new FavoriteItemView(context);
				Itemview.addFragment(MyAccountFavoriteDialog.this);
				return Itemview;
			}
		};
		baseAdapter.clear();
		baseAdapter.addAllItems(baseItemsAddbook);
		baseAdapter.notifyDataSetChanged();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				BaseItem baseItem = (BaseItem) parent.getItemAtPosition(position);
				ECFragment.addtocart_Productsid = baseItem.getString("id");
				startDetail();
			}
		});
		listview.setAdapter(baseAdapter);
	};
	
	public void startDetail() {
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
		return R.layout.v2_myaccount_favorite_layout;
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

	String id_remove = "";
	public void removeFavorite(final BaseItem data) {
		// TODO Auto-generated method stub
		String[] items;
		String title = getActivity().getResources().getString(R.string.favorite_delete_confirm);
		items = new String[] { getActivity().getResources().getString(R.string.msg_no),getActivity().getResources().getString(R.string.msg_yes) };
		if (!new Setting(getActivity()).isLangEng()) {
			title = getActivity().getResources().getString(R.string.favorite_delete_confirm_j);
			items = new String[] { getActivity().getResources().getString(R.string.msg_no_j) ,getActivity().getResources().getString(R.string.msg_yes_j) };
		}
		new ProfileChangeOptionDialog(getActivity(), items, title, new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 1) {
					// yes-->delete
					deleteProduct(data);
				} else if (which == 0) {
					// no
				}

			}
		}).show();

	}

	protected void deleteProduct(BaseItem data) {
		// TODO Auto-generated method stub
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][id_product]", data.getString("id") );
		id_remove =  data.getString("id");
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_MYACCOUNT_FAVORITE_DEL, inputs, new CheerzServiceCallBack() {

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
							for(int i = 0; i < baseItems.size(); i++){
								String id_check = baseItems.get(i).getString("id");
								if(id_check.equals(id_remove)){
									baseItems.remove(i);
								}
							}
							addView(baseItems);
							baseAdapter.notifyDataSetChanged();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					
				}
			}

		});
	}
	
}