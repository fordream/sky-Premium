package z.lib.base;

import org.com.atmarkcafe.sky.SkypeApplication;
import org.com.atmarkcafe.sky.SkypeGaUtils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Window;

public abstract class LibsBaseAdialog extends Dialog {
	public DialogInterface.OnClickListener clickListener;

	public LibsBaseAdialog(Context context) {
		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
	}

	private Context mContext;

	public Context getmContext() {
		return mContext;
	}

	public LibsBaseAdialog(Context context,
			DialogInterface.OnClickListener clickListener) {
		// super(context);
		super(context, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
		mContext = context;
		this.clickListener = clickListener;
		if (context instanceof Activity)
			SkypeGaUtils.intance().ga(this, (Activity) context);

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setCancelable(false);
		if (getLayout() != 0)
			setContentView(getLayout());

	}

	public abstract int getLayout();

	public void showDialogMessage(String message) {
		CommonAndroid.showDialog(getContext(), message, null);
	}
}