package org.com.atmarkcafe.sky;


import java.util.Timer;
import java.util.TimerTask;

import org.com.atmarkcafe.sky.fragment.AboutSpmcFragment;
import org.com.atmarkcafe.sky.fragment.ContactFragment;
import org.com.atmarkcafe.sky.fragment.GalleryFragment;
import org.com.atmarkcafe.sky.fragment.HomeFragment;
import org.com.atmarkcafe.sky.fragment.MailboxFragment;
import org.com.atmarkcafe.sky.fragment.Menu2Fragment;
import org.com.atmarkcafe.sky.fragment.NewsFragment;
import org.com.atmarkcafe.sky.fragment.ProfileFragment;

import z.lib.base.BaseFragment;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.SCREEN;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Account;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
//import org.com.atmarkcafe.sky.fragment.MenuFragment;

public class SkyMainActivity extends BaseSkyActivity implements OnClickListener {

	private android.support.v4.app.Fragment mContent;

	private ImageButton btnHome;
	public static final String EXTRA_CHECK_BACK_CONTACTUS = "EXTRA_CHECK_BACK_CONTACTUS";
	public static final String EXTRA_CHECK_BACK_PROFILE = "EXTRA_CHECK_BACK_PROFILE";
	public static final String EXTRA_CHECK_BACK_NEW = "EXTRA_CHECK_BACK_NEW";
	public static final String EXTRA_CHECK_BACK_MAIL = "EXTRA_CHECK_BACK_MAIL";
	public static final String EXTRA_CHECK_BACK_HOME = "EXTRA_CHECK_BACK_HOME";
	public static boolean FLAG_CHECK_OPEN_MAIL_BOX_FROM_CHAT_HEADER = false;
	public static boolean FLAG_CHECK_OPEN_NEW_FROM_CHAT_HEADER = false;
	public static boolean FLAG_CHECK_OPEN_CONTACT = false;
	public static boolean FLAG_CHECK_ONLOADBEADGER = false;
	public static final String FLAG_CHECK_OPEN_ABOUT = "FLAG_CHECK_OPEN_ABOUT";
	public static final String FLAG_CHECK_OPEN_GALLERY = "FLAG_CHECK_OPEN_GALLERY";
	private String TAG = "SkyMainActivity";
	public static SlidingMenu sm;
	public static int screen_width;
	public static int screen_height;
	private String pageSelect = "page_select";
	
	public static boolean FLAG_EXTRA_CHECK_BACK_PROFILE = false;
	public static SlidingMenu sMenu;
	
	IntentFilter intentFilter = new IntentFilter("com.atmarkcafe.CUSTOM_INTENT");
	
	public SkyMainActivity() {
		super(R.string.sky);
	}
	
	BroadcastReceiver Receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(final Context context, Intent intent) {
			String message = intent.getStringExtra("msg");
			String token = "";
			try {
				token = new Account(context).getToken();
			} catch (Exception e) {}
			Log.i(TAG, "Name Fragment: " + context.getClass().getSimpleName() + "::" +token);
			if (!"".equals(token)) {
				SkyUtils.showDialogCustom(context, message,R.string.contract_not_tranf_yet_logout,R.string.contract_not_tranf_yet_logout_j, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Account acc = new Account(getApplicationContext());
						acc.removeToken(context);
						Intent intent = new Intent(getApplicationContext(), RootActivity.class);
						intent.putExtra(SkyUtils.KEY.KEY_SCREEN, SkyUtils.SCREEN.LOGIN);
						intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
						startActivity(intent);
						SkyMainActivity.this.finish();
						
						}
				});
			}
				
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FLAG_EXTRA_CHECK_BACK_PROFILE = false;
		sMenu = getSlidingMenu();
		SharedPreferences sharePref = getApplication().getSharedPreferences(SkyUtils.shareKey, getApplication().MODE_PRIVATE);
		CommonAndroid.updateBadgeLauncher(getApplication(), sharePref.getInt(SkyUtils.BADGE_EMAIL, 0) + sharePref.getInt(SkyUtils.BADGE_NEWS, 0));
		int count_xx = sharePref.getInt(SkyUtils.BADGE_EMAIL, 0) + sharePref.getInt(SkyUtils.BADGE_NEWS, 0);
		LogUtils.i(TAG, "count_xx==" + count_xx);
		// setSlidingActionBarEnabled(true);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
	    //FLAG_CHECK_OPEN_MAIL_BOX_FROM_CHAT_HEADER = sharePref.getBoolean("OPEN_MAIL", false);
	    //FLAG_CHECK_OPEN_NEW_FROM_CHAT_HEADER = sharePref.getBoolean("OPEN_NEWS", false);
	    Log.i(TAG, "FLAG_CHECK_OPEN_MAIL_BOX_FROM_CHAT_HEADER = " + FLAG_CHECK_OPEN_MAIL_BOX_FROM_CHAT_HEADER);
	    Log.i(TAG, "FLAG_CHECK_OPEN_NEW_FROM_CHAT_HEADER = " + FLAG_CHECK_OPEN_NEW_FROM_CHAT_HEADER);
	    disableGestureMenu(true);
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new HomeFragment();
		
		try {
			String back_check = (String) getIntent().getExtras().get("redirect");
			LogUtils.i(TAG, "bar set back===" + back_check);
			if(back_check.equals("1")){
				mContent = new MailboxFragment();
			}else if(back_check.equals("2")){
				mContent = new NewsFragment();
			}
		} catch (Exception e1) {
			LogUtils.e(TAG, "Err back");
		}
		
		try {
			String back_home = (String) getIntent().getExtras().get("EXTRA_CHECK_BACK_HOME");
			LogUtils.i(TAG, "set back===" + back_home);
			if(back_home.equals(EXTRA_CHECK_BACK_HOME)){
				mContent = new HomeFragment();
//				FLAG_CHECK_OPEN_NEW_FROM_CHAT_HEADER = true;
			}
		} catch (Exception e1) {
			LogUtils.e(TAG, "Err back===EXTRA_CHECK_BACK_MAIL");
		}
		
		try {
			String back_mail = (String) getIntent().getExtras().get("EXTRA_CHECK_BACK_MAIL");
			LogUtils.i(TAG, "set back===" + back_mail);
			if(back_mail.equals(EXTRA_CHECK_BACK_MAIL)){
				mContent = new MailboxFragment();
//				sharePref.edit().putBoolean("OPEN_MAIL", false).commit();
			}
		} catch (Exception e1) {
			LogUtils.e(TAG, "Err back===EXTRA_CHECK_BACK_MAIL");
		}
		try {
			if (FLAG_CHECK_OPEN_MAIL_BOX_FROM_CHAT_HEADER) {
				mContent = new MailboxFragment();
				FLAG_CHECK_OPEN_MAIL_BOX_FROM_CHAT_HEADER = false;
//				sharePref.edit().putBoolean("OPEN_MAIL", false).commit();
			}
		} catch (Exception e1) {
			LogUtils.e(TAG, "Err back===EXTRA_CHECK_BACK_MAIL");
		}
		
		try {
			String back_new = (String) getIntent().getExtras().get("EXTRA_CHECK_BACK_NEW");
			LogUtils.i(TAG, "set back===" + back_new);
			if(back_new.equals(EXTRA_CHECK_BACK_NEW)){
				mContent = new NewsFragment();
//				sharePref.edit().putBoolean("OPEN_NEWS", false).commit();
			}
		} catch (Exception e1) {
			LogUtils.e(TAG, "Err back===EXTRA_CHECK_BACK_NEW");
		}
		
		try {
			if(FLAG_CHECK_OPEN_NEW_FROM_CHAT_HEADER){
				mContent = new NewsFragment();
				FLAG_CHECK_OPEN_NEW_FROM_CHAT_HEADER = false;
//				sharePref.edit().putBoolean("OPEN_NEWS", false).commit();
			}
		} catch (Exception e1) {
			LogUtils.e(TAG, "Err back===EXTRA_CHECK_BACK_NEW");
		}
		
		try {
			String back_profile = (String) getIntent().getExtras().get("EXTRA_CHECK_BACK_PROFILE");
			LogUtils.i(TAG, "set back===" + back_profile);
			if(back_profile.equals(EXTRA_CHECK_BACK_PROFILE)){
				disableGestureMenu(false);
				FLAG_EXTRA_CHECK_BACK_PROFILE = true;
				mContent = new ProfileFragment();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
		}
		
		
		try {
			if(FLAG_CHECK_OPEN_CONTACT){
				mContent = new ContactFragment();
				FLAG_CHECK_OPEN_CONTACT = false;
				LogUtils.i(TAG, "bar set back===+++++++++++++FLAG_CHECK_OPEN_CONTACT");
			}
		} catch (Exception e1) {
			LogUtils.e(TAG, "Err back===FLAG_CHECK_OPEN_CONTACT");
		}
		
		
		sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		
		// set the Above View
		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent,"HOME").commit();
		
		// set the Behind View
		setBehindContentView(R.layout.menu_frame);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame, new Menu2Fragment()).commit();

//		btnHome = (ImageButton) findViewById(R.id.header_btn_left);
//		btnHome.setOnClickListener(this);
		
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		screen_height = metrics.heightPixels;
		screen_width = metrics.widthPixels;
		
		SkyUtils.saveInt(SkyMainActivity.this, pageSelect, 0);
		startTimer();
	}
	private Timer mTimer;
	private TimerTask mTimerTask;
	private Handler mHandler = new Handler();
	
	private void startTimer(){
		mTimer = new Timer();
		initTimerTask();
		mTimer.schedule(mTimerTask, 2000);
	}
	
	private void stopTimerTask(){
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
	}
	int count = 0;
	
	private void initTimerTask(){
		Log.i(TAG, "initTimerTask");
		mTimerTask = new TimerTask() {
			
			@Override
			public void run() {
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
//						Toast.makeText(SkyMainActivity.this, "count = " + count++, 6000).show();
					}
				});
				
			}
		};
	}
	
	@Override
	protected void onResume() {
		SharedPreferences sharePref = getApplication().getSharedPreferences(SkyUtils.shareKey, getApplication().MODE_PRIVATE);
		CommonAndroid.updateBadgeLauncher(getApplication(), sharePref.getInt(SkyUtils.BADGE_EMAIL, 0) + sharePref.getInt(SkyUtils.BADGE_NEWS, 0));
		int count_xx = sharePref.getInt(SkyUtils.BADGE_EMAIL, 0) + sharePref.getInt(SkyUtils.BADGE_NEWS, 0);
		LogUtils.i(TAG, "count_xx2==" + count_xx);
		
		try {
			String back_check = (String) getIntent().getExtras().get("redirect");
			LogUtils.i(TAG, "bar set back===+++++++++++++" + back_check);
			if(back_check.equals("1")){
				mContent = new MailboxFragment();
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent,"HOME").commit();
			}else if(back_check.equals("2")){
				mContent = new NewsFragment();
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mContent,"HOME").commit();
			}
			
		} catch (Exception e1) {
			LogUtils.e(TAG, "Err back");
		}
		try {
			this.registerReceiver(Receiver, intentFilter);
		} catch (Exception e) {}
		super.onResume();
	}
	public static void disableGestureMenu(boolean isAble){
		try {
			sMenu.setSlidingEnabled(isAble);
		} catch (Exception e) {}
	}

	public void switchContent(android.support.v4.app.Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment).commit();
		getSlidingMenu().showContent();
	}
	
	public void switchContent(android.support.v4.app.Fragment fragment,String nameFragment) {
		mContent = fragment;
//		getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, fragment,nameFragment).commit();
//		getSlidingMenu().showContent();
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
		//transaction.setCustomAnimations(R.anim.right_in, R.anim.right_in, R.anim.right_out, R.anim.right_out);
		transaction.replace(R.id.content_frame, fragment,nameFragment);
//		if (!nameFragment.equalsIgnoreCase("NO_BACK")) {
//		}
		transaction.addToBackStack(nameFragment);
		transaction.commit();
		getSlidingMenu().showContent();
	}
	
	public static void startFragment(BaseFragment baseFragment, Bundle bundle){
		
	}

	public static void hideSoftKeyboard(Activity activity,View v) {
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    if (inputMethodManager.isAcceptingText()) {
	    	inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
		}
	}
	
	public static void setupUI1(final Activity activity,View view) {

	    //Set up touch listener for non-text box views to hide keyboard.
	    if(!(view instanceof EditText)) {

	        view.setOnTouchListener(new OnTouchListener() {

	            public boolean onTouch(View v, MotionEvent event) {
	            	hideSoftKeyboard(activity,v);
	                return false;
	            }

	        });
	    }
	    
	}
	
	@Override
	public void onClick(View v) {
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			toggle();
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		Log.i(TAG , "Mainback");
//		List<Fragment> listFragments = getSupportFragmentManager().getFragments();
//		String tag = null;
//		listFragments.remove(listFragments.size()-1);
//		for (int i = 0; i < listFragments.size(); i++) {
//			Log.i(TAG, "tag : " + i + " : " + listFragments.get(i).getTag());
//			tag = listFragments.get(i).getTag();
//			
//			if ("MAIL_BOX".equalsIgnoreCase(tag)) {
//				mContent = new MailboxFragment();
//				//switchContent(mContent, tag);
//			}
//		}
		if(FLAG_EXTRA_CHECK_BACK_PROFILE){
			FLAG_EXTRA_CHECK_BACK_PROFILE = false;
			Bundle extras = new Bundle();
			startActivityForResult(SCREEN.EC__PRODUCT_LIST, extras);
		}
		return;
	}
	
	public void startActivityForResult(SCREEN screen, Bundle extras) {
		if (extras == null) {
			extras = new Bundle();
		}
		extras.putSerializable(SkyUtils.KEY.KEY_SCREEN, screen);
		extras.putBoolean(SkyUtils.KEY.KEY_SCREEN_TAG, true);
		Intent intent = new Intent(this, RootActivity.class);//
		intent.putExtras(extras);

		this.startActivityForResult(intent, 0);
		this.overridePendingTransition(R.anim.right_in, 0);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.i(TAG, "onActivityResult=========requestCode:" + requestCode);
		if(resultCode == RESULT_OK){
			
			try {
				String videoback = (String) data.getExtras().get("videoback");
				if(videoback.equals(FLAG_CHECK_OPEN_ABOUT)){
					mContent = new AboutSpmcFragment();
					switchContent(mContent, "AboutSpmcFragment");
				}
			} catch (Exception e) {}
			
			try {
				String videoback = (String) data.getExtras().get("videoback");
				if(videoback.equals(FLAG_CHECK_OPEN_GALLERY)){
					mContent = new GalleryFragment();
					switchContent(mContent, "GalleryFragment");
				}
			} catch (Exception e) {}
			
			
			try {
				String back_contact_us = (String) data.getExtras().get("EXTRA_CHECK_BACK_CONTACTUS");
				LogUtils.e(TAG, "set back===" + back_contact_us);
				if(back_contact_us.equals(EXTRA_CHECK_BACK_CONTACTUS)){
					mContent = new ContactFragment();
					switchContent(mContent, "ContactFragment");
				}
			} catch (Exception e) {}
			
			try {
				String back_profile = (String) data.getExtras().get("EXTRA_CHECK_BACK_PROFILE");
				LogUtils.e(TAG, "set back===" + back_profile);
				if(back_profile.equals(EXTRA_CHECK_BACK_PROFILE)){
					FLAG_EXTRA_CHECK_BACK_PROFILE = true;
					mContent = new ProfileFragment();
					switchContent(mContent, "ProfileFragment");
				}
			} catch (Exception e) {}
			
			try {
				String ec_back = (String) data.getExtras().get("ec_back");
				LogUtils.e(TAG, "set back===" + ec_back);
				if(ec_back.equals("1")){
					mContent = new HomeFragment();
					switchContent(mContent, "HomeFragment");
					disableGestureMenu(true);
				}
			} catch (Exception e) {
				disableGestureMenu(true);
			}
		}
		
		
	}
	 
 

}
