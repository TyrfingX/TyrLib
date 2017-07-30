package com.tyrfing.games.id17.mapgen.objects;


public enum MapObjectType {
	CASTLE, TREE1, VILLAGE, SHROOMS1, MINE, WINDMILL, TREE2, FARM, HORSE, TREE3, CATTLE, QUARRY;

	public int getInt() {
		return (this.ordinal()+1)*16;
	}
	
	public String getTypeName() {
		if (this == CASTLE) {
			return "Castle";
		} else if (this == TREE1) {
			return "Forest";
		} else if (this == VILLAGE) {
			return "Village";
		} else if (this == SHROOMS1) {
			return "Mushrooms";
		} else if (this == MINE) {
			return "Mine";
		} else if (this == WINDMILL) {
			return "Windmill";
		} else if (this == TREE2) {
			return "Trees";
		} else if (this == FARM) {
			return "Farm";
		} else if (this == HORSE) {
			return "Ranch";
		} else if (this == TREE3) {
			return "Great Forest";
		} else if (this == CATTLE) {
			return "Pasture";
		} else if (this == QUARRY) {
			return "Quarry";
		}
		
		
		return null;
	}
	
}
