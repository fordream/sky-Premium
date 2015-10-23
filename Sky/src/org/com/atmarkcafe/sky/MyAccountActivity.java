package org.com.atmarkcafe.sky;

import org.com.atmarkcafe.sky.dialog.MyAccountOrderHistoryDialog;

import z.lib.base.BaseActivity;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.SCREEN;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import com.SkyPremiumLtd.SkyPremium.R;

public class MyAccountActivity extends BaseActivity {

	private static final String TAG = "MyAccountActivity";
	public static Dialog EC_CREDIT = null;
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		SkyUtils.SCREEN screen = (SkyUtils.SCREEN) getIntent().getSerializableExtra(SkyUtils.KEY.KEY_SCREEN);
		if (screen == SCREEN.MYACCOUNT_HISTORY) {
			new MyAccountOrderHistoryDialog(this, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
				}
				
			}).show();
		} 
		
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
		finish();
		super.onBackPressed();
//		RootActivity.back = true;
//		finish();
//		super.onBackPressed();
		return;
	}
	
	

}
