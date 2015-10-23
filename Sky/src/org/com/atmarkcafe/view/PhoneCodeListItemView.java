package org.com.atmarkcafe.view;
import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.SkyPremiumLtd.SkyPremium.R;

public class PhoneCodeListItemView extends BaseView implements OnClickListener {
	public PhoneCodeListItemView(Context context) {
		super(context);
		init(R.layout.user_dialog_counttry_code_item);
		
	}

	public PhoneCodeListItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(R.layout.user_dialog_counttry_code_item);
	}

	@Override
	public void init(int res) {
		super.init(res);
	}

	@Override
	public void refresh() {
		super.refresh();
		setText( ((BaseItem) getData()).getString("counttry_name"),
				R.id.country_value);
	}

	@Override
	public void onClick(View v) {
	}

}