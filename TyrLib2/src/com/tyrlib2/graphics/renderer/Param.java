package com.tyrlib2.graphics.renderer;

public abstract class Param {
	public String name;
	public int paramHandle;
	
	public Param(String name) {
		this.name = name;
	}
	
	public void setProgram(int programHandle) {
		paramHandle = TyrGL.glGetUniformLocation(programHandle, name);
	}
	
	public abstract void set(int programHandle);
}
