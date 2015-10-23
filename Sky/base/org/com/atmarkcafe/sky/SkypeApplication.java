package org.com.atmarkcafe.sky;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.com.atmarkcafe.sky.dialog.EcAddToCartDialog;
import org.com.atmarkcafe.sky.dialog.EcAddressDialog;
import org.com.atmarkcafe.sky.dialog.EcCreditDialog;
import org.com.atmarkcafe.sky.dialog.EcFinishDialog;
import org.com.atmarkcafe.sky.dialog.EcProductsDetailDialog;
import org.com.atmarkcafe.sky.dialog.EcUpdateAddressEditDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountAddressUpdateEditDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountFavoriteDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountMyAddressDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountMyCreditDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountOptionDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountOrderHistoryDetailDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountOrderHistoryDialog;
import org.com.atmarkcafe.sky.dialog.ProfileUpdateDialog;
import org.com.atmarkcafe.sky.fragment.AboutSpmcFragment;
import org.com.atmarkcafe.sky.fragment.ChangepwdFragment;
import org.com.atmarkcafe.sky.fragment.ContactFragment;
import org.com.atmarkcafe.sky.fragment.ECFragment;
import org.com.atmarkcafe.sky.fragment.EmailDetailFragment;
import org.com.atmarkcafe.sky.fragment.FaqFragment;
import org.com.atmarkcafe.sky.fragment.GalleryDetailFragment;
import org.com.atmarkcafe.sky.fragment.GalleryFragment;
import org.com.atmarkcafe.sky.fragment.HomeFragment;
import org.com.atmarkcafe.sky.fragment.InviteFriendFragment;
import org.com.atmarkcafe.sky.fragment.MailboxFragment;
import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;
import org.com.atmarkcafe.sky.fragment.NewsFragment;
import org.com.atmarkcafe.sky.fragment.PravicyFragment;
import org.com.atmarkcafe.sky.fragment.ProfileFragment;
import org.com.atmarkcafe.sky.fragment.ServiceList2Fragment;
import org.com.atmarkcafe.sky.fragment.ServiceListFragment;
import org.com.atmarkcafe.sky.fragment.TermFragment;
import org.com.atmarkcafe.sky.fragment.TradeFragment;
import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.DataStore;
import z.lib.base.ImageLoaderUtils;
import z.lib.base.LibsBaseAdialog;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient;
import z.lib.base.callback.RestClient.RequestMethod;
import z.lib.base.downloadpdf.FileDownloadPDFCache;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Categories;
import com.acv.cheerz.db.Download;
import com.acv.cheerz.db.ECGallery;
import com.acv.cheerz.db.MainGallery;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.ProductsOfCategories;
import com.acv.cheerz.db.ServiceList;
import com.acv.cheerz.db.Setting;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.SaveCallback;

public class SkypeApplication extends Application {

	/**
	 * download
	 */
	public static Context context;
	private static String TAG = "SkypeApplication";

//	private static final String PROPERTY_ID = "UA-64049832-1";
//	public static final String PROPERTY_ID_TEST = "UA-66607508-1";
//	private static final boolean TRACKER_TEST = true;
	public static int GENERAL_TRACKER = 0;

//	public enum TrackerName {
//		APP_TRACKER, // Tracker used only in this app.
//		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
//						// roll-up tracking.
//		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a
//							// company.
//	}

	// HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName,
	// Tracker>();
	public static String Parse_app_key = "";
	public static String Parse_client_key = "";

	@Override
	public void onCreate() {
		super.onCreate();
		context = this;
		SkypeGaUtils.intance().init(this);
		LogUtils.i(TAG, "onCreate");
		ImageLoaderUtils.getInstance(this);
		FileDownloadPDFCache.getInstance(this).clearHistoryDownload();

		Parse_app_key = this.getResources().getString(R.string.parse_app_key);
		Parse_client_key = this.getResources().getString(
				R.string.parse_client_key);
		LogUtils.i("settingPush", "==========" + new Setting(this).isPush());

		// LogUtils.e("XXXX", "application context = " +
		// getApplicationContext());
		try {
			// if (new Setting(getApplicationContext()).isPush().equals("1")) {
			// deleteInstallationCache(this);
			Parse.initialize(context, Parse_app_key, Parse_client_key);
			ParseInstallation.getCurrentInstallation().saveInBackground(
					new SaveCallback() {
						@Override
						public void done(ParseException e) {
							if (e == null) {
								LogUtils.i(TAG, "regis_parse done");
							} else {
								e.printStackTrace();
								LogUtils.i(TAG,
										"regis_parse err:" + e.getMessage());
							}
						}
					});
			// }
		} catch (Exception e) {
			// e.printStackTrace();
			LogUtils.e("settingPush", "==========" + new Setting(this).isPush());
		}
		initImageLoader(getApplicationContext());

	}

	public static boolean deleteInstallationCache(Context context) {
		boolean deletedParseFolder = false;
		File cacheDir = context.getCacheDir();
		File parseApp = new File(cacheDir.getParent(), "app_Parse");
		File installationId = new File(parseApp, "installationId");
		File currentInstallation = new File(parseApp, "currentInstallation");
		if (installationId.exists()) {
			deletedParseFolder = deletedParseFolder || installationId.delete();
		}
		if (currentInstallation.exists()) {
			deletedParseFolder = deletedParseFolder
					&& currentInstallation.delete();
		}
		LogUtils.i(TAG, "clearApplicationData delete============"
				+ deletedParseFolder);
		return deletedParseFolder;
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

	@SuppressWarnings("deprecation")
	protected void TestGAParse() {
		// TODO Auto-generated method stub
		ParseAnalytics.trackAppOpenedInBackground(new Intent());
		Map<String, String> dimensions = new HashMap<String, String>();
		dimensions.put("total", "2");

		// Send the dimensions to Parse along with the 'search' event
		ParseAnalytics.trackEventInBackground("search", dimensions);
	}

	public void execute(final RequestMethod method, final String api,
			final Map<String, String> params,
			final CheerzServiceCallBack cheerzServiceCallBack) {
		LogUtils.i(TAG, "execute 1");
		execute(method, api, params, new HashMap<String, String>(),
				cheerzServiceCallBack);
		// if (cheerzServiceCallBack != null) {
		// cheerzServiceCallBack.onStart();
		// }
		// params.put("req[lang]", new Setting(this).getLangStr());
		// if (!params.containsKey("req[param][user_id]")) {
		// params.put("req[param][user_id]", new Account(this).getUserId());
		// }
		//
		// if (!params.containsKey("req[token_key]")) {
		// params.put("req[token_key]", new Account(this).getToken());
		// }
		//
		// new AsyncTask<String, String, String>() {
		// @Override
		// protected void onPostExecute(String result) {
		// super.onPostExecute(result);
		// if (cheerzServiceCallBack != null) {
		// if (client.getResponseCode() == 200) {
		// try {
		// JSONObject jsonObject = new JSONObject(
		// client.getResponse());
		// String is_succes = CommonAndroid.getString(
		// jsonObject, SkyUtils.KEY.is_success);
		// String err_msg = CommonAndroid.getString(
		// jsonObject, SkyUtils.KEY.err_msg);
		// if (SkyUtils.VALUE.STATUS_API_SUCCESS
		// .equals(is_succes)) {
		// cheerzServiceCallBack.onSucces(client
		// .getResponse());
		// } else {
		// cheerzServiceCallBack.onError(err_msg);
		// }
		//
		// return;
		// } catch (Exception exception) {
		// }
		// }
		//
		// cheerzServiceCallBack
		// .onError(getString(new Setting(
		// SkypeApplication.this).isLangEng() ? R.string.msg_err_connect
		// : R.string.msg_err_connect_j));
		// }
		// }
		//
		// private RestClient client;
		//
		// @Override
		// protected String doInBackground(String... paramStrs) {
		//
		// client = new RestClient(SkyUtils.API.BASESERVER + api);
		// client.addHeader("Content-Type",
		// "application/json; charset=utf-8");
		// client.addHeader("Accept", "application/json; charset=utf-8");
		// Set<String> keys = params.keySet();
		//
		// for (String key : keys) {
		// client.addParam(key, params.get(key));
		// }
		//
		// client.execute(method);
		// updateData(api, params, client);
		//
		// return null;
		// }
		// }.execute("");
	}

	public void execute(final RequestMethod method, final String api,
			final JSONObject params,
			final CheerzServiceCallBack cheerzServiceCallBack) {
		LogUtils.i(TAG, "execute 2");
		execute(method, api, params, new HashMap<String, String>(),
				cheerzServiceCallBack);
	}

	private void updateData(String api, Map<String, String> params,
			RestClient client) {
		LogUtils.i(TAG, "execute 3");
		if (!SkyUtils.isApiSuccess(client.getResponse())) {
			return;
		} else {
			if (API.API_USER_LOGIN.equals(api)) {
				new Account(this).updateWhenLogin(client.getResponseCode(),
						this, client.getResponse(), params);
			} else if (API.API_SERVICE_LIST.equals(api)) {
				if ("service-list".equals(params.get("req[param][slug]"))) {
					// home update
					new ServiceList(this).updateServiceList(client
							.getResponse());
				} else if ("downloads".equals(params.get("req[param][slug]"))) {
					new Download(this).update(client.getResponse());
				} else if ("gallery".equals(params.get("req[param][slug]"))) {
					LogUtils.i("SkypeApplication", "add data gallery");
					new MainGallery(this).updateGallery(client.getResponse());
				}

			} else if (API.API_EC_PRODUCTLIST.equals(api)) {
				if(RootActivity.PJ_Detail == null){
					updateEc(client.getResponse());
				}
			} else if (API.API_EC_PRODUCTDETAIL.equals(api)) {
				// LogUtils.e("AAAAAAAAAAAAAA", client.getResponse());
				updateEcDetail(client.getResponse());
			} else if (API.API_USER_LOGOUT.equals(api)) {
				// new Account(this).updateWhenLogin(client.getResponseCode(),
				// this, client.getResponse(), params);
			} else if (API.API_EC_SEARCH.equals(api)) {
				// updateSearch(client.getResponse());
			}
		}

	}

	private void updateEc(String response) {
		// Ec ec = new Ec(this);
		LogUtils.i(TAG, "execute 4");
		try {
			JSONArray data = new JSONObject(response).getJSONArray("data");
			// GlobalFunction.writeText2File(response,"DATA_EC");
			// Delete cache
			new Categories(this).deleteCat();
			new Products(this).deleteProductEc();
			new ProductsOfCategories(this).deleteProductsOfCategories();
			for (int i = 0; i < data.length(); i++) {
				JSONObject ec_data = data.getJSONObject(i);
				String id_ec = CommonAndroid.getString(ec_data, Categories.id);
				// ec.addEc(ec_data);
				// add category
				// if(i == 6){
				// CommonAndroid.writeTextToFile(ec_data.toString());
				// }
				new Categories(this).addCategory(ec_data, id_ec);

				JSONArray products_data = ec_data.getJSONArray("products");

				addProductEc(0, products_data, id_ec);
			}
		} catch (Exception e) {
		}
	}

	private void updateEcDetail(String response) {
		try {
			String data = new JSONObject(response).getString("data");
			JSONObject product = new JSONObject(data);
			JSONArray galleryList = product.getJSONArray("gallery");
			String id_product = product.getString("id");
			addGalery(0, galleryList, id_product);
			LogUtils.e(TAG, "updateEcDetail========");
		} catch (Exception e) {
		}
	}

	private void addProductEc(int index, JSONArray productList,
			String id_category) {
		// Log.i(TAG, "execute 5");
		LogUtils.i(TAG, "execute 5==" + productList.length() + "==::::::==" + index);
		if (productList.length() > index && index <= 200) {
			try {
				try {
					JSONObject product = productList.getJSONObject(index);
					String id_product = CommonAndroid.getString(product,
							Products.id);
					/**
					 * update products
					 */
					new Products(this).addProductEc(product, id_category);

					/**
					 * update category
					 */

					// TODO: comment test
					// JSONArray categoriyList = product.getJSONArray("categories");
					// addCategory(0, categoriyList, id_product);

					JSONArray galleryList = product.getJSONArray("gallery");
					addGalery(0, galleryList, id_product);
				} catch (Exception e) {}
				// new ProductsOfCategories(this).addProduct(id_product,
				// id_category);
				addProductEc(index + 1, productList, id_category);

			} catch (Exception e) {}
		}

	}

	private void addGalery(int index, JSONArray galleryList, String id_product) {
		if (galleryList.length() > index) {
			try {
				String galery = galleryList.getString(index);
				new ECGallery(this).addGalery(galery, id_product);
				addGalery(index + 1, galleryList, id_product);
			} catch (Exception exception) {
				LogUtils.e("AAAAAAAAAAAAAA", exception);
			}
		}
	}

	/*
	 * private void addCategory(int index, JSONArray categoriyList, String
	 * id_product) { if (categoriyList.length() > index) { try { JSONObject
	 * category = categoriyList.getJSONObject(index); // new
	 * Categories(this).addCategory(category); new
	 * ProductsOfCategories(this).addProduct(id_product,
	 * CommonAndroid.getString(category, Categories.id));
	 * 
	 * addCategory(index + 1, categoriyList, id_product); } catch (Exception
	 * exception) {
	 * 
	 * } } }
	 */

	/**
	 * FONTS
	 * 
	 * @author teemo
	 * 
	 */
	public interface IConfigApplication {
		public void onXStart();

		public void onSuccess();
	}

	public void init(final IConfigApplication configApplication) {
		configApplication.onXStart();

		new AsyncTask<String, String, String>() {
			@Override
			protected String doInBackground(String... params) {

				for (String font : fonts) {
					if (!hashMap.containsKey(font)) {
						try {
							hashMap.put(font, Typeface.createFromAsset(
									getAssets(), "fonts/" + font));
						} catch (Exception exception) {

						}
					}
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				configApplication.onSuccess();
			}
		}.execute("");
	}

	public HashMap<String, Typeface> hashMap = new HashMap<String, Typeface>();
	public static final String fonts[] = new String[] {//
	// "helveticaneue.ttf",
			"helveticaneuebold.ttf",//
			"helveticaneuelight.ttf",//
			"helveticaneuelight.ttf",//
			"helveticaneuemedium.ttf",//

	};

	public void setFonts(int txtType, TextView editText) {
		try {
			if (txtType <= fonts.length - 1) {
				editText.setTypeface(hashMap.get(fonts[txtType]));
			}
		} catch (Exception exception) {

		}
	}

	/**
	 * 
	 * @param method
	 * @param api
	 * @param params
	 * @param headers
	 * @param cheerzServiceCallBack
	 */
	public void execute(final RequestMethod method, final String api,
			final Map<String, String> params,
			final Map<String, String> headers,
			final CheerzServiceCallBack cheerzServiceCallBack) {
		if (cheerzServiceCallBack != null) {
			cheerzServiceCallBack.onStart();
		}
		if (method.equals(RequestMethod.GET)) {
			params.put("req[lang]", new Setting(this).getLangStr());
		}
		if (!params.containsKey("req[param][user_id]")
				&& method.equals(RequestMethod.GET)) {
			params.put("req[param][user_id]", new Account(this).getUserId());
		}

		if (!params.containsKey("req[token_key]")
				&& method.equals(RequestMethod.GET)) {
			params.put("req[token_key]", new Account(this).getToken());
		}
		//
		params.put("req[version]",
				SkyUtils.getVersionName(getApplicationContext()));
		params.put("req[os]", "android");

		LogUtils.i("API", "param-send: " + params.toString());
		LogUtils.i("API", "token-send: " + new Account(this).getToken());
		new AsyncTask<String, String, String>() {
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (cheerzServiceCallBack != null) {
					LogUtils.i(TAG, "STATUS-CODE : " + client.getResponseCode());
					LogUtils.i(TAG, "Respone-" + client.getResponse());
					if (client.getResponseCode() == 200
							&& client.getResponse() != null) {
						// LogUtils.e(TAG, "API-StartEnd : " +
						// System.currentTimeMillis());
						// GlobalFunction.writeText2File(result, "FILE_DATA");
						try {
							JSONObject jsonObject = new JSONObject(
									client.getResponse());
							String is_succes = CommonAndroid.getString(
									jsonObject, SkyUtils.KEY.is_success);
							String err_msg = CommonAndroid.getString(
									jsonObject, SkyUtils.KEY.err_msg);
							int err_msg_acc_conflic = 0;
							int err_msg_timeout = 0;

							// try {
							// LogUtils.e("AAAAAAAAAAAAAAAAA",
							// client.getResponse());
							// } catch (Exception e) {
							// // TODO Auto-generated catch block
							// e.printStackTrace();
							// }
							// GlobalFunction.writeText2File(jsonObject.toString());

							LogUtils.i(TAG, "====>1 " + is_succes);
							// if (err_msg_acc_conflic == 1) {
							// cheerzServiceCallBack.onErrorAccountConflic(SkyUtils.convertStringErrF(err_msg));
							// Log.i(TAG, "onErrorAccountConflic: " +
							// is_succes);
							// }else if(err_msg_timeout == 1){
							// cheerzServiceCallBack.onErrorTimeOut(SkyUtils.convertStringErrF(err_msg));
							// Log.i(TAG, "onErrorTimeOut: " + is_succes);
							// }

							if (SkyUtils.VALUE.STATUS_API_SUCCESS
									.equals(is_succes)) {
								cheerzServiceCallBack.onSucces(client
										.getResponse());
								LogUtils.i(TAG, "onSucces: " + is_succes);
								// JSONObject jsonDataSuccess =
								// jsonObject.getJSONObject("data");
								// if
								// (jsonDataSuccess.has(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE))
								// {
								// Intent intent = new Intent();
								// intent.setAction("com.atmarkcafe.CUSTOM_INTENT");
								// intent.putExtra("isAvailable", false);
								// intent.putExtra("msg",
								// jsonDataSuccess.getString(SkyUtils.KEY.PRODUCT_NOT_AVAILABLE));
								// sendBroadcast(intent);
								// }
							} else {
								if (jsonObject.has("data")) {
									JSONObject jsonData = jsonObject
											.getJSONObject("data");
									if (jsonData
											.has(SkyUtils.KEY.err_msg_acc_conflic)) {
										err_msg_acc_conflic = jsonData
												.getInt(SkyUtils.KEY.err_msg_acc_conflic);
									} else if (jsonData
											.has(SkyUtils.KEY.err_msg_timeout)) {
										err_msg_timeout = jsonData
												.getInt(SkyUtils.KEY.err_msg_timeout);
										;
									}

									LogUtils.i(TAG,
											"=== err_msg_acc_conflic : "
													+ err_msg_acc_conflic);
									LogUtils.i(TAG, "=== err_msg_timeout : "
											+ err_msg_timeout);
									if (err_msg_acc_conflic == 1) {
										// cheerzServiceCallBack.onErrorAccountConflic(SkyUtils.convertStringErrF(err_msg));
										Intent intent = new Intent();
										intent.setAction("com.atmarkcafe.CUSTOM_INTENT");
										intent.putExtra("msg", SkyUtils
												.convertStringErr(err_msg));
										sendBroadcast(intent);
										LogUtils.i(TAG, "Call 1");

									} else if (err_msg_timeout == 1) {
										// cheerzServiceCallBack.onErrorTimeOut(SkyUtils.convertStringErrF(err_msg));
										Intent intent = new Intent();
										intent.setAction("com.atmarkcafe.CUSTOM_INTENT");
										intent.putExtra("msg", SkyUtils
												.convertStringErr(err_msg));
										sendBroadcast(intent);
										LogUtils.i(TAG, "2");
									} else {
										cheerzServiceCallBack.onError(SkyUtils
												.convertStringErrF(err_msg));
									}
								} else {
									cheerzServiceCallBack.onError(SkyUtils
											.convertStringErrF(err_msg));
								}

								LogUtils.i(TAG, "onError: " + is_succes);
							}

							return;
						} catch (Exception exception) {
							exception.printStackTrace();

						}
					} else {
						cheerzServiceCallBack
								.onError(getString(new Setting(
										SkypeApplication.this).isLangEng() ? R.string.msg_err_connect_new
										: R.string.msg_err_connect_new_j));
					}

				}
			}

			private RestClient client;

			@Override
			protected String doInBackground(String... paramStrs) {
				LogUtils.e(TAG, "API-StartTime : " + System.currentTimeMillis());
				LogUtils.e("SkyUtils.API.BASESERVER",
						"SkyUtils.API.BASESERVER==" + SkyUtils.API.BASESERVER);
				client = new RestClient(SkyUtils.API.BASESERVER + api);
				client.addHeader("Content-Type",
						"application/json; charset=utf-8");
				client.addHeader("Accept", "application/json; charset=utf-8");
				Set<String> keys = params.keySet();

				for (String key : keys) {
					client.addParam(key, params.get(key));
				}

				if (headers != null) {
					Set<String> keyHeaders = headers.keySet();
					for (String key : keyHeaders) {
						client.addParam(key, headers.get(key));
					}
				}
				client.execute(method);
				updateData(api, params, client);

				return null;
			}
		}.execute("");
	}

	public void execute(final RequestMethod method, final String api,
			final JSONObject jsonbject, final Map<String, String> headers,
			final CheerzServiceCallBack cheerzServiceCallBack) {
		if (cheerzServiceCallBack != null) {
			cheerzServiceCallBack.onStart();
		}
		new AsyncTask<String, String, String>() {
			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);
				if (cheerzServiceCallBack != null) {
					LogUtils.i(TAG, "STATUS-CODE : " + client.getResponseCode());
					LogUtils.i(TAG, "Respone-" + client.getResponse());
					if (client.getResponseCode() == 200
							&& client.getResponse() != null) {
						try {
							JSONObject jsonObject = new JSONObject(
									client.getResponse());
							String is_succes = CommonAndroid.getString(
									jsonObject, SkyUtils.KEY.is_success);
							String err_msg = CommonAndroid.getString(
									jsonObject, SkyUtils.KEY.err_msg);
							int err_msg_acc_conflic = 0;
							int err_msg_timeout = 0;
							// LogUtils.e("AAAAAAAAAAAAAAAAA",
							// client.getResponse());
							if (SkyUtils.VALUE.STATUS_API_SUCCESS
									.equals(is_succes)) {
								cheerzServiceCallBack.onSucces(client
										.getResponse());
							} else {
								// cheerzServiceCallBack.onError(SkyUtils
								// .convertStringErrF(err_msg));

								if (jsonObject.has("data")) {
									JSONObject jsonData = jsonObject
											.getJSONObject("data");
									if (jsonData
											.has(SkyUtils.KEY.err_msg_acc_conflic)) {
										err_msg_acc_conflic = jsonData
												.getInt(SkyUtils.KEY.err_msg_acc_conflic);
									} else if (jsonData
											.has(SkyUtils.KEY.err_msg_timeout)) {
										err_msg_timeout = jsonData
												.getInt(SkyUtils.KEY.err_msg_timeout);
										;
									}
									// LogUtils.i(TAG,
									// "=== err_msg_acc_conflic : " +
									// err_msg_acc_conflic);
									// LogUtils.i(TAG, "=== err_msg_timeout : "
									// + err_msg_timeout);

									if (err_msg_acc_conflic == 1) {
										// cheerzServiceCallBack.onErrorAccountConflic(SkyUtils.convertStringErrF(err_msg));

										Intent intent = new Intent();
										intent.setAction("com.atmarkcafe.CUSTOM_INTENT");
										intent.putExtra("msg", SkyUtils
												.convertStringErr(err_msg));
										sendBroadcast(intent);
										LogUtils.i(TAG, "Call 1");

									} else if (err_msg_timeout == 1) {
										// cheerzServiceCallBack.onErrorTimeOut(SkyUtils.convertStringErrF(err_msg));

										Intent intent = new Intent();
										intent.setAction("com.atmarkcafe.CUSTOM_INTENT");
										intent.putExtra("msg", SkyUtils
												.convertStringErr(err_msg));
										sendBroadcast(intent);
										LogUtils.i(TAG, "2");
									} else {
										cheerzServiceCallBack.onError(SkyUtils
												.convertStringErrF(err_msg));
									}
								} else {
									cheerzServiceCallBack.onError(SkyUtils
											.convertStringErrF(err_msg));
								}

								LogUtils.i(TAG, "onError: " + is_succes);

							}

							return;
						} catch (Exception exception) {
						}
					}

					cheerzServiceCallBack
							.onError(getString(new Setting(
									SkypeApplication.this).isLangEng() ? R.string.msg_err_connect_new
									: R.string.msg_err_connect_new_j));
				}
			}

			private RestClient client;

			@Override
			protected String doInBackground(String... paramStrs) {
				LogUtils.e("SkyUtils.API.BASESERVER",
						"SkyUtils.API.BASESERVER==" + SkyUtils.API.BASESERVER);
				client = new RestClient(SkyUtils.API.BASESERVER + api);
				client.addHeader("Content-Type",
						"application/json; charset=utf-8");
				client.addHeader("Accept", "application/json; charset=utf-8");

				if (headers != null) {
					Set<String> keyHeaders = headers.keySet();
					for (String key : keyHeaders) {
						client.addParam(key, headers.get(key));
					}
				}
				client.addParam2(jsonbject);
				client.execute(method);

				return null;
			}
		}.execute("");
	}

	public static void initImageLoader(Context context) {
		DisplayImageOptions options = new DisplayImageOptions.Builder()
				.showImageForEmptyUri(R.drawable.mask_background)
				.showImageOnFail(R.drawable.mask_background)
				.resetViewBeforeLoading().cacheOnDisc()
				.imageScaleType(ImageScaleType.EXACTLY)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(300)).build();

		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.defaultDisplayImageOptions(options)
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCacheExtraOptions(480, 800)
				.diskCacheExtraOptions(480, 800, null)
				.threadPoolSize(3)
				// default
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new LruMemoryCache(10 * 1024 * 1024))
				.memoryCacheSize(10 * 1024 * 1024)
				.memoryCacheSizePercentage(13)
				// default
				.diskCacheSize(50 * 1024 * 1024).diskCacheFileCount(100)
				.diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
				.imageDownloader(new BaseImageDownloader(context)) // default
				.defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
				.writeDebugLogs().build();

		com.nostra13.universalimageloader.core.ImageLoader.getInstance().init(
				config);
	}

	// public void GA(String page) {
	// if (page == null || "".equals(page)) {
	// return;
	// }
	// LogUtils.i(TAG, "GA==" + page);
	// /*
	// * Tracker t = getTracker( TrackerName.APP_TRACKER); t.send(new
	// * HitBuilders.EventBuilder().setCategory(page)
	// * .setAction(page).setLabel(page).build()); t.setScreenName(page);
	// * t.send(new HitBuilders.AppViewBuilder().build());
	// */
	//
	// Tracker t = getTracker(TrackerName.APP_TRACKER);
	// t.send(new HitBuilders.EventBuilder()
	// .setCategory(getString(R.string.menu_service_list))
	// .setAction(getString(R.string.menu_service_list))
	// .setLabel(page).build());
	// t.setScreenName(page);
	// t.send(new HitBuilders.AppViewBuilder().build());
	// }
	//
	// public synchronized Tracker getTracker(TrackerName trackerId) {
	// if (!mTrackers.containsKey(trackerId)) {
	//
	// GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
	// Tracker t = analytics.newTracker(PROPERTY_ID);
	// if (TRACKER_TEST) {
	// t = analytics.newTracker(PROPERTY_ID_TEST);
	// }
	// mTrackers.put(trackerId, t);
	//
	// }
	// return mTrackers.get(trackerId);
	// }

	// private static void GA(String page, Activity activity) {
	// boolean isEnglish = new Setting(activity).isLangEng();
	// LogUtils.ENABLE = true;
	// String KEY = "GA_KEY";
	// HashMap<String, Integer> maps = new HashMap<String, Integer>();
	//
	//
	//
	//
	// // maps.put(key, isEnglish ? R.string.ga_category_eng :
	// R.string.ga_category_jap);
	// // maps.put(key, isEnglish ? R.string.ga_new_product_eng :
	// R.string.ga_new_product_jap);
	// // maps.put(key, isEnglish ? R.string.ga_platinum_product_eng :
	// R.string.ga_platinum_product_jap);
	// // maps.put(key, isEnglish ? R.string.ga_carefully_selected_gourmet_eng :
	// R.string.ga_carefully_selected_gourmet_jap);
	// // maps.put(key, isEnglish ? R.string.ga_wine_sake_eng :
	// R.string.ga_wine_sake_jap);
	// // maps.put(key, isEnglish ? R.string.ga_food_beverage_eng :
	// R.string.ga_food_beverage_jap);
	// // maps.put(key, isEnglish ? R.string.ga_art_eng : R.string.ga_art_jap);
	// // maps.put(key, isEnglish ? R.string.ga_cart__eng :
	// R.string.ga_cart__jap);
	// // maps.put(key, isEnglish ? R.string.ga_fruit_eng :
	// R.string.ga_fruit_jap);
	// // maps.put(key, isEnglish ? R.string.ga_payment_eng :
	// R.string.ga_payment_jap);
	// // maps.put(key, isEnglish ? R.string.ga_deliver_method_eng :
	// R.string.ga_deliver_method_jap);
	// // maps.put(key, isEnglish ? R.string.ga_deliver_address_eng :
	// R.string.ga_deliver_address_jap);
	// // maps.put(key, isEnglish ? R.string.ga_credit_information_eng :
	// R.string.ga_credit_information_jap);
	// // maps.put(key, isEnglish ? R.string.ga_credit_card_payment_eng :
	// R.string.ga_credit_card_payment_jap);
	//
	// LogUtils.e(KEY, page);
	// maps.put(HomeFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_service_list : R.string.menu_service_list_j);
	// maps.put(MailboxFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_mailbox : R.string.menu_mailbox_j);
	// maps.put(EmailDetailFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_mailbox : R.string.menu_mailbox_j);
	// maps.put(GalleryFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_gallery : R.string.menu_gallery_j);
	// maps.put(FaqFragment.class.getSimpleName(), isEnglish ? R.string.menu_faq
	// : R.string.menu_faq_j);
	// maps.put(ContactFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_contact_us : R.string.menu_contact_us_j);
	// maps.put(NewsFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_news : R.string.menu_news_j);
	// maps.put(AboutSpmcFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_about_spmc : R.string.menu_about_spmc_j);
	// maps.put(InviteFriendFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_invite_friend : R.string.menu_invite_friend_j);
	// maps.put(TradeFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_trade_law : R.string.menu_trade_law_j);
	// maps.put(PravicyFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_privacy : R.string.menu_privacy_j);
	// maps.put(TermFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_term : R.string.menu_term_j);
	// maps.put(ChangepwdFragment.class.getSimpleName(), isEnglish ?
	// R.string.menu_change_pass : R.string.menu_change_pass_j);
	// maps.put(GalleryDetailFragment.class.getSimpleName(), isEnglish ?
	// R.string.ga_product_detail_eng : R.string.ga_product_detail_jap);
	//
	// maps.put(ECFragment.class.getSimpleName(), isEnglish ?
	// R.string.ga_product_detail_eng : R.string.ga_product_detail_jap);
	//
	//
	// // maps.put(ServiceListFragment.class.getSimpleName(), isEnglish ?
	// R.string.ga_product_detail_eng : R.string.ga_product_detail_jap);
	// // maps.put(ServiceList2Fragment.class.getSimpleName(), isEnglish ?
	// R.string.ga_product_detail_eng : R.string.ga_product_detail_jap);
	//
	//
	// maps.put(EcAddressDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_address_eng : R.string.ga_address_jap);
	// maps.put(MyAccountMyAddressDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_address_eng : R.string.ga_address_jap);
	// maps.put(MyAccountOptionDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_my_account_eng : R.string.ga_my_account_jap);
	// maps.put(EcUpdateAddressEditDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_address_edit_eng : R.string.ga_address_edit_jap);
	// maps.put(MyAccountAddressUpdateEditDialog.class.getSimpleName(),
	// isEnglish ? R.string.ga_address_edit_eng : R.string.ga_address_edit_jap);
	// maps.put(MyAccountOrderHistoryDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_purchase_history_eng : R.string.ga_purchase_history_jap);
	// maps.put(MyAccountOrderHistoryDetailDialog.class.getSimpleName(),
	// isEnglish ? R.string.ga_purchase_history_detail_eng :
	// R.string.ga_purchase_history_detail_jap);
	// maps.put(MyAccountFavoriteDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_favourite_product_eng : R.string.ga_favourite_product_jap);
	// maps.put(ProfileFragment.class.getSimpleName(), isEnglish ?
	// R.string.ga_user_information_eng : R.string.ga_user_information_jap);
	// maps.put(ProfileUpdateDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_user_information_eng : R.string.ga_user_information_jap);
	// maps.put(MyAccountMyCreditDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_credit_information_eng : R.string.ga_credit_information_jap);
	// maps.put(EcCreditDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_credit_information_eng : R.string.ga_credit_information_jap);
	// maps.put(ProfileFragment.class.getSimpleName(), isEnglish ?
	// R.string.ga_contract_management_eng :
	// R.string.ga_contract_management_jap);
	// maps.put(EcAddToCartDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_add_to_cart_eng : R.string.ga_add_to_cart_jap);
	// maps.put(EcProductsDetailDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_product_detail_eng : R.string.ga_product_detail_jap);
	// maps.put(EcFinishDialog.class.getSimpleName(), isEnglish ?
	// R.string.ga_order_successful_eng : R.string.ga_order_successful_jap);
	// maps.put(MyCart2Fragment.class.getSimpleName(), isEnglish ?
	// R.string.ga_shoping_cart_list_eng : R.string.ga_shoping_cart_list_jap);
	//
	//
	//
	// if(!maps.containsKey(page)){
	// return;
	// }
	//
	// int res = maps.get(page);
	//
	//
	// if(activity == null){
	// return;
	// }
	//
	// String xpage =activity.getString(res) + "_ANDROID";
	//
	// DataStore.getInstance().init(activity);
	// String data = DataStore.getInstance().get(KEY, "[]");
	// JSONArray array = new JSONArray();
	// try {
	// array = new JSONArray(data);
	// JSONObject jsonObject = new JSONObject();
	// jsonObject.put("name", page + " : " + xpage);
	// boolean isAdd = true;
	// for (int i = 0; i < array.length(); i++) {
	// if (array.getJSONObject(i).toString()
	// .equals(jsonObject.toString())) {
	// isAdd = false;
	// }
	// }
	//
	// if (isAdd) {
	// array.put(jsonObject);
	// }
	// } catch (Exception e) {
	// LogUtils.e(KEY, e);
	// }
	//
	// String userId = new Account(activity).getUserId();
	// if (CommonAndroid.isBlank(userId)) {
	// } else if (!userId.startsWith("123")) {
	// ((SkypeApplication) activity.getApplicationContext()).GA(userId + " " +
	// xpage);
	// }
	//
	// GoogleAnalytics.getInstance(activity).reportActivityStart(activity);
	//
	//
	// DataStore.getInstance().save(KEY, array.toString());
	// LogUtils.ENABLE = false;
	// }
	//
	// public static void GA(BaseFragment page, Activity activity) {
	// GA(page.getClass().getSimpleName(), activity);
	// }
	//
	// public static void GA(LibsBaseAdialog page, Activity activity) {
	// GA(page.getClass().getSimpleName(), activity);
	// }
}
