package org.com.atmarkcafe.view;
import org.com.atmarkcafe.sky.dialog.MyAccountMyAddressDialog;

import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.SkyPremiumLtd.SkyPremium.R;

public class EcAddressBookItemView2 extends BaseView implements OnClickListener {
	private static final String TAG = "EcAddressBookItemView2";
	public EcAddressBookItemView2(Context context) {
		super(context);
		init(R.layout.v2_ec_addressbook_item_layout);
		
	}

	public EcAddressBookItemView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(R.layout.v2_ec_addressbook_item_layout);
	}

	@Override
	public void init(int res) {
		super.init(res);
		findViewById(R.id.mya_address_update).setOnClickListener(this);
		findViewById(R.id.mya_address_delete).setOnClickListener(this);
	}

	@Override
	public void refresh() {
		super.refresh();
		String name = "";
		if( !"".equals( checkIsNull("fullname") ))
			name = checkIsNull("fullname");
		else
			name = checkIsNull("firstname") + " " + checkIsNull("lastname");
		setText( name, R.id.mya_address_name);
		setText( checkIsNull("alias"), R.id.mya_address_alias);
		setText( checkIsNull("company"), R.id.mya_address_company);
		setText( checkIsNull("address1"), R.id.mya_address_address1);
		setText( checkIsNull("address2"), R.id.mya_address_address2);
		setText( checkIsNull("postcode"), R.id.mya_address_code);
		setText( checkIsNull("city"), R.id.mya_address_city);
		setText( checkIsNull("state"), R.id.mya_address_prefectures);
		setText( checkIsNull("country"), R.id.mya_address_country);
		setText( checkIsNull("phone"), R.id.mya_address_tel);
		setText( checkIsNull("phone_mobile"), R.id.mya_address_mobilephone);
		setText( checkIsNull("other"), R.id.mya_address_more);
		
	}
	
	private String checkIsNull(String param){
		try {
			String str_check = ((BaseItem) getData()).getString(param);
			if("null".equals(str_check))
				return "";
			return str_check.toString().trim();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "";
		}
	}

	@Override
	public void onClick(View v) {
		if (mainFragment != null) {
			switch (v.getId()) {
			case R.id.mya_address_update:
				LogUtils.e(TAG, "mya_address_update");
				mainFragment.update((BaseItem) getData());
				break;
			case R.id.mya_address_delete:
				mainFragment.delete((BaseItem) getData());
				break;
			default:
				break;
			}
		}
	}
	
	MyAccountMyAddressDialog mainFragment;
	public void addFragment(MyAccountMyAddressDialog mydialog) {
		// TODO Auto-generated method stub
		mainFragment = mydialog;
	}

}