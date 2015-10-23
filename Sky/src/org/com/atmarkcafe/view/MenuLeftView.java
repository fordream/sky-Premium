package org.com.atmarkcafe.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.SkyPremiumLtd.SkyPremium.R;

//com.acv.cheerz.view.MenuLeftView
public class MenuLeftView extends LinearLayout implements View.OnClickListener {
	private ListView menuleftlist;
	private MenuItemView logout;
	private List<ItemMenu> listMenu = new ArrayList<MenuLeftView.ItemMenu>();

	public MenuLeftView(Context context) {
		super(context);
		init();
	}

	public MenuLeftView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		((LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.menu_left,this);
		menuleftlist = (ListView) findViewById(R.id.menuleftlist);
	}

	@Override
	public void onClick(View v) {
		if (v == logout) {
			iMenuLeftOnClick.onClickItem(R.string.account);
		}
	}

	private CustomImageView icon;
	private Integer[] resStr = new Integer[] {};
	private Integer[] resIconStr = new Integer[] {};

	public void initData() {

		resStr = new Integer[] {//
				R.string.menu_mailbox,
				R.string.menu_faq,
				R.string.menu_download,
				R.string.menu_contact_us,
				R.string.menu_news,
				R.string.menu_about_spmc,
				R.string.menu_invite_friend,
				R.string.menu_service_list,
				R.string.spmc_selection,
				R.string.menu_my_profile,
				R.string.menu_logout,
				R.string.menu_lang_japanese,
				R.string.menu_lang_english
				
		};//

		resIconStr = new Integer[] {//
				R.drawable.ico_mail_m,//
				R.drawable.ico_fag,//
				R.drawable.ico_dowload,//
				R.drawable.ico_contact,//
				R.drawable.ico_news,//
				R.drawable.ico_info, 
				R.drawable.ico_invite,
				R.drawable.ico_invite,
				R.drawable.ico_invite,
				R.drawable.ico_invite,
				R.drawable.ico_invite,
				R.drawable.ico_invite,
				R.drawable.ico_invite

		};//
		
		for (int i = 0; i < 13; i++) {
//			ItemMenu item = new ItemMenu(i % 10 == 0 ? ItemMenu.SECTION : ItemMenu.ITEM);
//			item.setIconId(resIconStr[i % resIconStr.length]);
//			item.setTitle(getResources().getString(resStr[i % resStr.length]));
//			listMenu.add(item);
			ItemMenu item  = new ItemMenu();
			if (i == 0 || i == 8) {
				item = new ItemMenu(ItemMenu.SECTION);	
			}else{
				item = new ItemMenu(ItemMenu.ITEM);
			}
			item.setIconId(resIconStr[i]);
			item.setTitle(getResources().getString(resStr[i]));
			listMenu.add(item);
		}
		
		MenuAdapter adapter = new MenuAdapter(getContext(), listMenu);
		menuleftlist.setAdapter(adapter);
	}

	static class MenuAdapter extends ArrayAdapter<ItemMenu>implements
			org.com.atmarkcafe.view.PinnedSectionListView.PinnedSectionListAdapter {

		List<ItemMenu> listData;
		Context ctx;

		public MenuAdapter(Context context, List<ItemMenu> listItem) {
			super(context, 0, listItem);
			this.ctx = context;
			this.listData = listItem;

			generateDataset('A', 'Z', false);
		}

		protected void prepareSections(int sectionsNumber) {
		}

		protected void onSectionAdded(Item section, int sectionPosition) {
		}

		public void generateDataset(char from, char to, boolean clear) {

			
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View itemView = convertView;
			ViewHolder holder = null;
			if (itemView == null) {
				itemView = ((LayoutInflater) ctx
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
						.inflate(R.layout.item_menu, null);
				holder = new ViewHolder();
				holder.imgIcon = (ImageView) itemView.findViewById(R.id.menu_item_icon_id);
				holder.txtTitle = (TextView) itemView.findViewById(R.id.menu_item_title_id);
				itemView.setTag(holder);
			} else {
				holder = (ViewHolder) itemView.getTag();
			}
			
			ItemMenu item = getItem(position);
			holder.imgIcon.setImageResource(item.getIconId());
			holder.txtTitle.setText(item.getTitle());
			

			if (item.type == ItemMenu.SECTION) {
				holder.txtTitle.setText("header");
			}
			
			itemView.setBackgroundColor((item.type == ItemMenu.SECTION) ? Color.RED	: Color.TRANSPARENT);
			return itemView;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position).type;
		}

		@Override
		public boolean isItemViewTypePinned(int viewType) {
			return viewType == Item.SECTION;
		}
	}

	static class ViewHolder {
		private ImageView imgIcon;
		private TextView txtTitle;
	}

	static class ItemMenu {
		public ItemMenu() {
		}
		
		ItemMenu(int type) {
			this.type = type;
		}

		public static final int ITEM = 0;
		public static final int SECTION = 1;

		public int type;
		public String text;

		public int sectionPosition;
		public int listPosition;

		private String title;
		private int iconId;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public int getIconId() {
			return iconId;
		}

		public void setIconId(int iconId) {
			this.iconId = iconId;
		}

	}

	static class SimpleAdapter extends ArrayAdapter<Item>
			implements
			org.com.atmarkcafe.view.PinnedSectionListView.PinnedSectionListAdapter {

		private static final int[] COLORS = new int[] { R.color.green_light,
				R.color.orange_light, R.color.blue_light, R.color.red_light };

		public SimpleAdapter(Context context, int resource,
				int textViewResourceId) {
			super(context, resource, textViewResourceId);
			generateDataset('A', 'Z', false);
		}

		public void generateDataset(char from, char to, boolean clear) {

			if (clear)
				clear();

			final int sectionsNumber = to - from + 1;
			prepareSections(sectionsNumber);

			int sectionPosition = 0, listPosition = 0;
			for (char i = 0; i < sectionsNumber; i++) {
				Item section = new Item(Item.SECTION,
						String.valueOf((char) ('A' + i)));
				section.sectionPosition = sectionPosition;
				section.listPosition = listPosition++;
				onSectionAdded(section, sectionPosition);
				add(section);

				final int itemsNumber = (int) Math.abs((Math.cos(2f * Math.PI
						/ 3f * sectionsNumber / (i + 1f)) * 25f));
				for (int j = 0; j < itemsNumber; j++) {
					Item item = new Item(Item.ITEM,
							section.text.toUpperCase(Locale.ENGLISH) + " - "
									+ j);
					item.sectionPosition = sectionPosition;
					item.listPosition = listPosition++;
					add(item);
				}

				sectionPosition++;
			}
		}

		protected void prepareSections(int sectionsNumber) {
		}

		protected void onSectionAdded(Item section, int sectionPosition) {
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = (TextView) super.getView(position, convertView,
					parent);
			view.setTextColor(Color.DKGRAY);
			view.setTag("" + position);
			Item item = getItem(position);
			if (item.type == Item.SECTION) {
				// view.setOnClickListener(PinnedSectionListActivity.this);
				view.setBackgroundColor(parent.getResources().getColor(
						COLORS[item.sectionPosition % COLORS.length]));
			}
			return view;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			return getItem(position).type;
		}

		public boolean isItemViewTypePinned(int viewType) {
			return viewType == Item.SECTION;
		}

	}

	static class Item {

		public static final int ITEM = 0;
		public static final int SECTION = 1;

		public final int type;
		public final String text;

		public int sectionPosition;
		public int listPosition;

		public Item(int type, String text) {
			this.type = type;
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}

	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		menuleftlist.setOnItemClickListener(onItemClickListener);
	}

	private IMenuLeftOnClick iMenuLeftOnClick;

	public void setiMenuLeftOnClick(IMenuLeftOnClick iMenuLeftOnClick) {
		this.iMenuLeftOnClick = iMenuLeftOnClick;
	}

	public interface IMenuLeftOnClick {
		public void onClickItem(int resId);
	}
}