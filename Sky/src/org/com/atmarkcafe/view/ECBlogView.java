package org.com.atmarkcafe.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.com.atmarkcafe.object.EcObject;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.customviews.charting.MTextView;

import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderUtils;
import z.lib.base.LogUtils;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.Setting;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//com.acv.cheerz.view.HeaderView
public abstract class ECBlogView extends LinearLayout {
	private org.com.atmarkcafe.sky.customviews.charting.MTextView text;
	// private ViewPager pager;
	private com.viewpagerindicator.CirclePageIndicator indicator;
	private ImageButton imgSlideBack, imgSlideNext;
	private int currentPage, totalPage;
	private String TAG = "ECBlogView";
	private HorizontalListView listview;
	private List<EcObject> listObject;
	private DisplayImageOptions options;
	ImageLoader imageLoader = ImageLoader.getInstance();
	private int widthScreen, heightScreen;
	private Context mContext;
	private Setting setting;
	private boolean isEnglish = false;
	private HorizontalScrollView horizontalScrollview;
	private LinearLayout horizontalOuterLayout;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private Timer scrollTimer = null;
	private Timer clickTimer = null;
	private Timer faceTimer = null;
	private Boolean isFaceDown = true;
	private TimerTask clickSchedule;
	private TimerTask scrollerSchedule;
	private TimerTask faceAnimationSchedule;
	private int countItem; // total item of line 1
	private Handler mHandler = new Handler();
	private int countLine;
	int detalX = 1;

	public ECBlogView(Context context, int count) {
		super(context);
		this.mContext = context;
		this.countLine = count;
		init();
	}

	public ECBlogView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}

	int k = 0;
	int row = 0;
	int scrollMax = 0;
	SharedPreferences shareData;
	boolean flagStop = false;

	private void init() {
		// init option for imageloader
		setautoslide = true;
		((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.ec_blog_update, this);
		View view = ((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.ec_blog_update, this);

		text = CommonAndroid.getView(this, R.id.text);
		listview = (HorizontalListView) view
				.findViewById(R.id.horizontal_listview_id);

		Log.i(TAG, "======Y Count : " + countLine);
		if (countLine == 1) {
			scrollPos = 0;
		}

		indicator = CommonAndroid.getView(this, R.id.indicator);

		findViewById(R.id.header_blog_ec).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						onCLick(id_ec, name);
					}
				});

		imgSlideBack = CommonAndroid.getView(view, R.id.img_slide_back_id);
		imgSlideNext = CommonAndroid.getView(view, R.id.img_slide_next_id);
		CommonAndroid.changeColorImageView(mContext, R.drawable.ec_blog_next, getResources().getColor(R.color.ec_blog_next));
	}

	private void initSlideImage(View view) {
		horizontalScrollview = (HorizontalScrollView) view
				.findViewById(R.id.horiztonal_scrollview_id);
		horizontalOuterLayout = (LinearLayout) findViewById(R.id.horiztonal_outer_layout_id);

		horizontalScrollview.setHorizontalScrollBarEnabled(false);
		listTemp.addAll(listObject);
		listTemp.addAll(listObject);
		listTemp.addAll(listObject);
		listTemp.addAll(listObject);
		listTemp.addAll(listObject);
		addImagesToView();

		ViewTreeObserver vto = horizontalOuterLayout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				horizontalOuterLayout.getViewTreeObserver()
						.removeGlobalOnLayoutListener(this);
				getScrollMaxAmount();
				startAutoScrolling();
			}
		});

		horizontalScrollview.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				
				int action = MotionEventCompat.getActionMasked(event);

			    switch(action) {
			        case (MotionEvent.ACTION_DOWN) :
			            Log.d(TAG,"Action was DOWN");
			        	stopAutoScrolling();
			            break;
			        case (MotionEvent.ACTION_MOVE) :
			            Log.d(TAG,"Action was MOVE");
			        	stopAutoScrolling();
			        	break;
			        case (MotionEvent.ACTION_UP) :
			            Log.d(TAG,"Action was UP");
			        	startAutoScrolling();
			        	break;
			        case (MotionEvent.ACTION_CANCEL) :
			            Log.d(TAG,"Action was CANCEL");
			        	startAutoScrolling();
			        	break;
			    }      

				return false;
			}
		});
	}

	
	@Override
	protected void onDetachedFromWindow() {
		stopAutoScrolling();
		super.onDetachedFromWindow();
	}

	public void startAutoScrolling() {
		Log.i(TAG, "ScrollPosX 1 = " + scrollPos);
		if (scrollTimer == null) {
			scrollTimer = new Timer();
			final Runnable Timer_Tick = new Runnable() {
				public void run() {
					moveScrollView();
				}
			};

			if (scrollerSchedule != null) {
				scrollerSchedule.cancel();
				scrollerSchedule = null;
			}

			scrollerSchedule = new TimerTask() {
				@Override
				public void run() {
					mHandler.postDelayed(Timer_Tick, 0);
				}
			};
			
			scrollTimer.schedule(scrollerSchedule, 0, 20);
		}
	}

	Boolean checkLoadMore = true;
	int scrollMaxTemp = 0;
	protected void moveScrollView() {
		if(setautoslide){
			getScrollMaxAmount();
			if (scrollPos >= scrollMax- widthScreen && checkLoadMore) {
				checkLoadMore = false;
				scrollMaxTemp = scrollMax;
				new updateContent().execute("");
			}
			
			scrollPos = (int) (horizontalScrollview.getScrollX() + 1);
			horizontalScrollview.smoothScrollTo(scrollPos, 0);
				
			if(!checkLoadMore && (scrollMax > scrollMaxTemp)){
				checkLoadMore = true;
			}
		}
		
	}
	
	public class updateContent extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... params) {
		
					if (listTemp.size() >= 21) {
						for (int j = 0; j < 8; j++) {
							listTemp.remove(j);
						}
						Log.i(TAG, "remove Item");
					}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			 addImagesToView();
			super.onPostExecute(result);
		}
	}
	
	protected void getScrollMaxAmount() {
		int actualWidth = (horizontalOuterLayout.getMeasuredWidth() - widthScreen);
		scrollMax = actualWidth;
	}
	
	int countAdd = 0;
	private List<EcObject> listTemp = new ArrayList<EcObject>();
	private static View convertView;
	private void addImagesToView() {
		int countI = 0;
		countAdd++;
		Log.i(TAG, "Addview : " + System.currentTimeMillis());
		stopAutoScrolling();
		LayoutInflater inf = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		for (countI = 0; countI < listTemp.size(); countI++) {
			convertView = inf.inflate(R.layout.ec_blog_item_update, null);
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) (widthScreen / 2.2),
					android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
			convertView.setLayoutParams(params);
			ImageView imageView = (ImageView) convertView.findViewById(R.id.img);

			MTextView txtView1 = (MTextView) convertView.findViewById(R.id.text_1);
			TextView txtView2 = (TextView) convertView.findViewById(R.id.text_2);
			MTextView txtView3 = (MTextView) convertView.findViewById(R.id.text_3);
			imageLoader.displayImage(listTemp.get(countI).getUrlImage(),imageView, options);
			LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(256, 256);
			params1.setMargins(0, 25, 0, 25);
			convertView.setLayoutParams(params);
			Setting setting = new Setting(getContext());
			String price = String.format(
					"%s %s %s"//
					,
					setting.isLangEng() ? getResources().getString(
							R.string.ec_price_unit) : getResources().getString(
							R.string.ec_price_unit_j)//
					,
					(listTemp.get(countI).getPrice()).toString()//
					,
					setting.isLangEng() ? getResources().getString(
							R.string.ec_taxincl) : getResources().getString(
							R.string.ec_taxincl_j)//
					);
			txtView1.setText(listTemp.get(countI).getName());

			txtView2.setText(price);

			String status_text = "";
			String status_text_in = ""
					+ (setting.isLangEng() ? getResources().getString(
							R.string.ec_in_stock) : getResources().getString(
							R.string.ec_in_stock_j));
			String status_text_out = ""
					+ (setting.isLangEng() ? getResources().getString(
							R.string.ec_out_stock) : getResources().getString(
							R.string.ec_out_stock_j));

			int status_text_tem = 0;
			try {
				status_text_tem = Integer.parseInt(listTemp.get(countI)
						.getQuantity());
			} catch (Exception e) {
				e.printStackTrace();
			}

			int status_text_color = getResources().getColor(R.color.ec_selection_status_text_in);
			if (status_text_tem > 0) {
				status_text_color = getResources().getColor(R.color.ec_selection_status_text_in);
				status_text = status_text_in;
			} else {
				status_text_color = getResources().getColor(R.color.ec_selection_status_text_out);
				status_text = status_text_out;
			}

			txtView3.setTextColor(status_text_color);
			txtView3.setText(status_text);
			final int tempI = countI;
			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String idProduct = listTemp.get(tempI).getId();
					openProduct(idProduct);
				}
			});
			
			convertView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int action = MotionEventCompat.getActionMasked(event);

				    switch(action) {
				        case (MotionEvent.ACTION_DOWN) :
				            Log.d(TAG,"Action1 was DOWN");
				        	stopAutoScrolling();
				        	break;
				         case (MotionEvent.ACTION_UP) :
				            Log.d(TAG,"Action1 was UP");
				        	startAutoScrolling();
				        	break;
				        case (MotionEvent.ACTION_CANCEL) :
				            Log.d(TAG,"Action1 was CANCEL");
				        	startAutoScrolling();
				        	break;
				    }      

					
					return false;
				}
			});
			horizontalOuterLayout.addView(convertView);
		}
		startAutoScrolling();
		Log.i(TAG, "Addview 1: " + System.currentTimeMillis());
		getScrollMaxAmount();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		return super.onTouchEvent(event);

	}

	public void stopAutoScrolling() {
		Log.i(TAG, "ScrollPosX = " + scrollPos);
		if (scrollTimer != null) {
			scrollTimer.cancel();
			scrollTimer = null;
		}
	}

	public static final long DELAY = 25;
	public static boolean setautoslide = false;
	int scrollPos = 0;

	/*
	 * private Handler handler = new Handler() {
	 * 
	 * public void dispatchMessage(android.os.Message msg) {
	 * 
	 * if (msg.what == 0) { if (mContext != null) { if (scrollPos >= scrollMax)
	 * { scrollPos = 0; Log.i(TAG, "scrollPos = " + scrollPos); }
	 * moveScrollView(); Message message = new Message(); message.what = 0; if
	 * (setautoslide) { handler.sendMessageDelayed(message, DELAY); } else {
	 * 
	 * } } } } };
	 */

	/*
	 * public void autoSlide() { final Handler mHandler = new Handler(); //
	 * Create runnable for posting final Runnable mUpdateResults = new
	 * Runnable() { public void run() {
	 * 
	 * AnimateandSlideShow();
	 * 
	 * } };
	 * 
	 * int delay = 1000; // delay for 1 sec. int period = 15000; // repeat every
	 * 4 sec. Timer timer = new Timer(); timer.scheduleAtFixedRate(new
	 * TimerTask() { public void run() { mHandler.post(mUpdateResults); }
	 * 
	 * }, delay, period); }
	 */

	public int currentimageindex = 0;

	/*
	 * private void AnimateandSlideShow() { final int value = currentimageindex
	 * % (ECBlogView.this.totalPage - 1); currentimageindex++;
	 * 
	 * }
	 */

	public abstract void openProduct(String id);

	public abstract void onCLick(String id_ec, String name);

	private String id_ec;
	private String name;
	HorizontalAdapter adapter;

	public void update(final Context context, String id_category, String name,
			int count, int countitem) {
		this.id_ec = id_category;
		this.name = name;
		text.setText(name);
		this.mContext = context;
		WindowManager window = (WindowManager) mContext
				.getSystemService(Context.WINDOW_SERVICE);
		Display display = window.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		widthScreen = size.x;
		heightScreen = size.y;
		countItem = countitem;
		Log.i(TAG, "CountItem = " + countItem);
		// Log.i(TAG, "Width = " + widthScreen);
		// Log.i(TAG, "Height = " + heightScreen);
		// this.countLine = count;
		setting = new Setting(mContext);
		isEnglish = setting.isLangEng();
		if (isEnglish) {
			options = new DisplayImageOptions.Builder()
					.showImageOnFail(R.drawable.mask_background)
					.showImageOnLoading(R.drawable.thumb_en)
					.showImageForEmptyUri(R.drawable.mask_background)
					.cacheInMemory().cacheInMemory(true).cacheOnDisc(true)
					.build();
		} else {
			options = new DisplayImageOptions.Builder()
					.showImageOnFail(R.drawable.mask_background)
					.showImageOnLoading(R.drawable.thumb_ja)
					.showImageForEmptyUri(R.drawable.mask_background)
					.cacheInMemory().cacheInMemory(true).cacheOnDisc(true)
					.build();
		}

		getDataFromDb();
		adapter = new HorizontalAdapter(getContext(), listObject);
		listview.setAdapter(adapter);
		adapter.notifyDataSetChanged();
		Log.i(TAG, " listview notifyDataSetChanged");

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String idProduct = listObject.get(position).getId();
				stopAutoScrolling();
				openProduct(idProduct);
			}
		});

		if (countLine == 1) {
			// setautoslide = true;
			// getScrollMaxAmount(context);
			// Message message = new Message();
			// message.what = 0;
			// handler.sendMessageDelayed(message, DELAY);
		}
		if (countLine == 1) {
			scrollPos = 0;
			/*
			 * Message message = new Message(); message.what = 0;
			 * handler.sendMessageDelayed(message, DELAY); int actualWidth =
			 * (listview.getMeasuredWidth()-512); ViewTreeObserver viewTree =
			 * listview.getViewTreeObserver();
			 * viewTree.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			 * 
			 * @Override public void onGlobalLayout() { int actualWidth =
			 * (listview.getMeasuredWidth()); scrollMax = actualWidth; } });
			 */
			// init slideview
			listview.setVisibility(View.INVISIBLE);
			View view = ((LayoutInflater) getContext().getSystemService(
					Context.LAYOUT_INFLATER_SERVICE)).inflate(
					R.layout.ec_blog_update, this);
			initSlideImage(view);

		} else {
			listview.setVisibility(View.VISIBLE);
		}
	}

	private void getDataFromDb() {
		listObject = new ArrayList<EcObject>();
		String where = String.format("%s = '%s' and %s = '%s' "//
				, Products.user_id, new Account(getContext()).getUserId() //
				, Products.id_ec, id_ec);//

		Cursor cursor = new Products(getContext()).querry(where);
		List<Object> list = new ArrayList<Object>();
		if (cursor != null) {

			while (cursor.moveToNext()) {
				EcObject obj = new EcObject();
				obj.setId(CommonAndroid.getString(cursor, Products.id));
				obj.setName(CommonAndroid.getString(cursor, Products.name));
				obj.setPrice(CommonAndroid.DecimalFormatPrice(CommonAndroid
						.getString(cursor, Products.base_price)));
				obj.setQuantity(CommonAndroid.getString(cursor,
						Products.quantity));
				obj.setStatusText(CommonAndroid.getString(cursor,
						Products.status_text));
				obj.setUrlImage(CommonAndroid.getString(cursor,
						Products.thumbnail));
				listObject.add(obj);
			}

			cursor.close();
		}

	}

	public class ViewPagerAdapter extends PagerAdapter {
		private List list = new ArrayList();

		public ViewPagerAdapter(List list) {
			this.list = list;

		}

		public int getCount() {
			return list.size();
		}

		public Object instantiateItem(View collection, int position) {

			View view = CommonAndroid.initView(collection.getContext(),
					R.layout.ec_blog_item_update, null);

			// Setting setting = new Setting(getContext());
			// String price = String.format(
			// "%s %s %s"//
			// ,
			// setting.isLangEng() ? getResources().getString(
			// R.string.ec_price_unit) : getResources().getString(
			// R.string.ec_price_unit_j)//
			// ,
			// (((String[]) list.get(position))[2]).toString()//
			// ,
			// setting.isLangEng() ? getResources().getString(
			// R.string.ec_taxincl) : getResources().getString(
			// R.string.ec_taxincl_j)//
			// );
			//
			// CommonAndroid.setText(view.findViewById(R.id.text_1),
			// ((String[]) list.get(position))[3]);

			// CommonAndroid.setText(view.findViewById(R.id.text_2), price);

			// String status_text = "";
			// String status_text_in = ""
			// + (setting.isLangEng() ? getResources().getString(
			// R.string.ec_in_stock) : getResources().getString(
			// R.string.ec_in_stock_j));
			// String status_text_out = ""
			// + (setting.isLangEng() ? getResources().getString(
			// R.string.ec_out_stock) : getResources().getString(
			// R.string.ec_out_stock_j));
			// // String status_text_tem = ((String[]) list.get(position))[4];
			// // if(
			// //
			// status_text_tem.equals(getResources().getString(R.string.ec_in_stock))
			// // ||
			// //
			// status_text_tem.equals(getResources().getString(R.string.ec_in_stock_j))
			// // ){
			// int status_text_tem = 0;
			// try {
			// status_text_tem = Integer.parseInt(((String[]) list
			// .get(position))[5]);
			// } catch (Exception e) {
			// // TODO Auto-generated catch block
			// e.printStackTrace();
			// }
			// String status_text_color = "#036898";
			// if (status_text_tem > 0) {
			// status_text_color = "#036898";
			// status_text = status_text_in;
			// } else {
			// status_text_color = "#ee0000";
			// status_text = status_text_out;
			// }
			// TextView text_color = (TextView) view.findViewById(R.id.text_3);
			// text_color.setTextColor(Color.parseColor(status_text_color));
			// CommonAndroid
			// .setText(view.findViewById(R.id.text_3), status_text /*
			// * ((String
			// * [])
			// * list
			// * .get
			// * (position
			// * ))[4]
			// */);

			ImageView imgeView = CommonAndroid.getView(view, R.id.img);
			ImageLoaderUtils.getInstance(getContext()).displayImageEcProduct(
					listObject.get(position).getUrlImage(), imgeView);

			// final String id = ((String[]) list.get(position))[0];
			final String id = listObject.get(position).getId();
			View gotodetail = CommonAndroid.getView(view, R.id.gotodetail);
			/* view */gotodetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					openProduct(id);
				}
			});

			((ViewPager) collection).addView(view, 0);
			// pager.setOnPageChangeListener(onPageChange);
			// totalPage = list.size();
			// currentPage = pager.getCurrentItem();
			// if (xx == 0) {
			// imgSlideBack.setEnabled(false);
			// imgSlideBack.setBackgroundResource(R.drawable.ic_back_slide_select);
			// xx = 1;
			// }
			// if(currentPage > 0 && currentPage < list.size() - 1) {
			// imgSlideBack.setEnabled(true);
			// imgSlideBack.setBackgroundResource(R.drawable.btn_slide_back);
			// imgSlideNext.setEnabled(true);
			// imgSlideNext.setBackgroundResource(R.drawable.btn_slide_next);
			// }

			return view;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView((View) arg2);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == ((View) arg1);
		}

		@Override
		public Parcelable saveState() {
			return null;
		}
	}

	private class HorizontalAdapter extends BaseAdapter {

		private Context mContext;
		private List<EcObject> listData;
		private LayoutInflater inf;

		public HorizontalAdapter(Context ctx, List<EcObject> list) {
			this.mContext = ctx;
			this.listData = list;
			inf = (LayoutInflater) ctx
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HolderView holder;
			if (convertView == null) {
				holder = new HolderView();
				convertView = inf.inflate(R.layout.ec_blog_item_update, null);
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						(int) (widthScreen / 2.2),
						android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
				convertView.setLayoutParams(params);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.img);

				holder.txtView1 = (MTextView) convertView
						.findViewById(R.id.text_1);
				holder.txtView2 = (TextView) convertView
						.findViewById(R.id.text_2);
				holder.txtView3 = (MTextView) convertView
						.findViewById(R.id.text_3);
				convertView.setTag(holder);
			} else {
				holder = (HolderView) convertView.getTag();
			}

			imageLoader.displayImage(listData.get(position).getUrlImage(),
					holder.imageView, options);

			Setting setting = new Setting(getContext());
			String price = String.format(
					"%s %s %s"//
					,
					setting.isLangEng() ? getResources().getString(
							R.string.ec_price_unit) : getResources().getString(
							R.string.ec_price_unit_j)//
					,
					(listData.get(position).getPrice()).toString()//
					,
					setting.isLangEng() ? getResources().getString(
							R.string.ec_taxincl) : getResources().getString(
							R.string.ec_taxincl_j)//
					);
			holder.txtView1.setText(listData.get(position).getName());

			holder.txtView2.setText(price);

			String status_text = "";
			String status_text_in = ""
					+ (setting.isLangEng() ? getResources().getString(
							R.string.ec_in_stock) : getResources().getString(
							R.string.ec_in_stock_j));
			String status_text_out = ""
					+ (setting.isLangEng() ? getResources().getString(
							R.string.ec_out_stock) : getResources().getString(
							R.string.ec_out_stock_j));

			int status_text_tem = 0;
			try {
				status_text_tem = Integer.parseInt(listData.get(position)
						.getQuantity());
			} catch (Exception e) {
				e.printStackTrace();
			}
			int status_text_color = getResources().getColor(R.color.ec_selection_status_text_in);
			if (status_text_tem > 0) {
				status_text_color = getResources().getColor(R.color.ec_selection_status_text_in);
				status_text = status_text_in;
			} else {
				status_text_color = getResources().getColor(R.color.ec_selection_status_text_out);
				status_text = status_text_out;
			}
			holder.txtView3.setTextColor(status_text_color);
			holder.txtView3.setText(status_text);

			return convertView;
		}

	}

	private class HolderView {
		private ImageView imageView;
		private MTextView txtView1;
		private TextView txtView2;
		private MTextView txtView3;
	}

}