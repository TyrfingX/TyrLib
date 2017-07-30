package com.tyrlib2.graphics.compositors;

import com.tyrlib2.math.Vector2;

public class FullQuad extends CompositQuad {
	
	public FullQuad() {
		super(new Vector2[] { 	new Vector2(-1, -1),
								new Vector2(1, -1),
								new Vector2(-1, 1),
								new Vector2(1, 1) }
		);
	}
}
