package com.tyrfing.games.id17.holdings;


public enum PopulationType {
	
	Traders, Scholars, Workers, Peasants;
	
	public static final int BASE_TAX = 1;
	public static final int ARMY_PROB = 2;
	public static final int PRODUCTIVITY = 3;
	public static final int RESEARCH = 4;
	public static final int TRADE = 5;
	public static final PopulationType[] VALUES = PopulationType.values();
	
	public static final int COUNT_STATS = 6;
	
	public static final float[][] POP_STATS = new float[VALUES.length][];
	
	static {
		float[] stats;
		
		stats = new float[COUNT_STATS];
		stats[BASE_TAX] = 0.7f;
		stats[ARMY_PROB] = 0.000007f;
		stats[PRODUCTIVITY] = 0.01f;
		stats[TRADE] = 1f;
		POP_STATS[PopulationType.Traders.ordinal()] = stats;
		
		stats = new float[COUNT_STATS];
		stats[BASE_TAX] = 0.035f;
		stats[ARMY_PROB] = 0.0000025f;
		stats[RESEARCH] = 0.002f;
		stats[PRODUCTIVITY] = 0.005f;
		POP_STATS[PopulationType.Scholars.ordinal()] = stats;
		
		stats = new float[COUNT_STATS];
		stats[BASE_TAX] = 0.015f;
		stats[ARMY_PROB] = 0.000025f;
		stats[PRODUCTIVITY] = 0.2f;
		POP_STATS[PopulationType.Workers.ordinal()] = stats;

		stats = new float[COUNT_STATS];
		stats[BASE_TAX] = 0.006f;
		stats[ARMY_PROB] = 0.0001f;
		stats[PRODUCTIVITY] = 0.05f;
		POP_STATS[PopulationType.Peasants.ordinal()] = stats;
	}
}
