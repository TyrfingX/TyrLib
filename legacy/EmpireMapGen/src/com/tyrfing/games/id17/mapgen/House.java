package com.tyrfing.games.id17.mapgen;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.id17.mapgen.objects.MapObject;

public class House {
	public String name;
	public int sigilID;
	public List<House> subHouses = new ArrayList<House>();
	public int freeHoldings;
	public List<MapObject> holdings = new ArrayList<MapObject>();
	public Color color;
	public House parent;
	
	public House() {
		color = new Color((float)Math.random(), (float)Math.random(), (float)Math.random());
	}
}
