package org.com.atmarkcafe.sky.dialog;

import z.lib.base.BaseAdialog;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.SkyPremiumLtd.SkyPremium.R;

public class MyAccountOptionDialog extends BaseAdialog implements android.view.View.OnClickListener {
	private static final String TAG = "ContractUpdateOptionDialog";
	public MyAccountOptionDialog(Context context, OnClickListener clickListener) {
		super(context, clickListener);
	}
	
	@Override
	public void onBackPressed() {
		close(-1);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setCancelable(true);
		openPopActivity(findViewById(R.id.dialog_main));
		findViewById(R.id.dialog_main).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close(-1);
			}
		});

		findViewById(R.id.myaccount_history).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close(0);
			}
		});

		findViewById(R.id.myaccount_address).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close(1);
			}
		});
		
		findViewById(R.id.myaccount_info).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close(2);
			}
		});
		
		findViewById(R.id.myaccount_favorite).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close(3);
			}
		});

		findViewById(R.id.myaccount_credit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close(4);
			}
		});
		
		findViewById(R.id.myaccount_cancel).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				close(-1);
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
		return R.layout.v2_myaccount_selectionoption_layout;
	}
}