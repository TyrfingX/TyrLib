package com.tyrfing.games.tyrlib3.graphics.compositors;

import com.tyrfing.games.tyrlib3.math.Vector2F;

public class FullQuad extends CompositQuad {
	
	public FullQuad() {
		super(new Vector2F[] { 	new Vector2F(-1, -1),
								new Vector2F(1, -1),
								new Vector2F(-1, 1),
								new Vector2F(1, 1) }
		);
	}
}
