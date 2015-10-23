package org.com.atmarkcafe.sky.fragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.com.atmarkcafe.sky.GlobalFunction;
import org.com.atmarkcafe.sky.SkyMainActivity;
import org.com.atmarkcafe.sky.customviews.charting.MEditText;
import org.com.atmarkcafe.sky.customviews.charting.MTextView;
import org.com.atmarkcafe.sky.fragment.ContactListFragment.ContactListObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import z.lib.base.BaseFragment;
import z.lib.base.CheerzServiceCallBack;
import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import z.lib.base.SkyUtils;
import z.lib.base.callback.RestClient.RequestMethod;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Rect;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.base.view.LoadingView;
import com.acv.cheerz.db.Setting;

@SuppressLint("ValidFragment")
public class ContactInboxFragment extends BaseFragment {

	private String mTitle, mCatalog, mTitleParent;
	private ImageButton btnBack;
	private MTextView headerTitleView;
	private ImageButton btnRight;
	private MTextView txtCatalog;
	private MEditText txtTitleContent;
	private MEditText txtContent;
	private Button btnSend;
	private ListView listView;
	private LinearLayout ll_listHistory;
	private char[] contactType;
	private LoadingView loading;
	private String txtContentMsg = "";
	private String txtTitleMsg = "";
	private String TAG = "ContactInboxFragment";
	private ContactListObject contactObject;
	private TextView txtContentHistory, txtTimeHistory;
	private MTextView txtTitleHistory;
	private boolean languageId;
	private Setting setting;
	private RelativeLayout parentView;
	private String quoteStr = ">";
	private MTextView txtQuote, txtConfirmReply, txtCreate;
	private List<HistoryContent> listHistoryContents;
	private LinearLayout btnReply, btnCreateContact;
	private MEditText edtReply;
	private LinearLayout llReply;
	private EditText txtHistory;
	private ScrollView main_scroll;
	private LinearLayout main_bottom;
	private Boolean showKeyboard = false;
	private Boolean showKeyboard_2 = false;
	private RelativeLayout.LayoutParams params;
	private View views;
	private ScrollView main_list;
	private int top2_temp;
	public ContactInboxFragment(ContactListObject obj) {
		super();
		this.contactObject = obj;
	}

	@Override
	public int getLayout() {

		// return R.layout.contactus_inbox_layout;
		return R.layout.v3_contactus_inbox_layout;
	}

	@Override
	public void init(View view) {
		views = view;
		final int top1_temp = (int) getActivity().getResources().getDimension(R.dimen.dimen_100dp);
		top2_temp = (int) getActivity().getResources().getDimension(R.dimen.dimen_10dp);
		final int height_temp = (int) getActivity().getResources().getDimension(R.dimen.dimen_450dp);
		final int height_temp_2 = (int) getActivity().getResources().getDimension(R.dimen.dimen_230dp);
		main_list = (ScrollView) view.findViewById(R.id.main_list);
		main_scroll = (ScrollView) view.findViewById(R.id.main_scroll);
		main_bottom = (LinearLayout) view.findViewById(R.id.main_bottom);
		params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        final View activityRootView = view.findViewById(R.id.contact_content_layout_id);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
        	if(showKeyboard_2){
        		 Rect r = new Rect();
                 //r will be populated with the coordinates of your view that area still visible.
                 activityRootView.getWindowVisibleDisplayFrame(r);

                 int heightDiff = activityRootView.getRootView().getHeight() - (r.bottom - r.top);
                 if (heightDiff > 100) { // if more than 100 pixels, its probably a keyboard...
                 	if(!showKeyboard){
                 		 LogUtils.i(TAG, "aaaaaaaaaaaaaaaaaaa");
                          main_scroll.setLayoutParams(new  LayoutParams(LayoutParams.WRAP_CONTENT, height_temp ));
                          params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
                          params.topMargin = top1_temp;
                          main_bottom.setLayoutParams(params);
                          showKeyboard = true;
                 	}
                    
                 }else{
                 	if(showKeyboard){
                 		LogUtils.i(TAG, "bbbbbbbbbbb");
                     	main_scroll.setLayoutParams(new  LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                        params.topMargin = top2_temp;
                        main_bottom.setLayoutParams(params);
                        showKeyboard = false;
                 	}
                 	
                 }
        	}
           
         }
        }); 
        
//		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.btn_checkout, getResources().getColor(R.color.ec_btn_checkout));//OK
		CommonAndroid.changeColorImageView(getActivity(), R.drawable.btn_continue, getResources().getColor(R.color.ec_btn_continue));//OK
		setting = new Setting(getActivity());
		languageId = setting.isLangEng();
		loading = (LoadingView) view.findViewById(R.id.loading);
		parentView = (RelativeLayout) view.findViewById(R.id.parrent_id);
		
		CommonAndroid.showView(false, loading);

		btnBack = (ImageButton) view.findViewById(R.id.header_btn_left);
		btnBack.setImageResource(R.drawable.icon_back);
		btnBack.setOnClickListener(this);

		headerTitleView = (MTextView) view.findViewById(R.id.header_title);
		headerTitleView.setText("" + contactObject.getTitle());

		btnRight = (ImageButton) view.findViewById(R.id.header_btn_right);
		btnRight.setVisibility(View.INVISIBLE);
		btnReply = (LinearLayout) view.findViewById(R.id.contactus_btn_reply_id);
		btnReply.setOnClickListener(this);
		txtCatalog = (MTextView) view.findViewById(R.id.contact_header_catalog_text_id);
		btnCreateContact = (LinearLayout) view.findViewById(R.id.contactus_btn_create_id);
		btnCreateContact.setOnClickListener(this);
		llReply = (LinearLayout) view.findViewById(R.id.ll_reply_id);
		llReply.setVisibility(View.GONE);

		edtReply = (MEditText) view.findViewById(R.id.edt_reply_id);
		ll_listHistory = (LinearLayout) view.findViewById(R.id.contact_inbox_history_id);
		txtConfirmReply = (MTextView) view.findViewById(R.id.contactus_txt_reply_id);
//		txtConfirmReply.setOnClickListener(this);
		txtHistory = (EditText) view.findViewById(R.id.txt_history_id);
//		GlobalFunction.handlerKeyboardOnView(getActivity(), parentView);
		getDataReply();
		
//		Window window = getActivity().getWindow();
//		window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE |WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		
		// editor reply
		edtReply.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				txtContentMsg = s.toString();
				LogUtils.i(TAG, "text reply : " + txtContentMsg);
				if (setting.isLangEng()) {
					if (txtContentMsg.equalsIgnoreCase("")) {
						txtConfirmReply.setTextEdit(getResources().getString(R.string.tlt_cancel));
					}else{
						txtConfirmReply.setTextEdit(getResources().getString(R.string.tlt_send));	
					}
					
				} else {
					if (txtContentMsg.equalsIgnoreCase("")) {
						txtConfirmReply.setTextEdit(getResources().getString(R.string.tlt_cancel_j));
					}else{
						txtConfirmReply.setTextEdit(getResources().getString(R.string.tlt_send_j));
					}
				}

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				try {
					String temp = s.toString();
					if (temp.equalsIgnoreCase("")) {
						InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					    keyboard.hideSoftInputFromWindow(getView().getWindowToken(), 0);
					}
				} catch (Exception e) {
					InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				    keyboard.hideSoftInputFromWindow(getView().getWindowToken(), 0);
				}
			}
		});
		
	}

	/*private void addCatalogType(JSONObject jsonData) {
		try {
			JSONObject jsonCate = jsonData.getJSONObject("reply_contact_type");
			txtCatalog.setText(jsonCate.getString("name"));
			txtTitleHistory.setText(jsonData.getString("reply_contact_title"));
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}*/

	@Override
	public void onClick(View v) {
		super.onClick(v);
		int idView = v.getId();
		switch (idView) {
		/*case R.id.contact_btn_confirm_id:
			// get all infor and send
			showKeyboard_2 = true;
			txtTitleMsg = txtTitleContent.getText().toString().trim();
			txtContentMsg = txtContent.getText().toString().trim();

			if (txtContentMsg.equalsIgnoreCase("")
					| txtTitleMsg.equalsIgnoreCase("")) {
				if (setting.isLangEng()) {
					SkyUtils.showDialog(
							getActivity(),
							getActivity().getResources().getString(
									R.string.contact_content_require), null);
				} else {
					SkyUtils.showDialog(
							getActivity(),
							getActivity().getResources().getString(
									R.string.contact_content_require_j), null);
				}
			} else {
				postDataToServer();
			}
			break;*/
		case R.id.header_btn_left:
			ContactListFragment contactListFragment = new ContactListFragment();
			if (getActivity() instanceof SkyMainActivity) {
				SkyMainActivity main = (SkyMainActivity) getActivity();
				main.switchContent(contactListFragment, "");
			}
			// onBackPressed(null);
			break;
		case R.id.contactus_btn_create_id:
			ContactFragment contactFragment = new ContactFragment();
			switchFragment(contactFragment);
			break;
		case R.id.contactus_btn_reply_id:
			resetLayout();
			txtContentMsg = "";
			LogUtils.i(TAG, "1111111111111 ==== " + txtContentMsg);
			txtContentMsg = edtReply.getText().toString().trim();
			if (!txtContentMsg.equalsIgnoreCase("")) {
				postDataToServer();
			} else {
				if (setting.isLangEng()) {
					txtConfirmReply.setTextEdit(getResources().getString(
							R.string.tlt_cancel));
				} else {
					txtConfirmReply.setTextEdit(getResources().getString(
							R.string.tlt_cancel_j));
				}
				openReply();

			}

			break;
		/*case R.id.contactus_txt_reply_id:
			resetLayout();
            
			LogUtils.i(TAG, "222222222222 ==== " + txtContentMsg);
			txtContentMsg = edtReply.getText().toString().trim();
			if (!txtContentMsg.equalsIgnoreCase("")) {
				postDataToServer();
			} else {
//				if (setting.isLangEng()) {
//					txtConfirmReply.setTextEdit(getResources().getString(
//							R.string.tlt_cancel));
//				} else {
//					txtConfirmReply.setTextEdit(getResources().getString(
//							R.string.tlt_cancel_j));
//				}
				openReply();
			}

			break;*/
		default:
			break;
		}

	}

	protected void switchFragment(Fragment newFragment) {
		if (getActivity() instanceof SkyMainActivity) {
			SkyMainActivity fca = (SkyMainActivity) getActivity();
			fca.switchContent(newFragment, "");
		}
	}

	private void openReply() {
		showKeyboard_2 = true;
		resetLayout();
		if (llReply.getVisibility() == View.VISIBLE) {
			llReply.setVisibility(View.GONE);
			if (setting.isLangEng()) {
				txtConfirmReply.setText(getResources().getString(R.string.contact_us_reply));
			} else {
				txtConfirmReply.setText(getResources().getString(R.string.contact_us_reply_j));
			}
		} else {
			txtHistory.setText(txtTitleMsg);
			if (setting.isLangEng()) {
				txtConfirmReply.setText(getResources().getString(R.string.tlt_cancel));
			} else {
				txtConfirmReply.setText(getResources().getString(R.string.tlt_cancel_j));
			}
			llReply.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onChangeLanguage() {
		languageId = setting.isLangEng();
		getDataReply();
	}

	private void postDataToServer() {
		showKeyboard_2 = false;
		resetLayout();
		JSONObject objParam = new JSONObject();

		txtTitleMsg = txtHistory.getText().toString().trim();
		txtContentMsg = edtReply.getText().toString().trim();
		LogUtils.i(TAG, "======== string txtTitleMsg = " + txtTitleMsg);
		LogUtils.i(TAG, "======== string txtContentMsg = " + txtContentMsg);
		
		if (txtContentMsg.equalsIgnoreCase("")) {
			if (setting.isLangEng()) {
				SkyUtils.showDialog(getActivity(), getActivity().getResources()
						.getString(R.string.contact_content_require), null);
			} else {
				SkyUtils.showDialog(getActivity(), getActivity().getResources()
						.getString(R.string.contact_content_require_j), null);
			}
			
		}else if(txtTitleMsg.equalsIgnoreCase("")){
			if (setting.isLangEng()) {
				SkyUtils.showDialog(getActivity(), getActivity().getResources()
						.getString(R.string.contact_title_require), null);
			} else {
				SkyUtils.showDialog(getActivity(), getActivity().getResources()
						.getString(R.string.contact_title_require_j), null);
			}
		} else {
			llReply.setVisibility(View.GONE);
			try {
				objParam.put("contact_id", contactObject.getContactId());
				objParam.put("contact_type", contactObject.getContactType());
				
				objParam.put("content", txtContentMsg);
				objParam.put("title", txtTitleMsg);
				SkyUtils.execute(getActivity(), RequestMethod.POST,
						SkyUtils.API.API_USER_CONTACT_DETAIL,
						SkyUtils.paramRequest(getActivity(), objParam),
						new CheerzServiceCallBack() {
							@Override
							public void onStart() {
								CommonAndroid.showView(true, loading);
								super.onStart();
							}

							@Override
							public void onError(String message) {
								CommonAndroid.showView(false, loading);
								if (languageId) {
									SkyUtils.showDialog(
											getActivity(),
											getResources().getString(
													R.string.msg_failure), null);
								} else {
									SkyUtils.showDialog(
											getActivity(),
											getResources().getString(
													R.string.msg_failure_j),
											null);
								}
								super.onError(message);
							}

							@Override
							public void onSucces(String response) {
								CommonAndroid.showView(false, loading);
								try {
									if (response != null) {
										LogUtils.i(TAG, "data respone"
												+ response);
										JSONObject jsonResObject = new JSONObject(
												response);
										int flagSuccess = jsonResObject
												.getInt("is_success");
										LogUtils.i(TAG, "is_success = "
												+ flagSuccess);
										showAlertSendMsg(flagSuccess);
									}
								} catch (Exception e) {
									e.printStackTrace();
								}
								super.onSucces(response);
							}

						});
			} catch (Exception e) {
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		ContactListFragment contactListFragment = new ContactListFragment();
		if (getActivity() instanceof SkyMainActivity) {
			SkyMainActivity main = (SkyMainActivity) getActivity();
			main.switchContent(contactListFragment, "");
		}
		super.onBackPressed();
	}
	
	/*private String convertStreamToString(InputStream is) {
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}*/

	int touched = 1;

	private void getDataReply() {
		llReply.setVisibility(View.GONE);
//		if (setting.isLangEng()) {
//			txtConfirmReply.setText(getResources().getString(
//					R.string.contact_us_reply));
//		} else {
//			txtConfirmReply.setText(getResources().getString(
//					R.string.contact_us_reply_j));
//		}
		
		ll_listHistory.removeAllViews();
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("req[param][contact_id]", contactObject.getContactId());
		SkyUtils.execute(getActivity(), RequestMethod.GET,
				SkyUtils.API.API_USER_CONTACT_DETAIL, params,
				new CheerzServiceCallBack() {

					@Override
					public void onStart() {
						CommonAndroid.showView(true, loading);
						super.onStart();
					}

					@Override
					public void onError(String message) {
						LogUtils.i(TAG, "error = " + message);
						CommonAndroid.showView(false, loading);
						if (languageId) {
							SkyUtils.showDialog(getActivity(), getResources()
									.getString(R.string.msg_failure), null);
						} else {
							SkyUtils.showDialog(getActivity(), getResources()
									.getString(R.string.msg_failure_j), null);
						}
						super.onError(message);
					}

					@Override
					public void onSucces(String response) {
						super.onSucces(response);
						CommonAndroid.showView(false, loading);
						LogUtils.i(TAG, "success = " + response);

						try {
							JSONObject jsonRes = new JSONObject(response);
							JSONObject jsonData = jsonRes.getJSONObject("data");
							JSONArray jsonArray = jsonData
									.getJSONArray("threads");

							if (jsonData.has("reply_contact_type")) {
								JSONObject jsonCate = jsonData
										.getJSONObject("reply_contact_type");
								txtCatalog.setText(jsonCate.getString("name"));
//								txtTitleMsg = jsonData
//										.getString("reply_contact_title");
							}

							listHistoryContents = new ArrayList<ContactInboxFragment.HistoryContent>();
							for (int i = 0; i < jsonArray.length(); i++) {
								LogUtils.i(TAG, "Read json");
								JSONObject jObjectI = jsonArray
										.getJSONObject(i);
								HistoryContent object = new HistoryContent();
								object.setTitle(jObjectI.getString("title"));
								object.setContent(jObjectI.getString("content"));
								object.setCreated_by(jObjectI
										.getString("created_by"));
								object.setTime(jObjectI.getString("time"));
								txtTitleMsg = object.getTitle();
								if (jObjectI.has("question")) {
									object.setQuestionFlag(jObjectI
											.getInt("question"));
								} else {
									object.setQuestionFlag(1);
								}
								listHistoryContents.add(object);

								LogUtils.i(TAG, "addview : " + i + "==" + txtTitleMsg);
							}
							if (setting.isLangEng()) {
								txtConfirmReply.setText(getResources().getString(
										R.string.contact_us_reply));
							} else {
								txtConfirmReply.setText(getResources().getString(
										R.string.contact_us_reply_j));
							}
							updateLayoutHistory();
						} catch (Exception e) {
						}
					}

				});
	}

	private void updateLayoutHistory() {
		resetLayout();
		LogUtils.i(TAG, "updateLayoutHistory");
		LayoutInflater inf = getActivity().getLayoutInflater();
		for (int i = 0; i < listHistoryContents.size(); i++) {
			LogUtils.i(TAG, "updateLayoutHistory : " + i);
			HistoryContent objContent = listHistoryContents.get(i);
			View viewChild = null;
			if (objContent.getQuestionFlag() == 0) {
				viewChild = inf.inflate(R.layout.answer_layout, null);
			} else {
				viewChild = inf.inflate(R.layout.question_layout, null);
			}
			TextView txtTitle = (TextView) viewChild
					.findViewById(R.id.qs_title_id);
			TextView txtContent = (TextView) viewChild
					.findViewById(R.id.qs_content_id);
			txtTitle.setText(objContent.getTitle());
			txtContent.setText(objContent.getContent());
			// txtTitleMsg = objContent.getTitle();
			ll_listHistory.addView(viewChild);

		}
		
		main_list.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
		    @Override
		    public void onGlobalLayout() {
		    	main_list.post(new Runnable() {
		            public void run() {
		            	main_list.fullScroll(View.FOCUS_DOWN);
		            }
		        });
		    }
		});
	}

	private void resetLayout() {
		// TODO Auto-generated method stub
		main_scroll.setLayoutParams(new  LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.topMargin = top2_temp;
        main_bottom.setLayoutParams(params);
        InputMethodManager keyboard = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        keyboard.hideSoftInputFromWindow(views.getWindowToken(), 0);
	}

	/**
	 * validate content of edittext
	 * 
	 * @param edt
	 * @return true | false
	 */
	/*private boolean validateInputText(EditText edt) {
		boolean valid = false;
		String content = edt.getText().toString().trim();
		if (content != null) {
			valid = true;
		} else {
			valid = false;
		}
		return valid;
	}

	private void addRef(JSONObject jsonData) {

	}*/

	private boolean flagOpenLoading = true;

	private void showAlertSendMsg(final int flagSuccess) {
		CommonAndroid.showView(false, loading);
		String msg = "";
		if (flagSuccess == 1) {
			if (languageId) {
				msg = getActivity().getResources().getString(
						R.string.msg_success);
			} else {
				msg = getActivity().getResources().getString(
						R.string.msg_success_j);
			}
		} else {
			if (languageId) {
				msg = getActivity().getResources().getString(
						R.string.msg_failure);
			} else {
				msg = getActivity().getResources().getString(
						R.string.msg_failure_j);
			}
		}
		SkyUtils.showDialog(getActivity(), msg, new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (flagSuccess == 1) {
					flagOpenLoading = false;
					if (setting.isLangEng()) {
						txtConfirmReply.setText(getResources().getString(
								R.string.contact_us_reply));
					} else {
						txtConfirmReply.setText(getResources().getString(
								R.string.contact_us_reply_j));
					}
					edtReply.setText("");
					getDataReply();
					// txtContent.setText(" ");
					// txtTitleContent.setText("RE: "+contactObject.getTitle());
				}
			}
		});
		//
		// AlertDialog.Builder alertBuilder = new AlertDialog.Builder(
		// getActivity());
		// alertBuilder.setTitle(getResources().getString(
		// R.string.menu_sky_premium));
		// alertBuilder.setIcon(R.drawable.ic_launcher);
		// if (flagSuccess == 1) {
		// if (languageId) {
		// alertBuilder.setMessage(R.string.msg_success);
		// } else {
		// alertBuilder.setMessage(R.string.msg_success_j);
		// }
		// } else {
		// if (languageId) {
		// alertBuilder.setMessage(R.string.msg_failure);
		// } else {
		// alertBuilder.setMessage(R.string.msg_failure_j);
		// }
		// }
		// String strYes = null;
		// if (languageId) {
		// strYes = getResources().getString(R.string.msg_yes);
		// } else {
		// strYes = getResources().getString(R.string.msg_yes_j);
		// }
		// alertBuilder.setPositiveButton(strYes,
		// new DialogInterface.OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// if (flagSuccess == 1) {
		// getDataReply();
		// }
		// }
		// });
		//
		// Dialog dialog = alertBuilder.create();
		// dialog.show();
	}

	
	private class HistoryContent {
		private String title;
		private String content;
		private String created_by;
		private String time;
		private int questionFlag;

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getContent() {
			return content;
		}

		public void setContent(String content) {
			this.content = content;
		}

		public String getCreated_by() {
			return created_by;
		}

		public void setCreated_by(String created_by) {
			this.created_by = created_by;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public int getQuestionFlag() {
			return questionFlag;
		}

		public void setQuestionFlag(int questionFlag) {
			this.questionFlag = questionFlag;
		}

	}
	
	/*public static void hideSoftKeyboard(Activity activity) {
	    InputMethodManager inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	}
	
	public void setupUI(View view,final Activity mContext) {

	    //Set up touch listener for non-text box views to hide keyboard.
	    if(!(view instanceof EditText)) {

	        view.setOnTouchListener(new OnTouchListener() {

	            public boolean onTouch(View v, MotionEvent event) {
	                hideSoftKeyboard(mContext);
	                return false;
	            }

	        });
	    }

	    //If a layout container, iterate over children and seed recursion.
	    if (view instanceof ViewGroup) {

	        for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {

	            View innerView = ((ViewGroup) view).getChildAt(i);

	            setupUI(innerView,mContext);
	        }
	    }
	}*/
}
