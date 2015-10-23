package org.com.atmarkcafe.sky.fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.customviews.charting.MTextView;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONArray;
import org.json.JSONObject;

import se.emilsjolander.flipview.FlipView;
import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderSquareUtils;
import z.lib.base.ImageLoaderUtils;
import z.lib.base.SkyLoader;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.SkyUtils.SCREEN;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.ServiceList;
import com.acv.cheerz.db.Setting;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
@SuppressLint("ValidFragment")
public class ServiceList2Fragment extends BaseFragment {
	private se.emilsjolander.flipview.FlipView flip;
	private ListView list;
	private LoadingView loading;
	private BaseAdapter plipAdapter;
	private List<SeviceListObject> listObject;
	private String title;
	private String TAG = "ServiceList2Fragment";
	Drawable myIcon_select;
	Drawable myIcon_unselect;
	ImageView ico_indi_gray;

	public ServiceList2Fragment(String title) {
		super();
		this.title = title;
		Log.i(TAG, "CREATE");
	}

	@Override
	public int getLayout() {
		return R.layout.home;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		super.onItemClick(arg0, arg1, arg2, arg3);
		ServiceList2DetailFragment productListFragment = null;
		String type = listObject.get(arg2).getType();
		if (type.equalsIgnoreCase("external")) {
			openBrowser(listObject.get(arg2).getExternalUrl());
		} else {
			productListFragment = new ServiceList2DetailFragment(listObject
					.get(arg2).getSlug(), listObject.get(arg2).getType(),
					listObject.get(arg2).getTitle(), listObject.get(arg2)
							.getContent(), title, listObject.get(arg2)
							.getExternalUrl());

//			Bundle extras = new Bundle();
//			extras.putParcelable("", listObject.get(arg2));
//			startActivity(SCREEN.SERVICE_LIST_DETAIL, extras);
			
			if (productListFragment != null) {
				if (getActivity() instanceof SkyMainActivity) {
					SkyMainActivity skyMain = (SkyMainActivity) getActivity();
					skyMain.switchContent(productListFragment, "");
				}
			}
		}
	}

	private void openBrowser(String url){
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		getActivity().startActivity(i);
	}
	
	@Override
	public void onClick(View v) {
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			HomeFragment fg = new HomeFragment();
			if (getActivity() instanceof SkyMainActivity) {
				SkyMainActivity mainActivity = (SkyMainActivity) getActivity();
				mainActivity.switchContent(fg, "");
				Log.i("ServiceList2", "back");
			}
			break;

		default:
			break;
		}
		super.onClick(v);
	}

	private void callApi() {
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][id]", "");
		inputs.put("req[param][slug]", "life-planning");
		inputs.put("req[param][type]", "page");

		SkyUtils.execute(getActivity(), RequestMethod.GET,API.API_SERVICE_LIST, inputs, callback);
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
				Log.i(TAG, "===> " + response);
				updateData();
				listObject = new ArrayList<ServiceList2Fragment.SeviceListObject>();
				try {
					JSONObject jsonRes = new JSONObject(response);
					JSONArray jsonArray = jsonRes.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {
						SeviceListObject obj = new SeviceListObject();
						JSONObject jobj = jsonArray.getJSONObject(i);
						obj.setId(jobj.getInt("id"));
						obj.setTitle(jobj.getString("title"));
						obj.setContent(jobj.getString("content"));
						obj.setSubTitle(jobj.getString("subtitle"));
						obj.setExternalUrl(jobj.getString("external_url"));
						obj.setSlug(jobj.getString("slug"));
						obj.setType(jobj.getString("type"));
						obj.setUrl(jobj.getString("url"));
						obj.setThumbnail(jobj.getString("thumbnail"));
						listObject.add(obj);
					}

					galleryAdapter = new GalleryAdapter(getActivity(),listObject);
					list.setAdapter(galleryAdapter);
					String parenTitle = jsonRes.getString("parent_title");
					txtTitleHeader.initHeader(parenTitle,parenTitle);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}
		};
	};

	private Setting setting;
	private HeaderView txtTitleHeader;

	@Override
	public void init(View view) {
		// initHeader(view, R.id.header, R.string.menu_service_list,
		// R.string.menu_service_list_j, R.drawable.icon_back, 0);
		myIcon_select = getResources().getDrawable( R.drawable.ico_indi_gray2 );
		myIcon_unselect = getResources().getDrawable( R.drawable.ico_indi_gray );
		
		txtTitleHeader = (HeaderView) view.findViewById(R.id.header);
		
		setting = new Setting(getActivity());
		txtTitleHeader.initHeader(title,title);

		ImageButton btnBack = (ImageButton) view
				.findViewById(R.id.header_btn_left);
		btnBack.setImageResource(R.drawable.icon_back);
		btnBack.setOnClickListener(this);

		ImageButton btnRight = (ImageButton) view
				.findViewById(R.id.header_btn_right);
		btnRight.setVisibility(View.INVISIBLE);

		loading = CommonAndroid.getView(view, R.id.loading);
		list = CommonAndroid.getView(view, R.id.list);

		list.setOnItemClickListener(this);

		flip = CommonAndroid.getView(view, R.id.flip);

		flip.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (event.getAction() == MotionEvent.ACTION_DOWN
						|| event.getAction() == MotionEvent.ACTION_MOVE) {
					SkyMainActivity.sm.setSlidingEnabled(false);
					SkyMainActivity.sm
							.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				} else if (event.getAction() == MotionEvent.ACTION_CANCEL
						|| event.getAction() == MotionEvent.ACTION_UP) {
					SkyMainActivity.sm.setSlidingEnabled(true);
					SkyMainActivity.sm
							.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
				}

				return false;
			}
		});

		flip.setOnFlipListener(new FlipView.OnFlipListener() {

			@Override
			public void onFlippedToPage(FlipView v, int position, long id) {
				if (galleryAdapter != null) {
					galleryAdapter.setPositionSelection(position);
					/*int color = getActivity().getResources().getColor(R.color.color_focus_lion);
//					list.getChildAt(position).setBackgroundColor(color);
					for (int i = 0; i < 3; i++) {
						if (i == position) {
							list.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.servicelist_first));//color_gray
							Log.i(TAG, "===> " + position);
						}else{
							list.getChildAt(i).setBackgroundColor(getResources().getColor(android.R.color.transparent));
						}
					}*/
					
				}
				if (position == 0) {
					SkyMainActivity.sm
							.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				}
			}
		});

		// updateData();
		callApi();
	}

	private void fillData() {

		new SkyLoader() {

			@Override
			public void loadSucess(Object data) {

			}

			@Override
			public Object loadData() {

				return null;
			}
		};
	}

	private void updateData() {

		new SkyLoader() {

			@Override
			public void loadSucess(Object data) {

				// serviceAdapter = new ServiceAdapter(list, (Cursor) data);

				plipAdapter = new BaseAdapter() {
					@Override
					public View getView(int position, View convertView,
							ViewGroup parent) {
						if (convertView == null) {
							convertView = CommonAndroid.initView(parent.getContext(),R.layout.service_list_slide_item_z, null);
						}
						convertView.setBackgroundResource(R.color.servicelist_first);
						ImageView img = CommonAndroid.getView(convertView,R.id.img);
						TextView text_1 = CommonAndroid.getView(convertView,R.id.text_1);
						int color = getActivity().getResources().getColor(R.color.servicelist_first);//color_focus_lion
						list.getChildAt(0).setBackgroundColor(color);
						text_1.setText(listObject.get(position).getTitle());
						ImageLoaderSquareUtils.getInstance(getActivity()).displayImageHomeSlide(listObject.get(position).getThumbnail(), img);
						Gallery.LayoutParams layoutParams = new Gallery.LayoutParams(LayoutParams.MATCH_PARENT,	320);
						convertView.setLayoutParams(layoutParams);
						convertView.setOnClickListener(new FlipOnClickItemListener(position));
						return convertView;
					}

					@Override
					public long getItemId(int position) {
						return listObject.get(position).getId();
					}

					@Override
					public Object getItem(int position) {
						return listObject.get(position);
					}

					@Override
					public int getCount() {
						return listObject.size();
					}
				};
				flip.setAdapter(plipAdapter);
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

	private int positionSelection = 0;
	class GalleryAdapter extends BaseAdapter {

		private Context mContext;
		private List<SeviceListObject> listData;

		public GalleryAdapter(Context ctx, List<SeviceListObject> list) {
			this.mContext = ctx;
			this.listData = list;
		}

		@Override
		public int getCount() {

			return listData.size();
		}

		@Override
		public Object getItem(int position) {

			return listData.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}
		
		public void setPositionSelection(int xpositionSelection) {
			positionSelection = xpositionSelection;
			notifyDataSetChanged();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = convertView;
			if (view == null) {
				view = CommonAndroid.initView(mContext,
						R.layout.service_list_item_z, null);
			}
			
			ico_indi_gray = (ImageView) view.findViewById(R.id.ico_indi_gray);
			if (positionSelection == position ) {
				view.setBackgroundResource(R.color.servicelist_first);
				ico_indi_gray.setImageDrawable(myIcon_select);
			} else {
				view.setBackgroundResource(android.R.color.transparent);
				ico_indi_gray.setImageDrawable(myIcon_unselect);
			}
			

			ImageView img = CommonAndroid.getView(view, R.id.img);
			TextView text_1 = CommonAndroid.getView(view, R.id.text_1);
			text_1.setText(listData.get(position).getTitle());
			ImageLoaderUtils.getInstance(parent.getContext()).displayImageHome(
					listData.get(position).getThumbnail(), img);
			return view;
		}

	}

	@Override
	public void onChangeLanguage() {
		callApi();
	}

	@Override
	public void reload() {
		super.reload();
	}

	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImageTask(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String urldisplay = urls[0];
			Bitmap mIcon11 = null;
			try {
				InputStream in = new java.net.URL(urldisplay).openStream();
				mIcon11 = BitmapFactory.decodeStream(in);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mIcon11;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}
	
	private ServiceAdapter serviceAdapter;
	private GalleryAdapter galleryAdapter;

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
			return CommonAndroid.initView(arg0, R.layout.service_list_item,
					null);
		}

		@Override
		public void bindView(View view, Context context, Cursor c) {
			if (view == null) {
				view = CommonAndroid.initView(context,
						R.layout.service_list_item, null);
			}

			if (positionSelection == c.getPosition()) {
				view.setBackgroundResource(R.color.servicelist_first);//R.color.servicelist_first
			} else {
				view.setBackgroundResource(android.R.color.transparent);
			}

			ImageView img = CommonAndroid.getView(view, R.id.img);
			TextView text_1 = CommonAndroid.getView(view, R.id.text_1);
			TextView text_2 = CommonAndroid.getView(view, R.id.text_2);

			CommonAndroid.setText(text_1, c, ServiceList.title);
			CommonAndroid.setText(text_2, c, ServiceList.subtitle);

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

	class SeviceListObject {
		private int id;
		private String title;
		private String subTitle;
		private String content;
		private String slug;
		private String url;
		private String thumbnail;
		private String downloadUrl;
		private String externalUrl;
		private String platinum_accessed_msg;
		private String type;
		private String video_src;

		public String getVideo_src() {
			return video_src;
		}

		public void setVideo_src(String video_src) {
			this.video_src = video_src;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public String getPlatinum_accessed_msg() {
			return platinum_accessed_msg;
		}

		public void setPlatinum_accessed_msg(String platinum_accessed_msg) {
			this.platinum_accessed_msg = platinum_accessed_msg;
		}

		public String getExternalUrl() {
			return externalUrl;
		}

		public void setExternalUrl(String externalUrl) {
			this.externalUrl = externalUrl;
		}

		public String getDownloadUrl() {
			return downloadUrl;
		}

		public void setDownloadUrl(String downloadUrl) {
			this.downloadUrl = downloadUrl;
		}

		public String getThumbnail() {
			return thumbnail;
		}

		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getSlug() {
			return slug;
		}

		public void setSlug(String slug) {
			this.slug = slug;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getSubTitle() {
			return subTitle;
		}

		public void setSubTitle(String subTitle) {
			this.subTitle = subTitle;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}
	}
}
