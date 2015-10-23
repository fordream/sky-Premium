package org.com.atmarkcafe.sky.customviews.charting;

import org.com.atmarkcafe.sky.SkypeApplication;

import z.lib.base.CommonAndroid;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;

//com.acv.cheerz.customviews.charting.MEditText
public class MButton extends android.widget.Button {
	private String txtEng, txtJa;
	private int txtStyle = -1;
	private int txtFont = -1;
	private int txtTouch = -1;
	private Setting setting;
	public MButton(Context context,AttributeSet attrs) {
		super(context,attrs);
		setting = new Setting(getContext());
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MTextView, 0, 0);

		try {
			txtEng = a.getString(R.styleable.MTextView_txtEng);
			txtJa = a.getString(R.styleable.MTextView_txtJa);
			txtFont = a.getInteger(R.styleable.MTextView_txtFont, -1);
			txtStyle = a.getInteger(R.styleable.MTextView_txtStyle, -1);
			txtTouch = a.getInteger(R.styleable.MTextView_txtTouch, -1);
		} finally {
			a.recycle();
		}
		if (txtTouch != -1) {
			CommonAndroid.setAnimationOnClick(this);
		}
		try {
			((SkypeApplication) getContext().getApplicationContext()).setFonts(txtFont, this);
		} catch (Exception x) {

		}
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		try {
			String text = setting.isLangEng() ? txtEng : txtJa;

			if (!CommonAndroid.isBlank(text)) {
				setText(txtStyle == 0 ? String.format("<u>%s</u>", text) : text);
			}

		} catch (Exception exception) {
		}
		invalidate();
	}

}