package org.com.atmarkcafe.view;
import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.SkyPremiumLtd.SkyPremium.R;

public class HistoryProductSelectItemView extends BaseView implements OnClickListener {
	public HistoryProductSelectItemView(Context context) {
		super(context);
		init(R.layout.v2_history_product_select_item);
		
	}

	public HistoryProductSelectItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(R.layout.v2_history_product_select_item);
	}

	@Override
	public void init(int res) {
		super.init(res);
	}

	@Override
	public void refresh() {
		super.refresh();
		setText( ((BaseItem) getData()).getString("name"),
				R.id.item_value);
	}

	@Override
	public void onClick(View v) {
	}

}