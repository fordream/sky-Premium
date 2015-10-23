package com.acv.cheerz.db;

import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.CommonAndroid;
import z.lib.base.SkyUtils;
import android.content.ContentValues;
import android.content.Context;

public class Download extends SkypeTable {
	public Download(Context context) {
		super(context);

		addColumns(user_id);
		addColumns(slug);
		addColumns(thumbnail);
		addColumns(url);
		addColumns(id);
		addColumns(title);
		addColumns(content);
	}

	public static final String id = "id";
	public static final String title = "title";
	public static final String user_id = "user_id";
	public static final String slug = "slug";
	public static final String url = "url";
	public static final String thumbnail = "thumbnail";
	public static final String content = "content";

	@Override
	public int getIndex() {
		return 5;
	}

	public void update(String response) {
		String user_idStr = new Account(getContext()).getUserId();
		try {
			JSONObject json = new JSONObject(response);
//			String is_succes = CommonAndroid.getString(json, SkyUtils.KEY.is_success);

//			if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
				JSONArray array = json.getJSONArray("data");

				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					String idStr = CommonAndroid.getString(object, id);

					if (!CommonAndroid.isBlank(idStr)) {
						String where = String.format("%s = '%s' and %s = '%s'", id, idStr, user_id, user_idStr);
						ContentValues values = new ContentValues();

						values.put(id, CommonAndroid.getString(object, id));
						values.put(title, CommonAndroid.getString(object, title));
						values.put(content, CommonAndroid.getString(object, content));
						values.put(slug, CommonAndroid.getString(object, slug));
						values.put(url, CommonAndroid.getString(object, url));
						values.put(thumbnail, CommonAndroid.getString(object, thumbnail));
						values.put(user_id, user_idStr);

						if (has(where)) {
							getContext().getContentResolver().update(getContentUri(), values, where, null);
						} else {
							getContext().getContentResolver().insert(getContentUri(), values);
						}
					}
				}
//			}
		} catch (Exception e) {
		}
	}

}
