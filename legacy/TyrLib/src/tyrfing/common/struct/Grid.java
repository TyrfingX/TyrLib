package tyrfing.common.struct;

public class Grid<T> {
	private T grid[][];
	private int width;
	private int height;
	
	@SuppressWarnings("unchecked")
	public Grid(int width, int height)
	{
		this.width = width;
		this.height = height;
		
		this.grid = (T[][]) new Object[width][height];
		
	}
	
	public int getWidth()
	{
		return width;
	}
	
	public int getHeight()
	{
		return height;
	}
	
	public T getItem(int x, int y)
	{
		if (checkBounds(x,y))
		{
			return grid[x][y];
		}
		
		return null;
	}
	
	public T getItem(Coord2 coord)
	{
		return getItem(coord.x, coord.y);
	}
	
	public void setItem(int x, int y, T item)
	{
		grid[x][y] = item;
	}
	
	public void setItem(Coord2 coord, T item)
	{
		setItem(coord.x, coord.y, item);
	}
	
	public boolean checkBounds(int x, int y)
	{
		if (x >= 0 && y >= 0)
		{	
			if (x < width && y < height)
			{
				return true;
			}
		}	
		
		return false;
	}
	
	public void writeNull()
	{
		for (int x = 0; x < width; x++)
		{
			for (int y = 0; y < width; y++)
			{
				grid[x][y] = null;
			}
		}
	}
	
}
