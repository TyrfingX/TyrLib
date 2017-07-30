package com.tyrfing.games.id17.mapgen.zones;

import java.awt.Color;

public enum Biome {
	GRASSLAND, SNOW, FOREST, ROCK, SWAMP, DRYLAND, WATER, BEACH;
	
	public static final Biome[] BIOME_TABLE = {
		DRYLAND, DRYLAND, GRASSLAND, GRASSLAND, GRASSLAND, FOREST, SWAMP,
		DRYLAND, GRASSLAND, GRASSLAND, GRASSLAND, FOREST, FOREST, SWAMP, 
		ROCK, ROCK, GRASSLAND, GRASSLAND, FOREST, SNOW, SNOW,
		ROCK, ROCK, ROCK, ROCK, ROCK, SNOW, SNOW
	};
	
	public Color getMapColor() {
		if (this == GRASSLAND) {
			return Color.decode("#a3ce27");
		} else if (this == FOREST) {
			return Color.decode("#44891a");
		} else if (this == SNOW) {
			return Color.decode("#ffffff");
		} else if (this == ROCK) {
			return Color.decode("#9d9d9d");
		} else if (this == SWAMP) {
			return Color.decode("#2f484e");
		} else if (this == DRYLAND) {
			return Color.decode("#eb8931");
		} else if (this == WATER) {
			return AreaType.OCEAN.getColor();
		} else if (this == BEACH) {
			return Color.decode("#f7e26b");
		}
		
		return Color.BLACK;
	}
	
	public TileType getBaseTileType() {
		if (this == GRASSLAND) {
			return TileType.GRASS;
		} else if (this == FOREST) {
			return TileType.GRASS;
		} else if (this == SNOW) {
			return TileType.SNOW;
		} else if (this == ROCK) {
			return TileType.ROCK;
		} else if (this == SWAMP) {
			return TileType.GRASS;
		} else if (this == DRYLAND) {
			return TileType.SOIL;
		} else if (this == WATER) {
			return TileType.WATER;
		} else if (this == BEACH) {
			return TileType.BEACH;
		}
		
		return TileType.GRASS;
	}
}
