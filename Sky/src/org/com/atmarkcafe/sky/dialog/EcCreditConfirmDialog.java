package org.com.atmarkcafe.sky.dialog;

import java.util.HashMap;

import com.SkyPremiumLtd.SkyPremium.R;

import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;
import org.json.JSONObject;

import z.lib.base.Base2Adialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Products;

public class EcCreditConfirmDialog extends Base2Adialog implements android.view.View.OnClickListener {
	RelativeLayout main_detail;
	protected static final String TAG = "EcCreditConfirmDialog";
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	HashMap<String, String> inputs = new HashMap<String, String>();
	private String id_card;
	private Boolean onTouch = false;
	public EcCreditConfirmDialog(Context context,HashMap<String, String> input, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.inputs = input;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.ec_credit_confirm_header, R.string.ec_credit_confirm_header_j);//ec_d
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);// icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(header, R.id.header_btn_right).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.ec_credit_bt_save).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		addView();
		onTouch = false;
	}
	
	private void addView() {
		// TODO Auto-generated method stub
		((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_name)).setText( inputs.get("req[param][holder_name]") );
		((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_cardnum)).setText( inputs.get("req[param][number]") );
		((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_code)).setText( inputs.get("req[param][security_code]") );
		((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired)).setText( inputs.get("req[param][expiry_date]") );
		((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_phone)).setText( inputs.get("req[param][phone]") );
		((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_email)).setText( inputs.get("req[param][email]") );
		String ec_credit_confirm_amount = Products.ec_credit_confirm_amount;
		id_card = inputs.get("req[param][id_card]");
		((TextView) CommonAndroid.getView(main_detail, R.id.ec_credit_confirm_amount)).setText( ec_credit_confirm_amount );
	}

	@Override
	public void onBackPressed(){
		close(true);
		/*new EcCreditNewDialog(getActivity() , EcUpdateCreditFragment.inputs_new, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();*/
	}
	
	private void finishEC() {
		// TODO Auto-generated method stub
//		startFragment(new ECProductsFragment(), null);
		/*HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][id_card]", id_card);
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_CONFIRM, input s, callbackfinish);*/
		
		try {
			JSONObject objParam = new JSONObject();//root key: param
			objParam.put("id_card", id_card);
			SkyUtils.execute(getActivity(), RequestMethod.POST, API.API_EC_CONFIRM, SkyUtils.paramRequest(getActivity(), objParam) , callbackfinish);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*private void update() {
		// TODO Auto-generated method stub
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_EC_CREDIT_NEW, inputs, callbackupdate);
	}*/

	private CheerzServiceCallBack callbackfinish = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
			CommonAndroid.hiddenKeyBoard(getActivity());
		};

		public void onError(String message) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				CommonAndroid.showDialog(getActivity(), "" + SkyUtils.convertStringErr(message), null);
				onTouch = false;
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
					LogUtils.i(TAG, "finish==" + mainJsonObject.toString());
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						/*EcUpdateCreditFragment.inputs_new = null;
						JSONObject item_new = mainJsonObject.getJSONObject("data");
						EcUpdateCreditFragment.addView(item_new.toString());*/
						String err_msg = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.err_msg);
						if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
							try {
								JSONObject jsonObject = mainJsonObject.getJSONObject("data");
								if (jsonObject.has(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE)) {
									String message2 = jsonObject.getString(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE);
									SkyUtils.showDialog(getActivity(), message2, new OnClickListener() {
										
										@Override
										public void onClick(DialogInterface dialog, int which) {
											// TODO Auto-generated method stub
											MyCart2Fragment.reLoadMyCart();
										}
									});	
									return;
								}
							} catch (Exception e) {
								
							}
							close(true);
							MyCartActivity.EC_FINISH = new EcFinishDialog(getActivity(), new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
								}
							});
							MyCartActivity.EC_FINISH.show();
						}else{
							CommonAndroid.showDialog(getActivity(), "" + SkyUtils.convertStringErr(err_msg), null);
							onTouch = false;
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};

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
		return R.layout.ec_creditconfirm_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			close(true);
//			new EcCreditNewDialog(getActivity() , EcCreditDialog.inputs_new, new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//				}
//			}).show();
			break;
		case R.id.ec_credit_bt_save:
			if(!onTouch){
				finishEC();
				onTouch = true;
			}
			break;
		default:
			break;
		}
	}
	
	private FragmentActivity getActivity() {
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
	

}