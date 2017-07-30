package com.tyrfing.games.id17.world;

import java.io.Serializable;

import com.tyrlib2.files.IBitmap;
import com.tyrlib2.main.Media;

public class MapFile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 795184242635872989L;
	
	public transient IBitmap baronyMap;
	public transient IBitmap tileMap;
	public String baronyData;
	public String houseData;
	public String mapName;
	
	public MapFile() {
		baronyData = "baronies/Baronies.xml";
		houseData = "houses/Houses.xml";
		mapName = "small";
		load();
	}
	
	public MapFile(String mapName) {
		baronyData = "maps/" + mapName + "/Baronies.xml";
		houseData = "maps/" + mapName + "/Houses.xml";
		this.mapName = mapName;
		load();
	}

	public void load() {
		baronyMap = Media.CONTEXT.loadStaticBitmap(Media.CONTEXT.getResourceID("baronymap" + mapName, "drawable"), false);
		tileMap = Media.CONTEXT.loadStaticBitmap(Media.CONTEXT.getResourceID("tilemap" + mapName, "drawable"), false);
	}
}
