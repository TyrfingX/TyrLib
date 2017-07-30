package com.tyrfing.games.id17.holdings;

import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.trade.Stone;

public class Quarry extends Holding {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2673495916177935565L;
	public static final float[] stats = new float[HoldingTypes.COUNT_STATS];
	
	static {
		stats[HoldingTypes.SCHOLAR_ATTRACTIVITY] = -100;
		stats[HoldingTypes.GROWTH] = 0.004f;
		stats[HoldingTypes.INCOME] = 0.1f;
		stats[HoldingTypes.WORKERS_ATTRACTIVITY] = 30;
	}
	
	public Quarry(HoldingData holdingData, float[] stats) {
		super(holdingData, stats);
	
		GoodProduction stoneProduction = new GoodProduction();
		stoneProduction.addOutputGood(new Stone(5, this));
		productions.add(stoneProduction);
	}

}
