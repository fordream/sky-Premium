package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;
import java.util.UUID;

import org.com.atmarkcafe.sky.GlobalFunction;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;

import org.com.atmarkcafe.sky.dialog.SkypeProgressDialog;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ForgotPasswordFragment extends BaseFragment {
	private EditText user, email;
	private ImageView btnClearUser,btnClearEmail;
	private TextView txtBackLogin;
	private boolean isShowSoftKeyboad = false;
	private LinearLayout parentView;
	private Setting setting;
	
	public ForgotPasswordFragment() {
		super();
	}

	@Override
	public void onChangeLanguage() {

	}

	@Override
	public int getLayout() {
		return R.layout.v3_forgotpassword;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.btnforgotpassword) {
			
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("req[param][user_id]", user.getText().toString());
			params.put("req[param][user_mail]", email.getText().toString());

			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_FORGOTPWD, params, callback);
		}else if(v.getId() == R.id.backtologin){
			onBackPressed(new Bundle());
			
		}else if(v.getId() == R.id.mail_support){
			sendEmail();
		} else if(v.getId() == R.id.clear_user_id){
			user.setText("");
			CommonAndroid.showView(false, btnClearUser);
		} else if(v.getId() == R.id.clear_email_id){
			email.setText("");
			CommonAndroid.showView(false, btnClearEmail);
		} else if(v.getId() == R.id.forgot_your_id){
			Bundle bundle = new Bundle();
			startFragment(new ForgotIdFragment(), bundle);
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
		emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		try {
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
//			finish();
			Log.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public void init(View view) {
		user = CommonAndroid.getView(view, R.id.user);
		email = CommonAndroid.getView(view, R.id.email);
		btnClearUser = CommonAndroid.getView(view, R.id.clear_user_id);
		btnClearEmail = CommonAndroid.getView(view, R.id.clear_email_id);
		txtBackLogin = CommonAndroid.getView(view, R.id.backtologin);
		btnClearUser.setOnClickListener(this);
		btnClearEmail.setOnClickListener(this);
		
		parentView = CommonAndroid.getView(view, R.id.rl_fotgotpassword_id);
		
		CommonAndroid.getView(view, R.id.btnforgotpassword).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.backtologin).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.mail_support).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.forgot_your_id).setOnClickListener(this);
		setting = new Setting(getActivity());
		
		user.clearFocus();
		email.clearFocus();
		user.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isShowSoftKeyboad = hasFocus;
				if (hasFocus && !"".equalsIgnoreCase(user.getText().toString())) {
					CommonAndroid.showView(true, btnClearUser);
					CommonAndroid.showView(false, btnClearEmail);
				}else{
					CommonAndroid.showView(false, btnClearEmail);
				}
				
			}
		});
		
		email.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isShowSoftKeyboad = hasFocus;
				if (hasFocus && !"".equalsIgnoreCase(email.getText().toString())) {
					CommonAndroid.showView(true, btnClearEmail);
					CommonAndroid.showView(false, btnClearUser);
				}else{
					CommonAndroid.showView(false, btnClearUser);
				}
			}
		});
		
		CommonAndroid.showView(false, btnClearUser);
		CommonAndroid.showView(false, btnClearEmail);
		setupUI(parentView);
		GlobalFunction.handlerKeyboardOnView(getActivity(), parentView);
		user.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				CommonAndroid.showView(true, btnClearUser);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					CommonAndroid.showView(false, btnClearUser);
				}else{
					CommonAndroid.showView(true, btnClearUser);
				}
			}
		});
		
		email.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				CommonAndroid.showView(true, btnClearEmail);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					CommonAndroid.showView(false, btnClearEmail);
				}else{
					CommonAndroid.showView(true, btnClearEmail);
				}
			}
		});
		
		txtBackLogin.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideSoftKeyboard();
				return false;
			}
		});
		
	}

	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {
		private SkypeProgressDialog dialog;

		public void onStart() {
			CommonAndroid.hiddenKeyBoard(getActivity());
			if (dialog == null) {
				dialog = new SkypeProgressDialog(getActivity());

			}
			dialog.show();
			// CommonAndroid.showView(false,
			// getView().findViewById(R.id.loginmain));
		};

		public void onError(String message) {
			Log.i("FG", "ERROR = " + message);
			if (!isFinish()) {
				if (dialog != null) {
					dialog.dismiss();
				}
				CommonAndroid.showDialog(getActivity(), message , null);
				// CommonAndroid.showView(true,
				// getView().findViewById(R.id.loginmain));
			}
		};

		public void onSucces(String response) {
			Log.i("FG", "Success = " + response.toString());
			
			if (setting.isLangEng()) {
				CommonAndroid.showDialog(getActivity(), "" + getResources().getString(R.string.msg_forgot_pass_success), null);
			}else{
				CommonAndroid.showDialog(getActivity(), "" + getResources().getString(R.string.msg_forgot_pass_success_j), null);
			}
			
			if (!isFinish()) {
				if (dialog != null) {
					dialog.dismiss();
				}
				new Setting(getActivity()).saveLang(R.id.eng);
				onBackPressed(new Bundle());
				// CommonAndroid.showView(false,
				// getView().findViewById(R.id.loginmain));
				// startActivity(new Intent(getActivity(), MainActivity.class));
				// finish();
			}
		};
	};
	
	public void setupUI(View view) {

	    //Set up touch listener for non-text box views to hide keyboard.
	    if(!(view instanceof EditText)) {

	        view.setOnTouchListener(new OnTouchListener() {

	            public boolean onTouch(View v, MotionEvent event) {
	            	hideSoftKeyboard();
	                return false;
	            }

	        });
	    }
	    
	}
	
	/**
	 * hide softkeyboard
	 */
	private void hideSoftKeyboard(){
			InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (inputMethodManager.isAcceptingText()) {
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
				email.clearFocus();
				user.clearFocus();
				CommonAndroid.showView(false, btnClearUser);
				CommonAndroid.showView(false, btnClearEmail);
			}
	}
	
}