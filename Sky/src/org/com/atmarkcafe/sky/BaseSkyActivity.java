package org.com.atmarkcafe.sky;

import java.util.List;

import org.com.atmarkcafe.sky.fragment.HomeFragment;
import org.com.atmarkcafe.sky.fragment.ServiceListFragment;

import z.lib.base.BaseFragment;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.util.Log;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

public class BaseSkyActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	protected ListFragment mFrag;
	private FragmentManager fragmentManager;
	Fragment mContent = null;

	public BaseSkyActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@Override
	protected void onStart() {
		super.onStart();
		SkypeGaUtils.intance().onStart(this);
	}

	@Override
	protected void onStop() {
		super.onStop();
		SkypeGaUtils.intance().onStop(this);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragmentManager = getSupportFragmentManager();
		// setTitle(mTitleRes);

		// set the Behind View
		// setBehindContentView(R.layout.menu_frame);
		// if (savedInstanceState == null) {
		// FragmentTransaction t = this.getSupportFragmentManager()
		// .beginTransaction();
		// mFrag = new MenuListFragment();
		// t.replace(R.id.menu_frame, mFrag);
		// t.commit();
		// } else {
		// mFrag = (ListFragment) this.getSupportFragmentManager()
		// .findFragmentById(R.id.menu_frame);
		// }

		// customize the SlidingMenu
		// SlidingMenu sm = getSlidingMenu();
		// sm.setShadowWidthRes(R.dimen.shadow_width);
		// sm.setShadowDrawable(R.drawable.shadow);
		// sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		// sm.setFadeDegree(0.35f);
		// sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		// actionbar
		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// ActionBar actionBar = getSupportActionBar();
		// actionBar.setDisplayShowCustomEnabled(true);
		// View headerView = (View) getLayoutInflater().inflate(R.layout.header,
		// null);
		// ActionBar.LayoutParams params = new
		// ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
		// ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER);
		// actionBar.setCustomView(headerView, params);
		// actionBar.setDisplayHomeAsUpEnabled(false);
		// actionBar.setDisplayHomeAsUpEnabled(false);
		// actionBar.setHomeButtonEnabled(false);
		// actionBar.setDisplayUseLogoEnabled(false);
		// actionBar.setDisplayShowTitleEnabled(false);

	}

	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case android.R.id.home:
	// toggle();
	// return true;
	// }
	// return super.onOptionsItemSelected(item);
	// }

	/*
	 * @Override public void onBackPressed() { onBackPressed(null); }
	 * 
	 * 
	 * int countBack = 0; public void onBackPressed(Bundle extras) { try {
	 * List<Fragment> fragments = fragmentManager.getFragments();
	 * LogUtils.i("1xxxxxx", "1xxxxxxxxxxxxx Slug get count back= " +
	 * countBack); if (countBack == 0) { fragmentManager.popBackStack();
	 * countBack ++; } int count = fragments.size();
	 * LogUtils.i("1xxxxxx"," count = " + count); if (count > 1) { if
	 * (fragments.size() > 1) {
	 * LogUtils.i("1xxxxxx"," count 2 ="+fragments.size()); BaseFragment
	 * baseFragment = (BaseFragment) fragments.get(fragments.size() - 2);
	 * if(baseFragment != null){ baseFragment.onFragmentBackPress(extras);
	 * fragmentManager.popBackStack(); }else{ fragmentManager.popBackStack(); //
	 * super.onBackPressed(); } }else{ //super.onBackPressed(); }
	 * 
	 * } else if (count == 1) {
	 * 
	 * for (int i = 0; i < fragments.size(); i++) { Log.i("BASE", "FRAG = " +i +
	 * " : "+ fragments.get(i).getClass().getSimpleName()); if
	 * ("LoginFragment".equalsIgnoreCase
	 * (fragments.get(0).getClass().getSimpleName()) ) { new
	 * Setting(getApplicationContext()).saveLang(R.id.eng); BaseFragment
	 * baseFragment = (BaseFragment) fragments.get(0);
	 * baseFragment.onFragmentBackPress(extras); fragmentManager.popBackStack();
	 * 
	 * }else{ finish(); } } //finish(); } else { fragmentManager.popBackStack();
	 * // super.onBackPressed(); } } catch (Exception e) { // TODO
	 * Auto-generated catch block Log.i("BASE", "FRAG = err");
	 * fragmentManager.popBackStack(); } }
	 * 
	 * public int getFragemtCount() { try { List<Fragment> fragments =
	 * fragmentManager.getFragments(); return fragments.size(); } catch
	 * (Exception exception) { return 0; } }
	 */

	@Override
	public void onBackPressed() {
		try {
			if (HomeFragment.listSlugObject.size() > 0) {
				ServiceListFragment.goback();
			}
		} catch (Exception e) {
			super.onBackPressed();
		}

		return;
	}
}
