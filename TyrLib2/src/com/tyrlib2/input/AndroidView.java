package com.tyrlib2.input;

import android.view.View;

public class AndroidView implements IView {

	private View view;
	
	public AndroidView(View view) {
		this.view = view;
	}
	
	@Override
	public float getWidth() {
		return view.getWidth();
	}

	@Override
	public float getHeight() {
		return view.getHeight();
	}

}
