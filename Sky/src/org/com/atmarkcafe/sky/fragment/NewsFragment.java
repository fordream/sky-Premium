package org.com.atmarkcafe.sky.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.com.atmarkcafe.object.ItemMail;
import org.com.atmarkcafe.object.NewsObject;

import com.SkyPremiumLtd.SkyPremium.R;

import org.com.atmarkcafe.sky.GcmBroadcastReceiver;
import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.customviews.charting.MTextView;
import org.com.atmarkcafe.sky.fragment.MailboxFragment.Holder;
import org.com.atmarkcafe.sky.fragment.MailboxFragment.MailboxAdapter;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

@SuppressLint({ "ValidFragment", "ViewHolder" })
public class NewsFragment extends BaseFragment implements OnClickListener {

	private String TAG = "NewsFragment";
	private List<NewsObject> listNews;
	private ListView listview;
	private LoadingView loading;
	private ImageButton btnSortDate;

	private HeaderView txtTitleHeader;
	private Setting setting;
	private ImageButton btnSortCategory;

	private String txtSortNews, txtSortLatest;
	private ImageButton btnMenu;
	private NewsAdapter newsAdapter;
	private List<String> listCategories = new ArrayList<String>();
	private Map<String, String> categories = new HashMap<String, String>();
	private Iterator<String> keysCategories;
	private JSONObject jsonCategories;
	private boolean isGetDataSuccess = false;
	private int indexFocusSort = 2;
	private int typeSortNews = 1;
	private int typeSortby = 0;
	private SharedPreferences sharePref;
	
	@Override
	public int getLayout() {
		return R.layout.news_layout;
	}

	@Override
	public void init(View view) {
		loading = (LoadingView) view.findViewById(R.id.loading);
		CommonAndroid.showView(false, loading);
		getDataNews();
		listview = (ListView) view.findViewById(R.id.news_listcontent_id);

		btnSortCategory = (ImageButton) view.findViewById(R.id.header_btn_right_left);
		btnSortCategory.setImageResource(R.drawable.icon_sort_category);
		btnSortCategory.setOnClickListener(this);
		btnSortCategory.setVisibility(View.INVISIBLE);

		btnSortDate = (ImageButton) view.findViewById(R.id.header_btn_right);
		btnSortDate.setVisibility(View.VISIBLE);
		btnSortDate.setImageResource(R.drawable.icon_sort_date);
		btnSortDate.setOnClickListener(this);

		setting = new Setting(getActivity());

		txtTitleHeader = CommonAndroid.getView(view,R.id.news_header_id);
		txtTitleHeader.initHeader(R.string.menu_news, R.string.menu_news_j);
		
		btnMenu = (ImageButton) view.findViewById(R.id.header_btn_left);
		btnMenu.setOnClickListener(this);
		
		sharePref = getActivity().getSharedPreferences(SkyUtils.shareKey, Context.MODE_PRIVATE);
//		sharePref.edit().putInt(SkyUtils.BADGE_NEWS, 0).commit();
		
//		GcmBroadcastReceiver.setBadgeDevice(getActivity(), 0, 0);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				NewsDetailFragment newsDetailFragment = new NewsDetailFragment(
						listNews.get(position));
				if (getActivity() instanceof SkyMainActivity) {
					SkyMainActivity fca = (SkyMainActivity) getActivity();
					fca.switchContent(newsDetailFragment, "");
					
				}
			}
		});
		// clear badge of news
		
		
	}
	
	
	
	@Override
	public void onChangeLanguage() {
		getDataNews();
	}

	@Override
	public void onClick(View v) {
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_right:
			if (isGetDataSuccess) {
				typeSortby = 1;
				ShowDialogSort dlgSortDate = new ShowDialogSort(1);
				dlgSortDate.show(getActivity().getSupportFragmentManager(),"sort_date");
			}
			break;
		case R.id.header_btn_right_left:
			//nothing
			break;
		case R.id.header_btn_left:
			SkyMainActivity.sm.toggle();
		default:
			break;
		}
		super.onClick(v);
	}

	private void getDataNews() {
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("req[param][show_opt]", "news");
//		params.put("req[param][sort_opt]", "");
		SkyUtils.execute(getActivity(), RequestMethod.GET,
				SkyUtils.API.API_SERVICE_NEWS, params,
				new CheerzServiceCallBack() {
					@Override
					public void onStart() {
						CommonAndroid.showView(true, loading);
						super.onStart();
					}

					@Override
					public void onError(String message) {
						if (!isFinish()) {
							CommonAndroid.showView(false, loading);
							if (setting.isLangEng()) {
								SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure), null);
							}else{
								SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure_j), null);
							}
							isGetDataSuccess = false;
//							changeStatesBtnSort(false);
							getBadge();
						}
					}

					@Override
					public void onSucces(String response) {
						if (!isFinish()) {
							Log.i(TAG, "content = " + response.toString());
							CommonAndroid.showView(false, loading);
							listNews = new ArrayList<NewsObject>();
							try {
								JSONObject jsonRes = new JSONObject(response);
								JSONObject jsonData = jsonRes.getJSONObject("data");

								JSONArray jsonArrayNews = jsonData
										.getJSONArray("news");
								listNews.clear();
								for (int i = jsonArrayNews.length() - 1; i >= 0 ; i--) {
									JSONObject jsonObjNews = jsonArrayNews.getJSONObject(i);
									NewsObject newsObj = new NewsObject();
									 
									newsObj.setId(jsonObjNews.getInt("id"));
									newsObj.setTitle(jsonObjNews.getString("title"));
//									newsObj.setContent(jsonObjNews.getString("content"));
									newsObj.setTime(jsonObjNews.getString("datetime"));
									newsObj.setCategory(jsonObjNews.getJSONArray("category"));
									newsObj.setNewsStatus(jsonObjNews.getInt("new"));
									
									listNews.add(newsObj);
								}
								LogUtils.i(TAG, "count1==" + jsonArrayNews.length());
								LogUtils.i(TAG, "count2==" + listNews.size());
								jsonCategories = jsonData.getJSONObject("categories");
								keysCategories = jsonCategories.keys();
								
//								sortNews(listNews);
								Collections.sort(listNews, new Comparator<NewsObject>() {

									@SuppressLint("SimpleDateFormat")
									@Override
									public int compare(NewsObject obj1, NewsObject obj2) {
										SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
										Date date1 = null,date2 = null;
										try {
											date1 = (Date)format.parse(obj1.getTime());
											date2 = (Date)format.parse(obj2.getTime());
										} catch (ParseException e) {
											e.printStackTrace();
										}
										return date1.compareTo(date2);
									}
								});
								Log.i(TAG, "Sort by Date");
								Collections.reverse(listNews);
								newsAdapter = new NewsAdapter(getActivity(), listNews);
								newsAdapter.notifyDataSetChanged();
								listview.setAdapter(newsAdapter);
								
								isGetDataSuccess = true;
								//NewsAdapter adapter = new NewsAdapter(getActivity(), listNews);
								//listview.setAdapter(adapter);
//								changeStatesBtnSort(true);
							} catch (Exception e) {
								e.printStackTrace();
							}
							getBadge();
							super.onSucces(response);
						}
						
					}
				});
	}
	
	private DialogInterface.OnClickListener onClickDialog = new DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			
		}
	};

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	
	
	protected void sortNews(List<NewsObject> listData) {
		Log.i(TAG, "Sort Mail");
		Collections.sort(listData, new Comparator<NewsObject>() {

			@Override
			public int compare(NewsObject obj1, NewsObject obj2) {
				Date date1 = null, date2 = null;
				try {
					date1 = format.parse(obj1.getTime().trim());
					date2 = format.parse(obj2.getTime().trim());
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return date1.compareTo(date2);
			}

		});

		for (int i = 0; i < listData.size() - 1; i++) {
			Date date1 = null, date2 = null;
			try {
				date1 = format.parse(listData.get(i).getTime().trim());
				
				LogUtils.e("data", "date==" + date1);
				date2 = format.parse(listData.get(i + 1).getTime().trim());
				if ( !date1.equals(date2)) {
					listData.get(i).setType(0);
				}else{
					listData.get(i).setType(1);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
//		Date datei = null;
//		String strLimitDate = GlobalFunction.getCalculateDate("yyyy-MM-dd", -10);
//		for (int i = 0; i < listData.size(); i++) {
//			Log.i(TAG, "===> limitDate = " + strLimitDate);
//			Date dateLimit;
//			try {
//				dateLimit = format.parse(strLimitDate);
//				datei = format.parse(listData.get(i).getTime().trim());
//				if (datei.after(dateLimit)) {
//					listData.get(i).setNewsStatus(1);
//				}else{
//					listData.get(i).setNewsStatus(0);
//				}
//			} catch (ParseException e) {
//				e.printStackTrace();
//			}
//			
//		}
		Collections.reverse(listData);
		newsAdapter = new NewsAdapter(getActivity(), listData);
		listview.setAdapter(newsAdapter);
		
	}


	class Holder {
		private TextView txtSubjectItem;
		private TextView txtHeaderItem;
		private TextView txtSubject;
		private ImageView icon;
		private ImageView iconNews;
	}

	class NewsAdapter extends BaseAdapter {
		private List<NewsObject> data;
		private Context context;

		public NewsAdapter(Context ctx, List<NewsObject> lData) {
			this.context = ctx;
			this.data = lData;
		}

		@Override
		public int getCount() {
			return data.size();
		}

		@Override
		public Object getItem(int position) {
			return data.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		public int getItemViewType(int position) {
			if (data.get(position).getType() == 0  ) {
				return 0;
			}
				return 1;
			
		};
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			/*View rowView = convertView;
			// fill data
			Holder	holderView ;
			if (rowView == null) {
				holderView = new Holder();
				LayoutInflater inf = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					rowView = inf.inflate(R.layout.item_header, null);
					holderView.txtHeaderItem = (TextView)rowView.findViewById(R.id.item_header_text_id);
					holderView.txtSubject = (TextView)rowView.findViewById(R.id.mailbox_row_date_id);
					holderView.txtHeaderItem.setText(data.get(position).getTime());
					holderView.txtSubject.setText(data.get(position).getTitle());
					holderView.icon = (ImageView)rowView.findViewById(R.id.icon_detail_id);
					holderView.icon.setVisibility(View.INVISIBLE);
					holderView.iconNews = (ImageView)rowView.findViewById(R.id.icon_news_id);
					if (data.get(position).getNewsStatus() == 1) {
						holderView.iconNews.setVisibility(View.VISIBLE);
					}else{
						holderView.iconNews.setVisibility(View.INVISIBLE);
					}
					Log.i(TAG, "News ====== " + data.get(position).getNewsStatus());
					rowView.setTag(holderView);
			}else{
				holderView = (Holder)rowView.getTag();
			}
			
			return rowView;*/
			
			LayoutInflater inf = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inf.inflate(R.layout.item_header, null);
			ImageView icon = (ImageView)rowView.findViewById(R.id.icon_detail_id);
			icon.setVisibility(View.INVISIBLE);
			ImageView iconNews = (ImageView)rowView.findViewById(R.id.icon_news_id);
			if (data.get(position).getNewsStatus() == 1) {
				iconNews.setVisibility(View.VISIBLE);
			}else{
				iconNews.setVisibility(View.INVISIBLE);
			}  
			TextView txtHeaderItem = (TextView)rowView.findViewById(R.id.item_header_text_id);
			txtHeaderItem.setText(data.get(position).getTime());
			TextView txtSubject = (TextView)rowView.findViewById(R.id.mailbox_row_date_id);
			txtSubject.setText(data.get(position).getTitle());
			return rowView;
		}

	}

	class ObjectCategories{
		private String keyCat;
		private String valueCat;
		
		public String getKeyCat() {
			return keyCat;
		}
		
		public void setKeyCat(String keyCat) {
			this.keyCat = keyCat;
		}
		
		public String getValueCat() {
			return valueCat;
		}
		
		public void setValueCat(String valueCat) {
			this.valueCat = valueCat;
		}
		
	}
	private List<ObjectCategories> listCateObj = new ArrayList<NewsFragment.ObjectCategories>();
	
	class ShowDialogSort extends DialogFragment implements OnClickListener {

		int typeSort; // type 1 : sort by date. type 2 : sort by category

		MTextView txtNews,txtNews1, txtLatestNews;
		MTextView txtSortName, txtSortDate, txtSortCate;


		@SuppressLint("ValidFragment")
		public ShowDialogSort(int type) {
			this.typeSort = type;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View popupView = null;
			getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
			
			if(keysCategories != null){
				while (keysCategories.hasNext()) {
					try {
						String _key = keysCategories.next();
						String _value = jsonCategories.getString(_key);
						ObjectCategories item = new ObjectCategories();
						item.setKeyCat(_key);
						item.setValueCat(_value);
						listCateObj.add(item);
						
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
				}
			}
			
			
			if (typeSort == 1) {
				popupView = inflater.inflate(R.layout.popup_sort_date_layout,null);
				txtSortName = (MTextView) popupView.findViewById(R.id.popup_sort_date_name_id);
				txtSortDate = (MTextView) popupView.findViewById(R.id.popup_sort_date_date_id);
				txtSortCate = (MTextView) popupView.findViewById(R.id.popup_sort_date_cate_id);
				
				txtSortName.setOnClickListener(this);
				txtSortDate.setOnClickListener(this);
				txtSortCate.setOnClickListener(this);
				chageStatusFocus(indexFocusSort);
			} else {
				popupView = inflater.inflate(R.layout.popup_sort_cate_layout,null);
				
				if (listCateObj.size() == 2) {
					if (txtNews1 != null) {
						txtNews1.setVisibility(View.INVISIBLE);
					}
					txtNews = (MTextView) popupView.findViewById(R.id.popup_sort_cate_1_id);
					txtLatestNews = (MTextView) popupView.findViewById(R.id.popup_sort_cate_2_id);
					txtNews.setVisibility(View.VISIBLE);
					txtLatestNews.setVisibility(View.VISIBLE);
					
					txtNews.setTextEdit(listCateObj.get(0).getValueCat());
					txtLatestNews.setTextEdit(listCateObj.get(1).getValueCat());

					txtNews.setOnClickListener(this);
					txtLatestNews.setOnClickListener(this);
					chageStatusFocus(typeSortNews);
	
				}else if(listCateObj.size() == 1){
					if (txtNews != null) {
						txtNews.setVisibility(View.INVISIBLE);
						txtLatestNews.setVisibility(View.INVISIBLE);
					}
					txtNews1 = (MTextView) popupView.findViewById(R.id.popup_sort_cate_11_id);
					txtNews1.setVisibility(View.VISIBLE);
					txtNews1.setTextEdit(listCateObj.get(0).getValueCat());
					txtNews1.setOnClickListener(this);
				}
			}
			

			return popupView;
		}
		
		/**
		 * 
		 * @param index
		 * <code>
		 *  @param index = 1 : focus to sort name
		 *  @param index = 2 : focus to sort date
		 *  @param index = 3 : focus to sort category
		 *  <code>
		 */
		private void chageStatusFocus(int index){
			if (typeSortby == 1) {
				if (index == 1) {
					txtSortName
							.setBackgroundResource(R.drawable.select_option_on_radius);/*popup_bgr_button_select*/
					txtSortDate.setBackgroundResource(R.drawable.select_option_off_radius);
					txtSortCate.setBackgroundResource(R.drawable.select_option_off_radius);
				} else if (index == 2) {
					txtSortName.setBackgroundResource(R.drawable.select_option_off_radius);
					txtSortDate
							.setBackgroundResource(R.drawable.select_option_on_radius);
					txtSortCate.setBackgroundResource(R.drawable.select_option_off_radius);
				} else {
					txtSortName.setBackgroundResource(R.drawable.select_option_off_radius);
					txtSortDate.setBackgroundResource(R.drawable.select_option_off_radius);
					txtSortCate
							.setBackgroundResource(R.drawable.select_option_on_radius);
				}
	
			}else{
				if (typeSortNews == 1) {
					txtNews.setBackgroundResource(R.drawable.select_option_on_radius);
					txtLatestNews.setBackgroundResource(R.drawable.select_option_off_radius);
				}else{
					txtNews.setBackgroundResource(R.drawable.select_option_off_radius);
					txtLatestNews.setBackgroundResource(R.drawable.select_option_on_radius);
				}
			}
			
		}
		
		
		@Override
		public void onClick(View v) {
			int idView = v.getId();
			switch (idView) {
			case R.id.popup_sort_cate_1_id:
				Log.i(TAG, "sort 1");
				typeSortNews = 1;
				chageStatusFocus(typeSortNews);
				sort(1);
				break;
			case R.id.popup_sort_cate_2_id:
				typeSortNews = 2;
				chageStatusFocus(typeSortNews);
				sort(2);
				break;
			case R.id.popup_sort_date_name_id:
				indexFocusSort = 1;
				chageStatusFocus(indexFocusSort);
				sort(3);
				break;
			case R.id.popup_sort_date_date_id:
				indexFocusSort = 2;
				chageStatusFocus(indexFocusSort);
				sort(4);
				break;
			case R.id.popup_sort_date_cate_id:
				indexFocusSort = 3;
				chageStatusFocus(indexFocusSort);
				sort(5);
				break;
			case R.id.popup_sort_cate_11_id:
				if (listCateObj.get(0).getKeyCat().equalsIgnoreCase("14")) {
					sort(1);
				}else if (listCateObj.get(0).getKeyCat().equalsIgnoreCase("25")) {
					sort(2);
				}
				
				break;
			default:
				break;
			}
			if (getDialog().isShowing()) {
				getDialog().dismiss();
			}
		}

	}

	/**
	 * 
	 * @param typeSort
	 *            = 1 : sort by News
	 * @param typeSort
	 *            = 2 : sort by latest
	 * @param typeSort
	 *            = 3 : sort by name
	 * @param typeSort
	 *            = 4 : sort by date
	 * @param typeSort
	 *            = 5 : sort by category
	 */
	public void sort(int typeSort) {
		List<NewsObject> listSort = new ArrayList<NewsObject>();
		JSONArray jsonCate = null;
		switch (typeSort) {
		case 1:
			for (int i = 0; i < listNews.size(); i++) {
				jsonCate = listNews.get(i).getCategory();
				try {
					if (jsonCate.getInt(1) == 25) {
						listSort.add(listNews.get(i));
						Log.i(TAG, "sort news");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case 2:
			for (int i = 0; i < listNews.size(); i++) {
				jsonCate = listNews.get(i).getCategory();
				try {
					if (jsonCate.getInt(0) == 14) {
						listSort.add(listNews.get(i));
						Log.i(TAG, "sort latest");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		case 3:
			listSort = listNews;
			Collections.sort(listSort, new Comparator<NewsObject>() {

				@Override
				public int compare(NewsObject obj1, NewsObject obj2) {
					
					return obj1.getTitle().compareToIgnoreCase(obj2.getTitle());
				}
			});
			
			break;
		case 4:
			listSort = listNews;
			Collections.sort(listSort, new Comparator<NewsObject>() {

				@SuppressLint("SimpleDateFormat")
				@Override
				public int compare(NewsObject obj1, NewsObject obj2) {
					SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					Date date1 = null,date2 = null;
					try {
						date1 = (Date)format.parse(obj1.getTime());
						date2 = (Date)format.parse(obj2.getTime());
					} catch (ParseException e) {
						e.printStackTrace();
					}
					return date1.compareTo(date2);
				}
			});
			Log.i(TAG, "Sort by Date");
			Collections.reverse(listSort);
			break;
		case 5:
			List<NewsObject> listTemp = new ArrayList<NewsObject>();
			for (int i = 0; i < listNews.size(); i++) {
				jsonCate = listNews.get(i).getCategory();
				try {
					if (jsonCate.getInt(0) == 14) {
						listSort.add(listNews.get(i));
						Log.i(TAG, "sort latest");
					}else{
						listTemp.add(listNews.get(i));
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			listSort.addAll(listTemp);
			break;
			
		default:
			break;
		}
		if (listSort.size() > 0) {
			NewsAdapter adapter = new NewsAdapter(getActivity(), listSort);
			listview.setAdapter(adapter);
			Log.i(TAG, "sort by News");
		}
	}
	
	/**
	 * 
	 * @param state = true : visible else gray
	 * 
	 */
	private void changeStatesBtnSort(boolean state){
		btnSortDate.setEnabled(state);
		btnSortCategory.setEnabled(state);
		if (state) {
//			btnSortDate.setBackgroundResource(R.drawable.icon_sort_date_select);
//			btnSortCategory.setBackgroundResource(R.drawable.icon_sort_category_select);
		}else{
//			btnSortDate.setBackgroundResource(R.drawable.icon_sort_date);
//			btnSortCategory.setBackgroundResource(R.drawable.icon_sort_category);
		}
	}
	
	/**
	 * this method will get total badge and save to sharepreference
	 */
	private void getBadge(){
		HashMap<String, String> params = new HashMap<String, String>();
		SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_GET_BADGE, params, new CheerzServiceCallBack(){
			@Override
			public void onError(String message) {
				// TODO Auto-generated method stub
				super.onError(message);
			}
			
			@Override
			public void onSucces(String response) {
				super.onSucces(response);
				Log.i(TAG, "GET Badge = " + response.toString());
				try {
					JSONObject jsonData = new JSONObject(response);
					JSONObject jsonObj = jsonData.getJSONObject("data");
					int countEmail = jsonObj.getInt("badge_mail");
					int countNews = jsonObj.getInt("badge_new");
					GcmBroadcastReceiver.notifiServiceUpdate(getActivity(),countEmail ,1, true, true);
					GcmBroadcastReceiver.notifiServiceUpdate(getActivity(),countNews ,2, true , true);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
	}


}
