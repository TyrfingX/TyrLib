package com.tyrfing.games.tyrlib3.model.struct;

import java.io.Serializable;

import com.tyrfing.games.tyrlib3.model.math.Vector2I;

public class Grid<T extends Serializable> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4831714069358773062L;
	protected T rawGrid[][];
	protected Vector2I size;
	
	@SuppressWarnings("unchecked")
	public Grid(Vector2I size)
	{
		this.size = size;
		this.rawGrid = (T[][]) new Serializable[size.x][size.y];
	}
	
	public int getWidth() {
		return size.x;
	}
	
	public int getHeight() {
		return size.y;
	}
	
	public Vector2I getSize() {
		return size;
	}
	
	public T getItem(int x, int y) {
		if (checkBounds(x,y)) {
			return rawGrid[x][y];
		}
		
		return null;
	}
	
	public T getItem(Vector2I gridPosition) {
		return getItem(gridPosition.x, gridPosition.y);
	}
	
	public void setItem(int x, int y, T item) {
		rawGrid[x][y] = item;
	}
	
	public void setItem(Vector2I gridPosition, T item) {
		setItem(gridPosition.x, gridPosition.y, item);
	}
	
	public T[][] getRawGrid() {
		return rawGrid;
	}
	
	public boolean checkBounds(int x, int y) {
		if (x >= 0 && y >= 0)
		{	
			if (x < size.x && y < size.y)
			{
				return true;
			}
		}	
		
		return false;
	}
	
	public boolean inBounds(Vector2I gridPosition) {		
		return checkBounds(gridPosition.x, gridPosition.y);
	}
	
	public void writeAll(T object) {
		for (int x = 0; x < size.x; x++)
		{
			for (int y = 0; y < size.y; y++)
			{
				rawGrid[x][y] = object;
			}
		}
	}
	
}
