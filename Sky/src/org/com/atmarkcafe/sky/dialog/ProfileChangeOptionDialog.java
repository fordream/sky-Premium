package org.com.atmarkcafe.sky.dialog;

import com.SkyPremiumLtd.SkyPremium.R;

import z.lib.base.BaseAdialog;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ProfileChangeOptionDialog extends BaseAdialog implements android.view.View.OnClickListener {
	private static final String TAG = "ProfileChangeOptionDialog";
	private String data[];
	private String title_dialog = null;
	public ProfileChangeOptionDialog(Context context, String data[], OnClickListener clickListener) {
		super(context, clickListener);
		this.data = data;
	}
	
	public ProfileChangeOptionDialog(Context context, String data[], String title, OnClickListener clickListener) {
		super(context, clickListener);
		this.data = data;
		this.title_dialog = title;
	}

	@Override
	public void onBackPressed() {
		close(-1);
		// super.onBackPressed();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(title_dialog != null){
			LinearLayout header_main = (LinearLayout) findViewById(R.id.dialog_select_title_main);
			header_main.setVisibility(View.VISIBLE);
			TextView header = (TextView) findViewById(R.id.dialog_select_title);
			header.setText(title_dialog);
//			header.setBackgroundDrawable(null);
		}
		if (data != null) {
			try {
				TextView option1 = (TextView) findViewById(R.id.dialog_select_option1);
				option1.setText(data[0].toString());
				TextView option2 = (TextView) findViewById(R.id.dialog_select_option2);
				option2.setText(data[1].toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				LogUtils.e(TAG, e.getMessage());
				e.printStackTrace();
			}
		}
		setCancelable(true);
		openPopActivity(findViewById(R.id.dialog_main));
		findViewById(R.id.dialog_main).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close(-1);
			}
		});

		findViewById(R.id.dialog_select_option1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close(0);
			}
		});

		findViewById(R.id.dialog_select_option2).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close(1);
			}
		});
	}

	private void close(final int index) {
		closePopActivity(getContext(), findViewById(R.id.dialog_main), new AnimationAction() {
			@Override
			public void onAnimationEnd() {
				dismiss();
				clickListener.onClick(null, index);
			}
		});
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public int getLayout() {
		return R.layout.profile_user_dialog;
	}
}