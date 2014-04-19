package com.tyrlib2.gui;

import com.tyrlib2.math.Vector2;

/**
 * Basic vector object implementing 2D vector math.
 * @author Sascha
 *
 */

public class ScaledVector2 {
	public float x;
	public float y;
	
	public ScaledVector2(float x, float y) { this.x = x; this.y = y; }
	public ScaledVector2(ScaledVector2 other) { this.x = other.x; this.y = other.y; }
	public ScaledVector2() { this(0,0); }
	public Vector2 get() { 
		return new Vector2(x * WindowManager.getInstance().getScale().x, y * WindowManager.getInstance().getScale().y); 
	}
	
	public ScaledVector2 multiply(float m)
	{
		return new ScaledVector2(x*m, y*m);
	}
	
	public ScaledVector2 add(ScaledVector2 v)
	{
		return new ScaledVector2(x+v.x, y+v.y);
	}
	
	
}
