package com.tyrfing.games.id17.mapgen.zones;

import java.awt.Color;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.colors.ColorUtil;
import com.tyrfing.games.id17.mapgen.Map;
import com.tyrfing.games.id17.mapgen.gui.GUI;

public class Area {
	public final Path2D.Double polygon;
	public AreaType type = AreaType.OCEAN;
	public final Point2D.Double generator;
	public final List<Area> neighbours = new ArrayList<Area>(); 
	public int elevation = 1;
	public double moisture;
	public Biome biome;
	public final Map map;
	public final int index;
	public Barony barony;
	public boolean isBeachArea;
	public TileType baseTileType;
	public boolean hasMapObject;
	public double supplies;
	public final List<Corner> corners = new ArrayList<Corner>();
	public int roadDegree;
	
	public Area(Map map, int index, Path2D.Double polygon, Point2D.Double generator) {
		this.polygon = polygon;
		this.generator = generator;
		this.map = map;
		this.index = index;
	}
	
	public void addCorner(Area other) {
		if (!neighbours.contains(other)) {
			neighbours.add(other);
		}
	}
	
	public Point2D.Double getCenter() {
		double x = 0,y = 0;
		for (int i = 0; i < corners.size(); ++i) {
			x += corners.get(i).point.getX();
			y += corners.get(i).point.getY();
		}
		x /= corners.size();
		y /= corners.size();
		
		return new Point2D.Double(x*GUI.WINDOW_SIZE, y*GUI.WINDOW_SIZE);
	}

	public Color getElevationColor() {
		if (type != AreaType.LAND) {
			return type.getColor();
		} else {
			int offset = 0;
			if (biome != Biome.BEACH) offset = 1;
			return ColorUtil.lerp(Color.decode("#44891a"), Color.WHITE, (map.generatedMaxElevation+1-elevation+offset)*1/(map.generatedMaxElevation+offset));
		}
	}
	
	public Color getMoistureColor() {
		if (type != AreaType.LAND) {
			return type.getColor();
		} else {
			return ColorUtil.lerp(Color.decode("#2f484e"), Color.decode("#f7e26b"), moisture);
		}
	}

	public Color getSupplyColor() {
		if (type != AreaType.LAND) {
			return type.getColor();
		} else {
			return ColorUtil.lerp(Color.decode("#f7e26b"), Color.decode("#2f484e"), supplies);
		}
	}
	
}
