package org.com.atmarkcafe.sky.dialog;

import com.SkyPremiumLtd.SkyPremium.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class SkypeProgressDialog extends ProgressDialog {

	public SkypeProgressDialog(Context context) {
		super(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);
	}

}