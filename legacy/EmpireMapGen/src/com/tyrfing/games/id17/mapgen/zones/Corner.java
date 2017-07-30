package com.tyrfing.games.id17.mapgen.zones;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class Corner {
	public final List<Area> connects = new ArrayList<Area>();
	public final Point2D.Double point;
	public final List<Corner> neighbours = new ArrayList<Corner>();
	public double moisture;
	public boolean partOfRiver;
	
	public Corner(Point2D.Double point) {
		this.point = point;
	}
	
	public double getHeight() {
		double height = connects.get(0).elevation;
		for (int i = 1; i < connects.size(); ++i) {
			height = Math.min(height, connects.get(i).elevation);
		}
		return height;
	}
	
	public boolean isCoastCorner() {
		for (int i = 0; i < connects.size(); ++i) {
			if (connects.get(i).type != AreaType.LAND) {
				return true;
			}
		}
		
		return false;
	}
}
