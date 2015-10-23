package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.fragment.ProfileFragment;
import org.com.atmarkcafe.view.ProfileRecommentItemView;
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
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.acv.cheerz.base.view.LoadingView;

public class ProfileRecommentDialog extends Base2Adialog implements android.view.View.OnClickListener {
	protected static final String TAG = "ProfileRecommentDialog";
	static RelativeLayout main_detail;
	private static LoadingView loading;
	private org.com.atmarkcafe.view.HeaderView header;
	private static BaseAdapter baseAdapter;
	static List<BaseItem> baseItemsCreditMain = new ArrayList<BaseItem>();
	public static HashMap<String, String> inputs_new = new HashMap<String, String>();
	public ProfileRecommentDialog(Context context,OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.profile, R.string.profile_j);
		header.visibivityLeft(false, R.drawable.icon_nav_back);
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		main_detail = (RelativeLayout) main_detail.findViewById(R.id.main_detail);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		onLoad();
	}
	
	private void onLoad() {
		// TODO Auto-generated method stub
		HashMap<String, String> inputs = new HashMap<String, String>();
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_LISTRECOMMENT, inputs, callback);
	}

	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {

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
					LogUtils.e(TAG, "dataUPDATE==" + mainJsonObject.toString());
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					String err_msg = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.err_msg);
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						baseItemsCreditMain.clear();
						JSONArray array = null;
						try {
							array = mainJsonObject.getJSONArray("data");
							for (int i = 0; i < array.length() ; i++) {
								baseItemsCreditMain.add(new BaseItem(array.getJSONObject(i)));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(array != null && array.length() > 0){
							addView();
						}
						
					}else{
						SkyUtils.showDialog(getActivity(), "" + err_msg,null);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		}

};

	public static void addView() {
		// TODO Auto-generated method stub
		ListView listview = (ListView) main_detail.findViewById(R.id.listview);
		baseAdapter = new BaseAdapter(getActivity(), new ArrayList<Object>()) {
			@Override
			public BaseView getView(Context context, Object data) {
				final ProfileRecommentItemView profileRecommentItemView = new ProfileRecommentItemView(context);
				return profileRecommentItemView;
			}
		};
		baseAdapter.clear();
		baseAdapter.addAllItems(baseItemsCreditMain);
		baseAdapter.notifyDataSetChanged();
		listview.setOnItemClickListener(new OnItemClickListener() {
	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BaseItem baseItem = (BaseItem) parent.getItemAtPosition(position);
//				finishEC(baseItem.getString("id_card"));
			}
			
		});
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
				dismiss();
				if (isOnClick) {
					clickListener.onClick(null, 0);
				}
			}
		});
	}

	@Override
	public int getLayout() {
		return R.layout.user_recomment;
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
	
	
	private static FragmentActivity getActivity() {
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