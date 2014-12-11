package com.tyrlib2.game;

import gnu.trove.map.hash.TIntFloatHashMap;

import java.io.Serializable;

/**
 * This class manages a collection of stats
 * @author Sascha
 *
 */

public class Stats implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2556194353551947323L;
	
	/** Collection of the stats **/
	public TIntFloatHashMap stats;
	
	public Stats()
	{
		stats = new TIntFloatHashMap();
	}
	
	/**
	 * Set a stat to a value
	 * @param name	Name of the stat
	 * @param value	New Value
	 */
	public void setStat(int name, float value)
	{
		stats.put(name, value);
	}
	
	
	public void changeStat(int name, float value) {
		stats.put(name, stats.get(name) + value);
	}
	
	/**
	 * Get a value of a stat
	 * @param name	Name of the stat
	 * @return		The value of the stat
	 */
	
	public float getStat(int name)
	{
		return stats.get(name);
	}
	
	public boolean hasStat(int name) {
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
		StringBuilder b = new StringBuilder();
		for (int key : stats.keys())
		{
			float value = stats.get(key);
			b.append(key);
			b.append(delimiterKeyValue);
			b.append(value);
			b.append(delimiterKeys);
		}
		return b.toString();
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
	
	public void changeStats(Stats stats) {
		for (int key : stats.stats.keys())
		{
			float value = stats.stats.get(key);
			changeStat(key, value);
		}
	}
	
}
