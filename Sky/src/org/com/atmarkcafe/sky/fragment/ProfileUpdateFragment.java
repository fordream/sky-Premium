//package org.com.atmarkcafe.sky.fragment;
//
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.HashMap;
//import java.util.Locale;
//
//import com.SkyPremiumLtd.SkyPremium.R;
//import org.com.atmarkcafe.sky.dialog.PostalCodeDialog;
//import org.com.atmarkcafe.sky.dialog.ProfileChangeOptionDialog;
//import org.json.JSONObject;
//
//import z.lib.base.BaseFragment;
//import z.lib.base.BaseItem;
//import z.lib.base.CheerzServiceCallBack;
//import z.lib.base.CommonAndroid;
//import z.lib.base.LogUtils;
//import z.lib.base.SkyUtils;
//import z.lib.base.SkyUtils.API;
//import z.lib.base.callback.RestClient.RequestMethod;
//import z.lib.base.wheel.DatePickerDailog;
//import android.annotation.SuppressLint;
//import android.app.Dialog;
//import android.content.DialogInterface;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.widget.TextView;
//
//import com.acv.cheerz.db.Setting;
//
//public class ProfileUpdateFragment extends BaseFragment {
//	protected static final String TAG = "ProfileFragmentUpdate";
//	BaseItem baseItem;
//	View view;
//	String sex = "1";
//	String language = "en";
//	public static String country_phone_code = "";
//	Calendar dateandtime;
//	public ProfileUpdateFragment() {
//		super();
//	}
//
//	@Override
//	public int getLayout() {
//		return R.layout.user_profile;
//	}
//
//	@Override
//	public void onClick(View v) {
//		super.onClick(v);
//		if (v.getId() == R.id.button_submit) {
//			LogUtils.e(TAG, "button_submit");
////			updateProfile();
//			showControllerDialog();
//		} else if(v.getId() == R.id.profile_tel_code){
//			new PostalCodeDialog(getActivity(), new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface dialog, int which) {
//
//				}
//			}).show();
//		}
//	}
//
//	private void updateProfile() {
//		// TODO Auto-generated method stub
//		try {
//			HashMap<String, String> inputs = new HashMap<String, String>();
//			inputs.put("req[param][mail]", ((TextView)CommonAndroid.getView(view, R.id.profile_email_v)).getText().toString().trim() );
//			inputs.put("req[param][mobile_mail]", ((TextView)CommonAndroid.getView(view, R.id.profile_mobile_mail_v)).getText().toString().trim());
//			inputs.put("req[param][last_name]", ((TextView)CommonAndroid.getView(view, R.id.profile_last_name_v)).getText().toString().trim());
//			inputs.put("req[param][middle_name]", ((TextView)CommonAndroid.getView(view, R.id.profile_middle_name_v)).getText().toString().trim());
//			inputs.put("req[param][first_name]", ((TextView)CommonAndroid.getView(view, R.id.profile_first_name_v)).getText().toString().trim());
//			inputs.put("req[param][country_phone_code]", country_phone_code);
//			inputs.put("req[param][tel]", ((TextView)CommonAndroid.getView(view, R.id.profile_tel_v)).getText().toString().trim() );
//			inputs.put("req[param][birth_date]", ((TextView)CommonAndroid.getView(view, R.id.profile_date_of_birth_v)).getText().toString().trim());
//			inputs.put("req[param][sex]", sex );
//			inputs.put("req[param][country]", ((TextView)CommonAndroid.getView(view, R.id.profile_country_v)).getText().toString().trim());
//			inputs.put("req[param][city]", ((TextView)CommonAndroid.getView(view, R.id.profile_city_v)).getText().toString().trim());
//			inputs.put("req[param][postal_code]", ((TextView)CommonAndroid.getView(view, R.id.profile_postal_code_v)).getText().toString().trim());
//			inputs.put("req[param][address]", ((TextView)CommonAndroid.getView(view, R.id.profile_address_v)).getText().toString().trim());
//			inputs.put("req[param][address_kana]", ((TextView)CommonAndroid.getView(view, R.id.profile_address_kana_v)).getText().toString().trim());
//			inputs.put("req[param][language]", language);
//			LogUtils.e(TAG, "data==" + inputs.toString());
//			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_PROFILEUPDATE, inputs, callbackupdate);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void init(final View view) {
//		HashMap<String, String> inputs = new HashMap<String, String>();
//		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_PROFILE, inputs, callbackprofile);
//		CommonAndroid.getView(view, R.id.button_submit).setOnClickListener(this);
//		CommonAndroid.getView(view, R.id.profile_tel_code).setOnClickListener(this);
//		this.view = view;
//		dateandtime = Calendar.getInstance(Locale.US);
//		CommonAndroid.getView(view, R.id.profile_date_of_birth_v).setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(final View arg0) {
//			
//				DatePickerDailog dp = new DatePickerDailog(getActivity(),
//						dateandtime, new DatePickerDailog.DatePickerListner() {
//
//							@SuppressLint("SimpleDateFormat")
//							@Override
//							public void OnDoneButton(Dialog datedialog, Calendar c) {
//								datedialog.dismiss();
//								dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
//								dateandtime.set(Calendar.MONTH,
//										c.get(Calendar.MONTH));
//								dateandtime.set(Calendar.DAY_OF_MONTH,
//										c.get(Calendar.DAY_OF_MONTH));
//								((TextView)CommonAndroid.getView(view, R.id.profile_date_of_birth_v)).setText(new SimpleDateFormat("dd/MM/yyyy")
//										.format(c.getTime()));
//							}
//
//							@Override
//							public void OnCancelButton(Dialog datedialog) {
//								// TODO Auto-generated method stub
//								datedialog.dismiss();
//							}
//						});
//				dp.show();
//			}
//		});
//	}
//	
//	private CheerzServiceCallBack callbackprofile = new CheerzServiceCallBack() {
//
//		public void onStart() {
//			CommonAndroid.hiddenKeyBoard(getActivity());
//		};
//
//		public void onError(String message) {
//			if (!isFinish()) {
//				SkyUtils.showDialog(getActivity(), "" + message);
//			}
//		};
//
//		public void onSucces(String response) {
//			if (!isFinish()) {
//				JSONObject mainJsonObject;
//				try {
//					mainJsonObject = new JSONObject(response);
//					String is_succes = CommonAndroid.getString(mainJsonObject,
//							SkyUtils.KEY.is_success);
//
//					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
//						JSONObject jsonObject = mainJsonObject
//								.getJSONObject("data").getJSONObject("user");
//						baseItem = new BaseItem(jsonObject);
//						addView();
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				
//			}
//		};
//	};
//
//	protected void addView() {
//		// TODO Auto-generated method stub
//		try {
//			JSONObject data = baseItem.getObject();
//			country_phone_code = data.getString("postal_code");//confirm ? country_phone_code
//			((TextView) CommonAndroid.getView(view, R.id.profile_first_name_v)).setText(data.getString("first_name"));
//			((TextView) CommonAndroid.getView(view, R.id.profile_middle_name_v)).setText(data.getString("middle_name"));
//			((TextView) CommonAndroid.getView(view, R.id.profile_last_name_v)).setText(data.getString("last_name"));
//			((TextView) CommonAndroid.getView(view, R.id.profile_email_v)).setText(data.getString("email"));
//			((TextView) CommonAndroid.getView(view, R.id.profile_mobile_mail_v)).setText(data.getString("mobile_mail"));
//			((TextView) CommonAndroid.getView(view, R.id.profile_tel_v)).setText(data.getString("tel"));
//			((TextView) CommonAndroid.getView(view, R.id.profile_date_of_birth_v)).setText(data.getString("birth_date"));
//			sex = data.getString("sex");
//			if(sex.equals("1")){
//				if(new Setting(getActivity()).isLangEng()){
//					((TextView) CommonAndroid.getView(view, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_male));
//				}else{
//					((TextView) CommonAndroid.getView(view, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_male_j));
//				}
//			}else{
//				if(new Setting(getActivity()).isLangEng()){
//					((TextView) CommonAndroid.getView(view, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_female));
//				}else{
//					((TextView) CommonAndroid.getView(view, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_female_j));
//				}
//			}
//			
//			((TextView) CommonAndroid.getView(view, R.id.profile_country_v)).setText(data.getString("country"));
//			((TextView) CommonAndroid.getView(view, R.id.profile_city_v)).setText(data.getString("city"));
//			((TextView) CommonAndroid.getView(view, R.id.profile_postal_code_v)).setText(data.getString("postal_code"));
//			((TextView) CommonAndroid.getView(view, R.id.profile_address_v)).setText(data.getString("address"));
//			((TextView) CommonAndroid.getView(view, R.id.profile_address_kana_v)).setText(data.getString("address_kana"));
//			language = data.getString("lang");
//			if(language.equals("en")){
//				((TextView) CommonAndroid.getView(view, R.id.profile_language_v)).setText(getActivity().getResources().getString(R.string.menu_lang_english));
//			}else{
//				((TextView) CommonAndroid.getView(view, R.id.profile_language_v)).setText(getActivity().getResources().getString(R.string.menu_lang_japanese));
//			}
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	private CheerzServiceCallBack callbackupdate = new CheerzServiceCallBack() {
//
//		public void onStart() {
//			CommonAndroid.hiddenKeyBoard(getActivity());
//		};
//
//		public void onError(String message) {
//			if (!isFinish()) {
//				SkyUtils.showDialog(getActivity(), "" + message);
//			}
//		};
//
//		public void onSucces(String response) {
//			if (!isFinish()) {
//				JSONObject mainJsonObject;
//				try {
//					mainJsonObject = new JSONObject(response);
//					String is_succes = CommonAndroid.getString(mainJsonObject,
//							SkyUtils.KEY.is_success);
//					String message = CommonAndroid.getString(mainJsonObject,
//							SkyUtils.KEY.err_msg);
//					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
//						SkyUtils.showDialog(getActivity(), "Done:" + message );
//					}else{
//						SkyUtils.showDialog(getActivity(), "Error:" + message );
//					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				
//				
//			}
//		};
//	};
//
//	@Override
//	public void onChangeLanguage() {
//		// TODO Auto-generated method stub
//		
//	}
//	
//	public void showControllerDialog() {
//		String[] items;
//		if(new Setting(getActivity()).isLangEng()){
//			items = new String[] { getActivity().getResources().getString(R.string.profile_sex_male), getActivity().getResources().getString(R.string.profile_sex_female) };
//		}else{
//			items = new String[] { getActivity().getResources().getString(R.string.profile_sex_male_j), getActivity().getResources().getString(R.string.profile_sex_female_j) };
//		}
//		new ProfileChangeOptionDialog(getActivity(), items, new DialogInterface.OnClickListener() {
//			@SuppressLint("NewApi")
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which == 0) {
//					
//				} else if (which == 1) {
//					
//				}
//			}
//		}).show();
//	}
//}