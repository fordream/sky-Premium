package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;

import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.SkyMainActivity;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import z.lib.base.downloadpdf.DownloadHander;
import z.lib.base.downloadpdf.FileDownloadPDFCache;
import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Download;

public class DownloadFragment extends BaseFragment {
	private ListView list;
	private LoadingView loading;

	public DownloadFragment() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.download;
	}

	@Override
	public void onClickHeaderLeft() {
		super.onClickHeaderLeft();
		SkyMainActivity.sm.toggle();
	}

	private void callApi() {
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][id]", "");
		inputs.put("req[param][slug]", "downloads");
		inputs.put("req[param][type]", "page");

		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_SERVICE_LIST, inputs, callback);
	}

	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {

		public void onStart() {
			if (downloadAdapter != null && downloadAdapter.getCount() > 0) {
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
				updateData();
			}
		};
	};

	@Override
	public void init(View view) {
		initHeader(view, R.id.header, R.string.menu_download, R.string.menu_download_j, R.drawable.menu_icon, 0);
		loading = CommonAndroid.getView(view, R.id.loading);
		list = CommonAndroid.getView(view, R.id.list);
		list.setOnItemClickListener(this);
		updateData();
		callApi();
	}

	private void updateData() {

		if (downloadAdapter == null) {
			downloadAdapter = new DownloadAdapter(list, null);
		}
		list.postDelayed(new Runnable() {

			@Override
			public void run() {
				downloadAdapter.getFilter().filter("");
			}
		}, 500);

	}

	@Override
	public void onChangeLanguage() {
		callApi();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		super.onItemClick(arg0, arg1, arg2, arg3);

		Cursor cursor = (Cursor) arg0.getItemAtPosition(arg2);
		String content = CommonAndroid.getString(cursor, Download.content);

		String url = CommonAndroid.getUrlFromContent(content);

		if (FileDownloadPDFCache.getInstance(getActivity()).isDownload(url)) {

		} else if (FileDownloadPDFCache.getInstance(getActivity()).isDownloaded(url)) {
			CommonAndroid.showPDF(getActivity(), url);
		} else {
			FileDownloadPDFCache.getInstance(getActivity()).download(url, downloadHander);
			downloadAdapter.notifyDataSetChanged();
		}
	}

	private DownloadHander downloadHander = new DownloadHander() {

		@Override
		public void onSuccess(String url) {
			if (getActivity() != null) {
				CommonAndroid.showPDF(getActivity(), url);
				downloadAdapter.notifyDataSetChanged();
			}
		}

		@Override
		public void onFail(String url, String message) {
			if (getActivity() != null) {
				CommonAndroid.showDialog(getActivity(), message + "\n" + url, null);
				downloadAdapter.notifyDataSetChanged();
			}
		}
	};

	@Override
	public void reload() {
		super.reload();
	}

	private DownloadAdapter downloadAdapter;

	private class DownloadAdapter extends CursorAdapter {
		private ListView list;

		public DownloadAdapter(ListView list, Cursor c) {
			super(list.getContext(), c);
			this.list = list;
			list.setAdapter(this);
		}

		@Override
		public View newView(Context arg0, Cursor arg1, ViewGroup arg2) {
			return CommonAndroid.initView(arg0, R.layout.download_list_item, null);
		}

		@Override
		public void bindView(View view, Context context, Cursor c) {
			if (view == null) {
				view = CommonAndroid.initView(context, R.layout.download_list_item, null);
			}

			ImageView img = CommonAndroid.getView(view, R.id.img);
			TextView text_1 = CommonAndroid.getView(view, R.id.text_1);
			ProgressBar progress = CommonAndroid.getView(view, R.id.progress);

			String url = CommonAndroid.getUrlFromContent(CommonAndroid.getString(c, Download.content));

			if (FileDownloadPDFCache.getInstance(context).isDownload(url)) {
				progress.setVisibility(View.VISIBLE);
			} else {
				progress.setVisibility(View.GONE);
			}
			CommonAndroid.setText(text_1, c, Download.title);

			ImageLoaderUtils.getInstance(context).displayImageHome(CommonAndroid.getString(c, Download.thumbnail), img);
		}

		@Override
		public Filter getFilter() {

			return new Filter() {

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					changeCursor((Cursor) results.values);
				}

				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					String user_idStr = new Account(getActivity()).getUserId();
					String where = String.format("%s = '%s'", Download.user_id, user_idStr);
					filterResults.values = new Download(getActivity()).querry(where);
					return filterResults;
				}
			};
		}
	}
}