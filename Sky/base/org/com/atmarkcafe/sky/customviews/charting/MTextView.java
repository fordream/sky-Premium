package org.com.atmarkcafe.sky.customviews.charting;

import org.com.atmarkcafe.sky.SkypeApplication;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.text.Html;
import android.util.AttributeSet;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;

//org.com.atmarkcafe.sky.customviews.charting.MTextView
public class MTextView extends android.widget.TextView {
	private String txtEng, txtJa, txtHintEng, txtHintJa;
	private int txtStyle = -1;
	private int txtFont = -1;
	private Setting setting;
	private int txtTouch = -1;

	public MTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setting = new Setting(getContext());
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MTextView, 0, 0);
		setText("   ");
		try {
			txtEng = a.getString(R.styleable.MTextView_txtEng);
			txtJa = a.getString(R.styleable.MTextView_txtJa);
			txtHintEng = a.getString(R.styleable.MTextView_txtHintEng);
			txtHintJa = a.getString(R.styleable.MTextView_txtHintJa);
//			txtFont = a.getInteger(R.styleable.MTextView_txtFont, -1);
			txtStyle = a.getInteger(R.styleable.MTextView_txtStyle, -1);
			txtTouch = a.getInteger(R.styleable.MTextView_txtTouch, -1);
			//Typeface face = Typeface.createFromAsset(context.getAssets(),"fonts/epimodem.ttf");
//			this.setTypeface(face);
		} finally {
			a.recycle();
		}
		try {
			((SkypeApplication) getContext().getApplicationContext()).setFonts(txtFont, this);
		} catch (Exception x) {

		}

		if (txtTouch != -1) {
			CommonAndroid.setAnimationOnClick(this);
		}
	}

	public void setTxtEng(String txtEng) {
		this.txtEng = txtEng;
	}

	public void setTxtJa(String txtJa) {
		this.txtJa = txtJa;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		try {
			String text = setting.isLangEng() ? txtEng : txtJa;

			if (CommonAndroid.isBlank(txtEdt)) {
				if (!CommonAndroid.isBlank(text)) {
					// setText(txtStyle == 0 ?
					// Html.fromHtml(String.format("<u>%s</u>", text)) : text);
				}
			} else {
				// setText(txtStyle == 0 ?
				// Html.fromHtml(String.format("<u>%s</u>", txtEdt + "")) :
				// Html.fromHtml(txtEdt + ""));

				text = txtEdt;
			}

			if (!CommonAndroid.isBlank(text) && !text.equals(curentText)) {
				curentText = text;
				setText(txtStyle == 0 ? Html.fromHtml(String.format("<u>%s</u>", text)) : Html.fromHtml(text));
			}

			if (isEnglish != setting.isLangEng()) {
				setHint(setting.isLangEng() ? txtHintEng : txtHintJa);

				isEnglish = setting.isLangEng();
			}
		} catch (Exception exception) {
		}
		invalidate();
	}

	private boolean isEnglish = true;
	private String curentText = null;
	private String txtEdt = null;

	public void setTextEdit(String txtEdt) {
		this.txtEdt = txtEdt;
	}
}