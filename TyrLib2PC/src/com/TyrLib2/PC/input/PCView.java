package com.TyrLib2.PC.input;

import javax.media.opengl.awt.GLCanvas;

import com.tyrlib2.input.IView;

public class PCView implements IView {
	
	private GLCanvas view;
	
	public PCView(GLCanvas canvas) {
		this.view = canvas;
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
