package org.com.atmarkcafe.view;

import org.com.atmarkcafe.sky.dialog.MyAccountOrderHistoryDialog;

import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import z.lib.base.CommonAndroid;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.SkyPremiumLtd.SkyPremium.R;

public class PurchaseHistoryItemView extends BaseView implements OnClickListener {
	public PurchaseHistoryItemView(Context context) {
		super(context);
		init(R.layout.v2_myaccount_purchase_item);
		
	}

	public PurchaseHistoryItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(R.layout.v2_myaccount_purchase_item);
	}

	@Override
	public void init(int res) {
		super.init(res);
		findViewById(R.id.mya_history_detail).setOnClickListener(this);
		findViewById(R.id.mya_history_reorder).setOnClickListener(this);
	}

	@Override
	public void refresh() {
		super.refresh();
		setText( ((BaseItem) getData()).getString("reference"), R.id.mya_history_reference);
		setText( ((BaseItem) getData()).getString("date"), R.id.mya_history_date);
		setText( ((BaseItem) getData()).getString("payment_method"), R.id.mya_history_payment);
		setText( ((BaseItem) getData()).getString("status"), R.id.mya_history_status);
		setText( CommonAndroid.formatPriceEC(getActivity(),((BaseItem) getData()).getString("total")), R.id.mya_history_totalprice);
	}

	@Override
	public void onClick(View v) {
		if (mainFragment != null) {
			switch (v.getId()) {
			case R.id.mya_history_detail:
				mainFragment.detail((BaseItem) getData());
				break;
			case R.id.mya_history_reorder:
				mainFragment.reorder((BaseItem) getData());
				break;
			default:
				break;
			}
		}
	}

	MyAccountOrderHistoryDialog mainFragment;
	public void addFragment(MyAccountOrderHistoryDialog mydialog) {
		// TODO Auto-generated method stub
		mainFragment = mydialog;
	}
}