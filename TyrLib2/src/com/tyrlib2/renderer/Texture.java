package com.tyrlib2.renderer;

public class Texture {
	
	protected int handle;
	protected int resId;
	
	public Texture(int handle) {
		this.handle = handle;
	}
	
	public int getHandle()  {
		return handle;
	}
}
