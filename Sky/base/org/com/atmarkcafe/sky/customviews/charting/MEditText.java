package org.com.atmarkcafe.sky.customviews.charting;

import org.com.atmarkcafe.sky.SkypeApplication;

import z.lib.base.CommonAndroid;
import z.lib.base.LogUtils;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.SkyPremiumLtd.SkyPremium.R;
import com.acv.cheerz.db.Setting;

//org.com.atmarkcafe.sky.customviews.charting.MEditText
public class MEditText extends android.widget.EditText {
	private String txtEng, txtJa, txtHintEng, txtHintJa;
	private int txtStyle = -1;
	private int txtFont = -1;
	private int txtTouch = -1;
	private Setting setting;
	private KeyImeChange keyImeChangeListener;
	public MEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		setting = new Setting(getContext());
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MTextView, 0, 0);
		try {
			txtEng = a.getString(R.styleable.MTextView_txtEng);
			txtJa = a.getString(R.styleable.MTextView_txtJa);
			txtHintEng = a.getString(R.styleable.MTextView_txtHintEng);
			txtHintJa = a.getString(R.styleable.MTextView_txtHintJa);
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
		setHint(" ");
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		try {
			String text = setting.isLangEng() ? txtEng : txtJa;

			if (!CommonAndroid.isBlank(text)) {
				setText(txtStyle == 0 ? String.format("<u>%s</u>", text) : text);
			}

			setHint(setting.isLangEng() ? txtHintEng : txtHintJa);
		} catch (Exception exception) {
		}
		invalidate();
	}
	
    public interface KeyImeChange {
        public void onKeyIme(int keyCode, KeyEvent event);
    }
	
	@Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		LogUtils.e("AAAAAAAAAA", "BBBBBBBB" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // User has pressed Back key. So hide the keyboard
            if(keyImeChangeListener != null){
                keyImeChangeListener.onKeyIme(keyCode, event);
            }    
        }
        return false;
	}

	public void setKeyImeChangeListener(KeyImeChange keyImeChange) {
		// TODO Auto-generated method stub
		keyImeChangeListener = keyImeChange;
	}   
}