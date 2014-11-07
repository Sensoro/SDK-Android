package com.sensoro.experience.tool;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class TTFIcon extends TextView {

	Typeface font;

	public TTFIcon(Context context, AttributeSet attrs) {
		super(context, attrs);
		font = Typeface.createFromAsset(context.getAssets(), "fontawesome-webfont.ttf");
		this.setTypeface(font);
	}
}
