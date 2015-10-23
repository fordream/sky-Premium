package org.com.atmarkcafe.sky.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.customviews.charting.MTextView;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

public class ContactListFragment extends BaseFragment implements OnClickListener{
	
	private ImageButton btnSMS;
	private HeaderView txtTitleHeader;
	private Setting setting;
	private boolean languageId;
	private ImageButton btnMenu;
	public ListView listview;
	private String TAG = "ContactListFragment";
	private List<ContactListObject> listData;
	private LinearLayout llContactUs;
	private LoadingView loading;
	
	public ContactListFragment() {
	
	}

	@Override
	public int getLayout() {
		Log.i("ContactHistory ", "ok");
		return R.layout.contact_list_layout;
	}

	@Override
	public void init(View view) {
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_infor_green, getResources().getColor(R.color.contact_icon));//OK
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_contact_green, getResources().getColor(R.color.contact_icon));//OK
		btnSMS = (ImageButton)view.findViewById(R.id.header_btn_right);
		btnSMS.setVisibility(View.INVISIBLE);
		loading = (LoadingView)view.findViewById(R.id.loading);
		CommonAndroid.showView(false, loading);
		
		
		txtTitleHeader = CommonAndroid.getView(view, R.id.contactlist_header_id);
		setting = new Setting(getActivity());
		txtTitleHeader.initHeader(R.string.contact_history_list, R.string.contact_history_list_j);
		
		languageId = setting.isLangEng();
		btnMenu = (ImageButton)view.findViewById(R.id.header_btn_left);
		btnMenu.setOnClickListener(this);
		getListContact();
		listview = (ListView)view.findViewById(R.id.contactlist_listview_id);
		
		llContactUs = (LinearLayout)view.findViewById(R.id.contactlist_ll_contact_id);
		llContactUs.setOnClickListener(this);
		
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				ContactInboxFragment contactInboxFragment = new ContactInboxFragment(listData.get(position));
				switchFragment(contactInboxFragment);
			}
		});
	}

	@Override
	public void onChangeLanguage() {
		getListContact();
	}

	@Override
	public void onClick(View v) {
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			SkyMainActivity.sm.toggle();
			break;
		case R.id.contactlist_ll_contact_id:
			ContactFragment contactFragment = new ContactFragment();
			switchFragment(contactFragment);
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	protected void switchFragment(Fragment newFragment) {
		if (getActivity() instanceof SkyMainActivity) {
			SkyMainActivity fca = (SkyMainActivity) getActivity();
			fca.switchContent(newFragment, "");
		}
	}
	
	private void getListContact(){
		listData = new ArrayList<ContactListFragment.ContactListObject>();
		HashMap<String, String> params = new HashMap<String, String>();
		SkyUtils.execute(getActivity(), RequestMethod.GET, SkyUtils.API.API_USER_CONTACT, params, new CheerzServiceCallBack(){
			
			@Override
			public void onStart() {
				CommonAndroid.showView(true, loading);
				super.onStart();
			}
			
			@Override
			public void onError(String message) {
				CommonAndroid.showView(false, loading);
				super.onError(message);
			}
			
			@Override
			public void onSucces(String response) {
				Log.i(TAG, response);
				CommonAndroid.showView(false, loading);
				try {
					JSONObject jsonObject = new JSONObject(response);
					JSONArray jsonArray = jsonObject.getJSONArray("data");
					for (int i = 0; i < jsonArray.length(); i++) {
						JSONObject jobj = jsonArray.getJSONObject(i);
						ContactListObject obj = new ContactListObject();
						obj.setTitle(jobj.getString("title"));
						String time = jobj.getString("sent_time");
						time = time.replace("-", "/");
						if (setting.isLangEng()) {
							time = CommonAndroid.convertDate2(getActivity(), time, 3);
						}
						obj.setTime(time);
						obj.setContactId(jobj.getString("contact_id"));
						obj.setContactType(jobj.getString("contact_type"));
						obj.setContent(jobj.getString("content"));
						obj.setStatusOpen(jobj.getString("status"));
						listData.add(obj);
					}
					ContactListAdapter adapter = new ContactListAdapter(getActivity(), listData);
					listview.setAdapter(adapter);
				} catch (Exception e) {
					e.printStackTrace();
				}
				super.onSucces(response);
			}
		});
	}
	
	class ContactListObject{
		private String title;
		private String time;
		private String contactId;
		private String contactType;
		private String content;
		private String statusOpen;
		
		public String getTitle() {
			return title;
		}
		
		public void setTitle(String title) {
			this.title = title;
		}
		
		public String getTime() {
			return time;
		}
		
		public void setTime(String time) {
			this.time = time;
		}

		public String getContactId() {
			return contactId;
		}

		public void setContactId(String contactId) {
			this.contactId = contactId;
		}

		public String getContactType() {
			return contactType;
		}

		public void setContactType(String contactType) {
			this.contactType = contactType;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getStatusOpen() {
			return statusOpen;
		}

		public void setStatusOpen(String statusOpen) {
			this.statusOpen = statusOpen;
		}
		
	}
	
	class HolderView{
		TextView txtTitle;
		TextView txtTime;
		ImageView imgIcon;
	}
	
	class ContactListAdapter extends android.widget.BaseAdapter{
		private List<ContactListObject> listData;
		private Context context;
		
		public ContactListAdapter(Context ctx,List<ContactListObject> lData) {
			this.context = ctx;
			this.listData = lData;
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

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				rowView = inf.inflate(R.layout.row_contact_list, null);
				HolderView holder = new HolderView();
				holder.txtTitle = (TextView)rowView.findViewById(R.id.row_contactlist_title_id);
				holder.txtTime = (TextView)rowView.findViewById(R.id.row_contactlist_time_id);
				holder.imgIcon = (ImageView)rowView.findViewById(R.id.row_contact_img_id);
				rowView.setTag(holder);
			}
			HolderView holder = (HolderView)rowView.getTag();
//			holder.txtTitle.setText(listData.get(position).getTitle());
//			holder.txtTime.setText(listData.get(position).getTime());
			if (listData.get(position).getStatusOpen().equalsIgnoreCase("2")) {
				holder.imgIcon.setVisibility(View.VISIBLE);
			}else{
				holder.imgIcon.setVisibility(View.INVISIBLE);
			}
			((TextView) CommonAndroid.getView(rowView, R.id.row_contactlist_title_id)).setText(listData.get(position).getTitle());
			((TextView) CommonAndroid.getView(rowView, R.id.row_contactlist_time_id)).setText(listData.get(position).getTime());
			return rowView;
		}
		
	}
}
