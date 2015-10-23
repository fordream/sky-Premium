package z.lib.base;

import java.util.HashMap;
import java.util.Map;

import org.com.atmarkcafe.sky.SkypeApplication;
import org.com.atmarkcafe.sky.dialog.ComfirmMessageDialog;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;

public class SkyUtils {

	public static class API {

		public static final String BASESERVER_ 	= "http://pgcafe.asia/samuraistation-clone/";//server test
//		public static final String BASESERVER_ 	= "http://member.skypremium.com.sg/"; //link version 1.1
		public static final String BASESERVER 	= BASESERVER_ + "api/";
//		public static final String BASESERVER = "http://acvdev.com/samuraistation-clone/api/"; server test
//		public static final String BASESERVER = "http://spmc.skypremium.com.sg/api/"; link version 1.0

		public static final String API_USER_LOGIN = "auth/login";
		public static final String API_USER_LOGOUT = "auth/logout";
		public static final String API_USER_FORGOTPWD = "auth/forgotpwd";
		public static final String API_USER_FORGOTID = "auth/forgotid";

		public static final String API_USER_MAILBOX = "user/mailbox";
		public static final String API_USER_MAILDETAIL = "user/maildetail";
		public static final String API_USER_PROFILE = "user/profile";
		public static final String API_USER_PROFILEUPDATE = "user/profileupd";
		public static final String API_USER_CHANGEPWD = "user/pwdchange";
		public static final String API_USER_CONTRACT_INFO = "contract/index";
		public static final String API_USER_INVITE = "user/invite";
		public static final String API_USER_CONTACT = "user/contact";
		public static final String API_USER_CONTACT_TYPE = "api/user/contacttype";
		public static final String API_USER_CONTACT_DETAIL = "user/contactdetail";
		public static final String API_USER_PURCHASEHISTORY = "user/purchasehistory";
		public static final String API_USER_PURCHASEDETAIL = "user/purchasedetail";

		public static final String API_SERVICE_NEWS = "service/news";
		public static final String API_SERVICE_NEWS_DETAIL = "/service/newsdetail";
		public static final String API_SERVICE_ABOUT = "service/post"; // About
																		// SPMC
		public static final String API_SERVICE_FAQ = "service/post"; // FAQ
		public static final String API_SERVICE_DOWLOAD = "service/post"; // Download
		public static final String API_SERVICE_LIST = "service/post"; // Service
																		// List

		public static final String API_EC_PRODUCTLIST = "ec/product";// Product
																		// list
		public static final String API_EC_PRODUCTDETAIL = "ec/productdetail";// Product2
																				// detail
		public static final String API_EC_CARRIER = "ec/carrier";
		/*My cart*/
		public static final String API_EC_SHOWCART =  "ec/showcart";
		public static final String API_EC_ADDTOCART =  "ec/addtocart";
		public static final String API_EC_UPDATECART =  "ec/updatecart";
		public static final String API_EC_ADDRESS_UPDATE_ORDER =  "ec/updateorderaddress";
		public static final String API_EC_ADDRESS_BOOK =  "ec/listaddress";
		public static final String API_EC_ADDRESS_UPDATE =  "ec/updateaddress";
		public static final String API_EC_ADDRESS_UPDATE_NEW =  "ec/addnewaddress";
		public static final String API_EC_CREDIT =  "ec/listusedcards";
		public static final String API_EC_CREDIT_NEW =  "ec/addnewcard";
		public static final String API_EC_CONFIRM =  "ec/orderconfirm";
		public static final String API_USER_LISTRECOMMENT ="user/listrecommend";
		public static final String API_EC_SEARCH ="ec/search";
		public static final String API_CONTRACT_FORM = "contract/form";
		public static final String API_HELP = "user/help";
		public static final String API_CONTARCT_TERM ="contract/term";
		public static final String API_CONTARCT_UPDATE_BANK ="contract/banktransfer";
		
		public static final String API_MYACCOUNT_HISTORY ="ec/orderhistory";
		public static final String API_MYACCOUNT_HISTORY_DETAIL ="ec/orderdetail";//id_order
		public static final String API_MYACCOUNT_HISTORY_RE ="ec/reorder";//id_order
		public static final String API_MYACCOUNT_HISTORY_MESSAGE ="ec/ordermessage";//id_order,id_product,message
		
		public static final String API_MYACCOUNT_FAVORITE ="ec/listfavoriteproducts";
		public static final String API_MYACCOUNT_FAVORITE_DEL ="ec/deletefavoriteproduct";
		public static final String API_MYACCOUNT_FAVORITE_ADD ="ec/addfavoriteproduct";
		public static final String API_MYACCOUNT_ADDRESS ="ec/listaddress";
		public static final String API_MYACCOUNT_ADDRESS_DEL ="ec/deleteaddress";
		public static final String API_MYACCOUNT_CARD ="ec/listusedcards";
		public static final String API_MYACCOUNT_CARD_ADD ="ec/addnewcard";
		public static final String API_MYACCOUNT_CARD_UPDATE ="ec/updatecard";
		public static final String API_MYACCOUNT_CARD_DEL ="ec/deletecard";
		public static final String API_GET_LIST_STATE ="ec/liststate";
		
		public static final String API_GET_BADGE ="user/getNotifyBadge";
		public static final String API_UPDATE_BADGE = "user/updateNotifyBadge";
		
//		public static final String API_HELP = "";
//		public static final String API_TRADE = "";
//		public static final String API_PRIVACY = "";
		
		
	}

	public static final class KEY {
		public static final String KEY_SCREEN = "KEY_SCREEN";
		public static final String is_success = "is_success";
		public static final String err_msg = "err_msg";
		public static final String err_msg_acc_conflic = "account_conflict";
		public static final String err_msg_timeout = "session_timeout";
		public static final String REMEMBERPASSWORD = "req[param][remember]";
		public static final String KEY_SCREEN_TAG = "KEY_SCREEN_TAG";
		public static final String PRODUCT_NOT_AVAILABLE = "product_not_available";
		public static final String PRODUCT_NOT_AVAILABLE_DETAIL = "product_not_available_detail";
	}
	
	public static final class KEY_CONTRACT {
//		public static final int update_annual_contract = 1;
//		public static final int upgrade_platinum = 2;
//		public static final int upgrade_premium = 3;
		
		public static final int extend_after_expire = 1;//done
		public static final int extend_after_expire_gold = 4;
//		public static final int extend_before_expire_month = 5;
//		public static final int extend_before_expire_year = 6;
		public static final int update_contract_unpaid = 0;//done
//		public static final int update_contract_unpaid_month = 7;
//		public static final int update_contract_unpaid_year = 7;
		public static final int upgrade_platinum = 9;
		public static final int not_transfer_yet = 2;
		public static final int register_another_credit_card = 3;
		public static final int extend_before_expire = 6;
		
	}
	
	public static String shareKey = "com.acv.sky";
	public static String BADGE_EMAIL = "badge_mail";
	public static String BADGE_NEWS = "badge_new";
	public static String BADGE_LOG_TIME = "badge_log_time";
	public static String OPEN_QR_CODE = "open_qr_code";
//	public static String BADGE_TOTAL = "badge_total";
//	public static String BADGE_TOTAL_NEW = "badge_total_new";
	
	public enum SCREEN {
		LOGIN, MAIL_BOX,MAIL_BOX_DETAIL, FAQ, DOWNLOAD, CONTACT, NEWS, ABOUT_SPMC, HOME, DEFAULT, PROFILE, EC__PRODUCT_LIST,
		SERVICE_LIST,SERVICE_LIST_DETAIL , MYCART , ADDTOCART, PJDETAIL , EC_ADDRESS, MYACCOUNT_HISTORY
	}
	
	public static class FRAGMENT{
		public static final String FR_GALLERY = "gallery";
		public static final String FR_GALLERY_DETAIL = "gallery_detail";
	}
	
	public static boolean FLAG_RELEASE = false;//default == true

	public final static class VALUE {

		public static final String STATUS_API_SUCCESS = "1";
		public static final String STATUS_REMEMBER_SUCCESS = "1";
		public static final String STATUS_API_FAIL = "0";

	};
	
	

	public static void execute(Context context, final RequestMethod method,
			final String api, final Map<String, String> params,
			final CheerzServiceCallBack cheerzServiceCallBack) {
		((SkypeApplication) context.getApplicationContext()).execute(method,
				api, params, cheerzServiceCallBack);

	}
	
	public static void execute(Context context, final RequestMethod method,
			final String api, final JSONObject params,
			final CheerzServiceCallBack cheerzServiceCallBack) {
		Log.i("TAG", "Data send server : " + params.toString());
		((SkypeApplication) context.getApplicationContext()).execute(method,
				api, params, cheerzServiceCallBack);

	}

	public static void showDialog(Context activity, String message, OnClickListener listener) {
		/*Builder builder = new Builder(activity);
		builder.setMessage(message);
		builder.show();*/
		if(activity != null && message != null)
			new ComfirmMessageDialog(activity, message, listener, null, (new Setting(activity).isLangEng() ? R.string.tlt_ok : R.string.tlt_ok_j), 0).show();
	}

	public static void showDialogCustom(Context activity, String message,int resStringEnglish,int resStringJapan, OnClickListener listener) {
		new ComfirmMessageDialog(activity, message, listener, null, (new Setting(activity).isLangEng() ? resStringEnglish : resStringJapan), 0).show();
	}
	
	public static boolean isApiSuccess(String response) {
		try {
			JSONObject json = new JSONObject(response);
			String is_succes = CommonAndroid.getString(json,
					SkyUtils.KEY.is_success);

			return SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes);
		} catch (Exception exception) {
			return false;
		}
	}

	public static void executeHeader(Context context, RequestMethod method,
			String api, Map<String, String> headers,
			CheerzServiceCallBack cheerzServiceCallBack) {
		((SkypeApplication) context.getApplicationContext()).execute(method,
				api, new HashMap<String, String>(), headers,
				cheerzServiceCallBack);
	}
	
	public static String convertStringErr(String err){
		String message_alert = "";
		String input = err;
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(input);
			for (int i = 0; i < jsonArray.length(); i++) {
				message_alert+= jsonArray.getString(i) + "\n";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return err;
		}
		if(!"".equals(message_alert)){
			return message_alert;
		}else{
			return err;
		}
		
	}
	
	public static void saveInt(Context context,String key,int value){
		SharedPreferences sharePref = context.getSharedPreferences(shareKey, Context.MODE_PRIVATE);
		sharePref.edit().putInt(key, value).commit();
	}
	
	public static int getInt(Context context,String key,int value){
		SharedPreferences sharePref = context.getSharedPreferences(shareKey, Context.MODE_PRIVATE);
		value = sharePref.getInt(key, value);
		return value;
	}
	
	public static String convertStringErrF_old(String err){
		String message_alert = "";
		String input = err;
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(input);
			for (int i = 0; i < jsonArray.length(); i++) {
				message_alert = jsonArray.getString(0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return err;
		}
		if(!"".equals(message_alert)){
			return message_alert;
		}else{
			return err;
		}
		
	}
	
	public static String convertStringErrF(String arr){
		String message_alert = "";
//		LogUtils.i("CCCCCC", arr);
		try {
			JSONArray jsonArray = (JSONArray) new JSONObject(new JSONTokener("{data:"+arr+"}")).get("data");
//			LogUtils.i("AAAAAAAA", jsonArray.toString());
			for (int i = 0; i < jsonArray.length(); i++) {
				message_alert += jsonArray.getString(i) + "\n";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}
		LogUtils.i("BBBBBB", message_alert);
		if(!"".equals(message_alert)){
			return message_alert;
		}else{
			return arr;
		}
	}
	
	public static String getVersionName(Context context){
		String versionName = null;
		try {
			versionName = context.getPackageManager()
				    .getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return versionName;
	}
	
	public static boolean isSimExist(Context context){
		TelephonyManager telMgr = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
	    int simState = telMgr.getSimState();
	    if (simState == TelephonyManager.SIM_STATE_READY) {
			return true;
		}
		return false;
	}
	public static JSONObject paramRequest(Context context,JSONObject jsons){
		JSONObject json = new JSONObject();
		try {
			JSONObject objReq = new JSONObject();
			objReq.put("lang", new Setting(context).getLangStr());
			objReq.put("user_id", new Account(context).getUserId());
			objReq.put("token_key", new Account(context).getToken());
			objReq.put("version", getVersionName(context));
			objReq.put("os","android");
			objReq.put("param", jsons);
			
			json.put("req", objReq);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return json;
	}
	
	public static String addStyleContent(String strContent){
//		strContent = strContent.replaceAll("font-family: Georgia", "font-family: Helvetica");
//		strContent = strContent.replaceAll("font-family:Georgia", "font-family: Helvetica");
		String content = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/fonts/helveticaneue.ttf\")}body {font-family: Arial, Helvetica, sans-serif;font-size: medium;text-align: justify;}</style></head><style>body {background-color: #ededed; color:#777777; width:98% ; margin: 0 auto}</style><body>";//lightgrey
//		String content = "<html><body>";
		return content.concat(strContent).concat("</body></html>");
	}
	
	public static String addStyleContentNewDetail(String strContent){
		String content = "<style>body {background-color: #f4f4f4; color:#777777; width:98% ; margin: 0 auto}</style><body>";//lightgrey
		return content.concat(strContent).concat("</body></html>");
	}
	
	public static String addStyleContentWithBackgoundWhite(String strContent){
		String content = "<style>body {background-color:#FFFFFF; color:#777777; width:98% ; margin: 0 auto}</style><body>";
		return content.concat(strContent).concat("</body></html>");
	}
	
	public static String addStyleContentTeam(String strContent){
		String content = "<style>body { width:98% ; margin: 0 auto ; font-size: 10.5pt;}  h1{ font-size: 13pt;} h2{ font-size: 12pt;} h3{ font-size: 11pt;} h4{ font-size: 10pt;} h5{ font-size: 9pt;}</style><body>";
		return content.concat(strContent).concat("</body></html>");
	}
}
