package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;
import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.SkyMainActivity;
import se.emilsjolander.flipview.FlipView;
import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderSquareUtils;
import z.lib.base.ImageLoaderUtils;
import z.lib.base.LogUtils;
import z.lib.base.SkyLoader;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class GalleryFragment extends BaseFragment {
	private se.emilsjolander.flipview.FlipView flip;
	private ListView list;
	private LoadingView loading;
	private BaseAdapter plipAdapter;
	Drawable myIcon_select;
	Drawable myIcon_unselect;
	ImageView ico_indi_gray;
	public GalleryFragment() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.gallery_layout;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		super.onItemClick(arg0, arg1, arg2, arg3);
		Cursor cursor = (Cursor) serviceAdapter.getItem(arg2);

//		GalleryDetailFragment galleryFragment = new GalleryDetailFragment(CommonAndroid.getString(cursor, com.acv.cheerz.db.MainGallery.title)
//																	, CommonAndroid.getString(cursor, com.acv.cheerz.db.MainGallery.thumbnail)
//																	, CommonAndroid.getString(cursor, com.acv.cheerz.db.MainGallery.video_src));
		GalleryDetailFragment galleryFragment = new GalleryDetailFragment(cursor);
		if (getActivity() instanceof SkyMainActivity) {
			SkyMainActivity skyMain = (SkyMainActivity)getActivity();
			skyMain.switchContent(galleryFragment, SkyUtils.FRAGMENT.FR_GALLERY_DETAIL);
		}
		
	}

	@Override
	public void onClickHeaderLeft() {
		super.onClickHeaderLeft();
		SkyMainActivity.sm.toggle();
	}

	private void callApi() {
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][id]", "");
		inputs.put("req[param][slug]", "gallery");
		inputs.put("req[param][type]", "page");

		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_SERVICE_LIST, inputs, callback);
	}

	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {

		public void onStart() {
			if (serviceAdapter != null && serviceAdapter.getCount() > 0) {
				CommonAndroid.showView(false, loading);
			} else {
				CommonAndroid.showView(true, loading);
			}
		};

		public void onError(String message) {
			if (!isFinish()) {
				CommonAndroid.showView(false, loading);
			}
		};

		public void onSucces(String response) {
			if (!isFinish()) {
				CommonAndroid.showView(false, loading);
				LogUtils.i("Gallery", "respone : " + response);
				updateData();
			}
		};
	};

	@Override
	public void init(View view) {
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_indi_gray2, getResources().getColor(R.color.home_icon_select));//OK
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_indi_gray, getResources().getColor(R.color.home_icon_unselect));//OK
		myIcon_select = getResources().getDrawable( R.drawable.ico_indi_gray2 );
		myIcon_unselect = getResources().getDrawable( R.drawable.ico_indi_gray );
		
		initHeader(view, R.id.header, R.string.menu_gallery, R.string.menu_gallery_j, R.drawable.menu_icon, 0);
		loading = CommonAndroid.getView(view, R.id.loading);
		list = CommonAndroid.getView(view, R.id.list);
		list.setOnItemClickListener(this);

		flip = CommonAndroid.getView(view, R.id.flip);

		flip.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_MOVE) {
					SkyMainActivity.sm.setSlidingEnabled(false);
					SkyMainActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP) {
					SkyMainActivity.sm.setSlidingEnabled(true);
					SkyMainActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				}

				return false;
			}
		});
		
		flip.setOnFlipListener(new FlipView.OnFlipListener() {

			@Override
			public void onFlippedToPage(FlipView v, int position, long id) {
				if (serviceAdapter != null) {
					serviceAdapter.setPositionSelection(position);
				}
				if (position == 0) {
					SkyMainActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				}
			}
		});
		
		
		// updateData();
		callApi();
	}

	private void updateData() {

		new SkyLoader() {

			@Override
			public void loadSucess(Object data) {

				serviceAdapter = new ServiceAdapter(list, (Cursor) data);
				plipAdapter = new BaseAdapter() {
					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						if (convertView == null) {
							convertView = CommonAndroid.initView(parent.getContext(), R.layout.gallery_list_slide_item, null);
						}
						convertView.setBackgroundResource(R.color.servicelist_first);
						ImageView img = CommonAndroid.getView(convertView, R.id.img);
						TextView text_1 = CommonAndroid.getView(convertView, R.id.text_1);
//						TextView text_2 = CommonAndroid.getView(convertView, R.id.text_2);
						Cursor c = (Cursor) getItem(position);
						CommonAndroid.setText(text_1, c, com.acv.cheerz.db.MainGallery.title);
//						CommonAndroid.setText(text_2, c, com.acv.cheerz.db.MainGallery.subtitle);

//						ImageLoaderSquareUtils.getInstance(parent.getContext()).displayImageHomeSlide(CommonAndroid.getString(c, com.acv.cheerz.db.MainGallery.thumbnail), img);
						ImageLoaderSquareUtils.getInstance(getActivity()).displayImageGallery(CommonAndroid.getString(c, com.acv.cheerz.db.MainGallery.thumbnail), img);
						Gallery.LayoutParams layoutParams = new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
						convertView.setLayoutParams(layoutParams);
						convertView.setOnClickListener(new FlipOnClickItemListener(position));
						return convertView;
					}

					@Override
					public long getItemId(int position) {
						return serviceAdapter.getCount();
					}

					@Override
					public Object getItem(int position) {
						return serviceAdapter.getItem(position);
					}

					@Override
					public int getCount() {
						return serviceAdapter.getCount();
					}
				};
				flip.setAdapter(plipAdapter);
			}

			@Override
			public Object loadData() {
				String user_idStr = new Account(getActivity()).getUserId();
				String where = String.format("%s = '%s'", com.acv.cheerz.db.MainGallery.user_id, user_idStr);
				Cursor cursor = new com.acv.cheerz.db.MainGallery(getActivity()).querry(where);
				return cursor;
			}
		}.executeLoader(500);

	}

	@Override
	public void onChangeLanguage() {
		LogUtils.i("GALLERY", "Change language");
		callApi();
	}

	@Override
	public void reload() {
		super.reload();
	}

	private ServiceAdapter serviceAdapter;

	private class ServiceAdapter extends CursorAdapter {
		Handler handler = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				// list.setSelected(true);
				// list.smoothScrollToPosition(positionSelection);

				list.setSelection(positionSelection);
			}
		};
		private int positionSelection = 0;
		private ListView list;

		public void setPositionSelection(int xpositionSelection) {
			this.positionSelection = xpositionSelection;
			notifyDataSetChanged();
			handler.sendEmptyMessageAtTime(0, 5);
		}

		public ServiceAdapter(ListView list, Cursor c) {
			super(list.getContext(), c);
			this.list = list;
			list.setAdapter(this);
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			return CommonAndroid.initView(arg0, R.layout.gallery_list_item, null);
		}

		@Override
		public void bindView(View view, Context context, Cursor c) {
			if (view == null) {
				view = CommonAndroid.initView(context, R.layout.gallery_list_item, null);
			}

			ico_indi_gray = (ImageView) view.findViewById(R.id.ico_indi_gray);
			if (positionSelection == c.getPosition()) {
				view.setBackgroundResource(R.color.servicelist_first);
				ico_indi_gray.setImageDrawable(myIcon_select);
			} else {
				view.setBackgroundResource(android.R.color.transparent);
				ico_indi_gray.setImageDrawable(myIcon_unselect);
			}

			ImageView img = CommonAndroid.getView(view, R.id.img);
			TextView text_1 = CommonAndroid.getView(view, R.id.text_1);
//			TextView text_2 = CommonAndroid.getView(view, R.id.text_2);

			CommonAndroid.setText(text_1, c, com.acv.cheerz.db.MainGallery.title);
//			CommonAndroid.setText(text_2, c, com.acv.cheerz.db.MainGallery.subtitle);

			ImageLoaderUtils.getInstance(context).displayImageHome(CommonAndroid.getString(c, com.acv.cheerz.db.MainGallery.thumbnail), img);
		}
	}

	private class FlipOnClickItemListener implements View.OnClickListener {

		private int position = 0;

		public FlipOnClickItemListener(int p) {
			position = p;
		}

		@Override
		public void onClick(View v) {
			onItemClick(null, null, position, 0);
		}
	}
}
