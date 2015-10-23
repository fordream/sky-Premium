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

import com.SkyPremiumLtd.SkyPremium.R;

public abstract class BaseAdialog extends LibsBaseAdialog {
	public int getAnimationOpen() {
		return R.anim.bot_to_top;
	}

	public int getAnimationClose() {
		return R.anim.top_to_bot;
	}

	public void onOpenDialogAnimationSuccess() {

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public BaseAdialog(Context context) {
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

	public BaseAdialog(Context context, OnClickListener clickListener) {
		super(context, clickListener);

	}

	public void gotoEC() {
		if (getContext() instanceof MyCartActivity) {
			((Activity) getContext()).setResult(0);
			((Activity) getContext()).finish();
		}
	}
}