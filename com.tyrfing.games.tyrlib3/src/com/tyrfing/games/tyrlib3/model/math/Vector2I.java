package com.tyrfing.games.tyrlib3.model.math;

import java.io.Serializable;

public class Vector2I implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3849208536566898374L;
	public static Vector2I UNIT_X = new Vector2I(1, 0);
	public static Vector2I UNIT_Y = new Vector2I(0, 1);
	public static Vector2I NEGATIVE_UNIT_X = new Vector2I(-1, 0);
	public static Vector2I NEGATIVE_UNIT_Y = new Vector2I(0, -1);
	public static Vector2I ZERO = new Vector2I(0, 0);
	public static Vector2I[] UNIT_VECTORS = { UNIT_X, UNIT_Y, NEGATIVE_UNIT_X, NEGATIVE_UNIT_Y };
	
	public int x, y;
	
	public Vector2I() {
		
	}
	
	public Vector2I(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2I vectorTo(Vector2I other)
	{
		return new Vector2I(other.x - this.x, other.y - this.y);
	}
	
	public Vector2I add(Vector2I other)
	{
		return new Vector2I(other.x + this.x, other.y + this.y);
	}
	
	public int length() {
		return (int) Math.sqrt(this.x * this.x + this.y * this.y);
	}
	
	public int abs() {
		return Math.abs(x) + Math.abs(y);
	}
	
	public int normalize() {
		int length = this.length();
		if (length != 0)
		{
			x /= length;
			y /= length;
		}
		return length;
	}
	
	public static void add(Vector2I v1, Vector2I v2, Vector2I target) {
		target.x = v1.x + v2.x;
		target.y = v1.y + v2.y;
	}
	
	public Vector2I mapToUnitAxis() {
		if (Math.abs(x) >= Math.abs(y)) {
			if (x > 0) {
				return UNIT_X;
			} else {
				return NEGATIVE_UNIT_X;
			}
		} else {
			if (y > 0) {
				return UNIT_Y;
			} else {
				return NEGATIVE_UNIT_Y;
			}
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Vector2I) {
			Vector2I vector = (Vector2I) obj;
			return x == vector.x && y == vector.y;
		}
		
		return super.equals(obj);
	}
	
	@Override
	public String toString() {
		return x + ", " +  y;
	}
}
