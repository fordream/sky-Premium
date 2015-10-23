package com.acv.cheerz.base.view;

import z.lib.base.BaseView;
import android.content.Context;
import android.util.AttributeSet;

import com.SkyPremiumLtd.SkyPremium.R;

public class LoadingView extends BaseView {

	public LoadingView(Context context) {
		super(context);
		init(R.layout.loading);
	}

	public LoadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(R.layout.loading);

		this.setOnClickListener(null);
	}
}
