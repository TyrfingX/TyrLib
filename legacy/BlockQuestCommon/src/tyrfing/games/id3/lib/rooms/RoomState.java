package tyrfing.games.id3.lib.rooms;

public enum RoomState {
	NORMAL,
	MADERED,
	RED,
	VIOLET,
	BLUE,
	MADEBLUE,
	CURSED;
	
	public boolean strenghtenMonsters()
	{
		return (this == MADERED || this == VIOLET || this == RED);
	}
	
	public static RoomState reducedRedness(RoomState state)
	{
		if (state == RED)
		{
			return MADERED;
		} else if (state == MADERED) {
			return NORMAL;
		} else if (state == VIOLET) {
			return RED;
		}
		else
		{
			return NORMAL;
		}
	}
	
	public boolean overwrite(RoomState state)
	{
		if (this == NORMAL || this == MADEBLUE || this == CURSED)
		{
			return true;
		}
		else
		{
			if (this == BLUE)
			{
				return state.strenghtenMonsters();
			}
			else if (this == MADERED)
			{
				return (state == RED || state == VIOLET);
			}
			else
			{
				if (this == RED)
				{
					return state == VIOLET;
				}
				else
				{
					return false;
				}
			}
		}
	}
	
	public static RoomState reducedBlueness(RoomState state)
	{
		if (state == BLUE)
		{
			return MADEBLUE;
		}
		else if (state == MADEBLUE)
		{
			return NORMAL;
		}
		else
		{
			return NORMAL;
		}
	}
	
}
