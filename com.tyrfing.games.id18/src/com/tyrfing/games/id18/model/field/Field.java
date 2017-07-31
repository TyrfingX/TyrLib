package com.tyrfing.games.id18.model.field;

import java.util.ArrayList;
import java.util.List;

import com.tyrfing.games.tyrlib3.model.math.Vector2I;
import com.tyrfing.games.tyrlib3.model.resource.ISaveable;
import com.tyrfing.games.tyrlib3.model.struct.Grid;

public class Field implements ISaveable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1235934507489391688L;
	private Grid<Tile> tileGrid;
	private List<IFieldObject> objects;
	
	public Field(Vector2I size) {
		tileGrid = new Grid<Tile>(size);
		objects = new ArrayList<IFieldObject>();
		
		for (int x = 0; x < size.x; ++x) {
			for (int y = 0; y < size.y; ++y) {
				tileGrid.setItem(x, y, new Tile());
			}
		}
	}
	
	public Grid<Tile> getTileGrid() {
		return tileGrid;
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
		
		int startHeight = tileGrid.getItem(position).getHeight();
		
		while (foundReceiver == null) {
			if (!tileGrid.inBounds(position)) {
				break;
			}
			
			int currentHeight = tileGrid.getItem(position).getHeight();
			
			if (startHeight != currentHeight) {
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
}
