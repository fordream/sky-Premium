package com.acv.cheerz.db;

import org.json.JSONObject;

import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import android.content.ContentValues;
import android.content.Context;

public class ProductsSearch extends SkypeTable {
	@Override
	public int getIndex() {
		return 7;
	}

	public ProductsSearch(Context context) {
		super(context);
		addColumns(user_id);
		addColumns(name);
		addColumns(id);
		addColumns(id_ec);
		addColumns(thumbnail);
		addColumns(base_price);
		addColumns(specific_price);
		addColumns(description_short);
		addColumns(description);
		addColumns(reference_code);
		addColumns(condition);
		addColumns(quantity);
		addColumns(status_text);
	}

	public static final String user_id = "user_id";
	public static final String id_ec = "id_ec";

	public static final String id = "id";
	public static final String name = "name";
	public static final String thumbnail = "thumbnail";
	public static final String base_price = "base_price";
	public static final String specific_price = "specific_price";
	public static final String description_short = "description_short";
	public static final String description = "description";
	public static final String reference_code = "reference_code";
	public static final String condition = "condition";
	public static final String quantity = "quantity";
	public static final String status_text = "status_text";
	public static final String quantity_addcart = "quantity_addcart";
	public static final String address_delivery = "address_delivery";
	public static final String address_invoice = "address_invoice";
	public static final String address_type = "address_type";
	public static String ec_credit_confirm_amount = "0";

	public void addProductEcSearch(JSONObject product, String xid_ec) {
		ContentValues values = new ContentValues();
		for (String key : new String[] { id, name, thumbnail, base_price, specific_price, description_short, description, reference_code, condition, quantity, status_text }) {
			values.put(key, CommonAndroid.getString(product, key));
		}

		values.put(id_ec, xid_ec);
		values.put(user_id, new Account(getContext()).getUserId());
//and %s = '%s'
		String where = String.format("%s = '%s'  and %s = '%s' and %s = '%s'"//
				, user_id, new Account(getContext()).getUserId()//
				, id_ec, xid_ec//
				, id, CommonAndroid.getString(product, id)//
				);//

		if (has(where)) {
			getContext().getContentResolver().update(getContentUri(), values, where, null);
		} else {
			getContext().getContentResolver().insert(getContentUri(), values);
		}
	}
	
	public void deleteProductEcSearch()
    {
		String user_id = new Account(getContext()).getUserId();
		getContext().getContentResolver().delete(getContentUri(), "user_id=? ", new String[]{user_id});
    }
	
	public void updateQuantityProduct(Context context, String id_, String quantity_ ) {
		ContentValues values = new ContentValues();
		values.put(quantity, quantity_);
		String where = String.format("%s = '%s'", id, id_ );
		if (new Account(context).has(where)) {
			LogUtils.e("updateQuantityProduct", "update==id_::" + id_ +"::quantity_::" +quantity_);
			context.getContentResolver().update(getContentUri(), values, where, null);
		} else {
			LogUtils.e("updateQuantityProduct", "err_id::" + id_);
		}
	}

}
