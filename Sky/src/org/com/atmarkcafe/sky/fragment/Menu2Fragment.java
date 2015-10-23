package org.com.atmarkcafe.sky.fragment;

import java.util.List;

import org.com.atmarkcafe.sky.GcmBroadcastReceiver;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.dialog.ProfileChangeOptionDialog;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.SCREEN;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;

public class Menu2Fragment extends BaseFragment implements OnClickListener {

	private String TAG = "MenuFragment";
	Fragment contentFragment;
	private String nameFragment;
	private TextView txtMailBox, txtFaq, txtDownload, txtContactUs, txtNews, txtLanguageHeader, txtAccountHeader;
	private TextView txtAboutSpmc, txtInviteFrinds, txtService, txtLanguageJ, txtLanguageE, txtProfile, txtChangePass, txtLogout,txtGallery;
	private boolean languageID = false; // true is english,false is japan
	private Setting settings;
	private ImageView iconCheckLanguageE, iconCheckLanguageJ;
	private boolean isSWitchLanguage = false;
	private MailboxFragment mailboxFragment;
	private TextView txtVersion;
	private String pageSelect = "page_select";
	private SharedPreferences sharePref;
	private RelativeLayout rlQrcodelayou1t,rlBntOk,rlBtnCancel;
	private static final int ZBAR_SCANNER_REQUEST = 0;
	private static final int ZBAR_QR_SCANNER_REQUEST = 1;
	private LoadingView loading;
	@Override
	public void init(View view) {
		sharePref = getActivity().getSharedPreferences(SkyUtils.shareKey, Context.MODE_PRIVATE);
//		View menuView = inflater.inflate(R.layout.menu_view, null);
		((RelativeLayout) view.findViewById(R.id.menu_rl_mailbox_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_faq_id)).setOnClickListener(this);
//		((RelativeLayout) view.findViewById(R.id.menu_rl_download_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_contact_us_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_news_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_about_spmc_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_invite_friend_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_service_list_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_language_j_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_language_e_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_profile_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_changepass_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_logout_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_gallery_id)).setOnClickListener(this);
		// term
		((RelativeLayout) view.findViewById(R.id.menu_rl_trade_law_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_privacy_id)).setOnClickListener(this);
		((RelativeLayout) view.findViewById(R.id.menu_rl_term_service_id)).setOnClickListener(this);
		/*rlQrcodelayout = (RelativeLayout)view.findViewById(R.id.menu_rl_qr_id);
		rlQrcodelayout.setOnClickListener(this);*/
		// check open QRcode
		/*if (sharePref.getBoolean(SkyUtils.OPEN_QR_CODE, false)) {
			rlQrcodelayout.setVisibility(View.VISIBLE);
		}else{
			rlQrcodelayout.setVisibility(View.GONE);
		}*/
		
		// declare UI
		txtMailBox = (TextView) view.findViewById(R.id.menu_mail_box_id);
		txtFaq = (TextView) view.findViewById(R.id.menu_faq_id);
		txtContactUs = (TextView) view.findViewById(R.id.menu_contact_us_id);
		txtNews = (TextView) view.findViewById(R.id.menu_news_id);
		txtAboutSpmc = (TextView) view.findViewById(R.id.menu_about_spmc_id);
		txtInviteFrinds = (TextView) view.findViewById(R.id.menu_invite_id);
		txtService = (TextView) view.findViewById(R.id.menu_service_id);
		txtLanguageJ = (TextView) view.findViewById(R.id.menu_language_j_id);
		txtLanguageE = (TextView) view.findViewById(R.id.menu_language_e_id);
		txtProfile = (TextView) view.findViewById(R.id.menu_profile_id);
		txtChangePass = (TextView) view.findViewById(R.id.menu_changepass_id);
		txtLogout = (TextView) view.findViewById(R.id.menu_logout_id);
		txtLanguageHeader = (TextView) view.findViewById(R.id.menu_language_id);
		txtAccountHeader = (TextView) view.findViewById(R.id.menu_account_id);
		iconCheckLanguageJ = (ImageView) view.findViewById(R.id.img_10);
		iconCheckLanguageE = (ImageView) view.findViewById(R.id.img_111);
		txtGallery = (TextView)view.findViewById(R.id.menu_gallery_id);
		txtVersion = (TextView)view.findViewById(R.id.menu_version_txt_id);
		if (SkyUtils.FLAG_RELEASE) {
			txtVersion.setText(getVersionName());
		}
		
		loading = CommonAndroid.getView(view, R.id.loading);
		CommonAndroid.showView(false, loading);
		// end
		settings = new Setting(getActivity());
		languageID = settings.isLangEng();
		changeLanguageLabel();
//		getAllFragmentExits();
		
		/*Drawable myIcon = getResources().getDrawable( R.drawable.ico_sort );
		ColorFilter filter = new LightingColorFilter( Color.parseColor("#cce2ed"), android.graphics.Color.parseColor("#000000") );
//		myIcon.setColorFilter(filter);
		myIcon.setColorFilter(Color.parseColor("#a4a099") , PorterDuff.Mode.SRC_IN);
		ImageView img_8 = (ImageView) view.findViewById(R.id.img_8);
		img_8.setImageDrawable(myIcon);*/
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_sort, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_mail_m, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_gallery, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_fag, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_contact, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_news, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_info, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_invite, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_check, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_trade, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_pravicy, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_term, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_myprofile, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_password, getResources().getColor(R.color.icon_color_new));
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_logout, getResources().getColor(R.color.icon_color_new));
		
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_indi_white, getResources().getColor(R.color.icon_color_new));
	}

	public void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof SkyMainActivity) {
			SkyMainActivity fca = (SkyMainActivity) getActivity();
			fca.switchContent(fragment);
		}

	}

	private void switchFragment(BaseFragment fragment, String nameFragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof SkyMainActivity) {
			SkyMainActivity fca = (SkyMainActivity) getActivity();
			fca.switchContent(fragment, nameFragment);
		}

	}
	
	public void getAllFragmentExits(){
		List<Fragment> listFragment = getActivity().getSupportFragmentManager().getFragments();
		for (int i = 0; i < listFragment.size(); i++) {
			Log.i(TAG, "FR i = " + i + "" + listFragment.get(i).getTag());
		}
	}

	@Override
	public void onClick(View v) {
		int idView = v.getId();
		SCREEN screen = null;
		switch (idView) {

		case R.id.menu_rl_mailbox_id:
			contentFragment = new MailboxFragment();
			nameFragment = "MAIL_BOX";
			screen = SkyUtils.SCREEN.MAIL_BOX;
			MailboxFragment.flagBackFromDetailEmail = false;
			break;
		case R.id.menu_rl_faq_id:
			contentFragment = new FaqFragment();
			break;
		case R.id.menu_rl_gallery_id:
			contentFragment = new GalleryFragment();
			break;
		case R.id.menu_rl_contact_us_id:
			contentFragment = new ContactFragment();
			break;
		case R.id.menu_rl_news_id:
			contentFragment = new NewsFragment();
			break;
		case R.id.menu_rl_about_spmc_id:
			contentFragment = new AboutSpmcFragment();
			break;
		case R.id.menu_rl_invite_friend_id:
			contentFragment = new InviteFriendFragment();
			break;
		case R.id.menu_rl_service_list_id:
			SkyUtils.saveInt(getActivity(), pageSelect, 0);
			contentFragment = new HomeFragment();
			break;
		case R.id.menu_rl_language_e_id:
			languageID = true;
			isSWitchLanguage = true;
			settings.saveLang(R.id.eng);
			changeLanguageLabel();
//			contentFragment.onResume();
			break;
		case R.id.menu_rl_language_j_id:
			languageID = false;
			isSWitchLanguage = true;
			settings.saveLang(R.id.ja);
			changeLanguageLabel();
//			contentFragment.onResume();
			break;
		case R.id.menu_rl_profile_id:
			contentFragment = new ProfileFragment();
			break;
		case R.id.menu_rl_changepass_id:
			contentFragment = new ChangepwdFragment();
			break;
		case R.id.menu_rl_logout_id:
			contentFragment = null;
			logout();
			break;
		case R.id.menu_rl_trade_law_id:
			Log.i(TAG, "menu_trade_law_id");
			contentFragment = new TradeFragment();
			break;
		case R.id.menu_rl_privacy_id:
			Log.i(TAG, "menu_privacy_id");
			contentFragment = new PravicyFragment();
			break;
		case R.id.menu_rl_term_service_id:
			Log.i(TAG, "menu_rl_term_service_id");
			contentFragment = new TermFragment();
			break;
		/*case R.id.menu_rl_qr_id:
			LogUtils.i(TAG, "Open QRcode Function");
			launchQRScanner(v);
			//SkyMainActivity.sm.toggle();
			break;*/
		default:
			break;
		}


		if (isSWitchLanguage) {
			isSWitchLanguage = false;
			return;
		}else{
			if (contentFragment != null) {
				switchFragment(contentFragment);
				isSWitchLanguage = false;
			}
		}
	}

	private void logout() {
		// TODO Auto-generated method stub
		String[] items;
		String title = getActivity().getResources().getString(R.string.msg_logout_confirm);
		items = new String[] { getActivity().getResources().getString(R.string.msg_no),getActivity().getResources().getString(R.string.msg_yes) };
		if (!new Setting(getActivity()).isLangEng()) {
			title = getActivity().getResources().getString(R.string.msg_logout_confirm_j);
			items = new String[] { getActivity().getResources().getString(R.string.msg_no_j) ,getActivity().getResources().getString(R.string.msg_yes_j) };
		}
		
		new ProfileChangeOptionDialog(getActivity(), items, title, new DialogInterface.OnClickListener() {
			@SuppressLint("NewApi")
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == 1) {
					GcmBroadcastReceiver.resetUserInfo(getActivity());
					GcmBroadcastReceiver.notifiServiceStop(getActivity());
					new Account(getActivity()).removeToken(getActivity());
					finish();
					Intent intent = new Intent(getActivity(), RootActivity.class);
					intent.putExtra(SkyUtils.KEY.KEY_SCREEN, SkyUtils.SCREEN.LOGIN);
					startActivity(intent);
				} else if (which == 0) {
					// no
				}

			}
		}).show();
	}

	private CheerzServiceCallBack callbacklogout = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
			CommonAndroid.hiddenKeyBoard(getActivity());
		};

		public void onError(String message) {
			if (!isFinish()) {
				CommonAndroid.showView(false, loading);
						new Account(getActivity()).removeToken(getActivity());
						GcmBroadcastReceiver.resetUserInfo(getActivity());
						Intent intent = new Intent(getActivity(), RootActivity.class);
						intent.putExtra(SkyUtils.KEY.KEY_SCREEN, SkyUtils.SCREEN.LOGIN);
						startActivity(intent);
						finish();
			}
		};

		public void onSucces(String response) {
			if (!isFinish()) {
				CommonAndroid.showView(false, loading);
				JSONObject mainJsonObject;
						new Account(getActivity()).removeToken(getActivity());
						GcmBroadcastReceiver.resetUserInfo(getActivity());
						Intent intent = new Intent(getActivity(), RootActivity.class);
						intent.putExtra(SkyUtils.KEY.KEY_SCREEN, SkyUtils.SCREEN.LOGIN);
						startActivity(intent);
						finish();
			}
		};
	};

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

	/**
	 * change language label for menu left
	 */
	private void changeLanguageLabel() {
		Log.i(TAG, "changeLanguageLabel : " + languageID);
		if (languageID) {
			txtMailBox.setText(getResources().getString(R.string.menu_mailbox));
			txtFaq.setText(getResources().getString(R.string.menu_faq));
//			txtDownload.setText(getResources().getString(R.string.menu_download));
			txtContactUs.setText(getResources().getString(R.string.menu_contact_us));
			txtNews.setText(getResources().getString(R.string.menu_news));
			txtAboutSpmc.setText(getResources().getString(R.string.menu_about_spmc));
			txtInviteFrinds.setText(getResources().getString(R.string.menu_invite_friend));
			txtService.setText(getResources().getString(R.string.menu_service_list));
			txtProfile.setText(getResources().getString(R.string.menu_my_profile));
			txtChangePass.setText(getResources().getString(R.string.menu_change_pass));
			txtLogout.setText(getResources().getString(R.string.menu_logout));
			txtLanguageHeader.setText(getResources().getString(R.string.language));
			txtAccountHeader.setText(getResources().getString(R.string.account));
			txtGallery.setText(getResources().getString(R.string.menu_gallery));
			iconCheckLanguageE.setVisibility(View.VISIBLE);
			iconCheckLanguageJ.setVisibility(View.INVISIBLE);
		} else {
			txtMailBox.setText(getResources().getString(R.string.menu_mailbox_j));
			txtFaq.setText(getResources().getString(R.string.menu_faq_j));
//			txtDownload.setText(getResources().getString(R.string.menu_download_j));
			txtContactUs.setText(getResources().getString(R.string.menu_contact_us_j));
			txtNews.setText(getResources().getString(R.string.menu_news_j));
			txtAboutSpmc.setText(getResources().getString(R.string.menu_about_spmc_j));
			txtInviteFrinds.setText(getResources().getString(R.string.menu_invite_friend_j));
			txtService.setText(getResources().getString(R.string.menu_service_list_j));
			txtProfile.setText(getResources().getString(R.string.menu_my_profile_j));
			txtChangePass.setText(getResources().getString(R.string.menu_change_pass_j));
			txtLogout.setText(getResources().getString(R.string.menu_logout_j));
			txtLanguageHeader.setText(getResources().getString(R.string.language_j));
			txtAccountHeader.setText(getResources().getString(R.string.account_j));
			txtGallery.setText(getResources().getString(R.string.menu_gallery_j));
			iconCheckLanguageE.setVisibility(View.INVISIBLE);
			iconCheckLanguageJ.setVisibility(View.VISIBLE);
		}
	}
	
	private String getVersionName(){
		String versionName = null;
		try {
			versionName = getActivity().getPackageManager()
				    .getPackageInfo(getActivity().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionName;
	}
	
	private int getVersionCode(){
		int versionCode = 1;
		try {
			versionCode = getActivity().getPackageManager()
				    .getPackageInfo(getActivity().getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionCode;
	}
	
	@Override
	public int getLayout() {
		return R.layout.menu_view;
	}

	@Override
	public void onChangeLanguage() {
		// TODO Auto-generated method stub
		if(new Setting(getActivity()).isLangEng()){
			iconCheckLanguageE.setVisibility(View.VISIBLE);
			iconCheckLanguageJ.setVisibility(View.INVISIBLE);
		}else{
			iconCheckLanguageE.setVisibility(View.INVISIBLE);
			iconCheckLanguageJ.setVisibility(View.VISIBLE);
		}
	}
	
	private void launchQRScanner(View v) {
		/*Log.i(TAG, "launchQRScanner");
        if (isCameraAvailable()) {
            Intent intent = new Intent(getActivity(), ZBarScannerActivity.class);
            intent.putExtra(ZBarConstants.SCAN_MODES, new int[]{Symbol.QRCODE});
            startActivityForResult(intent, ZBAR_SCANNER_REQUEST);
        } else {
//            Toast.makeText(this, "Rear Facing Camera Unavailable", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Camera Unavailable");
        }*/
    }

    private boolean isCameraAvailable() {
        PackageManager pm = getActivity().getPackageManager();
        return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	/*switch (requestCode) {
        case ZBAR_SCANNER_REQUEST:
        case ZBAR_QR_SCANNER_REQUEST:
            if (resultCode == getActivity().RESULT_OK) {
                Log.i(TAG,  "Scan Result = " + data.getStringExtra(ZBarConstants.SCAN_RESULT));
                String msg = data.getStringExtra(ZBarConstants.SCAN_RESULT);
                openDialogConfirmResult(msg);
            } else if(resultCode == getActivity().RESULT_CANCELED && data != null) {
                String error = data.getStringExtra(ZBarConstants.ERROR_INFO);
                if(!TextUtils.isEmpty(error)) {
                    Log.i(TAG,  error);
                }
            }
            break;
    }*/
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    private void openDialogConfirmResult(final String msg){
    	final Dialog dialog = new Dialog(getActivity());
    	dialog.setContentView(R.layout.vertical_dialog_layout);
    	TextView txtMsg = (TextView)dialog.findViewById(R.id.popup_txt_msg_id);
    	txtMsg.setText(msg);
    	rlBntOk = (RelativeLayout)dialog.findViewById(R.id.popup_rl_ok_id);
    	rlBtnCancel = (RelativeLayout)dialog.findViewById(R.id.popup_rl_cancel_id);
    	rlBntOk.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// goto service list
//				contentFragment = new TradeFragment();
				contentFragment = new ServiceList2Fragment(msg);
				switchFragment(contentFragment);
				dialog.dismiss();
			}
		});
    	
    	rlBtnCancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
    	
    	dialog.show();
    }
    
}
