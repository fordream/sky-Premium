package org.com.atmarkcafe.sky;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class GlobalFunction {
	
	/**
	 * hide keyboard
	 * @param activity
	 * @param v
	 */
	public static void hideSoftKeyboard(Activity activity,View v) {
		try {

		    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		    if (inputMethodManager.isAcceptingText() ) {
		    	inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
			}
		} catch (Exception e) {
			
		}
		
	}
	
	/**
	 * handler keybord on view
	 * @param activity
	 * @param view
	 */
	public static void handlerKeyboardOnView(final Activity activity,final View view) {

	    //Set up touch listener for non-text box views to hide keyboard.
		if (activity != null) {
		    if(!(view instanceof EditText)) {

		        view.setOnTouchListener(new OnTouchListener() {

		            public boolean onTouch(View v, MotionEvent event) {
		            	hideSoftKeyboard(activity,v);
		            	Log.i("GLOBAL", "HideKeyboard : " + view.getClass().getSimpleName());
		                return false;
		            }

		        });
		    }
		}

	    
	}
	
	public static String formatRespone(String respone){
		respone = respone.replace("null", " ");
		return respone;
	}
	
	public static String base64String(String stringContent){
		
		return Base64.encodeToString(stringContent.getBytes(), Base64.DEFAULT);
	}
	
	public static boolean isTablet(Context context){
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	public static void writeText2File(String data,String fileName) {
		try {
			File myFile = new File("/sdcard/"+fileName+".txt");
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(data);
			myOutWriter.close();
			fOut.close();
			Log.i("Commond", "write done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * this method will get date ago some days.
	 * @param formatDate
	 * @param days
	 * @return date ago some days
	 */
	public static String getCalculateDate(String formatDate,int days){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat(formatDate);
		cal.add(Calendar.DAY_OF_YEAR, days);
		return format.format(new Date(cal.getTimeInMillis()));
	}
}
