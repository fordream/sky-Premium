package z.lib.base;

import android.content.Context;
import android.widget.ImageView;

import com.SkyPremiumLtd.SkyPremium.R;

public class ImageLoaderSquareUtils {
	private ImageSquareLoader imageLoader;
	public static ImageLoaderSquareUtils instance;

	public static ImageLoaderSquareUtils getInstance(Context context) {
		return (instance == null ? (instance = new ImageLoaderSquareUtils()) : instance).init(context);
	}

	private ImageLoaderSquareUtils init(Context context) {
		if (imageLoader == null) {
			imageLoader = new ImageSquareLoader(context);
		}
		imageLoader.updateContext(context);
		return this;
	}

	private ImageLoaderSquareUtils() {

	}

	public void displayImageHome(String url, ImageView imageView) {
		imageView.setImageResource(R.drawable.tranfer);
		if (!CommonAndroid.isBlank(url)) {
			imageLoader.displayImage(url, imageView, false);
		}
	}

	public void displayImageHomeSlide(String url, ImageView imageView) {
		imageView.setImageResource(R.drawable.tranfer);
		if (!CommonAndroid.isBlank(url)) {
			imageLoader.displayImage(url, imageView, false);
		}
	}

	public void showLogoTinTuc(ImageView imageView, String images) {
		imageView.setImageResource(R.drawable.tranfer);
		imageLoader.displayImage(images, imageView, false);
	}
	
	public void displayImageAbout(String url, ImageView imageView) {
		imageView.setImageResource(R.drawable.tranfer);
		if (!CommonAndroid.isBlank(url)) {
			imageLoader.displayImageScale(url, imageView, true);
		}
	}
	
	public void displayImageGallery(String url, ImageView imageView) {
		imageView.setImageResource(R.drawable.tranfer);
		if (!CommonAndroid.isBlank(url)) {
			imageLoader.displayImageScale(url, imageView, true);
		}
	}
	
//	public void displayImageWithScale(int ){
//		
//	}
}