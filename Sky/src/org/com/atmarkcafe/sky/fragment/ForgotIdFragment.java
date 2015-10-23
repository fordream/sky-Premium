package org.com.atmarkcafe.sky.fragment;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
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
import z.lib.base.wheel.DatePickerDailog;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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

public class ForgotIdFragment extends BaseFragment {
	protected static final String TAG = "ForgotIdFragment";
	private EditText /*user,*/ email/*, phone*/ , birth;
	private ImageView /*btnClearUser,*/btnClearEmail/*,btnClearPhone*/;
	private TextView txtBackLogin;
	private boolean isShowSoftKeyboad = false;
	private LinearLayout parentView;
	private Setting setting;
	Calendar dateandtime;
	private String dtStart_temp = null;
	public ForgotIdFragment() {
		super();
	}

	@Override
	public void onChangeLanguage() {

	}

	@Override
	public int getLayout() {
		return R.layout.v3_forgot_id;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.v2_forgot_birth){
			calenda();
		}
		else if (v.getId() == R.id.btnforgotid) {
//			CommonAndroid.showView(false, btnClearUser);
			CommonAndroid.showView(false, btnClearEmail);
//			CommonAndroid.showView(false, btnClearPhone);
			HashMap<String, String> params = new HashMap<String, String>();
//			params.put("req[param][username]", user.getText().toString());
			params.put("req[param][mail]", email.getText().toString());
//			params.put("req[param][phone]", phone.getText().toString());
			
			params.put("req[param][birthday]", birth.getText().toString());
			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_FORGOTID, params, callback);
		}else if(v.getId() == R.id.backtologin){
			Bundle bundle = new Bundle();
			bundle.putString("back_login", "back_login");
			onBackPressed(bundle);
			/*Bundle bundle = new Bundle();
			startFragment(new LoginFragment(), bundle);*/
		}else if(v.getId() == R.id.mail_support){
			sendEmail();
		} /*else if(v.getId() == R.id.clear_forgot_id_name){
			user.setText("");
			CommonAndroid.showView(false, btnClearUser);
		} */else if(v.getId() == R.id.clear_forgot_id_email){
			email.setText("");
			CommonAndroid.showView(false, btnClearEmail);
		} /*else if(v.getId() == R.id.clear_forgot_id_mobile){
			phone.setText("");
			CommonAndroid.showView(false, btnClearPhone);
		} */
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
		dateandtime = Calendar.getInstance(Locale.UK);
//		user = CommonAndroid.getView(view, R.id.forgot_id_name);
		email = CommonAndroid.getView(view, R.id.forgot_id_email);
//		phone = CommonAndroid.getView(view, R.id.forgot_id_mobile);
//		btnClearUser = CommonAndroid.getView(view, R.id.clear_forgot_id_name);
		btnClearEmail = CommonAndroid.getView(view, R.id.clear_forgot_id_email);
//		btnClearPhone = CommonAndroid.getView(view, R.id.clear_forgot_id_mobile);
		txtBackLogin = CommonAndroid.getView(view, R.id.backtologin);
//		btnClearUser.setOnClickListener(this);
		btnClearEmail.setOnClickListener(this);
//		btnClearPhone.setOnClickListener(this);
		
		birth = CommonAndroid.getView(view, R.id.v2_forgot_birth);
		
		parentView = CommonAndroid.getView(view, R.id.rl_fotgotpassword_id);
		
		CommonAndroid.getView(view, R.id.btnforgotid).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.backtologin).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.mail_support).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.v2_forgot_birth).setOnClickListener(this);
		setting = new Setting(getActivity());
		
//		user.clearFocus();
		email.clearFocus();
//		phone.clearFocus();
		/*user.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isShowSoftKeyboad = hasFocus;
				if (hasFocus && !"".equalsIgnoreCase(user.getText().toString())) {
					CommonAndroid.showView(true, btnClearUser);
					CommonAndroid.showView(false, btnClearEmail);
					CommonAndroid.showView(false, btnClearPhone);
				}else{
					CommonAndroid.showView(false, btnClearEmail);
					CommonAndroid.showView(false, btnClearPhone);
				}
				
			}
		});*/
		
		email.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isShowSoftKeyboad = hasFocus;
				if (hasFocus && !"".equalsIgnoreCase(email.getText().toString())) {
					CommonAndroid.showView(true, btnClearEmail);
//					CommonAndroid.showView(false, btnClearUser);
//					CommonAndroid.showView(false, btnClearPhone);
				}/*else{
					CommonAndroid.showView(false, btnClearUser);
					CommonAndroid.showView(false, btnClearPhone);
				}*/
			}
		});
		
		/*phone.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isShowSoftKeyboad = hasFocus;
				if (hasFocus && !"".equalsIgnoreCase(email.getText().toString())) {
					CommonAndroid.showView(true, btnClearPhone);
					CommonAndroid.showView(false, btnClearUser);
					CommonAndroid.showView(false, btnClearEmail);
				}else{
					CommonAndroid.showView(false, btnClearUser);
					CommonAndroid.showView(false, btnClearEmail);
				}
			}
		});*/
		
//		CommonAndroid.showView(false, btnClearUser);
		CommonAndroid.showView(false, btnClearEmail);
//		CommonAndroid.showView(false, btnClearPhone);
		setupUI(parentView);
		GlobalFunction.handlerKeyboardOnView(getActivity(), parentView);
		/*user.addTextChangedListener(new TextWatcher() {
			
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
		});*/
		
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
		
		/*phone.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				CommonAndroid.showView(true, btnClearPhone);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					CommonAndroid.showView(false, btnClearPhone);
				}else{
					CommonAndroid.showView(true, btnClearPhone);
				}
			}
		});
		*/
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
			LogUtils.i("FG", "ERROR = " + message);
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
			LogUtils.i("FG", "Success = " + response.toString());
			
			
			
			if (!isFinish()) {
				if (dialog != null) {
					dialog.dismiss();
				}

				String message = getResources().getString(R.string.msg_forgot_id_success_j);
				if (setting.isLangEng()) 
					message = getResources().getString(R.string.msg_forgot_id_success);
					CommonAndroid.showDialog(getActivity(), message , new OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							new Setting(getActivity()).saveLang(R.id.eng);
							Bundle bundle = new Bundle();
							bundle.putString("back_login", "back_login");
							onBackPressed(bundle);
//							onBackPressed(new Bundle());
						}
					});
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
//				user.clearFocus();
//				CommonAndroid.showView(false, btnClearUser);
				CommonAndroid.showView(false, btnClearEmail);
			}
	}
	
	private void calenda(){
		Log.i(TAG, "====> dtStart_temp:  " + dtStart_temp);
		if(dtStart_temp != null)
			dateandtime = CommonAndroid.convertToCalendar(getActivity(), dtStart_temp);
//			
		DatePickerDailog dp = new DatePickerDailog(getActivity(),
				dateandtime, new DatePickerDailog.DatePickerListner() {

					@SuppressLint("SimpleDateFormat")
					@Override
					public void OnDoneButton(Dialog datedialog, Calendar c) {
						datedialog.dismiss();
						dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
						dateandtime.set(Calendar.MONTH,	c.get(Calendar.MONTH));
						dateandtime.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
						
						dtStart_temp = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
						dtStart_temp =  CommonAndroid.convertDate(getActivity(), dtStart_temp, 0) ;
						LogUtils.e(TAG, "dateandtime3=" + dtStart_temp);
						((TextView)CommonAndroid.getView(parentView, R.id.v2_forgot_birth)).setText( dtStart_temp );
					}

					@Override
					public void OnCancelButton(Dialog datedialog) {
						// TODO Auto-generated method stub
						datedialog.dismiss();
					}
				});
		dp.show();
	}
}