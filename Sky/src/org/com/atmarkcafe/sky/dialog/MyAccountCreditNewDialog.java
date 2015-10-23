package org.com.atmarkcafe.sky.dialog;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import org.com.atmarkcafe.sky.MyCartActivity;

import com.SkyPremiumLtd.SkyPremium.R;

import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.Base2Adialog;
import z.lib.base.BaseItem;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import z.lib.base.wheel.DatePickerDailog;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

public class MyAccountCreditNewDialog extends Base2Adialog implements android.view.View.OnClickListener {
	RelativeLayout main_detail;
	protected static final String TAG = "EcCreditNewDialog";
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	HashMap<String, String> inputs = new HashMap<String, String>();
//	HashMap<String, String> inputs_cache = new HashMap<String, String>();
	TextView ec_creditnew_expired;
	Calendar dateandtime;
	private String dtStart_temp = "";
	private String dtStart_format_update = "";
//	private Boolean StatusDialog;
	FragmentActivity activity;
	BaseItem data;
	private LinearLayout parentView;
	private static DatePickerDailog dp = null;
	public MyAccountCreditNewDialog(FragmentActivity context, BaseItem input,OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		activity = context;
		this.data = input;
		MyAccountMyCreditDialog.showView(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dateandtime = Calendar.getInstance(Locale.US);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.ec_creditnew_header2, R.string.ec_creditnew_header2_j);
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);//icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(header, R.id.header_btn_right).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.ec_credit_bt_save).setOnClickListener(this);
		ec_creditnew_expired = (TextView) findViewById(R.id.ec_creditnew_expired);
		ec_creditnew_expired.setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		if(data != null){
			addView();
		}else{
			SimpleDateFormat format = new SimpleDateFormat("MM/yyyy");
			String currentDate = format.format(new Date());
			dtStart_format_update = new SimpleDateFormat("yyyy/MM").format(new Date());
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired)).setText( CommonAndroid.convertDate(getActivity(), currentDate , 1)  );
		}
		parentView = CommonAndroid.getView(main_detail, R.id.main_sub_id);
		CommonAndroid.TouchViewhiddenKeyBoard(getActivity(), parentView);
	}
	
	
	private void addView() {
		// TODO Auto-generated method stub
		try {
			LogUtils.e(TAG,"add view");
			dtStart_format_update = CommonAndroid.convertDate(getActivity(), data.getString("expiry_date") , 2);
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_name)).setText( data.getString("holder_name") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_cardnum)).setText( data.getString("number") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_code)).setText( data.getString("security_code") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired)).setText( CommonAndroid.convertDate(getActivity(), data.getString("expiry_date") , 1)  );
//			((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired_y)).setText( inputs_cache.get("req[param][expiry_date_y]") );
//			((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_phone_code)).setText( inputs_cache.get("req[param][phone_1]") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_phone)).setText( data.getString("phone") );
			((TextView) CommonAndroid.getView(main_detail, R.id.ec_creditnew_email)).setText( data.getString("email") );
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed(){
		MyAccountMyCreditDialog.showView(true);
		close(true);
	}
	
	private void update() {
		// TODO Auto-generated method stub
		inputs.put("req[param][holder_name]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_name)).getText().toString().trim() );
		inputs.put("req[param][number]", 			((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_cardnum)).getText().toString().trim());
		inputs.put("req[param][security_code]", 	((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_code)).getText().toString().trim());
//		String temp1 = "";
//		if( !(((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired_y)).getText().toString().trim()).equals("")){
//			temp1 = "/";
//		}
//		String ec_creditnew_expired = 				((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired_m)).getText().toString().trim() + temp1 + ((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired_y)).getText().toString().trim();
		inputs.put("req[param][expiry_date]", 		dtStart_format_update/*((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired)).getText().toString().trim()*/);
//		inputs.put("req[param][expiry_date_m]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired_m)).getText().toString().trim());
//		inputs.put("req[param][expiry_date_y]", 		((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired_y)).getText().toString().trim());
//		String ec_creditnew_phone = 				((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_phone_code)).getText().toString().trim() + "" + ((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_phone)).getText().toString().trim();
		inputs.put("req[param][phone]", 			((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_phone)).getText().toString().trim());
//		inputs.put("req[param][phone_1]", 			((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_phone_code)).getText().toString().trim() );
//		inputs.put("req[param][phone_2]", 			((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_phone)).getText().toString().trim() );
		inputs.put("req[param][email]", 			((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_email)).getText().toString().trim() );
		
		/*EcUpdateCreditFragment.inputs_new = inputs;
		close(true);
		new EcCreditConfirmDialog(getActivity() ,inputs ,new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).show();*/
		
		boolean checkNull_ = false;
		if(checkNull("holder_name", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_creditnew_name2) : getActivity().getString(R.string.ec_creditnew_name2_j));
		}else if(checkNull("number", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_creditnew_cardnum2) : getActivity().getString(R.string.ec_creditnew_cardnum2_j));
		}
		else if(checkNull("security_code", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_creditnew_code2) : getActivity().getString(R.string.ec_creditnew_code2_j));
		}
		else if(checkNull("expiry_date", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_creditnew_expired2) : getActivity().getString(R.string.ec_creditnew_expired2_j));
		}
		else if(checkNull("phone", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_creditnew_phone2) : getActivity().getString(R.string.ec_creditnew_phone2_j));
		}
		else if(checkNull("email", inputs)){
			showErr(new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_creditnew_email2) : getActivity().getString(R.string.ec_creditnew_email2_j));
		}
		else{
			String email = inputs.get("req[param][email]");
			if(CommonAndroid.isEmail(email)){
				checkNull_ = true;
			}else{
				CommonAndroid.showDialog(getActivity(), new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_creditnew_mailvalidate) : getActivity().getString(R.string.ec_creditnew_mailvalidate_j) , null);
			}
			
		}
		
		if(checkNull_){
			if(data != null){
				inputs.put("req[param][id_card]", data.getString("id_card") );
				SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_MYACCOUNT_CARD_UPDATE, inputs, callbackupdate);
			}else{
				SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_MYACCOUNT_CARD_ADD, inputs, callbackupdate);
			}
			
		}
		
//		LogUtils.e(TAG, "data==" + inputs.toString());
	}
	
	private CheerzServiceCallBack callbackupdate = new CheerzServiceCallBack() {

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
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						String message_done = (new Setting(getActivity()).isLangEng()) ? getActivity().getString(R.string.create_credit_done) : getActivity().getString(R.string.create_credit_done_j);
						SkyUtils.showDialog(getActivity(), message_done , new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								MyAccountMyCreditDialog.showView(true);
								close(true);
								MyAccountMyCreditDialog.onLoadData();
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
	
	private boolean checkNull(String param_key, HashMap<String, String> inputs){
		String value = inputs.get("req[param][" + param_key + "]");
		if("".equals(value)){
			return true;
		}
		return false;
	}
	
	private void showErr(String err_message){
		String message = err_message + (new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.ec_address_validate) : getActivity().getString(R.string.ec_address_validate_j));
		SkyUtils.showDialog(getActivity(), message ,null);
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
		return R.layout.v2_myaccount_creditnew_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			MyAccountMyCreditDialog.showView(true);
			close(true);
			break;
		case R.id.ec_credit_bt_save:
			update();
			break;
		case R.id.ec_creditnew_expired:
			calenda();
			break;
		default:
			break;
		}
	}
	
	private void calenda(){
		if(!"".equals(dtStart_temp)){
			dateandtime = CommonAndroid.convertToCalendarCredit(getActivity(), dtStart_temp);
		}
		try {
			dp.dismiss();
		} catch (Exception e1) {
		}
		dp = new DatePickerDailog(getActivity(),
				dateandtime, null, new DatePickerDailog.DatePickerListner() {

					@SuppressLint("SimpleDateFormat")
					@Override
					public void OnDoneButton(Dialog datedialog, Calendar c) {
						datedialog.dismiss();
						dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
						dateandtime.set(Calendar.MONTH,
								c.get(Calendar.MONTH));
						dateandtime.set(Calendar.DAY_OF_MONTH,
								c.get(Calendar.DAY_OF_MONTH));
						dtStart_temp = new SimpleDateFormat("yyyy/MM").format(c.getTime());//yyyy/MM
						dtStart_format_update = new SimpleDateFormat("yyyy/MM").format(c.getTime());
						/*if(new Setting(getActivity()).isLangEng())
							dtStart_temp = new SimpleDateFormat("MM/yyyy").format(c.getTime());*/
						((TextView)CommonAndroid.getView(main_detail, R.id.ec_creditnew_expired)).setText( CommonAndroid.convertDate(getActivity(), dtStart_temp, 1) /*dtStart_temp*/ /*new SimpleDateFormat("MM/yyyy")
								.format(c.getTime())*/);
					}

					@Override
					public void OnCancelButton(Dialog datedialog) {
						// TODO Auto-generated method stub
						datedialog.dismiss();
					}
				});
		dp.show();
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
	

}