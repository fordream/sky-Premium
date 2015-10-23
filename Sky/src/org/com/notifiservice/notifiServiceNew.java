package org.com.notifiservice;


import org.com.atmarkcafe.sky.GcmBroadcastReceiver;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.SkypeApplication;
import org.com.atmarkcafe.sky.SplashActivity;

import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;

import com.SkyPremiumLtd.SkyPremium.R;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class notifiServiceNew extends Service {

	private WindowManager windowManager;
//	private ImageView chatHead;
	WindowManager.LayoutParams params;
	WindowManager.LayoutParams params_x;
	WindowManager.LayoutParams params_hidden;
	private LinearLayout layout;
	private LinearLayout layout_x;
	public static Context myService;
	private static String KEY_NUMBER = "";
	private static String KEY_TYPE = "";
	private Boolean touch_move = false;
	private int delta_change = 20;
	private long timeClick = 300;
	private long time1 = 0;
	private long deltaTime = 310;
	@Override
	public void onCreate() {
		super.onCreate();
		myService = this;
		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		delta_change = (int) getApplicationContext().getResources().getDimension(R.dimen.dimen_20dp);
	    LayoutInflater inflater = (LayoutInflater) getSystemService (LAYOUT_INFLATER_SERVICE);
	    layout = (LinearLayout) inflater.inflate (R.layout.main_notif_new, null);
	    layout_x = (LinearLayout) inflater.inflate (R.layout.main_notif_x, null);

//		chatHead = new ImageView(this);
//		chatHead.setImageResource(R.drawable.LogUtilsin_LogUtilso1);

		params= new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params.gravity = Gravity.TOP | Gravity.LEFT;
		params.x = windowManager.getDefaultDisplay().getWidth() -  layout.getWidth();
		params.y =  (int) getApplicationContext().getResources().getDimension(R.dimen.dimen_160dp);;//windowManager.getDefaultDisplay().getHeight() / 2;
		
		params_x= new WindowManager.LayoutParams(
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params_x.gravity = Gravity.BOTTOM;
		params_x.x = 0;
		params_x.y = 100;
		
		params_hidden= new WindowManager.LayoutParams(0,
				0,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		params_hidden.gravity = Gravity.BOTTOM;
		params_hidden.x = 0;
		params_hidden.y = 0;
		
		//this code is for dragging the chat head
		layout.setOnTouchListener(new View.OnTouchListener() {
			private int initialX;
			private int initialY;
			private float initialTouchX;
			private float initialTouchY;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					touch_move = false;
					try {
						initialX = params.x;
						initialY = params.y;
						initialTouchX = event.getRawX();
						initialTouchY = event.getRawY();
//						windowManager.updateViewLayout(layout_x, params_x);
//						time1 = System.currentTimeMillis();
						LogUtils.i("XXX", "================ time delta = " + (System.currentTimeMillis() - time1));
						deltaTime = (System.currentTimeMillis() - time1);
						
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				case MotionEvent.ACTION_UP:
					try {
						if (layout_x != null)
							time1 = System.currentTimeMillis();
							windowManager.updateViewLayout(layout_x, params_hidden);
						if((params.y + layout.getHeight()) >= windowManager.getDefaultDisplay().getHeight() - (layout_x.getHeight() + 100)){
							onStop();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(!touch_move){
						if (deltaTime < timeClick) {
							LogUtils.i("XXXXX", "STOP di Cưng");
						}else{
							goToApp();
						}
					}
					return true;
				case MotionEvent.ACTION_MOVE:
					try {
						params.x = initialX
								+ (int) (event.getRawX() - initialTouchX);
						params.y = initialY
								+ (int) (event.getRawY() - initialTouchY);
						if( ( Math.abs(initialX - params.x) > delta_change) || ( Math.abs(initialY - params.y) > delta_change) ){ //
							touch_move = true;
							windowManager.updateViewLayout(layout_x, params_x);
							LogUtils.i("AAAAA", "y==" + params.y + "===" +  windowManager.getDefaultDisplay().getHeight());
						}
						
//						if(params.y >= windowManager.getDefaultDisplay().getHeight() - 300){
//							if (layout != null)
//								windowManager.removeView(layout);
//							if (layout_x != null)
//								windowManager.removeView(layout_x);
//							onStop();
//						}else{
							windowManager.updateViewLayout(layout, params);
//						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				case MotionEvent.ACTION_CANCEL:
					try {
						if (layout_x != null)
							windowManager.updateViewLayout(layout_x, params_hidden);
						if(( params.y + layout.getHeight()) >= windowManager.getDefaultDisplay().getHeight() - (layout_x.getHeight() + 100)){
							onStop();
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return true;
				}
				return false;
			}
		});
			windowManager.addView(layout_x, params_hidden);
			windowManager.addView(layout, params);
		LogUtils.i("AAAAAAA", "onCreate======");
	}
	

    
    protected void goToApp() {
		// TODO Auto-generated method stub
//    	GcmBroadcastReceiver.notifiServiceUpdate(getApplication(),0);
    	/*Intent i= new Intent(myService, SplashActivity.class);//SkyMainActivity
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if("1".equals(KEY_TYPE)){
//        	i.putExtra(SkyMainActivity.EXTRA_CHECK_BACK_MAIL, SkyMainActivity.EXTRA_CHECK_BACK_MAIL);
//        }else if("2".equals(KEY_TYPE)){
        	i.putExtra(SkyMainActivity.EXTRA_CHECK_BACK_NEW, SkyMainActivity.EXTRA_CHECK_BACK_NEW);
//        }else{
//        	i.putExtra(SkyMainActivity.EXTRA_CHECK_BACK_HOME, SkyMainActivity.EXTRA_CHECK_BACK_HOME);
//        }
        
        myService.startActivity(i);*/
    	Intent launcherApp = getPackageManager().getLaunchIntentForPackage("com.SkyPremiumLtd.SkyPremium");
    	SkyMainActivity.FLAG_CHECK_OPEN_NEW_FROM_CHAT_HEADER = true;
    	launcherApp.addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
//    	SharedPreferences shareData = getApplicationContext().getSharedPreferences(SkyUtils.shareKey, Context.MODE_PRIVATE);
//    	shareData.edit().putBoolean("OPEN_NEWS", true).commit();
		LogUtils.i("NotifyService", "OPEN APP FROM SERVICE-NewsService");
//    	SharedPreferences shareData = getApplicationContext().getSharedPreferences(SkyUtils.shareKey, Context.MODE_PRIVATE);
//    	shareData.edit().putBoolean("OPEN_NEWS", true).commit();
    	startActivity(launcherApp);

	}



	protected void onStop() {
		// TODO Auto-generated method stub
    	LogUtils.i("AAAAAAA", "stop======");
    	stopService(new Intent(myService, notifiServiceNew.class));
	}



	@Override
    public int onStartCommand (Intent intent, int flags, int startId) {
		LogUtils.i("AAAAAAA", "onStartCommand======BADGE_NEWS");
		KEY_NUMBER = "";
    	try {
			KEY_NUMBER 	= intent.getStringExtra("KEY_NUMBER");
		} catch (Exception e) {}
    	try {
			KEY_TYPE  	= intent.getStringExtra("KEY_TYPE");
		} catch (Exception e) {}
    	LogUtils.i("AAAAAAA", "KEY_NUMBER---" + KEY_NUMBER + ":KEY_TYPE----" + KEY_TYPE);
    	try {
    		SharedPreferences sharePref = getApplicationContext().getSharedPreferences(SkyUtils.shareKey, getApplication().MODE_PRIVATE);
			int total = sharePref.getInt(SkyUtils.BADGE_NEWS, 0);
			if("".equals(KEY_NUMBER)){
				KEY_NUMBER = "" +total;
			}
			try {
				if(Integer.parseInt(KEY_NUMBER) > 99){
					KEY_NUMBER = "99+";
				}
			} catch (Exception e) {}
			try {
				if("".equals(KEY_NUMBER) | "0".equals(KEY_NUMBER)){
					onStop();
				}
			} catch (Exception e) {}
			LogUtils.i("AAAAAAA", "KEY_NUMBER---" + KEY_NUMBER + ":KEY_TYPE----" + KEY_TYPE);
    		TextView number = (TextView) layout.findViewById(R.id.number_value_new);
    		if(!"".equals(KEY_NUMBER) && !"0".equals(KEY_NUMBER)){
    			number.setVisibility(View.VISIBLE);
    			number.setText(KEY_NUMBER);
    		}else{
    			number.setVisibility(View.GONE);
    		}
			if(windowManager != null && layout != null)
				windowManager.updateViewLayout(layout, params);
		} catch (Exception e) {}
        return START_STICKY;
    } 
    
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (layout != null)
			windowManager.removeView(layout);
		if (layout_x != null)
			windowManager.removeView(layout_x);
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
}