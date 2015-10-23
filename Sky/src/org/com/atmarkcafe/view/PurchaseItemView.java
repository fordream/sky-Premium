package org.com.atmarkcafe.view;

import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.SkyPremiumLtd.SkyPremium.R;

public class PurchaseItemView extends BaseView implements OnClickListener {
	public PurchaseItemView(Context context) {
		super(context);
		init(R.layout.user_purchase_item);
		
	}

	public PurchaseItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(R.layout.user_purchase_item);
	}

	@Override
	public void init(int res) {
		super.init(res);
	}

	@Override
	public void refresh() {
		super.refresh();
		setText( ((BaseItem) getData()).getString("no"), R.id.no);
		setText( ((BaseItem) getData()).getString("code"), R.id.code);
		setText( ((BaseItem) getData()).getString("status"), R.id.status);
		setText( ((BaseItem) getData()).getString("amount"), R.id.amount);
	}

	@Override
	public void onClick(View v) {
	}

}