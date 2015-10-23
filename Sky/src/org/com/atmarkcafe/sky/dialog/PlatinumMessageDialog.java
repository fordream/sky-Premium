package org.com.atmarkcafe.sky.dialog;

import z.lib.base.BaseAdialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;

public class PlatinumMessageDialog extends BaseAdialog implements android.view.View.OnClickListener {
	public PlatinumMessageDialog(Context context, String message, OnClickListener onClickListener, int strOkie) {
		super(context);

		this.message = message;
		this.onClickOkListener = onClickListener;
		this.strOkie = strOkie;
	}

	private String message;
	private OnClickListener onClickOkListener;
	private int strOkie = 0;
	private TextView txt_content, txt_okie;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		txt_content = (TextView) findViewById(R.id.txt_content);
		txt_okie = (TextView) findViewById(R.id.txt_okie);
		txt_content.setText(message);
		txt_okie.setText(strOkie);
		txt_okie.setOnClickListener(this);

	}

	@Override
	public int getLayout() {
		return R.layout.dialog_platinum_upgrade;
	}

	@Override
	public void onClick(View v) {
		dismiss();
		if (v.getId() == R.id.txt_okie) {
			if (onClickOkListener != null) {
				onClickOkListener.onClick(null, 0);
			}
		}
	}
}