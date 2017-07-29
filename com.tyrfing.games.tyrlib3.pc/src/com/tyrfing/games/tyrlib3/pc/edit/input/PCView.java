package com.tyrfing.games.tyrlib3.pc.edit.input;

import com.tyrfing.games.tyrlib3.edit.input.IView;
import com.tyrfing.games.tyrlib3.pc.main.PCOpenGLSurfaceView;

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
