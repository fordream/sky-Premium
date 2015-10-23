package org.com.atmarkcafe.sky;

import org.com.atmarkcafe.service.CallbackListenner;
import org.com.atmarkcafe.sky.dialog.EcProductsDetailDialog;
import org.com.atmarkcafe.sky.fragment.ECFragment;
import org.com.atmarkcafe.sky.fragment.HomeFragment;
import org.com.atmarkcafe.sky.fragment.LoginFragment;

import z.lib.base.BaseActivity;
import z.lib.base.BaseFragment;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.SCREEN;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.Setting;

public class RootActivity extends BaseActivity {

	private static final String TAG = "RootActivity";
	public static Dialog PJ_Detail = null;
	public static Dialog PJ_AddToCart = null;
	public static Dialog PJ_ListCat = null;
	public static Dialog PJ_SearchDialog = null;
	public static Dialog PJ_FavoriteDialog = null;
	public static Dialog PJ_HistoryDialog = null;
	public static Dialog PJ_HistoryDetailDialog = null;
	public static Boolean back = false;
	public static Boolean onLoadEC = false;
	IntentFilter intentFilter = new IntentFilter("com.atmarkcafe.CUSTOM_INTENT");
	private SharedPreferences sharePref;
	public static Activity rootActivity;
	BroadcastReceiver Receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(final Context context, Intent intent) {
			String message = intent.getStringExtra("msg");
			boolean isAvaible = intent.getBooleanExtra("isAvailable", true);
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
							RootActivity.this.finish();
						}
				});
			}
			
		}
	};
	
	public  void finishActivity(){
		finish();
	}
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		rootActivity = this;
		fragmentManager = getSupportFragmentManager();
		if (getFragemtCount() > 0) {
			return;
		}
		SkyUtils.SCREEN screen = (SkyUtils.SCREEN) getIntent().getSerializableExtra(SkyUtils.KEY.KEY_SCREEN);
		
		if (screen == SCREEN.LOGIN) {
			new Account(RootActivity.this).removeToken(RootActivity.this);
			GcmBroadcastReceiver.resetUserInfoLogout(getApplicationContext());
			startFragment(new LoginFragment(), null);
		} else if (screen == SCREEN.EC__PRODUCT_LIST) {
			LogUtils.e(TAG, "test=========");
			startFragment(new ECFragment(), getIntent().getExtras());
			//check product_id
			try {
				int product_id = -1;
				product_id = Integer.parseInt( (String) getIntent().getExtras().get(Products.id) );
				LogUtils.i(TAG, "set back===" + product_id);
				if(product_id >= 0){
					Bundle extras = new Bundle();
					extras.putString(Products.id, product_id + "");
					ECFragment.addtocart_Productsid = product_id + "";
					startDetail(SCREEN.PJDETAIL, extras);
				}
			} catch (Exception e1) {}
			
//			startFragment(new ECFragmentUpdate12052015(), getIntent().getExtras());
		} else if(screen == SCREEN.MAIL_BOX){
			//startFragment(new MailboxFragment(), getIntent().getExtras());
		} else if (screen == SCREEN.MAIL_BOX_DETAIL) {
			//startFragment(new EmailDetailFragment(),  getIntent().getExtras());
		}
		
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
	}

	CallbackListenner callbackListenner = new CallbackListenner() {
		@Override
		public void onLoad(boolean onload) {
			// TODO Auto-generated method stub
		}
	};
	
	public void startDetail(SCREEN screen, Bundle extras) {
		try {
			RootActivity.PJ_Detail = new EcProductsDetailDialog(this,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			},callbackListenner);
			RootActivity.PJ_Detail.show();
		} catch (Exception e) {
			
		}
	}
	
	@Override
	public int getLayout() {
		return R.layout.root;
	}

	@Override
	public int getResMain() {
		return R.id.root;
	}

	@Override
	public void finish() {
		Intent intent = new Intent();
		intent.putExtra("ec_back", "1");
		setResult(RESULT_OK, intent);
		LogUtils.e(TAG, "EC=========back");
		super.finish();
		if (getIntent().getBooleanExtra(SkyUtils.KEY.KEY_SCREEN_TAG, false)) {
			overridePendingTransition(R.anim.a_nothing, R.anim.right_out);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		LogUtils.e(TAG, "onActivityResult=========requestCode:" + requestCode);
		if (requestCode == 0 && !back) {
			goTOEC();
//			Bundle extras = new Bundle();
//			extras.putString("back", "back");
//			startFragment2(new ECFragment(), extras );
		}	
	}
	
	public static void goTOEC(){
		if(PJ_Detail != null){
			PJ_Detail.hide();
			PJ_Detail.dismiss();
			PJ_Detail = null;
		}
		if(PJ_AddToCart != null){
			PJ_AddToCart.dismiss();
		}
		if(PJ_ListCat != null){
			PJ_ListCat.dismiss();
		}
		if(PJ_FavoriteDialog != null){
			PJ_FavoriteDialog.dismiss();
		}
		if(PJ_HistoryDialog != null){
			PJ_HistoryDialog.dismiss();
		}
		if(PJ_HistoryDetailDialog != null){
			PJ_HistoryDetailDialog.dismiss();
		}
		try {
			LogUtils.i(TAG, "test=========1");
			ECFragment.showView(true);
		} catch (Exception e) {
			LogUtils.i(TAG, "test=========2");
		}
	}
	
	public static void startActivityForResult(SCREEN screen, Bundle extras) {
		if (extras == null) {
			extras = new Bundle();
		}
		extras.putSerializable(SkyUtils.KEY.KEY_SCREEN, screen);
		extras.putBoolean(SkyUtils.KEY.KEY_SCREEN_TAG, true);
		Intent intent = new Intent(rootActivity, RootActivity.class);//
		intent.putExtras(extras);

		rootActivity.startActivityForResult(intent, 0);
		rootActivity.overridePendingTransition(R.anim.right_in, 0);
	}
	
	
	private static FragmentManager fragmentManager;
	Fragment mContent = null;
	public final void startFragment2(BaseFragment baseFragment, Bundle bundle) {
		android.support.v4.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
//		transaction.setCustomAnimations(R.anim.right_in, R.anim.right_in, R.anim.right_out, R.anim.right_out);
		if (bundle != null)
			baseFragment.setArguments(bundle);
		transaction.add(getResMain(), baseFragment, "" + System.currentTimeMillis());
		transaction.addToBackStack(null);
		transaction.commit();
	}
	
	@Override
	public void onBackPressed() {
		LogUtils.e(TAG, "test=========2");
		//finish();
		super.onBackPressed();
		return;
	}
	
	@Override
	protected void onResume() {
		this.registerReceiver(Receiver, intentFilter);
		super.onResume();
	}
	
	@Override
	protected void onDestroy() {
		this.unregisterReceiver(Receiver);
		super.onDestroy();
	}
}
