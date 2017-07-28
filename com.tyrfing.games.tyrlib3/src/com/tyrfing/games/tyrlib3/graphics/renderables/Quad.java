package com.tyrfing.games.tyrlib3.graphics.renderables;

import com.tyrfing.games.tyrlib3.graphics.renderer.Material;
import com.tyrfing.games.tyrlib3.graphics.renderer.Renderable;
import com.tyrfing.games.tyrlib3.math.Vector2F;
import com.tyrfing.games.tyrlib3.math.Vector3F;

/**
 * A non rotated rectangle
 * @author Sascha
 *
 */

public class Quad extends Renderable {
	
	public static final short[] DRAW_ORDER_QUAD = { 0, 1, 2, 1, 3, 2};
	
	public Quad(Material material, Vector2F min, Vector2F max) {
		
		Vector3F[] points = { 	new Vector3F(max.x, max.y, 0),
								new Vector3F(min.x, max.y, 0),				
								new Vector3F(max.x, min.y, 0),	
								new Vector3F(min.x, min.y, 0),
							 };
		init(material, points, DRAW_ORDER_QUAD);
	}
}
