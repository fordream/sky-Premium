package org.com.atmarkcafe.sky;

import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;

import z.lib.base.BaseActivity;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import android.app.Dialog;
import android.os.Bundle;

import com.SkyPremiumLtd.SkyPremium.R;

public class MyCartActivity extends BaseActivity {

	private static final String TAG = "MyCartActivity";
	public static Dialog EC_ADDRESS = null;
	public static Dialog EC_SHIP = null;
	public static Dialog EC_CREDIT = null;
	public static Dialog EC_CREDIT_CONFIRM = null;
	public static Dialog EC_FINISH = null;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		RootActivity.back = false;
		if (getFragemtCount() > 0) {
			return;
		}
		startFragment(new MyCart2Fragment(), getIntent().getExtras());
		
	}

	@Override
	public int getLayout() {
		return R.layout.root;
	}

	@Override
	public int getResMain() {
		return R.id.root;
	}

	@Override
	public void finish() {
		super.finish();
		if (getIntent().getBooleanExtra(SkyUtils.KEY.KEY_SCREEN_TAG, false)) {
			overridePendingTransition(R.anim.a_nothing, R.anim.right_out);
		}
	}
	
	@Override
	public void onBackPressed() {
		LogUtils.e(TAG, "check update mycart");
		if(MyCart2Fragment.onChangeData){
			MyCart2Fragment.Next = 1;
			MyCart2Fragment.checkout();
		}else{
			finish();
			super.onBackPressed();
		}
//		RootActivity.back = true;
//		finish();
//		super.onBackPressed();
		return;
	}
	
	

}
