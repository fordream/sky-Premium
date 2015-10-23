package z.lib.base.wheel;

import java.util.Calendar;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;


public class DatePickerDailog extends Dialog {

	private Context Mcontex;
private Calendar calendar_set;
	private int NoOfYear = 40; 
	
	public DatePickerDailog(Context context, Calendar calendar,
			final DatePickerListner dtp) {

		super(context);
		Mcontex = context;
		calendar_set = calendar;
		LinearLayout lytmain = new LinearLayout(Mcontex);
		lytmain.setOrientation(LinearLayout.VERTICAL);
		LinearLayout lytdate = new LinearLayout(Mcontex);
		LinearLayout lytbutton = new LinearLayout(Mcontex);

		Button btnset = new Button(Mcontex);
		Button btncancel = new Button(Mcontex);

		if(new Setting(context).isLangEng()){
			btnset.setText(getContext().getString(R.string.btn_set));
			btncancel.setText(getContext().getString(R.string.btn_cancel));
		}else{
			btnset.setText(getContext().getString(R.string.btn_set_j));
			btncancel.setText(getContext().getString(R.string.btn_cancel_j));
		}

		final WheelView month = new WheelView(Mcontex);
		final WheelView year = new WheelView(Mcontex);
		final WheelView day = new WheelView(Mcontex);

		lytdate.addView(day, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f));
		lytdate.addView(month, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 0.8f));
		lytdate.addView(year, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		lytbutton.addView(btnset, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

		lytbutton.addView(btncancel, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		lytbutton.setPadding(5, 5, 5, 5);
		lytmain.addView(lytdate);
		lytmain.addView(lytbutton);

		setContentView(lytmain);

		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDays(year, month, day);

			}
		};

		// month
		int curMonth = calendar.get(Calendar.MONTH);
		String months[] = new String[] { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		if(!new Setting(context).isLangEng()){
			for(int i= 0; i < months.length; i++){
				months[i] = (i + 1) + getContext().getString(R.string.month);
			}
		}
		month.setViewAdapter(new DateArrayAdapter(context, months, curMonth));
		month.setCurrentItem(curMonth);
		month.addChangingListener(listener);

		Calendar cal = calendar;//Calendar.getInstance();
		// year
		int curYear = calendar.get(Calendar.YEAR);
		int Year = cal.get(Calendar.YEAR);
		
		
		year.setViewAdapter(new DateNumericAdapter(context, Year - NoOfYear,
				Year + NoOfYear, NoOfYear));
		year.setCurrentItem(curYear-(Year-NoOfYear));
		year.addChangingListener(listener);

		// day
		updateDays(year, month, day);
		day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);

		btnset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar c = updateDays(year, month, day);
				dtp.OnDoneButton(DatePickerDailog.this, c);
			}
		});
		btncancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dtp.OnCancelButton(DatePickerDailog.this);

			}
		});

	}

	public DatePickerDailog(Context context, Calendar calendar, String type,
			final DatePickerListner dtp) {

		super(context);
		Mcontex = context;
		calendar_set = calendar;
		LinearLayout lytmain = new LinearLayout(Mcontex);
		lytmain.setOrientation(LinearLayout.VERTICAL);
		LinearLayout lytdate = new LinearLayout(Mcontex);
		LinearLayout lytbutton = new LinearLayout(Mcontex);

		Button btnset = new Button(Mcontex);
		Button btncancel = new Button(Mcontex);
		if(new Setting(context).isLangEng()){
			btnset.setText(getContext().getString(R.string.btn_set));
			btncancel.setText(getContext().getString(R.string.btn_cancel));
		}else{
			btnset.setText(getContext().getString(R.string.btn_set_j));
			btncancel.setText(getContext().getString(R.string.btn_cancel_j));
		}
		

		final WheelView month = new WheelView(Mcontex);
		final WheelView year = new WheelView(Mcontex);
		final WheelView day = new WheelView(Mcontex);

//		lytdate.addView(day, new LayoutParams(
//				android.view.ViewGroup.LayoutParams.FILL_PARENT,
//				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.2f));
		lytdate.addView(month, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f));
		lytdate.addView(year, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1.5f));
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		lytbutton.addView(btnset, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

		lytbutton.addView(btncancel, new LayoutParams(
				android.view.ViewGroup.LayoutParams.FILL_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
		lytbutton.setPadding(5, 5, 5, 5);
		lytmain.addView(lytdate);
		lytmain.addView(lytbutton);

		setContentView(lytmain);

		getWindow().setLayout(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		OnWheelChangedListener listener = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				updateDays(year, month, day);

			}
		};

		// month
		int curMonth = calendar.get(Calendar.MONTH);
		String months[] = new String[] { "January", "February", "March",
				"April", "May", "June", "July", "August", "September",
				"October", "November", "December" };
		if(!new Setting(context).isLangEng()){
			for(int i= 0; i < months.length; i++){
				months[i] = (i + 1) + getContext().getString(R.string.month);
			}
		}
		month.setViewAdapter(new DateArrayAdapter(context, months, curMonth));
		month.setCurrentItem(curMonth);
		month.addChangingListener(listener);

		Calendar cal = calendar;//Calendar.getInstance();
		// year
		int curYear = calendar.get(Calendar.YEAR);
		int Year = cal.get(Calendar.YEAR);
		
		
		year.setViewAdapter(new DateNumericAdapter(context, Year - NoOfYear,
				Year + NoOfYear, NoOfYear));
		year.setCurrentItem(curYear-(Year-NoOfYear));
		year.addChangingListener(listener);

		// day
		updateDays(year, month, day);
		day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);

		btnset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar c = updateDays(year, month, day);
				dtp.OnDoneButton(DatePickerDailog.this, c);
			}
		});
		btncancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				dtp.OnCancelButton(DatePickerDailog.this);

			}
		});

	}
	
	Calendar updateDays(WheelView year, WheelView month, WheelView day) {
		//TODO: updateDays
		Calendar calendar = Calendar.getInstance();
//		LogUtils.e("updateDays", "dateandtime21===" + calendar.toString());
//		LogUtils.e("updateDays", "dateandtime21===year:" + year.getCurrentItem() +":NoOfYear:"+NoOfYear +":month:" + month.getCurrentItem());
		calendar.set(Calendar.YEAR,
				calendar_set.get(Calendar.YEAR) + (year.getCurrentItem()-NoOfYear));
		
		calendar.set(Calendar.MONTH, month.getCurrentItem());//OK

		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
		
		day.setViewAdapter(new DateNumericAdapter(Mcontex, 1, maxDays, calendar_set
				.get(Calendar.DAY_OF_MONTH) - 1));
		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
		day.setCurrentItem(curDay - 1, true);
		calendar.set(Calendar.DAY_OF_MONTH, curDay);
		
//		LogUtils.e("updateDays", "dateandtime22===" + calendar.toString());
		
//		calendar.set(Calendar.YEAR, year.getCurrentItem());
//		calendar.set(Calendar.MONTH, month.getCurrentItem());
//		
//		int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
//		day.setViewAdapter(new DateNumericAdapter(Mcontex, 1, maxDays, calendar
//				.get(Calendar.DAY_OF_MONTH) - 1));
//		int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
//		day.setCurrentItem(curDay - 1, true);
//		calendar.set(Calendar.DAY_OF_MONTH, curDay);
		return calendar;

	}

	private class DateNumericAdapter extends NumericWheelAdapter {
		int currentItem;
		int currentValue;

		public DateNumericAdapter(Context context, int minValue, int maxValue,
				int current) {
			super(context, minValue, maxValue);
			this.currentValue = current;
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(null, Typeface.BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	private class DateArrayAdapter extends ArrayWheelAdapter<String> {
		int currentItem;
		int currentValue;

		public DateArrayAdapter(Context context, String[] items, int current) {
			super(context, items);
			this.currentValue = current;
			setTextSize(20);
		}

		@Override
		protected void configureTextView(TextView view) {
			super.configureTextView(view);
			if (currentItem == currentValue) {
				view.setTextColor(0xFF0000F0);
			}
			view.setTypeface(null, Typeface.BOLD);
		}

		@Override
		public View getItem(int index, View cachedView, ViewGroup parent) {
			currentItem = index;
			return super.getItem(index, cachedView, parent);
		}
	}

	public interface DatePickerListner {
		public void OnDoneButton(Dialog datedialog, Calendar c);

		public void OnCancelButton(Dialog datedialog);
	}
}
