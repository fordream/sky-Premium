package z.lib.base;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.SkyPremiumLtd.SkyPremium.R;
import com.app.lazyload.ImageLoader2;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class ImageLoaderUtils {
//	private ImageLoader imageLoader;
	private ImageLoader2 imageLoader;
	public static ImageLoaderUtils instance;
	DisplayImageOptions options;
	public static ImageLoaderUtils getInstance(Context context) {
		return (instance == null ? (instance = new ImageLoaderUtils()) : instance).init(context);
	}

	private ImageLoaderUtils init(Context context) {
		if (imageLoader == null) {
			imageLoader = new ImageLoader2(context);//ImageLoader
		}
		imageLoader.updateContext(context);
		return this;
	}

	private ImageLoaderUtils() {
		
	}

	public void displayImageHome(String url, ImageView imageView) {
		imageView.setImageResource(R.drawable.tranfer);
		if (!CommonAndroid.isBlank(url)) {
			imageLoader.displayImage(url, imageView, true);
			Log.i("vao load image", "");
		}
	}

	public void displayImageHomeSlide(String url, ImageView imageView) {
		imageView.setImageResource(R.drawable.tranfer);
		if (!CommonAndroid.isBlank(url)) {
			imageLoader.displayImage(url, imageView, false);
		}
	}

	public void displayImageEcProduct(String url, ImageView imageView) {
		imageView.setImageResource(R.drawable.tranfer);
		if (!CommonAndroid.isBlank(url)) {
			imageLoader.displayImage(url, imageView, false);
		}
	}
	
	public void displayImageMyCart(String url, ImageView imageView) {
		imageView.setImageResource(R.drawable.tranfer);
		if (!CommonAndroid.isBlank(url)) {
			imageLoader.displayImage(url, imageView, false);
		}
	}

	public void showLogoTinTuc(ImageView tintuc_item_img_icon, String images) {
		tintuc_item_img_icon.setImageResource(R.drawable.tranfer);
		imageLoader.displayImage(images, tintuc_item_img_icon, false);

	}

}