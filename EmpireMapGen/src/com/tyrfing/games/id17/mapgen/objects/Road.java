package com.tyrfing.games.id17.mapgen.objects;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import com.tyrfing.games.id17.colors.Color4;
import com.tyrfing.games.id17.mapgen.zones.Area;
import com.tyrfing.games.id17.mapgen.zones.TileType;

public class Road {
	
	public final Area start;
	public final Area end;
	public final Line2D.Double line;
	
	public Road(Area start, Area end) {
		this.start = start;
		this.end = end;
		line = new Line2D.Double(start.generator, end.generator);
	}
	
	public void render(Color4[][] tileMap) {
		Rectangle2D.Double r = new Rectangle2D.Double(0, 0, 1.0, 1.0);
		for (int x = 0; x < tileMap.length; ++x) {
			for (int y = 0; y < tileMap[x].length; ++y) {{
					if (tileMap[x][y].a == 255) { 		
						r.x = x-0.5f;
						r.y = y-0.5f;
						if (line.intersects(r)) {
							tileMap[x][y].r = TileType.SOIL.getInt() | (tileMap[x][y].r & 0x02) ;
						}
					}
				}
			}
		}
	}
}
