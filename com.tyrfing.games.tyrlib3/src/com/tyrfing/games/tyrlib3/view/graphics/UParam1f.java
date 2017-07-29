package com.tyrfing.games.tyrlib3.view.graphics;

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
