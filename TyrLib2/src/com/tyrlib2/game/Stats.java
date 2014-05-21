package com.tyrlib2.game;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages a collection of stats
 * @author Sascha
 *
 */

public class Stats<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2556194353551947323L;
	
	/** Collection of the stats **/
	private Map<T, Float> stats;
	
	public Stats()
	{
		stats = new HashMap<T, Float>();
	}
	
	/**
	 * Set a stat to a value
	 * @param name	Name of the stat
	 * @param value	New Value
	 */
	public void setStat(T name, Float value)
	{
		stats.put(name, value);
	}
	
	
	public void changeStat(T name, Float value) {
		if (!stats.containsKey(name)) {
			stats.put(name, 0f);
		}
		stats.put(name, stats.get(name) + value);
	}
	
	/**
	 * Get a value of a stat
	 * @param name	Name of the stat
	 * @return		The value of the stat
	 */
	
	public Float getStat(Integer name)
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
	
	public boolean hasStat(Integer name) {
		return stats.containsKey(name);
	}
	
	/**
	 * Inputs all stats into this stat collection
	 * @param stats The collection of stats to be added to this
	 */
	
	public void setStats(Stats stats)
	{
		this.stats.putAll(stats.stats);
	}
	
	/**
	 * Get a human readable string representation of this Stats object
	 * @param delimiterKeyValue	Delimiter for key and value
	 * @param delimiterKeys		Delimiter for the individual stat entries
	 * @return					A human readable string representation using the delimiters
	 */
	
	public String toString(char delimiterKeyValue, char delimiterKeys)
	{
		String res = "";
		for (T key : stats.keySet())
		{
			float value = stats.get(key);
			res += key.toString() + delimiterKeyValue + value + delimiterKeys;
		}
		return res;
	}
	
	/**
	 * Copy this stats collection
	 * @return	A copy of this collection
	 */
	
	public Stats copy()
	{
		Stats copyStats = new Stats();
		copyStats.stats.putAll(stats);
		return copyStats;
	}
	
	public void changeStats(Stats<T> stats) {
		for (T key : stats.stats.keySet())
		{
			float value = stats.stats.get(key);
			changeStat(key, value);
		}
	}
}
