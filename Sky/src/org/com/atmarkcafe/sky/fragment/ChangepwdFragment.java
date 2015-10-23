package org.com.atmarkcafe.sky.fragment;

import java.util.HashMap;

import org.com.atmarkcafe.sky.GlobalFunction;
import com.SkyPremiumLtd.SkyPremium.R;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.json.JSONObject;

import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.SkyUtils.API;
import z.lib.base.callback.RestClient.RequestMethod;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class ChangepwdFragment extends BaseFragment {
	View views;
	private LoadingView loading;
	RelativeLayout main_detail;
	private org.com.atmarkcafe.view.HeaderView header;
	
	
	public ChangepwdFragment() {
		super();
	}

	@Override
	public int getLayout() {
		return R.layout.user_changepwd;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		String pwd_current = "";
		String pwd_new = "";
		String pwd_confirm = "";
		TextView pwd_current_t = CommonAndroid.getView(views, R.id.passwd_old);
		TextView pwd_new_t = CommonAndroid.getView(views, R.id.passwd_new);
		TextView pwd_confirm_t = CommonAndroid.getView(views, R.id.passwd_confirm);
		if(v.getId() == R.id.button_reset){
			Reset();
		}else if(v.getId() == R.id.button_submit){
			pwd_current = pwd_current_t.getText().toString();
			pwd_new 	= pwd_new_t.getText().toString();
			pwd_confirm = pwd_confirm_t.getText().toString();
			changePwd(pwd_current, pwd_new, pwd_confirm);
		} else if(v.getId() == R.id.header_btn_left){
			SkyMainActivity.sm.toggle();
		}
		
	}
	
	private void Reset(){
		TextView pwd_current_t = CommonAndroid.getView(views, R.id.passwd_old);
		TextView pwd_new_t = CommonAndroid.getView(views, R.id.passwd_new);
		TextView pwd_confirm_t = CommonAndroid.getView(views, R.id.passwd_confirm);
		pwd_current_t.setText("");
		pwd_new_t.setText("");
		pwd_confirm_t.setText("");
	}

	private void changePwd(String pwd_current, String pwd_new, String pwd_confirm) {
		// TODO Auto-generated method stub
		HashMap<String, String> inputs = new HashMap<String, String>();
		inputs.put("req[param][pwd_current]", pwd_current);
		inputs.put("req[param][pwd_new]", pwd_new);
		inputs.put("req[param][pwd_confirm]", pwd_confirm);
		SkyUtils.execute(getActivity(), RequestMethod.GET, API.API_USER_CHANGEPWD, inputs, callback);
	}

	private ScrollView parentView;
	@Override
	public void init(View view) {
		this.views = view;
		CommonAndroid.getView(view, R.id.button_submit).setOnClickListener(this);
		CommonAndroid.getView(view, R.id.button_reset).setOnClickListener(this);
		header = CommonAndroid.getView(view, R.id.header);
		header.initHeader(R.string.profile_change_password, R.string.profile_change_password_j);
		header.visibivityRight(false, R.drawable.icon_edit);
		CommonAndroid.getView(header, R.id.header_btn_left).setOnClickListener(this);
		main_detail = (RelativeLayout) views.findViewById(R.id.main_detail);
		loading = CommonAndroid.getView(main_detail, R.id.loading);
		CommonAndroid.showView(false, loading);
		parentView = CommonAndroid.getView(view, R.id.main);
		CommonAndroid.TouchViewhiddenKeyBoard(getActivity(),parentView);
		GlobalFunction.handlerKeyboardOnView(getActivity(), parentView);
	}

	private CheerzServiceCallBack callback = new CheerzServiceCallBack() {

		public void onStart() {
			CommonAndroid.showView(true, loading);
			CommonAndroid.hiddenKeyBoard(getActivity());
		};

		public void onError(String message) {
			if (!isFinish()) {
				CommonAndroid.showView(false, loading);
				SkyUtils.showDialog(getActivity(), "" + SkyUtils.convertStringErrF(message), null);
			}
		};

		public void onSucces(String response) {
			if (!isFinish()) {
				CommonAndroid.showView(false, loading);
				JSONObject mainJsonObject;
				try {
					mainJsonObject = new JSONObject(response);
					String is_succes = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.is_success);
					String err_msg = CommonAndroid.getString(mainJsonObject,
							SkyUtils.KEY.err_msg);
					String update_done = getActivity().getResources().getString(R.string.msg_success);
					if( !new Setting(getActivity()).isLangEng()){
						update_done = getActivity().getResources().getString(R.string.msg_success_j);
					}
					if (SkyUtils.VALUE.STATUS_API_SUCCESS.equals(is_succes)) {
						SkyUtils.showDialog(getActivity(), update_done ,new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Reset();
							}
						});
					}else{
						SkyUtils.showDialog(getActivity(), err_msg ,null);
					}
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
		
	}
	

}