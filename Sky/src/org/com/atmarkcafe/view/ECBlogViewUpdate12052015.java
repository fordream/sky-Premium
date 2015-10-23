package org.com.atmarkcafe.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderUtils;
import z.lib.base.LogUtils;
import z.lib.base.SkyLoader;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Products;
import com.acv.cheerz.db.ProductsOfCategories;
import com.acv.cheerz.db.Setting;

//com.acv.cheerz.view.HeaderView
public abstract class ECBlogViewUpdate12052015 extends LinearLayout {
	private org.com.atmarkcafe.sky.customviews.charting.MTextView text;
	private ViewPager pager;
	private com.viewpagerindicator.CirclePageIndicator indicator;
	private ImageButton imgSlideBack, imgSlideNext;
	private int currentPage, totalPage;
	private String TAG = "ECBlogView";

	public ECBlogViewUpdate12052015(Context context) {
		super(context);
		init();
	}

	public ECBlogViewUpdate12052015(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.ec_blog_update,this);
		View view = ((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.ec_blog_update,this);
		text = CommonAndroid.getView(this, R.id.text);
		pager = CommonAndroid.getView(this, R.id.pager);
		pager.setOffscreenPageLimit(5);
		pager.setPageMargin(15);
		pager.setClipChildren(false);
		
		// current not use
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

		imgSlideBack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentPage = pager.getCurrentItem();
				if (currentPage > 0) {
					currentPage -= 1;
					Log.i(TAG, "slide back currentPage = " + currentPage);
					pager.setCurrentItem(currentPage);
					imgSlideNext.setEnabled(true);
					imgSlideNext
							.setBackgroundResource(R.drawable.btn_slide_next);
					imgSlideBack
							.setBackgroundResource(R.drawable.btn_slide_back);
					imgSlideBack.setEnabled(true);
					if (currentPage == 0) {
						imgSlideBack.setEnabled(false);
						imgSlideBack
								.setBackgroundResource(R.drawable.ic_back_slide_select);
					}
				}
			}
		});

		imgSlideNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				currentPage = pager.getCurrentItem();
				if (currentPage < ECBlogViewUpdate12052015.this.totalPage - 1) {
					Log.i(TAG, "slide next currentPage = " + currentPage);
					currentPage += 1;
					pager.setCurrentItem(currentPage);
					imgSlideNext.setEnabled(true);
					imgSlideNext
							.setBackgroundResource(R.drawable.btn_slide_next);
					imgSlideBack
							.setBackgroundResource(R.drawable.btn_slide_back);
					imgSlideBack.setEnabled(true);
					if (currentPage == totalPage - 1) {
						imgSlideNext.setEnabled(false);
						imgSlideNext
								.setBackgroundResource(R.drawable.ic_next_slide_select);
					}
				}

			}
		});

		// pager.setOnTouchListener(new OnTouchListener() {
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// int kk = pager.getCurrentItem();
		// Log.i(TAG, "Curent====> Page = " + kk);
		// if (kk == 1) {
		// imgSlideBack.setEnabled(false);
		// imgSlideBack.setBackgroundResource(R.drawable.ic_back_slide_select);
		// }else if (kk == totalPage - 2) {
		// imgSlideNext.setEnabled(false);
		// imgSlideNext.setBackgroundResource(R.drawable.ic_next_slide_select);
		// }else if (kk > 0 && kk < totalPage - 1) {
		// imgSlideBack.setEnabled(true);
		// imgSlideBack.setBackgroundResource(R.drawable.btn_slide_back);
		// imgSlideNext.setEnabled(true);
		// imgSlideNext.setBackgroundResource(R.drawable.btn_slide_next);
		// }
		// return false;
		// }
		// });

		// findViewById(R.id.img_slide_back_id).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Log.i("EC", "Back");
		// }
		// });
		//
		// findViewById(R.id.img_slide_next_id).setOnClickListener(new
		// OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Log.i("EC", "Next");
		// }
		// });

		// imgSlideBack.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Log.i("ECBlog", "Slide Back");
		// }
		// });
		//
		// imgSlideNext.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// Log.i("ECBlog", "Slide Next");
		// }
		// });
//		autoSlide();
	}
	
	public void autoSlide() {
		final Handler mHandler = new Handler();
        // Create runnable for posting
        final Runnable mUpdateResults = new Runnable() {
            public void run() {
            	
            	AnimateandSlideShow();
            	
            }
        };
		
        int delay = 1000; // delay for 1 sec.
        int period = 15000; // repeat every 4 sec.
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
        public void run() {
        	 mHandler.post(mUpdateResults);
        }

        }, delay, period);
        LogUtils.e(TAG, "auto");
    }
	
	public int currentimageindex=0;
	private void AnimateandSlideShow() {
		final int value = currentimageindex%(ECBlogViewUpdate12052015.this.totalPage - 1);
		pager.setCurrentItem(value, true);
   		currentimageindex++;
    	
    }

	public abstract void openProduct(String id);

	public abstract void onCLick(String id_ec, String name);

	private String id_ec;
	private String name;

	public void update(String id_category, String name) {
		this.id_ec = id_category;
		this.name = name;
		text.setText(name);

		new SkyLoader() {

			@Override
			public void loadSucess(Object list) {
				pager.setAdapter(new ViewPagerAdapter((List<Object>) list));
				indicator.setViewPager(pager);
			}

			@Override
			public Object loadData() {
//				Cursor productOfCategory = new ProductsOfCategories(
//						getContext()).querry(String.format(
//						"%s = '%s' and %s = '%s'",
//						ProductsOfCategories.user_id,
//						new Account(getContext()).getUserId()//
//						, ProductsOfCategories.id_category, id_ec));
//
//				String products = "";

//				while (productOfCategory != null
//						&& productOfCategory.moveToNext()) {
//					if (CommonAndroid.isBlank(products)) {
//						products = CommonAndroid.getString(productOfCategory,
//								ProductsOfCategories.id_product);
//					} else {
//						products = String.format("%s,%s", products,
//								CommonAndroid.getString(productOfCategory,
//										ProductsOfCategories.id_product));
//					}
//				}
//
//				if (productOfCategory != null) {
//					productOfCategory.close();
//				}

//				String where = String.format(
//						"%s = '%s' and %s in(%s) "//
//						, Products.user_id,
//						new Account(getContext()).getUserId() //
//						, Products.id, products);//
				
				String where = String.format(
						"%s = '%s' and %s = '%s' "//
						, Products.user_id,
						new Account(getContext()).getUserId() //
						, Products.id_ec, id_ec);//

				Cursor cursor = new Products(getContext()).querry(where);
				List<Object> list = new ArrayList<Object>();
				if (cursor != null) {

					while (cursor.moveToNext()) {
						list.add(new String[] {//
								CommonAndroid.getString(cursor, Products.id)// id
																			// 0
								,
								CommonAndroid.getString(cursor,
										Products.thumbnail)// 1
								,
								CommonAndroid.DecimalFormatPrice(CommonAndroid
										.getString(cursor, Products.base_price))// 2
								,
								CommonAndroid.getString(cursor, Products.name) // 3
																				// description_short
								,
								CommonAndroid.getString(cursor,
										Products.status_text) // 4
								,
								CommonAndroid.getString(cursor,
										Products.quantity) // 5
						});
					}

					cursor.close();
				}
				return list;
			}
		}.executeLoader(0);

	}

	private int xx = 0;
	public class ViewPagerAdapter extends PagerAdapter {
		private List list = new ArrayList();

		public ViewPagerAdapter(List list) {
			this.list = list;
			
		}

		public int getCount() {
			return list.size();
		}

		public Object instantiateItem(View collection, int position) {

			View view = CommonAndroid.initView(collection.getContext(),	R.layout.ec_blog_item, null);

			Setting setting = new Setting(getContext());
			String price = String.format(
					"%s %s %s"//
					,
					setting.isLangEng() ? getResources().getString(
							R.string.ec_price_unit) : getResources().getString(
							R.string.ec_price_unit_j)//
					,
					(((String[]) list.get(position))[2]).toString()//
					,
					setting.isLangEng() ? getResources().getString(
							R.string.ec_taxincl) : getResources().getString(
							R.string.ec_taxincl_j)//
					);
			
			CommonAndroid.setText(view.findViewById(R.id.text_1),
					((String[]) list.get(position))[3]);
			CommonAndroid.setText(view.findViewById(R.id.text_2), price);
			String status_text = "";
			String status_text_in = " - "
					+ (setting.isLangEng() ? getResources().getString(
							R.string.ec_in_stock) : getResources().getString(
							R.string.ec_in_stock_j));
			String status_text_out = " - "
					+ (setting.isLangEng() ? getResources().getString(
							R.string.ec_out_stock) : getResources().getString(
							R.string.ec_out_stock_j));
			// String status_text_tem = ((String[]) list.get(position))[4];
			// if(
			// status_text_tem.equals(getResources().getString(R.string.ec_in_stock))
			// ||
			// status_text_tem.equals(getResources().getString(R.string.ec_in_stock_j))
			// ){
			int status_text_tem = 0;
			try {
				status_text_tem = Integer.parseInt(((String[]) list
						.get(position))[5]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String status_text_color = "#036898";
			if (status_text_tem > 0) {
				status_text_color = "#036898";
				status_text = status_text_in;
			} else {
				status_text_color = "#ee0000";
				status_text = status_text_out;
			}
			TextView text_color = (TextView) view.findViewById(R.id.text_3);
			text_color.setTextColor(Color.parseColor(status_text_color));
			CommonAndroid
					.setText(view.findViewById(R.id.text_3), status_text /*
																		 * ((String
																		 * [])
																		 * list
																		 * .get
																		 * (position
																		 * ))[4]
																		 */);
			ImageView imgeView = CommonAndroid.getView(view, R.id.img);
			ImageLoaderUtils.getInstance(getContext()).displayImageEcProduct(
					((String[]) list.get(position))[1], imgeView);

			final String id = ((String[]) list.get(position))[0];
			View gotodetail = CommonAndroid.getView(view, R.id.gotodetail);
			/*view*/gotodetail.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					openProduct(id);
				}
			});

			((ViewPager) collection).addView(view, 0);
			pager.setOnPageChangeListener(onPageChange);
			totalPage = list.size();
			currentPage = pager.getCurrentItem();
			 if (xx == 0) {
				 imgSlideBack.setEnabled(false);
				 imgSlideBack.setBackgroundResource(R.drawable.ic_back_slide_select);
				 xx  = 1;
			 }
			 if(currentPage > 0 && currentPage < list.size() - 1) {
					imgSlideBack.setEnabled(true);
					imgSlideBack.setBackgroundResource(R.drawable.btn_slide_back);
					imgSlideNext.setEnabled(true);
					imgSlideNext.setBackgroundResource(R.drawable.btn_slide_next);
				}
			
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

	private final ViewPager.OnPageChangeListener onPageChange = new ViewPager.OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int arg0) {
			
			int kk = pager.getCurrentItem();
			
			if (kk == 0) {
				imgSlideBack.setEnabled(false);
				imgSlideBack.setBackgroundResource(R.drawable.ic_back_slide_select);
			} else if (kk == totalPage - 1) {
				imgSlideNext.setEnabled(false);
				imgSlideNext.setBackgroundResource(R.drawable.ic_next_slide_select);
			} else  {
				imgSlideBack.setEnabled(true);
				imgSlideBack.setBackgroundResource(R.drawable.btn_slide_back);
				imgSlideNext.setEnabled(true);
				imgSlideNext.setBackgroundResource(R.drawable.btn_slide_next);
			}
		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	};
}