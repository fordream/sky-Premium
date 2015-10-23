package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;
import org.com.atmarkcafe.view.EcCreditItemView;
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
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

public class EcCreditDialog extends Base2Adialog implements android.view.View.OnClickListener {
	protected static final String TAG = "EcCreditDialog";
	static RelativeLayout main_detail;
	private static LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	private static BaseAdapter baseAdapter;
	static List<BaseItem> baseItemsCreditMain = new ArrayList<BaseItem>();
	static List<BaseItem> baseItemsCredit = new ArrayList<BaseItem>();
	public static HashMap<String, String> inputs_new = new HashMap<String, String>();
	static EcCreditDialog credit = null;
	static ListView listview;
	public EcCreditDialog(Context context,OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		credit = this;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.ec_credit_header, R.string.ec_credit_header_j);//ec_h
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);// icon_nav_back
		header.visibivityRight(true, R.drawable.header_v2_icon_add);// ec_credit_icon_new
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(header, R.id.header_btn_right).setOnClickListener(this);
		main_detail = (RelativeLayout) main_detail.findViewById(R.id.main_detail);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		CommonAndroid.getView(main_detail, R.id.mya_credit_add).setOnClickListener(this);
		onLoad();
	}
	
	private void onLoad() {
		// TODO Auto-generated method stub
		HashMap<String, String> inputs = new HashMap<String, String>();
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_CREDIT, inputs, callback);
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
						baseItemsCredit.clear();
						baseItemsCreditMain.clear();
						JSONArray array = null;
						try {
							array = mainJsonObject.getJSONArray("data");
							for (int i = array.length() - 1; i >= 0 ; i--) {
								baseItemsCredit.add(new BaseItem(array.getJSONObject(i)));
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						if(array != null && array.length() > 0){
							addView(null);
						}else{
							new EcCreditNewDialog(getActivity() ,false ,null, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							}).show();
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

	public static void addView(String newItem) {
		// TODO Auto-generated method stub
		if(newItem != null){
			JSONObject newJsonObject;
			try {
				newJsonObject = new JSONObject(newItem);
				baseItemsCreditMain.add(new BaseItem(newJsonObject));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			baseItemsCreditMain.addAll(baseItemsCredit);
		}
		LogUtils.e(TAG, "dataView==" + baseItemsCreditMain.toString());
		listview = (ListView) main_detail.findViewById(R.id.listview_credit);
		baseAdapter = new BaseAdapter(getActivity(), new ArrayList<Object>()) {
			@Override
			public BaseView getView(Context context, Object data) {
				final EcCreditItemView ecCreditItemView = new EcCreditItemView(context);
				ecCreditItemView.addFragment(credit);
				return ecCreditItemView;
			}
		};
		baseAdapter.clear();
		baseAdapter.addAllItems(baseItemsCreditMain);
		baseAdapter.notifyDataSetChanged();
		/*listview.setOnItemClickListener(new OnItemClickListener() {
	
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
			}
			
		});*/
		listview.setAdapter(baseAdapter);
	};
	
	public void selectCredit(BaseItem data) {
//		finishEC(baseItem.getString("id_card"));
		String is_expired = data.getString("is_expired");
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][id_card]", 		data.getString("id_card") );
		inputs.put("req[param][holder_name]", 	data.getString("holder_name") );
		inputs.put("req[param][number]", 		data.getString("number") );
		inputs.put("req[param][security_code]", data.getString("security_code") );
		inputs.put("req[param][expiry_date]", 	data.getString("expiry_date") );
		inputs.put("req[param][phone]", 		data.getString("phone") );
		inputs.put("req[param][email]", 		data.getString("email") );
		if(!"1".equals(is_expired)){
			confirmCredit(inputs);
		}else{
			String err_msg = getActivity().getString(R.string.ec_credit_expired);
			if(!new Setting(getActivity()).isLangEng())
				err_msg = getActivity().getString(R.string.ec_credit_expired_j);
			SkyUtils.showDialog(getActivity(), err_msg ,null);
		}
		
	}

	protected static void confirmCredit(HashMap<String, String> inputs) {
		
		// TODO Auto-generated method stub
		MyCartActivity.EC_CREDIT_CONFIRM = new EcCreditConfirmDialog(getActivity() ,inputs ,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		MyCartActivity.EC_CREDIT_CONFIRM.show();
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
		return R.layout.ec_credit_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			close(true);
			break;
		case R.id.header_btn_right:
			new EcCreditNewDialog(getActivity() , true , null, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			break;
		case R.id.mya_credit_add:
			new EcCreditNewDialog(getActivity() , true , null, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			}).show();
			break;
		default:
			break;
		}
	}
	
	
	private static FragmentActivity getActivity() {
		return MyCart2Fragment.activity;
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
	
	public static void showView(Boolean show){
		LinearLayout footer = CommonAndroid.getView(main_detail, R.id.footer);
		footer.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
		listview.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
	}
}