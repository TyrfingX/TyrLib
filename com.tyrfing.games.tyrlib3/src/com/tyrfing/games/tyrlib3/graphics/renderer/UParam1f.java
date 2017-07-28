package com.tyrfing.games.tyrlib3.graphics.renderer;

import com.tyrfing.games.tyrlib3.util.ValueF;

public class UParam1f extends Param {

	public ValueF paramValue;
	
	public UParam1f(String name, ValueF value) {
		super(name);
		this.paramValue = value;
	}
	
	@Override
	public void set(int programHandle) {
		TyrGL.glUniform1f(paramHandle, paramValue.value);
	}
	
}
