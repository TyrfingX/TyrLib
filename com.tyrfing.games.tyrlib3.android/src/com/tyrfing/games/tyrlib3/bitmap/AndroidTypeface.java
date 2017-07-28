package com.tyrfing.games.tyrlib3.bitmap;

import com.tyrfing.games.tyrlib3.bitmap.ITypeface;

import android.graphics.Typeface;

public class AndroidTypeface implements ITypeface {
	public final Typeface tf;
	
	public AndroidTypeface(Typeface tf) {
		this.tf = tf;
	}
}
