package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;
import java.util.Locale;

import org.com.atmarkcafe.sky.GcmBroadcastReceiver;
import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.SkypeApplication;
import org.com.atmarkcafe.sky.dialog.ContractExpireToContactDialog;
import org.com.atmarkcafe.sky.dialog.ContractUpdateAnnualDialog;
import org.com.atmarkcafe.sky.dialog.ContractUpdateNotTransferYetDialog;
import org.com.atmarkcafe.sky.dialog.ProfileChangeOptionDialog;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyLoader;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.SkyUtils.KEY_CONTRACT;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;

public class LoginFragment extends BaseFragment {
	protected static final String TAG = "LoginFragment";
	private EditText login, password;
	private TextView checkbox_text;
	private View forgotpassword, btnlogin;
	private CheckBox checkbox/*, checkbox_english, checkbox_ja*/;
	private LinearLayout logo;
	private LinearLayout parentView;
	private boolean isFocusPassEdt = false;
	private boolean isFocusLoginEdt = false;
	private ImageView btnClearAcc,btnClearPass;
//	private SharedPreferences sharePref;
	static TextView lang_en;
	static TextView lang_jp;
	private static FragmentActivity activity;
	public LoginFragment() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.v3_login;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if (v.getId() == R.id.forgotpassword) {
			Bundle bundle = new Bundle();
			startFragment(new ForgotPasswordFragment(), bundle);
		} else if (v.getId() == R.id.btnlogin) {
			if("".equals(login.getText().toString())){
				String err_message = (new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.login_validate_id) : getActivity().getString(R.string.login_validate_id_j));
				SkyUtils.showDialog(getActivity(), err_message ,null);
			}else if("".equals(password.getText().toString())){
				String err_message = (new Setting(getActivity()).isLangEng() ? getActivity().getString(R.string.login_validate_pwd) : getActivity().getString(R.string.login_validate_pwd_j));
				SkyUtils.showDialog(getActivity(), err_message ,null);
			}else{
				startLogin();
			}
		} else if (v.getId() == R.id.eng) {
			new Setting(getActivity()).saveLangLogin(R.id.eng);
			updateCheckBoxLanguage();
		} else if (v.getId() == R.id.ja) {
			new Setting(getActivity()).saveLangLogin(R.id.ja);
			updateCheckBoxLanguage();
		} else if (v.getId() == R.id.checkbox_text) {
			checkbox.setChecked(!checkbox.isChecked());
		} else if (v.getId() == R.id.clear_account_id) {
			login.setText("");
			CommonAndroid.showView(false, btnClearAcc);
		} else if (v.getId() == R.id.clear_pass_id) {
			password.setText("");
			CommonAndroid.showView(false, btnClearPass);
		} else if(v.getId() == R.id.forgot_id){
			Bundle bundle = new Bundle();
			startFragment(new ForgotIdFragment(), bundle);
		}
	}
	
	/**
	 * this method will get total badge and save to sharepreference
	 */
	private void getBadge(){
//		sharePref = getActivity().getSharedPreferences(SkyUtils.shareKey, Context.MODE_PRIVATE);
		HashMap<String, String> params = new HashMap<String, String>();
		SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_GET_BADGE, params, new CheerzServiceCallBack(){
			@Override
			public void onError(String message) {
				// TODO Auto-generated method stub
				super.onError(message);
			}
			
			@Override
			public void onSucces(String response) {
				super.onSucces(response);
				Log.i(TAG, "GET Badge = " + response.toString());
				try {
					JSONObject jsonData = new JSONObject(response);
					JSONObject jsonObj = jsonData.getJSONObject("data");
					int countEmail = jsonObj.getInt("badge_mail");
					int countNews = jsonObj.getInt("badge_new");
//					sharePref.edit().putInt(SkyUtils.BADGE_EMAIL, countEmail).commit();
//					sharePref.edit().putInt(SkyUtils.BADGE_NEWS, countNews).commit();
//					GcmBroadcastReceiver.setBadgeDevice(getActivity(), 0 , 0);
//					CommonAndroid.updateBadgeLauncher(getActivity(), countNews+countEmail);
					if(countEmail > 0){
						GcmBroadcastReceiver.notifiServiceUpdate(getActivity(),countEmail ,1, true, false);
					}
					if(countNews > 0){
						GcmBroadcastReceiver.notifiServiceUpdate(getActivity(),countNews ,2, true, false);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
	}

	private void startLogin() {
		CommonAndroid.hiddenKeyBoard(getActivity());
		CommonAndroid.showView(false, getView().findViewById(R.id.progressbar_text));
		CommonAndroid.showView(false, getView().findViewById(R.id.loginmain));
		CommonAndroid.showView(true, getView().findViewById(R.id.backgound));

		Message message = new Message();
		message.what = 0;
		handlerLogin.sendMessageDelayed(message, DELAY);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
		return;
	}
	@Override
	public void init(View view) {
		activity = getActivity();
		getActivity().getWindow().getDecorView().clearFocus();
		pushParse();
		parentView = CommonAndroid.getView(view, R.id.parent_id);
		login = CommonAndroid.getView(view, R.id.login);
		password = CommonAndroid.getView(view, R.id.password);
		forgotpassword = CommonAndroid.getView(view, R.id.forgotpassword);
		btnlogin = CommonAndroid.getView(view, R.id.btnlogin);
		checkbox_text = CommonAndroid.getView(view, R.id.checkbox_text);
		checkbox = CommonAndroid.getView(view, R.id.checkbox);
		btnClearAcc = CommonAndroid.getView(view, R.id.clear_account_id);
		btnClearPass = CommonAndroid.getView(view, R.id.clear_pass_id);
		btnClearAcc.setOnClickListener(this);
		btnClearPass.setOnClickListener(this);

		/*checkbox_ja = CommonAndroid.getView(view, R.id.checkbox_ja);
		checkbox_english = CommonAndroid.getView(view, R.id.checkbox_english);*/
		lang_en = CommonAndroid.getView(view, R.id.lang_en);
		lang_jp = CommonAndroid.getView(view, R.id.lang_jp);

		logo = CommonAndroid.getView(view, R.id.logo);
		
		CommonAndroid.getView(view, R.id.forgot_id).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.forgotpassword).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.btnlogin).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.eng).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.ja).setOnClickListener(this);
		checkbox_text.setOnClickListener(this);

		/**
		 * android:text="016141216A" android:text="1234567a"
		 * 
		 * 
		 */
		
		login.setText("");
		password.setText("");
		
		login.clearFocus();
		password.clearFocus();
		login.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isFocusLoginEdt = hasFocus;
				
				if(hasFocus && !"".equals(login.getText().toString())){
					CommonAndroid.showView(true, btnClearAcc);
				}else{
					CommonAndroid.showView(false, btnClearAcc);
				}
			}
		});
		
		password.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				isFocusPassEdt = hasFocus;
				if(hasFocus && !"".equals(password.getText().toString())){
					CommonAndroid.showView(true, btnClearPass);
				}else{
					CommonAndroid.showView(false, btnClearPass);
				}
			}
		});
		
		CommonAndroid.showView(false, btnClearAcc);
		CommonAndroid.showView(false, btnClearPass);
		GlobalFunction.handlerKeyboardOnView(getActivity(), parentView);
		// handler button clear 
		login.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
				CommonAndroid.showView(true, btnClearAcc);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
				CommonAndroid.showView(true, btnClearAcc);
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				
				if (s.length() == 0) {
					CommonAndroid.showView(false, btnClearAcc);
				}else{
					CommonAndroid.showView(true, btnClearAcc);
				}
			}
		});
		
		password.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				CommonAndroid.showView(true, btnClearPass);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() == 0) {
					CommonAndroid.showView(false, btnClearPass);
				}else{
					CommonAndroid.showView(true, btnClearPass);
				}
			}
		});
		login_status = true;
		startAnimation();
		GcmBroadcastReceiver.notifiServiceStop(getActivity());
		loadSkypeLoader(new SkyLoader() {

			@Override
			public void loadSucess(Object data) {
				Cursor cursor = (Cursor) data;
				if (cursor != null) {
					if (cursor.moveToNext()) {
						if (SkyUtils.VALUE.STATUS_REMEMBER_SUCCESS.equals(CommonAndroid.getString(cursor, Account.rememberpassword))) {
							login.setText(CommonAndroid.getString(cursor, Account.user_id));
							password.setText(CommonAndroid.getString(cursor, Account.password));
							checkbox.setChecked(true);
							CommonAndroid.showView(false, btnClearAcc);
							CommonAndroid.showView(false, btnClearPass);
						}
					}
					cursor.close();
				}

			}

			@Override
			public Object loadData() {
				return new Account(getActivity()).querryLoginAccount();
			}
		}, 500);
		
	}

	public static void updateCheckBoxLanguage() {
		/*checkbox_english.setChecked(new Setting(getActivity()).isLangEng());
		checkbox_ja.setChecked(!new Setting(getActivity()).isLangEng());*/
		lang_en.setTextColor(Color.parseColor(new Setting(activity).isLangEng() ? "#c2b59b" : "#ffffff"));
		lang_jp.setTextColor(Color.parseColor(new Setting(activity).isLangEng() ? "#ffffff" : "#c2b59b"));
	}

	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.hiddenKeyBoard(getActivity());
			CommonAndroid.showView(false, getView().findViewById(R.id.progressbar_text));
			CommonAndroid.showView(true, getView().findViewById(R.id.progressbar_bg));
			CommonAndroid.showView(true, getView().findViewById(R.id.progressbar));
		};

		public void onError(String message) {
			if (!isFinish()) {
				SkyUtils.showDialog(getActivity(), "" + message,null);
				CommonAndroid.showView(false, getView().findViewById(R.id.progressbar_bg));
				CommonAndroid.showView(false, getView().findViewById(R.id.progressbar));
				login_status = false;
				startAnimation();
			}
		};

		public void onSucces(String response) {
			if (!isFinish()) {
				login_status = false;
				CommonAndroid.showView(false, getView().findViewById(R.id.progressbar_bg));
				CommonAndroid.showView(false, getView().findViewById(R.id.progressbar));
				String contract_code = new Account(getActivity()).getCode();//"" +KEY_CONTRACT.update_contract_unpaid;//
				String contract_status = new Account(getActivity()).getStatus();
				String contract_type = new Account(getActivity()).getContractType();
				
//				boolean TestContract = true;//default == false
				if (/*!TestContract && */contract_status.equals("")/* && !"null".equals(contract_type) && !"".equals(contract_type)*/) {
					
					GcmBroadcastReceiver.registerUserInfo(getActivity());
					getBadge();
					SkyMainActivity.FLAG_CHECK_ONLOADBEADGER = true;
					getActivity().startActivity(new Intent(getActivity(), SkyMainActivity.class));
					getActivity().overridePendingTransition(R.anim.right_in, R.anim.a_nothing);
					finish();

				}
				else {
					// code
					// 0: User đã đăng ký nhưng chưa kích hoạt hợp đồng
					// 1: Đã hết hạn hợp đồng
					// 2: Hợp đồng chưa được thanh toán
					// 3: Hợp đồng đã bị cancel khi thanh toán không thành công
					// 4: User được đăng ký bởi admin nhưng chưa có hợp đồng

					// redirect
					// 0"update": Chuyển đến màn hình update hợp đồng
					// 1"update-bank-transfer" : Chuyển đến màn hình cập nhật hợp
					// đồng với hợp đồng thanh toán qua chuyền khoản ngân hàng
					// 2"unpaid": Chuyển đến màn hình unpaid
					// 3"register-another-credit-card": Chuyển đến màn hình đăng
					// ký credit card mới
					// 4"create": Chuyển đến màn hình tạo mới hợp đồng
					
					/*const CODE_UNPAID = 0; / Unpaid /
					 const CODE_EXPIRED = 1;
					 const CODE_NOT_TRANSFER_YET = 2; / Contract status = unpaid, after login redirect to unpaid page /
					 const CODE_TRANSACTION_FAILED = 3; / Auto transaction failed /
					 const CODE_CREATE = 4; / Created by admin /
					 const CODE_UPGRADE = 5;
					 const CODE_EXTEND = 6;*/
					
					//1
					int code = -1;
					int tem_code = -1;
					try {
						tem_code = Integer.parseInt(contract_code);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					switch (tem_code) {
						case KEY_CONTRACT.extend_after_expire:
							code = KEY_CONTRACT.extend_after_expire;
							break;
						case KEY_CONTRACT.update_contract_unpaid:
							code = KEY_CONTRACT.update_contract_unpaid;
							break;
						case KEY_CONTRACT.not_transfer_yet://"code":2,"redirect":"unpaid"
							code = KEY_CONTRACT.not_transfer_yet;
							break;
						case KEY_CONTRACT.register_another_credit_card: //"code":3,"redirect":"register-another-credit-card"
							code = KEY_CONTRACT.register_another_credit_card;
							break;
						default:
							break;
					}
					LogUtils.e("Login", "code==" + code);
					if(code != -1){
						if(code == KEY_CONTRACT.extend_after_expire){
							new ContractExpireToContactDialog(getActivity(),new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {

								}
							}).show();
						}else{
							if(code != KEY_CONTRACT.not_transfer_yet && code != KEY_CONTRACT.register_another_credit_card){
								new ContractUpdateAnnualDialog(getActivity(), code, new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {

									}
								}).show();
							}else{
								//not_transfer_yet
								new ContractUpdateNotTransferYetDialog(getActivity(), code, new DialogInterface.OnClickListener() {
									@SuppressLint("NewApi")
									@Override
									public void onClick(DialogInterface dialog, int which) {

									}
								}).show();
							}
						}
						
					}else{
						String message = new Setting(
								getActivity()).isLangEng() ? getString(R.string.msg_err_inactive_login) : getString(R.string.msg_err_inactive_login_j);
						CommonAndroid.showDialog(getActivity(), message, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								login_status = true;
								startAnimation();
							}
						} );
					}
					startAnimation();
				}
			}
		};
	};

	public void onChangeLanguage() {
		LogUtils.i(TAG, "onChangeLanguage");
		updateCheckBoxLanguage();
	}

	public static final long DELAY = 5;
	private static Boolean login_status = true;
	private void startAnimation() {
		Message message = new Message();
		message.what = 0;
		handler.sendMessageDelayed(message, DELAY);
	}

	private Handler handler = new Handler() {
		public void dispatchMessage(android.os.Message msg) {

			if (msg.what == 0) {
				if (getActivity() != null) {
//					int min = (int) getActivity().getResources().getDimension(R.dimen.dimen_0dp);/*dimen_12dp*/
					int next = (int) getActivity().getResources().getDimension(R.dimen.dimen_0dp);/*dimen_5dp*/
					int max = (int) getActivity().getResources().getDimension(R.dimen.dimen_270dp); //dimen_280dp
					if (next == 0) {
						next = 2;
					}
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) logo.getLayoutParams();
					int topMargin = params.topMargin;

					/*if (topMargin - next > min) {
						topMargin = topMargin - next;
					} else {
						topMargin = min;
					}*/
					if (topMargin + next < max) {
						topMargin = topMargin + next;
					} else {
						topMargin = max;
					}
					params.topMargin = topMargin + (int) getActivity().getResources().getDimension(R.dimen.dimen_5dp);//-

					logo.setLayoutParams(params);
					Message message = new Message();
					message.what = 0;
					if (topMargin < max) { //topMargin > min
						handler.sendMessageDelayed(message, DELAY);
					} else {
						//TODO: set default lang
						if(login_status){
							new Setting(getActivity()).saveLang(R.id.eng);
							updateCheckBoxLanguage();
						}
						
						CommonAndroid.showView(true, getView().findViewById(R.id.progressbar_text));
						CommonAndroid.showView(true, getView().findViewById(R.id.loginmain));
//						CommonAndroid.showView(true, getView().findViewById(R.id.clear_account_id));
						CommonAndroid.showView(true, getView().findViewById(R.id.backgound));
						getView().findViewById(R.id.loginmain).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale));
//						getView().findViewById(R.id.backgound).startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.scale));

					}
				}
			}
		}
	};

	private Handler handlerLogin = new Handler() {
		public void dispatchMessage(android.os.Message msg) {

			if (msg.what == 0) {
				if (getActivity() != null) {
					int max = (int) getActivity().getResources().getDimension(R.dimen.dimen_144dp);
					int next = (int) getActivity().getResources().getDimension(R.dimen.dimen_5dp);
					LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) logo.getLayoutParams();
					int topMargin = params.topMargin;

					/*if (topMargin + next < max) {
						topMargin = topMargin + next;
					} else {
						topMargin = max;
					}*/
					LogUtils.i(TAG, "topMargin==" + topMargin);
					if (topMargin - next > max) {
						topMargin = topMargin - next;
					} else {
						topMargin = max;
					}
					params.topMargin = topMargin;

					logo.setLayoutParams(params);
					Message message = new Message();
					message.what = 0;
					if (topMargin > max) { //<
						handlerLogin.sendMessageDelayed(message, DELAY);
					} else {
						HashMap<String, String> inputs = new HashMap<String, String>();
						// params.put("req[lang]", "ja");
//						String account_id = "123140331C";
						String account_id = login.getText().toString();
						inputs.put("req[param][user_id]", account_id);
						inputs.put("req[param][password]", password.getText().toString());
						inputs.put(SkyUtils.KEY.REMEMBERPASSWORD, checkbox.isChecked() + "");
						SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_LOGIN, inputs, callback);
						
					}
				}
			}
		}
	};
	
	private void pushParse() {
		// TODO Auto-generated method stub
		try {
			if (new Setting(getActivity()).isPush() == null){
				Locale current_locale = getResources().getConfiguration().locale;
//				ShortcutBadger.with(getActivity()).count(0);
				String[] items;
				String title = getActivity().getResources().getString(R.string.msg_push_confirm);
				items = new String[] { getActivity().getResources().getString(R.string.msg_push_confirm_no),getActivity().getResources().getString(R.string.msg_push_confirm_yes) };
				if (/*!new Setting(getActivity()).isLangEng()*/ "ja_JP".equals(current_locale.toString())) {
					LogUtils.i(TAG, "current locale==" + current_locale);//ja_JP
					title = getActivity().getResources().getString(R.string.msg_push_confirm_j);
					items = new String[] { getActivity().getResources().getString(R.string.msg_push_confirm_no_j),getActivity().getResources().getString(R.string.msg_push_confirm_yes_j) };
				}
				new ProfileChangeOptionDialog(getActivity(), items, title, new DialogInterface.OnClickListener() {
					@SuppressLint("NewApi")
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 1) {
							// yes-->register push
							new Setting(getActivity()).settingPush(which + "");
							GcmBroadcastReceiver.resetUserInfo(getActivity());
						} else if (which == 0) {
							// no
							new Setting(getActivity()).settingPush(which + "");
						}

					}
				}).show();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}