package org.com.atmarkcafe.sky.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.service.VideoActivity;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.view.HeaderView;
import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.ImageLoaderSquareUtils;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
//import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.SkyPremiumLtd.SkyPremium.R;

@SuppressLint("ValidFragment")
public class GalleryDetailFragment extends BaseFragment implements OnClickListener{

	private String TAG = getClass().getSimpleName();
	private String mTitle,mUrlThumb,mUrlVideo, content, idVideo;
	private ImageButton btnBack,btnRight;
	private HeaderView txtTitleView;
	private ImageView imgThumb;
	private Button btnPlay;
//	private VideoView videoView;
	ProgressDialog pDialog;
	RelativeLayout rlBanner;
//	private Cursor cursor;
//	private LoadingView loading;
	private VideoView videoView;
	
	public GalleryDetailFragment() {
		super();
	}
	 
	private List<ItemGallery> listItem = new ArrayList<GalleryDetailFragment.ItemGallery>();
	public GalleryDetailFragment(String title,String urlThumb,String urlVideo) {
		this.mTitle = title;
		this.mUrlThumb = urlThumb;
		this.mUrlVideo = urlVideo;
	}
	
	public GalleryDetailFragment(Cursor mCursor) {
//		this.cursor = mCursor;
		mTitle = CommonAndroid.getString(mCursor, com.acv.cheerz.db.MainGallery.title);
		mUrlThumb = CommonAndroid.getString(mCursor, com.acv.cheerz.db.MainGallery.thumbnail);
		mUrlVideo = CommonAndroid.getString(mCursor, com.acv.cheerz.db.MainGallery.video_src);
		content = CommonAndroid.getString(mCursor, com.acv.cheerz.db.MainGallery.content);
		idVideo = CommonAndroid.getString(mCursor, com.acv.cheerz.db.MainGallery.id);
		
	}
	
	@Override
	public int getLayout() {
		return R.layout.gallery_detail_layout;
	}

	@Override
	public void init(View view) {
		//cursor = GalleryFragment.cursordetail;
		btnBack = (ImageButton)view.findViewById(R.id.header_btn_left);
		btnBack.setImageResource(R.drawable.icon_back);
		btnBack.setOnClickListener(this);
		
		btnRight = (ImageButton)view.findViewById(R.id.header_btn_right);
		btnRight.setVisibility(View.INVISIBLE);
		
		txtTitleView = (HeaderView)view.findViewById(R.id.gallery_detail_header_id);
//		mTitle = CommonAndroid.getString(cursor, com.acv.cheerz.db.MainGallery.title);
		txtTitleView.initHeader(mTitle, mTitle);
		rlBanner = (RelativeLayout)view.findViewById(R.id.ll_id);
		imgThumb = (ImageView)view.findViewById(R.id.gallery_img_thumb_id);
//		mUrlThumb = CommonAndroid.getString(cursor, com.acv.cheerz.db.MainGallery.thumbnail);
//		mUrlVideo = CommonAndroid.getString(cursor, com.acv.cheerz.db.MainGallery.video_src);
//		content = CommonAndroid.getString(cursor, com.acv.cheerz.db.MainGallery.content);
//		
		LogUtils.i(TAG,"mUrlVideo=="+ mUrlVideo + "::content==" + content);
//		ImageLoaderSquareUtils.getInstance(getActivity()).displayImageHomeSlide(mUrlThumb, mUrlThumb);
		ImageLoaderSquareUtils.getInstance(getActivity()).displayImageGallery(mUrlThumb, imgThumb);
		Gallery.LayoutParams layoutParams = new Gallery.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		view.setLayoutParams(layoutParams);
		
		btnPlay = (Button)view.findViewById(R.id.gallery_btn_play_id);
		btnPlay.setOnClickListener(this);
//		videoView = (VideoView)view.findViewById(R.id.gallery_detail_videoview_id);
	}

	@Override
	public void onChangeLanguage() {
		txtTitleView.setTextHeader(mTitle);
		callApi();
		
		
	}

	@Override
	public void onClick(View v) {
		int idView = v.getId();
		switch (idView) {
		case R.id.header_btn_left:
			GalleryFragment gl = new GalleryFragment();
			if (getActivity() instanceof SkyMainActivity) {
				SkyMainActivity mainActivity = (SkyMainActivity)getActivity();
				mainActivity.switchContent(gl, "");
			}
			break;
		case R.id.gallery_btn_play_id:
			openPlayerDefault();
//			playVideo(mUrlVideo);
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	private void openPlayerDefault(){
		if(!"".equals(mUrlVideo) && mUrlVideo.endsWith(".mp4")){
			String extension = MimeTypeMap.getFileExtensionFromUrl(mUrlVideo);
			String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
			Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
			mediaIntent.setDataAndType(Uri.parse(mUrlVideo), mimeType);
			LogUtils.i(TAG, "Url-Video : " + mUrlVideo);
			getActivity().startActivity(mediaIntent);
		}
		else if(!"".equals(content)){
			LogUtils.i(TAG, "Url-content : " + mUrlVideo);
			Intent intent = new Intent(getActivity(), VideoActivity.class);
	       	intent.putExtra("contentview", content);
	       	intent.putExtra("contentview_url", mUrlVideo);
	       	intent.putExtra("gallery_detail", "1");
	       	getActivity().startActivityForResult(intent, 0);
		}
		
	}
	
	private void playVideo(String urlVideo){
		
		videoView.setMediaController(new MediaController(getActivity()));       
		videoView.setVideoURI(Uri.parse(urlVideo));
		videoView.requestFocus();
		videoView.start();
	}
	
	

	private void callApi() {
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][id]", "");
		inputs.put("req[param][slug]", "gallery");
		inputs.put("req[param][type]", "page"); 

		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_SERVICE_LIST, inputs, callback);
	}

	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {

		

		public void onStart() {
		};

		public void onError(String message) {
		};

		public void onSucces(String response) {
			try {
				JSONObject jsonRes = new JSONObject(response);
				JSONArray jsonArr = jsonRes.getJSONArray("data");
				for (int i = 0; i < jsonArr.length(); i++) {
					JSONObject jObj = jsonArr.getJSONObject(i);
					ItemGallery item = new ItemGallery();
					item.setId(jObj.getString("id"));
					item.setTitle(jObj.getString("title"));
					item.setThumbnail(jObj.getString("thumbnail"));
					item.setVideo_src(jObj.getString("video_src"));
					listItem.add(item);
				}
				String id = idVideo;// CommonAndroid.getString(cursor, com.acv.cheerz.db.MainGallery.id);
				for (int i = 0; i < listItem.size(); i++) {
					if (id.equalsIgnoreCase(listItem.get(i).getId())) {
						txtTitleView.setTextHeader(listItem.get(i).getTitle());
						mUrlThumb = listItem.get(i).getThumbnail();
						mUrlVideo = listItem.get(i).getVideo_src();
						LogUtils.i(TAG, "change language 1" + listItem.get(i).getTitle());
						LogUtils.i(TAG, "change language 2" + mUrlThumb);
						LogUtils.i(TAG, "change language 3" + mUrlVideo);
//						ImageLoaderSquareUtils.getInstance(getActivity()).displayImageHomeSlide(mUrlThumb, imgThumb);
						ImageLoaderSquareUtils.getInstance(getActivity()).displayImageGallery(mUrlThumb, imgThumb);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		};
	};
	
	class ItemGallery{
		
		private String id;
		private String title;
		private String subtitle;
		private String content;
		private String slug;
		private String url;
		private String thumbnail;
		private String download_url;
		private String external_url;
		private String is_platinum_accessed;
		private String platinum_accessed_msg;
		private String type;
		private String video_src;
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getTitle() {
			return title;
		}
		public void setTitle(String title) {
			this.title = title;
		}
		public String getSubtitle() {
			return subtitle;
		}
		public void setSubtitle(String subtitle) {
			this.subtitle = subtitle;
		}
		public String getContent() {
			return content;
		}
		public void setContent(String content) {
			this.content = content;
		}
		public String getSlug() {
			return slug;
		}
		public void setSlug(String slug) {
			this.slug = slug;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getThumbnail() {
			return thumbnail;
		}
		public void setThumbnail(String thumbnail) {
			this.thumbnail = thumbnail;
		}
		public String getDownload_url() {
			return download_url;
		}
		public void setDownload_url(String download_url) {
			this.download_url = download_url;
		}
		public String getExternal_url() {
			return external_url;
		}
		public void setExternal_url(String external_url) {
			this.external_url = external_url;
		}
		public String getIs_platinum_accessed() {
			return is_platinum_accessed;
		}
		public void setIs_platinum_accessed(String is_platinum_accessed) {
			this.is_platinum_accessed = is_platinum_accessed;
		}
		public String getPlatinum_accessed_msg() {
			return platinum_accessed_msg;
		}
		public void setPlatinum_accessed_msg(String platinum_accessed_msg) {
			this.platinum_accessed_msg = platinum_accessed_msg;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getVideo_src() {
			return video_src;
		}
		public void setVideo_src(String video_src) {
			this.video_src = video_src;
		}
	}
}
