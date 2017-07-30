package com.tyrfing.games.id17.holdings;

import com.tyrfing.games.id17.trade.GoodProduction;
import com.tyrfing.games.id17.trade.Iron;
import com.tyrfing.games.id17.trade.Wood;

public class Mine extends Holding {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7764782296399764378L;
	public static final float[] stats = new float[HoldingTypes.COUNT_STATS];
	
	static {
		stats[HoldingTypes.SCHOLAR_ATTRACTIVITY] = -100;
		stats[HoldingTypes.GROWTH] = 0.0000004f;
		stats[HoldingTypes.INCOME] = 24f;
		stats[HoldingTypes.WORKERS_ATTRACTIVITY] = 150;
	}
	
	
	public Mine(HoldingData holdingData, float[] stats) {
		super(holdingData, stats);
		
		GoodProduction ironProduction = new GoodProduction();
		ironProduction.addOutputGood(new Iron(5, this));
		productions.add(ironProduction);
		
		this.addDemand(Wood.ID, 2);
	}

}
