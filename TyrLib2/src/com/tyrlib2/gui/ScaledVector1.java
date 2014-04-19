package com.tyrlib2.gui;

import com.tyrlib2.math.Vector2;

/**
 * Basic vector object implementing 2D vector math.
 * @author Sascha
 *
 */

public class ScaledVector1 {
	public float x;
	public ScaleDirection dir;
	
	public enum ScaleDirection {
		X, Y
	};
	
	public ScaledVector1(float x, ScaleDirection dir) { this.x = x; this.dir = dir; }
	public float get() { 
		if (dir == ScaleDirection.X) {
			return x * WindowManager.getInstance().getScale().x; 
		} else {
			return x * WindowManager.getInstance().getScale().y; 
		}
	}
	
	public ScaledVector1 multiply(float m)
	{
		return new ScaledVector1(x*m, dir);
	}
	
	
}
