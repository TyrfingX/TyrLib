package com.tyrlib2.game;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages a collection of stats
 * @author Sascha
 *
 */

public class Stats {
	private Map<String, Float> stats;
	
	public Stats()
	{
		stats = new HashMap<String, Float>();
	}
	
	public void setStat(String name, Float value)
	{
		stats.put(name, value);
	}
	
	public Float getStat(String name)
	{
		if (stats.containsKey(name))
		{
			return stats.get(name);
		}
		else
		{
			return 0f;
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
			float value = stats.get(key);
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
