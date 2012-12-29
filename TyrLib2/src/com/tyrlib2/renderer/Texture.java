package com.tyrlib2.renderer;

public class Texture {
	
	protected int handle;
	protected int resId;
	
	public Texture(int handle) {
		this.handle = handle;
		this.resId = -1;
	}
	
	public int getHandle()  {
		return handle;
	}
	
	public void setHandle(int handle) {
		this.handle = handle;
	}
}
