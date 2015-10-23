package z.lib.base;

import org.com.atmarkcafe.sky.MyCartActivity;
import org.com.atmarkcafe.sky.RootActivity;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.SkypeApplication;
import org.com.atmarkcafe.sky.SkypeGaUtils;

import z.lib.base.SkyUtils.SCREEN;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;

public abstract class BaseFragment extends Fragment implements
		View.OnClickListener, AdapterView.OnItemClickListener {

	public BaseFragment() {
		super();
	}

	private OnChangLanguage changLanguage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		changLanguage = new OnChangLanguage(this);

	}

	@Override
	public void onResume() {
		super.onResume();
		SkypeGaUtils.intance().ga(this, getActivity());
		Setting.registerOnChangLanguage(changLanguage);
		// ((SkyMainActivity)getActivity()).registerReceiver(receiver,
		// intentFilter);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Setting.registerOnChangLanguage(changLanguage);
	}

	@Override
	public void onClick(View v) {

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public final View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(getLayout(), null);
		view.setOnClickListener(null);
		init(view);

		return view;
	}

	public abstract int getLayout();

	public abstract void init(View view);

	public boolean onBackPressed(Bundle extras) {
		if (getActivity() instanceof BaseActivity) {
			((BaseActivity) getActivity()).onBackPressed(extras);
		}
		// if (getActivity() instanceof BaseSkyActivity) {
		// ((BaseSkyActivity) getActivity()).onBackPressed(extras);
		// }

		return true;
	}

	public final void startFragment(BaseFragment baseFragment, Bundle extras) {
		if (getActivity() instanceof BaseActivity) {
			((BaseActivity) getActivity()).startFragment(baseFragment, extras);
		}

		if (getActivity() instanceof SkyMainActivity) {
			((SkyMainActivity) getActivity()).startFragment(baseFragment,
					extras);
		}
	}

	public void onFragmentBackPress(Bundle extras) {

	}

	public abstract void onChangeLanguage();

	public boolean isFinish() {
		if (getActivity() == null) {
			return true;
		}

		return getActivity().isFinishing();
	}

	public void finish() {
		if (getActivity() != null) {
			getActivity().finish();
		}
	}

	public void reload() {

	}

	/**
	 * 
	 * @param parent
	 * @param res
	 *            id của header
	 * @param resEng
	 *            title tiếng anh của header
	 * @param resJa
	 *            title tiếng nhật của header
	 * @param resLeft
	 *            image của menu left, nếu =0 thì không hiển thị
	 * @param resRight
	 *            iamge của menu right nếu =0 thì không hiển thị
	 */
	public void initHeader(View parent, int res, int resEng, int resJa,
			int resLeft, int resRight) {
		org.com.atmarkcafe.view.HeaderView header = CommonAndroid.getView(
				parent, R.id.header);
		header.initHeader(resEng, resJa);

		View.OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.header_btn_left) {
					onClickHeaderLeft();
				} else if (v.getId() == R.id.header_btn_right) {
					onClickHeaderRight();
				}
			}

		};

		ImageButton header_btn_right = CommonAndroid.getView(header,
				R.id.header_btn_right);
		ImageButton header_btn_left = CommonAndroid.getView(header,
				R.id.header_btn_left);

		header_btn_right.setOnClickListener(clickListener);
		header_btn_left.setOnClickListener(clickListener);

		if (resLeft == 0) {
			header_btn_left.setVisibility(View.GONE);
		} else {
			header_btn_left.setImageResource(resLeft);
		}

		if (resRight == 0) {
			header_btn_right.setVisibility(View.GONE);
		} else {
			header_btn_right.setImageResource(resRight);
		}
	}

	public void initHeader(View parent, int res, String resEng, String resJa,
			int resLeft, int resRight) {
		org.com.atmarkcafe.view.HeaderView header = CommonAndroid.getView(
				parent, R.id.header);
		header.initHeader(resEng, resJa);

		View.OnClickListener clickListener = new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.getId() == R.id.header_btn_left) {
					onClickHeaderLeft();
				} else if (v.getId() == R.id.header_btn_right) {
					onClickHeaderRight();
				}
			}

		};

		ImageButton header_btn_right = CommonAndroid.getView(header,
				R.id.header_btn_right);
		ImageButton header_btn_left = CommonAndroid.getView(header,
				R.id.header_btn_left);

		header_btn_right.setOnClickListener(clickListener);
		header_btn_left.setOnClickListener(clickListener);

		if (resLeft == 0) {
			header_btn_left.setVisibility(View.GONE);
		} else {
			header_btn_left.setImageResource(resLeft);
		}

		if (resRight == 0) {
			header_btn_right.setVisibility(View.GONE);
		} else {
			header_btn_right.setImageResource(resRight);
		}
	}

	public void startActivity(SCREEN screen, Bundle extras) {
		if (extras == null) {
			extras = new Bundle();
		}
		extras.putSerializable(SkyUtils.KEY.KEY_SCREEN, screen);
		extras.putBoolean(SkyUtils.KEY.KEY_SCREEN_TAG, true);
		Intent intent = new Intent(getActivity(), RootActivity.class);
		intent.putExtras(extras);

		getActivity().startActivity(intent);
		getActivity().overridePendingTransition(R.anim.right_in, 0);
	}

	public void startActivityForResult(SCREEN screen, Bundle extras) {
		if (extras == null) {
			extras = new Bundle();
		}
		extras.putSerializable(SkyUtils.KEY.KEY_SCREEN, screen);
		extras.putBoolean(SkyUtils.KEY.KEY_SCREEN_TAG, true);
		Intent intent = new Intent(getActivity(), RootActivity.class);//
		intent.putExtras(extras);

		getActivity().startActivityForResult(intent, 0);
		getActivity().overridePendingTransition(R.anim.right_in, 0);
	}

	public void onClickHeaderRight() {

	}

	public void onClickHeaderLeft() {

	}

	public void loadSkypeLoader(SkyLoader skypeLoader, long time) {
		skypeLoader.executeLoader(time);
	}

	public void switchFragment(Fragment fragment, String nameFragment) {
		if (getActivity() == null)
			return;

		if (getActivity() instanceof SkyMainActivity) {
			SkyMainActivity fca = (SkyMainActivity) getActivity();
			fca.switchContent(fragment, nameFragment);
		}

	}

	public void swithchFragment(Fragment frgment, SCREEN screen) {
		Bundle extras = new Bundle();
		startActivity(screen, extras);
	}

	public void onBackPressed() {
		// TODO Auto-generated method stub
	}

	public void gotoEC() {
		goTOEC2();
		if (getActivity() instanceof MyCartActivity) {
			((Activity) getActivity()).setResult(0);
			((Activity) getActivity()).finish();
		} /*
		 * else if (getActivity() instanceof BaseActivity) { ((BaseActivity)
		 * getActivity()).setResult(0); ((BaseActivity) getActivity()).finish();
		 * }
		 */
	}

	public static void goTOEC2() {
		try {
			if (RootActivity.PJ_Detail != null) {
				RootActivity.PJ_Detail.dismiss();
				RootActivity.PJ_Detail = null;
			}
			if (RootActivity.PJ_AddToCart != null) {
				RootActivity.PJ_AddToCart.dismiss();
			}
			if (RootActivity.PJ_ListCat != null) {
				RootActivity.PJ_ListCat.dismiss();
			}
			if (RootActivity.PJ_FavoriteDialog != null) {
				RootActivity.PJ_FavoriteDialog.dismiss();
			}
			if (RootActivity.PJ_HistoryDialog != null) {
				RootActivity.PJ_HistoryDialog.dismiss();
			}
			if (RootActivity.PJ_HistoryDetailDialog != null) {
				RootActivity.PJ_HistoryDetailDialog.dismiss();
			}
		} catch (Exception e) {
			LogUtils.e("KKKKKKKKKKK", "HHHHHHHHHHHHHHHHHHHHHH");
		}
	}
}
