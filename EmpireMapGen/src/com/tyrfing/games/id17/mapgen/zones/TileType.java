package com.tyrfing.games.id17.mapgen.zones;

public enum TileType {
	SOIL, GRASS, WATER, ROCK, SNOW, BEACH;
	
	public int getInt() {
		return (this.ordinal()+1) * 16;
	}
}
