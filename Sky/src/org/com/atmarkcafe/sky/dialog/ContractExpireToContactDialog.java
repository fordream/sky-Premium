package org.com.atmarkcafe.sky.dialog;

import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.customviews.charting.MEditText;
import org.com.atmarkcafe.sky.fragment.LoginFragment;
import org.json.JSONObject;

import z.lib.base.BaseAdialog;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;

public class ContractExpireToContactDialog extends BaseAdialog implements android.view.View.OnClickListener {
	static RelativeLayout main_detail;
	protected static final String TAG = "ContractExpireToContactDialog";
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderView header;
	private static FragmentActivity activity;
	private MEditText txtTile, txtContent;
	private int contactType =  8;
	private ImageButton btnCall, btnSMS, btnMenu;
	private LinearLayout parentView;
	public ContractExpireToContactDialog(FragmentActivity activity_ , OnClickListener clickListener) {
		super(activity_, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		activity = activity_;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.contact_us_title, R.string.contact_us_title_j);
		header.visibivityLeft(false, R.drawable.icon_nav_back);
//		header.visibivityRight(false, 0);
		btnCall = (ImageButton) findViewById(R.id.header_btn_right_left);
		btnCall.setVisibility(View.VISIBLE);
		btnCall.setImageResource(R.drawable.icon_call);
		btnCall.setOnClickListener(this);

		btnSMS = (ImageButton) findViewById(R.id.header_btn_right);
		btnSMS.setImageResource(R.drawable.icon_sms);
		btnSMS.setOnClickListener(this);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
//		CommonAndroid.getView(header, R.id.header_btn_right).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		txtTile = (MEditText) findViewById(R.id.contact_title_id);
		txtContent = (MEditText) findViewById(R.id.contact_content_id);
		CommonAndroid.getView(main_detail, R.id.contact_btn_confirm_id).setOnClickListener(this);
		String message = String.format( new Setting(getActivity()).isLangEng() ? getActivity().getResources().getString(R.string.user_expired) : getActivity().getResources().getString(R.string.user_expired_j) , new Account(getActivity()).getUserId());
		TextView user_expired = (TextView) findViewById(R.id.user_expired);
		user_expired.setText( message);
		parentView = CommonAndroid.getView(main_detail, R.id.contact_content_layout_id);
		CommonAndroid.TouchViewhiddenKeyBoard(getActivity(), parentView);
	}
	
	private void sendContact() {
		String txtTitleMsg = txtTile.getText().toString().trim();
		if (txtTitleMsg.equalsIgnoreCase("")) {
			if (new Setting(getActivity()).isLangEng()) {
				SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.contact_title_require), null);
			}else{
				SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.contact_title_require_j), null);
			}
			return;
		}
		
		String txtContentMsg = txtContent.getText().toString().trim();
		if (txtContentMsg.equalsIgnoreCase("")) {
			if (new Setting(getActivity()).isLangEng()) {
				SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.contact_content_require), null);
			}else{
				SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.contact_content_require_j), null);
			}
			return;
		}
		
		JSONObject objParam = new JSONObject();
		try {
			objParam.put("contact_type", String.valueOf(contactType));
			objParam.put("content", txtContent.getText().toString().trim());
			objParam.put("title", txtTile.getText().toString().trim());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SkyUtils.execute(getActivity(), RequestMethod.POST,SkyUtils.API.API_USER_CONTACT, SkyUtils.paramRequest(getActivity(), objParam),
				new CheerzServiceCallBack() {

					@Override
					public void onStart() {
						CommonAndroid.showView(true, loading);
						
						super.onStart();
					}

					@Override
					public void onError(String message) {
						LogUtils.i(TAG, "Error MSG = " + message);
						CommonAndroid.showView(false, loading);
						showAlertSendMsg(0);
						super.onError(message);
					}

					@Override
					public void onSucces(String response) {
						CommonAndroid.showView(false, loading);
						LogUtils.i(TAG, "Response : " + response);
						try {

							if (response != null) {
								JSONObject jsonResObject = new JSONObject(
										response);
								int flagSuccess = jsonResObject
										.getInt("is_success");
								CommonAndroid.showView(false, loading);
								showAlertSendMsg(flagSuccess);
							}
						} catch (Exception e) {
							// TODO: handle exception
						}
						super.onSucces(response);
					}

				});
	}
	
private void showAlertSendMsg(final int flagSuccess) {
		
		CommonAndroid.showView(false, loading);
		String msg = "";
		Boolean languageId = new Setting(getActivity()).isLangEng();
		if (flagSuccess == 1) {
			if (languageId) {
				msg = getActivity().getResources().getString(R.string.msg_success);
			} else {
				msg = getActivity().getResources().getString(R.string.msg_success_j);
			}
		} else {
			if (languageId) {
				msg = getActivity().getResources().getString(R.string.msg_failure);
			} else {
				msg = getActivity().getResources().getString(R.string.msg_failure_j);
			}
		}
		SkyUtils.showDialog(getActivity(), msg, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (flagSuccess == 1) {
					txtContent.setText("");
					txtTile.setText("");
				}
			}
		});
		
	}

	@Override
	public void onBackPressed(){
		close(true);
	}
	
	
	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.main_detail), new AnimationAction() {

			@Override
			public void onAnimationEnd() {
				try {
					new Setting(getActivity()).saveLang(R.id.eng);
					LoginFragment.updateCheckBoxLanguage();
				} catch (Exception e) {}
				dismiss();
				if (isOnClick) {
					clickListener.onClick(null, 0);
				}
			}
		});
	}

	@Override
	public int getLayout() {
		return R.layout.contact_expire_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			close(true);
			break;
		case R.id.contact_btn_confirm_id:
			sendContact();
			break;
		case R.id.header_btn_right:
			LogUtils.i(TAG, "email");
			sendEmail();
			break;
		case R.id.header_btn_right_left:
			LogUtils.i(TAG, "Call");
			openActionCall();
			break;	
		default:
			break;
		}
	}
	
	protected void sendEmail() {
		String[] TO = { "support@member.skypremium.com.sg" };
		String[] CC = { "support@member.skypremium.com.sg" };
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setData(Uri.parse("mailto:support@member.skypremium.com.sg"));
		emailIntent.setType("text/plain");

		emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
		emailIntent.putExtra(Intent.EXTRA_CC, CC);
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, "SKY PREMIUM");
		emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message goes here");

		try {
			getActivity().startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			//finish();
			LogUtils.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException ex) {
			ex.printStackTrace();
		}
	}
	
	private void openActionCall() {
		if (SkyUtils.isSimExist(getActivity())) {
			try {
				Intent intenCall = new Intent(Intent.ACTION_DIAL);//ACTION_CALL
				intenCall.setData(Uri.parse("tel:6565357704"));
				getActivity().startActivity(intenCall);
			} catch (Exception e) {}
		}else{
			if (new Setting(getActivity()).isLangEng()) {
				SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.call_error), null);
			}else{
				SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.call_error_j), null);
			}
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
	
}