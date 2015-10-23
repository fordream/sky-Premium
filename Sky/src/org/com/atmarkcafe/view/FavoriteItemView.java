package org.com.atmarkcafe.view;

import org.com.atmarkcafe.sky.dialog.MyAccountFavoriteDialog;

import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import z.lib.base.ImageLoaderUtils;
import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.SkyPremiumLtd.SkyPremium.R;

public class FavoriteItemView extends BaseView implements OnClickListener {
	public FavoriteItemView(Context context) {
		super(context);
		init(R.layout.v2_myaccount_favorite_item);
		
	}

	public FavoriteItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(R.layout.v2_myaccount_favorite_item);
	}

	@Override
	public void init(int res) {
		super.init(res);
		findViewById(R.id.mya_favorite_delete).setOnClickListener(this);
	}

	@Override
	public void refresh() {
		super.refresh();
		setText( ""+ Html.fromHtml( ((BaseItem) getData()).getString("name") ).toString().trim() , R.id.name);
		ImageView imageView = (ImageView) findViewById(R.id.thumbnail);
		ImageLoaderUtils.getInstance(getActivity()).displayImageEcProduct( ((BaseItem) getData()).getString("thumbnail") , imageView );
		setText( ""+ Html.fromHtml( ((BaseItem) getData()).getString("description_short") ).toString().trim() , R.id.description_short);
//		setText( ((BaseItem) getData()).getString("description_short"), R.id.description_short);
	}

	@Override
	public void onClick(View v) {
		if (mainFragment != null) {
			switch (v.getId()) {
			case R.id.mya_favorite_delete:
				mainFragment.removeFavorite((BaseItem) getData());
				break;
			default:
				break;
			}
		}
	}

	MyAccountFavoriteDialog mainFragment;
	public void addFragment(MyAccountFavoriteDialog mydialog) {
		// TODO Auto-generated method stub
		mainFragment = mydialog;
	}
}