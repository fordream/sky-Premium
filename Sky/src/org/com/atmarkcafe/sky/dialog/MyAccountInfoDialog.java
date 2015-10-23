package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.List;

import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;

import z.lib.base.Base2Adialog;
import z.lib.base.BaseAdapter;
import z.lib.base.BaseItem;
import z.lib.base.CommonAndroid;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;

public class MyAccountInfoDialog extends Base2Adialog implements android.view.View.OnClickListener {
	View views;
	RelativeLayout main_detail;
	protected static final String TAG = "MyAccountInfoDialog";
	private BaseAdapter baseAdapter;
	List<BaseItem> baseItemsAddbook = new ArrayList<BaseItem>();
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderECView header;
	LinearLayout content_main;;
	ScrollView detail;
	FragmentActivity activity;
	public MyAccountInfoDialog(FragmentActivity context, OnClickListener clickListener) {
		super(context, clickListener);
		this.activity = context;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.myaccount, R.string.myaccount_j);
		header.visibivityLeft(false, R.drawable.header_v2_icon_back);//icon_nav_back
		header.visibivityRight(false, 0);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		content_main = CommonAndroid.getView(main_detail, R.id.content_main);
		
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
		return R.layout.v2_myaccount_myinfo_layout;
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
		return activity;
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