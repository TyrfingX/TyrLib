package com.tyrfing.games.id17.holdings;

import com.tyrfing.games.id17.trade.Wood;


public class Village extends Holding {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8772019933897489251L;
	public static final float[] stats = new float[HoldingTypes.COUNT_STATS];
	
	static {
		stats[HoldingTypes.GROWTH] = 0.2f;
		stats[HoldingTypes.INCOME] = 6.f;
		stats[HoldingTypes.WORKERS_ATTRACTIVITY] = 50;
		stats[HoldingTypes.PEASANTS_ATTRACTIVITY] = 250;
	}
	
	public Village(HoldingData holdingData, float[] stats) {
		super(holdingData, stats);
	}

}
