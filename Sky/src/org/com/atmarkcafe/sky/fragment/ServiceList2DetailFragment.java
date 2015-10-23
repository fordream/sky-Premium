package org.com.atmarkcafe.sky.fragment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderSquareUtils;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;

@SuppressLint("ValidFragment")
public class ServiceList2DetailFragment extends BaseFragment implements
		OnClickListener {

	private String TAG = "ServiceList2DetailFragment";
	private String mSlug, mType, mTitle;
	private ImageButton btnHome;
	private ImageButton btnRight;
	private HeaderView txtTile;
	private WebView webview;
	private LoadingView loading;
	private ImageView imgThum;
	private String mContent, mUrlExternal;
	private String mParentTitle;
	private int sWidth, sHeight;
	private TextView txtSubtitle;
	private TextView txtMsgUpgrate;
	private TextView txtConfirmUpgrate;
	private String strMsgUpdate = "";
	private int flagCheckUpgrate = 0;
	private LinearLayout ll_bottom;

	public ServiceList2DetailFragment() {
		Log.i(TAG, "init ServiceList2DetailFragment");
	}

	@SuppressLint("ValidFragment")
	public ServiceList2DetailFragment(String slug, String type, String title,
			String content, String parentTitle, String urlExternal) {
		this.mSlug = slug;
		this.mType = type;
		this.mTitle = title;
		this.mContent = content;
		this.mParentTitle = parentTitle;
		this.mUrlExternal = urlExternal;

		Log.i(TAG, "slug = " + " type = " + type + " UrlExetenal = "
				+ mUrlExternal);
	}

	@Override
	public int getLayout() {

		return R.layout.service_list_layout;
	}

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	@Override
	public void init(View view) {
		// CommonAndroid.changeColorImageView(getActivity(),
		// R.drawable.btn_upgrate,
		// getActivity().getResources().getColor(R.color.selection_upgrade_now));
		// CommonAndroid.changeColorImageView(getActivity(),
		// R.drawable.btn_upgrate_select,
		// getActivity().getResources().getColor(R.color.selection_upgrade_now_select));
		Log.i(TAG, "init view");
		loading = (LoadingView) view.findViewById(R.id.loading);
		CommonAndroid.showView(false, loading);
		getProductList();

		btnHome = (ImageButton) view.findViewById(R.id.header_btn_left);
		btnHome.setImageResource(R.drawable.icon_back);
		btnHome.setOnClickListener(this);

		btnRight = (ImageButton) view.findViewById(R.id.header_btn_right);
		btnRight.setVisibility(View.INVISIBLE);

		txtTile = CommonAndroid.getView(view, R.id.header_id);
		txtTile.initHeader(mTitle, mTitle);
		// txtTile.setText(mTitle);
		txtSubtitle = (TextView) view
				.findViewById(R.id.servicelist_subtitle_id);

		imgThum = (ImageView) view.findViewById(R.id.servicelist_thumnail_id);

		webview = (WebView) view.findViewById(R.id.servicelist_webview_id);
		webview.getSettings().setJavaScriptEnabled(true);
		webview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		// webview.loadDataWithBaseURL(null, mContent, "text/html", "utf-8",
		// null);
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		sWidth = size.x;
		sHeight = size.y;
		txtMsgUpgrate = (TextView) view
				.findViewById(R.id.servicelist_msg_update_id);
		txtConfirmUpgrate = (TextView) view
				.findViewById(R.id.servicelist_img_upgrate_id);
		Setting setting = new Setting(getActivity());
		if (!setting.isLangEng()) {
			txtConfirmUpgrate.setTextSize(14.0f);
		}
		txtConfirmUpgrate.setOnClickListener(this);
		// CommonAndroid.getView(view,
		// R.id.servicelist_img_upgrate_id).setOnClickListener(this);
		ll_bottom = (LinearLayout) view
				.findViewById(R.id.servicelist_ll_bottom_id);

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			ServiceList2Fragment homeFragment = new ServiceList2Fragment(
					mParentTitle);

			if (getActivity() instanceof SkyMainActivity) {
				SkyMainActivity skyActivity = (SkyMainActivity) getActivity();
				skyActivity.switchContent(homeFragment, "");
			}

			break;
		case R.id.servicelist_img_upgrate_id:
			ContactFragment contactFragment = new ContactFragment();
			Log.i(TAG, "sang contact us cho a ");
			if (getActivity() instanceof SkyMainActivity) {
				SkyMainActivity skyActivity = (SkyMainActivity) getActivity();
				skyActivity.switchContent(contactFragment, "");
				Log.i(TAG, "sang contact us cho a ");
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onChangeLanguage() {
		Log.i(TAG, "===>>> onChangeLanguage()");
		getProductList();
	}

	private void getProductList() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("req[param][slug]", mSlug);
		params.put("req[param][type]", mType);
		SkyUtils.execute(getActivity(), RequestMethod.GET,
				SkyUtils.API.API_SERVICE_LIST, params,
				new CheerzServiceCallBack() {

					@Override
					public void onStart() {
						CommonAndroid.showView(true, loading);
						super.onStart();
					}

					@Override
					public void onError(String message) {
						CommonAndroid.showView(false, loading);
						super.onError(message);
					}

					@Override
					public void onSucces(String response) {
						super.onSucces(response);
						CommonAndroid.showView(false, loading);
						Log.i(TAG, "data = " + response);
						try {
							JSONObject jsonRes = new JSONObject(response);
							JSONArray jsonData = jsonRes.getJSONArray("data");
							String content = null;
							String urlThum = null;
							String mSubtitle = null;
							for (int i = 0; i < jsonData.length(); i++) {
								JSONObject jobj = jsonData.getJSONObject(i);
								content = jobj.getString("content");
								urlThum = jobj.getString("thumbnail");
								mSubtitle = jobj.getString("subtitle");
								mTitle = jobj.getString("title");
								strMsgUpdate = jobj
										.getString("platinum_accessed_msg");
								flagCheckUpgrate = jobj
										.getInt("is_platinum_accessed");
							}
							if (mSubtitle.equalsIgnoreCase("")) {
								LayoutParams params = new LayoutParams(0, 0);
								txtSubtitle.setLayoutParams(params);
							}
							if (mSlug.equalsIgnoreCase("platinum-vacations")) {
								content = content.replaceAll("<p>&nbsp;</p>", "<p></p>");
								processData(content);
							} else {
								content = SkyUtils.addStyleContent(content);
								// content = content.replaceAll("#0099ff",
								// "#2a2a2a");
								// content = content.replaceAll("#D8D8D8",
								// "#2a2a2a");
								webview.loadDataWithBaseURL(null, content,
										"text/html", "utf-8", null);
								txtTile.initHeader(mTitle, mTitle);
							}
							// gold account
							String contract_type = new Account(getActivity())
									.getContractType();
							if (flagCheckUpgrate == 1
									&& "2".equalsIgnoreCase(contract_type)) {
								// show

							} else {
								// hide
							}
							ImageLoaderSquareUtils.getInstance(getActivity())
									.displayImageHomeSlide(urlThum, imgThum);

						} catch (Exception e) {
							e.printStackTrace();
						}

					}
				});
	}

	private void processData(String content) {
		content = content.replace("width=\"1280\"", "width=\"100%\"");
		String str = "";
		str += loadResouceHTMLFromAssets("html/project_detail_header.html");
		if (content != null) {
			str += content;
		}
		str += loadResouceHTMLFromAssets("html/project_detail_footer.html");
		webview.loadDataWithBaseURL("file:///android_asset/", str, "text/html",
				"UTF-8", null);
	}

	public String loadResouceHTMLFromAssets(String filename) {
		String tmp = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(getActivity()
					.getAssets().open(filename)));
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

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			Log.i(TAG, "===W : " + result.getWidth());
			Log.i(TAG, "===H : " + result.getHeight());
			bmImage.setImageBitmap(result);
		}
	}

	private void writeText2File(String data) {
		try {
			File myFile = new File("/sdcard/productlist1.txt");
			myFile.createNewFile();
			FileOutputStream fOut = new FileOutputStream(myFile);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(data);
			myOutWriter.close();
			fOut.close();
			Log.i(TAG, "write done");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
