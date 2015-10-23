package org.com.atmarkcafe.sky.dialog;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.fragment.ProfileFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.BaseAdialog;
import z.lib.base.BaseItem;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import z.lib.base.wheel.DatePickerDailog;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;
import com.parse.ParseException;

public class ProfileUpdateDialog extends BaseAdialog implements android.view.View.OnClickListener {
	private static final String TAG = "ProjectUpdateDialog";
	BaseItem baseItem;
	static RelativeLayout main_detail;
	String sex = "1";
	String language = "en";
	public static String country_phone_code = "";
	Calendar dateandtime;
	private LoadingView loading;
	private String dtStart_temp = "";
	static Context ctx;
	private org.com.atmarkcafe.view.HeaderView header;
	String profile_address_v = "";
	Boolean isAllASCII_Dialog = false;
	public ProfileUpdateDialog(Context context,BaseItem Item, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.baseItem = Item;
		ctx = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initHeader();
		main_detail = (RelativeLayout) findViewById(R.id.profile_update);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.profile_edit, R.string.profile_edit_j);
		header.visibivityLeft(false, R.drawable.icon_nav_back);
		header.visibivityRight(false, 0);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		CommonAndroid.showView(false, loading);
		openPopActivity(findViewById(R.id.profile_update));
		onLoad();
		final EditText edittext = (EditText) findViewById(R.id.profile_address_v);
		edittext.addTextChangedListener(new TextWatcher() {

		    @Override
		    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {            
//		    	edittext.setSelection(edittext.getText().toString().trim().length());
		    }
		        @Override
		    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
		                    int arg3) {    
		        profile_address_v =  ((TextView)CommonAndroid.getView(main_detail, R.id.profile_address_v)).getText().toString().trim();
		    }
			@Override
			public void afterTextChanged(final Editable et) {
				// TODO Auto-generated method stub
				String s=et.toString();
			      if(!isAllASCII(s) && !isAllASCII_Dialog)
			      {
			    	  	isAllASCII_Dialog = true;
				    	String message_err = new Setting(getActivity()).isLangEng() ? getActivity().getResources().getString(R.string.profile_address_validate) : getActivity().getResources().getString(R.string.profile_address_validate_j);
				  		SkyUtils.showDialog(getActivity(), "" + message_err, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								edittext.setText(removeAllASCII(et.toString()));
								isAllASCII_Dialog = false;
							}
						});
						
				  		CommonAndroid.hiddenKeyBoard(getActivity());
			      }
			}
		});  
	}
	
	private static boolean isAllASCII(String input) {
	    boolean isASCII = true;
	    for (int i = 0; i < input.length(); i++) {
	        int c = input.charAt(i);
	        if (c > 0x7F) { //return ch >= 32 && ch < 127;
	            isASCII = false;
	            break;
	        }
	    }
	    return isASCII;
	}
	
	private static String removeAllASCII(String string) {
		StringBuilder sb = new StringBuilder(string.length());
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        for (char c : string.toCharArray()) {
            if (c <= '\u007F') sb.append(c);
        }
        return sb.toString();
//	    return input;
	}
	
	

	private void onLoad() {
		addView();
		CommonAndroid.getView(main_detail, R.id.button_submit).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.profile_tel_code).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.profile_date_of_birth_v).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.profile_sex_v).setOnClickListener(this);
		CommonAndroid.getView(main_detail, R.id.profile_language_v).setOnClickListener(this);
		dateandtime = Calendar.getInstance(Locale.US);
		
	}

	protected void addView() {
		// TODO Auto-generated method stub
		try {
			JSONObject data = baseItem.getObject();
			country_phone_code = data.getString("country_phone_code");//confirm ? country_phone_code
			setTelCode(country_phone_code);
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_first_name_v)).setText(data.getString("first_name"));
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_middle_name_v)).setText(data.getString("middle_name"));
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_last_name_v)).setText(data.getString("last_name"));
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_email_v)).setText(data.getString("email"));
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_mobile_mail_v)).setText(data.getString("mobile_mail"));
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_tel_v)).setText(data.getString("tel"));
//			((TextView) CommonAndroid.getView(main_detail, R.id.profile_date_of_birth_v)).setText(data.getString("birth_date"));
			dtStart_temp = data.getString("birth_date");  
			try {
				/*SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy");  //yyyy-MM-dd
				Date date = format.parse(dtStart);  
				LogUtils.e(TAG,"date" + date.getTime());*/
				((TextView) CommonAndroid.getView(main_detail, R.id.profile_date_of_birth_v)).setText(CommonAndroid.convertDate(getActivity(), dtStart_temp, 0) /*new SimpleDateFormat("dd/MM/yyyy")
				.format(date.getTime())*/);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			sex = data.getString("sex");
			if(sex.equals("1")){
				if(new Setting(getActivity()).isLangEng()){
					((TextView) CommonAndroid.getView(main_detail, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_male));
				}else{
					((TextView) CommonAndroid.getView(main_detail, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_male_j));
				}
			}else{
				if(new Setting(getActivity()).isLangEng()){
					((TextView) CommonAndroid.getView(main_detail, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_female));
				}else{
					((TextView) CommonAndroid.getView(main_detail, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_female_j));
				}
			}
			
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_country_v)).setText(data.getString("country"));
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_city_v)).setText(data.getString("city"));
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_postal_code_v)).setText(data.getString("postal_code"));
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_address_v)).setText(data.getString("address"));
			((TextView) CommonAndroid.getView(main_detail, R.id.profile_address_kana_v)).setText(data.getString("address_kana"));
			language = data.getString("lang");
			if(new Setting(ctx).isLangEngLogin() /*language.equals("en")*/){
				language = "en";
				((TextView) CommonAndroid.getView(main_detail, R.id.profile_language_v)).setText(getActivity().getResources().getString(R.string.menu_lang_english));
			}else{
				language = "ja";
				((TextView) CommonAndroid.getView(main_detail, R.id.profile_language_v)).setText(getActivity().getResources().getString(R.string.menu_lang_japanese));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void updateProfile() {
		// TODO Auto-generated method stub
		try {
			HashMap<String, String> inputs = new HashMap<String, String>();
			inputs.put("req[param][email]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_email_v)).getText().toString().trim() );
			inputs.put("req[param][mobile_mail]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_mobile_mail_v)).getText().toString().trim());
			inputs.put("req[param][last_name]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_last_name_v)).getText().toString().trim());
			inputs.put("req[param][middle_name]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_middle_name_v)).getText().toString().trim());
			inputs.put("req[param][first_name]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_first_name_v)).getText().toString().trim());
			inputs.put("req[param][country_phone_code]", country_phone_code);
			inputs.put("req[param][tel]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_tel_v)).getText().toString().trim() );
			try {
				JSONObject data = baseItem.getObject();
//				if(CommonAndroid.checkDateIsJapan(getContext(),data.getString("birth_date"))){
//					LogUtils.e(TAG, "updateProfile==ja" + CommonAndroid.convertDateUpdateProfile(getActivity(), dtStart_temp, "ja"));
//					inputs.put("req[param][birth_date]", CommonAndroid.convertDateUpdateProfile(getActivity(), dtStart_temp, "ja"));
//				}else{
//					LogUtils.e(TAG, "updateProfile==en" + CommonAndroid.convertDateUpdateProfile(getActivity(), dtStart_temp, "en"));
//					inputs.put("req[param][birth_date]", CommonAndroid.convertDateUpdateProfile(getActivity(), dtStart_temp, "en"));
//				}
//				data.getString("lang")
				inputs.put("req[param][birth_date]", CommonAndroid.convertDateUpdateProfile(getActivity(), dtStart_temp, data.getString("lang")));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
//			inputs.put("req[param][birth_date]", CommonAndroid.convertDateUpdateProfile(getActivity(), dtStart_temp, language) /*dtStart_temp*/ /* ((TextView)CommonAndroid.getView(main_detail, R.id.profile_date_of_birth_v)).getText().toString().trim()*/);
			inputs.put("req[param][sex]", sex );
			inputs.put("req[param][country]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_country_v)).getText().toString().trim());
			inputs.put("req[param][city]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_city_v)).getText().toString().trim());
			inputs.put("req[param][postal_code]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_postal_code_v)).getText().toString().trim());
			inputs.put("req[param][address]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_address_v)).getText().toString().trim());
			inputs.put("req[param][address_kana]", ((TextView)CommonAndroid.getView(main_detail, R.id.profile_address_kana_v)).getText().toString().trim());
			inputs.put("req[param][language]", language);
			LogUtils.e(TAG, "data==" + inputs.toString());
			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_PROFILEUPDATE, inputs, callbackupdate);
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
				CommonAndroid.showDialog(getActivity(), SkyUtils.convertStringErr(message) , null);
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
//							SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.msg_success) );
							CommonAndroid.showDialog(getActivity(), SkyUtils.convertStringErr( getActivity().getResources().getString(R.string.msg_success)) , null);
						}else{
//							SkyUtils.showDialog(getActivity(), getActivity().getResources().getString(R.string.msg_success_j) );
							CommonAndroid.showDialog(getActivity(), SkyUtils.convertStringErr( getActivity().getResources().getString(R.string.msg_success_j)) , null);
						}
					}
					if(language.equals("en")){
						new Setting(getActivity()).saveLangLogin(R.id.eng);
					}else{
						new Setting(getActivity()).saveLangLogin(R.id.ja);
					}
					ProfileFragment.onloadProfile = false;
					ProfileFragment.onLoadProfile();
					close(true);
//					onLoadProfile();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};
	
	private void initHeader() {
		
	}

	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.profile_update), new AnimationAction() {

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
		return R.layout.user_profile;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.header_btn_left){
			close(true);
			try {
				ProfileFragment.addView(baseItem);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(v.getId() == R.id.profile_date_of_birth_v){
			calenda();
		} else if(v.getId() == R.id.button_submit){
			String s = ((TextView)CommonAndroid.getView(main_detail, R.id.profile_address_v)).getText().toString().trim();
		      if(!isAllASCII(s))
		      {
			    	String message_err = new Setting(getActivity()).isLangEng() ? getActivity().getResources().getString(R.string.profile_address_validate) : getActivity().getResources().getString(R.string.profile_address_validate_j);
			  		SkyUtils.showDialog(getActivity(), "" + message_err, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
//							 edittext.setText("");
						}
					});
					
		        
		      }else{
		    	  updateProfile();
		      }
			
		} else if(v.getId() == R.id.profile_sex_v){
			changeSex();
		} else if(v.getId() == R.id.profile_tel_code){
			new PostalCodeDialog(getActivity(), new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			}).show();
		} else if(v.getId() == R.id.profile_language_v){
			changeLanguage();
		}
	}

	private void changeLanguage() {
		// TODO Auto-generated method stub
		String[] items;
		items = new String[] { getActivity().getResources().getString(R.string.menu_lang_english), getActivity().getResources().getString(R.string.menu_lang_japanese) };
		new ProfileChangeOptionDialog(getActivity(), items, new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 0) {
					language = "en";
				} else if (which == 1) {
					language = "ja";
				}
				if(language.equals("en")){
					((TextView) CommonAndroid.getView(main_detail, R.id.profile_language_v)).setText(getActivity().getResources().getString(R.string.menu_lang_english));
				}else{
					((TextView) CommonAndroid.getView(main_detail, R.id.profile_language_v)).setText(getActivity().getResources().getString(R.string.menu_lang_japanese));
				}
			}
		}).show();
	}

	private void changeSex() {
		// TODO Auto-generated method stub
		String[] items;
		if(new Setting(getActivity()).isLangEng()){
			items = new String[] { getActivity().getResources().getString(R.string.profile_sex_male), getActivity().getResources().getString(R.string.profile_sex_female) };
		}else{
			items = new String[] { getActivity().getResources().getString(R.string.profile_sex_male_j), getActivity().getResources().getString(R.string.profile_sex_female_j) };
		}
		new ProfileChangeOptionDialog(getActivity(), items, new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				LogUtils.e(TAG, "sex==" + which);
				if (which == 0) {
					sex = "1";
				} else if (which == 1) {
					sex = "2";
				}
				if(sex.equals("1")){
					if(new Setting(getActivity()).isLangEng()){
						((TextView) CommonAndroid.getView(main_detail, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_male));
					}else{
						((TextView) CommonAndroid.getView(main_detail, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_male_j));
					}
				}else{
					if(new Setting(getActivity()).isLangEng()){
						((TextView) CommonAndroid.getView(main_detail, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_female));
					}else{
						((TextView) CommonAndroid.getView(main_detail, R.id.profile_sex_v)).setText(getActivity().getResources().getString(R.string.profile_sex_female_j));
					}
				}
			}
		}).show();
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
	
	private void calenda(){
//		LogUtils.e(TAG, "dateandtime1=" + dtStart_temp);
		dateandtime = CommonAndroid.convertToCalendar(getActivity(), dtStart_temp);
//		LogUtils.e(TAG, "dateandtime2=" + dateandtime.toString());
		DatePickerDailog dp = new DatePickerDailog(getActivity(),
				dateandtime, new DatePickerDailog.DatePickerListner() {

					@SuppressLint("SimpleDateFormat")
					@Override
					public void OnDoneButton(Dialog datedialog, Calendar c) {
						datedialog.dismiss();
						dateandtime.set(Calendar.YEAR, c.get(Calendar.YEAR));
						dateandtime.set(Calendar.MONTH,
								c.get(Calendar.MONTH));
						dateandtime.set(Calendar.DAY_OF_MONTH,
								c.get(Calendar.DAY_OF_MONTH));
						dtStart_temp = new SimpleDateFormat("dd/MM/yyyy").format(c.getTime());
						LogUtils.e(TAG, "dateandtime3=" + dtStart_temp);
						((TextView)CommonAndroid.getView(main_detail, R.id.profile_date_of_birth_v)).setText( CommonAndroid.convertDate(getActivity(), dtStart_temp, 0)  );
					}

					@Override
					public void OnCancelButton(Dialog datedialog) {
						// TODO Auto-generated method stub
						datedialog.dismiss();
					}
				});
		dp.show();
	}
	
	private void onLoadProfile() {
		// TODO Auto-generated method stub
			HashMap<String, String> inputs = new HashMap<String, String>();
			SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_PROFILE, inputs, callbackprofile);
	}
	
	private CheerzServiceCallBack callbackprofile = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
			CommonAndroid.hiddenKeyBoard(getActivity());
		};

		public void onError(String message) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
//				SkyUtils.showDialog(getActivity(), "" + message);
				CommonAndroid.showDialog(getActivity(), SkyUtils.convertStringErr(message) , null);
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
						JSONObject jsonObject = mainJsonObject
								.getJSONObject("data").getJSONObject("user");
						baseItem = new BaseItem(jsonObject);
//						addView();
						close(true);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};
	
	public static void setTelCode(String code){
		String[] recourseList= ctx.getResources().getStringArray(R.array.CountryCodesNew);
		String code_value ="";
		for(int i = 0; i < recourseList.length; i++){
			String[] temp =recourseList[i].split(",");
		    try
		    {
		    	String postal_code = temp[0].trim();
		    	if(code.equals(postal_code)){
		    		code_value = temp[1].trim();
		    		LogUtils.e(TAG,"done============");
		    	}
		    }catch(Exception e){
		    	LogUtils.e(TAG, "e2" +e.getMessage());
		    }
		    
		}
		((TextView) CommonAndroid.getView(main_detail, R.id.profile_tel_code_v)).setText(code_value);
		
	}
	
	@Override
	public void onBackPressed(){
		close(true);
	}

}