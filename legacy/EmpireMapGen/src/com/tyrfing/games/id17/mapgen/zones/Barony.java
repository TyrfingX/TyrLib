package com.tyrfing.games.id17.mapgen.zones;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.colors.Color4;
import com.tyrfing.games.id17.mapgen.House;
import com.tyrfing.games.id17.mapgen.objects.MapObject;
import com.tyrfing.games.id17.mapgen.objects.MapObjectType;
import com.tyrfing.games.id17.mapgen.objects.River;

public class Barony {
	

	public static final Color[] BARONY_COLORS = new Color[] {
		Color.decode("#be2633"),
		Color.decode("#e06f8b"),
		Color.decode("#a46422"),
		Color.decode("#eb8931"),
		Color.decode("#f7e26b"),
		Color.decode("#44891a"),
		Color.decode("#a3ce27"),
		Color.decode("#ffffff"),
		Color.decode("#9d9d9d")
	};
	
	public final List<Area> areas = new ArrayList<Area>();
	public final List<Barony> neighbours = new ArrayList<Barony>();
	public final List<MapObject> mapObjects = new ArrayList<MapObject>();
	public Color color;
	public Color printColor;
	
	public int x;
	public int y;
	public int width;
	public int height;
	
	public Color4[][] tiles;
	
	public String name;
	
	public List<Integer> getAreaIndices() {
		List<Integer> areaIndices = new ArrayList<Integer>();
		for (int i = 0; i < areas.size(); ++i) {
			areaIndices.add(areas.get(i).index);
		}
		return areaIndices;
	}
	
	public Color getGreedyColor() {
		
		Color color = BARONY_COLORS[0];
		
		search: for (int j = 0; j < BARONY_COLORS.length; ++j) {
			color = BARONY_COLORS[j];
		
			for (int i = 0; i < neighbours.size(); ++i) {
				if (neighbours.get(i).color != null && color == neighbours.get(i).color) {
					continue search;
				}
			}
			
			break;
		}
		
		return color;
	}
	
	public void calcBounds() {
		Rectangle2D bounds = null;
		for (int i = 0; i < areas.size(); ++i) {
			Area a = areas.get(i);
			a.baseTileType = a.biome.getBaseTileType();
			Rectangle2D rect = a.polygon.getBounds2D();
			if (bounds == null) {
				bounds = rect;
			} else {
				Rectangle2D.union(bounds, rect, bounds);
			}
		}
		
		x = Math.max(0, (int) bounds.getX()-2);
		y = Math.max(0, (int) bounds.getY()-2);
		width = (int) (bounds.getWidth()+3);
		height = (int) (bounds.getHeight()+3);
		
		double maxHeight = 0;
		
		tiles = new Color4[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				tiles[x][y] = new Color4(0,0,0,0);
			}
		}
		
		Rectangle2D.Double r = new Rectangle2D.Double(0, 0, 1.6, 1.6);
		
		for (int x = 0; x < width; ++x) {
			r.x = x+this.x-0.8;
			for (int y = 0; y < height; ++y) {
				r.y = y+this.y-0.8;
				for (int i = 0; i < areas.size(); ++i) {
					Area a = areas.get(i);
					
					if (a.polygon.intersects(r)) {
						tiles[x][y].r = a.baseTileType.getInt();
						tiles[x][y].g = a.elevation;
						maxHeight = Math.max(maxHeight, a.elevation);
						tiles[x][y].a = 255;
						break;
					}
				}
			}
		}
		
		r.width = 2;
		r.height = 2;
		
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				for (int i = 0; i < areas.get(0).map.allRivers.size(); ++i) {
					if (tiles[x][y].a == 255) { 
						River river = areas.get(0).map.allRivers.get(i);
						r.x = x+this.x-1;
						r.y = y+this.y-1;
						
						
						if (river.path.intersects(r)) {
							tiles[x][y].r = TileType.WATER.getInt();
							break;
						}
					}
				}
			}
		}
		

	}
	
	public House getOwner() {
		return mapObjects.get(0).owner;
	}

	public boolean hasObject(MapObjectType type) {
		for (int i = 0; i < mapObjects.size(); ++i) {
			if (mapObjects.get(i).type == type) {
				return true;
			}
		}
		
		return false;
	}
	
	
}
