package org.com.atmarkcafe.sky.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.sky.GcmBroadcastReceiver;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.SkypeApplication;
import org.com.atmarkcafe.sky.SkypeGaUtils;
import org.com.atmarkcafe.view.ECBlogView;
import org.json.JSONException;
import org.json.JSONObject;

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
import z.lib.base.SkyUtils.SCREEN;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
//import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.ServiceList;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class HomeFragment extends BaseFragment {
	private se.emilsjolander.flipview.FlipView flip;
	private ListView list;
	private LoadingView loading;
	private BaseAdapter plipAdapter;
	Boolean onLoad = true;
	Boolean DataCache = false;
	private String pageSelect = "page_select";
	private int pageIndexSelect = 0;
	private boolean flagGotoDetail = false;
	public static java.util.ArrayList<String> listSlugObject = new ArrayList<String>();
	public static java.util.ArrayList<String> listmTypeObject = new ArrayList<String>();
	Drawable myIcon_select;
	Drawable myIcon_unselect;
	ImageView ico_indi_gray;

	public HomeFragment() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.home;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		super.onItemClick(arg0, arg1, arg2, arg3);
		listSlugObject.clear();
		listmTypeObject.clear();
		ServiceListFragment productListFragment = null;
		Cursor cursor = (Cursor) serviceAdapter.getItem(arg2);
		pageIndexSelect = arg2;
		LogUtils.i(TAG, "page selected : " + pageIndexSelect);
		SkyUtils.saveInt(getActivity(), pageSelect, arg2);
		flagGotoDetail = true;
		String screen = CommonAndroid.getString(cursor, ServiceList.title);
		LogUtils.i(TAG, "Select==" + screen);
		SkypeGaUtils.intance().ga(screen);
		if ("page".equals(CommonAndroid.getString(cursor, ServiceList.type))) {
			if ("spmc-selection".equals(CommonAndroid.getString(cursor,
					ServiceList.slug))) {
				Bundle extras = new Bundle();
				startActivityForResult(SCREEN.EC__PRODUCT_LIST, extras);
				productListFragment = null;
			} else if ("life-planning".equalsIgnoreCase(CommonAndroid
					.getString(cursor, ServiceList.slug))) {
				ServiceList2Fragment serviceFragment = new ServiceList2Fragment(
						CommonAndroid.getString(cursor, ServiceList.title));
				if (getActivity() instanceof SkyMainActivity) {
					SkyMainActivity skyMain = (SkyMainActivity) getActivity();
					skyMain.switchContent(serviceFragment, "");
					return;
				}

			} else {
				productListFragment = new ServiceListFragment(
						CommonAndroid.getString(cursor, ServiceList.slug),
						CommonAndroid.getString(cursor, ServiceList.type),
						CommonAndroid.getString(cursor, ServiceList.title));
			}
		} else if ("post".equals(CommonAndroid.getString(cursor,
				ServiceList.type))) {
			productListFragment = new ServiceListFragment(
					CommonAndroid.getString(cursor, ServiceList.slug),
					CommonAndroid.getString(cursor, ServiceList.type),
					CommonAndroid.getString(cursor, ServiceList.title));
		} else if ("external".equalsIgnoreCase(CommonAndroid.getString(cursor,
				ServiceList.type))) {
			String externalUrl = CommonAndroid.getString(cursor,
					ServiceList.external_url);
			openBrowserDefault(externalUrl);
			return;
		}

		if (productListFragment != null) {
			if (getActivity() instanceof SkyMainActivity) {
				SkyMainActivity skyMain = (SkyMainActivity) getActivity();
				skyMain.switchContent(productListFragment, "Detail");
			}

			// Bundle extras = new Bundle();
			// startActivityForResult(SCREEN.MAIL_BOX, extras);
			// startFragment(productListFragment, null);
		}
		LogUtils.i(
				"Home",
				"slug = " + CommonAndroid.getString(cursor, ServiceList.slug)
						+ "-- type = "
						+ CommonAndroid.getString(cursor, ServiceList.type));

		// test badge
		// CommonAndroid.updateBadgeLauncher(getActivity(), 10);
	}

	@Override
	public void onClickHeaderLeft() {
		super.onClickHeaderLeft();
		SkyMainActivity.sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		SkyMainActivity.disableGestureMenu(true);
		SkyMainActivity.sm.toggle();
	}

	@Override
	public void onPause() {
		super.onPause();
		LogUtils.i("HOME", "onpause");
	}

	private void callApi() {
		CommonAndroid.showView(false, loading);
		if (onLoad) {
			onLoad = false;
			HashMap<String, String> inputs = new HashMap<String, String>();
			inputs.put("req[param][id]", "");
			inputs.put("req[param][slug]", "service-list");
			inputs.put("req[param][type]", "page");

			SkyUtils.execute(getActivity(), RequestMethod.GET,
					API.API_SERVICE_LIST, inputs, callback);
		}

	}

	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {

		public void onStart() {
			// if (serviceAdapter != null && serviceAdapter.getCount() > 0) {
			// CommonAndroid.showView(false, loading);
			// } else {
			if (!DataCache)
				CommonAndroid.showView(true, loading);
			// }
		};

		public void onError(String message) {
			if (!isFinish()) {
				LogUtils.i(TAG, "Err  = " + message);
				CommonAndroid.showView(false, loading);
				if (!DataCache)
					SkyUtils.showDialog(getActivity(), "" + message, null);
			}
		};

		public void onSucces(String response) {
			LogUtils.i(TAG, "Err1  = " + response);
			if (!isFinish()) {
				CommonAndroid.showView(false, loading);

				if (!DataCache)
					updateData();

			}
		};
	};
	private String TAG = "HOME";

	@Override
	public void init(View view) {
		LogUtils.i(TAG, "init-Home");
		CommonAndroid.changeColorImageView(getActivity(),
				R.drawable.ico_indi_gray2,
				getResources().getColor(R.color.home_icon_select));// OK
		CommonAndroid.changeColorImageView(getActivity(),
				R.drawable.ico_indi_gray,
				getResources().getColor(R.color.home_icon_unselect));// OK
		myIcon_select = getResources().getDrawable(R.drawable.ico_indi_gray2);
		myIcon_unselect = getResources().getDrawable(R.drawable.ico_indi_gray);

		initHeader(view, R.id.header, R.string.menu_service_list,
				R.string.menu_service_list_j, R.drawable.menu_icon, 0);
		// CommonAndroid.updateBadgeLauncher(getActivity(), 1);
		// CommonAndroid.updateBadgeLauncher(getActivity(), 10);
		loading = CommonAndroid.getView(view, R.id.loading);
		list = CommonAndroid.getView(view, R.id.list);
		list.setOnItemClickListener(this);

		flip = CommonAndroid.getView(view, R.id.flip);

		flip.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_MOVE) {
					SkyMainActivity.sm
							.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL
						|| event.getAction() == MotionEvent.ACTION_UP) {
					SkyMainActivity.sm
							.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
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
					SkyMainActivity.sm
							.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				}
			}
		});

		CommonAndroid.showView(false, loading);
		Boolean back = false;
		try {
			if ("back".equals(getArguments().getString("back"))) {
				back = true;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!back) {
			callApi();
			CommonAndroid.showView(true, loading);
		} else {
			updateData();
		}

		// callApi();

		// ico_indi_gray = (ImageView) view.findViewById(R.id.ico_indi_gray);
		// ico_indi_gray2 = (ImageView) view.findViewById(R.id.ico_indi_gray2);
	}

	private void setupStatusSelection(int page) {
		list.setItemChecked(page, true);
		list.setSelection(page);
		LogUtils.i(TAG, "setupStatusSelection");
	}

	private void updateData() {
		CommonAndroid.showView(true, loading);
		new SkyLoader() {

			@Override
			public void loadSucess(Object data) {

				Cursor cursor = (Cursor) data;
				if (cursor != null) {
					flip.removeAllViews();
					try {
						serviceAdapter = new ServiceAdapter(list, (Cursor) data);
						plipAdapter = new BaseAdapter() {
							@Override
							public View getView(int position, View convertView,
									ViewGroup parent) {
								if (convertView == null) {
									convertView = CommonAndroid.initView(
											parent.getContext(),
											R.layout.service_list_slide_item,
											null);
								}
								convertView
										.setBackgroundResource(R.color.servicelist_first);
								ImageView img = CommonAndroid.getView(
										convertView, R.id.img);
								TextView text_1 = CommonAndroid.getView(
										convertView, R.id.text_1);
								TextView text_2 = CommonAndroid.getView(
										convertView, R.id.text_2);
								Cursor c = (Cursor) getItem(position);
								CommonAndroid.setText(text_1, c,
										ServiceList.title);
								CommonAndroid.setText(text_2, c,
										ServiceList.subtitle);
								ImageLoaderSquareUtils.getInstance(
										parent.getContext())
										.displayImageHomeSlide(
												CommonAndroid.getString(c,
														ServiceList.thumbnail),
												img);
								Gallery.LayoutParams layoutParams = new Gallery.LayoutParams(
										LayoutParams.MATCH_PARENT,
										LayoutParams.MATCH_PARENT);
								convertView.setLayoutParams(layoutParams);
								convertView
										.setOnClickListener(new FlipOnClickItemListener(
												position));
								DataCache = true;
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
						plipAdapter.notifyDataSetChanged();

						// pageIndexSelect = SkyUtils.getInt(getActivity(),
						// pageSelect , pageIndexSelect);
						// list.setSelection(pageIndexSelect);
						// flip.flipTo(pageIndexSelect);
						pageIndexSelect = SkyUtils.getInt(getActivity(),
								pageSelect, pageIndexSelect);
						if (pageIndexSelect != 0) {
							list.setSelection(pageIndexSelect);
							// list.getChildAt(pageIndexSelect).setBackgroundResource(R.color.servicelist_first);
							flip.flipTo(pageIndexSelect);
							// flagGotoDetail = false;
						}
					} catch (Exception e) {
					}
				}
				callApi();
			}

			@Override
			public Object loadData() {
				String user_idStr = new Account(getActivity()).getUserId();
				String where = String.format("%s = '%s'", ServiceList.user_id,
						user_idStr);
				Cursor cursor = new ServiceList(getActivity()).querry(where);
				return cursor;
			}
		}.executeLoader(500);

	}

	@Override
	public void onChangeLanguage() {
		onLoad = true;
		DataCache = false;
		callApi();
	}

	@Override
	public void reload() {
		super.reload();
	}

	private ServiceAdapter serviceAdapter;

	private int positionSelection = 0;

	private class ServiceAdapter extends CursorAdapter {
		Handler handler = new Handler() {
			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				// list.setSelected(true);
				// list.smoothScrollToPosition(positionSelection);

				// if (pageIndexSelect != 0) {
				// setupStatusSelection(pageIndexSelect);
				// }else{
				// }
				list.setSelection(positionSelection);
			}
		};

		private ListView list;

		public void setPositionSelection(int xpositionSelection) {
			positionSelection = xpositionSelection;
			notifyDataSetChanged();
			handler.sendEmptyMessageAtTime(0, 5);
		}

		public ServiceAdapter(ListView list, Cursor c) {
			super(list.getContext(), c);
			this.list = list;
			// updateGallery();
			list.setAdapter(this);
			try {
				pageIndexSelect = SkyUtils.getInt(getActivity(), pageSelect,
						pageIndexSelect);
			} catch (Exception e) {
			}
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			return CommonAndroid.initView(arg0, R.layout.service_list_item,
					null);
		}

		@Override
		public void bindView(View view, Context context, Cursor c) {
			if (view == null) {
				view = CommonAndroid.initView(context,
						R.layout.service_list_item, null);
			}

			ico_indi_gray = (ImageView) view.findViewById(R.id.ico_indi_gray);
			if (positionSelection == c.getPosition()) {
				view.setBackgroundResource(R.color.servicelist_first);
				// int h1 = list.getHeight();
				// int h2 = view.getHeight();
				// list.smoothScrollToPositionFromTop(positionSelection, h1/2 +
				// h2/2, 1);
				ico_indi_gray.setImageDrawable(myIcon_select);
			} else {
				view.setBackgroundResource(android.R.color.transparent);
				ico_indi_gray.setImageDrawable(myIcon_unselect);
			}

			ImageView img = CommonAndroid.getView(view, R.id.img);

			TextView text_1, text_11, text_2;
			text_1 = CommonAndroid.getView(view, R.id.text_1);
			text_2 = CommonAndroid.getView(view, R.id.text_2);
			text_11 = CommonAndroid.getView(view, R.id.text_11);
			if (CommonAndroid.getString(c, ServiceList.subtitle)
					.equalsIgnoreCase("")) {
				text_11.setVisibility(View.VISIBLE);
				CommonAndroid.setText(text_11, c, ServiceList.title);
				text_1.setVisibility(View.INVISIBLE);
				text_2.setVisibility(View.INVISIBLE);
			} else {
				CommonAndroid.setText(text_1, c, ServiceList.title);
				CommonAndroid.setText(text_2, c, ServiceList.subtitle);
				text_1.setVisibility(View.VISIBLE);
				text_2.setVisibility(View.VISIBLE);
				text_11.setVisibility(View.INVISIBLE);
			}
			// plipAdapter.notifyDataSetChanged();
			ImageLoaderUtils.getInstance(context).displayImageHome(
					CommonAndroid.getString(c, ServiceList.thumbnail), img);

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

	public void openBrowserDefault(String url) {
		Intent browserIntent = new Intent("android.intent.action.VIEW",
				Uri.parse(url));
		getActivity().startActivity(browserIntent);
	}

	@Override
	public void onResume() {
		super.onResume();
		ECBlogView.setautoslide = false;
		LogUtils.i(TAG, "onResume");
		try {
			if (flagGotoDetail) {
				pageIndexSelect = SkyUtils.getInt(getActivity(), pageSelect,
						pageIndexSelect);
				list.setSelection(pageIndexSelect);
				flip.flipTo(pageIndexSelect);
				flagGotoDetail = false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			onLoad = true;
			DataCache = false;
			callApi();
			e.printStackTrace();
		}
		if (SkyMainActivity.FLAG_CHECK_ONLOADBEADGER)
			getBadge();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		LogUtils.e("HomeFragment", "onBackPressed-------------------");
		// finish();
		return;
	}

	/**
	 * this method will get total badge and save to sharepreference
	 */
	private void getBadge() {
		// sharePref = getActivity().getSharedPreferences(SkyUtils.shareKey,
		// Context.MODE_PRIVATE);
		HashMap<String, String> params = new HashMap<String, String>();
		SkyUtils.execute(getActivity(), RequestMethod.GET,
				SkyUtils.API.API_GET_BADGE, params,
				new CheerzServiceCallBack() {
					@Override
					public void onError(String message) {
						// TODO Auto-generated method stub
						super.onError(message);
					}

					@Override
					public void onSucces(String response) {
						super.onSucces(response);
						LogUtils.i(TAG, "GET Badge = " + response.toString());
						try {
							SkyMainActivity.FLAG_CHECK_ONLOADBEADGER = false;
							JSONObject jsonData = new JSONObject(response);
							JSONObject jsonObj = jsonData.getJSONObject("data");
							int countEmail = jsonObj.getInt("badge_mail");
							int countNews = jsonObj.getInt("badge_new");
							// sharePref.edit().putInt(SkyUtils.BADGE_EMAIL,
							// countEmail).commit();
							// sharePref.edit().putInt(SkyUtils.BADGE_NEWS,
							// countNews).commit();
							// GcmBroadcastReceiver.setBadgeDevice(getActivity(),
							// 0 , 0);
							// CommonAndroid.updateBadgeLauncher(getActivity(),
							// countNews+countEmail);
							if (countEmail > 0) {
								GcmBroadcastReceiver.notifiServiceUpdate(
										getActivity(), countEmail, 1, true,
										false);
							}
							if (countNews > 0) {
								GcmBroadcastReceiver.notifiServiceUpdate(
										getActivity(), countNews, 2, true,
										false);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				});
	}
}
