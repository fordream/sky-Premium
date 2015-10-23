
//package org.com.atmarkcafe.sky.fragment;
//
//import java.util.HashMap;
//import java.util.List;
//
//import org.com.atmarkcafe.sky.GcmBroadcastReceiver;
//import com.SkyPremiumLtd.SkyPremium.R;
//import org.com.atmarkcafe.sky.RootActivity;
//import org.com.atmarkcafe.sky.SkyMainActivity;
//import org.com.atmarkcafe.sky.dialog.ProfileChangeOptionDialog;
//import org.json.JSONObject;
//
//import z.lib.base.CheerzServiceCallBack;
//import z.lib.base.CommonAndroid;
//import z.lib.base.LogUtils;
//import z.lib.base.SkyUtils;
//import z.lib.base.SkyUtils.API;
//import z.lib.base.callback.RestClient.RequestMethod;
//import android.annotation.SuppressLint;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.pm.PackageManager.NameNotFoundException;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.acv.cheerz.base.view.LoadingView;
//import com.acv.cheerz.db.Setting;
//
//public class MenuFragment extends Fragment implements OnClickListener {
//
//	private String TAG = "MenuFragment";
//	Fragment contentFragment;
//	private String nameFragment;
//	private TextView txtMailBox, txtFaq, txtDownload, txtContactUs, txtNews, txtLanguageHeader, txtAccountHeader;
//	private TextView txtAboutSpmc, txtInviteFrinds, txtService, txtLanguageJ, txtLanguageE, txtProfile, txtChangePass, txtLogout,txtGallery;
//	private boolean languageID = false; // true is english,false is japan
//	private Setting settings;
//	private ImageView iconCheckLanguageE, iconCheckLanguageJ;
//	private boolean isSWitchLanguage = false;
//	private MailboxFragment mailboxFragment;
//	private TextView txtVersion;
//	
//	private LoadingView loading;
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		View menuView = inflater.inflate(R.layout.menu_view, null);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_mailbox_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_faq_id)).setOnClickListener(this);
////		((RelativeLayout) menuView.findViewById(R.id.menu_rl_download_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_contact_us_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_news_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_about_spmc_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_invite_friend_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_service_list_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_language_j_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_language_e_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_profile_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_changepass_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_logout_id)).setOnClickListener(this);
//		((RelativeLayout) menuView.findViewById(R.id.menu_rl_gallery_id)).setOnClickListener(this);
//
//		// declare UI
//		txtMailBox = (TextView) menuView.findViewById(R.id.menu_mail_box_id);
//		txtFaq = (TextView) menuView.findViewById(R.id.menu_faq_id);
////		txtDownload = (TextView) menuView.findViewById(R.id.menu_download_id);
//		txtContactUs = (TextView) menuView.findViewById(R.id.menu_contact_us_id);
//		txtNews = (TextView) menuView.findViewById(R.id.menu_news_id);
//		txtAboutSpmc = (TextView) menuView.findViewById(R.id.menu_about_spmc_id);
//		txtInviteFrinds = (TextView) menuView.findViewById(R.id.menu_invite_id);
//		txtService = (TextView) menuView.findViewById(R.id.menu_service_id);
//		txtLanguageJ = (TextView) menuView.findViewById(R.id.menu_language_j_id);
//		txtLanguageE = (TextView) menuView.findViewById(R.id.menu_language_e_id);
//		txtProfile = (TextView) menuView.findViewById(R.id.menu_profile_id);
//		txtChangePass = (TextView) menuView.findViewById(R.id.menu_changepass_id);
//		txtLogout = (TextView) menuView.findViewById(R.id.menu_logout_id);
//		txtLanguageHeader = (TextView) menuView.findViewById(R.id.menu_language_id);
//		txtAccountHeader = (TextView) menuView.findViewById(R.id.menu_account_id);
//		iconCheckLanguageJ = (ImageView) menuView.findViewById(R.id.img_10);
//		iconCheckLanguageE = (ImageView) menuView.findViewById(R.id.img_111);
//		txtGallery = (TextView)menuView.findViewById(R.id.menu_gallery_id);
//		txtVersion = (TextView)menuView.findViewById(R.id.menu_version_txt_id);
//		if (SkyUtils.FLAG_RELEASE) {
//			txtVersion.setText(getVersionName());
//		}
//		
//		loading = CommonAndroid.getView(menuView, R.id.loading);
//		CommonAndroid.showView(false, loading);
//		// end
//		settings = new Setting(getActivity());
//		languageID = settings.isLangEng();
//		changeLanguageLabel();
//		getAllFragmentExits();
//		return menuView;
//	}
//
//	public void switchFragment(Fragment fragment) {
//		if (getActivity() == null)
//			return;
//
//		if (getActivity() instanceof SkyMainActivity) {
//			SkyMainActivity fca = (SkyMainActivity) getActivity();
//			fca.switchContent(fragment);
//		}
//
//	}
//
//	private void switchFragment(Fragment fragment, String nameFragment) {
//		if (getActivity() == null)
//			return;
//
//		if (getActivity() instanceof SkyMainActivity) {
//			SkyMainActivity fca = (SkyMainActivity) getActivity();
//			fca.switchContent(fragment, nameFragment);
//		}
//
//	}
//	
//	public void getAllFragmentExits(){
//		List<Fragment> listFragment = getActivity().getSupportFragmentManager().getFragments();
//		for (int i = 0; i < listFragment.size(); i++) {
//			Log.i(TAG, "FR i = " + i + "" + listFragment.get(i).getTag());
//		}
//	}
//
//	@Override
//	public void onClick(View v) {
//		int idView = v.getId();
//
//		switch (idView) {
//
//		case R.id.menu_rl_mailbox_id:
//			contentFragment = new MailboxFragment();
//			
//			break;
//		case R.id.menu_rl_faq_id:
//			contentFragment = new FaqFragment();
//			break;
//		case R.id.menu_rl_gallery_id:
//			contentFragment = new GalleryFragment();
//			break;
//		case R.id.menu_rl_contact_us_id:
//			contentFragment = new ContactFragment();
//			break;
//		case R.id.menu_rl_news_id:
//			contentFragment = new NewsFragment();
//			break;
//		case R.id.menu_rl_about_spmc_id:
//			contentFragment = new AboutSpmcFragment();
//			break;
//		case R.id.menu_rl_invite_friend_id:
//			contentFragment = new InviteFriendFragment();
//			break;
//		case R.id.menu_rl_service_list_id:
//			contentFragment = new HomeFragment();
//			break;
//		case R.id.menu_rl_language_e_id:
//			languageID = true;
//			isSWitchLanguage = true;
//			changeLanguageLabel();
//			settings.saveLang(R.id.eng);
////			contentFragment.onResume();
//			break;
//		case R.id.menu_rl_language_j_id:
//			languageID = false;
//			isSWitchLanguage = true;
//			changeLanguageLabel();
//			settings.saveLang(R.id.ja);
////			contentFragment.onResume();
//			break;
//		case R.id.menu_rl_profile_id:
//			contentFragment = new ProfileFragment();
//			break;
//		case R.id.menu_rl_changepass_id:
//			contentFragment = new ChangepwdFragment();
//			break;
//		case R.id.menu_rl_logout_id:
//			contentFragment = null;
//			logout();
//			break;
//		default:
//			break;
//		}
//
//
//		if (isSWitchLanguage) {
//			isSWitchLanguage = false;
//			return;
//		}else{
//			if (contentFragment != null) {
//				switchFragment(contentFragment,nameFragment);
//				isSWitchLanguage = false;
//			}
//		}
//	}
//
//	private void logout() {
//		// TODO Auto-generated method stub
//		String[] items;
//		String title = getActivity().getResources().getString(R.string.msg_logout_confirm);
//		items = new String[] { getActivity().getResources().getString(R.string.msg_no),getActivity().getResources().getString(R.string.msg_yes) };
//		if (!new Setting(getActivity()).isLangEng()) {
//			title = getActivity().getResources().getString(R.string.msg_logout_confirm_j);
//			items = new String[] { getActivity().getResources().getString(R.string.msg_no_j) ,getActivity().getResources().getString(R.string.msg_yes_j) };
//		}
//		new ProfileChangeOptionDialog(getActivity(), items, title, new DialogInterface.OnClickListener() {
//			@SuppressLint("NewApi")
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				if (which == 1) {
//					// yes-->logout
//					HashMap<String, String> inputs = new HashMap<String, String>();
//					SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_LOGOUT, inputs, callbacklogout);
//				} else if (which == 0) {
//					// no
//				}
//
//			}
//		}).show();
//	}
//
//	private CheerzServiceCallBack callbacklogout = new CheerzServiceCallBack() {
//
//		public void onStart() {
//			CommonAndroid.showView(true, loading);
//			CommonAndroid.hiddenKeyBoard(getActivity());
//		};
//
//		public void onError(String message) {
//			if (!isFinish()) {
//				CommonAndroid.showView(false, loading);
//				CommonAndroid.showDialog(getActivity(), "" + message, new DialogInterface.OnClickListener() {
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						GcmBroadcastReceiver.unregisterUserInfo(getActivity());
//						Intent intent = new Intent(getActivity(), RootActivity.class);
//						intent.putExtra(SkyUtils.KEY.KEY_SCREEN, SkyUtils.SCREEN.LOGIN);
//						startActivity(intent);
//						finish();
//					}
//				});
//			}
//		};
//
//		public void onSucces(String response) {
//			if (!isFinish()) {
//				CommonAndroid.showView(false, loading);
//				JSONObject mainJsonObject;
//				try {
//					mainJsonObject = new JSONObject(response);
//					LogUtils.e(TAG, "logout" + mainJsonObject.toString());
////					String is_succes = CommonAndroid.getString(mainJsonObject, SkyUtils.KEY.is_success);
////					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
//						GcmBroadcastReceiver.unregisterUserInfo(getActivity());
//						Intent intent = new Intent(getActivity(), RootActivity.class);
//						intent.putExtra(SkyUtils.KEY.KEY_SCREEN, SkyUtils.SCREEN.LOGIN);
//						startActivity(intent);
//						finish();
////					}
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//
//			}
//		};
//	};
//
//	public boolean isFinish() {
//		if (getActivity() == null) {
//			return true;
//		}
//
//		return getActivity().isFinishing();
//	}
//
//	public void finish() {
//		if (getActivity() != null) {
//			getActivity().finish();
//		}
//	}
//
//	/**
//	 * change language label for menu left
//	 */
//	private void changeLanguageLabel() {
//		Log.i(TAG, "changeLanguageLabel : " + languageID);
//		if (languageID) {
//			txtMailBox.setText(getResources().getString(R.string.menu_mailbox));
//			txtFaq.setText(getResources().getString(R.string.menu_faq));
////			txtDownload.setText(getResources().getString(R.string.menu_download));
//			txtContactUs.setText(getResources().getString(R.string.menu_contact_us));
//			txtNews.setText(getResources().getString(R.string.menu_news));
//			txtAboutSpmc.setText(getResources().getString(R.string.menu_about_spmc));
//			txtInviteFrinds.setText(getResources().getString(R.string.menu_invite_friend));
//			txtService.setText(getResources().getString(R.string.menu_service_list));
//			txtProfile.setText(getResources().getString(R.string.menu_my_profile));
//			txtChangePass.setText(getResources().getString(R.string.menu_change_pass));
//			txtLogout.setText(getResources().getString(R.string.menu_logout));
//			txtLanguageHeader.setText(getResources().getString(R.string.language));
//			txtAccountHeader.setText(getResources().getString(R.string.account));
//			txtGallery.setText(getResources().getString(R.string.menu_gallery));
//			iconCheckLanguageE.setVisibility(View.VISIBLE);
//			iconCheckLanguageJ.setVisibility(View.INVISIBLE);
//		} else {
//			txtMailBox.setText(getResources().getString(R.string.menu_mailbox_j));
//			txtFaq.setText(getResources().getString(R.string.menu_faq_j));
////			txtDownload.setText(getResources().getString(R.string.menu_download_j));
//			txtContactUs.setText(getResources().getString(R.string.menu_contact_us_j));
//			txtNews.setText(getResources().getString(R.string.menu_news_j));
//			txtAboutSpmc.setText(getResources().getString(R.string.menu_about_spmc_j));
//			txtInviteFrinds.setText(getResources().getString(R.string.menu_invite_friend_j));
//			txtService.setText(getResources().getString(R.string.menu_service_list_j));
//			txtProfile.setText(getResources().getString(R.string.menu_my_profile_j));
//			txtChangePass.setText(getResources().getString(R.string.menu_change_pass_j));
//			txtLogout.setText(getResources().getString(R.string.menu_logout_j));
//			txtLanguageHeader.setText(getResources().getString(R.string.language_j));
//			txtAccountHeader.setText(getResources().getString(R.string.account_j));
//			txtGallery.setText(getResources().getString(R.string.menu_gallery_j));
//			iconCheckLanguageE.setVisibility(View.INVISIBLE);
//			iconCheckLanguageJ.setVisibility(View.VISIBLE);
//		}
//	}
//	
//	private String getVersionName(){
//		String versionName = null;
//		try {
//			versionName = getActivity().getPackageManager()
//				    .getPackageInfo(getActivity().getPackageName(), 0).versionName;
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return versionName;
//	}
//	
//	private int getVersionCode(){
//		int versionCode = 1;
//		try {
//			versionCode = getActivity().getPackageManager()
//				    .getPackageInfo(getActivity().getPackageName(), 0).versionCode;
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return versionCode;
//	}
//	
//}
