package org.com.atmarkcafe.sky.fragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.object.ItemMail;
import org.com.atmarkcafe.sky.GcmBroadcastReceiver;
import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.customviews.charting.MButton;
import org.com.atmarkcafe.sky.customviews.charting.MEditText;
import org.com.atmarkcafe.sky.customviews.charting.MTextView;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
//import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

@SuppressLint("DefaultLocale")
public class MailboxFragment extends BaseFragment implements OnClickListener {

	private String TAG = "MailboxFragment";
	public static List<ItemMail> listEmail; 
	private ListView listview;
	private MailboxAdapter mailboxAdapter;
	private HeaderView titleFragment;
	private ImageButton btnSearch, btnHome;
	private Button btnk;
	private Setting settings;
	private LinearLayout searchView;
	private boolean openSearchView = false;
	private RelativeLayout rlDateFrom, rlDateTo;
	public String dateFromTag, dateToTag;
	public String dateFromValue ;
	public String dateToValue ;
	public String subjectValue ;
	private MTextView txtDateFrom, txtDateTo, txtDoneSearch;
	private MEditText edtSubject;
	private TextView txtheader, txtSearch;
	private boolean languageId = true;
	private LoadingView loading;
	private int width, height;
	private MTextView emtyView;
	private ImageView imgClearInput;
	private MButton btnClearDate;
	private RelativeLayout parentView;
	private int typeSearch = 0;
	public static boolean flagBackFromDetailEmail = false;
	public boolean flagSearchlEmail = false;
	private static List<ItemMail> listSort;
	public static List<ItemMail> listMailResult = new ArrayList<ItemMail>();
	public static List<ItemMail> listMailResultSearch = new ArrayList<ItemMail>();
	private SharedPreferences sharePref;
	public boolean flagOpenDatePicker = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@SuppressLint("NewApi")
	private void getScreenSize() {
		Display display = getActivity().getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
	}

	private String limitDate;
	
	private void getAllMailBox() {
		listEmail = new ArrayList<ItemMail>();
		
		SimpleDateFormat format = new SimpleDateFormat("");
		if (settings.isLangEng()) {
			format =  new SimpleDateFormat("dd/MM/yyyy");
		}else{
			format =  new SimpleDateFormat("yyyy/MM/dd");
		}
		
		HashMap<String, String> params = new HashMap<String, String>();
		
		params.put("req[param][subject]", "");
		params.put("req[param][start_received]", "");
		params.put("req[param][end_received]", "");
		//
		if (getActivity() != null) {
			SkyUtils.execute(getActivity(), RequestMethod.GET,
					API.API_USER_MAILBOX, params, new CheerzServiceCallBack() {

						@Override
						public void onStart() {
							CommonAndroid.showView(true, loading);
							super.onStart();
						}

						@Override
						public void onError(String message) {
							CommonAndroid.showView(false, loading);
							if (!isFinish()) {
								if (languageId) {
									SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure), null);
								}else{
									SkyUtils.showDialog(getActivity(), getResources().getString(R.string.msg_failure_j), null);
								}
								getBadge();
							}
						}
						
						@Override
						public void onErrorAccountConflic(String respone) {
							
							super.onErrorAccountConflic(respone);
						}
						
						@Override
						public void onSucces(String response) {
							super.onSucces(response);
							CommonAndroid.showView(false, loading);
							try {
								JSONObject jsonObject = new JSONObject(response);
								int isSuccess = jsonObject.getInt("is_success");
								
								if (isSuccess == 1) {
									if (listEmail.size() > 0) {
										listEmail.clear();
									}
									JSONArray jsonArray = jsonObject.getJSONArray("data");
									for (int i = 0; i < jsonArray.length(); i++) {
										JSONObject jsonObj = jsonArray.getJSONObject(i);
										ItemMail item = new ItemMail();
										item.setId(jsonObj.getInt("id"));
										item.setSubject(jsonObj.getString("subject"));
										item.setContent(jsonObj.getString("content"));
										item.setDateTime(jsonObj.getString("datetime").replace("-", "/"));
										item.setType(1);
										item.setStatusOpen(jsonObj.getInt("new"));
										
										listEmail.add(item);
									}
								}
								sortEmail(listEmail);
								if (!listMailResult.isEmpty()) {
									listMailResult.clear();
								}
								listMailResult.addAll(listEmail);
							} catch (JSONException e) {
								e.printStackTrace();
							}
							getBadge();
						}

						@Override
						public void onSucces(JSONObject jsonObject) {
							super.onSucces(jsonObject);
						}

					});
		}

	}
	
	private void OnChangLanguage() {
		getAllMailBox();
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	private int TYPE_HEADER = 0;
	public class MailboxAdapter extends BaseAdapter {

		private List<ItemMail> Data;
		private Context mContext;

		public MailboxAdapter(Context ctx, List<ItemMail> list) {
			this.mContext = ctx;
			this.Data = list;
		}

		@Override
		public int getCount() {

			return Data.size();
		}

		@Override
		public Object getItem(int position) {

			return Data.get(position);
		}

		@Override
		public long getItemId(int position) {

			return Data.get(position).getId();
		}

		public int getItemViewType(int position) {
			if (Data.get(position).getType() == TYPE_HEADER  ) {
				return 0;
			}
				return 1;
			
		};
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			int type = getItemViewType(position);
			View rowView = convertView;
			// fill data
			Holder	holderView = new Holder();
			LayoutInflater inf = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (type == 0) {
				rowView = inf.inflate(R.layout.item_header, null);
				holderView.txtHeaderItem = (TextView)rowView.findViewById(R.id.item_header_text_id);
				holderView.txtSubject = (TextView)rowView.findViewById(R.id.mailbox_row_date_id);
				holderView.iconNews = (ImageView)rowView.findViewById(R.id.icon_news_id);
				holderView.txtHeaderItem.setText(Data.get(position).getDateTime());
				holderView.txtSubject.setText(Data.get(position).getSubject());
				if (Data.get(position).getNewsStatus() == 1) {
					if (Data.get(position).getStatusOpen() == 1) {
						holderView.iconNews.setVisibility(View.VISIBLE);
					}else{
						holderView.iconNews.setVisibility(View.INVISIBLE);
					}
				}else{
					holderView.iconNews.setVisibility(View.INVISIBLE);
				}
			} else {
				rowView = inf.inflate(R.layout.row_mailbox, null);
				holderView.txtSubjectItem = (TextView)rowView.findViewById(R.id.mailbox_row_subject_id);
				holderView.iconNews = (ImageView)rowView.findViewById(R.id.icon_news_id);
				holderView.txtSubjectItem.setText(Data.get(position).getSubject());
				if (Data.get(position).getStatusOpen() == 1) {
					holderView.iconNews.setVisibility(View.VISIBLE);
				}else{
					holderView.iconNews.setVisibility(View.INVISIBLE);
				}
			}
			
			rowView.setTag(holderView);
			return rowView;
		}
	}
	
	public static class Holder {
		TextView txtSubjectItem;
		TextView txtHeaderItem;
		TextView txtSubject;
		ImageView iconNews;
	}
	

	@Override
	public void onClick(View v) {
		int viewId = v.getId();
		switch (viewId) {
		case R.id.header_btn_right:
			if (openSearchView) {
				openSearchView = false;
			} else {
				openSearchView = true;
			}
			addViewSearch();
			showDataOld();
			break;
		case R.id.header_btn_left:
			SkyMainActivity.sm.toggle();
			break;

		case R.id.mailbox_rl_search_date_from_id:
			dateFromTag = "FROM_TAG";
			new DatePickerFragment().show(getActivity().getSupportFragmentManager(), dateFromTag);
			break;
		case R.id.mailbox_rl_search_date_to_id:
			dateToTag = "TO_TAG";
			new DatePickerFragment().show(getActivity().getSupportFragmentManager(), dateToTag);
			break;
		case R.id.mailbox_search_done_id:
			try {
				InputMethodManager inputMethodManager = (InputMethodManager)  getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
			} catch (Exception e) {}
			if (subjectValue != null && (dateFromValue != null || dateToValue != null)) {
				typeSearch = 3;
			}
			
			processSearch(typeSearch);
			break;
		case R.id.mail_box_edtsearch_id:
			edtSubject.setText("");
			break;
		case R.id.mailbox_clear_subject_id:
			edtSubject.setText("");
			subjectValue = null;
			sortEmail(listMailResult);
			if (subjectValue == null && dateFromValue == null && dateToValue == null) {
				showDataOld();
				Log.i(TAG, "11111111111111111113333333");
			}else if (dateFromValue != null || dateToValue != null) {
				typeSearch = 2;
				processSearch(typeSearch);
				Log.i(TAG, "1111111111111111111");
			}else if(dateFromValue == null || dateToValue == null){
				showDataOld();
				Log.i(TAG, "111111111111111111122222222222");
			}
			
			break;
		case R.id.mailbox_search_btnclear_date_id:
			txtDateFrom.setText("");
			txtDateTo.setText("");
			dateFromValue = null;
			dateToValue = null;
			sortEmail(listMailResult);
			if (subjectValue != null) {
				typeSearch = 1;
				processSearch(typeSearch);
			}else{
				showDataOld();
			}
			
			break;
		default:
			break;
		}
	}

	public void addViewSearch() {
		if (openSearchView) {
			searchView.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			searchView.setLayoutParams(params);
		} else {
			searchView.setVisibility(View.INVISIBLE);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, 0);
			searchView.setLayoutParams(params);
			emtyView.setVisibility(View.INVISIBLE);
			listview.setVisibility(View.VISIBLE);
			edtSubject.setText("");
			txtDateFrom.setText("");
			txtDateTo.setText("");
		}
		searchView.invalidate();
	}

	SimpleDateFormat format = null;
	
	@SuppressLint("SimpleDateFormat")
	private void sortEmail(List<ItemMail> listData) {
		if (listData.size() > 0) {
			
		
		Collections.sort(listData, new Comparator<ItemMail>() {

			@Override
			public int compare(ItemMail obj1, ItemMail obj2) {
				Date date1 = null, date2 = null;
				try {
					String strDate1 = CommonAndroid.convertDate(getActivity(), obj1.getDateTime().trim(), 0);
					String strDate2 = CommonAndroid.convertDate(getActivity(), obj2.getDateTime().trim(), 0);
					if (settings.isLangEng()) {
						format =  new SimpleDateFormat("dd/MM/yyyy");
					}else{
						format =  new SimpleDateFormat("yyyy/MM/dd");
					}
					date1 = format.parse(strDate1);
					date2 = format.parse(strDate2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return date1.compareTo(date2);
			}

		});
		
		Collections.reverse(listData);
		
		if (settings.isLangEng()) {
			format =  new SimpleDateFormat("dd/MM/yyyy");
			limitDate = GlobalFunction.getCalculateDate("dd/MM/yyyy", -10);
			for (int i = 0; i < listData.size(); i++) {
				String strDate = CommonAndroid.convertDate(getActivity(), listData.get(i).getDateTime().trim(), 0);
				listData.get(i).setDateTime(strDate);
				try {
					Date datei = format.parse(strDate);
					Date dateLimit = format.parse(limitDate);
					if (datei.after(dateLimit)) {
						listData.get(i).setNewsStatus(1);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}else{
			format =  new SimpleDateFormat("yyyy/MM/dd");
			limitDate = GlobalFunction.getCalculateDate("yyyy/MM/dd", -10);
			for (int i = 0; i < listData.size(); i++) {
				String strDate = CommonAndroid.convertDate(getActivity(), listData.get(i).getDateTime().trim(), 0);
				listData.get(i).setDateTime(strDate);
				try {
					Date datei = format.parse(strDate);
					Date dateLimit = format.parse(limitDate);
					if (datei.after(dateLimit)) {
						listData.get(i).setNewsStatus(1);
					}
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		for (int i = listData.size() - 1; i > 0; i--) {
			Date date1 = null, date2 = null;
			try {
				String strDate1 = CommonAndroid.convertDate(getActivity(), listData.get(i).getDateTime().trim(), 0);
				date1 = format.parse(strDate1);
				String strDate2 = CommonAndroid.convertDate(getActivity(), listData.get(i - 1).getDateTime().trim(), 0);
				date2 = format.parse(strDate2);
				
				if (!date1.equals(date2)) {
					listData.get(i).setType(0);
					
				} else {
					listData.get(i).setType(1);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		}
		 
		listData.get(0).setType(0);
		
		//listEmail = listData;//hieudd
		
		mailboxAdapter = new MailboxAdapter(getActivity(), listData);
		listview.setAdapter(mailboxAdapter);
		emtyView.setVisibility(View.INVISIBLE);
		listview.setVisibility(View.VISIBLE);
		}
	}

	private List<ItemMail> searchBySubject(List<ItemMail> list){
		listSort = new ArrayList<ItemMail>();
		
		subjectValue = edtSubject.getText().toString();
		for (int i = 0; i < list.size(); i++) {
			String str1 = list.get(i).getSubject().toUpperCase().trim();
			String str2 = subjectValue.toUpperCase().trim();
			
			if (str1.contains(str2)) {
				listSort.add(list.get(i));
				str1 = null;
				str2 = null;
			}
		}
//		sharePref.edit().putString("subjectValue", subjectValue).commit();
//		sharePref.edit().putString("date_FromValue", null).commit();
//		sharePref.edit().putString("date_ToValue", null).commit();
		return listSort;
	}
	
	private List<ItemMail> searchByDate(List<ItemMail> list){
		Date dateFrom = null, dateTo = null;
		listSort = new ArrayList<ItemMail>();
//		sharePref.edit().putString("date_FromValue", dateFromValue).commit();
//		sharePref.edit().putString("date_ToValue", dateToValue).commit();
//		sharePref.edit().putString("subjectValue", null).commit();
		Log.i(TAG, "=============== : " + dateToValue);
//		if (dateToValue.equalsIgnoreCase("")) {
//			dateToValue = null;
//		}
//		
//		if (dateFromValue.equalsIgnoreCase("")) {
//			dateFromValue = null;
//		}
		
		for (int i = 0; i < list.size(); i++) {
			try {
				if (dateFromValue != null) {
					dateFrom = format.parse(dateFromValue.trim());
				} else {
					dateFrom = new Date();
				}
				
				if (dateToValue != null) {
					dateTo = format.parse(dateToValue.trim());
				} else {
					dateTo = new Date();
				}
				
				Date dateEmail = format.parse(list.get(i).getDateTime().trim());

				if (dateEmail.after(dateFrom) && dateEmail.before(dateTo)) {
					listSort.add(list.get(i));
				}
				
				
				if (dateEmail.compareTo(dateFrom) == 0 || dateEmail.compareTo(dateTo) == 0) {
					listSort.add(list.get(i));
				}
				
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}

		}
		
		return listSort;
	}
	/**
	 * 
	 * @param type : 1 => search by subject
	 * @param type : 2 => search by date time
	 * @param type : 3 => search by 1+2
	 * 
	 */
	public void processSearch(int type) {
		listSort = null;
		flagSearchlEmail = true;
			if (type == 1) {
				listSort = new ArrayList<ItemMail>();
				listSort = searchBySubject(listEmail);
			} else if(type == 2){
				listSort = new ArrayList<ItemMail>();
				listSort = searchByDate(listEmail);
			}else if(type == 0){
				listSort = new ArrayList<ItemMail>(); 
				listSort = listEmail;
			}else if(type == 3){
				listSort = searchBySubject(listEmail);
				listSort = searchByDate(listSort);
			}
		
		if (listSort.size() > 0) {
			sortEmail(listSort);
			listMailResultSearch = listSort;
			mailboxAdapter.notifyDataSetChanged();	
			emtyView.setVisibility(View.INVISIBLE);
			listview.setVisibility(View.VISIBLE);
		} else {
			emtyView.setVisibility(View.VISIBLE);
			listview.setVisibility(View.INVISIBLE);
		}

	}

	TextView.OnEditorActionListener edtListenner = new TextView.OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			return false;
		}
	};
	
	@SuppressLint("ValidFragment")
	public class DatePickerFragment extends SherlockDialogFragment implements
			DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			flagOpenDatePicker  = true;
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			if (this.getTag().equalsIgnoreCase(dateFromTag)) {
				dateFromValue = String.valueOf(year) + "/"	+ String.valueOf(month + 1) + "/" + String.valueOf(day);
				String dateValueShow = String.valueOf(year) + "/"	+ String.valueOf(month + 1) + "/" + String.valueOf(day);
				if (settings.isLangEng()) {
					dateFromValue = CommonAndroid.convertDate2(getActivity(), dateFromValue, 3);
					dateValueShow = CommonAndroid.convertDate2(getActivity(), dateValueShow, 3);
				}else{
					dateFromValue = CommonAndroid.convertDate2(getActivity(), dateFromValue, 0);
					dateValueShow = CommonAndroid.convertDate2(getActivity(), dateValueShow, 0);
				}
				txtDateFrom.setText(dateValueShow);
				typeSearch = 2;
			} else {
				dateToValue = String.valueOf(year) + "/" + String.valueOf(month + 1) + "/" + String.valueOf(day);
				if (settings.isLangEng()) {
					dateToValue = CommonAndroid.convertDate2(getActivity(), dateToValue, 3);
				}else{
					
					dateToValue = CommonAndroid.convertDate2(getActivity(), dateToValue, 0);
				}
				txtDateTo.setText(dateToValue);
				typeSearch = 2;
			}
		}
	}


	@Override
	public int getLayout() {
		
		return R.layout.mailbox_layout;
	}

	@Override
	public void init(View view) {
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.ico_indi_gray2, getResources().getColor(R.color.home_icon_select));//OK
		parentView = (RelativeLayout)view.findViewById(R.id.mailbox_mail_layout_id);
		GlobalFunction.handlerKeyboardOnView(getActivity(), parentView);
		
		getScreenSize();
		listview = (ListView) view.findViewById(R.id.list_id);
		
		emtyView = (MTextView)view.findViewById(R.id.mailbox_search_emty_id);
		
		settings = new Setting(getActivity());
		loading = CommonAndroid.getView(view, R.id.loading);
		CommonAndroid.showView(false, loading);

		titleFragment = CommonAndroid.getView(view,R.id.header_mailbox_id);
		titleFragment.initHeader(R.string.menu_mailbox, R.string.menu_mailbox_j);

		btnSearch = (ImageButton) view.findViewById(R.id.header_btn_right);
		btnSearch.setImageResource(R.drawable.icon_search);
		btnSearch.setOnClickListener(this);
		btnHome = (ImageButton) view.findViewById(R.id.header_btn_left);
		btnHome.setOnClickListener(this);

		searchView = (LinearLayout) view
				.findViewById(R.id.mailbox_ll_search_id);

		rlDateFrom = (RelativeLayout) view
				.findViewById(R.id.mailbox_rl_search_date_from_id);
		rlDateFrom.setOnClickListener(this);

		rlDateTo = (RelativeLayout) view
				.findViewById(R.id.mailbox_rl_search_date_to_id);
		rlDateTo.setOnClickListener(this);

		txtDateFrom = (MTextView) view
				.findViewById(R.id.mailbox_search_date_from_id);
		txtDateTo = (MTextView) view
				.findViewById(R.id.mailbox_search_date_to_id);

		edtSubject = (MEditText) view
				.findViewById(R.id.mail_box_edtsearch_id);
		txtDoneSearch = (MTextView) view
				.findViewById(R.id.mailbox_search_done_id);
		txtDoneSearch.setOnClickListener(this);
		
		btnClearDate = (MButton)view.findViewById(R.id.mailbox_search_btnclear_date_id);
		btnClearDate.setOnClickListener(this);
		
		imgClearInput = (ImageView)view.findViewById(R.id.mailbox_clear_subject_id);
		imgClearInput.setVisibility(View.INVISIBLE);
		imgClearInput.setOnClickListener(this);
		
		sharePref = getActivity().getSharedPreferences(SkyUtils.shareKey, Context.MODE_PRIVATE);
//		flagSearchlEmail = sharePref.getBoolean("FLAG_SEARCH", false);
		
		edtSubject.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				imgClearInput.setVisibility(View.VISIBLE);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				if (s.length() > 0) {
					imgClearInput.setVisibility(View.VISIBLE);
					subjectValue = edtSubject.getText().toString();
					typeSearch = 1;
					processSearch(1);
					flagOpenDatePicker = false;
				}else{
					imgClearInput.setVisibility(View.INVISIBLE);
				}
				
				
			}
		});

		Log.i(TAG, "Flag search init = " + flagSearchlEmail);
		Log.i(TAG, "Value 1 = " + subjectValue);
		Log.i(TAG, "Value 2 = " + dateFromValue);
		Log.i(TAG, "Value 3 = " + dateToValue);
		if (flagSearchlEmail) {
			addViewSearch();
			showDataSearch();
			if (subjectValue != null) {
				edtSubject.setText(subjectValue);
			}
			if (dateFromValue != null) {
				txtDateFrom.setText(dateFromValue);
			}
			if (dateToValue != null) {
				txtDateTo.setText(dateToValue);
			}
		}else{
			//showDataOld();
			getAllMailBox();	
		}

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (subjectValue == null && dateToValue == null && dateFromValue == null) {
					flagSearchlEmail = false;
				}
				
				EmailDetailFragment emailDetail = new EmailDetailFragment(position,listEmail,flagSearchlEmail);
				emailDetail.logDataSearch(subjectValue, dateFromValue, dateToValue);
				switchFragment(emailDetail, "EMAIL_DETAIL");
			}
		});
	}
	
	public void updateFlagSearch(boolean flag){
		flagSearchlEmail = flag;
		Log.i(TAG, "Flag search = " + flagSearchlEmail);
	}
	
	public void updateDataSearch(String subject,String dateFrom, String dateTo){
		subjectValue = subject;
		dateFromValue = dateFrom;
		dateToValue = dateTo;
	}
	
	private void showDataOld(){
		if (listMailResult != null && listMailResult.size() > 0) {
			sortEmail(listMailResult);
		}
	}
	
	private void showDataSearch(){
		if (listMailResultSearch != null && listMailResultSearch.size() > 0) {
			sortEmail(listMailResultSearch);
		}
	}
	@Override
	public void onChangeLanguage() {
		getAllMailBox();
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
					GcmBroadcastReceiver.notifiServiceUpdate(getActivity(),countNews ,2, true, true);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
		});
	}
}
