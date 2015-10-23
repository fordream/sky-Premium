package org.com.atmarkcafe.sky.dialog;

import org.com.atmarkcafe.sky.fragment.ProfileFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.BaseAdialog;
import z.lib.base.BaseItem;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyUtils;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

public class ContractDialog extends BaseAdialog implements android.view.View.OnClickListener {
	private static final String TAG = "ContractDialog";
	BaseItem baseItem;
	RelativeLayout main_detail;
	private LoadingView loading;
	String contract_type = "0";
	private org.com.atmarkcafe.view.HeaderView header;
	public ContractDialog(Context context,String contracttype, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		if(!"".equals(contracttype)){
			this.contract_type = contracttype;
		}
		LogUtils.e(TAG, "contract_type=" +contract_type);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.contract_main);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		CommonAndroid.showView(false, loading);
		openPopActivity(findViewById(R.id.contract_main));
		header = CommonAndroid.getView(main_detail, R.id.header);
		initHeader();
		header.visibivityLeft(false, R.drawable.icon_nav_back);
		header.visibivityRight(false, R.drawable.icon_edit);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		onLoad();
	}

	private void onLoad() {
		addView();
//		CommonAndroid.getView(main_detail, R.id.button_submit).setOnClickListener(this);
		
	}

	protected void addView() {
		// TODO Auto-generated method stub
		try {
			JSONObject data = baseItem.getObject();
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_first_name_v)).setText(data.getString("first_name"));
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void updateContract() {
		// TODO Auto-generated method stub
		try {
//			HashMap<String, String> inputs = new HashMap<String, String>();
//			inputs.put("req[param][mail]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_email_v)).getText().toString().trim() );
//			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_PROFILEUPDATE, inputs, callbackupdate);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private CheerzServiceCallBack callbackupdate = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
			CommonAndroid.hiddenKeyBoard(getActivity());
		};

		public void onError(String message) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				String message_alert = "";
				String input = message;
				JSONArray jsonArray;
				try {
					jsonArray = new JSONArray(input);
					for (int i = 0; i < jsonArray.length(); i++) {
						message_alert+= jsonArray.getString(i) + "\n";
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(!"".equals(message_alert)){
					SkyUtils.showDialog(getActivity(), "" + message_alert,null);
				}
				
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
						if(new Setting(getActivity()).isLangEng()){
							SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.msg_success) ,null);
						}else{
							SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.msg_success_j) ,null);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};
	
	private void initHeader() {
		try {
			int contracttype = Integer.parseInt(contract_type);
			switch (contracttype) {
			case 0:
				header.initHeader(R.string.profile_contract, R.string.profile_contract_j);
				break;
			default:
				header.initHeader(R.string.profile_contract, R.string.profile_contract_j);
				break;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.contract_main), new AnimationAction() {

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
		return R.layout.user_contract_update;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.header_btn_left){
			close(true);
		} else if(v.getId() == R.id.profile_date_of_birth_v){
			
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