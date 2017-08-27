package com.tyrfing.games.id17.world;

import com.tyrfing.games.id17.holdings.Barony;
import com.tyrlib2.bitmap.ICanvas;
import com.tyrlib2.bitmap.IDrawableBitmap;
import com.tyrlib2.graphics.renderer.Texture;
import com.tyrlib2.graphics.renderer.TextureManager;
import com.tyrlib2.main.Media;
import com.tyrlib2.math.Vector3;
import com.tyrlib2.util.Color;

public class FogMap {
	
	private WorldMap map;
	private String textureName;
	
	private ICanvas canvas;
	private IDrawableBitmap bitmap;
	
	public static final Color FOGGED_COLOR = new Color(0,0,0,1);
	public static final Color EXPLORED_COLOR = new Color(1,0,0,1);
	
	public FogMap(WorldMap map, String textureName) {
		this.map = map;
		this.textureName = textureName;
		
	    bitmap = Media.CONTEXT.createBitmap(map.width, map.height);
	    canvas = Media.CONTEXT.createCanvas(bitmap);	
	}
	
	public void init() {
		System.out.print("Initializing Fog Map... ");
	    for (int i = 0; i < World.getInstance().getCountBaronies(); ++i) {
	    	Barony b = World.getInstance().getBarony(i);
	    	updateFog(b);
	    }
		System.out.println(" ... finished initializing fog map.");
	}
	
	public void setFog(int x, int y, Color color) {
		canvas.setRGB(x, y, color);
	}
	
	public void updateFog(Barony barony) {
		WorldChunk chunk = barony.getWorldChunk();
		
		Vector3 pos = barony.getNode().getRelativePos();
		int baseX = (int) (pos.x / WorldChunk.BLOCK_SIZE) + World.getInstance().getMap().width/2;
		int baseY = (int) (pos.y / WorldChunk.BLOCK_SIZE) + World.getInstance().getMap().height/2;
		
		int width = chunk.getWidth();
		int height = chunk.getHeight();
		
		boolean[][] unfogged = new boolean[width*2][height*2];
		
		for (int x = baseX - width; x < baseX + width; ++x) {
			for (int y = baseY - height; y < baseY + height; ++y) {
				if (x >= 0 && x < map.width && y >= 0 && y < map.height) {
					Tile t2 = map.getTile(x, y);
					if (t2 != null) {
						if (t2.chunk == chunk) {
							if (barony.isExplored()) { 
								setFog(y, x, EXPLORED_COLOR);
								unfogged[x-baseX+width][y-baseY+height] = true;
							} else {
								setFog(y, x, FOGGED_COLOR);
							}
						}
					} else if (!unfogged[x-baseX+width][y-baseY+height] && (x-baseX-width)*(x-baseX-width)+(y-baseY-height)*(y-baseY-height) <= (width+height)*(width+height)/2) {
						if (barony.isExplored()) { 
							setFog(y, x, EXPLORED_COLOR);
							unfogged[x-baseX+width][y-baseY+height] = true;
						} else {
							setFog(y, x, FOGGED_COLOR);
						}
					}
				}
			}
		
		}
	}
	
	
	public Texture build() {
		return TextureManager.getInstance().createTexture(textureName, bitmap.toBitmap());
	}

	public void updateFogTexture() {
		Texture t = TextureManager.getInstance().getTexture(textureName);
		if (t != null) {
			TextureManager.getInstance().destroyTexture(textureName);
			build();
		}
	}
}
