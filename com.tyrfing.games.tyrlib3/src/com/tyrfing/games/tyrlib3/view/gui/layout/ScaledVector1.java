package com.tyrfing.games.tyrlib3.view.gui.layout;

import com.tyrfing.games.tyrlib3.view.gui.WindowManager;

/**
 * Basic vector object implementing 2D vector math.
 * @author Sascha
 *
 */

public class ScaledVector1 {
	public float x;
	public int scaleIndex;
	public ScaleDirection dir;
	
	public enum ScaleDirection {
		X, Y
	};
	
	public ScaledVector1(float x, ScaleDirection dir) { this.x = x; this.dir = dir; }
	public ScaledVector1(float x, ScaleDirection dir, int scaleIndex) { this.x = x; this.dir = dir; this.scaleIndex = scaleIndex; }
	public float get() { 
		if (dir == ScaleDirection.X) {
			return x * WindowManager.getInstance().getScale(scaleIndex).x; 
		} else {
			return x * WindowManager.getInstance().getScale(scaleIndex).y; 
		}
	}
	
	public ScaledVector1 multiply(float m)
	{
		return new ScaledVector1(x*m, dir, scaleIndex);
	}
	
	
}
