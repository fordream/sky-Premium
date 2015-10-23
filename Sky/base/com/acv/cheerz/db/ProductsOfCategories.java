package com.acv.cheerz.db;

import android.content.ContentValues;
import android.content.Context;

public class ProductsOfCategories extends SkypeTable {
	@Override
	public int getIndex() {
		return 10;
	}

	public ProductsOfCategories(Context context) {
		super(context);
		addColumns(user_id);
		addColumns(id_category);
		addColumns(id_product);
	}

	public static final String user_id = "user_id";
	public static final String id_category = "id_category";
	public static final String id_product = "id_product";

	public void addProduct(String xid_product, String xid_category) {
		ContentValues values = new ContentValues();
		values.put(user_id, new Account(getContext()).getUserId());
		values.put(id_category, xid_category);
		values.put(id_product, xid_product);
		
		String where = String.format("%s = '%s' and %s = '%s' and %s = '%s'"//
				, user_id,new Account(getContext()).getUserId()
				, id_category,xid_category//
				, id_product,xid_product);//);
		
		if(!has(where)){
			getContext().getContentResolver().insert(getContentUri(), values);
		}
	}
	
	public void deleteProductsOfCategories()
    {
		String user_id = new Account(getContext()).getUserId();
		getContext().getContentResolver().delete(getContentUri(), "user_id=? ", new String[]{user_id});
    }
}