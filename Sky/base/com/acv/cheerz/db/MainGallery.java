package com.acv.cheerz.db;

import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.CommonAndroid;
import android.content.ContentValues;
import android.content.Context;

public class MainGallery extends SkypeTable {
	@Override
	public int getIndex() {
		return 11;
	}

	public MainGallery(Context context) {
		super(context);
		addColumns(user_id);
		addColumns(id_products);
		addColumns(url);
		addColumns(page);
		addColumns(id);
		addColumns(title);
		addColumns(content);
		addColumns(slug);
		addColumns(thumbnail);
		addColumns(download_url);
		addColumns(external_url);
		addColumns(is_platinum_accessed);
		addColumns(platinum_accessed_msg);
		addColumns(type);
		addColumns(video_src);
	}

	public static final String user_id = "user_id";
	public static final String id_products = "id_products";
	public static final String url = "url";
	public static final String page = "page";
	public static final String id = "id";
	public static final String title = "title";
	public static final String subtitle = "title";
	public static final String content = "content";
	public static final String slug = "slug";
	public static final String thumbnail = "thumbnail";
	public static final String download_url = "download_url";
	public static final String external_url = "external_url";
	public static final String is_platinum_accessed = "is_platinum_accessed";
	public static final String platinum_accessed_msg = "platinum_accessed_msg";
	public static final String type = "type";
	public static final String video_src = "video_src";
	public static final String video_src_android = "video_src_android";
	
	public void updateGallery(String response) {
		String user_idStr = new Account(getContext()).getUserId();
		getContext().getContentResolver().delete(getContentUri(), "user_id=? ", new String[]{user_idStr});
		try {
			JSONObject json = new JSONObject(response);
				JSONArray array = json.getJSONArray("data");

				for (int i = 0; i < array.length(); i++) {
					JSONObject object = array.getJSONObject(i);
					String idStr = CommonAndroid.getString(object, id);

					if (!CommonAndroid.isBlank(idStr)) {
						String where = String.format("%s = '%s' and %s = '%s'", id, idStr, user_id, user_idStr);
						ContentValues values = new ContentValues();

						values.put(id, CommonAndroid.getString(object, id));
						values.put(title, CommonAndroid.getString(object, title));
						values.put(subtitle, CommonAndroid.getString(object, subtitle));
						values.put(page, CommonAndroid.getString(object, page));
						values.put(content, CommonAndroid.getString(object, content));
						values.put(slug, CommonAndroid.getString(object, slug));
						values.put(url, CommonAndroid.getString(object, url));
						values.put(thumbnail, CommonAndroid.getString(object, thumbnail));
						values.put(download_url, CommonAndroid.getString(object, download_url));
						values.put(external_url, CommonAndroid.getString(object, external_url));
						values.put(is_platinum_accessed, CommonAndroid.getString(object, is_platinum_accessed));
						values.put(platinum_accessed_msg, CommonAndroid.getString(object, platinum_accessed_msg));
						values.put(type, CommonAndroid.getString(object, type));
						values.put(user_id, user_idStr);
						
						String urlVideo = CommonAndroid.getString(object, video_src_android);
						if (urlVideo.equalsIgnoreCase("")) {
							urlVideo = CommonAndroid.getString(object, video_src);
							values.put(video_src, CommonAndroid.getString(object, video_src));
						}else{
							values.put(video_src, CommonAndroid.getString(object, video_src_android));
						}
//						values.put(video_src, CommonAndroid.getString(object, video_src));
						
						
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
	
//	public void addGalery(String xurl, String id_product) {
//		try {
//			ContentValues values = new ContentValues();
//			values.put(id_products, id_product);
//			values.put(url, xurl);
//			values.put(user_id, new Account(getContext()).getUserId());
//			
//			String where = String.format("%s = '%s' and %s = '%s' and %s = '%s'"//
//					, user_id, new Account(getContext()).getUserId()//
//					, id_products, id_product//
//					, url, xurl//
//					);//
//			
//			if (has(where)) {
//				getContext().getContentResolver().update(getContentUri(), values, where, null);
//			} else {
//				getContext().getContentResolver().insert(getContentUri(), values);
//			}
//		} catch (Exception exception) {
//		}
//	}

}
