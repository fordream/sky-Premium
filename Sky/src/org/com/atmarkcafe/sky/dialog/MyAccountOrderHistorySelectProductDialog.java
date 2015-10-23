package org.com.atmarkcafe.sky.dialog;

import java.util.ArrayList;
import java.util.List;

import org.com.atmarkcafe.view.HistoryProductSelectItemView;
import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.BaseAdapter;
import z.lib.base.BaseAdialog;
import z.lib.base.BaseItem;
import z.lib.base.BaseView;
import z.lib.base.LogUtils;
import z.lib.base.SkyAnimationUtils.AnimationAction;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.SkyPremiumLtd.SkyPremium.R;

public class MyAccountOrderHistorySelectProductDialog extends BaseAdialog implements android.view.View.OnClickListener {
	protected static final String TAG = "MyAccountOrderHistorySelectProductDialog";
	ListView listview;
	private BaseAdapter baseAdapter;
	JSONArray listproduct;
	public MyAccountOrderHistorySelectProductDialog(Context context, JSONArray listproduct_, OnClickListener clickListener) {
		super(context, clickListener);
		listproduct =listproduct_;
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initHeader();
		listview = (ListView) findViewById(R.id.listview);
		findViewById(R.id.header_btn_left).setOnClickListener(this);
		openPopActivity(findViewById(R.id.dialog_main));
//        String[] recourseList=getActivity().getResources().getStringArray(R.array.CountryCodesNew);
		
        List<BaseItem> baseItems = new ArrayList<BaseItem>();
		baseItems.clear();
		for(int i = 0; i < listproduct.length(); i++){
//			String[] temp =recourseList[i].split(",");
//			JSONObject itemObj = new JSONObject();
		    try
		    {
//		    	itemObj.put("postal_code", temp[0].trim());
//		        itemObj.put("counttry_name", temp[1].trim());
		        baseItems.add(new BaseItem(listproduct.getJSONObject(i)));
		    }catch(Exception e){
		    	LogUtils.e(TAG, "e2" +e.getMessage());
		    }
		    
		}
        
        baseAdapter = new BaseAdapter(getActivity(), new ArrayList<Object>()) {
			@Override
			public BaseView getView(Context context, Object data) {
				final HistoryProductSelectItemView ListItemView = new HistoryProductSelectItemView(context);
				return ListItemView;
			}
		};
		baseAdapter.clear();
		baseAdapter.addAllItems(baseItems);
		baseAdapter.notifyDataSetChanged();
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BaseItem baseItem = (BaseItem) parent.getItemAtPosition(position);
				MyAccountOrderHistoryDetailDialog.id_product = baseItem.getString("id");
				MyAccountOrderHistoryDetailDialog.setSelectProduct(baseItem.getString("name"));
				close(true);
//				LogUtils.e(TAG,ProfileUpdateDialog.country_phone_code);
				
			}
		});
		listview.setAdapter(baseAdapter);
	}

	private void initHeader() {
	}

	private void close(final boolean isOnClick) {
		closePopActivity(getContext(), findViewById(R.id.dialog_main), new AnimationAction() {

			@Override
			public void onAnimationEnd() {
				dismiss();
				if (isOnClick) {
					clickListener.onClick(null, 0);
				}
			}
		});
	}

	@Override
	public int getLayout() {
		return R.layout.v2_history_product_select;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.header_btn_left){
			close(true);
		}
	}

	private Context getActivity() {
		return getContext();
	}

}