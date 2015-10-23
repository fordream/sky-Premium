package org.com.atmarkcafe.sky.dialog;

import com.SkyPremiumLtd.SkyPremium.R;

import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;

import z.lib.base.Base2Adialog;
import z.lib.base.CommonAndroid;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

public class EcFinishDialog extends Base2Adialog implements android.view.View.OnClickListener {
	RelativeLayout main_detail;
	protected static final String TAG = "EcFinishDialog";
	private org.com.atmarkcafe.view.HeaderECView header;
	public EcFinishDialog(Context context, OnClickListener clickListener) {
		super(context, clickListener);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		main_detail = (RelativeLayout) findViewById(R.id.main_detail);
		header = CommonAndroid.getView(main_detail, R.id.header);
		header.initHeader(R.string.ec_finish_header, R.string.ec_finish_header_j);
		header.visibivityLeftFinish(false, R.drawable.header_v2_icon_back);
		header.visibivityRight(false, 0);
		CommonAndroid.getView(main_detail, R.id.bt_continue).setOnClickListener(this);
		RootActivity.onLoadEC = true;
	}
	
	@Override
	public void onBackPressed(){
//		close(true);
		finish();
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
		return R.layout.ec_finish_layout;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_continue:
//			close(true);
			finish();
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
		try {
			if(RootActivity.PJ_Detail != null){
				RootActivity.PJ_Detail.dismiss();
				RootActivity.PJ_Detail = null;
			}
			if(RootActivity.PJ_AddToCart != null){
				RootActivity.PJ_AddToCart.dismiss();
			}
			if(RootActivity.PJ_ListCat != null){
				RootActivity.PJ_ListCat.dismiss();
			}
			if(RootActivity.PJ_FavoriteDialog != null){
				RootActivity.PJ_FavoriteDialog.dismiss();
			}
			if(RootActivity.PJ_HistoryDialog != null){
				RootActivity.PJ_HistoryDialog.dismiss();
			}
			if(RootActivity.PJ_HistoryDetailDialog != null){
				RootActivity.PJ_HistoryDetailDialog.dismiss();
			}
		} catch (Exception e) {}
		if (getActivity() != null) {
			getActivity().finish();
			try {
				if(MyCartActivity.EC_FINISH != null){
					MyCartActivity.EC_FINISH.dismiss();
				}
			} catch (Exception e) {}
		}
	}
	

}