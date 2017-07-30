package com.tyrfing.games.id17.mapgen.objects;

import com.tyrfing.games.id17.mapgen.House;
import com.tyrfing.games.id17.mapgen.zones.Area;

public class MapObject implements Comparable<MapObject> {

	public String name;
	public House owner;
	public final MapObjectType type;
	public final Area area;
	
	public int x;
	public int y;

	public MapObject(MapObjectType type, Area area) {
		this.type = type;
		this.area = area;
	}

	@Override
	public int compareTo(MapObject other) {
		if (this.y < other.y) {
			return -1;
		} else if (this.y > other.y) {
			return 1;
		}
		
		if (this.x < other.x) {
			return -1;
		} else if (this.x > other.x) {
			return 1;
		}
		
		return 0;
	}
}
