package org.com.atmarkcafe.view;
import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.SkyPremiumLtd.SkyPremium.R;

public class PurchaseDetailItemView extends BaseView implements OnClickListener {
	public PurchaseDetailItemView(Context context) {
		super(context);
		init(R.layout.user_purchase_detail_item);
		
	}

	public PurchaseDetailItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(R.layout.user_purchase_detail_item);
	}

	@Override
	public void init(int res) {
		super.init(res);
	}

	@Override
	public void refresh() {
		super.refresh();
		setText( ((BaseItem) getData()).getString("name"), R.id.name);
		setText( ((BaseItem) getData()).getString("price"), R.id.price);
		setText( ((BaseItem) getData()).getString("amount"), R.id.amount);
	}

	@Override
	public void onClick(View v) {
	}

}