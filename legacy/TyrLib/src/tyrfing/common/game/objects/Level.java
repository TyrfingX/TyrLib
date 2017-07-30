package tyrfing.common.game.objects;

public class Level {
	
	private Stats stats;
	
	public Level(Stats stats)
	{
		this.stats = stats;
	}
	
	public boolean checkLevelUp()
	{
		if (stats.getStat("Exp") > stats.getStat("NextExp"))
		{
			stats.setStat("Level", stats.getStat("Level") + 1);
			stats.setStat("Exp", stats.getStat("Exp") - stats.getStat("NextExp"));
			stats.setStat("NextExp", stats.getStat("NextExp") + stats.getStat("ExpPerLvl"));
			stats.setStat("ExpPerLvl", stats.getStat("ExpPerLvl") + stats.getStat("Level"));
			return true;
		}
		return false;
	}
}
