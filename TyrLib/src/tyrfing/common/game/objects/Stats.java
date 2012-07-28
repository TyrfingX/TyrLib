package tyrfing.common.game.objects;

import java.util.HashMap;
import java.util.Map;

public class Stats {
	private Map<String, Integer> stats;
	
	public Stats()
	{
		stats = new HashMap<String, Integer>();
	}
	
	public void setStat(String name, Integer value)
	{
		stats.put(name, value);
	}
	
	public Integer getStat(String name)
	{
		if (stats.containsKey(name))
		{
			return stats.get(name);
		}
		else
		{
			return 0;
		}
	}
	
	
	public void setStats(Stats stats)
	{
		this.stats.putAll(stats.stats);
	}
	
	public String toString(char limiterKeyValue, char limiterKeys)
	{
		String res = "";
		for (String key : stats.keySet())
		{
			int value = stats.get(key);
			res += key + limiterKeyValue + value + limiterKeys;
		}
		return res;
	}
	
	public Stats copy()
	{
		Stats copyStats = new Stats();
		copyStats.stats.putAll(stats);
		return copyStats;
	}
	
}
