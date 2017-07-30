package com.tyrfing.games.id17.mapgen.objects;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.mapgen.gui.GUI;
import com.tyrfing.games.id17.mapgen.zones.Corner;

public class River {
	public List<Corner> corners = new ArrayList<Corner>();
	public Path2D.Double path = new Path2D.Double();
	
	public River(Corner origin) {
		corners.add(origin);
		path.moveTo(origin.point.getX()*GUI.WINDOW_SIZE, origin.point.getY()*GUI.WINDOW_SIZE);
	}
	
	public void addCorner(Corner corner) {
		corners.add(corner);
		path.lineTo(corner.point.getX()*GUI.WINDOW_SIZE, corner.point.getY()*GUI.WINDOW_SIZE);
		corner.partOfRiver = true;
	}
	
	public void close() {
		for (int i = 0; i < corners.size(); ++i) {
			Point2D.Double point = corners.get(corners.size()-i-1).point;
			path.lineTo(point.getX()*GUI.WINDOW_SIZE, point.getY()*GUI.WINDOW_SIZE);
		}
	}
}
