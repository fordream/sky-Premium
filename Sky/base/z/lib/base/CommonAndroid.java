package z.lib.base;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import me.leolin.shortcutbadger.ShortcutBadger;

import org.com.atmarkcafe.sky.customviews.charting.MTextView;
import org.com.atmarkcafe.sky.dialog.ComfirmMessageDialog;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.downloadpdf.FileDownloadPDFCache;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog.Builder;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.IBinder;
import android.os.StatFs;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;

@SuppressLint("NewApi")
public class CommonAndroid {

	public static final void viewPagerSetSlowChangePage(ViewGroup pager, final int mDuration) {
		try {
			Field mScroller = null;
			mScroller = ViewPager.class.getDeclaredField("mScroller");

			if (pager instanceof ListView) {
				mScroller = ListView.class.getDeclaredField("mScroller");
			} else {

			}
			mScroller.setAccessible(true);
			Scroller scroller = new Scroller(pager.getContext()) {

				@Override
				public void startScroll(int startX, int startY, int dx, int dy, int duration) {
					super.startScroll(startX, startY, dx, dy, mDuration);
				}

				@Override
				public void startScroll(int startX, int startY, int dx, int dy) {
					super.startScroll(startX, startY, dx, dy, mDuration);
				}
			};

			mScroller.set(pager, scroller);
		} catch (Exception e) {

		}
	}

	public interface ScreenCallBack {
		// when screen off
		public void screenOff();

		// when screen on but haven't lock
		public void screenOn();

		// when screen on but have lock
		public void screenOnHaveLock();

		// screen unlock
		public void screenUnlock();
	}

	public static void registerScreenAction(final Context context, final ScreenCallBack screenCallBack) {
		BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
					screenCallBack.screenOff();
				} else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
					if (!haveLockScreen(context)) {
						screenCallBack.screenOn();
					} else {
						screenCallBack.screenOnHaveLock();
					}
				} else if (intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
					screenCallBack.screenUnlock();
				}
			}

		};

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		filter.addAction(Intent.ACTION_USER_PRESENT);
		context.registerReceiver(broadcastReceiver, filter);
	}

	public static void showDialog(Context context, String message, OnClickListener listener) {
//		Builder builder = new Builder(context);
//		builder.setMessage(message);
//		builder.setCancelable(false);
//		builder.setPositiveButton("OK", listener);
//		builder.show();
		if(context != null && message != null)
			new ComfirmMessageDialog(context, message, listener, null, (new Setting(context).isLangEng() ? R.string.tlt_ok : R.string.tlt_ok_j) , 0).show();
	}

	public static void showDialog(Context context, String message, OnClickListener onClickListener, OnClickListener onClickListener2) {
		Builder builder = new Builder(context);
		builder.setMessage(message);
		builder.setCancelable(false);
		builder.setPositiveButton("OK", onClickListener);
		builder.setNegativeButton("Cancel", onClickListener2);
		builder.show();

	}
	
	/**
	 * this function will update badge on icon launcher of application
	 * @param mContext 
	 * @param count : number will show on icon launcher
	 */
	public static void updateBadgeLauncher(Context mContext,int count){
		ShortcutBadger.with(mContext).count(count);
        //find the home launcher Package
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo resolveInfo = mContext.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        Log.i("Commond", "updateBadgeLauncher : " + count);
	}

	public static boolean haveLockScreen(Context context) {
		KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
		return km.inKeyguardRestrictedInputMode();
	}

	public static void showMarketPublish(Context context, String publish) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://search?q=pub:" + publish));
		context.startActivity(intent);
	}

	public static void showMarketProductBuyPackage(Context context, String pack) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + pack));
		context.startActivity(intent);
	}

	public static boolean callPhone(Context context, String phone) {
		try {
			try {
				Intent callIntent = new Intent(Intent.ACTION_DIAL);//ACTION_CALL
				callIntent.setData(Uri.parse("tel:" + phone));
				context.startActivity(callIntent);
				return true;
			} catch (Exception e) {
				return false;
			}
		} catch (ActivityNotFoundException e) {
			return false;
		}
	}

	public static boolean callWeb(Context context, String url) {
		try {
			Intent myIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
			context.startActivity(myIntent);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	public static void hiddenKeyBoard(Activity activity) {
		try {
			String service = Context.INPUT_METHOD_SERVICE;
			InputMethodManager imm = null;
			imm = (InputMethodManager) activity.getSystemService(service);
			IBinder binder = activity.getCurrentFocus().getWindowToken();
			imm.hideSoftInputFromWindow(binder, 0);
		} catch (Exception e) {
		}
	}
	
	public static void hiddenKeyBoard(Activity activity, View v) {
		try {
			InputMethodManager keyboard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.hideSoftInputFromWindow(v.getWindowToken(), 0);
		} catch (Exception e) {
		}
	}

	public static void setOrientation(Activity activity, boolean islandscape) {
		if (islandscape) {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		} else {
			activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
	}

	public static void hiddenTitleBarAndFullScreen(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
		activity.getWindow().setFlags(flag, flag);
	}

	public static void showKeyBoard(Activity activity, EditText editText) {
		String service = Context.INPUT_METHOD_SERVICE;
		InputMethodManager imm = null;
		imm = (InputMethodManager) activity.getSystemService(service);
		imm.showSoftInput(editText, InputMethodManager.SHOW_FORCED);
	}

	public static void showKeyBoard(Activity activity) {
		String service = Context.INPUT_METHOD_SERVICE;
		InputMethodManager imm = null;
		imm = (InputMethodManager) activity.getSystemService(service);
		imm.showSoftInput(activity.getCurrentFocus(), InputMethodManager.SHOW_FORCED);
	}

	public static int getVersionApp(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			return 0;
		}
	}

	public static String getVersionName(Context context) {
		try {
			return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return null;
		}
	}

	public static void toast(Context context, String message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static void toast(Context context, int message) {
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}

	public static boolean checkApplicationRunning(Context context) {
		// <uses-permission android:name="android.permission.GET_TASKS" />
		ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
		String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
		if (packageName.equalsIgnoreCase(context.getPackageName())) {
			return true;
		}
		return false;
	}

	public static Intent getLaucher(String otherPackage, Context context) {
		PackageManager packageManager = (PackageManager) context.getPackageManager();
		return packageManager.getLaunchIntentForPackage(otherPackage);
	}

	/**
	 * @param packageName
	 * @return
	 */
	public static boolean isAppRuning(String packageName, Context mContext) {
		ActivityManager activityManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
		for (int i = 0; i < procInfos.size(); i++) {
			if (procInfos.get(i).processName.equals(packageName)) {
				return true;
			}
		}

		return false;
	}

	public static boolean isAppRunningOnTop(Context mContext) {
		ActivityManager am = (ActivityManager) mContext.getSystemService(Activity.ACTIVITY_SERVICE);
		String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
		if (packageName.equalsIgnoreCase(mContext.getPackageName())) {
			return true;
		}
		return false;
	}

	public static boolean checkPermission(String permission, Context mContext) {
		PackageManager packageManager = mContext.getPackageManager();
		return (packageManager.checkPermission(permission, mContext.getPackageName()) == PackageManager.PERMISSION_GRANTED);
	}

	// ============================================================================
	// GPS
	// ============================================================================
	public static class GPS {
		public static boolean isSupportGPS(Context context) {
			LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			List<String> lAllProviders = manager.getAllProviders();
			for (int i = 0; i < lAllProviders.size(); i++) {
				if (LocationManager.GPS_PROVIDER.equals(lAllProviders.get(i))) {
					return true;
				}
			}
			return false;
		}

		public static void showGPSSetting(Context context, int request_code) {
			String action = android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS;
			Intent intent = new Intent(action);
			((Activity) context).startActivityForResult(intent, request_code);
		}

		public static void onpenGPS(Context context) {
			final Intent poke = new Intent();
			String packageName = "com.android.settings";
			String className = "com.android.settings.widget.SettingsAppWidgetProvider";
			poke.setClassName(packageName, className);
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);
		}

		public static void turnOffGPS(Context context) {
			Intent poke = new Intent();
			poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
			poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
			poke.setData(Uri.parse("3"));
			context.sendBroadcast(poke);

		}

		public static boolean isOpenGPS(Context context) {
			LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
			return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
	}

	// ============================================================================
	// NetWork
	// ============================================================================
	public static class NETWORK {
		public static boolean haveConnectTed(Context context) {
			Object service = context.getSystemService(Context.CONNECTIVITY_SERVICE);
			ConnectivityManager connectivityManager = (ConnectivityManager) service;
			NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
			for (int i = 0; i < networkInfos.length; i++) {
				if (networkInfos[i].isConnected()) {
					return true;
				}
			}

			return false;
		}

		public static void opennetworkSim(Context context, int requestCode) {
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setClassName("com.android.phone", "com.android.phone.NetworkSetting");
			((Activity) context).startActivityForResult(intent, requestCode);
		}

		public static void openWIFISetting(Context context, int requestCode) {
			Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
			((Activity) context).startActivityForResult(intent, requestCode);
		}

		public static void openNetWorkSetting(Context context, int requestCode) {
			Intent intent = new Intent(Settings.ACTION_WIRELESS_SETTINGS);
			((Activity) context).startActivityForResult(intent, requestCode);
		}
	}

	// ============================================================================
	// Account
	// ============================================================================
	public static class ACCOUNT {
		/**
		 * Check account google
		 * 
		 * @param context
		 * @return
		 */
		public static boolean haveMailAccountGoogleOnDevice(Context context) {
			AccountManager accountManager = AccountManager.get(context);
			Account[] accounts = accountManager.getAccountsByType("com.google");
			Account account;
			if (accounts.length > 0) {
				account = accounts[0];
			} else {
				account = null;
			}

			return account != null;
		}

	}

	// ============================================================================
	// ROOT CHECKER
	// ============================================================================
	public static class ROOTCHECKER {

		public static boolean checkRoot() {

			Process p;
			try {
				// Perform SU to get root privledges
				p = Runtime.getRuntime().exec("su");

				DataOutputStream os = new DataOutputStream(p.getOutputStream());
				os.writeBytes("remount rw");
				os.writeBytes("echo Root Test > /system/rootcheck.txt");
				os.writeBytes("exit\n");
				os.flush();

				p.waitFor();
				if (p.exitValue() != 255) {
					return true;
					// Phone is rooted
				} else {
					// Phone is not rooted
				}
			} catch (Exception e) {

			}
			return false;

		}
	}

	// ============================================================================
	// FONT
	// ============================================================================
	public static class FONT {
		public static final String PATH = "src/org/com/cnc/common/android/font/";
		public static final String BRADHITC = "BRADHITC.TTF";
		public static final String AGENCYB = "AGENCYB.TTF";
		public static final String BROADW = "BROADW.TTF";
		public static final String ALGER = "ALGER.TTF";

		public static void setTypeface(TextView tv, String fileAsset) {
			try {
				File file = new File(PATH + fileAsset);
				Log.i("file", file.exists() + "");
				Typeface tf = Typeface.createFromFile(file);
				tv.setTypeface(tf);
			} catch (Exception e) {
			}
		}

		public static void setTypefaceFromAsset(TextView tv, String fileAsset) {
			try {
				AssetManager assertManager = tv.getContext().getAssets();
				Typeface tf = Typeface.createFromAsset(assertManager, fileAsset);
				tv.setTypeface(tf);
			} catch (Exception e) {
			}
		}

		public static void main(String[] args) {
			File file = new File(PATH + ALGER);
			System.out.println(file.exists());
		}
	}

	// ============================================================================
	// CommonDeviceId
	// ============================================================================

	public static class DEVICEID {
		private static final int SIZE_10 = 10;
		public static final String TYPE_ID_IMEI = "IMEI";
		public static final String TYPE_ID_IPSEUDO_UNIQUE_ID = "Pseudo_Unique_Id";
		public static final String TYPE_ID_IANDROIDID = "AndroidId";
		public static final String TYPE_ID_IWLAN_MAC_ADDRESS = "WLAN_MAC_Address";
		public static final String TYPE_ID_IBT_MAC_ADDRESS = "BT_MAC_Address";
		public static final String TYPE_ID_ICOMBINED_DEVICE_ID = "Combined_Device_ID";

		public static final int SIZE_WIDTH_Y = 240;// Galaxy Y
		public static final int SIZE_HEIGHT_Y = 320;// Galaxy Y

		public static final int SIZE_WIDTH_EMULATOR_16 = 320;// EMULATOR
		public static final int SIZE_HEIGHT_EMULATOR_16 = 480;// EMULATOR

		public static final int SIZE_WIDTH_S = 480;// Galaxy S
		public static final int SIZE_HEIGHT_S = 800;// Galaxy S

		public static final int SIZE_WIDTH_TAB = 600;// Tab 7'
		public static final int SIZE_HEIGHT_TAB = 1024;// Tab 7'

		public static final int SIZE_WIDTH_VIEWSONIC = 600;// View Sonic
		public static final int SIZE_HEIGHT_VIEWSONIC = 1024;// View Sonic\

		public static boolean isTablet(Activity context) {
			Display display = context.getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();
			int min = width < height ? width : height;
			if (min > SIZE_WIDTH_S) {
				return true;
			}

			return false;
		}

		public static int getWidth(Activity context) {
			Display display = context.getWindowManager().getDefaultDisplay();
			return display.getWidth();
		}

		public static int getHeight(Activity context) {
			Display display = context.getWindowManager().getDefaultDisplay();
			return display.getHeight();
		}

		// Device ID
		public static String deviceId(Context context, String type) {
			if (TYPE_ID_IANDROIDID.equals(type)) {
				return deviceIdFromAndroidId(context);
			} else if (TYPE_ID_IBT_MAC_ADDRESS.equals(type)) {
				return deviceIdFromBT_MAC_Address(context);
			} else if (TYPE_ID_ICOMBINED_DEVICE_ID.equals(type)) {
				return deviceIdFromCombined_Device_ID(context);
			} else if (TYPE_ID_IMEI.equals(type)) {
				return deviceIdFromIMEI(context);
			} else if (TYPE_ID_IPSEUDO_UNIQUE_ID.equals(type)) {
				return deviceIdFromIMEI(context);
			} else if (TYPE_ID_IWLAN_MAC_ADDRESS.equals(type)) {
				return deviceIdFromWLAN_MAC_Address(context);
			}

			return null;
		}

		private static String deviceIdFromIMEI(Context context) {
			try {
				TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
				return TelephonyMgr.getDeviceId();
			} catch (Exception e) {
				return null;
			}

		}

		private static String deviceIdFromPseudo_Unique_Id() {
			StringBuilder builder = new StringBuilder();
			builder.append("35");
			builder.append(Build.BOARD.length() % SIZE_10);
			builder.append(Build.BRAND.length() % SIZE_10);
			builder.append(Build.CPU_ABI.length() % SIZE_10);
			builder.append(Build.DEVICE.length() % SIZE_10);
			builder.append(Build.DISPLAY.length() % SIZE_10);
			builder.append(Build.HOST.length() % SIZE_10);
			builder.append(Build.ID.length() % SIZE_10);
			builder.append(Build.MANUFACTURER.length() % SIZE_10);
			builder.append(Build.MODEL.length() % SIZE_10);
			builder.append(Build.PRODUCT.length() % SIZE_10);
			builder.append(Build.TAGS.length() % SIZE_10);
			builder.append(Build.TYPE.length() % SIZE_10);
			builder.append(Build.USER.length() % SIZE_10);
			return builder.toString();
		}

		private static String deviceIdFromAndroidId(Context context) {
			try {
				ContentResolver cr = context.getContentResolver();
				return Secure.getString(cr, Secure.ANDROID_ID);
			} catch (Exception e) {
				return null;
			}
		}

		private static String deviceIdFromWLAN_MAC_Address(Context context) {
			try {
				WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
				return wm.getConnectionInfo().getMacAddress();
			} catch (Exception e) {
				return null;
			}
		}

		private static String deviceIdFromBT_MAC_Address(Context context) {
			try {
				BluetoothAdapter m_BluetoothAdapter = null;
				m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				return m_BluetoothAdapter.getAddress();
			} catch (Exception e) {
				return null;
			}
		}

		private static String deviceIdFromCombined_Device_ID(Context context) {
			StringBuilder builder = new StringBuilder();
			builder.append(deviceIdFromIMEI(context));
			builder.append(deviceIdFromPseudo_Unique_Id());
			builder.append(deviceIdFromAndroidId(context));
			builder.append(deviceIdFromWLAN_MAC_Address(context));
			builder.append(deviceIdFromBT_MAC_Address(context));

			String m_szLongID = builder.toString();
			MessageDigest m = null;
			try {
				m = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
			byte p_md5Data[] = m.digest();
			String m_szUniqueID = new String();
			for (int i = 0; i < p_md5Data.length; i++) {
				int b = (0xFF & p_md5Data[i]);
				if (b <= 0xF)
					m_szUniqueID += "0";
				m_szUniqueID += Integer.toHexString(b);
			}

			return m_szUniqueID;
		}

		public static boolean canCallPhone(Context context) {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			if (telephonyManager.getSimSerialNumber() != null) {
				if (telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY) {
					return true;
				}
			}

			return false;
		}

		public static void rescanSdcard(Context context) {
			new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
			IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_SCANNER_STARTED);
			intentFilter.addDataScheme("file");
			context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
		}

		public static String findDeviceID(Context context) {
			String deviceID = android.provider.Settings.Secure.getString(context.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);
			return deviceID;
		}
	}

	// ============================================================================
	// RESIZE
	// ============================================================================
	public static class RESIZE {

		public static int getWidthScreen(Context context) {
			Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
			return display.getWidth();
		}

		public static int getHeightScreen(Context context) {
			Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
			return display.getHeight();
		}

		// -------------------------------------------------------
		// 20130408 fix
		// -------------------------------------------------------
		public static float _20130408_ScaleLandW960H640(Context context) {
			try {
				float SCREEN_HIGHT = 640;
				float SCREEN_WIDTH = 960;

				float res_width = getWidthScreen(context);
				float res_height = getHeightScreen(context);

				float scale = res_height / SCREEN_HIGHT;

				if (SCREEN_HIGHT / res_height < SCREEN_WIDTH / res_width) {
					scale = res_width / SCREEN_WIDTH;
				}

				return scale;
			} catch (Exception exception) {
				return 1.0f;
			}
		}

		public static void _20130408_resizeLandW960H640(View view, int width, int height) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			float scale = _20130408_ScaleLandW960H640(view.getContext());
			layoutParams.width = (int) (width * scale);
			layoutParams.height = (int) (height * scale);
			view.setLayoutParams(layoutParams);
		}

		public static int _20130408_getSizeByScreenLandW960H640(Context context, int sizeFirst) {
			return (int) (sizeFirst * _20130408_ScaleLandW960H640(context));
		}

		public static void _20130408_sendViewToPositionLandW960H640(View view, int left, int top) {
			float scale = _20130408_ScaleLandW960H640(view.getContext());
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			int _left = (int) (left * scale);
			int _top = (int) (top * scale);

			if (layoutParams instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof FrameLayout.LayoutParams) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableRow.LayoutParams) {
				TableRow.LayoutParams lp = new TableRow.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableLayout.LayoutParams) {
				TableLayout.LayoutParams lp = new TableLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			}
		}

		// Scale Width
		public static float _20130408_ScaleW960(Context context) {
			try {
				float SCREEN_WIDTH = 960;
				float res_width = getWidthScreen(context);
				return res_width / SCREEN_WIDTH;
			} catch (Exception exception) {
				return 1.0f;
			}
		}

		public static void _20130408_resizeW960(View view, int width, int height) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			float scale = _20130408_ScaleW960(view.getContext());
			layoutParams.width = (int) (width * scale);
			layoutParams.height = (int) (height * scale);
			if (width == LayoutParams.WRAP_CONTENT) {
				layoutParams.width = LayoutParams.WRAP_CONTENT;
			} else if (width == LayoutParams.MATCH_PARENT) {
				layoutParams.width = LayoutParams.MATCH_PARENT;
			} else if (width == LayoutParams.FILL_PARENT) {
				layoutParams.width = LayoutParams.FILL_PARENT;
			}

			if (height == LayoutParams.WRAP_CONTENT) {
				layoutParams.height = LayoutParams.WRAP_CONTENT;
			} else if (height == LayoutParams.MATCH_PARENT) {
				layoutParams.height = LayoutParams.MATCH_PARENT;
			} else if (height == LayoutParams.FILL_PARENT) {
				layoutParams.height = LayoutParams.FILL_PARENT;
			}

			view.setLayoutParams(layoutParams);
		}

		public static int _20130408_getSizeByScreenW960(Context context, int sizeFirst) {
			return (int) (sizeFirst * _20130408_ScaleW960(context));
		}

		public static void _20130408_sendViewToPositionW960(View view, int left, int top) {
			float scale = _20130408_ScaleW960(view.getContext());
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			int _left = (int) (left * scale);
			int _top = (int) (top * scale);

			if (layoutParams instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof FrameLayout.LayoutParams) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableRow.LayoutParams) {
				TableRow.LayoutParams lp = new TableRow.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableLayout.LayoutParams) {
				TableLayout.LayoutParams lp = new TableLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			}
		}

		// Height
		// Scale Width
		public static float _20130408_ScaleH960(Context context) {
			try {
				float SCREEN_WIDTH = 960;
				float res_width = getHeightScreen(context);
				return res_width / SCREEN_WIDTH;
			} catch (Exception exception) {
				return 1.0f;
			}
		}

		public static void _20130408_resizeH960(View view, int width, int height) {
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			float scale = _20130408_ScaleH960(view.getContext());
			layoutParams.width = (int) (width * scale);
			layoutParams.height = (int) (height * scale);
			view.setLayoutParams(layoutParams);
		}

		public static int _20130408_getSizeByScreenH960(Context context, int sizeFirst) {
			return (int) (sizeFirst * _20130408_ScaleH960(context));
		}

		public static void _20130408_sendViewToPositionH960(View view, int left, int top) {
			float scale = _20130408_ScaleH960(view.getContext());
			ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
			int _left = (int) (left * scale);
			int _top = (int) (top * scale);

			if (layoutParams instanceof RelativeLayout.LayoutParams) {
				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof LinearLayout.LayoutParams) {
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof FrameLayout.LayoutParams) {
				FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableRow.LayoutParams) {
				TableRow.LayoutParams lp = new TableRow.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			} else if (layoutParams instanceof TableLayout.LayoutParams) {
				TableLayout.LayoutParams lp = new TableLayout.LayoutParams(layoutParams.width, layoutParams.height);
				lp.setMargins(_left, _top, 0, 0);
				view.setLayoutParams(lp);
			}
		}
	}

	// ============================================================================
	// SHORTCUT
	// ============================================================================
	/**
	 * <uses-permission
	 * android:name="com.android.launcher.permission.INSTALL_SHORTCUT" />
	 * 
	 * @author truongvv
	 * 
	 */
	public static class SHORTCUT {
		private Context context;

		public SHORTCUT(Context context) {
			this.context = context;
		}

		public void deleteShortCut(Class<?> cls, int resstrName, int resIcon) {
			Intent removeIntent = createIntent(cls, "com.android.launcher.action.UNINSTALL_SHORTCUT", resstrName, resIcon);
			context.sendBroadcast(removeIntent);
		}

		public void autoCreateShortCut(Class<?> clss, int resstrName, int resIcon) {
			Intent intentShortcut = createIntent(clss, null, resstrName, resIcon);
			intentShortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			context.sendBroadcast(intentShortcut);
		}

		private Intent createIntent(Class<?> cls, String action, int resstrName, int resIcon) {

			Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
			shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			// -------------------------------------------------------------
			// sam sung 2x
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && Build.BRAND.equals("samsung")) {
				// sam sung 2.x
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.BRAND.equals("samsung")) {
				// sam sung 4.x
				shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				shortcutIntent.setPackage(context.getPackageName());
				shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			} else {
				// orther
				shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				shortcutIntent.setPackage(context.getPackageName());
			}
			// -------------------------------------------------------

			shortcutIntent.setClass(context, cls);

			Intent intentShortcut = new Intent();
			if (action != null) {
				intentShortcut = new Intent(action);
			}
			intentShortcut.putExtra("android.intent.extra.shortcut.INTENT", shortcutIntent);
			String title = context.getResources().getString(resstrName);
			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, title);
			// intentShortcut
			// .setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			intentShortcut.putExtra("duplicate", false);

			final int icon = resIcon;

			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, icon));
			return intentShortcut;
		}

		public void removieShortCutLauncher() {
			Intent intent = CommonAndroid.getLaucher(context.getPackageName(), context);
			intent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
			context.sendBroadcast(intent);
		}

		public void createShortCutLauncher(int resstrName, int resIcon) {
			Intent intent = CommonAndroid.getLaucher(context.getPackageName(), context);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //

			// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// -------------------------------------------------------------
			// sam sung 2x
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB && Build.BRAND.equals("samsung")) {
				// sam sung 2.x
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
			} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH && Build.BRAND.equals("samsung")) {
				// sam sung 4.x
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				// shortcutIntent.setPackage(context.getPackageName());
				intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
			} else {
				// orther
				// shortcutIntent.addCategory("android.intent.category.LAUNCHER");
				// shortcutIntent.setPackage(context.getPackageName());
			}
			// -------------------------------------------------------

			Intent intentShortcut = new Intent();
			intentShortcut.putExtra("android.intent.extra.shortcut.INTENT", intent);
			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, context.getResources().getString(resstrName));
			intentShortcut.putExtra("duplicate", false);
			intentShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, resIcon));
			intentShortcut.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
			context.sendBroadcast(intent);
		}

	}

	// ============================================================================
	// STORE AVAILABLE
	// ============================================================================
	public static class STOREAVAIABLE {
		public static long avaiableInternalStoreMemory() {
			StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
			long bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
			return (bytesAvailable / 1048576);
		}

		public static long totalInternalStorageMemory() {
			StatFs stat = new StatFs(Environment.getDataDirectory().getPath());
			long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
			return bytesAvailable / 1048576;
		}

		public static long availableExternalStorageMemory() {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			long bytesAvailable = (long) stat.getFreeBlocks() * (long) stat.getBlockSize();
			return bytesAvailable / 1048576;
		}

		public static long totalExternalStorageMemory() {
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			long bytesAvailable = (long) stat.getBlockSize() * (long) stat.getBlockCount();
			return bytesAvailable / 1048576;
		}
	}

	public static boolean isBlank(String columnName) {
		return columnName == null || columnName != null && columnName.trim().equals("");
	}

	public static <T extends View> T getView(View view, int res) {
		T v = (T) view.findViewById(res);
		return v;
	}

	public static void showView(boolean b, View... vs) {

		if (vs != null) {
			for (View v : vs) {
				v.setVisibility(b ? View.VISIBLE : View.GONE);
			}
		}

	}

	public static String getString(JSONObject jsonObject, String key) {
		if (jsonObject != null && jsonObject.has(key)) {
			try {
				String temp = jsonObject.getString(key);
				if("null".equals(temp))
					temp = "";
				return temp;
			} catch (JSONException e) {
			}
		}
		return "";
	}

	/**
	 * get int value by key
	 * @param jsonObject
	 * @param key
	 * @return != -1
	 * @return -1 : get fail
	 */
	public static int getInt(JSONObject jsonObject, String key) {
		if (jsonObject != null && jsonObject.has(key)) {
			try {
				JSONObject jsonObjectData = jsonObject.getJSONObject("data");
				int temp = jsonObjectData.getInt(key);
				if("null".equals(temp))
					temp = -1;
				return temp;
			} catch (JSONException e) {
			}
		}
		return -1;
	}
	public static String getString(Cursor cursor, String key) {
		return (cursor.getString(cursor.getColumnIndex(key))).trim();
	}

	/**
	 * 
	 */

	public static final class StringConnvert {
		public static final String convertVNToAlpha(String str) {
			if (isBlank(str)) {
				return str;
			}
			String temp = Normalizer.normalize(str, Normalizer.Form.NFD);
			Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
			// D D d d
			return pattern.matcher(temp).replaceAll("").replaceAll("Đ", "D").replaceAll("đ", "d");
		}

		private static boolean isBlank(String str) {
			return str == null || (str != null && str.trim().equals(""));
		}
	}

	/*
	 * */

	public static final void setAnimationOnClick(View view) {
		OnTouchListener onTouchListener = new OnTouchListener() {
			private Animation a_normal, a_selected, a_current;
			private boolean canUseAnimation = true;

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				/**
				 */
				// a_normal = new AlphaAnimation(1, 1f);
				if (a_normal == null) {
					a_normal = new ScaleAnimation(1, 1, 1, 1, v.getWidth() / 2, v.getHeight() / 2);
					a_normal.setDuration(0);
					a_normal.setFillAfter(true);
				}
				// a_selected = new AlphaAnimation(0.5f, 0.5f);
				if (a_selected == null) {
					a_selected = new ScaleAnimation(0.9f, 0.9f, 0.9f, 0.9f, v.getWidth() / 2, v.getHeight() / 2);
					a_selected.setDuration(0);
					a_selected.setFillAfter(true);
				}
				if (a_current == null) {
					a_current = a_normal;
				}

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					canUseAnimation = true;
					a_current = a_selected;
					v.startAnimation(a_current);
				} else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
					canUseAnimation = false;
					a_current = a_normal;
					v.startAnimation(a_current);
				} else if (event.getAction() == MotionEvent.ACTION_MOVE) {
					float x = event.getX();
					float y = event.getY();
					float w = v.getWidth();
					float h = v.getHeight();
					if (x < 0 || x > w) {
						if (a_current.equals(a_selected)) {
							canUseAnimation = false;
							a_current = a_normal;
							v.startAnimation(a_current);
						}
					} else if (y < 0 || y > h) {
						if (a_current.equals(a_selected)) {
							canUseAnimation = false;
							a_current = a_normal;
							v.startAnimation(a_current);
						}
					} else {
						if (canUseAnimation) {
							// if (a_current.equals(a_normal)) {
							a_current = a_selected;
							v.startAnimation(a_current);
							// }
						}
					}
				}
				return false;
			}
		};
		if (view != null) {
			view.setOnTouchListener(onTouchListener);
		}
	}

	/*
	 * 
	 */
	public static View initView(Context context, int res, ViewGroup root) {
		return ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(res, root);
	}

	public static void setText(TextView text_1, Cursor cursor, String key) {
		text_1.setText(Html.fromHtml(getString(cursor, key) + ""));
	}

	public static boolean copyFile(File mFile, File save) {
		try {
			InputStream in = new FileInputStream(mFile);
			OutputStream out = new FileOutputStream(save);

			// Transfer bytes from in to out
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
			return true;
		} catch (Exception exception) {
			return false;
		}
	}

	public static String getUrlFromContent(String content) {
		if (content != null) {
			content = content.substring(content.indexOf("href=\"") + 6, content.length());
			content = content.substring(0, content.indexOf("pdf") + 3);
		}
		return content;
	}

	public static void showPDF(final Context activity, String url) {
		if (activity != null) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(FileDownloadPDFCache.getInstance(activity).getFile(url)), "application/pdf");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				activity.startActivity(intent);
			} catch (Exception exception) {
				String message = activity.getString(new Setting(activity).isLangEng() ? R.string.pdf_markert : R.string.pdf_markert_j);
				showDialog(activity, message, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse("market://search?q=pdf"));
						activity.startActivity(intent);
					}
				});

			}
		}
	}

	public static void setText(View text_1, String str) {
		if (text_1 instanceof MTextView) {
			((MTextView) text_1).setTextEdit(str);
		} else if (text_1 instanceof TextView) {
			((TextView) text_1).setText(Html.fromHtml(str));
		}
	}
	
	public static boolean isEmail(String email) {
		 String EMAIL_REGEX =
		 "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		 Boolean b = email.matches(EMAIL_REGEX);
		 return b;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String convertDate(Context context, String temp , int type){
		String data = "";
		Boolean checkJP = false;
		if (temp.indexOf("-") > 0) {
			LogUtils.i("ConvertDate", " before :+== " + temp);
			temp = temp.replace("-", "/");
		}
		try {
			String [] temp1 = temp.split("/");
			if(temp1[0].length() == 4){
				checkJP = true;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String dtStart = temp; 
			SimpleDateFormat  format = new SimpleDateFormat("dd/MM/yyyy");  //yyyy-MM-dd
			String formatdate = new Setting(context).isLangEng() ? "dd/MM/yyyy" : "yyyy/MM/dd";
			switch (type) {
				case 0:
					format = checkJP ? new SimpleDateFormat("yyyy/MM/dd") : new SimpleDateFormat("dd/MM/yyyy");
					formatdate = new Setting(context).isLangEng() ? "dd/MM/yyyy" : "yyyy/MM/dd";
					break;
				case 1:
					format = checkJP ? new SimpleDateFormat("yyyy/MM") : new SimpleDateFormat("MM/yyyy");
					formatdate = new Setting(context).isLangEng() ? "MM/yyyy" : "yyyy/MM";
					break;
				case 2:
					format = checkJP ? new SimpleDateFormat("yyyy/MM") : new SimpleDateFormat("MM/yyyy");
					formatdate = "yyyy/MM";
					break;	
				default:
					break;
			}
			Date date = format.parse(dtStart); 
			data = new SimpleDateFormat(formatdate).format(date.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data ;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String convertDateUpdateProfile(Context context, String temp , String type){
		String data = "";
		Boolean checkJP = false;
		try {
			String [] temp1 = temp.split("/");
			if(temp1[0].length() == 4){
				checkJP = true;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			String dtStart = temp; 
			SimpleDateFormat  format = checkJP ? new SimpleDateFormat("yyyy/MM/dd") : new SimpleDateFormat("dd/MM/yyyy");
			String formatdate = new Setting(context).isLangEng() ? "dd/MM/yyyy" : "yyyy/MM/dd";
			if("ja".equals(type)){
				formatdate = "yyyy/MM/dd";
			}else{
				formatdate = "dd/MM/yyyy";
			}
			Date date = format.parse(dtStart); 
			data = new SimpleDateFormat(formatdate).format(date.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data ;
	}
	
	public static Boolean checkDateIsJapan(Context context, String temp){
		Boolean data = false;
		try {
			String [] temp1 = temp.split("/");
			if(temp1[0].length() == 4){
				data = true;
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		return data ;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String convertDate2(Context context, String temp , int type){
		String data = "";
		try {
			String dtStart = temp; 
			SimpleDateFormat  format_in = new SimpleDateFormat("yyyy/MM/dd");  //yyyy-MM-dd
			String format_out = new Setting(context).isLangEng() ? "dd/MM/yyyy" : "yyyy/MM/dd";
			Date date = format_in.parse(dtStart); 
			data = new SimpleDateFormat(format_out).format(date.getTime());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return data ;
}
	public static void TouchViewhiddenKeyBoard(final Activity context, View view) {

	    //Set up touch listener for non-text box views to hide keyboard.
		if (context != null) {
			if(!(view instanceof EditText)) {

		        view.setOnTouchListener(new OnTouchListener() {

		            public boolean onTouch(View v, MotionEvent event) {
		            	hiddenKeyBoard(context, v);
		                return false;
		            }

		        });
		    }
		}
	}
	
	public static String loadResouceHTMLFromAssets(final Activity context, String filename) {
		String tmp = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
			String word;
			while ((word = br.readLine()) != null) {
				if (!word.equalsIgnoreCase("")) {
					tmp += word;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				br.close(); // stop reading
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return tmp;
	}
	
	public static String DecimalFormatPrice(String value){
		String number = value;
		try {
			double amount = Double.parseDouble(number);
			DecimalFormat formatter = new DecimalFormat("#,###");
			number = formatter.format(amount);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return number;
	}
	
	public static void writeTextToFile(String string) {
		try {
			File myFile = new File("/sdcard/product_descreption.txt");
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(string);
			myOutWriter.close();
			fOut.close();
			Log.i("write", "write done");
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	@SuppressLint("SimpleDateFormat")
	public static Calendar convertToCalendar(Context context, String temp){
		Calendar dateandtime = Calendar.getInstance(Locale.US);
		try {
			Boolean checkJP = false;
			try {
				String [] temp1 = temp.split("/");
				if(temp1[0].length() == 4){
					checkJP = true;
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			String dateStr = "04/05/2010"; 
			SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy"); 
			curFormater = checkJP ? new SimpleDateFormat("yyyy/MM/dd") : new SimpleDateFormat("dd/MM/yyyy");
			Date dateObj = curFormater.parse(temp); 
			dateandtime = Calendar.getInstance();
			dateandtime.setTime(dateObj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateandtime;
		
	}
	
	@SuppressLint("SimpleDateFormat")
	public static Calendar convertToCalendar2(Context context, String temp){
		Calendar dateandtime = Calendar.getInstance(Locale.US);
		try {
			Boolean checkJP = false;
			try {
				String [] temp1 = temp.split("/");
				if(temp1[0].length() == 4){
					checkJP = true;
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			String dateStr = "04/05/2010"; 
			SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy"); 
//			curFormater = checkJP ? new SimpleDateFormat("yyyy/MM") : new SimpleDateFormat("MM/yyyy");
			Date dateObj = curFormater.parse(temp); 
			dateandtime = Calendar.getInstance();
			dateandtime.setTime(dateObj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateandtime;
		
	}
	
	@SuppressLint("SimpleDateFormat")
	public static Calendar convertToCalendarCredit(Context context, String temp){
		Calendar dateandtime = Calendar.getInstance(Locale.US);
		try {
			Boolean checkJP = false;
			try {
				String [] temp1 = temp.split("/");
				if(temp1[0].length() == 4){
					checkJP = true;
				}
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
//			String dateStr = "04/05/2010"; 
			temp = checkJP ? temp + "/01" : "01/" +temp ;
			SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy"); 
			curFormater = checkJP ? new SimpleDateFormat("yyyy/MM/dd") : new SimpleDateFormat("dd/MM/yyyy");
			Date dateObj = curFormater.parse(temp); 
			dateandtime = Calendar.getInstance();
			dateandtime.setTime(dateObj);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dateandtime;
		
	}
	
	public static String formatPriceEC(Context context,String price){
		String value ="";
		try {
			value = String.format("%s %s", new Setting(context).isLangEng() ? context.getResources().getString(
					R.string.ec_price_unit) : context.getResources().getString(
					R.string.ec_price_unit_j) , DecimalFormatPrice(price));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	
	public static String md5(String s) { 
		try { 
        
	        // Create MD5 Hash 
	        MessageDigest digest = java.security.MessageDigest.getInstance("MD5"); 
	        digest.update(s.getBytes()); 
	        byte messageDigest[] = digest.digest();
	 
	         // Create Hex String
	         StringBuffer hexString = new StringBuffer();
	         for (int i=0; i<messageDigest.length; i++)
	             hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
	        return hexString.toString();
	 
	     } catch (NoSuchAlgorithmException e) {
	         e.printStackTrace();
	     }
	     return "";
 
    }
	
	public static String createHash(String word) {
        String created_hash = null;
        try {
           MessageDigest md5 = MessageDigest.getInstance("MD5");
           md5.update(word.getBytes());
           BigInteger hash = new BigInteger(1, md5.digest());
           created_hash = hash.toString(16);
        } catch (NoSuchAlgorithmException e) {
           System.out.println("Exception : " + e.getMessage());
        }
        return created_hash;
     }
	
	public static void changeColorImageView(Context context, int drawableID, int colorNEW){
		/*Drawable myIcon = context.getResources().getDrawable(drawableID);
		try {
			myIcon.setColorFilter(colorNEW, Mode.SRC_IN);
		} catch (Exception e) {
			
		}
		try {
			myIcon.setColorFilter(colorNEW, android.graphics.PorterDuff.Mode.SRC_OVER);
		} catch (Exception e) {
			
			
		}*/
	}
	
	public static void changeColorImageView(Context context, int drawableID, int colorNEW, ImageView view){
		/*Drawable myIcon = context.getResources().getDrawable(drawableID);
		Drawable clone = myIcon.getConstantState().newDrawable();
		try {
			clone.setColorFilter(colorNEW, android.graphics.PorterDuff.Mode.SRC_IN);
//			view.setBackgroundDrawable(clone); // Use setBackgroundDrawable for API<16
//			view.setVisibility(View.VISIBLE);
		} catch (Exception e) {
			
		}
		*/
	}
}