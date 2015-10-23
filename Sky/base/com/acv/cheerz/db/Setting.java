package com.acv.cheerz.db;

import java.util.ArrayList;
import java.util.List;

import z.lib.base.DataStore;
import z.lib.base.LogUtils;
import z.lib.base.OnChangLanguage;
import android.content.Context;

import com.SkyPremiumLtd.SkyPremium.R;

public class Setting extends SkypeTable {
	public Setting(Context context) {
		super(context);

		addColumns(lang);
	}

	public static final String lang = "lang";

	@Override
	public int getIndex() {
		return 4;
	}

	/**
	 * 
	 * @param eng
	 *            R.id.eng R.id.ja
	 */
	public void saveLang(int eng) {
		boolean isLangEng = isLangEng();
		DataStore.getInstance().save("saveLang", eng == R.id.eng);
		if (isLangEng != isLangEng()) {
			for (OnChangLanguage changLanguage : lOnchangLaguage) {
				changLanguage.sendEmptyMessage(0);
			}
		}

	}
	
	public void saveLangLogin(int eng) {
		boolean isLangEng = isLangEng();
		DataStore.getInstance().save("saveLang", eng == R.id.eng);
		DataStore.getInstance().save("saveLangLogin", eng == R.id.eng);
		if (isLangEng != isLangEng()) {
			for (OnChangLanguage changLanguage : lOnchangLaguage) {
				changLanguage.sendEmptyMessage(0);
			}
		}

	}
	
	public void settingPush(String push) {
		LogUtils.e("settingPush", "==========" +push);
		DataStore.getInstance().save("setPush", push);
	}
	
	public String isPush() {
		DataStore.getInstance().init(getContext());
		return DataStore.getInstance().get("setPush", null);
	}

	public boolean isLangEng() {
		DataStore.getInstance().init(getContext());
		return DataStore.getInstance().get("saveLang", true);
	}
	
	public boolean isLangEngLogin() {
		DataStore.getInstance().init(getContext());
		return DataStore.getInstance().get("saveLangLogin", true);
	}

	private static List<OnChangLanguage> lOnchangLaguage = new ArrayList<OnChangLanguage>();

	public static void registerOnChangLanguage(OnChangLanguage changLanguage) {
		lOnchangLaguage.add(changLanguage);
	}

	public void unRegisterOnChangLanguage(OnChangLanguage changLanguage) {
		lOnchangLaguage.remove(changLanguage);
	}

	public String getLangStr() {
		return isLangEng() ? "en" : "ja";
	}
	
	public void settingPushRedirect(int redirect) {
		LogUtils.i("settingPush redirect", "==========" +redirect);
		DataStore.getInstance().save("setPushRedirect", ""+redirect);
	}
	
	public String getPushRedirect() {
		DataStore.getInstance().init(getContext());
		return DataStore.getInstance().get("setPushRedirect", null);
	}
}