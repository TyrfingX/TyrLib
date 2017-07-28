package com.tyrfing.games.id18.model.field;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.math.Vector2I;

public class Field {
	private Tile tiles[][];
	private List<IFieldObject> objects;
	private Vector2I size;
	
	public Field(Vector2I size) {
		this.size = size;
		tiles = new Tile[size.x][size.y];
		objects = new ArrayList<IFieldObject>();
	}
	
	public Vector2I getSize() {
		return size;
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public List<IFieldObject> getObjects() {
		return objects;
	}
	
	public IFieldObject getObjectAt(Vector2I position) {
		for (IFieldObject object : objects) {
			if (object.getFieldPosition().equals(position)) {
				return object;
			}
		}
		
		return null;
	}
	
	public IFieldObject getFirstObjectInLine(Vector2I position, Vector2I direction) {
		IFieldObject foundReceiver = null;
		
		while (foundReceiver == null) {
			if (!inBounds(position)) {
				break;
			}
			
			IFieldObject receiver = getObjectAt(position);
			if (receiver != null) {
				foundReceiver = receiver;
			} else {
				Vector2I.add(position, direction, position);
			}
		}
		
		return foundReceiver;
	}

	public boolean inBounds(Vector2I position) {
		return position.x >= 0 && position.y >= 0 && position.x < size.x && position.y < size.y;
	}
	
}
