package com.TyrLib2.PC.input;

import com.TyrLib2.PC.main.PCOpenGLSurfaceView;
import com.tyrlib2.input.IView;

public class PCView implements IView {
	
	private PCOpenGLSurfaceView view;
	
	public PCView(PCOpenGLSurfaceView pcOpenGLSurfaceView) {
		this.view = pcOpenGLSurfaceView;
	}
	
	@Override
	public float getWidth() {
		return view.getSize().x;
	}

	@Override
	public float getHeight() {
		return view.getSize().y;
	}

}
