package z.lib.base;

import java.util.List;

import org.com.atmarkcafe.sky.SkypeGaUtils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;

public abstract class BaseActivity extends FragmentActivity {
	private FragmentManager fragmentManager;
	Fragment mContent = null;

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
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		overridePendingTransition(R.anim.right_in, R.anim.a_nothing);
		fragmentManager = getSupportFragmentManager();
		setContentView(getLayout());
	}

	public abstract int getLayout();

	public final void startFragment(BaseFragment baseFragment, Bundle bundle) {
		android.support.v4.app.FragmentTransaction transaction = fragmentManager
				.beginTransaction();
		transaction.setCustomAnimations(R.anim.right_in, R.anim.right_in,
				R.anim.right_out, R.anim.right_out);
		if (bundle != null)
			baseFragment.setArguments(bundle);
		transaction.add(getResMain(), baseFragment,
				"" + System.currentTimeMillis());
		if (!baseFragment.getClass().getSimpleName()
				.equalsIgnoreCase("LoginFragment")) {
			transaction.addToBackStack(null);
		}
		transaction.commit();
	}

	public abstract int getResMain();

	public int getFragemtCount() {
		try {
			List<Fragment> fragments = fragmentManager.getFragments();
			return fragments.size();
		} catch (Exception exception) {
			return 0;
		}
	}

	public void onBackPressed(Bundle extras) {
		try {
			int count = fragmentManager.getBackStackEntryCount();
			List<Fragment> fragments = fragmentManager.getFragments();

			try {
				String extras_value = extras.getString("back_login");
				LogUtils.i("BASE", "extras==" + extras_value);
				if (extras_value.equals("back_login")) {
					new Setting(getApplicationContext()).saveLang(R.id.eng);
					fragmentManager.popBackStack();
				}
			} catch (Exception e) {
			}

			if (count > 1) {
				if (fragments.size() > 1) {
					BaseFragment baseFragment = (BaseFragment) fragments
							.get(fragments.size() - 2);
					if (baseFragment != null) {
						baseFragment.onFragmentBackPress(extras);
						fragmentManager.popBackStack();
					} else {
						super.onBackPressed();
					}
				} else {
					super.onBackPressed();
				}

			} else if (count == 1) {
				for (int i = 0; i < fragments.size(); i++) {
					Log.i("BASE", "FRAG = " + i + " : "
							+ fragments.get(i).getClass().getSimpleName());
					if ("LoginFragment".equalsIgnoreCase(fragments.get(0)
							.getClass().getSimpleName())) {
						new Setting(getApplicationContext()).saveLang(R.id.eng);
						BaseFragment baseFragment = (BaseFragment) fragments
								.get(0);
						baseFragment.onFragmentBackPress(extras);
						fragmentManager.popBackStack();
					} else {
						finish();
					}
				}
				// finish();
			} else {
				super.onBackPressed();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.i("BASE", "FRAG = err");
			fragmentManager.popBackStack();
		}
	}

	@Override
	public void onBackPressed() {
		onBackPressed(null);
	}

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
	}
}