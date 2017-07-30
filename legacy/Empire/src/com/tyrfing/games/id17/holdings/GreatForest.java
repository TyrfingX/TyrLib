package com.tyrfing.games.id17.holdings;

import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.trade.Wood;

public class GreatForest extends Holding {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8742138158533186290L;
	public static final float[] stats = new float[HoldingTypes.COUNT_STATS];
	
	static {
		stats[HoldingTypes.SCHOLAR_ATTRACTIVITY] = -100;
		stats[HoldingTypes.GROWTH] = 0.004f;
		stats[HoldingTypes.INCOME] = 0.3f;
		stats[HoldingTypes.WORKERS_ATTRACTIVITY] = 200;
	}
	
	public GreatForest(HoldingData holdingData, float[] stats) {
		super(holdingData, stats);
	
		GoodProduction woodProduction = new GoodProduction();
		woodProduction.addOutputGood(new Wood(5, this));
		productions.add(woodProduction);
	}

}
