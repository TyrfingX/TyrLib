package tyrfing.games.id3.lib.mechanics;

public class Floor {
	
	private int level;
	private boolean cleared;
	
	public Floor(int level)
	{
		this.level = level;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public void setCleared(boolean cleared)
	{
		this.cleared = cleared;
	}
	
	public boolean cleared()
	{
		return cleared;
	}
}
