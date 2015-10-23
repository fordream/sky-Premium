package org.com.atmarkcafe.sky;

import java.util.List;

import org.com.atmarkcafe.sky.SkypeApplication.IConfigApplication;
import org.com.atmarkcafe.sky.fragment.MailboxFragment;
import org.com.atmarkcafe.sky.fragment.NewsFragment;
import org.com.notifiservice.notifiServiceMail;
import org.com.notifiservice.notifiServiceNew;

import z.lib.base.BaseActivity;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;

public class SplashActivity extends BaseActivity implements IConfigApplication {
	private String TAG = "SplashActivity";
	String back_redirect= "0";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, " context : " + this);
		((SkypeApplication) getApplication()).init(this);
//		SkyMainActivity.FLAG_CHECK_OPEN_MAIL_BOX_FROM_CHAT_HEADER = false;
//		SkyMainActivity.FLAG_CHECK_OPEN_NEW_FROM_CHAT_HEADER = false;
//		notifiService(getApplicationContext(), true, 10, 1);
		
		try {
			String data = getIntent().getData().toString();
			Log.i(TAG, "========== dataxxx ==== " + data);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	@Override
	public int getLayout() {
		return R.layout.splash;
	}

	@Override
	public int getResMain() {
		return 0;
	}

	@Override
	public void onXStart() {

	}

	@Override
	public void onSuccess() {
		try {
			back_redirect = "0";
			String check = (String) getIntent().getExtras().get("redirect_splash");
			if(!"".equals(check)){
				back_redirect = new Setting(getApplicationContext()).getPushRedirect();
			}
			LogUtils.i(TAG, "set back splash===" + back_redirect);
		} catch (Exception e1) {
			LogUtils.i(TAG, "Err back_redirect");
		}
		
		String token = new Account(getApplicationContext()).getToken();
		String contract_status = new Account(getApplicationContext()).getStatus();
		String contract_type = new Account(getApplicationContext()).getContractType();
		LogUtils.i(TAG, "contract_type==" + contract_type + ":contract_status===" + contract_status + ":token==" + token);
		
		if (!isFinishing()) {
			if (token.equalsIgnoreCase("") || token == null || !contract_status.equals("") /*|| "null".equals(contract_type) || "".equals(contract_type)*/) {
				Intent intent = new Intent(this, RootActivity.class);
				intent.putExtra(SkyUtils.KEY.KEY_SCREEN, SkyUtils.SCREEN.LOGIN);
				startActivity(intent);
				finish();
			}else{
				Intent main = new Intent(getApplicationContext(), SkyMainActivity.class);
				main.putExtra("redirect", ""+back_redirect);
				startActivity(main);
				finish();
			}
			
		}
	}
	
	 public static String getLauncherClassName(Context context) {

	     PackageManager pm = context.getPackageManager();

	     Intent intent = new Intent(Intent.ACTION_MAIN);
	     intent.addCategory(Intent.CATEGORY_LAUNCHER);

	     List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
	     for (ResolveInfo resolveInfo : resolveInfos) {
	         String pkgName = resolveInfo.activityInfo.applicationInfo.packageName;
	         if (pkgName.equalsIgnoreCase(context.getPackageName())) {
	             String className = resolveInfo.activityInfo.name;
	             return className;
	         }
	     }
	     return null;
	 }
	 	
		public void notifiService(Context ctx, Boolean start, int number, int type){
			// potentially add data to the intent
//			long time = System.currentTimeMillis();
			SharedPreferences sharePref = ctx.getSharedPreferences(SkyUtils.shareKey, ctx.MODE_PRIVATE);
			try {
				if(number > 0){
					if(type == 1){ //mail
						Intent i_mail = new Intent(ctx, notifiServiceMail.class);
						int countEmail = sharePref.getInt(SkyUtils.BADGE_EMAIL, 0);
						sharePref.edit().putInt(SkyUtils.BADGE_EMAIL, countEmail + number).commit();
						int total_mail = sharePref.getInt(SkyUtils.BADGE_EMAIL, 0);
						if(total_mail <= 0){
							total_mail = 0;
						}
						i_mail.putExtra("KEY_NUMBER", "" +total_mail);
						i_mail.putExtra("KEY_TYPE", "" +type);
						if(start){
							ctx.startService(i_mail);
						}else{
							ctx.stopService(i_mail);
						}
					}else if(type == 2){ //new
						Intent i_new = new Intent(ctx, notifiServiceNew.class);
						int countNews = sharePref.getInt(SkyUtils.BADGE_NEWS, 0);
						sharePref.edit().putInt(SkyUtils.BADGE_NEWS, countNews + number).commit();
						int total_new = sharePref.getInt(SkyUtils.BADGE_NEWS, 0);
						if(total_new <= 0){
							total_new = 0;
						}
						i_new.putExtra("KEY_NUMBER", "" +total_new);
						i_new.putExtra("KEY_TYPE", "" +type);
						if(start){
							ctx.startService(i_new);
						}else{
							ctx.stopService(i_new);
						}
					}
				}
			} catch (Exception e) {}
			
			CommonAndroid.updateBadgeLauncher(ctx, sharePref.getInt(SkyUtils.BADGE_EMAIL, 0) + sharePref.getInt(SkyUtils.BADGE_NEWS, 0));
		}
	
}