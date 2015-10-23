package org.com.atmarkcafe.sky.dialog;

import z.lib.base.BaseAdialog;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;

public class ComfirmMessageDialog extends BaseAdialog implements android.view.View.OnClickListener {
	public ComfirmMessageDialog(Context context_, String message, OnClickListener onClickListener, OnClickListener onClickListener2, int strOkie, int strCancel) {
		super(context_);
		this.context = context_;
		this.message = message;
		this.onClickOkListener = onClickListener;
		this.onClickCancelListener = onClickListener2;
		this.strOkie = strOkie;
		this.strCancel = strCancel;
	}

	private String message;
	private OnClickListener onClickOkListener, onClickCancelListener;
	private int strOkie = 0;
	private int strCancel = 0;

	private TextView txt_content, txt_okie, txt_cancel;
	private LinearLayout ll_cancel;

	private Context context;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		CommonAndroid.changeColorImageView(context, R.drawable.button_activie, context.getResources().getColor(R.color.popup_btn_ok));
		CommonAndroid.changeColorImageView(context, R.drawable.button_normal, context.getResources().getColor(R.color.popup_btn_ok));//OK
		txt_content = (TextView) findViewById(R.id.txt_content);
		txt_okie = (TextView) findViewById(R.id.txt_okie);
		txt_cancel = (TextView) findViewById(R.id.txt_cancel);
		ll_cancel = (LinearLayout) findViewById(R.id.ll_cancel);

		txt_content.setText(message);
		String tlt_ok_default = new Setting(getContext()).isLangEng() ? getContext().getString(R.string.tlt_ok) : getContext().getString(R.string.tlt_ok_j);
		if(strOkie != 0)
			txt_okie.setText(strOkie);
		else
			txt_okie.setText(tlt_ok_default);
		
		if (strCancel == 0)
			ll_cancel.setVisibility(View.GONE);
		else
			txt_cancel.setText(strCancel);
		
		txt_okie.setOnClickListener(this);
		txt_cancel.setOnClickListener(this);

	}

	@Override
	public int getLayout() {
		return R.layout.dialog_comfirm;
	}

	@Override
	public void onClick(View v) {
		dismiss();
		if (v.getId() == R.id.txt_cancel) {
			if (onClickCancelListener != null) {
				onClickCancelListener.onClick(null, 0);
			}

		} else if (v.getId() == R.id.txt_okie) {
			if (onClickOkListener != null) {
				onClickOkListener.onClick(null, 0);
			}
		}
	}
}