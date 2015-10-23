/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.com.atmarkcafe.sky;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.com.notifiservice.notifiServiceMail;
import org.com.notifiservice.notifiServiceNew;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

/**
 * Handling of GCM messages.
 */
public class GcmBroadcastReceiver extends ParsePushBroadcastReceiver {
	static final String TAG = "GcmBroadcastReceiver";
	final static String KEY_USER_INFO = "userId";
	final static String KEY_CHANNEL_INFO_TEST = "privateACV2";//["privateACV","global"]privateACV1
	final static String KEY_CHANNEL_INFO_RELEASE = "pushglobal";//global
	private Context mContext;

	static Boolean key_parse_release = true; //default === true
	private Boolean isDuplicate = false;

	@Override
	protected Notification getNotification(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		String jsonData = extras.getString("com.parse.Data");
		LogUtils.i(TAG, "parse data==" +jsonData);
		
		String title = "SKY PREMIUM";
		String message = "";
		int checkDataType = 0;
		this.mContext = context;
		if (jsonData != null) {
			try {
				JSONObject data = new JSONObject(jsonData);
				if(data.has("alert")){
					message = data.getString("alert");
				}
				if(data.has("type")){
					checkDataType = data.getInt("type");
				}
				if(data.has("server_time")){
					SharedPreferences sharePref = context.getSharedPreferences(SkyUtils.shareKey, context.MODE_PRIVATE);
					String time_old = sharePref.getString(SkyUtils.BADGE_LOG_TIME, "");
					if(time_old.equals(data.getString("server_time")) && !"".equals(data.getString("server_time"))){
						isDuplicate = true;
					}else{
						isDuplicate = false;
						sharePref.edit().putString(SkyUtils.BADGE_LOG_TIME, data.getString("server_time")).commit();
					}
				}
			} catch (JSONException e) {
				if(!"".equals(jsonData)){
					message = jsonData;
				}
				Log.e(TAG, "Error parsing json data");
			}
		} else {
			Log.e(TAG, "cannot find notification data");
		}

		message = message.toString().trim();
		if("{}".equals(message)){
			message = "";
		}
		String isPush = "";
		try {
			isPush = new Setting(context).isPush();
		} catch (Exception e1) {}
		if ("1".equals(isPush) && !"".equals(message) /*&& checkDataType !=0*/ && !isDuplicate){

			LogUtils.i(TAG, "redirect==" + checkDataType);
			PendingIntent contentIntent = null;
			Intent notificationIntent = new Intent(context, SplashActivity.class);
			new Setting(context).settingPushRedirect(checkDataType);
			notificationIntent.putExtra("redirect_splash", ""+checkDataType);
			contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
			Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			// Vibrate for 300 milliseconds
			v.vibrate(300);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.ic_launcher).setContentTitle(title).setContentText(message)
					.setContentIntent(contentIntent).setDefaults(Notification.DEFAULT_ALL).setAutoCancel(true).setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			
			Intent pupInt = new Intent(context, ShowPopUpPush.class);
			pupInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
			pupInt.putExtra("data_push", message);
			
			String token = new Account(context).getToken();
			String contract_status = new Account(context).getStatus();
			String contract_type = new Account(context).getContractType();
			try {
				LogUtils.i(TAG, "contract_type==" + contract_type + ":contract_status===" + contract_status + ":token==" + token);
				if (token.equalsIgnoreCase("") || token == null || !contract_status.equals("") /*|| "null".equals(contract_type) || "".equals(contract_type)*/) {
					//root
					pupInt.putExtra("redirect", "0");
					LogUtils.i(TAG,"token err");
				}else{
					//main
					if(checkDataType == 1 | checkDataType == 2){
						pupInt.putExtra("redirect", ""+checkDataType);
					}else{
						LogUtils.i(TAG,"checkDataType err==" + checkDataType);
					}
				}
			} catch (Exception e) {
				LogUtils.e(TAG, "err token");
			}
			
			if(checkDataType == 0){
				pupInt.putExtra("redirect", "");
				context.getApplicationContext().startActivity(pupInt);
				return builder.build();
			}
			if (token.equalsIgnoreCase("") || token == null || !contract_status.equals("") /*|| "null".equals(contract_type) || "".equals(contract_type)*/) {
				Log.i(TAG, "type ======= err login");
				return null;
			}else if( checkDataType == 1 | checkDataType == 2 ){
				/*pupInt.putExtra("redirect", ""+checkDataType);
				context.getApplicationContext().startActivity(pupInt);
				setBadgeDevice(mContext, checkDataType, 1);*/
				notifiService(context, true, 1, checkDataType);
				Log.i(TAG, "type ======= " + checkDataType);
				return builder.build();
			}
			return null;
		}else{
			return null;
		}
		
	}
	
	public void notifiService(Context ctx, Boolean start, int number, int type){
		// potentially add data to the intent
//		long time = System.currentTimeMillis();
		String ispush = "";
		try {
			ispush = new Setting(ctx).isPush();
		} catch (Exception e2) {}
		if ("1".equals(ispush)){
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
	
	public static void notifiServiceUpdate(Context ctx, int number, int type, Boolean update_server, Boolean checkRun){ //update
		String ispush = "";
		
		try {
			Log.i(TAG, "CHECK-CONTEXT = " + ctx);
			
			ispush = new Setting(ctx).isPush();
		} catch (Exception e2) {}
		if ("1".equals(ispush)){
			SharedPreferences sharePref = ctx.getSharedPreferences(SkyUtils.shareKey, Context.MODE_PRIVATE);
			if(type == 1){
				int total = sharePref.getInt(SkyUtils.BADGE_EMAIL, 0);
				if(number > 0 && !update_server ){
					total = total - number;
				}else if(number > 0 && update_server ){
					total = number;
				}else{
					total = 0;
				}
				if(total <= 0){
					total = 0;
				}
				sharePref.edit().putInt(SkyUtils.BADGE_EMAIL, total).commit();
			}else if(type == 2){
				int total = sharePref.getInt(SkyUtils.BADGE_NEWS, 0);
				if(number > 0 && !update_server ){
					total = total - number;
				}else if(number > 0 && update_server ){
					total = number;
				}else{
					total = 0;
				}
				if(total <= 0){
					total = 0;
				}
				sharePref.edit().putInt(SkyUtils.BADGE_NEWS, total).commit();
			}
			try {
				if(isMyServiceRunning(ctx, notifiServiceMail.class) || (!checkRun && type == 1) ){
					LogUtils.i(TAG, "notifiServiceUpdate=======ok");
					
					
					Intent i= new Intent(ctx, notifiServiceMail.class);
					i.putExtra("KEY_NUMBER", "" + sharePref.getInt(SkyUtils.BADGE_EMAIL, 0));
					ctx.startService(i);
				}else{
					LogUtils.i(TAG, "notifiServiceUpdate=======no");
				}
			} catch (Exception e) {
				LogUtils.i(TAG, "notifiServiceUpdate=======mail err");
			}
			
			try {
				if(isMyServiceRunning(ctx, notifiServiceNew.class) || (!checkRun && type == 2)){
					LogUtils.i(TAG, "notifiServiceUpdate=======ok");
					
					Intent i= new Intent(ctx, notifiServiceNew.class);
					i.putExtra("KEY_NUMBER", "" + sharePref.getInt(SkyUtils.BADGE_NEWS, 0));
					ctx.startService(i);
				}else{
					LogUtils.i(TAG, "notifiServiceUpdate=======no");
				}
			} catch (Exception e) {
				LogUtils.i(TAG, "notifiServiceUpdate=======new err");
			}
			
			CommonAndroid.updateBadgeLauncher(ctx, sharePref.getInt(SkyUtils.BADGE_EMAIL, 0) + sharePref.getInt(SkyUtils.BADGE_NEWS, 0));
		}
		
	}
	
	public static void notifiServiceStop(Context ctx){
		SharedPreferences sharePref = ctx.getSharedPreferences(SkyUtils.shareKey, ctx.MODE_PRIVATE);
		try {
			sharePref.edit().putInt(SkyUtils.BADGE_EMAIL, 0).commit();
			sharePref.edit().putInt(SkyUtils.BADGE_NEWS, 0).commit();
			CommonAndroid.updateBadgeLauncher(ctx, sharePref.getInt(SkyUtils.BADGE_EMAIL, 0) + sharePref.getInt(SkyUtils.BADGE_NEWS, 0));
		} catch (Exception e1) {}
		try {
			if(isMyServiceRunning(ctx, notifiServiceMail.class) ){
				LogUtils.i(TAG, "stopService notifiServiceUpdate=======ok");
				Intent i= new Intent(ctx, notifiServiceMail.class);
				i.putExtra("KEY_NUMBER", "" + sharePref.getInt(SkyUtils.BADGE_EMAIL, 0));
				ctx.stopService(i);
			}
		} catch (Exception e) {
			LogUtils.i(TAG, "stopService notifiServiceUpdate=======mail err");
		}
		
		try {
			if(isMyServiceRunning(ctx, notifiServiceNew.class) ){
				LogUtils.i(TAG, "stopService notifiServiceUpdate=======ok");
				Intent i= new Intent(ctx, notifiServiceNew.class);
				i.putExtra("KEY_NUMBER", "" + sharePref.getInt(SkyUtils.BADGE_NEWS, 0));
				ctx.stopService(i);
			}
		} catch (Exception e) {
			LogUtils.i(TAG, "stopService notifiServiceUpdate=======new err");
		}
	}
	
	private static boolean isMyServiceRunning(Context ctx, Class<?> serviceClass) {
	    ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
	    for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
	        if (serviceClass.getName().equals(service.service.getClassName())) {
	            return true;
	        }
	    }
	    return false;
	}

	public static void registerUserInfo(Context context){
		try {
			if ("1".equals(new Setting(context).isPush())){
				String user_id = new Account(context).getUserId();
				LogUtils.i(TAG, "registerUserInfo==" + user_id);
				Parse.initialize(context, SkypeApplication.Parse_app_key, SkypeApplication.Parse_client_key); 
				ParseInstallation.getCurrentInstallation().put(KEY_USER_INFO, user_id);
//				ParseInstallation.getCurrentInstallation().addAllUnique("channels", Arrays.asList(KEY_CHANNEL_INFO_TEST, KEY_CHANNEL_INFO_RELEASE));
				ParseInstallation.getCurrentInstallation().put("channels",
		                Arrays.asList(KEY_CHANNEL_INFO_TEST, KEY_CHANNEL_INFO_RELEASE));
				ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							// OK
							LogUtils.i(TAG,"registerUserInfo Done");
						} else {
							// Err
							LogUtils.i(TAG,"registerUserInfo Err");
							e.printStackTrace();
						}
					}
				});
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	public static void resetUserInfo(Context context){
		try {
			if ("1".equals(new Setting(context).isPush())){
//				startService(context, true);
				Log.i(TAG, "resetUserInfo");
				Parse.initialize(context, SkypeApplication.Parse_app_key, SkypeApplication.Parse_client_key); 
				ParseInstallation.getCurrentInstallation().put(KEY_USER_INFO, "");
//				ParseInstallation.getCurrentInstallation().removeAll("channels", Arrays.asList(KEY_CHANNEL_INFO_TEST, KEY_CHANNEL_INFO_RELEASE, "privateACV","global"));
				ParseInstallation.getCurrentInstallation().put("channels",
		                Arrays.asList(new String[]{})); //Put the empty list to unsubcribe!
				ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							// OK
							LogUtils.i(TAG,"resetUserInfo Done");
						} else {
							// Err
							LogUtils.e(TAG,"resetUserInfo Err");
							e.printStackTrace();
						}
					}
				});
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	public static void resetUserInfoLogout(Context context){
		LogUtils.i(TAG, "resetUserInfo logout");
		try {
			if ("1".equals(new Setting(context).isPush())){
//				startService(context, true);
				ParseInstallation.getCurrentInstallation().put(KEY_USER_INFO, "");
				ParseInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
					@Override
					public void done(ParseException e) {
						if (e == null) {
							// OK
							LogUtils.i(TAG,"resetUserInfo Done");
						} else {
							// Err
							LogUtils.e(TAG,"resetUserInfo Err");
							e.printStackTrace();
						}
					}
				});
			}
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}
	
	private static void startService22(Context context, Boolean start){
		PackageManager pm  = context.getPackageManager();
		String packageName  = context.getPackageName();
		   ComponentName componentName = new ComponentName(packageName,
		         "com.parse.PushService");
		if(start)   
		      pm.setComponentEnabledSetting(componentName,
		        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		        PackageManager.DONT_KILL_APP);
	}
	
	/**
	 * update badge icon for launcher
	 * @param type
	 */
	/*public static  void setBadgeDevice(Context context, int type, int num){
			//Mailbox == 1, New == 2
			SharedPreferences sharePref = context.getSharedPreferences(SkyUtils.shareKey, context.MODE_PRIVATE);
			int countEmail = sharePref.getInt(SkyUtils.BADGE_EMAIL, 0);
			int countNews = sharePref.getInt(SkyUtils.BADGE_NEWS, 0);
			if(num > 0){
				if(type == 1){
					sharePref.edit().putInt(SkyUtils.BADGE_EMAIL, countEmail + num).commit();
				}else if(type == 2){
					sharePref.edit().putInt(SkyUtils.BADGE_NEWS, countNews + num).commit();
				}
			}
			
			
			int totalBadge = sharePref.getInt(SkyUtils.BADGE_TOTAL, 0);
			if (totalBadge == 0) {
				totalBadge = countEmail+countNews +1;
			}else{
				totalBadge += 1 ;
			}
			
//			LogUtils.i(TAG, "setBadgeDevice :count = "+totalBadge +" countMail = " + countEmail + " countNews = " + countNews);
//	        CommonAndroid.updateBadgeLauncher(context, sharePref.getInt(SkyUtils.BADGE_EMAIL, 0) + sharePref.getInt(SkyUtils.BADGE_NEWS, 0));
			CommonAndroid.updateBadgeLauncher(context, totalBadge);
	        sharePref.edit().putInt(SkyUtils.BADGE_TOTAL, totalBadge).commit();
	        Log.i(TAG, "====UpdateBadge by push total = " + totalBadge);
	        Log.i(TAG, "====UpdateBadge by push news = " + countNews);
	        Log.i(TAG, "====UpdateBadge by push email = " + countEmail);
//	        sharePref.edit().putInt(SkyUtils.BADGE_TOTAL, totalBadge).commit();
	}*/
	
	private boolean isServiceRunning(String serviceName) {
		boolean serviceRunning = false;
		ActivityManager am = (ActivityManager) mContext.getSystemService(mContext.ACTIVITY_SERVICE);
		List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
		Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
		while (i.hasNext()) {
			ActivityManager.RunningServiceInfo runningServiceInfo = (ActivityManager.RunningServiceInfo) i
					.next();

			if (runningServiceInfo.service.getClassName().equals(serviceName)) {
				serviceRunning = true;
			}
		}
		Log.i(TAG, "Is " + serviceName + " Service Running : "
				+ serviceRunning);
		return serviceRunning;
	}
	
	private static void test(Context ctx){
		/*LogUtils.i(TAG, "test==================1");
		ParseQuery<ParseInstallation> duplicateQuery = ParseInstallation.getQuery();
	    String android_id = Secure.getString(ctx.getContentResolver(),
                Secure.ANDROID_ID); 
	    LogUtils.i(TAG, "test==================1==" + android_id);
		duplicateQuery.whereEqualTo("androidId", android_id);
	    duplicateQuery.getFirstInBackground(new GetCallback<ParseInstallation>() {
		    @Override
		    public void done(ParseInstallation duplicate,ParseException e) {
		    	if (e != null) {
		    		LogUtils.i(TAG, "test==================2");
		    		e.printStackTrace(); //null in my case
	            }else{
	            	duplicate.deleteInBackground();
	    		    LogUtils.i(TAG, "test==================3");
	            }
		    
		}
	    });*/
	}
	
}