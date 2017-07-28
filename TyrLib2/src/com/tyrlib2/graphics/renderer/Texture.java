package com.tyrlib2.graphics.renderer;

import com.tyrlib2.math.Vector2;

public class Texture {
	
	protected int handle;
	protected int resId;
	public Vector2 size;
	
	public Texture() {
		this.handle = -1;
		this.resId = -1;
	}
	
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
	
	public Vector2 getSize() {
		return size;
	}
}
