//package org.com.atmarkcafe.sky.dialog;
//
//import z.lib.base.BaseAdialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.SkyPremiumLtd.SkyPremium.R;
//
//public class ContractUpdateOptionDialog_XXX extends BaseAdialog implements android.view.View.OnClickListener {
//	public ContractUpdateOptionDialog_XXX(Context context, String message, OnClickListener onClickListener, OnClickListener onClickListener2, int option11, int option22) {
//		super(context);
//		this.message = message;
//		this.onClickOption1Listener = onClickListener;
//		this.onClickOption2Listener = onClickListener2;
//		this.option1 = option11;
//		this.option2 = option22;
//	}
//
//	private String message;
//	private OnClickListener onClickOption1Listener, onClickOption2Listener;
//	private int option1 = 0;
//	private int option2 = 0;
//
//	private TextView txt_option1, txt_option2;
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		txt_option1 = (TextView) findViewById(R.id.option1);
//		txt_option2 = (TextView) findViewById(R.id.option2);
//
//		txt_option1.setText(option1);
//		txt_option2.setText(option2);
//		
//		txt_option1.setOnClickListener(this);
//		txt_option2.setOnClickListener(this);
//
//	}
//
//	@Override
//	public int getLayout() {
//		return R.layout.v2_contract_popup_layout;
//	}
//
//	@Override
//	public void onClick(View v) {
//		dismiss();
//		if (v.getId() == R.id.option1) {
//			if (onClickOption1Listener != null) {
//				onClickOption1Listener.onClick(null, 0);
//			}
//
//		} else if (v.getId() == R.id.option2) {
//			if (onClickOption2Listener != null) {
//				onClickOption2Listener.onClick(null, 0);
//			}
//		}
//	}
//}