package com.tyrfing.games.id17.world;


public class Tile {
	public int holdingID = RoadMap.NO_HOLDING;
	public int type;
	public int height;
	public int meshPos;
	public int x;
	public int y;
	public WorldChunk chunk;
	
	public boolean marked = false;
	public BorderBlock[] borderBlocks = new BorderBlock[4];
	
	public Tile(int type, int height) {
		this.type = type;
		this.height = height;
	}
	
	public float getScaledHeight() {
		return height * WorldChunk.BLOCK_SIZE * WorldChunk.HEIGHT_FACTOR;
	}

	public boolean isRoad() {
		return type == 1 || holdingID != RoadMap.NO_HOLDING;
	}
}
