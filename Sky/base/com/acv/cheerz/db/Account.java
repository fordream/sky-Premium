package com.acv.cheerz.db;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

public class Account extends SkypeTable {
	public Account(Context context) {
		super(context);

		addColumns(user_id);
		addColumns(password);
		addColumns(token_key);
		addColumns(contract_status);
		addColumns(contract_type);
		addColumns(token_key);
		addColumns(status_late_login);
		addColumns(name);
		addColumns(code);
		addColumns(redirect);
		addColumns(rememberpassword);
	}

	public static final String code = "code";
	public static final String redirect = "redirect";
	public static final String user_id = "user_id";
	public static final String password = "password";
	public static final String name = "name";
	public static final String contract_status = "contract_status";
	public static final String contract_type = "contract_type";
	public static final String token_key = "token_key";
	// 1 save, 0 no
	public static final String rememberpassword = "rememberpassword";

	// 1 is late
	//
	public static final String status_late_login = "status_late_login";
	private static final String TAG = "Account";

	public void updateWhenLogin(int responseCode, Context context, String response, Map<String, String> params) {
		if (!CommonAndroid.isBlank(response)) {
			try {
				JSONObject mainJsonObject = new JSONObject(response);
//				String is_succes = CommonAndroid.getString(mainJsonObject, SkyUtils.KEY.is_success);
				String remember = params.get(SkyUtils.KEY.REMEMBERPASSWORD);

//				if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
					JSONObject jsonObject = mainJsonObject.getJSONObject("data").getJSONObject("user");
					// all
					ContentValues xValues = new ContentValues();
					xValues.put(status_late_login, "0");
					context.getContentResolver().update(getContentUri(), xValues, null, null);

					ContentValues values = new ContentValues();

					values.put(contract_type, CommonAndroid.getString(jsonObject, contract_type));
					values.put(token_key, CommonAndroid.getString(jsonObject, token_key));
					values.put(name, CommonAndroid.getString(jsonObject, name));
					values.put(user_id, params.get("req[param][user_id]"));
					values.put(password, params.get("req[param][password]"));
					values.put(rememberpassword, "true".equals(remember) ? "1" : "0");
					values.put(status_late_login, "1");

					String where = String.format("%s = '%s'", user_id, params.get("req[param][user_id]"));

					values.put(contract_status, CommonAndroid.getString(jsonObject, contract_status));

					try {
						JSONObject contract_statusJs = jsonObject.getJSONObject(contract_status);
						values.put(code, CommonAndroid.getString(contract_statusJs, code));
						values.put(redirect, CommonAndroid.getString(contract_statusJs, redirect));
					} catch (Exception exception) {

					}
					if (new Account(context).has(where)) {
						context.getContentResolver().update(getContentUri(), values, where, null);
					} else {
						context.getContentResolver().insert(getContentUri(), values);
					}
//				}
			} catch (JSONException e) {
			}
		}
	}

	public void updateWhenLogOut(Context context){
		if (context != null) {
			context.getContentResolver().insert(getContentUri(), null);
		}
	}
	@Override
	public int getIndex() {
		return 1;
	}

	public Cursor querryLoginAccount() {
		return querry(String.format("%s =='1'", status_late_login));
	}

	public String getUserId() {
		String suser_id = "";
		Cursor cursor = querryLoginAccount();
		if (cursor != null) {
			if (cursor.moveToNext()) {
				suser_id = CommonAndroid.getString(cursor, user_id);
			}
			cursor.close();
		}
		return suser_id;
	}

	public String getToken() {
		String suser_id = "";
		Cursor cursor = querryLoginAccount();
		if (cursor != null) {
			if (cursor.moveToNext()) {
				suser_id = CommonAndroid.getString(cursor, token_key);
			}
			cursor.close();
		}
		return suser_id;
	}
	
	public void removeToken(Context context) {
		ContentValues values = new ContentValues();
		values.put(token_key, "");
		String where = String.format("%s = '%s'", user_id, getUserId() );
		if (new Account(context).has(where)) {
			context.getContentResolver().update(getContentUri(), values, where, null);
		} else {
			context.getContentResolver().insert(getContentUri(), values);
		}
		LogUtils.e(TAG, "removeToken");
	}
	
	public String getCode() {
		String suser_id = "";
		try {
			Cursor cursor = querryLoginAccount();
			if (cursor != null) {
				if (cursor.moveToNext()) {
					suser_id = CommonAndroid.getString(cursor, code);//code contract_status
				}
				cursor.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return suser_id;
	}
	
	public String getStatus() {
		String suser_id = "";
		try {
			Cursor cursor = querryLoginAccount();
			if (cursor != null) {
				if (cursor.moveToNext()) {
					suser_id = CommonAndroid.getString(cursor, contract_status);
				}
				cursor.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return suser_id;
	}

	public String getRedirect() {
		String suser_id = "";
		Cursor cursor = querryLoginAccount();
		if (cursor != null) {
			if (cursor.moveToNext()) {
				suser_id = CommonAndroid.getString(cursor, redirect);
			}
			cursor.close();
		}
		return suser_id;
	}
	
	public String getContractType() {
		String suser_id = "";
		Cursor cursor = querryLoginAccount();
		if (cursor != null) {
			if (cursor.moveToNext()) {
				suser_id = CommonAndroid.getString(cursor, contract_type);
			}
			cursor.close();
		}
		return suser_id;
	}
}
