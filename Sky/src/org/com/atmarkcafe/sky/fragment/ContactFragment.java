package org.com.atmarkcafe.sky.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.customviews.charting.MEditText;
import org.com.atmarkcafe.sky.customviews.charting.MTextView;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

public class ContactFragment extends BaseFragment implements OnClickListener{

	private String TAG = "ContactFragment";
	private ImageButton btnCall, btnSMS, btnMenu;
	private ImageView btnCatalog;
	private MEditText txtTile, txtContent;
	private Button btnConfirmSend;
	private HeaderView txtTitleHeader;
	private Setting setting;
	private RelativeLayout rlCatalog;
	public MTextView txtCatalog;
	public boolean languageId = true;
	private int contactType = 0;
	private String txtTitleMsg = "", txtContentMsg = "";
	private LoadingView loading;
	private LinearLayout llHistory, llContactUs;
	private RelativeLayout parentView;
	private ScrollView main_scroll;
	private Boolean showKeyboard = false;
	public ContactFragment() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.contact_layout;
	}

	@Override
	public void init(View view) {
//		final int height_temp = (int) getActivity().getResources().getDimension(R.dimen.dimen_350dp);
		final int height_temp_2 = (int) getActivity().getResources().getDimension(R.dimen.dimen_240dp);
		main_scroll = (ScrollView) view.findViewById(R.id.main_scroll);
        final View activityRootView = view.findViewById(R.id.contact_content_layout_id);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            Rect r = new Rect();
            //r will be populated with the coordinates of your view that area still visible.
            activityRootView.getWindowVisibleDisplayFrame(r);

            int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
            if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
            	if(!showKeyboard){
            		LogUtils.i(TAG, "aaaaaaaaaaaaaaaaaaa");
                    main_scroll.setLayoutParams(new  LayoutParams(LayoutParams.WRAP_CONTENT,   height_temp_2));
                    showKeyboard = true;
            	}
            }else{
            	if(showKeyboard){
            		LogUtils.i(TAG, "bbbbbbbbbbb");
                	main_scroll.setLayoutParams(new  LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                	showKeyboard = false;
            	}
            	
            }
         }
        }); 
        
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_infor_green, getResources().getColor(R.color.contact_icon));//OK
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_contact_green, getResources().getColor(R.color.contact_icon));//OK
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_indi_gray2, getResources().getColor(R.color.home_icon_select));//OK
		
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();

		StrictMode.setThreadPolicy(policy);
		parentView = (RelativeLayout)view.findViewById(R.id.contact_us_mail_layout_id);
		btnCall = (ImageButton) view.findViewById(R.id.header_btn_right_left);
		btnCall.setVisibility(View.VISIBLE);
		btnCall.setImageResource(R.drawable.icon_call);
		btnCall.setOnClickListener(this);

		btnSMS = (ImageButton) view.findViewById(R.id.header_btn_right);
		btnSMS.setImageResource(R.drawable.icon_sms);
		btnSMS.setOnClickListener(this);

		
		setting = new Setting(getActivity());
		txtTitleHeader = CommonAndroid.getView(view,R.id.contact_header_id);
		txtTitleHeader.initHeader(R.string.contact_us_title, R.string.contact_us_title_j);

		languageId = setting.isLangEng();
		btnMenu = (ImageButton) view.findViewById(R.id.header_btn_left);
		btnMenu.setOnClickListener(this);

		rlCatalog = (RelativeLayout) view.findViewById(R.id.contact_catalog_id);
		rlCatalog.setOnClickListener(this);

		txtCatalog = (MTextView) view.findViewById(R.id.contact_header_catalog_text_id);

		btnConfirmSend = (Button) view.findViewById(R.id.contact_btn_confirm_id);
		btnConfirmSend.setOnClickListener(this);
		if (languageId) {
			btnConfirmSend.setText(getActivity().getResources().getString(R.string.tlt_send));
		}else{
			btnConfirmSend.setText(getActivity().getResources().getString(R.string.tlt_send_j));
		}

		txtTile = (MEditText) view.findViewById(R.id.contact_title_id);
		txtContent = (MEditText) view.findViewById(R.id.contact_content_id);

		loading = CommonAndroid.getView(view, R.id.loading);
		CommonAndroid.showView(false, loading);

		llHistory = (LinearLayout) view
				.findViewById(R.id.contactlist_ll_contact_history_id);
		llHistory.setOnClickListener(this);
//		SkyMainActivity.setupUI(getActivity(), parentView);
		
//		mScrollview = (ScrollView)view.findViewById(R.id.contact_scroll_id);
//		mScrollview.getLayoutParams().height = ((int) ScHgt(getActivity())-47);
	 
	}
	
	public double ScHgt(Context context)
	{
	    WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
	    Display display = windowManager.getDefaultDisplay();
	    return display.getHeight();
	} 
	/**
	 * hide keyboard
	 * @param activity
	 * @param v
	 */
	public static void hideSoftKeyboard(Activity activity,View v) {
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    if (inputMethodManager.isAcceptingText() && inputMethodManager.isActive()) {
	    	inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		}
	}
	
	/**
	 * handler keybord on view
	 * @param activity
	 * @param view
	 */
	public static void handlerKeyboardOnView(final Activity activity,View view) {

	    //Set up touch listener for non-text box views to hide keyboard.
		if (activity != null) {
		    if(!(view instanceof EditText)) {

		        view.setOnTouchListener(new OnTouchListener() {

		            public boolean onTouch(View v, MotionEvent event) {
		            	hideSoftKeyboard(activity,v);
		                return false;
		            }

		        });
		    }
		}

	    
	}

	@Override
	public void onChangeLanguage() {
		
	}

	private void postDataContact() {
		languageId = setting.isLangEng();
		if (contactType == 0) {
			if (languageId) {
				SkyUtils.showDialog(getActivity(), getResources().getString(R.string.contact_confirm_require), null);
			}else{
				SkyUtils.showDialog(getActivity(), getResources().getString(R.string.contact_confirm_require_j), null);
			}
			return;
		}
		
		if (txtTitleMsg.equalsIgnoreCase("")) {
			if (languageId) {
				SkyUtils.showDialog(getActivity(), getResources().getString(R.string.contact_title_require), null);
			}else{
				SkyUtils.showDialog(getActivity(), getResources().getString(R.string.contact_title_require_j), null);
			}
			return;
		}
		
		if (txtContentMsg.equalsIgnoreCase("")) {
			if (languageId) {
				SkyUtils.showDialog(getActivity(), getResources().getString(R.string.contact_content_require), null);
			}else{
				SkyUtils.showDialog(getActivity(), getResources().getString(R.string.contact_content_require_j), null);
			}
			return;
		}
		
		
		JSONObject objParam = new JSONObject();
		try {
			objParam.put("contact_type", String.valueOf(contactType));
			objParam.put("content", txtContentMsg);
			objParam.put("title", txtTitleMsg);
			
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
						Log.i(TAG, "Error MSG = " + message);
						CommonAndroid.showView(false, loading);
						if (languageId) {
							SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure), null);
						}else{
							SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure_j), null);
						}
						super.onError(message);
					}

					@Override
					public void onSucces(String response) {
						CommonAndroid.showView(false, loading);
						Log.i(TAG, "Response : " + response);
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
		
		// 
//		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("req[param][contact_type]", String.valueOf(contactType));
//		params.put("req[param][content]", txtContentMsg);
//		params.put("req[param][title]", txtTitleMsg);
//		
//		SkyUtils.execute(getActivity(), RequestMethod.GET,SkyUtils.API.API_USER_CONTACT , params, new CheerzServiceCallBack(){
//			@Override
//			public void onStart() {
//				CommonAndroid.showView(true, loading);
//				
//				super.onStart();
//			}
//
//			@Override
//			public void onError(String message) {
//				Log.i(TAG, "Error MSG = " + message);
//				CommonAndroid.showView(false, loading);
//				if (languageId) {
//					SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure), null);
//				}else{
//					SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure_j), null);
//				}
//				super.onError(message);
//			}
//
//			@Override
//			public void onSucces(String response) {
//				CommonAndroid.showView(false, loading);
//				Log.i(TAG, "Response : " + response);
//				try {
//
//					if (response != null) {
//						JSONObject jsonResObject = new JSONObject(
//								response);
//						int flagSuccess = jsonResObject
//								.getInt("is_success");
//						CommonAndroid.showView(false, loading);
//						showAlertSendMsg(flagSuccess);
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				super.onSucces(response);
//			}
//		});
	}

	@Override
	public void onClick(View v) {
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			SkyMainActivity.sm.toggle();
			break;
		case R.id.header_btn_right:
			Log.i(TAG, "email");
			sendEmail();
			break;
		case R.id.header_btn_right_left:
			Log.i(TAG, "Call");
			openActionCall();
			break;
		case R.id.contact_catalog_id:
			Log.i(TAG, "show dialog");
			PopupCatalog popup = new PopupCatalog();

			popup.show(getActivity().getSupportFragmentManager(), "popup");
			break;
		case R.id.contact_btn_confirm_id:
			Log.i(TAG, "confirm send");
			txtTitleMsg = txtTile.getText().toString();
			txtContentMsg = txtContent.getText().toString();
			postDataContact();

			break;
		case R.id.contactlist_ll_contact_history_id:
			gotoListContactHistory();
			break;
		default:
			break;
		}
		super.onClick(v);
	}

	private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	
	private void showAlertSendMsg(final int flagSuccess) {
		
		CommonAndroid.showView(false, loading);
		String msg = "";
		languageId = setting.isLangEng();
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
					gotoListContactHistory();
				}
				
			}
		});
		
//		
//		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
//				getActivity());
//		alertBuilder.setTitle(getResources().getString(
//				R.string.menu_sky_premium));
//		alertBuilder.setIcon(R.drawable.ic_launcher);
//		if (flagSuccess == 1) {
//			if (languageId) {
//				alertBuilder.setMessage(R.string.msg_success);
//			} else {
//				alertBuilder.setMessage(R.string.msg_success_j);
//			}
//		} else {
//			if (languageId) {
//				alertBuilder.setMessage(R.string.msg_failure);
//			} else {
//				alertBuilder.setMessage(R.string.msg_failure_j);
//			}
//		}
//		String strYes = null;
//		if (languageId) {
//			strYes = getResources().getString(R.string.msg_yes);
//		} else {
//			strYes = getResources().getString(R.string.msg_yes_j);
//		}
//		alertBuilder.setPositiveButton(strYes,
//				new DialogInterface.OnClickListener() {
//
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						if (flagSuccess == 1) {
//							gotoListContactHistory();
//						}
//					}
//				});
//
//		Dialog dialog = alertBuilder.create();
//		dialog.show();
	}

	protected void gotoListContactHistory() {
		Log.i(TAG, "gotoListContactHistory");
		ContactListFragment contactListFragment = new ContactListFragment();
		if (getActivity() instanceof SkyMainActivity) {
			SkyMainActivity fca = (SkyMainActivity) getActivity();
			fca.switchContent(contactListFragment, "");
		}
	}

	private void openActionCall() {
		if (SkyUtils.isSimExist(getActivity())) {
			try {
				Intent intenCall = new Intent(Intent.ACTION_DIAL);//ACTION_CALL
				intenCall.setData(Uri.parse("tel:6565357704"));
				startActivity(intenCall);
			} catch (Exception e) {}
		}else{
			if (setting.isLangEng()) {
				SkyUtils.showDialog(getActivity(), getResources().getString(R.string.call_error), null);
			}else{
				SkyUtils.showDialog(getActivity(), getResources().getString(R.string.call_error_j), null);
			}
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
			startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			//finish();
			Log.i("Finished sending email...", "");
		} catch (android.content.ActivityNotFoundException ex) {
			ex.printStackTrace();
		}
	}

	@SuppressLint("ValidFragment")
	class PopupCatalog extends DialogFragment implements OnClickListener {
		private MTextView txtAboutSpmc, txtAboutAnnualFee, txtCancelMemberShip,txtUpdateContract,
				txtAboutPersonal,txtUpgrateRequest, txtOther, txtBack;

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {

			View view = inflater.inflate(R.layout.contact_popup_layout, null);
			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			txtAboutSpmc = (MTextView) view
					.findViewById(R.id.popup_about_spmc_id);
			txtAboutSpmc.setOnClickListener(this);

			txtAboutAnnualFee = (MTextView) view
					.findViewById(R.id.popup_about_annual_fee_id);
			txtAboutAnnualFee.setOnClickListener(this);

			txtCancelMemberShip = (MTextView) view
					.findViewById(R.id.popup_cancel_the_membership_id);
			txtCancelMemberShip.setOnClickListener(this);

			txtAboutPersonal = (MTextView) view
					.findViewById(R.id.popup_about_personal_infor_id);
			txtAboutPersonal.setOnClickListener(this);
			txtUpgrateRequest = (MTextView) view.findViewById(R.id.popup_about_personal_upgrate_request_id);
			txtUpgrateRequest.setOnClickListener(this);
			txtOther = (MTextView) view.findViewById(R.id.popup_other_id);
			txtOther.setOnClickListener(this);
			
			txtBack = (MTextView) view.findViewById(R.id.popup_back_id);
			txtBack.setOnClickListener(this);
			
			txtUpdateContract = (MTextView) view
					.findViewById(R.id.popup_update_contract_id);
			txtUpdateContract.setOnClickListener(this);

			return view;
		}
		
		private void changeFontColor(MTextView view){
			Log.i(TAG, "change background");
			view.setTextColor(getResources().getColor(R.color.color_popup_text_select));
		}
		
		@Override
		public void onClick(View v) {
			int idView = v.getId();
			Log.i(TAG, "LanguageID = " + languageId);
			languageId = setting.isLangEng();
			
			switch (idView) {
			case R.id.popup_about_spmc_id:
				if (languageId) {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_about_spmc));
				} else {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_about_spmc_j));
				}
				changeFontColor(txtAboutSpmc);
				
				contactType = 1;
				break;

			case R.id.popup_about_annual_fee_id:
				if (languageId) {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_about_annual_fee));
				} else {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_about_annual_fee_j));
				}
				changeFontColor(txtAboutAnnualFee);
				contactType = 2;
				break;
			case R.id.popup_cancel_the_membership_id:
				if (languageId) {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_about_cancel_membership));
				} else {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_about_cancel_membership_j));
				}
			
				contactType = 3;
				break;
			case R.id.popup_about_personal_infor_id:
				if (languageId) {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_about_personal_information));
				} else {
					txtCatalog
							.setText(getResources()
									.getString(
											R.string.contact_type_about_personal_information_j));
				}
				changeFontColor(txtAboutPersonal);
				contactType = 4;
				break;
			case R.id.popup_about_personal_upgrate_request_id:
				if (languageId) {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_about_upgrate_request));
				} else {
					txtCatalog
							.setText(getResources()
									.getString(
											R.string.contact_type_about_upgrate_request_j));
				}
				changeFontColor(txtUpgrateRequest);
				contactType = 6;
			break;		
			case R.id.popup_other_id:
				if (languageId) {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_other));
				} else {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_other_j));
				}
				changeFontColor(txtOther);
				contactType = 7;
				break;
			case R.id.popup_update_contract_id:
				if (languageId) {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_update_contract));
				} else {
					txtCatalog.setText(getResources().getString(
							R.string.contact_type_update_contract_j));
				}
				changeFontColor(txtUpdateContract);
				contactType = 8;
				break;
			case R.id.popup_back_id:

				break;
			default:
				break;
			}
			
			if (getDialog().isShowing()) {
				getDialog().dismiss();
			}
		}
	}

	
}
