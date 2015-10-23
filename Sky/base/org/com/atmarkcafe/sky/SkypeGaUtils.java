package org.com.atmarkcafe.sky;

import java.util.HashMap;

import org.com.atmarkcafe.sky.dialog.EcAddToCartDialog;
import org.com.atmarkcafe.sky.dialog.EcAddressDialog;
import org.com.atmarkcafe.sky.dialog.EcCreditDialog;
import org.com.atmarkcafe.sky.dialog.EcFinishDialog;
import org.com.atmarkcafe.sky.dialog.EcProductsDetailDialog;
import org.com.atmarkcafe.sky.dialog.EcUpdateAddressEditDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountAddressUpdateEditDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountFavoriteDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountMyAddressDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountMyCreditDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountOptionDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountOrderHistoryDetailDialog;
import org.com.atmarkcafe.sky.dialog.MyAccountOrderHistoryDialog;
import org.com.atmarkcafe.sky.dialog.ProfileUpdateDialog;
import org.com.atmarkcafe.sky.fragment.AboutSpmcFragment;
import org.com.atmarkcafe.sky.fragment.ChangepwdFragment;
import org.com.atmarkcafe.sky.fragment.ContactFragment;
import org.com.atmarkcafe.sky.fragment.ECFragment;
import org.com.atmarkcafe.sky.fragment.EmailDetailFragment;
import org.com.atmarkcafe.sky.fragment.FaqFragment;
import org.com.atmarkcafe.sky.fragment.GalleryDetailFragment;
import org.com.atmarkcafe.sky.fragment.GalleryFragment;
import org.com.atmarkcafe.sky.fragment.HomeFragment;
import org.com.atmarkcafe.sky.fragment.InviteFriendFragment;
import org.com.atmarkcafe.sky.fragment.MailboxFragment;
import org.com.atmarkcafe.sky.fragment.MyCart2Fragment;
import org.com.atmarkcafe.sky.fragment.NewsFragment;
import org.com.atmarkcafe.sky.fragment.PravicyFragment;
import org.com.atmarkcafe.sky.fragment.ProfileFragment;
import org.com.atmarkcafe.sky.fragment.ServiceList2Fragment;
import org.com.atmarkcafe.sky.fragment.ServiceListFragment;
import org.com.atmarkcafe.sky.fragment.TermFragment;
import org.com.atmarkcafe.sky.fragment.TradeFragment;
import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CommonAndroid;
import z.lib.base.DataStore;
import z.lib.base.LibsBaseAdialog;
import z.lib.base.LogUtils;
import android.app.Activity;
import android.app.Application;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

public class SkypeGaUtils {
	public enum TrackerName {
		APP_TRACKER, // Tracker used only in this app.
		GLOBAL_TRACKER, // Tracker used by all the apps from a company. eg:
						// roll-up tracking.
		ECOMMERCE_TRACKER, // Tracker used by all ecommerce transactions from a
							// company.
	}

	private static String TAG = "SkypeGaUtils";

	private static final String PROPERTY_ID = "UA-64049832-1";
	public static final String PROPERTY_ID_TEST = "UA-66607508-1";
	private static final boolean TRACKER_TEST = true;
	public static int GENERAL_TRACKER = 0;
	private HashMap<TrackerName, Tracker> mTrackers = new HashMap<TrackerName, Tracker>();

	private SkypeGaUtils() {
	}

	public Application application;

	public void init(Application application) {
		this.application = application;
	}

	private static SkypeGaUtils instance = new SkypeGaUtils();

	public static SkypeGaUtils intance() {
		return instance;
	}

	public void onStart(Activity activity) {
		GoogleAnalytics.getInstance(activity).reportActivityStart(activity);
	}

	public void onStop(Activity activity) {
		GoogleAnalytics.getInstance(activity).reportActivityStop(activity);
	}

	public void ga(String page, Activity activity) {
		boolean isEnglish = new Setting(activity).isLangEng();
//		LogUtils.ENABLE = true;
		String KEY = "GA_KEY";
		HashMap<String, Integer> maps = new HashMap<String, Integer>();

		// maps.put(key, isEnglish ? R.string.ga_category_eng :
		// R.string.ga_category_jap);
		// maps.put(key, isEnglish ? R.string.ga_new_product_eng :
		// R.string.ga_new_product_jap);
		// maps.put(key, isEnglish ? R.string.ga_platinum_product_eng :
		// R.string.ga_platinum_product_jap);
		// maps.put(key, isEnglish ? R.string.ga_carefully_selected_gourmet_eng
		// : R.string.ga_carefully_selected_gourmet_jap);
		// maps.put(key, isEnglish ? R.string.ga_wine_sake_eng :
		// R.string.ga_wine_sake_jap);
		// maps.put(key, isEnglish ? R.string.ga_food_beverage_eng :
		// R.string.ga_food_beverage_jap);
		// maps.put(key, isEnglish ? R.string.ga_art_eng : R.string.ga_art_jap);
		// maps.put(key, isEnglish ? R.string.ga_cart__eng :
		// R.string.ga_cart__jap);
		// maps.put(key, isEnglish ? R.string.ga_fruit_eng :
		// R.string.ga_fruit_jap);
		// maps.put(key, isEnglish ? R.string.ga_payment_eng :
		// R.string.ga_payment_jap);
		// maps.put(key, isEnglish ? R.string.ga_deliver_method_eng :
		// R.string.ga_deliver_method_jap);
		// maps.put(key, isEnglish ? R.string.ga_deliver_address_eng :
		// R.string.ga_deliver_address_jap);
		// maps.put(key, isEnglish ? R.string.ga_credit_information_eng :
		// R.string.ga_credit_information_jap);
		// maps.put(key, isEnglish ? R.string.ga_credit_card_payment_eng :
		// R.string.ga_credit_card_payment_jap);

		LogUtils.e(KEY, page);
		maps.put(HomeFragment.class.getSimpleName(), isEnglish ? R.string.menu_service_list : R.string.menu_service_list_j);
		maps.put(MailboxFragment.class.getSimpleName(), isEnglish ? R.string.menu_mailbox : R.string.menu_mailbox_j);
		maps.put(EmailDetailFragment.class.getSimpleName(), isEnglish ? R.string.menu_mailbox : R.string.menu_mailbox_j);
		maps.put(GalleryFragment.class.getSimpleName(), isEnglish ? R.string.menu_gallery : R.string.menu_gallery_j);
		maps.put(FaqFragment.class.getSimpleName(), isEnglish ? R.string.menu_faq : R.string.menu_faq_j);
		maps.put(ContactFragment.class.getSimpleName(), isEnglish ? R.string.menu_contact_us : R.string.menu_contact_us_j);
		maps.put(NewsFragment.class.getSimpleName(), isEnglish ? R.string.menu_news : R.string.menu_news_j);
		maps.put(AboutSpmcFragment.class.getSimpleName(), isEnglish ? R.string.menu_about_spmc : R.string.menu_about_spmc_j);
		maps.put(InviteFriendFragment.class.getSimpleName(), isEnglish ? R.string.menu_invite_friend : R.string.menu_invite_friend_j);
		maps.put(TradeFragment.class.getSimpleName(), isEnglish ? R.string.menu_trade_law : R.string.menu_trade_law_j);
		maps.put(PravicyFragment.class.getSimpleName(), isEnglish ? R.string.menu_privacy : R.string.menu_privacy_j);
		maps.put(TermFragment.class.getSimpleName(), isEnglish ? R.string.menu_term : R.string.menu_term_j);
		maps.put(ChangepwdFragment.class.getSimpleName(), isEnglish ? R.string.menu_change_pass : R.string.menu_change_pass_j);
		maps.put(GalleryDetailFragment.class.getSimpleName(), isEnglish ? R.string.ga_product_detail_eng : R.string.ga_product_detail_jap);
		maps.put(ECFragment.class.getSimpleName(), isEnglish ? R.string.ga_product_detail_eng : R.string.ga_product_detail_jap);
		maps.put(ServiceListFragment.class.getSimpleName(), isEnglish ? R.string.ga_product_detail_eng : R.string.ga_product_detail_jap);
		maps.put(ServiceList2Fragment.class.getSimpleName(), isEnglish ? R.string.ga_product_detail_eng : R.string.ga_product_detail_jap);
		maps.put(EcAddressDialog.class.getSimpleName(), isEnglish ? R.string.ga_address_eng : R.string.ga_address_jap);
		maps.put(MyAccountMyAddressDialog.class.getSimpleName(), isEnglish ? R.string.ga_address_eng : R.string.ga_address_jap);
		maps.put(MyAccountOptionDialog.class.getSimpleName(), isEnglish ? R.string.ga_my_account_eng : R.string.ga_my_account_jap);
		maps.put(EcUpdateAddressEditDialog.class.getSimpleName(), isEnglish ? R.string.ga_address_edit_eng : R.string.ga_address_edit_jap);
		maps.put(MyAccountAddressUpdateEditDialog.class.getSimpleName(), isEnglish ? R.string.ga_address_edit_eng : R.string.ga_address_edit_jap);
		maps.put(MyAccountOrderHistoryDialog.class.getSimpleName(), isEnglish ? R.string.ga_purchase_history_eng : R.string.ga_purchase_history_jap);
		maps.put(MyAccountOrderHistoryDetailDialog.class.getSimpleName(), isEnglish ? R.string.ga_purchase_history_detail_eng : R.string.ga_purchase_history_detail_jap);
		maps.put(MyAccountFavoriteDialog.class.getSimpleName(), isEnglish ? R.string.ga_favourite_product_eng : R.string.ga_favourite_product_jap);
		maps.put(ProfileFragment.class.getSimpleName(), isEnglish ? R.string.ga_user_information_eng : R.string.ga_user_information_jap);
		maps.put(ProfileUpdateDialog.class.getSimpleName(), isEnglish ? R.string.ga_user_information_eng : R.string.ga_user_information_jap);
		maps.put(MyAccountMyCreditDialog.class.getSimpleName(), isEnglish ? R.string.ga_credit_information_eng : R.string.ga_credit_information_jap);
		maps.put(EcCreditDialog.class.getSimpleName(), isEnglish ? R.string.ga_credit_information_eng : R.string.ga_credit_information_jap);
		maps.put(ProfileFragment.class.getSimpleName(), isEnglish ? R.string.ga_contract_management_eng : R.string.ga_contract_management_jap);
		maps.put(EcAddToCartDialog.class.getSimpleName(), isEnglish ? R.string.ga_add_to_cart_eng : R.string.ga_add_to_cart_jap);
		maps.put(EcProductsDetailDialog.class.getSimpleName(), isEnglish ? R.string.ga_product_detail_eng : R.string.ga_product_detail_jap);
		maps.put(EcFinishDialog.class.getSimpleName(), isEnglish ? R.string.ga_order_successful_eng : R.string.ga_order_successful_jap);
		maps.put(MyCart2Fragment.class.getSimpleName(), isEnglish ? R.string.ga_shoping_cart_list_eng : R.string.ga_shoping_cart_list_jap);
		LogUtils.e("PAGEACV", page);

		if (!maps.containsKey(page)) {
			return;
		}
		LogUtils.e("PAGEACV", "PUSH : " + page);
		int res = maps.get(page);

		if (activity == null) {
			return;
		}

		String xpage = activity.getString(res) + "_ANDROID";

		DataStore.getInstance().init(activity);
		String data = DataStore.getInstance().get(KEY, "[]");
		JSONArray array = new JSONArray();
		try {
			array = new JSONArray(data);
			JSONObject jsonObject = new JSONObject();
			jsonObject.put("name", page + " : " + xpage);
			boolean isAdd = true;
			for (int i = 0; i < array.length(); i++) {
				if (array.getJSONObject(i).toString().equals(jsonObject.toString())) {
					isAdd = false;
				}
			}

			if (isAdd) {
				array.put(jsonObject);
			}
		} catch (Exception e) {
			LogUtils.e(KEY, e);
		}
		GoogleAnalytics.getInstance(activity).reportActivityStart(activity);
		String userId = new Account(activity).getUserId();
		if (CommonAndroid.isBlank(userId)) {
		} else if (!userId.startsWith("123")) {
			ga(userId + " " + xpage);
		}

		GoogleAnalytics.getInstance(activity).reportActivityStop(activity);

		DataStore.getInstance().save(KEY, array.toString());
//		LogUtils.ENABLE = false;
	}

	public void ga(String page) {
		if (page == null || "".equals(page)) {
			return;
		}
		LogUtils.i(TAG, "GA==" + page);
		/*
		 * Tracker t = getTracker( TrackerName.APP_TRACKER); t.send(new
		 * HitBuilders.EventBuilder().setCategory(page)
		 * .setAction(page).setLabel(page).build()); t.setScreenName(page);
		 * t.send(new HitBuilders.AppViewBuilder().build());
		 */

		Tracker t = getTracker(TrackerName.APP_TRACKER);
		t.send(new HitBuilders.EventBuilder().setCategory(application.getString(R.string.menu_service_list)).setAction(application.getString(R.string.menu_service_list)).setLabel(page).build());
		t.setScreenName(page);
		t.send(new HitBuilders.AppViewBuilder().build());
	}

	public void ga(BaseFragment page, Activity activity) {
		ga(page.getClass().getSimpleName(), activity);
	}

	public void ga(LibsBaseAdialog page, Activity activity) {
		ga(page.getClass().getSimpleName(), activity);
	}

	private synchronized Tracker getTracker(TrackerName trackerId) {
		if (!mTrackers.containsKey(trackerId)) {

			GoogleAnalytics analytics = GoogleAnalytics.getInstance(application);
			Tracker t = analytics.newTracker(PROPERTY_ID);
			if (TRACKER_TEST) {
				t = analytics.newTracker(PROPERTY_ID_TEST);
			}
			mTrackers.put(trackerId, t);

		}
		return mTrackers.get(trackerId);
	}
}
