package org.com.atmarkcafe.view;

import org.com.atmarkcafe.sky.dialog.EcCreditDialog;

import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import z.lib.base.CommonAndroid;
import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;

public class EcCreditItemView extends BaseView implements OnClickListener {
	private Setting setting;
	private boolean languageID = false; // English = true, japan = false

	public EcCreditItemView(Context context) {
		super(context);
		init(R.layout.ec_credit_item_layout);
		setting = new Setting(context);
		// languageID = setting.isLangEng();
	}

	public EcCreditItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(R.layout.ec_credit_item_layout);
	}

	@Override
	public void init(int res) {
		super.init(res);
		findViewById(R.id.select_credit).setOnClickListener(this);
	}

	@Override
	public void refresh() {
		super.refresh();
		setText(((BaseItem) getData()).getString("holder_name"),
				R.id.ec_credit_name);
		// setText( ((BaseItem) getData()).getString("phone"),
		// R.id.ec_credit_tel);
		setText(((BaseItem) getData()).getString("number"),R.id.ec_credit_cardnum);
		String time = ((BaseItem) getData()).getString("expiry_date");
		if (time.indexOf("-") > 0) {
			Log.i("ConvertDate", " before :+== " + time);
			time = time.replace("-", "/");
		}
		Log.i("ConvertDate", " before : " + time);
		int spaceChar = time.indexOf("/");
		if(spaceChar > 0){
			String time1 = time.substring(0, spaceChar);
			String time2 = time.substring(spaceChar).replace("/", "");
			Log.i("ConvertDate", "time1 :" + time1);
			Log.i("ConvertDate", "time2 :" + time2);
			if (setting.isLangEng()) {
				
				if (time1.length() == 4) { // time 1 = year
					time = time2+"/"+time1;
				}else{ // time1 == 2
					time = time1 + "/" + time2;
				}
			}else{
				if (time1.length() == 4) { // time 1 = year
					time = time1+"/"+time2;
				}else{ // time1 == 2
					time = time2 + "/" + time1;
				}
			}
		}
		time = CommonAndroid.convertDate(getActivity(),  time  , 1);
		Log.i("ConvertDate", " after : " + time);
		setText(time, R.id.ec_credit_exp);
		setText(((BaseItem) getData()).getString("security_code"), R.id.mya_creditnew_code);
		setText(((BaseItem) getData()).getString("phone"),R.id.mya_credit_tel);
		setText(((BaseItem) getData()).getString("email"),R.id.mya_creditnew_email);
	}

	@Override
	public void onClick(View v) {
		if (mainFragment != null) {
			switch (v.getId()) {
			case R.id.select_credit:
				mainFragment.selectCredit((BaseItem) getData());
				break;
			default:
				break;
			}
		}
	}

	EcCreditDialog mainFragment;
	public void addFragment(EcCreditDialog credit) {
		// TODO Auto-generated method stub
		mainFragment = credit;
	}

}