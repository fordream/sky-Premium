package z.lib.base;

import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.SkypeApplication;

import z.lib.base.SkyAnimationUtils.AnimationAction;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;

import com.SkyPremiumLtd.SkyPremium.R;

public abstract class Base2Adialog extends LibsBaseAdialog implements
		AdapterView.OnItemClickListener {
	public int getAnimationOpen() {
		return R.anim.right_in;
	}

	public int getAnimationClose() {
		return R.anim.right_out;
	}

	public void onOpenDialogAnimationSuccess() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public Base2Adialog(Context context) {
		super(context);
	}

	public void openPopActivity(View mMain) {
		if (mMain == null)
			return;
		final AnimationAction animationAction = new AnimationAction() {

			@Override
			public void onAnimationEnd() {
				onOpenDialogAnimationSuccess();
			}
		};
		Animation animation = AnimationUtils.loadAnimation(getContext(),
				getAnimationOpen());
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (animationAction != null) {
					animationAction.onAnimationEnd();
				}
			}
		});
		mMain.startAnimation(animation);
	}

	boolean isRunAiniation = false;

	public void closePopActivity(Context context, View mMain,
			final AnimationAction animationAction) {
		if (!isRunAiniation) {
			isRunAiniation = true;
		} else {
			return;
		}
		Animation animation = AnimationUtils.loadAnimation(context,
				getAnimationClose());
		animation.setFillAfter(false);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (animationAction != null)
					animationAction.onAnimationEnd();
			}
		});
		mMain.startAnimation(animation);
	}

	public Base2Adialog(Context context, OnClickListener clickListener) {
		super(context, clickListener);
	}

	public void gotoEC() {
		LogUtils.e("gotoEC", "getContext==" + getContext());
		if (getContext() instanceof MyCartActivity) {
			((Activity) getContext()).setResult(0);
			((Activity) getContext()).finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}
}