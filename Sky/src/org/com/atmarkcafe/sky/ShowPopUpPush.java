package org.com.atmarkcafe.sky;

import z.lib.base.LogUtils;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;

public class ShowPopUpPush extends Activity implements OnClickListener {

private static final String TAG = "ShowPopUpPush";

	//	boolean click = true;
	private String message;
//	private OnClickListener onClickOkListener, onClickCancelListener;
//	private int strOkie = 0;
//	private int strCancel = 0;

	private TextView txt_content, txt_okie, txt_cancel;
	private LinearLayout ll_cancel;
	private String redirect = "";
	private Activity activity;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		activity = this;
		try {
			setContentView(R.layout.dialog_push);
			txt_content = (TextView) findViewById(R.id.txt_content);
			txt_okie = (TextView) findViewById(R.id.txt_okie);
			txt_cancel = (TextView) findViewById(R.id.txt_cancel);
			ll_cancel = (LinearLayout) findViewById(R.id.ll_cancel);
			Bundle extras = getIntent().getExtras();
			if (extras != null) {
				try {
					message = extras.getString("data_push");
					txt_content.setText(message);
					redirect = extras.getString("redirect");
					LogUtils.i(TAG, "extras redirect===" + redirect);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if("".equals(redirect)){
				ll_cancel.setVisibility(View.GONE);
			}else{
				ll_cancel.setVisibility(View.VISIBLE);
			}
			
			txt_okie.setOnClickListener(this);
			txt_cancel.setOnClickListener(this);
			
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
//		1==>mail
//		2==>new
		LogUtils.i(TAG, "redirect===" + redirect);
		if("".equals(redirect)){
			Intent intent = new Intent(activity, SkyMainActivity.class);
			intent.putExtra(SkyMainActivity.EXTRA_CHECK_BACK_HOME, SkyMainActivity.EXTRA_CHECK_BACK_HOME);
	        activity.startActivityForResult(intent, 0);
			finish();
		}else{
			switch (arg0.getId()) {
			case R.id.txt_okie:
				if("1".equals(redirect)){
					LogUtils.i(TAG, "redirect=" + redirect);
					Intent intent = new Intent(activity, SkyMainActivity.class);
			        intent.putExtra(SkyMainActivity.EXTRA_CHECK_BACK_MAIL, SkyMainActivity.EXTRA_CHECK_BACK_MAIL);
			        activity.startActivityForResult(intent, 0);
				}else if("2".equals(redirect)){
					LogUtils.i(TAG, "redirect=" + redirect);
					Intent intent = new Intent(activity, SkyMainActivity.class);
			        intent.putExtra(SkyMainActivity.EXTRA_CHECK_BACK_NEW, SkyMainActivity.EXTRA_CHECK_BACK_NEW);
			        activity.startActivityForResult(intent, 0);
				}
				finish();
				break;
			case R.id.txt_cancel:
				finish();
				break;
			default:
				break;
			}
		}
		
	}
}