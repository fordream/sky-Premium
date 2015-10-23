package org.com.atmarkcafe.sky.dialog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.Base2Adialog;
import z.lib.base.BaseAdapter;
import z.lib.base.BaseItem;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.Setting;

public class EcShipTearmDialog extends Base2Adialog implements android.view.View.OnClickListener {
	View views;
	RelativeLayout main_detail;
	protected static final String TAG = "EcShipDialog";
	private BaseAdapter baseAdapter;
	List<BaseItem> baseItemsAddbook = new ArrayList<BaseItem>();
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	ScrollView scrolllistview;
	LinearLayout linearLayoutForm;
	private WebView credit;
	CheckBox check_team;
	ScrollView detail;
	String tearm_content = "";
	public EcShipTearmDialog(Context context, String tearm_content_, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		this.tearm_content = tearm_content_;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.ec_ship_team_header, R.string.ec_ship_team_header_j);
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);// icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		linearLayoutForm = (LinearLayout) findViewById(R.id.linearLayoutForm);
		
		credit = new WebView(getContext());
		credit.setBackgroundColor(Color.parseColor("#ededed"));
		
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.term_value);
		rl.addView(credit);
		viewTeam(tearm_content);
	}
	
	private void viewTeam(String content) {
		try {
			credit.loadDataWithBaseURL(null, content,"text/html", "utf-8", null);
			credit.setWebChromeClient(new WebChromeClient() {
			});

			credit.setWebViewClient(new WebViewClient() {
				public boolean shouldOverrideUrlLoading(WebView view, String url) {
					return false;
				}

				public void onPageFinished(WebView view, String url) {
					credit.stopLoading();
					Log.e("TIME1", "2 : " + System.currentTimeMillis() + "");
					return;
				}

			});
		} catch (Exception e) {
			Log.e("TIME3", System.currentTimeMillis() + "");
			// credit.stopLoading();
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			Log.e("TIME4", System.currentTimeMillis() + "");
		}
	}

	@Override
	public void onBackPressed(){
		close(true);
	}
	
	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.main_detail), new AnimationAction() {

			@Override
			public void onAnimationEnd() {
				dismiss();
				if (isOnClick) {
					clickListener.onClick(null, 0);
				}
			}
		});
	}

	@Override
	public int getLayout() {
		return R.layout.ec_ship_tearm_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.header_btn_left:
			close(true);
			break;
		default:
			break;
		}
	}
	
	private FragmentActivity getActivity() {
		return MyCart2Fragment.activity;
	}
	
	public boolean isFinish() {
		if (getActivity() == null) {
			return true;
		}

		return getActivity().isFinishing();
	}

	public void finish() {
		if (getActivity() != null) {
			getActivity().finish();
		}
	}
	

}