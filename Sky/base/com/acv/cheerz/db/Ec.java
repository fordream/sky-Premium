//package com.acv.cheerz.db;
//
//import org.json.JSONObject;
//
//import z.lib.base.CommonAndroid;
//import android.content.ContentValues;
//import android.content.Context;
//
//public class Ec extends SkypeTable {
//	@Override
//	public int getIndex() {
//		return 8;
//	}
//
//	public Ec(Context context) {
//		super(context);
//		addColumns(user_id);
//		addColumns(id);
//		addColumns(name);
//	}
//
//	public static final String user_id = "user_id";
//	public static final String id = "id";
//	public static final String name = "name";
//
//	public void addEc(JSONObject ec_data) {
//		String id_ec = CommonAndroid.getString(ec_data, Ec.id);
//		String name_ec = CommonAndroid.getString(ec_data, Ec.name);
//		String userId = new Account(getContext()).getUserId();
//
//		ContentValues values = new ContentValues();
//		values.put(id, id_ec);
//		values.put(name, name_ec);
//		values.put(user_id, userId);
//		if (!CommonAndroid.isBlank(id_ec)) {
//			if (has(String.format("%s = '%s' and %s = '%s'", user_id, userId, id, id_ec))) {
//				getContext().getContentResolver().update(getContentUri(), values, String.format("%s = '%s' and %s = '%s'", user_id, userId, id, id_ec), null);
//			} else {
//				getContext().getContentResolver().insert(getContentUri(), values);
//			}
//		}
//	}
//
//}
