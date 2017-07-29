package com.tyrfing.games.tyrlib3.view.graphics.texture;

import com.tyrfing.games.tyrlib3.model.math.Vector2F;

public class Texture {
	
	protected int handle;
	protected int resId;
	public Vector2F size;
	
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
	
	public Vector2F getSize() {
		return size;
	}
	
	public int getResId() {
		return resId;
	}
	
	public void setResId(int resId) {
		this.resId = resId;
	}
}
