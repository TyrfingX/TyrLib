package com.tyrlib2.gui;

import java.io.Serializable;

import com.tyrlib2.math.Vector2;

/**
 * Basic vector object implementing 2D vector math.
 * @author Sascha
 *
 */

public class ScaledVector2 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6489101662972809135L;
	
	public float x;
	public float y;
	public int scaleIndex;
	
	public ScaledVector2(float x, float y, int scaleIndex){ this.x = x; this.y = y; this.scaleIndex = scaleIndex; }
	public ScaledVector2(float x, float y) { this.x = x; this.y = y; }
	public ScaledVector2(ScaledVector2 other) { this.x = other.x; this.y = other.y; this.scaleIndex = other.scaleIndex; }
	public ScaledVector2() { this(0,0); }
	public Vector2 get() { 
		return new Vector2(	x * WindowManager.getInstance().getScale(scaleIndex).x, 
							y * WindowManager.getInstance().getScale(scaleIndex).y); 
	}
	
	public Vector2 get(int scaleIndex) { 
		return new Vector2(	x * WindowManager.getInstance().getScale(scaleIndex).x, 
							y * WindowManager.getInstance().getScale(scaleIndex).y); 
	}
	
	public ScaledVector2 multiply(float m)
	{
		return new ScaledVector2(x*m, y*m, scaleIndex);
	}
	
	public ScaledVector2 add(ScaledVector2 v)
	{
		return new ScaledVector2(x+v.x, y+v.y, scaleIndex);
	}
	
	
}
