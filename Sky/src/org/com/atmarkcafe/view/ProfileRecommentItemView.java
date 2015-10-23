package org.com.atmarkcafe.view;
import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;

import com.SkyPremiumLtd.SkyPremium.R;

public class ProfileRecommentItemView extends BaseView implements OnClickListener {
	public ProfileRecommentItemView(Context context) {
		super(context);
		init(R.layout.user_recomment_item);
		
	}

	public ProfileRecommentItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(R.layout.user_recomment_item);
	}

	@Override
	public void init(int res) {
		super.init(res);
	}

	@Override
	public void refresh() {
		super.refresh();
		setText( ((BaseItem) getData()).getString("id"), R.id.profile_recomment_id);
		setText( ((BaseItem) getData()).getString("email"), R.id.profile_recomment_email);
		setText( ((BaseItem) getData()).getString("name"), R.id.profile_recomment_name);
	}

	@Override
	public void onClick(View v) {
	}

}