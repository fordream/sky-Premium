package com.acv.cheerz.db;

import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import android.content.ContentValues;
import android.content.Context;

public class Categories extends SkypeTable {
	@Override
	public int getIndex() {
		return 6;
	}

	public Categories(Context context) {
		super(context);
		addColumns(user_id);
		addColumns(name);
		addColumns(id);
		addColumns(type);
		// addColumns(id_products);
	}

	public static final String user_id = "user_id";
	public static final String id = "id";
	// public static final String id_products = "id_products";
	public static final String name = "name";

	/**
	 * type == ec -> EC type == 1 orther
	 */
	public static final String type = "type";

	public void addCategory(JSONObject category) {

		try {
			ContentValues values = new ContentValues();
			for (String key : new String[] { id, name }) {
				values.put(key, CommonAndroid.getString(category, key));
			}

			values.put(type, "ec");
			values.put(user_id, new Account(getContext()).getUserId());

			String where = String.format("%s = '%s' and %s = '%s'  and %s = 'ec' "//
					, user_id, new Account(getContext()).getUserId()//
					, id, CommonAndroid.getString(category, id)//
					, type);//

			if (has(where)) {
				getContext().getContentResolver().update(getContentUri(), values, where, null);
			} else {
				getContext().getContentResolver().insert(getContentUri(), values);
			}

		} catch (Exception exception) {

		}
	}
	
	public void deleteCat()
    {
		String user_id = new Account(getContext()).getUserId();
		getContext().getContentResolver().delete(getContentUri(), "user_id=? ", new String[]{user_id});
    }

	public void addCategory(JSONObject category, String id_ec) {
		// TODO Auto-generated method stub
		try {
			ContentValues values = new ContentValues();
			for (String key : new String[] { id, name }) {
				if(id_ec.equals(CommonAndroid.getString(category, key))){
					values.put(id, CommonAndroid.getString(category, id));
					values.put(name, CommonAndroid.getString(category, name));
				}
			}

			values.put(type, "ec");
			values.put(user_id, new Account(getContext()).getUserId());

			String where = String.format("%s = '%s' and %s = '%s'  and %s = 'ec' "//
					, user_id, new Account(getContext()).getUserId()//
					, id, CommonAndroid.getString(category, id)//
					, type);//

			if (has(where)) {
				getContext().getContentResolver().update(getContentUri(), values, where, null);
			} else {
				getContext().getContentResolver().insert(getContentUri(), values);
			}

		} catch (Exception exception) {

		}
	}

}
