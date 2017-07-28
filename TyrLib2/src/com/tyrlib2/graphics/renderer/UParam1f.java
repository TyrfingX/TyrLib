package com.tyrlib2.graphics.renderer;

import com.tyrlib2.util.Valuef;

public class UParam1f extends Param {

	public Valuef paramValue;
	
	public UParam1f(String name, Valuef value) {
		super(name);
		this.paramValue = value;
	}
	
	@Override
	public void set(int programHandle) {
		TyrGL.glUniform1f(paramHandle, paramValue.value);
	}
	
}
