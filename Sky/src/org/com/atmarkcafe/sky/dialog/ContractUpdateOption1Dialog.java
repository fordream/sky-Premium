//package org.com.atmarkcafe.sky.dialog;
//
//import org.com.atmarkcafe.sky.customviews.charting.MTextView;
//
//import z.lib.base.LogUtils;
//import android.os.Bundle;
//import android.support.v4.app.DialogFragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.view.Window;
//
//import com.SkyPremiumLtd.SkyPremium.R;
//
//public class ContractUpdateOption1Dialog extends DialogFragment implements OnClickListener {
//	private static final String TAG = "ContractUpdateAnnualDialogOption";
//	private MTextView option1, option2;
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//
//		View view = inflater.inflate(R.layout.v2_contract_popup1_layout, null);
//		getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//		option1 = (MTextView) view
//				.findViewById(R.id.option1);
//		option1.setOnClickListener(this);
//
//		option2 = (MTextView) view
//				.findViewById(R.id.option2);
//		option2.setOnClickListener(this);
//		return view;
//	}
//
//	@Override
//	public void onClick(View v) {
//		switch (v.getId()) {
//		case R.id.option1:
//			LogUtils.e(TAG,"value==" + option1.getText());
//			ContractUpdateAnnualDialog.addViewOption1(2,option1.getText().toString().trim());
//			break;
//		case R.id.option2:
//			ContractUpdateAnnualDialog.addViewOption1(1,option2.getText().toString().trim());
//			break;
//		default:
//			break;
//		}
//
//		if (getDialog().isShowing()) {
//			getDialog().dismiss();
//		}
//	}
//}