package org.com.atmarkcafe.sky.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.json.JSONArray;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Account;
import com.acv.cheerz.db.Setting;

public class InviteFriendFragment extends BaseFragment {
	private static final String TAG = "InviteFriendFragment";
	static View views;
	static java.util.ArrayList<String> list_mail_invite = new ArrayList<String>();
	String profile_language = "en";
	private LoadingView loading;
	private org.com.atmarkcafe.view.HeaderView header;
	static int Total_add_mail = 0;
	private ScrollView parentView;
	public InviteFriendFragment() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.user_invitefriend;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if(v.getId() == R.id.button_send){
			addMoreMail.getListMailMore();
			TextView email_mail = CommonAndroid.getView(views, R.id.invite_friend_emails);
			String email = email_mail.getText().toString().trim();
			if(!"".equals(email)){
				list_mail_invite.add(email);
			}
			/*Toast t = Toast.makeText(getActivity(), list_mail_invite.toString(), Toast.LENGTH_SHORT);
			t.show();*/
//			if(list_mail_invite.size() > 0){
				sendInvite();
//			}else{
//				if(new Setting(getActivity()).isLangEng()){
//					CommonAndroid.showDialog(getActivity(), getActivity().getResources().getString(R.string.invite_friend_emails_error_not_found) ,null);
//				}else{
//					CommonAndroid.showDialog(getActivity(), getActivity().getResources().getString(R.string.invite_friend_emails_error_not_found_j),null );
//				}
//			}
		} else if(v.getId() == R.id.profile_language_v){
			changeLanguage(profile_language, true);
		} else if(v.getId() == R.id.header_btn_left){
			SkyMainActivity.sm.toggle();
		} else if(v.getId() == R.id.invite_delete_mail){
			CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_delete_mail));
			CommonAndroid.showView(true, CommonAndroid.getView(views, R.id.invite_done_mail));
			if(Total_add_mail > 0){
				addMoreMail.enableListDelete(true);
			}
		} else if(v.getId() == R.id.invite_done_mail){
			CommonAndroid.showView(true, CommonAndroid.getView(views, R.id.invite_delete_mail));
			CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_done_mail));
			addMoreMail.enableListDelete(false);
		}
	}
	
	private void changeLanguage(String type, Boolean change){
		TextView set_language = CommonAndroid.getView(views, R.id.profile_language_v);
		if(change){
			if(type.equals("en")){
				profile_language = "ja";
				set_language.setText(R.string.menu_lang_japanese);
			}else{
				profile_language = "en";
				set_language.setText(R.string.menu_lang_english);
			}
		}else{
			if(type.equals("en")){
				profile_language = "en";
				set_language.setText(R.string.menu_lang_english);
			}else{
				profile_language = "ja";
				set_language.setText(R.string.menu_lang_japanese);
			}
		}
		
	}

	private void sendInvite() {
		// TODO Auto-generated method stub
		//lang
		//coupon_code
		//emails
		/*StringBuffer result = new StringBuffer();
		result.append("(");
		for (int i = 0; i < list_mail_invite.size(); i++) {
			if(i > 0){
				result.append(",");
			}
			result.append( ""+list_mail_invite.get(i) +"");
		   //result.append( optional separator );
		}
		result.append(")");
		String mynewstring = result.toString();
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][lang]", profile_language);
		inputs.put("req[param][coupon_code]", ((TextView)CommonAndroid.getView(views, R.id.invite_friend_coupon)).getText().toString().trim() );
		inputs.put("req[param][emails]", mynewstring);
		LogUtils.e(TAG, "data_up==" + mynewstring);
		SkyUtils.execute(getActivity(), RequestMethod.POST, API.API_USER_INVITE, inputs, callbacksendinvite);
		postData2();*/
		try {
			JSONArray email_ar = new JSONArray(); 
			for (int i = 0; i < list_mail_invite.size(); i++) {
				email_ar.put(list_mail_invite.get(i));
			}
			JSONObject objParam = new JSONObject();//root key: param
			objParam.put("lang", profile_language);
			objParam.put("coupon_code",  ((TextView)CommonAndroid.getView(views, R.id.invite_friend_coupon)).getText().toString().trim());
			objParam.put("emails", email_ar );
			SkyUtils.execute(getActivity(), RequestMethod.POST, API.API_USER_INVITE, SkyUtils.paramRequest(getActivity(), objParam) , callbacksendinvite);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(View view) {
		this.views = view;
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.icon_invite_star, getResources().getColor(R.color.mail_header));//OK
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		TextView addmore = CommonAndroid.getView(view, R.id.button_add_more);
		addMoreMail.add(getActivity(), addmore);
		CommonAndroid.getView(view, R.id.button_send).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.profile_language_v).setOnClickListener(this);
		loading = CommonAndroid.getView(view, R.id.loading);
		CommonAndroid.showView(false, loading);
		if(new Setting(getActivity()).isLangEng()){
			profile_language = "en";
		}else{
			profile_language = "ja";
		}
		changeLanguage(profile_language , false);
		header = CommonAndroid.getView(view, R.id.header);
		header.initHeader(R.string.menu_invite_friend, R.string.invite_friend_header_j);
		header.visibivityRight(false, R.drawable.icon_edit);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.invite_delete_mail).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.invite_done_mail).setOnClickListener(this);
		parentView = CommonAndroid.getView(view, R.id.main);
		CommonAndroid.TouchViewhiddenKeyBoard(getActivity(),parentView);
		CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_delete_mail));
	}

	private CheerzServiceCallBack callbacksendinvite = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.hiddenKeyBoard(getActivity());
			CommonAndroid.showView(true, loading);
		};

		public void onError(String message) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
//				SkyUtils.showDialog(getActivity(), SkyUtils.convertStringErr(message));
				CommonAndroid.showDialog(getActivity(), SkyUtils.convertStringErr(message) , null);
				if(Total_add_mail > 0){
					CommonAndroid.showView(true, CommonAndroid.getView(views, R.id.invite_delete_mail));
					CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_done_mail));
				}else{
					CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_delete_mail));
					CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_done_mail));
				}
				addMoreMail.enableListDelete(false);
			}
		};

		public void onSucces(String response) {
			CommonAndroid.showView(false, loading);
			if (!isFinish()) {
				JSONObject mainJsonObject;
				try {
					mainJsonObject = new JSONObject(response);
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);

					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						if(new Setting(getActivity()).isLangEng()){
							CommonAndroid.showDialog(getActivity(), getActivity().getResources().getString(R.string.invite_friend_success) , null);
						}else{
							CommonAndroid.showDialog(getActivity(), getActivity().getResources().getString(R.string.invite_friend_success_j), null );
						}
						addMoreMail.deleteListMailMore();
						if(Total_add_mail > 0){
							CommonAndroid.showView(true, CommonAndroid.getView(views, R.id.invite_delete_mail));
							CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_done_mail));
						}else{
							CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_delete_mail));
							CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_done_mail));
						}
						
					}else{
						String err_msg = CommonAndroid.getString(mainJsonObject,
								SkyUtils.KEY.err_msg);
//						SkyUtils.showDialog(getActivity(), err_msg );
						CommonAndroid.showDialog(getActivity(), SkyUtils.convertStringErr(err_msg) , null);
					}
					addMoreMail.enableListDelete(false);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
	};

	@Override
	public void onChangeLanguage() {
		// TODO Auto-generated method stub
		if(new Setting(getActivity()).isLangEng()){
			profile_language = "en";
		}else{
			profile_language = "ja";
		}
		changeLanguage(profile_language, false);
	}
	
	public static class addMoreMail {

		public static void getListMailMore()
		{
			LinearLayout scrollViewlinerLayout = (LinearLayout) views.findViewById(R.id.linearLayoutForm);
			list_mail_invite.clear();
			for (int i = 0; i < scrollViewlinerLayout.getChildCount(); i++)
			{
				LinearLayout innerLayout = (LinearLayout) scrollViewlinerLayout.getChildAt(i);
				EditText edit = (EditText) CommonAndroid.getView(innerLayout, R.id.editDescricao);
				String temp = edit.getText().toString().trim();
				if(!"".equals(temp)){
					list_mail_invite.add(temp);
				}
				
				
			}

		}
		
		public static void deleteListMailMore()
		{
			LinearLayout scrollViewlinerLayout = (LinearLayout) views.findViewById(R.id.linearLayoutForm);
			list_mail_invite.clear();
			scrollViewlinerLayout.removeAllViews();
			((TextView)CommonAndroid.getView(views, R.id.invite_friend_coupon)).setText("");
			((TextView)CommonAndroid.getView(views, R.id.invite_friend_emails)).setText("");
			Total_add_mail = 0;
		}
		
		public static void enableListDelete(Boolean enable)
		{
			LinearLayout scrollViewlinerLayout = (LinearLayout) views.findViewById(R.id.linearLayoutForm);
			for (int i = 0; i < scrollViewlinerLayout.getChildCount(); i++)
			{
				LinearLayout innerLayout = (LinearLayout) scrollViewlinerLayout.getChildAt(i);
				EditText edit = (EditText) CommonAndroid.getView(innerLayout, R.id.editDescricao);
				ImageView btnRemove = (ImageView) innerLayout.findViewById(R.id.btnRemove);
				btnRemove.setVisibility(enable ? View.VISIBLE : View.GONE);
			}

		}
		
		public static void add(final Activity activity, TextView addmore)
		{
			final LinearLayout linearLayoutForm = (LinearLayout) views.findViewById(R.id.linearLayoutForm);
			addmore.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					addMoreMail.enableListDelete(false);
					Total_add_mail++;
					CommonAndroid.showView(true, CommonAndroid.getView(views, R.id.invite_delete_mail));
					CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_done_mail));
					final LinearLayout newView = (LinearLayout)activity.getLayoutInflater().inflate(R.layout.invitefriend_rowdetail, null);

					newView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

					ImageView btnRemove = (ImageView) newView.findViewById(R.id.btnRemove);
					TextView input_mail = (TextView) newView.findViewById(R.id.editDescricao);
					input_mail.setWidth((int) activity.getResources().getDimension(R.dimen.dimen_300dp));
					btnRemove.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Total_add_mail--;
							linearLayoutForm.removeView(newView);
							if(Total_add_mail <= 0){
								CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_delete_mail));
								CommonAndroid.showView(false, CommonAndroid.getView(views, R.id.invite_done_mail));
								addMoreMail.enableListDelete(false);
							}
						}
					});
					btnRemove.setVisibility(View.GONE);
					linearLayoutForm.addView(newView);
				}
			});
		}
	}
}