package com.tyrfing.games.id17.mapgen.zones;

import java.awt.Color;

public enum AreaType {
	OCEAN, LAND, LAKE;
	
	public Color getColor()  {
		if (this == OCEAN) {
			return Color.decode("#005784");
		} else if (this == LAND) {
			return Color.decode("#f7e26b");
		} else if (this == LAKE) {
			return Color.decode("#005784"); //#31a2f2
		}
		
		return Color.BLACK;
	}
}
